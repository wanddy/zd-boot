package smartcode.form.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.api.vo.Result;
import commons.constant.CommonConstant;
import commons.constant.enums.CgformEnum;
import commons.util.*;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import smartcode.config.entity.DataBaseConfig;
import smartcode.config.entity.TableTypeEntity;
import smartcode.config.exception.BusinessException;
import smartcode.config.exception.DBException;
import smartcode.config.service.DbTableHandleI;
import smartcode.config.utils.TableTemplateUtils;
import smartcode.form.enhance.CgformEnhanceJavaInter;
import smartcode.form.enhance.CgformEnhanceJavaListInter;
import smartcode.form.entity.*;
import smartcode.form.gen.CodeGenerateOne;
import smartcode.form.gen.CodeGenerateOneToMany;
import smartcode.form.mapper.*;
import smartcode.form.model.AModel;
import smartcode.form.model.OnlGenerateModel;
import smartcode.form.service.OnlCgformFieldService;
import smartcode.form.service.OnlCgformHeadService;
import smartcode.form.service.OnlCgformIndexService;
import smartcode.form.utils.DbReadTableUtil;
import smartcode.form.utils.DbSelectUtils;
import smartcode.form.utils.dUtils;
import smartcode.form.vo.ColumnVo;
import smartcode.form.vo.MainTableVo;
import smartcode.form.vo.SubTableVo;
import smartcode.form.vo.TableVo;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 15:39
 * @Description: zdit.zdboot.auth.online.service.impl
 **/
@Service
public class OnlCgformHeadServiceImpl extends ServiceImpl<OnlCgformHeadMapper, OnlCgformHead> implements OnlCgformHeadService {

    private static final Logger log = LoggerFactory.getLogger(OnlCgformHeadServiceImpl.class);

    @Autowired
    private OnlCgformFieldService fieldService;
    @Autowired
    private OnlCgformIndexService indexService;
    @Autowired
    private DataBaseConfig dataBaseConfig;
    @Autowired
    private OnlCgformEnhanceJsMapper onlCgformEnhanceJsMapper;
    @Autowired
    private OnlCgformButtonMapper onlCgformButtonMapper;
    @Autowired
    private OnlCgformEnhanceJavaMapper onlCgformEnhanceJavaMapper;
    @Autowired
    private OnlCgformEnhanceSqlMapper onlCgformEnhanceSqlMapper;

    /**
     * online代码生成表单页
     *
     * @param headList
     */
    @Override
    public void initCopyState(List<OnlCgformHead> headList) {
        List<String> list = baseMapper.queryCopyPhysicId();
        for (OnlCgformHead thatcher : headList) {
            if (list.contains(thatcher.getId())) {
                thatcher.setHascopy(Integer.valueOf(1));
                continue;
            }
            thatcher.setHascopy(Integer.valueOf(0));
        }
    }

    /**
     * 单表代码生成
     *
     * @param model
     * @return
     */
    @Override
    public List<String> generateCode(OnlGenerateModel model) {
        TableVo tableVo = new TableVo();
        tableVo.setEntityName(model.getEntityName());
        tableVo.setEntityPackage(model.getEntityPackage());
        tableVo.setFtlDescription(model.getFtlDescription());
        tableVo.setTableName(model.getTableName());
        tableVo.setSearchFieldNum(-1);
        List<ColumnVo> list1 = new ArrayList();
        List<ColumnVo> list2 = new ArrayList();
        getOnlCgformField(model.getCode(), list1, list2);
        OnlCgformHead OnlCgformHead = baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("id", model.getCode()));
        HashMap<String, String> map = new HashMap();
        map.put("scroll", OnlCgformHead.getScroll() == null ? "0" : OnlCgformHead.getScroll().toString());
        String formTemplate = OnlCgformHead.getFormTemplate();
        if (oConvertUtils.isEmpty(formTemplate)) {
            tableVo.setFieldRowNum(1);
        } else {
            tableVo.setFieldRowNum(Integer.parseInt(formTemplate));
        }

        if ("Y".equals(OnlCgformHead.getIsTree())) {
            map.put("pidField", OnlCgformHead.getTreeParentIdField());
            map.put("hasChildren", OnlCgformHead.getTreeIdField());
            map.put("textField", OnlCgformHead.getTreeFieldname());
        }

        tableVo.setExtendParams(map);
        CgformEnum cgformEnum = CgformEnum.getCgformEnumByConfig(model.getJspMode());
        List<String> list = null;
        try {
            list = (new CodeGenerateOne(tableVo, list1, list2)).generateCodeFile(model.getProjectPath(), cgformEnum.getTemplatePath(), cgformEnum.getStylePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 多表代码生成
     *
     * @param model
     * @return
     * @throws Exception
     */
    @Override
    public List<String> generateOneToMany(OnlGenerateModel model) throws Exception {
        MainTableVo mainTableVo = new MainTableVo();
        mainTableVo.setEntityName(model.getEntityName());
        mainTableVo.setEntityPackage(model.getEntityPackage());
        mainTableVo.setFtlDescription(model.getFtlDescription());
        mainTableVo.setTableName(model.getTableName());
        OnlCgformHead OnlCgformHead = baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("id", model.getCode()));
        String formTemplate = OnlCgformHead.getFormTemplate();
        if (oConvertUtils.isEmpty(formTemplate)) {
            mainTableVo.setFieldRowNum(1);
        } else {
            mainTableVo.setFieldRowNum(Integer.parseInt(formTemplate));
        }

        List<ColumnVo> list1 = new ArrayList();
        List<ColumnVo> list2 = new ArrayList();
        this.getOnlCgformField(model.getCode(), list1, list2);
        List<OnlGenerateModel> subList = model.getSubList();
        List<SubTableVo> list3 = new ArrayList();
        Iterator iterator = subList.iterator();

        while (iterator.hasNext()) {
            OnlGenerateModel onlGenerateModel = (OnlGenerateModel) iterator.next();
            OnlCgformHead onlCgformHead = baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name", onlGenerateModel.getTableName()));
            if (onlCgformHead != null) {
                SubTableVo subTableVo = new SubTableVo();
                subTableVo.setEntityName(onlGenerateModel.getEntityName());
                subTableVo.setEntityPackage(model.getEntityPackage());
                subTableVo.setTableName(onlGenerateModel.getTableName());
                subTableVo.setFtlDescription(onlGenerateModel.getFtlDescription());
                Integer relationType = onlCgformHead.getRelationType();
                subTableVo.setForeignRelationType(relationType == 1 ? "1" : "0");
                List<ColumnVo> list4 = new ArrayList();
                List<ColumnVo> list5 = new ArrayList();
                OnlCgformField onlCgformField = this.getOnlCgformField(onlCgformHead.getId(), list4, list5);
                if (onlCgformField != null) {
                    subTableVo.setOriginalForeignKeys(new String[]{onlCgformField.getDbFieldName()});
                    subTableVo.setForeignKeys(new String[]{onlCgformField.getDbFieldName()});
                    subTableVo.setColums(list4);
                    subTableVo.setOriginalColumns(list5);
                    list3.add(subTableVo);
                }
            }
        }

        CgformEnum cgformEnum = CgformEnum.getCgformEnumByConfig(model.getJspMode());
        List<String> list = (new CodeGenerateOneToMany(mainTableVo, list1, list2, list3)).generateCodeFile(model.getProjectPath(), cgformEnum.getTemplatePath(), cgformEnum.getStylePath());
        System.out.println(list);
        return list;
    }

    /**
     * 新增表
     *
     * @param model
     * @return
     */
    @Override
    @Transactional
    public Result addAll(AModel model) {
        String ran = UUID.randomUUID().toString().replace("-", "");
        OnlCgformHead onlCgformHead = model.getHead();
        List<OnlCgformField> onlCgformFieldList = model.getFields();
        List<OnlCgformIndex> onlCgformIndexList = model.getIndexs();
        onlCgformHead.setId(ran);

        for (int i = 0; i < onlCgformFieldList.size(); i++) {
            OnlCgformField onlCgformField = onlCgformFieldList.get(i);
            onlCgformField.setId(null);
            onlCgformField.setCgformHeadId(ran);
            if (onlCgformField.getOrderNum() == null) {
                onlCgformField.setOrderNum(i);
            }
            this.getDbType(onlCgformField);
        }

        Iterator iterator = onlCgformIndexList.iterator();

        while (iterator.hasNext()) {
            OnlCgformIndex onlCgformIndex = (OnlCgformIndex) iterator.next();
            onlCgformIndex.setId(null);
            onlCgformIndex.setCgformHeadId(ran);
        }

        onlCgformHead.setIsDbSynch("N");
        onlCgformHead.setTableVersion(1);
        onlCgformHead.setCopyType(0);
        if (onlCgformHead.getTableType() == 3 && onlCgformHead.getTabOrderNum() == null) {
            onlCgformHead.setTabOrderNum(1);
        }

        super.save(onlCgformHead);
        this.fieldService.saveBatch(onlCgformFieldList);
        this.indexService.saveBatch(onlCgformIndexList);
        this.getOnlineType(onlCgformHead, onlCgformFieldList);
        return Result.ok("添加成功");
    }

    /**
     * 同步数据库
     *
     * @param code
     * @param synMethod
     */
    @Override
    public void doDbSynch(String code, String synMethod) throws DBException, SQLException, IOException, TemplateException {
        OnlCgformHead onlCgformHead = this.getById(code);
        if (onlCgformHead == null) {
            throw new DBException("实体配置不存在");
        } else {
            String tableName = onlCgformHead.getTableName();
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cgform_head_id", code);
            queryWrapper.orderByAsc("order_num");
            List list = this.fieldService.list(queryWrapper);
            TableTypeEntity tableTypeEntity = new TableTypeEntity();
            tableTypeEntity.setTableName(tableName);
            tableTypeEntity.setJformPkType(onlCgformHead.getIdType());
            tableTypeEntity.setJformPkSequence(onlCgformHead.getIdSequence());
            tableTypeEntity.setContent(onlCgformHead.getTableTxt());
            tableTypeEntity.setColumns(list);
            tableTypeEntity.setDbConfig(this.dataBaseConfig);
            if ("normal".equals(synMethod)) {
                long timeMillis = System.currentTimeMillis();
                log.info("==判断表是否存在消耗时间" + (System.currentTimeMillis() - timeMillis) + "毫秒");
                if (dUtils.table(tableName)) {
                    TableTemplateUtils templateUtils = new TableTemplateUtils();
                    List<String> stringList = templateUtils.b(tableTypeEntity);
                    Iterator iterator = stringList.iterator();

                    while (iterator.hasNext()) {
                        String next = (String) iterator.next();
                        if (!oConvertUtils.isEmpty(next) && !oConvertUtils.isEmpty(next.trim())) {
                            this.baseMapper.executeDDL(next);
                        }
                    }

                    List cgformIndexList = this.indexService.list(new QueryWrapper<OnlCgformIndex>().eq("cgform_head_id", code));
                    Iterator iterator1 = cgformIndexList.iterator();

                    label54:
                    while (true) {
                        OnlCgformIndex onlCgformIndex;
                        do {
                            if (!iterator1.hasNext()) {
                                break label54;
                            }

                            onlCgformIndex = (OnlCgformIndex) iterator1.next();
                        } while (!"N".equals(onlCgformIndex.getIsDbSynch()) && !CommonConstant.DEL_FLAG_1.equals(onlCgformIndex.getDelFlag()));

                        String str = templateUtils.b(onlCgformIndex.getIndexName(), tableName);
                        if (this.indexService.isExistIndex(str)) {
                            String string = templateUtils.a(onlCgformIndex.getIndexName(), tableName);

                            try {
                                log.info("删除索引 executeDDL:" + string);
                                baseMapper.executeDDL(string);
                                if (CommonConstant.DEL_FLAG_1.equals(onlCgformIndex.getDelFlag())) {
                                    this.indexService.removeById(onlCgformIndex.getId());
                                }
                            } catch (Exception e) {
                                log.error("删除表【" + tableName + "】索引(" + onlCgformIndex.getIndexName() + ")失败!", e);
                            }
                        } else if (CommonConstant.DEL_FLAG_1.equals(onlCgformIndex.getDelFlag())) {
                            this.indexService.removeById(onlCgformIndex.getId());
                        }
                    }
                } else {
                    TableTemplateUtils.a(tableTypeEntity);
                }
            } else if ("force".equals(synMethod)) {
                DbTableHandleI handle = dUtils.getTableHandle();
                String onlCgformField = handle.dropTableSQL(tableName);
                this.baseMapper.executeDDL(onlCgformField);
                TableTemplateUtils.a(tableTypeEntity);
            }

            this.indexService.createIndex(code, dUtils.getDatabaseType(), tableName);
            onlCgformHead.setIsDbSynch("Y");
            this.updateById(onlCgformHead);
        }
    }

    /**
     * 查询数据库表信息
     *
     * @return
     */
    @Override
    public List<String> queryOnlinetables() {
        return baseMapper.queryOnlinetables();
    }

    /**
     * 同步表信息到表单
     *
     * @param tbname
     */
    @Override
    @Transactional
    public void saveDbTable2Online(String tbname) {
        OnlCgformHead onlCgformHead = new OnlCgformHead();
        onlCgformHead.setTableType(1);
        onlCgformHead.setIsCheckbox("Y");
        onlCgformHead.setIsDbSynch("Y");
        onlCgformHead.setIsTree("N");
        onlCgformHead.setIsPage("Y");
        onlCgformHead.setQueryMode("group");
        onlCgformHead.setTableName(tbname.toLowerCase());
        onlCgformHead.setTableTxt(tbname);
        onlCgformHead.setTableVersion(1);
        onlCgformHead.setFormTemplate("1");
        onlCgformHead.setCopyType(0);
        onlCgformHead.setScroll(1);
        onlCgformHead.setThemeTemplate("normal");
        String generate = UUIDGenerator.generate();
        onlCgformHead.setId(generate);
        ArrayList list = new ArrayList();

        try {
            List<ColumnVo> columnVoList = DbReadTableUtil.b(tbname);

            for (int i = 0; i < columnVoList.size(); i++) {
                ColumnVo columnVo = columnVoList.get(i);
                log.info("  columnt : " + columnVo.toString());
                String fieldDbName = columnVo.getFieldDbName();
                OnlCgformField onlCgformField = new OnlCgformField();
                onlCgformField.setCgformHeadId(generate);
                onlCgformField.setDbFieldNameOld(columnVo.getFieldDbName().toLowerCase());
                onlCgformField.setDbFieldName(columnVo.getFieldDbName().toLowerCase());
                if (oConvertUtils.isNotEmpty(columnVo.getFiledComment())) {
                    onlCgformField.setDbFieldTxt(columnVo.getFiledComment());
                } else {
                    onlCgformField.setDbFieldTxt(columnVo.getFieldName());
                }

                onlCgformField.setDbIsKey(0);
                onlCgformField.setIsShowForm(1);
                onlCgformField.setIsQuery(0);
                onlCgformField.setFieldMustInput("0");
                onlCgformField.setIsShowList(1);
                onlCgformField.setOrderNum(i + 1);
                onlCgformField.setQueryMode("single");
                onlCgformField.setDbLength(oConvertUtils.getInt(columnVo.getPrecision()));
                onlCgformField.setFieldLength(120);
                onlCgformField.setDbPointLength(oConvertUtils.getInt(columnVo.getScale()));
                onlCgformField.setFieldShowType("text");
                onlCgformField.setDbIsNull("Y".equals(columnVo.getNullable()) ? 1 : 0);
                onlCgformField.setIsReadOnly(0);
                if ("id".equalsIgnoreCase(fieldDbName)) {
                    String[] strings = new String[]{"java.lang.Integer", "java.lang.Long"};
                    String fieldType = columnVo.getFieldType();
                    if (Arrays.asList(strings).contains(fieldType)) {
                        onlCgformHead.setIdType("NATIVE");
                    } else {
                        onlCgformHead.setIdType("UUID");
                    }

                    onlCgformField.setDbIsKey(1);
                    onlCgformField.setIsShowForm(0);
                    onlCgformField.setIsShowList(0);
                    onlCgformField.setIsReadOnly(1);
                }

                if ("java.lang.Integer".equalsIgnoreCase(columnVo.getFieldType())) {
                    onlCgformField.setDbType("int");
                } else if ("java.lang.Long".equalsIgnoreCase(columnVo.getFieldType())) {
                    onlCgformField.setDbType("int");
                } else if ("java.util.Date".equalsIgnoreCase(columnVo.getFieldType())) {
                    onlCgformField.setDbType("Date");
                    onlCgformField.setFieldShowType("date");
                } else if (!"java.lang.Double".equalsIgnoreCase(columnVo.getFieldType()) && !"java.lang.Float".equalsIgnoreCase(columnVo.getFieldType())) {
                    if (!"java.math.BigDecimal".equalsIgnoreCase(columnVo.getFieldType()) && !"BigDecimal".equalsIgnoreCase(columnVo.getFieldType())) {
                        if (!"byte[]".equalsIgnoreCase(columnVo.getFieldType()) && !columnVo.getFieldType().contains("blob")) {
                            if (!"java.lang.Object".equals(columnVo.getFieldType()) || !"text".equalsIgnoreCase(columnVo.getFieldDbType()) && !"ntext".equalsIgnoreCase(columnVo.getFieldDbType())) {
                                if ("java.lang.Object".equals(columnVo.getFieldType()) && "image".equalsIgnoreCase(columnVo.getFieldDbType())) {
                                    onlCgformField.setDbType("Blob");
                                } else {
                                    onlCgformField.setDbType("string");
                                }
                            } else {
                                onlCgformField.setDbType("Text");
                                onlCgformField.setFieldShowType("textarea");
                            }
                        } else {
                            onlCgformField.setDbType("Blob");
                            columnVo.setCharmaxLength((String) null);
                        }
                    } else {
                        onlCgformField.setDbType("BigDecimal");
                    }
                } else {
                    onlCgformField.setDbType("double");
                }

                if (oConvertUtils.isEmpty(columnVo.getPrecision()) && oConvertUtils.isNotEmpty(columnVo.getCharmaxLength())) {
                    if (Long.valueOf(columnVo.getCharmaxLength()) >= 3000L) {
                        onlCgformField.setDbType("Text");
                        onlCgformField.setFieldShowType("textarea");

                        try {
                            onlCgformField.setDbLength(Integer.valueOf(columnVo.getCharmaxLength()));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    } else {
                        onlCgformField.setDbLength(Integer.valueOf(columnVo.getCharmaxLength()));
                    }
                } else {
                    if (oConvertUtils.isNotEmpty(columnVo.getPrecision())) {
                        onlCgformField.setDbLength(Integer.valueOf(columnVo.getPrecision()));
                    } else if (onlCgformField.getDbType().equals("int")) {
                        onlCgformField.setDbLength(10);
                    }

                    if (oConvertUtils.isNotEmpty(columnVo.getScale())) {
                        onlCgformField.setDbPointLength(Integer.valueOf(columnVo.getScale()));
                    }
                }

                if (oConvertUtils.getInt(columnVo.getPrecision()) == -1 && oConvertUtils.getInt(columnVo.getScale()) == 0) {
                    onlCgformField.setDbType("Text");
                }

                if ("Blob".equals(onlCgformField.getDbType()) || "Text".equals(onlCgformField.getDbType()) || "Date".equals(onlCgformField.getDbType())) {
                    onlCgformField.setDbLength(0);
                    onlCgformField.setDbPointLength(0);
                }

                list.add(onlCgformField);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (oConvertUtils.isEmpty(onlCgformHead.getFormCategory())) {
            onlCgformHead.setFormCategory("bdfl_include");
        }

        this.save(onlCgformHead);
        this.fieldService.saveBatch(list);
    }

    @Override
    public OnlCgformEnhanceJs queryEnhanceJs(String formId, String cgJsType) {
        QueryWrapper queryWrapper = new QueryWrapper<OnlCgformEnhanceJs>();
        queryWrapper.eq("cgform_head_id", formId);
        queryWrapper.eq("cg_js_type", cgJsType);
        return this.onlCgformEnhanceJsMapper.selectOne(queryWrapper);
    }

    @Override
    public List<OnlCgformButton> queryButtonList(String code, boolean isListButton) {
        QueryWrapper var3 = new QueryWrapper<OnlCgformButton>();
        var3.eq("button_status", "1");
        var3.eq("cgform_head_id", code);
        if (isListButton) {
            ArrayList<String> list = new ArrayList<>();
            list.add("link");
            list.add("button");
            var3.in("button_style", list);
        } else {
            var3.eq("button_style", "form");
        }

        var3.orderByAsc("order_num");
        return this.onlCgformButtonMapper.selectList(var3);
    }

    @Override
    public void deleteRecordAndTable(String id) throws DBException, SQLException {
        OnlCgformHead onlCgformHead = this.getById(id);
        if (onlCgformHead == null) {
            throw new DBException("实体配置不存在");
        } else {
            long millis = System.currentTimeMillis();
            log.info("==判断表是否存在消耗时间 " + (System.currentTimeMillis() - millis) + "毫秒");
            if (dUtils.a(onlCgformHead.getTableName())) {
                String tableSQL = dUtils.getTableHandle().dropTableSQL(onlCgformHead.getTableName());
                log.info(" 删除表  executeDDL： " + tableSQL);
                this.baseMapper.executeDDL(tableSQL);
            }
            this.baseMapper.deleteById(id);
            this.a(onlCgformHead);
        }
    }

    @Override
    public void executeEnhanceList(OnlCgformHead head, String buttonCode, List<Map<String, Object>> dataList) throws BusinessException {
        QueryWrapper var4 = new QueryWrapper<OnlCgformEnhanceJava>();
        var4.eq("active_status", "1");
        var4.eq("button_code", buttonCode);
        var4.eq("cgform_head_id", head.getId());
        List var5 = this.onlCgformEnhanceJavaMapper.selectList(var4);
        if (var5 != null && var5.size() > 0) {
            Object var6 = this.a((OnlCgformEnhanceJava) var5.get(0));
            if (var6 != null && var6 instanceof CgformEnhanceJavaListInter) {
                CgformEnhanceJavaListInter var7 = (CgformEnhanceJavaListInter) var6;
                var7.execute(head.getTableName(), dataList);
            }
        }

    }

    private Object a(OnlCgformEnhanceJava var1) {
        if (var1 != null) {
            String var2 = var1.getCgJavaType();
            String var3 = var1.getCgJavaValue();
            if (oConvertUtils.isNotEmpty(var3)) {
                Object var4 = null;
                if ("class".equals(var2)) {
                    try {
                        var4 = MyClassLoader.getClassByScn(var3).newInstance();
                    } catch (InstantiationException var6) {
                        log.error(var6.getMessage(), var6);
                    } catch (IllegalAccessException var7) {
                        log.error(var7.getMessage(), var7);
                    }
                } else if ("spring".equals(var2)) {
                    var4 = SpringContextUtils.getBean(var3);
                }

                return var4;
            }
        }

        return null;
    }

    @Override
    @Transactional
    public Result<?> editAll(AModel model) {
        OnlCgformHead var2 = model.getHead();
        OnlCgformHead var3 = super.getById(var2.getId());
        if (var3 == null) {
            return Result.error("未找到对应实体");
        } else {
            String var4 = var3.getIsDbSynch();
            if (DbSelectUtils.a(var3, var2)) {
                var4 = "N";
            }

            Integer var5 = var3.getTableVersion();
            if (var5 == null) {
                var5 = 1;
            }

            var2.setTableVersion(var5 + 1);
            List var6 = model.getFields();
            List<OnlCgformIndex> var7 = model.getIndexs();
            ArrayList var8 = new ArrayList();
            ArrayList var9 = new ArrayList();
            Iterator var10 = var6.iterator();

            while(var10.hasNext()) {
                OnlCgformField var11 = (OnlCgformField)var10.next();
                String var12 = String.valueOf(var11.getId());
                this.a(var11);
                if (var12.length() == 32) {
                    var9.add(var11);
                } else {
                    String var13 = "_pk";
                    if (!var13.equals(var12)) {
                        var11.setId(null);
                        var11.setCgformHeadId(var2.getId());
                        var8.add(var11);
                    }
                }
            }

            if (var8.size() > 0) {
                var4 = "N";
            }

            int var18 = 0;

            Iterator var19;
            OnlCgformField var21;
            for(var19 = var9.iterator(); var19.hasNext(); this.fieldService.updateById(var21)) {
                var21 = (OnlCgformField)var19.next();
                OnlCgformField var23 = this.fieldService.getById(var21.getId());
                boolean var14 = DbSelectUtils.a(var23, var21);
                if (var14) {
                    var4 = "N";
                }

                if ((var23.getOrderNum() == null ? 0 : var23.getOrderNum()) > var18) {
                    var18 = var23.getOrderNum();
                }
            }

            for(var19 = var8.iterator(); var19.hasNext(); this.fieldService.save(var21)) {
                var21 = (OnlCgformField)var19.next();
                if (var21.getOrderNum() == null) {
                    ++var18;
                    var21.setOrderNum(var18);
                }
            }

            List var20 = this.indexService.getCgformIndexsByCgformId(var2.getId());
            ArrayList var22 = new ArrayList();
            ArrayList var24 = new ArrayList();
            Iterator var25 = var7.iterator();

            OnlCgformIndex var15;
            while(var25.hasNext()) {
                var15 = (OnlCgformIndex)var25.next();
                String var16 = String.valueOf(var15.getId());
                if (var16.length() == 32) {
                    var24.add(var15);
                } else {
                    var15.setId(null);
                    var15.setCgformHeadId(var2.getId());
                    var22.add(var15);
                }
            }

            var25 = var20.iterator();

            while(var25.hasNext()) {
                var15 = (OnlCgformIndex)var25.next();
                OnlCgformIndex finalVar1 = var15;
                boolean var26 = var7.stream().anyMatch((var1) ->
                    finalVar1.getId().equals(var1.getId())
                );
                if (!var26) {
                    var15.setDelFlag(CommonConstant.DEL_FLAG_1);
                    var24.add(var15);
                    var4 = "N";
                }
            }

            if (var22.size() > 0) {
                var4 = "N";
                this.indexService.saveBatch(var22);
            }

            for(var25 = var24.iterator(); var25.hasNext(); this.indexService.updateById(var15)) {
                var15 = (OnlCgformIndex)var25.next();
                OnlCgformIndex var27 = this.indexService.getById(var15.getId());
                boolean var17 = DbSelectUtils.a(var27, var15);
                if (var17) {
                    var4 = "N";
                    var15.setIsDbSynch("N");
                }
            }

            if (model.getDeleteFieldIds().size() > 0) {
                var4 = "N";
                this.fieldService.removeByIds(model.getDeleteFieldIds());
            }

            var2.setIsDbSynch(var4);
            super.updateById(var2);
            this.a(var2, var6);
            this.b(var2, var6);
            return Result.ok("全部修改成功");
        }
    }

    /**
     * 移除列表数据
     * @param id
     */
    @Override
    public void deleteRecord(String id) throws DBException {
        OnlCgformHead onlCgformHead= this.getById(id);
        if (onlCgformHead == null) {
            throw new DBException("实体配置不存在");
        } else {
            this.baseMapper.deleteById(id);
            this.a(onlCgformHead);
        }
    }

    @Override
    public Map<String, Object> queryManyFormData(String code, String id) throws DBException {
        OnlCgformHead var3 = (OnlCgformHead)this.getById(code);
        if (var3 == null) {
            throw new DBException("数据库主表ID[" + code + "]不存在");
        } else {
            List var4 = this.fieldService.queryFormFields(code, true);
            Map var5 = this.fieldService.queryFormData(var4, var3.getTableName(), id);
            if (var3.getTableType() == 2) {
                String var6 = var3.getSubTableStr();
                if (oConvertUtils.isNotEmpty(var6)) {
                    String[] var7 = var6.split(",");
                    String[] var8 = var7;
                    int var9 = var7.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        String var11 = var8[var10];
                        OnlCgformHead var12 = (OnlCgformHead)((OnlCgformHeadMapper)this.baseMapper).selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",var11));
                        if (var12 != null) {
                            List var13 = this.fieldService.queryFormFields(var12.getId(), false);
                            String var14 = "";
                            String var15 = null;
                            Iterator var16 = var13.iterator();

                            while(var16.hasNext()) {
                                OnlCgformField var17 = (OnlCgformField)var16.next();
                                if (!oConvertUtils.isEmpty(var17.getMainField())) {
                                    var14 = var17.getDbFieldName();
                                    String var18 = var17.getMainField();
                                    if (null == var5.get(var18)) {
                                        var15 = var5.get(var18.toUpperCase()).toString();
                                    } else {
                                        var15 = var5.get(var18).toString();
                                    }
                                }
                            }

                            List var19 = this.fieldService.querySubFormData(var13, var11, var14, var15);
                            if (var19 != null && var19.size() != 0) {
                                var5.put(var11, DbSelectUtils.d(var19));
                            } else {
                                var5.put(var11, new String[0]);
                            }
                        }
                    }
                }
            }

            return var5;
        }
    }

    private void b(OnlCgformHead var1, List<OnlCgformField> var2) {
        List var3 = this.list(new QueryWrapper<OnlCgformHead>().eq("physic_id",var1.getId()));
        if (var3 != null && var3.size() > 0) {
            Iterator var4 = var3.iterator();

            while(true) {
                List var6;
                String var13;
                ArrayList var19;
                Iterator var22;
                label108:
                do {
                    while(var4.hasNext()) {
                        OnlCgformHead var5 = (OnlCgformHead) var4.next();
                        var6 = this.fieldService.list(new QueryWrapper<OnlCgformField>().eq("cgform_head_id",var5.getId()));
                        OnlCgformField var9;
                        if (var6 != null && var6.size() != 0) {
                            HashMap var15 = new HashMap();
                            Iterator var16 = var6.iterator();

                            while(var16.hasNext()) {
                                var9 = (OnlCgformField)var16.next();
                                var15.put(var9.getDbFieldName(), 1);
                            }

                            HashMap var17 = new HashMap();
                            Iterator var18 = var2.iterator();

                            while(var18.hasNext()) {
                                OnlCgformField var10 = (OnlCgformField)var18.next();
                                var17.put(var10.getDbFieldName(), 1);
                            }

                            var19 = new ArrayList();
                            ArrayList var20 = new ArrayList();
                            Iterator var11 = var17.keySet().iterator();

                            while(var11.hasNext()) {
                                String var12 = (String)var11.next();
                                if (var15.get(var12) == null) {
                                    var20.add(var12);
                                } else {
                                    var19.add(var12);
                                }
                            }

                            ArrayList var21 = new ArrayList();
                            var22 = var15.keySet().iterator();

                            while(var22.hasNext()) {
                                var13 = (String)var22.next();
                                if (var17.get(var13) == null) {
                                    var21.add(var13);
                                }
                            }

                            OnlCgformField var23;
                            if (var21.size() > 0) {
                                var22 = var6.iterator();

                                while(var22.hasNext()) {
                                    var23 = (OnlCgformField)var22.next();
                                    if (var21.contains(var23.getDbFieldName())) {
                                        this.fieldService.removeById(var23.getId());
                                    }
                                }
                            }

                            if (var20.size() > 0) {
                                var22 = var2.iterator();

                                while(var22.hasNext()) {
                                    var23 = (OnlCgformField)var22.next();
                                    if (var20.contains(var23.getDbFieldName())) {
                                        OnlCgformField var14 = new OnlCgformField();
                                        var14.setCgformHeadId(var5.getId());
                                        this.a(var23, var14);
                                        this.fieldService.save(var14);
                                    }
                                }
                            }
                            continue label108;
                        }

                        Iterator var7 = var2.iterator();

                        while(var7.hasNext()) {
                            OnlCgformField var8 = (OnlCgformField)var7.next();
                            var9 = new OnlCgformField();
                            var9.setCgformHeadId(var5.getId());
                            this.a(var8, var9);
                            this.fieldService.save(var9);
                        }
                    }

                    return;
                } while(var19.size() <= 0);

                var22 = var19.iterator();

                while(var22.hasNext()) {
                    var13 = (String)var22.next();
                    this.b(var13, var2, var6);
                }
            }
        }
    }

    private void b(String var1, List<OnlCgformField> var2, List<OnlCgformField> var3) {
        OnlCgformField var4 = null;
        Iterator var5 = var2.iterator();

        while(var5.hasNext()) {
            OnlCgformField var6 = (OnlCgformField)var5.next();
            if (var1.equals(var6.getDbFieldName())) {
                var4 = var6;
            }
        }

        OnlCgformField var8 = null;
        Iterator var9 = var3.iterator();

        while(var9.hasNext()) {
            OnlCgformField var7 = (OnlCgformField)var9.next();
            if (var1.equals(var7.getDbFieldName())) {
                var8 = var7;
            }
        }

        if (var4 != null && var8 != null) {
            boolean var10 = false;
            if (!var4.getDbType().equals(var8.getDbType())) {
                var8.setDbType(var4.getDbType());
                var10 = true;
            }

            if (!var4.getDbDefaultVal().equals(var8.getDbDefaultVal())) {
                var8.setDbDefaultVal(var4.getDbDefaultVal());
                var10 = true;
            }

            if (var4.getDbLength() != var8.getDbLength()) {
                var8.setDbLength(var4.getDbLength());
                var10 = true;
            }

            if (var4.getDbIsNull() != var8.getDbIsNull()) {
                var8.setDbIsNull(var4.getDbIsNull());
                var10 = true;
            }

            if (var10) {
                this.fieldService.updateById(var8);
            }
        }

    }

    private void a(OnlCgformField var1, OnlCgformField var2) {
        var2.setDbDefaultVal(var1.getDbDefaultVal());
        var2.setDbFieldName(var1.getDbFieldName());
        var2.setDbFieldNameOld(var1.getDbFieldNameOld());
        var2.setDbFieldTxt(var1.getDbFieldTxt());
        var2.setDbIsKey(var1.getDbIsKey());
        var2.setDbIsNull(var1.getDbIsNull());
        var2.setDbLength(var1.getDbLength());
        var2.setDbPointLength(var1.getDbPointLength());
        var2.setDbType(var1.getDbType());
        var2.setDictField(var1.getDictField());
        var2.setDictTable(var1.getDictTable());
        var2.setDictText(var1.getDictText());
        var2.setFieldExtendJson(var1.getFieldExtendJson());
        var2.setFieldHref(var1.getFieldHref());
        var2.setFieldLength(var1.getFieldLength());
        var2.setFieldMustInput(var1.getFieldMustInput());
        var2.setFieldShowType(var1.getFieldShowType());
        var2.setFieldValidType(var1.getFieldValidType());
        var2.setFieldDefaultValue(var1.getFieldDefaultValue());
        var2.setIsQuery(var1.getIsQuery());
        var2.setIsShowForm(var1.getIsShowForm());
        var2.setIsShowList(var1.getIsShowList());
        var2.setMainField((String)null);
        var2.setMainTable((String)null);
        var2.setOrderNum(var1.getOrderNum());
        var2.setQueryMode(var1.getQueryMode());
        var2.setIsReadOnly(var1.getIsReadOnly());
        var2.setSortFlag(var1.getSortFlag());
        var2.setQueryDefVal(var1.getQueryDefVal());
        var2.setQueryConfigFlag(var1.getQueryConfigFlag());
        var2.setQueryDictField(var1.getQueryDictField());
        var2.setQueryDictTable(var1.getQueryDictTable());
        var2.setQueryDictText(var1.getQueryDictText());
        var2.setQueryMustInput(var1.getQueryMustInput());
        var2.setQueryShowType(var1.getQueryShowType());
        var2.setQueryValidType(var1.getQueryValidType());
        var2.setConverter(var1.getConverter());
    }

    private void a(OnlCgformHead var1, List<OnlCgformField> var2) {
        if (var1.getTableType() == 3) {
            var1 = this.baseMapper.selectById(var1.getId());

            for(int var3 = 0; var3 < var2.size(); ++var3) {
                OnlCgformField var4 = var2.get(var3);
                String var5 = var4.getMainTable();
                if (!oConvertUtils.isEmpty(var5)) {
                    OnlCgformHead var6 = (this.baseMapper).selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",var5));
                    if (var6 != null) {
                        String var7 = var6.getSubTableStr();
                        if (oConvertUtils.isEmpty(var7)) {
                            var7 = var1.getTableName();
                        } else if (var7.indexOf(var1.getTableName()) < 0) {
                            ArrayList var8 = new ArrayList(Arrays.asList(var7.split(",")));

                            for(int var9 = 0; var9 < var8.size(); ++var9) {
                                String var10 = (String)var8.get(var9);
                                OnlCgformHead var11 = this.baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",var10));
                                if (var11 != null && var1.getTabOrderNum() < oConvertUtils.getInt(var11.getTabOrderNum(), 0)) {
                                    var8.add(var9, var1.getTableName());
                                    break;
                                }
                            }

                            if (var8.indexOf(var1.getTableName()) < 0) {
                                var8.add(var1.getTableName());
                            }

                            var7 = String.join(",", var8);
                        }

                        var6.setSubTableStr(var7);
                        this.baseMapper.updateById(var6);
                        break;
                    }
                }
            }
        } else {
            List var12 = this.baseMapper.selectList(new QueryWrapper<OnlCgformHead>().eq("sub_table_str",var1.getTableName()));
            if (var12 != null && var12.size() > 0) {
                Iterator var13 = var12.iterator();

                while(var13.hasNext()) {
                    OnlCgformHead var14 = (OnlCgformHead) var13.next();
                    String var15 = var14.getSubTableStr();
                    if (var14.getSubTableStr().equals(var1.getTableName())) {
                        var15 = "";
                    } else if (var14.getSubTableStr().startsWith(var1.getTableName() + ",")) {
                        var15 = var15.replace(var1.getTableName() + ",", "");
                    } else if (var14.getSubTableStr().endsWith("," + var1.getTableName())) {
                        var15 = var15.replace("," + var1.getTableName(), "");
                    } else if (var14.getSubTableStr().indexOf("," + var1.getTableName() + ",") != -1) {
                        var15 = var15.replace("," + var1.getTableName() + ",", ",");
                    }

                    var14.setSubTableStr(var15);
                    this.baseMapper.updateById(var14);
                }
            }
        }

    }

    private void a(OnlCgformField var1) {
        if ("Text".equals(var1.getDbType()) || "Blob".equals(var1.getDbType())) {
            var1.setDbLength(0);
            var1.setDbPointLength(0);
        }

    }

    @Override
    @Transactional
    public void editManyFormData(String code, JSONObject json) throws DBException, BusinessException {
        OnlCgformHead var3 = this.getById(code);
        if (var3 == null) {
            throw new DBException("数据库主表ID[" + code + "]不存在");
        } else {
            String var4 = "edit";
            this.executeEnhanceJava(var4, "start", var3, json);
            String var5 = var3.getTableName();
            if ("Y".equals(var3.getIsTree())) {
                this.fieldService.editTreeFormData(code, var5, json, var3.getTreeIdField(), var3.getTreeParentIdField());
            } else {
                this.fieldService.editFormData(code, var5, json, false);
            }

            if (var3.getTableType() == 2) {
                String var6 = var3.getSubTableStr();
                if (oConvertUtils.isNotEmpty(var6)) {
                    String[] var7 = var6.split(",");
                    String[] var8 = var7;
                    int var9 = var7.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        String var11 = var8[var10];
                        OnlCgformHead var12 = this.baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",var11));
                        if (var12 != null) {
                            List var13 = this.fieldService.list(new QueryWrapper<OnlCgformField>().eq("cgform_head_id",var12.getId()));
                            String var14 = "";
                            String var15 = null;
                            Iterator var16 = var13.iterator();

                            while(var16.hasNext()) {
                                OnlCgformField var17 = (OnlCgformField)var16.next();
                                if (!oConvertUtils.isEmpty(var17.getMainField())) {
                                    var14 = var17.getDbFieldName();
                                    String var18 = var17.getMainField();
                                    if (json.get(var18.toLowerCase()) != null) {
                                        var15 = json.getString(var18.toLowerCase());
                                    }

                                    if (json.get(var18.toUpperCase()) != null) {
                                        var15 = json.getString(var18.toUpperCase());
                                    }
                                }
                            }

                            if (!oConvertUtils.isEmpty(var15)) {
                                this.fieldService.deleteAutoList(var11, var14, var15);
                                JSONArray var19 = json.getJSONArray(var11);
                                if (var19 != null && var19.size() != 0) {
                                    for(int var20 = 0; var20 < var19.size(); ++var20) {
                                        JSONObject var21 = var19.getJSONObject(var20);
                                        if (var15 != null) {
                                            var21.put(var14, var15);
                                        }

                                        this.fieldService.saveFormData(var13, var11, var21);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.executeEnhanceJava(var4, "end", var3, json);
            this.executeEnhanceSql(var4, code, json);
        }
    }

    @Override
    @Transactional
    public void saveManyFormData(String code, JSONObject json, String xAccessToken) throws DBException, BusinessException {
        OnlCgformHead var4 =  this.getById(code);
        if (var4 == null) {
            throw new DBException("数据库主表ID[" + code + "]不存在");
        } else {
            String var5 = "add";
            this.executeEnhanceJava(var5, "start", var4, json);
            String var6 = DbSelectUtils.f(var4.getTableName());
            if (var4.getTableType() == 2) {
                String var7 = var4.getSubTableStr();
                if (oConvertUtils.isNotEmpty(var7)) {
                    String[] var8 = var7.split(",");
                    String[] var9 = var8;
                    int var10 = var8.length;

                    for (int var11 = 0; var11 < var10; ++var11) {
                        String var12 = var9[var11];
                        JSONArray var13 = json.getJSONArray(var12);
                        if (var13 != null && var13.size() != 0) {
                            OnlCgformHead var14 = this.baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",var12));
                            if (var14 != null) {
                                List var15 = this.fieldService.list(new QueryWrapper<OnlCgformField>().eq("cgform_head_id",var14.getId()));
                                String var16 = "";
                                String var17 = null;
                                Iterator var18 = var15.iterator();

                                while (var18.hasNext()) {
                                    OnlCgformField var19 = (OnlCgformField) var18.next();
                                    if (!oConvertUtils.isEmpty(var19.getMainField())) {
                                        var16 = var19.getDbFieldName();
                                        String var20 = var19.getMainField();
                                        if (json.get(var20.toLowerCase()) != null) {
                                            var17 = json.getString(var20.toLowerCase());
                                        }

                                        if (json.get(var20.toUpperCase()) != null) {
                                            var17 = json.getString(var20.toUpperCase());
                                        }
                                    }
                                }

                                for (int var27 = 0; var27 < var13.size(); ++var27) {
                                    JSONObject var28 = var13.getJSONObject(var27);
                                    if (var17 != null) {
                                        var28.put(var16, var17);
                                    }

                                    this.fieldService.saveFormData(var15, var12, var28);
                                }
                            }
                        }
                    }
                }
            }

            if ("Y".equals(var4.getIsTree())) {
                this.fieldService.saveTreeFormData(code, var6, json, var4.getTreeIdField(), var4.getTreeParentIdField());
            } else {
                this.fieldService.saveFormData(code, var6, json, false);
            }

            this.executeEnhanceSql(var5, code, json);
            this.executeEnhanceJava(var5, "end", var4, json);
            if (oConvertUtils.isNotEmpty(json.get("bpm_status")) || oConvertUtils.isNotEmpty(json.get("bpm_status".toUpperCase()))) {
                try {
                    HttpHeaders var22 = new HttpHeaders();
                    var22.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
                    var22.set("Accept", "application/json;charset=UTF-8");
                    var22.set("X-Access-Token", xAccessToken);
                    JSONObject var23 = new JSONObject();
                    var23.put("flowCode", "onl_" + var4.getTableName());
                    var23.put("id", json.get("id"));
                    var23.put("formUrl", "modules/bpm/task/form/OnlineFormDetail");
                    var23.put("formUrlMobile", "online/OnlineDetailForm");
                    String var24 = RestUtil.getBaseUrl() + "/process/extActProcess/saveMutilProcessDraft";
                    JSONObject var25 = (JSONObject) RestUtil.request(var24, HttpMethod.POST, var22, (JSONObject) null, var23, JSONObject.class).getBody();
                    if (var25 != null) {
                        String var26 = var25.getString("result");
                        log.info("保存流程草稿 dataId : " + var26);
                    }
                } catch (Exception var21) {
                    log.error("保存流程草稿异常, " + var21.getMessage(), var21);
                }
            }

        }
    }

    @Override
    @Transactional
    public void deleteOneTableInfo(String formId, String dataId) throws BusinessException {
        OnlCgformHead var3 = this.getById(formId);
        if (var3 == null) {
            throw new BusinessException("未找到表配置信息");
        } else {
            String var4 = DbSelectUtils.f(var3.getTableName());
            Map var5 = this.baseMapper.queryOneByTableNameAndId(var4, dataId);
            if (var5 == null) {
                throw new BusinessException("未找到数据信息");
            } else {
                String var6 = "delete";
                JSONObject var7 = JSONObject.parseObject(JSON.toJSONString(var5));
                this.executeEnhanceJava(var6, "start", var3, var7);
                this.updateParentNode(var3, dataId);
                if (var3.getTableType() == 2) {
                    this.fieldService.deleteAutoListMainAndSub(var3, dataId);
                } else {
                    String var8 = "delete from " + var4 + " where id = '" + dataId + "'";
                    this.baseMapper.deleteOne(var8);
                }

                this.executeEnhanceSql(var6, formId, var7);
                this.executeEnhanceJava(var6, "end", var3, var7);
            }
        }
    }

    public void executeEnhanceSql(String buttonCode, String formId, JSONObject json) {
        QueryWrapper var4 = new QueryWrapper<>();
        var4.eq("button_code", buttonCode);
        var4.eq("cgform_head_id", formId);
        OnlCgformEnhanceSql var5 = this.onlCgformEnhanceSqlMapper.selectOne(var4);
        if (var5 != null && oConvertUtils.isNotEmpty(var5.getCgbSql())) {
            String var6 = DbSelectUtils.a(var5.getCgbSql(), json);
            String[] var7 = var6.split(";");
            String[] var8 = var7;
            int var9 = var7.length;

            for (int var10 = 0; var10 < var9; ++var10) {
                String var11 = var8[var10];
                if (var11 != null && !var11.toLowerCase().trim().equals("")) {
//                    OnlCgreportAPI.info(" online sql 增强： " + var11);
                    ((OnlCgformHeadMapper) this.baseMapper).executeDDL(var11);
                }
            }
        }

    }

    public void updateParentNode(OnlCgformHead head, String dataId) {
        if ("Y".equals(head.getIsTree())) {
            String var3 = DbSelectUtils.f(head.getTableName());
            String var4 = head.getTreeParentIdField();
            Map var5 = this.baseMapper.queryOneByTableNameAndId(var3, dataId);
            String var6 = null;
            if (var5.get(var4) != null && !"0".equals(var5.get(var4))) {
                var6 = var5.get(var4).toString();
            } else if (var5.get(var4.toUpperCase()) != null && !"0".equals(var5.get(var4.toUpperCase()))) {
                var6 = var5.get(var4.toUpperCase()).toString();
            }

            if (var6 != null) {
                Integer var7 = this.baseMapper.queryChildNode(var3, var4, var6);
                if (var7 == 1) {
                    String var8 = head.getTreeIdField();
                    this.fieldService.updateTreeNodeNoChild(var3, var8, var6);
                }
            }
        }

    }

    public int executeEnhanceJava(String buttonCode, String eventType, OnlCgformHead head, JSONObject json) throws BusinessException {
        QueryWrapper var5 = new QueryWrapper<OnlCgformEnhanceJava>();
        var5.eq("active_status", "1");
        var5.eq("button_code", buttonCode);
        var5.eq("cgform_head_id", head.getId());
        var5.eq("event", eventType);
        OnlCgformEnhanceJava var6 = this.onlCgformEnhanceJavaMapper.selectOne(var5);
        Object var7 = this.a(var6);
        if (var7 != null && var7 instanceof CgformEnhanceJavaInter) {
            CgformEnhanceJavaInter var8 = (CgformEnhanceJavaInter) var7;
            return var8.execute(head.getTableName(), json);
        } else {
            return 1;
        }
    }

    /**
     * 生成表单具体类型
     *
     * @param model
     * @param list1
     * @param list2
     * @return
     */
    private OnlCgformField getOnlCgformField(String model, List<ColumnVo> list1, List<ColumnVo> list2) {
        QueryWrapper<OnlCgformField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cgform_head_id", model);
        queryWrapper.orderByAsc("order_num");
        List<OnlCgformField> onlCgformFieldList = fieldService.list(queryWrapper);
        OnlCgformField OnlCgformHeadEntity = null;
        Iterator iterator = onlCgformFieldList.iterator();

        while (true) {
            OnlCgformField onlCgformField;
            ColumnVo columnVo;
            do {
                if (!iterator.hasNext()) {
                    return OnlCgformHeadEntity;
                }

                onlCgformField = (OnlCgformField) iterator.next();
                if (oConvertUtils.isNotEmpty(onlCgformField.getMainTable())) {
                    OnlCgformHeadEntity = onlCgformField;
                }

                columnVo = new ColumnVo();
                columnVo.setFieldLength(onlCgformField.getFieldLength());
                columnVo.setFieldHref(onlCgformField.getFieldHref());
                columnVo.setFieldValidType(onlCgformField.getFieldValidType());
                columnVo.setFieldDefault(onlCgformField.getDbDefaultVal());
                columnVo.setFieldShowType(onlCgformField.getFieldShowType());
                columnVo.setFieldOrderNum(onlCgformField.getOrderNum());
                columnVo.setIsKey(onlCgformField.getDbIsKey() == 1 ? "Y" : "N");
                columnVo.setIsShow(onlCgformField.getIsShowForm() == 1 ? "Y" : "N");
                columnVo.setIsShowList(onlCgformField.getIsShowList() == 1 ? "Y" : "N");
                columnVo.setIsQuery(onlCgformField.getIsQuery() == 1 ? "Y" : "N");
                columnVo.setQueryMode(onlCgformField.getQueryMode());
                columnVo.setDictField(onlCgformField.getDictField());
                if (oConvertUtils.isNotEmpty(onlCgformField.getDictTable()) && onlCgformField.getDictTable().indexOf("where") > 0) {
                    columnVo.setDictTable(onlCgformField.getDictTable().split("where")[0].trim());
                } else {
                    columnVo.setDictTable(onlCgformField.getDictTable());
                }

                columnVo.setDictText(onlCgformField.getDictText());
                columnVo.setFieldDbName(onlCgformField.getDbFieldName());
                columnVo.setFieldName(oConvertUtils.camelName(onlCgformField.getDbFieldName()));
                columnVo.setFiledComment(onlCgformField.getDbFieldTxt());
                columnVo.setFieldDbType(onlCgformField.getDbType());
                columnVo.setFieldType(typeUtil(onlCgformField.getDbType()));
                columnVo.setClassType(onlCgformField.getFieldShowType());
                columnVo.setClassType_row(onlCgformField.getFieldShowType());
                if (onlCgformField.getDbIsNull() != 0 && !"*".equals(onlCgformField.getFieldValidType()) && !"1".equals(onlCgformField.getFieldMustInput())) {
                    columnVo.setNullable("Y");
                } else {
                    columnVo.setNullable("N");
                }

                if ("switch".equals(onlCgformField.getFieldShowType())) {
                    columnVo.setDictField(onlCgformField.getFieldExtendJson());
                }

                list2.add(columnVo);
            } while (onlCgformField.getIsShowForm() != 1 && onlCgformField.getIsShowList() != 1);

            list1.add(columnVo);
        }
    }

    /**
     * 字段类型
     *
     * @param str
     * @return
     */
    private String typeUtil(String str) {
        str = str.toLowerCase();
        if (str.indexOf("int") >= 0) {
            return "java.lang.Integer";
        } else if (str.indexOf("double") >= 0) {
            return "java.lang.Double";
        } else if (str.indexOf("decimal") >= 0) {
            return "java.math.BigDecimal";
        } else {
            return str.indexOf("date") >= 0 ? "java.util.Date" : "java.lang.String";
        }
    }

    /**
     * 数据库字段类型
     *
     * @param onlCgformField
     */
    private void getDbType(OnlCgformField onlCgformField) {
        if ("Text".equals(onlCgformField.getDbType()) || "Blob".equals(onlCgformField.getDbType())) {
            onlCgformField.setDbLength(0);
            onlCgformField.setDbPointLength(0);
        }
    }

    /**
     * 具体表单实现
     *
     * @param onlCgformHead
     * @param onlCgformFieldList
     */
    private void getOnlineType(OnlCgformHead onlCgformHead, List<OnlCgformField> onlCgformFieldList) {
        if (onlCgformHead.getTableType() == 3) {
            onlCgformHead = this.baseMapper.selectById(onlCgformHead.getId());

            for (int i = 0; i < onlCgformFieldList.size(); i++) {
                OnlCgformField onlCgformField = onlCgformFieldList.get(i);
                String mainTable = onlCgformField.getMainTable();
                if (!oConvertUtils.isEmpty(mainTable)) {
                    OnlCgformHead OnlCgformHead = this.baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name", mainTable));
                    if (OnlCgformHead != null) {
                        String subTableStr = OnlCgformHead.getSubTableStr();
                        if (oConvertUtils.isEmpty(subTableStr)) {
                            subTableStr = onlCgformHead.getTableName();
                        } else if (subTableStr.indexOf(onlCgformHead.getTableName()) < 0) {
                            ArrayList list = new ArrayList(Arrays.asList(subTableStr.split(",")));

                            for (int j = 0; j < list.size(); j++) {
                                String onlHead = (String) list.get(j);
                                OnlCgformHead onlCgformHead1 = this.baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name", onlHead));
                                if (onlCgformHead1 != null && onlCgformHead.getTabOrderNum() < oConvertUtils.getInt(onlCgformHead1.getTabOrderNum(), 0)) {
                                    list.add(j, onlCgformHead.getTableName());
                                    break;
                                }
                            }

                            if (list.indexOf(onlCgformHead.getTableName()) < 0) {
                                list.add(onlCgformHead.getTableName());
                            }

                            subTableStr = String.join(",", list);
                        }

                        OnlCgformHead.setSubTableStr(subTableStr);
                        this.baseMapper.updateById(OnlCgformHead);
                        break;
                    }
                }
            }
        } else {
            List<OnlCgformHead> onlCgFormHeadList = this.baseMapper.selectList(new QueryWrapper<OnlCgformHead>().eq("sub_table_str", onlCgformHead.getTableName()));
            if (onlCgFormHeadList != null && onlCgFormHeadList.size() > 0) {
                Iterator onlCgformHead3 = onlCgFormHeadList.iterator();

                while (onlCgformHead3.hasNext()) {
                    OnlCgformHead onlCgformHead4 = (OnlCgformHead) onlCgformHead3.next();
                    String onlCgformHead5 = onlCgformHead4.getSubTableStr();
                    if (onlCgformHead4.getSubTableStr().equals(onlCgformHead.getTableName())) {
                        onlCgformHead5 = "";
                    } else if (onlCgformHead4.getSubTableStr().startsWith(onlCgformHead.getTableName() + ",")) {
                        onlCgformHead5 = onlCgformHead5.replace(onlCgformHead.getTableName() + ",", "");
                    } else if (onlCgformHead4.getSubTableStr().endsWith("," + onlCgformHead.getTableName())) {
                        onlCgformHead5 = onlCgformHead5.replace("," + onlCgformHead.getTableName(), "");
                    } else if (onlCgformHead4.getSubTableStr().indexOf("," + onlCgformHead.getTableName() + ",") != -1) {
                        onlCgformHead5 = onlCgformHead5.replace("," + onlCgformHead.getTableName() + ",", ",");
                    }

                    onlCgformHead4.setSubTableStr(onlCgformHead5);
                    this.baseMapper.updateById(onlCgformHead4);
                }
            }
        }
    }

    private void a(OnlCgformHead onlCgformHead) {
        if (onlCgformHead.getTableType() == 3) {
            QueryWrapper<OnlCgformField> queryWrapper = new QueryWrapper<OnlCgformField>().eq("cgform_head_id", onlCgformHead.getId());
            List<OnlCgformField> list = this.fieldService.list(queryWrapper);
            String mainTable = null;
            Iterator iterator = list.iterator();
            if (iterator.hasNext()) {
                OnlCgformField onlCgformField = (OnlCgformField) iterator.next();
                mainTable = onlCgformField.getMainTable();
            }

            if (oConvertUtils.isNotEmpty(mainTable)) {
                OnlCgformHead OnlCgformHead = this.baseMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name", mainTable));
                if (OnlCgformHead != null) {
                    String var9 = OnlCgformHead.getSubTableStr();
                    if (oConvertUtils.isNotEmpty(var9)) {
                        List<String> stringList = Arrays.asList(var9.split(",")).stream().collect(Collectors.toList());
                        stringList.remove(onlCgformHead.getTableName());
                        OnlCgformHead.setSubTableStr(String.join(",", stringList));
                        this.baseMapper.updateById(OnlCgformHead);
                    }
                }
            }
        }

    }
}
