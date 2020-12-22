package smartform.form.service.impl;

import auth.domain.dict.service.ISysDictService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartform.common.util.CheckDataUtil;
import smartform.common.util.DBCutConstants;
import smartform.common.util.UUIDUtil;
import smartform.common.util.dubbo.RulesUtil;
import smartform.exception.GlobalException;
import smartform.form.mapper.*;
import smartform.form.model.*;
import smartform.form.model.entity.ComponentStateEntity;
import smartform.form.model.entity.FormContentComponentEntity;
import smartform.form.model.entity.FormContentTableEntity;
import smartform.form.model.entity.FormContentUploadsEntity;
import smartform.form.redis.FormCategoryService;
import smartform.form.redis.SmartFormContentService;
import smartform.form.redis.SmartFormService;
import smartform.query.Query;
import smartform.widget.model.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hou
 * @ClassName: SmartFormServiceImpl
 * @Description: 表单数据Service类
 * @date 2018年9月24日 下午2:56:34
 */
@Service("SmartFormService")
@DS("smart-form")
public class SmartFormServiceImpl implements smartform.form.service.SmartFormService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private FormCategoryService categoryDao;

    @Autowired
    private SmartFormService dao;

    @Autowired
    private SmartFormMapper smartFormMapper;

    @Autowired
    private ISysDictService iSysDictService;

    /**
     * SmartForm内容基本信息的redis操作类
     */
    @Autowired
    private SmartFormContentService formContentDao;

    /**
     * 内容主表操作
     */
    @Autowired
    FormContentMainMapper contentMainDao;

    /**
     * 组件表操作
     */
    @Autowired
    FormContentComponentMapper componentDao;

    /**
     * 表格表操作
     */
    @Autowired
    FormContentTableMapper tableDao;

    /**
     * 组件状态操作
     */
    @Autowired
    ComponentStateMapper componentStateDao;

    /**
     * 上传列表操作
     */
    @Autowired
    FormContentUploadsMapper uploadsDao;

    @Override
    public List<FormPage> smartFormStatus(String formId, String contentId) {
        SmartForm smartForm = smartFormMapper.selectById(formId);
        if (smartForm != null) {
            String pagesJson = smartForm.getPagesJson();
            if (pagesJson != null && !pagesJson.equals("")) {
                List<FormPage> formPageList = JSON.parseArray(pagesJson, FormPage.class);
                formPageList.stream().forEach(item -> item.setFillState(ContentStateType.UNFILL.value));
                return formPageList;
            }
        }
        return null;
    }

    @Override
    public SmartForm smartForm(String id, boolean hasOptions) {
        SmartForm smartForm = loadSmartForm(id);
        if (smartForm != null) {
            if (hasOptions) {
                // 使用唯一Set进行选项源ID存储
                Set<String> optionsIDs = new HashSet<String>();
                if (smartForm.getPageList() != null) {
                    for (FormPage page : smartForm.getPageList()) {
                        if (page.getFieldList() != null && page.getFieldList().size() > 0) {
                            // 遍历分页中的字段获取选项源
                            for (FormFieldBase pageField : page.getFieldList()) {
                                if (pageField instanceof Group) {
                                    Group group = (Group) pageField;
                                    this.getGroupSelectOptions(group, optionsIDs);
                                } else {
                                    this.getSelectOptions((WidgetBase) pageField, optionsIDs);
                                }
                            }
                        }
                    }
                    // 遍历选项源ID读取选项
                    if (optionsIDs.size() > 0) {
                        ArrayList<String> list = new ArrayList<>(optionsIDs);
                        // 赋值选项源
                        try {
                            //切换数据源（选项源以及数据字典在master）
                            DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
                            smartForm.setOptionsList(iSysDictService.getIds(list));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            DynamicDataSourceContextHolder.poll();
                        }
                    }
                }
            }
            //设置默认参数
            if (smartForm.getSkipendtimevalidation() == null || !smartForm.getSkipendtimevalidation().equals("1")) {
                smartForm.setSkipendtimevalidation("0");
            }
            smartForm.setFieldMapperJson(null);
            smartForm.setConditionRulesJson(null);
            smartForm.setPagesJson(null);
        }
        return smartForm;
    }


    @Override
    public SmartForm smartForm(String id, String pageId, boolean hasOptions) {
        SmartForm smartForm = loadSmartForm(id);
        if (smartForm != null) {
            if (hasOptions || true) {
                // 使用唯一Set进行选项源ID存储
                Set<String> optionsIDs = new HashSet<String>();
                if (smartForm.getPageList() != null && smartForm.getPageList().size() > 0) {
                    // pageId为空时获取第一页的
                    if (CheckDataUtil.isNull(pageId)) {
                        pageId = smartForm.getPageList().get(0).getId();
                    }
                    for (FormPage page : smartForm.getPageList()) {
                        if (!page.getId().equals(pageId)) {
                            continue;
                        }
                        if (page.getFieldList() != null && page.getFieldList().size() > 0) {
                            // 遍历分页中的字段获取选项源
                            for (FormFieldBase pageField : page.getFieldList()) {
                                if (pageField instanceof Group) {
                                    Group group = (Group) pageField;
                                    this.getGroupSelectOptions(group, optionsIDs);
                                } else {
                                    this.getSelectOptions((WidgetBase) pageField, optionsIDs);
                                }
                            }
                        }
                    }
                    // 遍历选项源ID读取选项
                    if (optionsIDs.size() > 0) {
                        ArrayList<String> list = new ArrayList<>(optionsIDs);
                        // 赋值选项源
                        try {
                            //切换数据源（选项源以及数据字典在master）
                            DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
                            smartForm.setOptionsList(iSysDictService.getIds(list));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            DynamicDataSourceContextHolder.poll();
                        }
                    }
                }
            }
            //设置默认参数
            if (smartForm.getSkipendtimevalidation() == null || !smartForm.getSkipendtimevalidation().equals("1")) {
                smartForm.setSkipendtimevalidation("0");
            }
            smartForm.setFieldMapperJson(null);
            smartForm.setConditionRulesJson(null);
            smartForm.setPagesJson(null);
            smartForm.setExtraSettingJson(null);
        }
        return smartForm;
    }

    @Override
    public SmartForm smartFormInfo(String id, boolean hasPage) {
        SmartForm smartForm = loadSmartForm(id);
        if (smartForm != null) {
            if (hasPage) {
                // 如果包含分页信息
                // 清理字段信息
                for (FormPage page : smartForm.getPageList()) {
                    page.setFieldList(null);
                }
            } else {
                smartForm.setPageList(null);
            }
            //清理其他信息
            smartForm.setFieldMapperJson(null);
            smartForm.setPagesJson(null);
            smartForm.setExtraSettingJson(null);
        }
        return smartForm;
    }

    @Override
    public List<FormPage> smartFormPageList(String id, String contentId, boolean loadAll) throws ParseException {

        SmartForm smartform = loadSmartForm(id);
        if (smartform != null && CheckDataUtil.isNotNull(smartform.getPageList())) {
            if (CheckDataUtil.isNotNull(contentId)) {
                List<FormPage> list = new ArrayList<FormPage>();
                // 如果contentid不为空，则加载数据
                if (loadAll) {
                    for (FormPage page : smartform.getPageList()) {
                        list.add(smartFormPage(id, page.getId(), contentId));
                    }
                    return list;
                } else {
                    // 组件状态
                    // 状态表名
                    String stateTable = this.getMapperTable(smartform.getFieldMapperList(), DBFieldMapperType.STATETABLE);
                    List<ComponentStateEntity> componentStates = null;
                    componentStates = this.getComponentState(stateTable, contentId);
                    // 读取分页状态
                    setPageState(smartform.getPageList(), componentStates);
                    for (FormPage page : smartform.getPageList()) {
                        page.setFieldList(null);
                    }
                    return smartform.getPageList();
                }
            } else {
                for (FormPage page : smartform.getPageList()) {
                    page.setFieldList(null);
                }
            }
            return smartform.getPageList();
        }
        return null;
    }

    @Override
    public FormPage smartFormPage(String formId, String pageId, String contentId) throws ParseException {
        SmartForm smartform = loadSmartForm(formId);
        if (smartform != null && CheckDataUtil.isNotNull(smartform.getPageList())) {
            FormPage formPage = null;
            if (CheckDataUtil.isNull(pageId)) {
                formPage = smartform.getPageList().get(0);
            } else {
                for (FormPage page : smartform.getPageList()) {
                    if (page.getId().equals(pageId)) {
                        formPage = page;
                        break;
                    }
                }
            }
            if (formPage == null) {
                throw new GlobalException(60402);
            }
            //加载选项源
            // 使用唯一Set进行选项源ID存储
            Set<String> optionsIDs = new HashSet<String>();
            if (formPage.getFieldList() != null && formPage.getFieldList().size() > 0) {
                // 遍历分页中的字段获取选项源
                for (FormFieldBase pageField : formPage.getFieldList()) {
                    if (pageField instanceof Group) {
                        Group group = (Group) pageField;
                        this.getGroupSelectOptions(group, optionsIDs);
                    } else {
                        this.getSelectOptions((WidgetBase) pageField, optionsIDs);
                    }
                }
            }
            // 遍历选项源ID读取选项
            if (optionsIDs.size() > 0) {
                ArrayList<String> list = new ArrayList<>(optionsIDs);
                // 赋值选项源
                try {
                    //切换数据源（选项源以及数据字典在master）
                    DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
                    formPage.setOptionsList(iSysDictService.getIds(list));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    DynamicDataSourceContextHolder.poll();
                }
            }
            if (CheckDataUtil.isNotNull(contentId)) {
                // 加载数据
                try {
                    DynamicDataSourceContextHolder.push(smartform.getDbName());
                    formPage = getPageContentById(smartform, formPage, contentId);
                } finally {
                    DynamicDataSourceContextHolder.poll();
                }
            }
            return formPage;
        }
        return null;
    }

    @Override
    public FormPage saveFormPage(String data, boolean isStorage) throws ParseException {
        FormPageContent content = JSON.parseObject(data, FormPageContent.class);
        if (content != null) {
            // 检测data中 formId、PageId、ContentId 是否存在
            this.checkImportInfo(content);
            String formId = content.getFormId();
            String pageId = content.getPageId();
            // 读取表单结构数据，检查合法性，识别具体存储分页以及组织
            SmartForm orgForm = this.smartForm(formId, pageId, false);
            if (orgForm == null) {
                throw new GlobalException(60403);
            }
            FormPage formPage = this.saveFormContentPage(content, orgForm, isStorage);

            return formPage;
        }
        return null;
    }

    @Override
    public GroupLine saveGroupLine(String data, boolean isStorage) throws ParseException {
        FormGroupLineContent content = JSON.parseObject(data, FormGroupLineContent.class);
        if (content != null) {
            // 检测data中 formId，等重要信息存在
            this.checkImportInfo(content);
            // 读取表单结构数据，检查合法性，识别具体存储分页以及组织
            SmartForm orgForm = smartForm(content.getFormId(), false);
            if (orgForm == null) {
                throw new GlobalException(60403);
            }
            GroupLine groupLine;
            try {
                DynamicDataSourceContextHolder.push(orgForm.getDbName());
                groupLine = null;
                try {
                    groupLine = this.executeSubmitGridLine(orgForm, content, isStorage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
            return groupLine;

        }
        return null;
    }

    @Override
    public void delGroupLine(String formId, String contentId, String groupId, String lineId, String pageId) {
        // 读取表单结构数据，检查合法性，识别具体存储分页以及组织
        SmartForm orgForm = smartForm(formId, false);
        if (orgForm == null) {
            throw new GlobalException(60403);
        }

        try {
            //切换数据源
            DynamicDataSourceContextHolder.push(orgForm.getDbName());
            this.executeDeleteGridLine(orgForm, contentId, groupId, lineId, pageId);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    @Override
    public void swapGroupLine(String formId, String contentId, String groupId, String lineId1, String lineId2, String pageId) {
        // 读取表单结构数据，检查合法性，识别具体存储分页以及组织
        SmartForm orgForm = smartForm(formId, false);
        if (orgForm == null) {
            throw new GlobalException(60403);
        }
        if (lineId1.equals(lineId2)) {
            // 行id相等，不做处理
            return;
        }
        this.executeSwapGroupLine(orgForm, contentId, groupId, lineId1, lineId2, pageId);
    }

    @Override
    public void updateGroupLineSort(String formId, String contentId, String groupId, String lineId, Integer sort, String pageId) {
        // 读取表单结构数据，检查合法性，识别具体存储分页以及组织
        SmartForm orgForm = smartForm(formId, false);
        if (orgForm == null) {
            throw new GlobalException(60403);
        }
        this.executeUpdateGroupLineSort(orgForm, contentId, groupId, lineId, sort, pageId);

    }

    @Override
    public void resetSortGroupLine(String formId, String contentId, String groupId, String pageId) {
        // 读取表单结构数据，检查合法性，识别具体存储分页以及组织
        SmartForm orgForm = smartForm(formId, false);
        if (orgForm == null) {
            throw new GlobalException(60403);
        }
        this.executeResetSortGroupLine(orgForm, contentId, groupId, pageId);
    }

    /**
     * @param id
     * @return SmartForm    返回类型
     * @Title: loadSmartForm
     * @Description: 读取表单
     */
    private SmartForm loadSmartForm(String id) {
        SmartForm smartForm = null;
        // 先从redis中获取
        if (dao.hasKey(id)) {
            logger.info("loadSmartForm:" + id + " from redis");
            smartForm = dao.get(id);
        } else {
            logger.info("loadSmartForm:" + id + " from zd_boot_smart_form.Form");
            smartForm = smartFormMapper.getObjectById(id);
            if (smartForm != null) {
                initAndCacheSmartForm(smartForm);
            }
        }
        return smartForm;
    }

    /**
     * @param smartForm 参数说明
     * @return void    返回类型
     * @Title: initAndCacheSmartForm
     * @Description: 初始化和缓存表单进redis
     */
    private void initAndCacheSmartForm(SmartForm smartForm) {
        String json = smartForm.getPagesJson();
        if (json != null && !json.equals("")) {
            List<FormPage> pageList = JSON.parseArray(json, FormPage.class);
            if (null == pageList) {
                return;
            }
            smartForm.setPageList(pageList);

        }
        smartForm.setPagesJson(null);
        // 填充FieldMapperList用于redis保存
        String fieldMapperJson = smartForm.getFieldMapperJson();
        if (fieldMapperJson != null && !fieldMapperJson.equals("")) {
            List<DBFieldMapper> fieldMappers = JSON.parseArray(fieldMapperJson, DBFieldMapper.class);
            smartForm.setFieldMapperList(fieldMappers);
        }
        smartForm.setFieldMapperJson(null);
        String ruleJson = smartForm.getConditionRulesJson();
        if (ruleJson != null && !ruleJson.equals("")) {
            List<ConditionalRule> ruleList = JSON.parseArray(ruleJson, ConditionalRule.class);
            smartForm.setConditionRules(ruleList);
        }
        smartForm.setConditionRulesJson(null);
        // 填充ConditionRules用于redis保存
        String extraJson = smartForm.getExtraSettingJson();
        if (extraJson != null && !extraJson.equals("")) {
            FormSettings extraSetting = JSON.parseObject(extraJson, FormSettings.class);
            smartForm.setExtraSetting(extraSetting);
        }
        //增加”跳过时效校验“ 默认0 不跳过校验 正常校验
        if (smartForm.getSkipendtimevalidation() != null) {
            String skipendtimevalidation = smartForm.getSkipendtimevalidation();
            if (skipendtimevalidation == "" || skipendtimevalidation.length() < 1) {
                skipendtimevalidation = "0";
            }
            smartForm.setSkipendtimevalidation(skipendtimevalidation);
        } else {
            smartForm.setSkipendtimevalidation("0");
        }
        smartForm.setExtraSettingJson(null);
        // 继续缓存入redis中
        dao.update(smartForm);
    }


    /**
     * @param smartForm 参数说明
     * @return void    返回类型
     * @Title: initAndCacheSmartForm
     * @Description: 初始化和缓存表单进redis
     */
    private SmartForm smartFormParseJson(SmartForm smartForm) {
        String json = smartForm.getPagesJson();
        if (json != null && !json.equals("")) {
            List<FormPage> pageList = JSON.parseArray(json, FormPage.class);
            smartForm.setPageList(pageList);
        }
        smartForm.setPagesJson(null);
        // 填充FieldMapperList用于redis保存
        String fieldMapperJson = smartForm.getFieldMapperJson();
        if (fieldMapperJson != null && !fieldMapperJson.equals("")) {
            List<DBFieldMapper> fieldMappers = JSON.parseArray(fieldMapperJson, DBFieldMapper.class);
            smartForm.setFieldMapperList(fieldMappers);
        }
        smartForm.setFieldMapperJson(null);
        String ruleJson = smartForm.getConditionRulesJson();
        if (ruleJson != null && !ruleJson.equals("")) {
            List<ConditionalRule> ruleList = JSON.parseArray(ruleJson, ConditionalRule.class);
            smartForm.setConditionRules(ruleList);
        }
        smartForm.setConditionRulesJson(null);
        // 填充ConditionRules用于redis保存
        String extraJson = smartForm.getExtraSettingJson();
        if (extraJson != null && !extraJson.equals("")) {
            FormSettings extraSetting = JSON.parseObject(extraJson, FormSettings.class);
            smartForm.setExtraSetting(extraSetting);
        }
        //增加”跳过时效校验“ 默认0 不跳过校验 正常校验
        if (smartForm.getSkipendtimevalidation() != null) {
            String skipendtimevalidation = smartForm.getSkipendtimevalidation();
            if (skipendtimevalidation == "" || skipendtimevalidation.length() < 1) {
                skipendtimevalidation = "0";
            }
            smartForm.setSkipendtimevalidation(skipendtimevalidation);
        } else {
            smartForm.setSkipendtimevalidation("0");
        }
        smartForm.setExtraSettingJson(null);
        // 继续缓存入redis中
        //mapper.update(smartForm);
        return smartForm;
    }

    /**
     * 执行提交gridView行
     *
     * @param orgForm
     * @param content
     * @param isStorage
     * @throws ParseException
     */
    @Transactional
    private GroupLine executeSubmitGridLine(SmartForm orgForm, FormGroupLineContent content, boolean isStorage) throws ParseException {
        String contentId = content.getContentId();
        String groupId = content.getGroupId();
        SmartFormContent oldData = null;
        try {
            oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
                    orgForm.getFieldMapperList());
        } catch (Exception e) {
            System.out.println("641 oldData e:" + e);
        }

        // 查询当前分页
        FormPage nowPage = null;
        for (FormPage page : orgForm.getPageList()) {
            if (page.getId().equals(content.getPageId())) {
                nowPage = page;
                break;
            }
        }
        if (nowPage == null) {
            throw new GlobalException(60402);
        }

        // 状态表名
        String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
        // 上传表名
        String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

        // 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
        Group group = this.getGridGroup(orgForm, groupId, content.getPageId());
        GroupLine line = group.getOriginalLine();
        if (CheckDataUtil.isNull(line.getFieldList())) {
            return line;
        }

        // 表格行保存暂不运行复杂规则
        // this.executeFormRule(contentId, orgForm, pageId, 1);

        // 判定状态数据
        boolean isCreated = false;
        // 组件状态
        List<ComponentStateEntity> componentStates = null;
        int lineTotal = 0;
        try {
            lineTotal = 1 + getGridLineNum(group, contentId, ContentStateType.All);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (oldData != null) {
            if (orgForm.getSkipendtimevalidation() == null || orgForm.getSkipendtimevalidation().length() < 1 || !orgForm.getSkipendtimevalidation().equals("1")) {
                // 判定填报截止日期
                this.ckFillEndDate(oldData);
            }
            // 编辑模式，进行状态判定，锁定状态无法编辑
            if (oldData.getState() != null && oldData.getState().intValue() == ContentStateType.LOCK.value) {
                throw new GlobalException(60404);
            }

            // 检测行数
            if (group.getFillAdd()) {
                // 默认最大10000行
                int max = group.getMaxAdd() == null ? 10000 : group.getMaxAdd();
                if (CheckDataUtil.isNull(content.getLineId()) && max != 0 && lineTotal > max) {
                    throw new GlobalException(60405);
                }
            }


            // 获取当前的组件状态
            componentStates = this.getComponentState(stateTable, oldData.getId());
            // 判定超级组件状态
            if (componentStates != null) {
                for (ComponentStateEntity state : componentStates) {
                    if (state.getTableName().equals(group.getTable())
                            && state.getWorkType() == group.getWorkType()) {
                        // 判定当前组件状态不可编辑
                        if (state.getState() == ContentStateType.LOCK.value) {
                            throw new GlobalException(60404);
                        }
                        break;
                    }
                }
            }
            oldData.setName(orgForm.getName());
            oldData.setCreatedAt(oldData.getCreatedAt());
            oldData.setModifiedAt(new Date().getTime());


        } else {
            // 添加模式
            isCreated = true;
            oldData = new SmartFormContent();
            oldData.setId(contentId);
            oldData.setName(orgForm.getName());
            oldData.setFormId(orgForm.getId());
            oldData.setCreatedAt(System.currentTimeMillis());
            oldData.setModifiedAt(System.currentTimeMillis());
            oldData.setState(ContentStateType.STORAGE.value);
        }
        initContent(content);

        // 获取复杂规则替换列表
        Map<String, ConditionalWidget> replaceWidgets = this.getConditionRuleWidget(nowPage, group.getId(), line, content);


        // 要发布到主表的额外字段
        Map<String, Object> mainContentMap = new HashMap<String, Object>();
        // 要存储的表格表
        FormContentTableEntity table = new FormContentTableEntity();
        table.setId(content.getLineId());
        table.setContentId(contentId);
        table.setWorkType(group.getWorkType());
        table.setLineNum(content.getLineNum());
        table.setCreatedAt(System.currentTimeMillis());
        table.setModifiedAt(System.currentTimeMillis());
        if (isStorage) {
            table.setState(ContentStateType.STORAGE.value);
        } else {
            table.setState(ContentStateType.SAVE.value);
        }

        // 要存储的上传列表
        List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList = new ArrayList<FormContentBaseDTO<List<FormContentUploadsEntity>>>();

        Query contentQuery = new Query();
        // 遍历内容中的字段数据，合计mysql需要存储的表与字段
        for (WidgetBase widgetBase : line.getFieldList()) {
            // 查找替换信息
            if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                widgetBase.setHide(conditionalWidget.getHide());
                widgetBase.setDisable(conditionalWidget.getDisable());
                if (conditionalWidget.getField() != null) {
                    widgetBase.setRules(conditionalWidget.getField().getRules());
                }
            }
            if (widgetBase.getHide() != null && widgetBase.getHide()) {
                // 隐藏组件不处理
                continue;
            }
            // 根据字段ID查询结构数据
            // WidgetBase orgWidget = getOriginalWidget(group, null, widget.getId());
            // 赋值widget
            FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId());
            fillWidget(widgetBase, formWidgetContent);
            // 如果是保存验证规则
            if (!isStorage) {
                checkWidget(widgetBase, formWidgetContent, content.getWidgets());
            }
            this.handleField((WidgetBase) widgetBase, widgetBase, contentQuery, uploadList, content.getLineId(), group,
                    contentId, null, false);

        }
        // 检测字段中是否存在sort
        if (contentQuery.containsKey("sort")) {
            int sort = contentQuery.getAsInt("sort");
            if (sort != 0) {
                table.setSort(sort);
            } else {
                // 如果是0，则使用传递数据
                if (content.getSort() != null) {
                    // 排序字段储蓄
                    table.setSort(content.getSort());
                } else {
                    table.setSort(0);
                }
            }
            contentQuery.remove("sort");
        } else if (content.getSort() != null) {
            // 排序字段储蓄
            table.setSort(content.getSort());
        }
        // 存储表格表
        try {
            int asd = tableDao.saveContent(table, group.getTable(), contentQuery);
        } catch (Exception e) {
            System.out.println("tableDao.saveContent e:" + e);
        }
        // 存储上传表
        if (uploadList.size() > 0) {
            for (FormContentBaseDTO<List<FormContentUploadsEntity>> uploadData : uploadList) {
                // 删除已添加的数据
                if (!isCreated)
                    uploadsDao.removeContent(uploadTable, oldData.getId(), uploadData.getDbTable(),
                            uploadData.getWorkType(), content.getLineId(), null);
                // 删除第一个上传数据
                uploadData.getData().remove(0);
                // 批量一个表的上传数据
                if (uploadList.size() > 0) {
                    if (uploadData.getData().size() > 0) {

                        uploadsDao.batchSave(uploadData.getData(), uploadTable);
                    }
                }
            }
        }

        // 处理组件状态
        ComponentStateEntity comState = new ComponentStateEntity();
        comState.setId(UUIDUtil.getNextId());
        comState.setDbTable(stateTable);
        comState.setContentId(oldData.getId());
        comState.setTableName(group.getTable());
        comState.setWorkType(group.getWorkType());
        comState.setCreatedAt(System.currentTimeMillis());
        comState.setModifiedAt(System.currentTimeMillis());
        if (!isStorage) {
            // 检测已完成的行是否达标
            int min = group.getMinAdd() == null ? 0 : group.getMinAdd();
            if (min != 0 && lineTotal < min) {
                comState.setState(ContentStateType.STORAGE.value);
            } else {
                // 检测是否还存在未完成的行
                if (getGridLineNum(group, contentId, ContentStateType.STORAGE) > 0) {
                    comState.setState(ContentStateType.STORAGE.value);
                } else {
                    comState.setState(ContentStateType.SAVE.value);
                }
            }
        } else {
            comState.setState(ContentStateType.STORAGE.value);
        }

        this.componentStateDao.saveContent(comState, stateTable);

        // 设置主表状态
        if (comState.getState() == ContentStateType.SAVE.value) {
            // 当前已存的组件状态
            // 获取当前的组件状态
            componentStates = this.getComponentState(stateTable, oldData.getId());
            // 读取分页状态
            setPageState(orgForm.getPageList(), componentStates);
            oldData.setState(ContentStateType.SAVE.value);
            for (FormPage fp : orgForm.getPageList()) {
                if (fp.getFillState() != ContentStateType.SAVE.value) {
                    oldData.setState(ContentStateType.STORAGE.value);
                    break;
                }
            }
        } else {
            oldData.setState(ContentStateType.STORAGE.value);
        }

        Query mainContent = null;
        if (isCreated) {
            // 处理固定表单固定额外参数
            handleParamExtraData(contentId, orgForm.getFieldMapperList(), mainContentMap);
            if (mainContentMap.size() > 0)
                mainContent = new Query(mainContentMap);
            contentMainDao.saveContent(oldData, orgForm.getTableName(), mainContent);
        } else {
            if (mainContentMap.size() > 0)
                mainContent = new Query(mainContentMap);
            try {
                //切换选项源
                DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
                contentMainDao.updateContent(oldData, orgForm.getTableName(), mainContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //this.executeFormRule(contentId, orgForm, null, 2);

        // 重新获取状态进行设置
//		componentStates = this.getComponentState(stateTable, oldData.getId());
//		this.setPageState(orgForm.getPageList(), componentStates);
//		this.handleNowPage(orgForm.getPageList(), null, true);
        // 将最新的分页状态返回给前端
        oldData.setPageList(orgForm.getPageList());

        // 赋值要返回的数据
        line.setId(content.getLineId());
        line.setLineNum(content.getLineNum());
        line.setState(table.getState());

        return line;
    }

    /**
     * @param group
     * @return int    返回类型
     * @Title: getGridLineNum
     * @Description: 获取gridview填报行数
     */
    private int getGridLineNum(Group group, String contentId, ContentStateType state) {
        List<String> fixedId = null;
        if (group.getFixedLineList() != null && group.getFixedLineList().size() > 0) {
            fixedId = new ArrayList<String>();
            // 遍历固定行
            for (GroupLine line : group.getFixedLineList()) {
                if (line.getId() != null) {
                    fixedId.add(line.getId());
                }
            }
            if (fixedId.size() == 0)
                fixedId = null;
        }
        List<String> data = null;
        try {
            data = tableDao.countTotals(group.getTable(), contentId, group.getWorkType(), fixedId, state.value);
        } catch (Exception e) {
            System.out.println("e:" + e);
        }

        if (state == ContentStateType.All) {
            return Integer.parseInt(data.get(0));
        }
        return Integer.parseInt(data.get(1));
    }


    private void executeResetSortGroupLine(SmartForm orgForm, String contentId, String groupId, String pageId) {
        // 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
        SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
                orgForm.getFieldMapperList());

        // 获取当前退回的组件状态
        //List<ComponentStateEntity> componentStates = null;
        if (oldData != null) {
            if (orgForm.getSkipendtimevalidation() == null || orgForm.getSkipendtimevalidation().length() < 1 || !orgForm.getSkipendtimevalidation().equals("1")) {
                // 判定填报截止日期
                this.ckFillEndDate(oldData);
            }
            // 获取当前退回的组件状态
            //componentStates = this.getComponentState(stateTable, oldData.getId());
            // 编辑模式，进行状态判定，锁定状态无法提交
            if (oldData.getState() != null && oldData.getState().intValue() == ContentStateType.LOCK.value) {
                throw new GlobalException(60404);
            }

            // 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
            Group group = this.getGridGroup(orgForm, groupId, pageId);

            List<Map<String, Object>> dataMaps = tableDao.getContentList(group.getTable(), contentId,
                    group.getWorkType(), null);
            //数量不够不做排序
            if (CheckDataUtil.isNull(dataMaps) || dataMaps.size() == 1)
                return;
            int sort = 1;
            for (Map<String, Object> dataMap : dataMaps) {
                // 固定行无法排序
                if (dataMap.containsKey("fixed_id") && !dataMap.get("fixed_id").toString().equals("")) {
                    continue;
                }
                FormContentTableEntity table = new FormContentTableEntity();
                table.setSort(sort++);
                table.setContentId(contentId);
                table.setId(String.valueOf(dataMap.get("id")));
                tableDao.updateContentSort(table, group.getTable());
            }
        }
    }

    private void executeSwapGroupLine(SmartForm orgForm, String contentId, String groupId, String lineId1, String lineId2, String pageId) {
        // 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
        SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
                orgForm.getFieldMapperList());

        if (oldData != null) {
            if (orgForm.getSkipendtimevalidation() == null || orgForm.getSkipendtimevalidation().length() < 1 || !orgForm.getSkipendtimevalidation().equals("1")) {
                // 判定填报截止日期
                this.ckFillEndDate(oldData);
            }
            // 获取当前退回的组件状态
            //componentStates = this.getComponentState(stateTable, oldData.getId());
            // 编辑模式，进行状态判定，锁定状态无法提交
            if (oldData.getState() != null && oldData.getState().intValue() == ContentStateType.LOCK.value) {
                throw new GlobalException(60404);
            }
            // 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
            Group group = this.getGridGroup(orgForm, groupId, pageId);
            // 查询行1
            Map<String, Object> dataMaps1 = loadGroupLine(group.getTable(), oldData.getId(), group.getWorkType(),
                    lineId1, null);
            if (dataMaps1.containsKey("fixed_id")) {
                if (!dataMaps1.get("fixed_id").toString().equals("")) {
                    throw new GlobalException(60413);
                }
            }
            // 查询行2
            Map<String, Object> dataMaps2 = loadGroupLine(group.getTable(), oldData.getId(), group.getWorkType(),
                    lineId2, null);
            if (dataMaps2.containsKey("fixed_id")) {
                if (!dataMaps2.get("fixed_id").toString().equals("")) {
                    throw new GlobalException(60413);
                }
            }
            int sort1 = Integer.valueOf(String.valueOf(dataMaps1.get("sort")));
            int sort2 = Integer.valueOf(String.valueOf(dataMaps2.get("sort")));
            if (sort1 == sort2) {
                //排序相等不做处理
                return;
            }
            // 调整排序
            FormContentTableEntity table1 = new FormContentTableEntity();
            table1.setSort(sort2);
            table1.setContentId(contentId);
            table1.setId(lineId1);
            tableDao.updateContentSort(table1, group.getTable());
            FormContentTableEntity table2 = new FormContentTableEntity();
            table2.setSort(sort1);
            table2.setContentId(contentId);
            table2.setId(lineId2);
            tableDao.updateContentSort(table2, group.getTable());

        }
    }

    private void executeUpdateGroupLineSort(SmartForm orgForm, String contentId, String groupId, String lineId,
                                            Integer sort, String pageId) {

        // 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
        SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
                orgForm.getFieldMapperList());

        if (oldData != null) {
            if (orgForm.getSkipendtimevalidation() == null || orgForm.getSkipendtimevalidation().length() < 1 || !orgForm.getSkipendtimevalidation().equals("1")) {
                // 判定填报截止日期
                this.ckFillEndDate(oldData);
            }
            // 获取当前退回的组件状态
            //componentStates = this.getComponentState(stateTable, oldData.getId());
            // 编辑模式，进行状态判定，锁定状态无法提交
            if (oldData.getState() != null && oldData.getState().intValue() == ContentStateType.LOCK.value) {
                throw new GlobalException(60404);
            }
            // 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
            Group group = this.getGridGroup(orgForm, groupId, pageId);
            // 查询行1
            Map<String, Object> dataMaps = loadGroupLine(group.getTable(), oldData.getId(), group.getWorkType(),
                    lineId, null);
            if (dataMaps.containsKey("fixed_id")) {
                if (!dataMaps.get("fixed_id").toString().equals("")) {
                    throw new GlobalException(60413);
                }
            }
            // 调整排序
            FormContentTableEntity table = new FormContentTableEntity();
            table.setSort(sort);
            table.setContentId(contentId);
            table.setId(lineId);
            tableDao.updateContentSort(table, group.getTable());
        }

    }


    /**
     * @param table
     * @param contentId
     * @param workType
     * @param lineId
     * @param columns
     * @return Map<String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Object>    返回类型
     * @Title: loadGroupLine
     * @Description: 查询行
     */
    private Map<String, Object> loadGroupLine(String table, String contentId, Integer workType, String lineId, List<String> columns) {
        Map<String, Object> dataMaps = tableDao.getContent(table, contentId, workType,
                lineId, null);
        if (dataMaps == null) {
            throw new GlobalException(60414);
        }
        return dataMaps;
    }

    /**
     * 执行删除gridView行
     *
     * @param orgForm
     * @param contentId
     * @param groupId
     * @param lineId
     * @throws ParseException
     */
    @Transactional
    private SmartFormContent executeDeleteGridLine(SmartForm orgForm, String contentId, String groupId, String lineId, String pageId) {

        // 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
        SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
                orgForm.getFieldMapperList());

        // 状态表名
        String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
        // 上传表名
        String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

        // 获取当前退回的组件状态
        List<ComponentStateEntity> componentStates = null;
        if (oldData != null) {
            if (orgForm.getSkipendtimevalidation() == null || orgForm.getSkipendtimevalidation().length() < 1 || !orgForm.getSkipendtimevalidation().equals("1")) {
                // 判定填报截止日期
                this.ckFillEndDate(oldData);
            }
            // 获取当前退回的组件状态
            componentStates = this.getComponentState(stateTable, oldData.getId());
            // 编辑模式，进行状态判定，锁定状态无法提交
            if (oldData.getState() != null && oldData.getState().intValue() == ContentStateType.LOCK.value) {
                throw new GlobalException(60404);
            }

            // 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
            Group group = this.getGridGroup(orgForm, groupId, pageId);

            oldData = new SmartFormContent();
            oldData.setId(contentId);
            oldData.setName(orgForm.getName());
            oldData.setFormId(orgForm.getId());
            oldData.setCreatedAt(System.currentTimeMillis());
            oldData.setModifiedAt(System.currentTimeMillis());
            oldData.setState(ContentStateType.STORAGE.value);

            // 查询行
            Map<String, Object> dataMaps = tableDao.getContent(group.getTable(), oldData.getId(), group.getWorkType(),
                    lineId, null);
            if (dataMaps == null) {
                throw new GlobalException(60414);
                //throw new SmartFormError("此行已删除");
            }
            if (dataMaps.containsKey("fixed_id")) {
                if (!dataMaps.get("fixed_id").toString().equals("")) {
                    throw new GlobalException(60413);
                    //throw new SmartFormError("此行为固定行无法删除");
                }
            }
            // 删除行
            tableDao.removeContent(group.getTable(), oldData.getId(), group.getWorkType(), lineId);
            Query query = new Query();
            query.put("dbTable", group.getTable());
            query.put("contentId", oldData.getId());
            query.put("workType", group.getWorkType());
            int count = tableDao.countTotal(query);
            // 存储组件状态
            ComponentStateEntity comState = new ComponentStateEntity();
            comState.setId(UUIDUtil.getNextId());
            comState.setDbTable(stateTable);
            comState.setContentId(oldData.getId());
            comState.setTableName(group.getTable());
            comState.setWorkType(group.getWorkType());
            comState.setCreatedAt(System.currentTimeMillis());
            comState.setModifiedAt(System.currentTimeMillis());

            // 删除已添加的上传表数据
            uploadsDao.removeContent(uploadTable, oldData.getId(), group.getTable(), group.getWorkType(), lineId, null);

            // 删光数据改为暂存状态
            if (count == 0)
                comState.setState(ContentStateType.STORAGE.value);
            else {
                comState.setState(ContentStateType.SAVE.value);
            }
            this.componentStateDao.saveContent(comState, stateTable);
//			// 处理表单回退
//			List<FormContentBaseDTO<Group>> stateBackGroups = this.getStateBack(group, orgForm, componentStates);
//			// 执行表单回退
//			for (FormContentBaseDTO<Group> dto : stateBackGroups) {
//				ComponentStateEntity comStateBack = new ComponentStateEntity();
//				comStateBack.setId(UUIDUtil.getNextId());
//				comStateBack.setDbTable(stateTable);
//				comStateBack.setContentId(oldData.getId());
//				comStateBack.setTableName(dto.getDbTable());
//				comStateBack.setWorkType(dto.getWorkType());
//				comStateBack.setCreatedAt(System.currentTimeMillis());
//				comStateBack.setModifiedAt(System.currentTimeMillis());
//				comStateBack.setState(ContentStateType.STORAGE.value);
//				this.componentStateDao.saveContent(comStateBack, stateTable);
//			}
            // 重新获取状态进行设置
//			componentStates = this.getComponentState(stateTable, oldData.getId());
//			this.setPageState(orgForm.getPageList(), componentStates);
//			this.handleNowPage(orgForm.getPageList(), null, true);

            // 设置主表状态
            if (oldData.getState() == ContentStateType.SAVE.value) {
                // 当前已存的组件状态
                // 获取当前的组件状态
                componentStates = this.getComponentState(stateTable, oldData.getId());
                // 读取分页状态
                setPageState(orgForm.getPageList(), componentStates);
                oldData.setState(ContentStateType.SAVE.value);
                for (FormPage fp : orgForm.getPageList()) {
                    if (fp.getFillState() != ContentStateType.SAVE.value) {
                        oldData.setState(ContentStateType.STORAGE.value);
                        break;
                    }
                }
                // 存储主表mysql数据
                contentMainDao.updateContent(oldData, orgForm.getTableName(), null);

            }

            // 将最新的分页状态返回给前端
            oldData.setPageList(orgForm.getPageList());
            oldData.setName(orgForm.getName());
        }
        return oldData;
    }

    /**
     * 读取当前分页状态
     *
     * @param pageList
     * @param componentStates
     */
    private void setPageState(List<FormPage> pageList, List<ComponentStateEntity> componentStates) {
        for (FormPage page : pageList) {
            setPageState(page, componentStates);
        }
    }

    private void setPageState(FormPage page, List<ComponentStateEntity> componentStates) {
        // 存储组件列表状态
        Set<Integer> stateSet = new HashSet<Integer>();
        if (page.getFieldList() != null) {
            for (FormFieldBase field : page.getFieldList()) {
                if (field instanceof Group) {
                    Group group = (Group) field;
                    if (group.getGroupType() == GroupType.SUPER.value) {
                        // 遍历超级组件
                        List<FormFieldBase> groupFields = group.getFieldList();
                        boolean hasField = false;
                        if (groupFields != null) {
                            for (FormFieldBase gfield : groupFields) {
                                if (gfield instanceof Group) {
                                    Group tableGroup = (Group) gfield;
                                    // 遍历非样式组状态
                                    if (tableGroup.getGroupType() != GroupType.STYLE.value) {
                                        // 如果组件隐藏，则认为已经填报
                                        if (tableGroup.getHide() != null && tableGroup.getHide()) {
                                            stateSet.add(ContentStateType.SAVE.value);
                                            tableGroup.setState(ContentStateType.SAVE.value);
                                        } else {
                                            // 查找组件状态
                                            ComponentStateEntity state = this.getGroupState(tableGroup,
                                                    componentStates);
                                            if (state != null) {
                                                stateSet.add(state.getState());
                                                tableGroup.setState(state.getState());
                                            } else {
                                                stateSet.add(ContentStateType.UNFILL.value);
                                                tableGroup.setState(ContentStateType.UNFILL.value);
                                            }
                                        }
                                    } else
                                        hasField = true;
                                } else
                                    hasField = true;
                            }
                        }
                        if (hasField) {
                            ComponentStateEntity state = this.getGroupState(group, componentStates);
                            if (state != null) {
                                stateSet.add(state.getState());
                            } else
                                stateSet.add(ContentStateType.UNFILL.value);
                        }
                    }
                }
            }
        }
        // 额外判断一下状态表中page字段
        for (ComponentStateEntity state : componentStates) {
            if (page.getId().equals(state.getPageId())) {
                stateSet.add(state.getState());
                break;
            }
        }
        page.setFillState(ContentStateType.UNFILL.value);
        // 判定当前分页状态
        for (Integer state : stateSet) {
            if (state == ContentStateType.SAVE.value) {// || state == ContentStateType.REFUSESUBMIT.value) {
                page.setFillState(state);
            } else {
                page.setFillState(state);
                break;
            }
        }
    }


    /**
     * 查找组的状态
     *
     * @param group
     * @param componentStates
     * @return
     */
    private ComponentStateEntity getGroupState(Group group, List<ComponentStateEntity> componentStates) {
        if (componentStates != null && componentStates.size() > 0) {
            for (ComponentStateEntity state : componentStates) {
                if (state.getTableName().equals(group.getTable()) && state.getWorkType() == group.getWorkType()) {
                    return state;
                }
            }
        }
        return null;
    }

    /**
     * 查询表单中的GridView组
     *
     * @param origForm
     * @param groupId
     * @return
     */
    private Group getGridGroup(SmartForm origForm, String groupId, String pageId) {
        if (origForm != null && origForm.getPageList() != null) {
            for (FormPage page : origForm.getPageList()) {
                if (pageId != null) {
                    if (!pageId.equals(page.getId())) {
                        continue;
                    }
                }
                if (page.getFieldList() != null && page.getFieldList().size() > 0) {
                    for (FormFieldBase pageField : page.getFieldList()) {
                        if (pageField instanceof Group) {
                            Group group = (Group) pageField;
                            if (group.getGroupType() == GroupType.SUPER.value) {
                                // 遍历超级组件旗下是否还有表格组
                                List<FormFieldBase> groupFields = group.getFieldList();
                                if (groupFields != null) {
                                    for (FormFieldBase gfield : groupFields) {
                                        if (gfield instanceof Group) {
                                            group = (Group) gfield;
                                            if (group.getGroupType() == GroupType.TABLE.value) {
                                                // GridView模式表格
                                                if (group.hasGridView() && group.getId().equals(groupId)) {
                                                    return group;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param content
     * @param orgForm
     * @param isStorage
     * @return FormPage    返回类型
     * @throws ParseException 参数说明
     * @Title: saveFormContentPage
     * @Description: 保存分页填报内容
     */
    @Transactional
    private FormPage saveFormContentPage(FormPageContent content, SmartForm orgForm, boolean isStorage) throws ParseException {
        String pageId = content.getPageId();
        String contentId = content.getContentId();
        // 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
        //获取project_main表中的一些字段
        SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
                orgForm.getFieldMapperList());
        // 状态表名
        String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
        // 上传表名
        String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

        FormPage nowPage = null;
        // 从表格结构中获得原始数据当前分页
        List<FormPage> pageList = orgForm.getPageList();
        for (FormPage fpage : pageList) {
            if (fpage.getId().equals(pageId)) {
                nowPage = fpage;
                break;
            }
        }

        if (nowPage == null) {
            throw new GlobalException(60402);
        }

        //在content结构中挂一个widgets(map),将widgetList中的数据填充进去fieldId为key
        initContent(content);
        // 获取复杂规则替换列表
        Map<String, ConditionalWidget> replaceWidgets = this.getConditionRuleWidget(nowPage, content);

        // 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
        List<FormContentBaseDTO<Group>> components = this.getPageComponents(nowPage, replaceWidgets, false);

        // 当前已存的组件状态
        List<ComponentStateEntity> componentStates = null;

        // 判定状态数据
        boolean isCreated = false;
        if (oldData != null) {
            //判定是否跳过时效校验  1 跳过校验，0 或 null 正常校验
            logger.info("Skipendtimevalidation：" + orgForm.getSkipendtimevalidation());
            if (orgForm.getSkipendtimevalidation() == null || orgForm.getSkipendtimevalidation().length() < 1 || !orgForm.getSkipendtimevalidation().equals("1")) {
                // 判定填报截止日期
                this.ckFillEndDate(oldData);
            }

            // 编辑模式，进行状态判定，锁定状态无法提交
            if (oldData.getState() != null && oldData.getState().intValue() == ContentStateType.LOCK.value) {
                throw new GlobalException(60404);
            }
            // 获取当前退回的组件状态
            componentStates = this.getComponentState(stateTable, oldData.getId());

            // 移除已经锁定的组件
            if (CheckDataUtil.isNotNull(components) && CheckDataUtil.isNotNull(componentStates)) {
                for (int i = components.size() - 1; i >= 0; i--) {
                    FormContentBaseDTO<Group> component = components.get(i);
                    // 遍历组件状态
                    for (ComponentStateEntity componentState : componentStates) {
                        if (componentState.getTableName().equals(component.getDbTable())
                                && componentState.getWorkType() == component.getWorkType()) {
                            // 判定当前组件状态不可编辑
                            if (componentState.getState() == ContentStateType.LOCK.value) {
                                // 移除不可编辑的组件
                                components.remove(component);
                            }
                            break;
                        }
                    }
                }
            }

            oldData.setName(orgForm.getName());
            oldData.setModifiedAt(new Date().getTime());
        } else {
            if (CheckDataUtil.isNull(contentId)) {
                contentId = UUIDUtil.getNextId();
            }
            // 添加模式
            isCreated = true;
            oldData = new SmartFormContent();
            oldData.setId(contentId);
            oldData.setName(orgForm.getName());
            oldData.setCreatedAt(System.currentTimeMillis());
            oldData.setModifiedAt(System.currentTimeMillis());
            oldData.setState(ContentStateType.STORAGE.value);
        }

        // 该分页没有需要存储的组件，直接返回
        if (components.size() == 0) {
            throw new GlobalException(60411);
        }

        // 要发布到主表的额外字段
        Map<String, Object> mainContentMap = new HashMap<String, Object>();
        // 要存储的组件表   添加的表单列表数据存储到对应的表
        List<FormContentBaseDTO<FormContentComponentEntity>> componentList = new ArrayList<FormContentBaseDTO<FormContentComponentEntity>>();
        // 要存储的表格表
        List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList = new ArrayList<FormContentBaseDTO<List<FormContentTableEntity>>>();
        // 要存储的上传列表
        List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList = new ArrayList<FormContentBaseDTO<List<FormContentUploadsEntity>>>();

        // 遍历内容中的字段数据，合计mysql需要存储的表与字段
        // 并根据是否暂存检测必填字段存储
        handleDBData(orgForm, content, nowPage, replaceWidgets, components, mainContentMap, componentList, tableList, uploadList,
                isStorage);

        // 执行公式检测
        checkFormulaRule(nowPage, content);

        // 存储组件表
        if (componentList.size() > 0) {
            for (FormContentBaseDTO<FormContentComponentEntity> componentData : componentList) {
                if (componentData.getContent() != null && componentData.getContent().size() > 0) {
                    try {
                        DynamicDataSourceContextHolder.push(orgForm.getDbName());
                        componentDao.saveContent(componentData.getData(), componentData.getDbTable(),
                                componentData.getContent());
                    } finally {
                        DynamicDataSourceContextHolder.poll();
                    }
                }
            }
        }
        // 存储表格表
        if (tableList.size() > 0) {
            for (FormContentBaseDTO<List<FormContentTableEntity>> tableData : tableList) {
                // 删除已添加的数据
                if (!isCreated)
                    tableDao.removeContent(tableData.getDbTable(), contentId, tableData.getWorkType(), null);
                // 批量添加表格数据
                tableDao.batchSave(tableData.getData(), tableData.getDbTable());
            }
        }
        // 存储上传表
        if (uploadList.size() > 0) {
            for (FormContentBaseDTO<List<FormContentUploadsEntity>> uploadData : uploadList) {
                // 删除已添加的数据
                if (!isCreated)
                    uploadsDao.removeContent(uploadTable, contentId, uploadData.getDbTable(),
                            uploadData.getWorkType(), null, null);
                // 清除第一个非上传文件数据
                uploadData.getData().remove(0);
                if (uploadData.getData().size() > 0) {
                    // 批量一个表的上传数据
                    uploadsDao.batchSave(uploadData.getData(), uploadTable);
                }
            }
        }
        // 存储组件状态
        if (components.size() > 0 && stateTable != null) {
            for (FormContentBaseDTO<Group> component : components) {
                // 处理数据库
                // String dbName = component.getDbName();
                ComponentStateEntity comState = new ComponentStateEntity();
                comState.setId(UUIDUtil.getNextId());
                comState.setDbTable(stateTable);
                comState.setContentId(contentId);
                comState.setTableName(component.getDbTable());
                comState.setWorkType(component.getWorkType());
                comState.setCreatedAt(System.currentTimeMillis());
                comState.setModifiedAt(System.currentTimeMillis());
                if (isStorage) {
                    comState.setState(ContentStateType.STORAGE.value);
                } else {
                    comState.setState(ContentStateType.SAVE.value);
                }
                try {
                    DynamicDataSourceContextHolder.push(orgForm.getDbName());
                    this.componentStateDao.saveContent(comState, stateTable);
                } finally {
                    DynamicDataSourceContextHolder.poll();
                }
            }
        }
        nowPage.setFillState(isStorage ? ContentStateType.STORAGE.value : ContentStateType.SAVE.value);
        nowPage.setModifiedAt(new Date());

        Query mainContent = null;
        // 设置主表状态
        if (!isStorage) {
            // 当前已存的组件状态
            // 获取当前的组件状态
            try {
                DynamicDataSourceContextHolder.push(orgForm.getDbName());
                componentStates = this.getComponentState(stateTable, oldData.getId());
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
            // 读取分页状态
            setPageState(orgForm.getPageList(), componentStates);
            oldData.setState(ContentStateType.SAVE.value);
            for (FormPage fp : orgForm.getPageList()) {
                if (fp.getFillState() != ContentStateType.SAVE.value) {
                    oldData.setState(ContentStateType.STORAGE.value);
                    break;
                }
            }
        } else {
            oldData.setState(ContentStateType.STORAGE.value);
        }
        // 存储主表mysql数据
        try {
            DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
            if (isCreated) {
                // 处理固定表单固定额外参数
                handleParamExtraData(contentId, orgForm.getFieldMapperList(), mainContentMap);
                if (mainContentMap.size() > 0)
                    mainContent = new Query(mainContentMap);
                contentMainDao.saveContent(oldData, orgForm.getTableName(), mainContent);

            } else {
                if (mainContentMap.size() > 0)
                    mainContent = new Query(mainContentMap);
                contentMainDao.updateContent(oldData, orgForm.getTableName(), mainContent);

            }
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        return nowPage;
    }

    /**
     * @param page
     * @param content 参数说明
     * @return void    返回类型
     * @Title: checkFormulaRule
     * @Description: 验证分页公式规则
     */
    private void checkFormulaRule(FormPage page, FormPageContent content) {
        // 遍历分页中的超级组件
        for (FormFieldBase field : page.getFieldList()) {
            if (field.getType() == 2)    //组判断
            {
                Group group = (Group) field;
                if (group.getGroupType() == 1)    //超级组件判断
                {
                    // 遍历公式列表
                    if (CheckDataUtil.isNotNull(group.getFormulaRules())) {
                        for (Formula formulaRule : group.getFormulaRules()) {
                            if (CheckDataUtil.isNotNull(formulaRule.getOperatorList())) {
                                if (!checkOperator(formulaRule.getOperatorList(), content.getWidgets())) {
                                    throw new GlobalException(60410);
                                    //throw new SmartFormError("公式规则验证不通过");
                                }
                            }

                        }

                    }
                }
            }
        }

    }

    /**
     * @param page
     * @param content 参数说明
     * @return void    返回类型
     * @Title: getConditionRuleWidget
     * @Description: 检测复杂规则，获取符合条件的替换新规则的控件列表信息
     */
    private Map<String, ConditionalWidget> getConditionRuleWidget(FormPage page, FormPageContent content) {

        Map<String, ConditionalWidget> widgets = new HashMap<String, ConditionalWidget>();
        if (content == null) {
            return widgets;
        }
        // 遍历分页中的超级组件
        for (FormFieldBase field : page.getFieldList()) {
            if (field.getType() == 2)    //组判断
            {
                Group group = (Group) field;
                if (group.getGroupType() == 1)    //超级组件判断
                {
                    // 遍历条件复杂规则
                    if (CheckDataUtil.isNotNull(group.getConditionRules())) {
                        for (ConditionalRule conditionalRule : group.getConditionRules()) {
                            // 先判断要处理的组件列表是否为空
                            if (CheckDataUtil.isNull(conditionalRule.getWidgetList())) {
                                continue;
                            }
                            // 先验证条件，条件值为空时，默认为通过
                            if (CheckDataUtil.isNotNull(conditionalRule.getCondition())) {
                                FormWidgetContent formWidgetContent = findWidgetContent(content, conditionalRule.getFieldId(), conditionalRule.getLineNum());
                                if (formWidgetContent == null) {
                                    // 数据为空，默认条件不通过
                                    continue;
                                }
                                //进行验证条件
                                if (!checkCondition(OperatorType.valueOf(conditionalRule.getRelationOperator()), String.valueOf(formWidgetContent.getValue()), conditionalRule.getCondition())) {
                                    continue;
                                }
                            }
                            // 替换规则
                            for (ConditionalWidget conditionalWidget : conditionalRule.getWidgetList()) {
                                if (conditionalWidget.getField() != null) {
                                    widgets.put(conditionalWidget.getField().getId(), conditionalWidget);
                                } else {
                                    widgets.put(conditionalWidget.getGroupId(), conditionalWidget);
                                }

                            }
                        }

                    }
                }
            }
        }
        return widgets;
    }

    /**
     * @param content
     * @return Map<String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ConditionalWidget>    返回类型
     * @Title: getConditionRuleWidget
     * @Description: 检测复杂规则，获取符合条件的替换新规则的控件列表信息
     */
    private Map<String, ConditionalWidget> getConditionRuleWidget(FormPage page, String groupId, GroupLine line, FormGroupLineContent content) {
        Map<String, ConditionalWidget> widgets = new HashMap<String, ConditionalWidget>();
        if (content == null) {
            return widgets;
        }
        // 遍历分页中的超级组件
        for (FormFieldBase field : page.getFieldList()) {
            if (field.getType() == 2)    //组判断
            {
                Group group = (Group) field;
                if (group.getGroupType() == 1)    //超级组件判断
                {
                    // 先判断是否是该超级组件的内容
                    for (FormFieldBase widget : group.getFieldList()) {
                        if (widget instanceof Group) {
                            Group tabGroup = (Group) widget;
                            if (tabGroup.getId().equals(groupId)) {
                                // 遍历条件复杂规则
                                if (CheckDataUtil.isNotNull(group.getConditionRules())) {
                                    for (ConditionalRule conditionalRule : group.getConditionRules()) {
                                        // 先判断要处理的组件列表是否为空
                                        if (CheckDataUtil.isNull(conditionalRule.getWidgetList())) {
                                            continue;
                                        }
                                        // 先验证条件，条件值为空时，默认为通过
                                        if (CheckDataUtil.isNotNull(conditionalRule.getCondition())) {
                                            FormWidgetContent formWidgetContent = findWidgetContent(content, conditionalRule.getFieldId());
                                            if (formWidgetContent == null) {
                                                // 数据为空，默认条件不通过
                                                continue;
                                            }
                                            //进行验证条件
                                            if (!checkCondition(OperatorType.valueOf(conditionalRule.getRelationOperator()), String.valueOf(formWidgetContent.getValue()), conditionalRule.getCondition())) {
                                                continue;
                                            }
                                        }
                                        // 替换规则
                                        for (ConditionalWidget conditionalWidget : conditionalRule.getWidgetList()) {
                                            if (conditionalWidget.getField() != null) {
                                                widgets.put(conditionalWidget.getField().getId(), conditionalWidget);
                                            } else {
                                                widgets.put(conditionalWidget.getGroupId(), conditionalWidget);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return widgets;
    }

    /**
     * @param page
     * @param content 参数说明
     * @return void    返回类型
     * @Title: checkPageRule
     * @Description: 复杂条件规则验证
     */
    private void checkPageRule(FormPage page, FormPageContent content) {
        // 遍历分页中的超级组件
        for (FormFieldBase field : page.getFieldList()) {
            if (field.getType() == 2)    //组判断
            {
                Group group = (Group) field;
                if (group.getGroupType() == 1)    //超级组件判断
                {
                    // 遍历条件复杂规则
                    if (CheckDataUtil.isNotNull(group.getConditionRules())) {
                        for (ConditionalRule conditionalRule : group.getConditionRules()) {
                            // 先判断要处理的组件列表是否为空
                            if (CheckDataUtil.isNull(conditionalRule.getWidgetList())) {
                                continue;
                            }
                            // 先验证条件，条件值为空时，默认为通过
                            if (CheckDataUtil.isNotNull(conditionalRule.getCondition())) {
                                FormWidgetContent formWidgetContent = findWidgetContent(content, conditionalRule.getFieldId(), conditionalRule.getLineNum());
                                if (formWidgetContent == null) {
                                    // 数据为空，默认条件不通过
                                    continue;
                                }
                                //进行验证条件
                                if (!checkCondition(OperatorType.valueOf(conditionalRule.getRelationOperator()), String.valueOf(formWidgetContent.getValue()), conditionalRule.getCondition())) {
                                    continue;
                                }
                            }
                            // 进行规则验证
                            for (ConditionalWidget conditionalWidget : conditionalRule.getWidgetList()) {
                                ConditionalField conditionalField = conditionalWidget.getField();
                                if (conditionalField != null && CheckDataUtil.isNotNull(conditionalField.getRules())) {
                                    FormWidgetContent formWidgetContent = findWidgetContent(content, conditionalField.getId(), conditionalWidget.getLineNum());
                                    Object value = formWidgetContent != null ? formWidgetContent.getValue() : null;
                                    for (RuleBase ruleBase : conditionalField.getRules()) {
                                        checkRule(ruleBase, value, content.getWidgets());
                                    }
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    private void checkImportInfo(FormPageContent content) {
        CheckDataUtil.checkNull(content.getFormId(), "缺少表单ID");
        CheckDataUtil.checkNull(content.getPageId(), "缺少分页ID");
        CheckDataUtil.checkNull(content.getContentId(), "缺少填报ID");
    }

    private void checkImportInfo(FormGroupLineContent content) {
        CheckDataUtil.checkNull(content.getFormId(), "缺少表单ID");
        CheckDataUtil.checkNull(content.getGroupId(), "缺少表格组ID");
        CheckDataUtil.checkNull(content.getContentId(), "缺少填报ID");
    }

    /**
     * @param orgForm
     * @param content
     * @param components
     * @param mainContentMap
     * @param componentList
     * @param tableList
     * @param uploadList
     * @param isStorage
     * @return void    返回类型
     * @throws ParseException 参数说明
     * @Title: handleDBData
     * @Description: 筛选要存入数据库的数据
     */
    private void handleDBData(SmartForm orgForm, FormPageContent content, FormPage formPage, Map<String, ConditionalWidget> replaceWidgets,
                              List<FormContentBaseDTO<Group>> components, Map<String, Object> mainContentMap,
                              List<FormContentBaseDTO<FormContentComponentEntity>> componentList,
                              List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList,
                              List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, boolean isStorage)
            throws ParseException {

        if (CheckDataUtil.isNotNull(content.getWidgetList())) {
            int filedCount = 0;    //字段赋值计数，用于判断保存的数据格式是否合法

            String contentId = content.getContentId();
            // 遍历分页提取组件数据
            for (FormContentBaseDTO<Group> component : components) {
                // 获取组件内容
                Group contentGroup = getContentGroup(component, formPage);
                Group group = component.getData();
                if (group.getGroupType() == GroupType.SUPER.value) {
                    boolean useMainTable = false;
                    // 判定组件的库名与表名是否与表单主表相同
                    if (orgForm.getDbName().equals(contentGroup.getDbName())
                            && orgForm.getTableName().equals(contentGroup.getTable())) {
                        useMainTable = true;
                    }
                    // 处理超级组件
                    for (FormFieldBase widget : contentGroup.getFieldList()) {
                        if (widget instanceof Group) {
                            // 处理超级组件旗下的样式组
                            Group styleGroup = (Group) widget;
                            if (styleGroup.getGroupType() == GroupType.STYLE.value) {
                                // 遍历样式组字段
                                for (FormFieldBase styleWidget : styleGroup.getFieldList()) {
                                    if (styleWidget instanceof WidgetBase) {
                                        WidgetBase widgetBase = (WidgetBase) styleWidget;
                                        // 查找替换信息
                                        if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                                            ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                                            widgetBase.setHide(conditionalWidget.getHide());
                                            widgetBase.setDisable(conditionalWidget.getDisable());
                                            if (conditionalWidget.getField() != null) {
                                                widgetBase.setRules(conditionalWidget.getField().getRules());
                                            }
                                        }
                                        if (widgetBase.getHide() != null && widgetBase.getHide()) {
                                            // 隐藏组件不处理
                                            continue;
                                        }
                                        // 赋值widget
                                        FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId(), null);
                                        if (formWidgetContent != null) {
                                            // System.out.print("filedCount:" + filedCount + "->" + (filedCount+1) + "  content:" + formWidgetContent.getFieldId() + "\n");
                                            ++filedCount;
                                        }
                                        fillWidget(widgetBase, formWidgetContent);
                                        // 如果是保存验证规则
                                        if (!isStorage) {
                                            checkWidget(widgetBase, formWidgetContent, content.getWidgets());
                                        }
                                        this.handleFieldDTO(useMainTable, false, contentId, null, component,
                                                widgetBase, mainContentMap, componentList, tableList,
                                                uploadList, orgForm.getFieldMapperList(), null, isStorage);
                                    }
                                }
                            }
                        } else if (group.getGroupType() == GroupType.GRID.value) {
                            // 栅格组
                            // 遍历栅格组字段
                            for (GroupRow row : group.getRowList()) {
                                for (FormFieldBase styleWidget : row.getFieldList()) {
                                    if (styleWidget instanceof WidgetBase) {
                                        WidgetBase widgetBase = (WidgetBase) styleWidget;
                                        // 查找替换信息
                                        if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                                            ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                                            widgetBase.setHide(conditionalWidget.getHide());
                                            widgetBase.setDisable(conditionalWidget.getDisable());
                                            if (conditionalWidget.getField() != null) {
                                                widgetBase.setRules(conditionalWidget.getField().getRules());
                                            }
                                        }
                                        if (widgetBase.getHide() != null && widgetBase.getHide()) {
                                            // 隐藏组件不处理
                                            continue;
                                        }
                                        // 赋值widget
                                        FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId(), null);
                                        if (formWidgetContent != null) {
                                            // System.out.print("filedCount:" + filedCount + "->" + (filedCount+1) + "  content:" + formWidgetContent.getFieldId() + "\n");
                                            ++filedCount;
                                        }
                                        fillWidget(widgetBase, formWidgetContent);
                                        // 如果是保存验证规则
                                        if (!isStorage) {
                                            checkWidget(widgetBase, formWidgetContent, content.getWidgets());
                                        }
                                        this.handleFieldDTO(useMainTable, false, contentId, null, component,
                                                widgetBase, mainContentMap, componentList, tableList,
                                                uploadList, orgForm.getFieldMapperList(), null, isStorage);
                                    }
                                }
                            }
                        } else if (widget instanceof WidgetBase) {
                            WidgetBase widgetBase = (WidgetBase) widget;
                            // 查找替换信息
                            if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                                ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                                widgetBase.setHide(conditionalWidget.getHide());
                                widgetBase.setDisable(conditionalWidget.getDisable());
                                if (conditionalWidget.getField() != null) {
                                    widgetBase.setRules(conditionalWidget.getField().getRules());
                                }
                            }
                            if (widgetBase.getHide() != null && widgetBase.getHide()) {
                                // 隐藏组件不处理
                                continue;
                            }
                            // 赋值widget
                            FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId(), null);
                            if (formWidgetContent != null) {
                                ++filedCount;
                            }
                            fillWidget(widgetBase, formWidgetContent);
                            // 如果是保存验证规则
                            if (!isStorage) {
                                checkWidget(widgetBase, formWidgetContent, content.getWidgets());
                            }
                            this.handleFieldDTO(useMainTable, false, contentId, null, component, widgetBase,
                                    mainContentMap, componentList, tableList, uploadList, orgForm.getFieldMapperList(),
                                    null, isStorage);
                        }
                    }
                } else if (group.getGroupType() == GroupType.TABLE.value) {
                    // 处理表格
                    if (group.getSteerable() == null || !group.getSteerable()) {
                        // 查找table存储对象
                        FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = this.getTableDTO(component,
                                tableList);
                        // 普通表格
                        if (CheckDataUtil.isNull(contentGroup.getGridViewRules())) {
                            GroupLine line = contentGroup.getOriginalLine();
                            // 遍历组字段
                            if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                    && line.getFieldList().size() > 0) {
                                // 创建行
                                FormContentTableEntity lineDTO = new FormContentTableEntity();
                                String uuid = UUIDUtil.getNextId();
                                lineDTO.setId(uuid);
                                lineDTO.setContentId(contentId);
                                lineDTO.setLineNum(0);
                                lineDTO.setSort(0);
                                lineDTO.setWorkType(component.getWorkType());
                                lineDTO.setCreatedAt(System.currentTimeMillis());
                                lineDTO.setModifiedAt(System.currentTimeMillis());
                                if (isStorage) {
                                    lineDTO.setState(ContentStateType.STORAGE.value);
                                } else {
                                    lineDTO.setState(ContentStateType.SAVE.value);
                                }
                                // map添加
                                Query query = new Query();
                                lineDTO.setContent(query);
                                tableDTO.getData().add(lineDTO);
                                for (WidgetBase widget : line.getFieldList()) {
                                    if (widget instanceof WidgetBase) {
                                        WidgetBase widgetBase = (WidgetBase) widget;
                                        // 查找替换信息
                                        if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                                            ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                                            widgetBase.setHide(conditionalWidget.getHide());
                                            widgetBase.setDisable(conditionalWidget.getDisable());
                                            if (conditionalWidget.getField() != null) {
                                                widgetBase.setRules(conditionalWidget.getField().getRules());
                                            }
                                        }
                                        if (widgetBase.getHide() != null && widgetBase.getHide()) {
                                            // 隐藏组件不处理
                                            continue;
                                        }
                                        // 赋值widget
                                        FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId(), null);
                                        if (formWidgetContent != null) {
                                            ++filedCount;
                                        }
                                        fillWidget(widgetBase, formWidgetContent);
                                        // 如果是保存验证规则
                                        if (!isStorage) {
                                            checkWidget(widgetBase, formWidgetContent, content.getWidgets());
                                        }
                                        this.handleFieldDTO(false, true, contentId, lineDTO, component,
                                                widgetBase, mainContentMap, componentList, tableList,
                                                uploadList, null, null, isStorage);
                                    }
                                }
                            }
                        } else {
                            // 遍历表格组行
                            if (contentGroup.getLineList() != null && contentGroup.getLineList().size() > 0) {
                                int lineNum = 0;
                                for (GroupLine line : contentGroup.getLineList()) {
                                    // 遍历组字段
                                    if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                            && line.getFieldList().size() > 0) {
                                        lineNum++;
                                        // 创建行
                                        FormContentTableEntity lineDTO = new FormContentTableEntity();
                                        String uuid = UUIDUtil.getNextId();
                                        lineDTO.setId(uuid);
                                        lineDTO.setContentId(contentId);
                                        lineDTO.setLineNum(lineNum);
                                        lineDTO.setWorkType(component.getWorkType());
                                        lineDTO.setCreatedAt(System.currentTimeMillis());
                                        lineDTO.setModifiedAt(System.currentTimeMillis());
                                        if (isStorage) {
                                            lineDTO.setState(ContentStateType.STORAGE.value);
                                        } else {
                                            lineDTO.setState(ContentStateType.SAVE.value);
                                        }
                                        // map添加
                                        Query query = new Query();
                                        lineDTO.setContent(query);
                                        tableDTO.getData().add(lineDTO);
                                        for (WidgetBase widget : line.getFieldList()) {
                                            if (widget instanceof WidgetBase) {
                                                WidgetBase widgetBase = (WidgetBase) widget;
                                                // 查找替换信息
                                                if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                                                    ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                                                    widgetBase.setHide(conditionalWidget.getHide());
                                                    widgetBase.setDisable(conditionalWidget.getDisable());
                                                    if (conditionalWidget.getField() != null) {
                                                        widgetBase.setRules(conditionalWidget.getField().getRules());
                                                    }
                                                }
                                                if (widgetBase.getHide() != null && widgetBase.getHide()) {
                                                    // 隐藏组件不处理
                                                    continue;
                                                }
                                                // 赋值widget
                                                FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId(), line.getLineNum());
                                                if (formWidgetContent != null) {
                                                    // System.out.print("filedCount:" + filedCount + "->" + (filedCount+1) + "  content:" + formWidgetContent.getFieldId() + "\n");
                                                    ++filedCount;
                                                }
                                                fillWidget(widgetBase, formWidgetContent);
                                                // 如果是保存验证规则
                                                if (!isStorage) {
                                                    checkWidget(widgetBase, formWidgetContent, content.getWidgets());
                                                }
                                                this.handleFieldDTO(false, true, contentId, lineDTO, component,
                                                        widgetBase, mainContentMap, componentList, tableList,
                                                        uploadList, null, null, isStorage);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // 可控表格，做为超级组件处理
                        boolean useMainTable = false;
                        // 判定组件的库名与表名是否与表单主表相同
                        if (orgForm.getDbName().equals(contentGroup.getDbName())
                                && orgForm.getTableName().equals(contentGroup.getTable())) {
                            useMainTable = true;
                        }
                        // 遍历行
                        for (GroupLine line : contentGroup.getLineList()) {
                            // 遍历组字段
                            if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                    && line.getFieldList().size() > 0) {
                                for (WidgetBase widget : line.getFieldList()) {
                                    if (widget instanceof WidgetBase) {
                                        WidgetBase widgetBase = (WidgetBase) widget;
                                        // 查找替换信息
                                        if (replaceWidgets != null && replaceWidgets.containsKey(widgetBase.getId())) {
                                            ConditionalWidget conditionalWidget = replaceWidgets.get(widgetBase.getId());
                                            widgetBase.setHide(conditionalWidget.getHide());
                                            widgetBase.setDisable(conditionalWidget.getDisable());
                                            if (conditionalWidget.getField() != null) {
                                                widgetBase.setRules(conditionalWidget.getField().getRules());
                                            }
                                        }
                                        if (widgetBase.getHide() != null && widgetBase.getHide()) {
                                            // 隐藏组件不处理
                                            continue;
                                        }
                                        // 赋值widget
                                        FormWidgetContent formWidgetContent = findWidgetContent(content, widgetBase.getId(), line.getLineNum());
                                        if (formWidgetContent != null) {
                                            ++filedCount;
                                        }
                                        fillWidget(widgetBase, formWidgetContent);
                                        // 如果是保存验证规则
                                        if (!isStorage) {
                                            checkWidget(widgetBase, formWidgetContent, content.getWidgets());
                                        }
                                        this.handleFieldDTO(useMainTable, false, contentId, null, component,
                                                widgetBase, mainContentMap, componentList, tableList,
                                                uploadList, null, line.getLineNum(), isStorage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // 验证字段数据是否合法
            if (content.getWidgetList().size() != filedCount) {
                throw new GlobalException(60409);
            }
        }
    }

    /**
     * @param widgetBase        当前验证的字段
     * @param formWidgetContent 当前验证的字段值
     * @param widgetContents    本次提交的字段值map
     * @return void    返回类型
     * @Title: checkWidget
     * @Description: 验证字段规则
     */
    private void checkWidget(WidgetBase widgetBase, FormWidgetContent formWidgetContent, Map<String, FormWidgetContent> widgetContents) {
        if (CheckDataUtil.isNotNull(widgetBase.getRules())) {
            for (RuleBase ruleBase : widgetBase.getRules()) {
                checkRule(ruleBase, formWidgetContent != null ? formWidgetContent.getValue() : null, widgetContents);
            }
        }
    }

    /**
     * @param ruleBase
     * @param value
     * @param widgetContents 参数说明
     * @return void    返回类型
     * @Title: checkRule
     * @Description: 验证规则
     */
    private void checkRule(RuleBase ruleBase, Object value, Map<String, FormWidgetContent> widgetContents) {
        if (!ruleBase.getEnable())
            return;
        if (ruleBase.getType() == 7) {
            // 自定义规则单独处理
            RuleCustom ruleCustom = (RuleCustom) ruleBase;
            if (CheckDataUtil.isNull(ruleCustom.getResultList())) {
                // 如果验证判断为空，则跳过
                return;
            }
            // 判断是否要执行自定义规则
            if (checkRuleFormula(ruleCustom.getConditionList(), widgetContents)) {
                // 验证自定义规则
                if (!checkRuleFormula(ruleCustom.getResultList(), widgetContents)) {
                    throw new GlobalException(60407);
                }
            }
        } else {
            if (!RulesUtil.checkRules(ruleBase, value)) {
                throw new GlobalException(60408);
            }
        }
    }

    /**
     * @param formulaList
     * @param widgetContents 参数说明
     * @return void    返回类型
     * @Title: checkRuleFormula
     * @Description: 验证规则公式
     */
    private boolean checkRuleFormula(List<RuleFormula> formulaList, Map<String, FormWidgetContent> widgetContents) {
        if (CheckDataUtil.isNull(formulaList)) {
            return true;
        }
        boolean checked = true;
        boolean canSkip = false;
        // 遍历规则
        for (RuleFormula ruleFormula : formulaList) {
            // 判断规则类型，是否是运算单元
            if (ruleFormula.getType() == 1) {
                // 是否可以跳过计算
                if (canSkip) {
                    canSkip = false;
                    continue;
                }
                // 运算单元验证
                checked = checkOperator(ruleFormula.getOperatorList(), widgetContents);
            } else if (ruleFormula.getType() == 2) {
                // 逻辑运算符
                if (ruleFormula.getLogicOperator() == OperatorType.OR.value) {
                    // or判断，如果左面是true，右面可以跳过判断
                    if (checked) {
                        canSkip = true;
                    }
                } else if (ruleFormula.getLogicOperator() == OperatorType.AND.value) {
                    // and判断，如果左面是false，右面可以跳过判断
                    if (!checked) {
                        canSkip = true;
                    }
                }
            }
        }
        return checked;
    }

    /**
     * @param operatorList
     * @return boolean    返回类型
     * @Title: checkOperator
     * @Description: 验证运算单元
     */
    private boolean checkOperator(List<OperatorUnit> operatorList, Map<String, FormWidgetContent> widgetContents) {
        Object value = null;    //运算值
        boolean isLastOperator = false;    //是否是最后的运算单元
        OperatorType lastOperatorType = null;    //最后的运算符
        // 遍历运算单元
        for (OperatorUnit operatorUnit : operatorList) {

            // 判断类型 1,算数运算符;2,合计运算符;3,字段
            switch (operatorUnit.getType()) {
                case 1:    //算数运算符
                {
                    lastOperatorType = OperatorType.valueOf(operatorUnit.getOperatorType());
                    switch (lastOperatorType) {
                        case EQUAL:
                        case GREATERTHAN:
                        case GREATERTHANANDEQUAL:
                        case LESSTHAN:
                        case LESSTHANANDEQUAL:
                        case NOTEQUAL: {
                            // 结尾运算符
                            isLastOperator = true;
                        }
                        break;
                        default:
                            break;
                    }
                }
                break;
                case 2:    //合计运算符
                {
                    List<Double> totalList = new ArrayList<Double>();
                    // 获取合计所有字段
                    if (CheckDataUtil.isNotNull(operatorUnit.getFieldList())) {
                        for (String filedId : operatorUnit.getFieldList()) {
                            // 获取合计字段值
                            FormWidgetContent content = findWidgetContent(widgetContents, filedId, null);
                            double num = 0;
                            if (content != null) {
                                num = Double.valueOf(String.valueOf(content.getValue()));
                            }
                            totalList.add(num);
                        }
                    }
                    double totalValue = 0;
                    if (totalList.size() > 0) {
                        // 合计运算 1,SUM;2,AVERGE;3,MIN;4,MAX;5,COUNT
                        switch (operatorUnit.getTotalType()) {
                            case 1: {
                                for (Double total : totalList) {
                                    totalValue += total;
                                }
                            }
                            break;
                            case 2: {
                                for (Double total : totalList) {
                                    totalValue += total;
                                }
                                totalValue /= totalList.size();
                            }
                            break;
                            case 3: {
                                for (Double total : totalList) {
                                    totalValue = Math.min(totalValue, total);
                                }
                            }
                            break;
                            case 4: {
                                for (Double total : totalList) {
                                    totalValue = Math.max(totalValue, total);
                                }
                            }
                            break;
                            case 5: {
                                totalValue = totalList.size();
                            }
                            break;
                        }
                    }
                    if (lastOperatorType != null) {
                        // 算数运算处理
                        value = operatorMath(lastOperatorType, Double.valueOf(String.valueOf(value)), totalValue);
                    }
                }
                break;
                case 3:    //字段
                {
                    Object newValue = null;
                    // 获取值
                    // 先判断valueType
                    if (operatorUnit.getValueType() == null) {
                        // 从字段获取
                        FormWidgetContent content = findWidgetContent(widgetContents, operatorUnit.getFieldId(), operatorUnit.getLineNum());
                        newValue = content != null ? content.getValue() : null;
                    } else if (operatorUnit.getValueType() == 1) {
                        // 常量
                        newValue = operatorUnit.getValue();
                    } else if (operatorUnit.getValueType() == 2) {
                        // 正则
                        if (!isLastOperator) {
                            // 如果不是结尾运算，则报错
                            throw new GlobalException(60406);
                        }
                        String regex = operatorUnit.getValue();
                        // 进行正则运算
                        return RulesUtil.match(regex, String.valueOf(value));
                    }

                    if (lastOperatorType != null) {
                        // 是否是结尾运算
                        if (isLastOperator) {
                            return checkCondition(lastOperatorType, String.valueOf(value), String.valueOf(newValue));
                        } else {
                            // 算数运算
                            value = operatorMath(lastOperatorType, Double.valueOf(String.valueOf(value)), Double.valueOf(String.valueOf(newValue)));
                        }
                    } else {
                        // 初始值
                        value = newValue;
                    }

                }
                break;
            }
        }

        return true;
    }

    /**
     * @param type
     * @param value1
     * @param value2
     * @return Double    返回类型
     * @Title: operatorMath
     * @Description: 算数运算
     */
    public double operatorMath(OperatorType type, double value1, double value2) {
        double value = value1;
        switch (type) {
            case ADD:
                value = value1 + value2;
                break;
            case MINUS:
                value = value1 - value2;
                break;
            case MULTIPLY:
                value = value1 * value2;
                break;
            case DIVISION:
                if (value2 == 0)    //除0容错判断
                {
                    value = 0;
                } else {
                    value = value1 / value2;
                }
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * @param type
     * @param value1
     * @param value2
     * @return boolean    返回类型
     * @Title: checkCondition
     * @Description: 验证条件，也用在自定义规则的结尾判断
     */
    public boolean checkCondition(OperatorType type, String value1, String value2) {
        switch (type) {
            case EQUAL: {
                return value1.equals(value2);
            }
            case GREATERTHAN: {
                double num1 = Double.valueOf(value1);
                double num2 = Double.valueOf(value2);
                return num1 > num2;
            }
            case GREATERTHANANDEQUAL: {
                double num1 = Double.valueOf(value1);
                double num2 = Double.valueOf(value2);
                return num1 >= num2;
            }
            case LESSTHAN: {
                double num1 = Double.valueOf(value1);
                double num2 = Double.valueOf(value2);
                return num1 < num2;
            }
            case LESSTHANANDEQUAL: {
                double num1 = Double.valueOf(value1);
                double num2 = Double.valueOf(value2);
                return num1 <= num2;
            }
            case NOTEQUAL: {
                return !value1.equals(value2);
            }
            default:
                break;

        }
        return true;
    }

    /**
     * @param content 参数说明
     * @return void    返回类型
     * @Title: initContent
     * @Description: 初始化填报内容
     */
    private void initContent(FormPageContent content) {
        Map<String, FormWidgetContent> widgets = new HashMap<String, FormWidgetContent>();
        for (FormWidgetContent formWidgetContent : content.getWidgetList()) {
            String key = CheckDataUtil.isNull(formWidgetContent.getLineNum()) ? formWidgetContent.getFieldId() : formWidgetContent.getFieldId() + formWidgetContent.getLineNum();
            widgets.put(key, formWidgetContent);
        }
        content.setWidgets(widgets);
    }

    /**
     * @param content 参数说明
     * @return void    返回类型
     * @Title: initContent
     * @Description: 初始化行填报内容
     */
    private void initContent(FormGroupLineContent content) {
        Map<String, FormWidgetContent> widgets = new HashMap<String, FormWidgetContent>();
        for (FormWidgetContent formWidgetContent : content.getWidgetList()) {
            String key = formWidgetContent.getFieldId();
            widgets.put(key, formWidgetContent);
        }
        content.setWidgets(widgets);
        if (content.getLineId() == null) {
            content.setLineId(UUIDUtil.getNextId());
        }
    }

    /**
     * @param content
     * @param filedId
     * @param lineNum
     * @return FormWidgetContent    返回类型
     * @Title: findWidgetContent
     * @Description: 查找控件填报内容
     */
    private FormWidgetContent findWidgetContent(FormPageContent content, String filedId, Integer lineNum) {
        if (content.getWidgets() == null) {
            return null;
        }

        return findWidgetContent(content.getWidgets(), filedId, lineNum);
    }

    /**
     * @param content
     * @param filedId
     * @return FormWidgetContent    返回类型
     * @Title: findWidgetContent
     * @Description: 查找控件填报内容
     */
    private FormWidgetContent findWidgetContent(FormGroupLineContent content, String filedId) {
        if (content.getWidgets() == null) {
            return null;
        }

        return findWidgetContent(content.getWidgets(), filedId, null);
    }

    /**
     * @param contents
     * @param filedId
     * @param lineNum
     * @return FormWidgetContent    返回类型
     * @Title: findWidgetContent
     * @Description: 查找控件内容
     */
    private FormWidgetContent findWidgetContent(Map<String, FormWidgetContent> contents, String filedId, Integer lineNum) {
        String key = lineNum == null ? filedId : filedId + lineNum;
        return contents.get(key);
    }

    /**
     * @param formWidgetContent 参数说明
     * @return WidgetBase    返回类型
     * @throws ParseException
     * @Title: fillWidget
     * @Description: 根据填报内容填充字段
     */
    @SuppressWarnings({"unchecked"})
    private WidgetBase fillWidget(WidgetBase widget, FormWidgetContent formWidgetContent) throws ParseException {

        WidgetType dataType = WidgetType.valueOf(widget.getFieldType());
        if (dataType != WidgetType.UPLOAD && (CheckDataUtil.isNull(widget.getAlias()) || formWidgetContent == null)) {
            return widget;
        }
        WidgetBase simWidget = null;
        switch (dataType) {
            case NUMBER: {
                // 数字
                simWidget = widget;
                ((WidgetNumber) simWidget).setDefValue(handleBigDecimal(formWidgetContent.getValue()));
            }
            break;
            case INPUT:
            case TEXTAREA: {
                // 文本
                simWidget = widget;
                ((WidgetInput) simWidget).setDefValue(String.valueOf(formWidgetContent.getValue()));
            }
            break;
            case EDITOR: {
                // 富文本
                simWidget = widget;
                // 兼融老数据
                if (simWidget instanceof WidgetEditor) {
                    ((WidgetEditor) simWidget).setDefValue(String.valueOf(formWidgetContent.getValue()));
                } else {
                    ((WidgetInput) simWidget).setDefValue(String.valueOf(formWidgetContent.getValue()));
                }
            }
            break;
            case RADIO: {
                // 单选
                simWidget = widget;
                ((WidgeRadio) simWidget).setDefValue(String.valueOf(formWidgetContent.getValue()));
            }
            break;
            case CHECKBOX:
            case SELECT:
            case CASCADER: {
                // 多选
                simWidget = widget;
                if (formWidgetContent.getValue() == null) {
                    ((WidgeSelect) simWidget).setDefValue(null);
                } else {
                    if (formWidgetContent.getValue() instanceof JSONArray) {
                        JSONArray list = (JSONArray) formWidgetContent.getValue();
                        if (list != null && list.size() > 0) {
                            List<String> defs = list.toJavaList(String.class);
                            ((WidgeSelect) simWidget).setDefValue(defs);
                        } else {
                            ((WidgeSelect) simWidget).setDefValue(null);
                        }
                    } else {
                        List<String> defs = new ArrayList<String>();
                        defs.add(String.valueOf(formWidgetContent.getValue()));
                        ((WidgeSelect) simWidget).setDefValue(defs);
                    }
                }
            }
            break;
            case DATE: {
                // 日期
                simWidget = widget;
                Long time = handleLong(formWidgetContent.getValue());
                if (time != null && time != 0) {
                    ((WidgetDate) simWidget).setDefStartDate(new Date(time));
                } else {
                    ((WidgetDate) simWidget).setDefStartDate(null);
                }
            }
            break;
            case DATESPAN: {
                // 日期段
                simWidget = widget;
                JSONArray timelist = (JSONArray) formWidgetContent.getValue();
                Long startTime = null;
                Long entTime = null;
                if (CheckDataUtil.isNotNull(timelist)) {
                    startTime = handleLong(timelist.get(0));
                    if (timelist.size() > 1) {
                        entTime = handleLong(timelist.get(1));
                    }
                }

                if (startTime != null && startTime != 0) {
                    ((WidgetDate) simWidget).setDefStartDate(new Date(startTime));
                } else {
                    ((WidgetDate) simWidget).setDefStartDate(null);
                }

                if (entTime != null && entTime != 0) {
                    ((WidgetDate) simWidget).setDefEndDate(new Date(entTime));
                } else {
                    ((WidgetDate) simWidget).setDefEndDate(null);
                }
            }
            break;
            case TIME: {
                // 时间简化
                simWidget = widget;
                Integer time = handleInteger(formWidgetContent.getValue());
                if (time != null && time != 0) {
                    ((WidgetTime) simWidget).setDefStartTime(this.getTimeStampStr(time));
                } else {
                    ((WidgetTime) simWidget).setDefStartTime(null);
                }
            }
            break;
            case TIMESPAN: {
                // 时间段简化
                simWidget = widget;
                ArrayList<String> timelist = (ArrayList<String>) formWidgetContent.getValue();
                Integer startTime = null;
                Integer entTime = null;
                if (CheckDataUtil.isNotNull(timelist)) {
                    startTime = handleInteger(timelist.get(0));
                    if (timelist.size() > 1) {
                        entTime = handleInteger(timelist.get(1));
                    }
                }

                if (startTime != null && startTime != 0) {
                    ((WidgetTime) simWidget).setDefStartTime(this.getTimeStampStr(startTime));
                } else {
                    ((WidgetTime) simWidget).setDefStartTime(null);
                }

                if (entTime != null && entTime != 0) {
                    ((WidgetTime) simWidget).setDefEndTime(this.getTimeStampStr(entTime));
                } else {
                    ((WidgetTime) simWidget).setDefEndTime(null);
                }
            }
            break;
            case UPLOAD: {
                WidgetUpload vData = (WidgetUpload) widget;
                if (formWidgetContent != null) {
                    JSONArray udata = (JSONArray) formWidgetContent.getValue();
                    if (udata != null && udata.size() > 0) {
                        List<UploadItem> uploads = new ArrayList<UploadItem>();
                        for (int i = 0; i < udata.size(); i++) {
                            JSONObject obj = udata.getJSONObject(i);
                            UploadItem item = obj.toJavaObject(UploadItem.class);
                            uploads.add(item);
                        }
                        vData.setDefValue(uploads);
                    }
                }
                simWidget = vData;
            }
            break;
            default:
                break;
        }

        return simWidget;
    }

    /**
     * 筛选要存入数据库的数据
     *
     * @param contentId      填报id
     * @param orgForm        表单结构
     * @param page           分页内容
     * @param components     当前页要存储的组件列表
     * @param mainContentMap 主表内容数据
     * @param componentList  组件表数据
     * @param tableList      表格表列表数据
     * @param uploadList
     * @param hasStorage     是否暂存，暂存不做验证
     * @throws ParseException
     */
    private FormPage handleDBData(String contentId, SmartForm orgForm, FormPage page,
                                  List<FormContentBaseDTO<Group>> components, Map<String, Object> mainContentMap,
                                  List<FormContentBaseDTO<FormContentComponentEntity>> componentList,
                                  List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList,
                                  List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, boolean hasStorage)
            throws ParseException {

        // 遍历分页提取组件数据
        if (page != null && page.getFieldList() != null && page.getFieldList().size() > 0) {
            // 遍历要存储的组件
            for (FormContentBaseDTO<Group> component : components) {
                // 获取组件内容
                Group contentGroup = getContentGroup(component, page);
                Group group = component.getData();
                if (group.getGroupType() == GroupType.SUPER.value) {
                    boolean useMainTable = false;
                    // 判定组件的库名与表名是否与表单主表相同
                    if (orgForm.getDbName().equals(contentGroup.getDbName())
                            && orgForm.getTableName().equals(contentGroup.getTable())) {
                        useMainTable = true;
                    }
                    // 处理超级组件
                    for (FormFieldBase widget : contentGroup.getFieldList()) {
                        if (widget instanceof Group) {
                            // 处理超级组件旗下的样式组
                            Group styleGroup = (Group) widget;
                            if (styleGroup.getGroupType() == GroupType.STYLE.value) {
                                // 遍历样式组字段
                                for (FormFieldBase styleWidget : styleGroup.getFieldList()) {
                                    if (styleWidget instanceof WidgetBase) {
                                        this.handleFieldDTO(useMainTable, false, contentId, null, component,
                                                (WidgetBase) styleWidget, mainContentMap, componentList, tableList,
                                                uploadList, orgForm.getFieldMapperList(), null, hasStorage);
                                    }
                                }
                            }
                        } else if (widget instanceof WidgetBase) {
                            this.handleFieldDTO(useMainTable, false, contentId, null, component, (WidgetBase) widget,
                                    mainContentMap, componentList, tableList, uploadList, orgForm.getFieldMapperList(),
                                    null, hasStorage);
                        }
                    }
                } else if (group.getGroupType() == GroupType.TABLE.value) {
                    // 处理表格
                    if (group.getSteerable() == null || !group.getSteerable()) {
                        // 遍历表格组行
                        if (contentGroup.getLineList() != null && contentGroup.getLineList().size() > 0) {
                            // 查找table存储对象
                            FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = this.getTableDTO(component,
                                    tableList);
                            int lineNum = 0;
                            for (GroupLine line : contentGroup.getLineList()) {
                                // 遍历组字段
                                if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                        && line.getFieldList().size() > 0) {
                                    lineNum++;
                                    // 创建行
                                    FormContentTableEntity lineDTO = new FormContentTableEntity();
                                    String uuid = UUIDUtil.getNextId();
                                    lineDTO.setId(uuid);
                                    lineDTO.setContentId(contentId);
                                    lineDTO.setLineNum(lineNum);
                                    lineDTO.setWorkType(component.getWorkType());
                                    lineDTO.setCreatedAt(System.currentTimeMillis());
                                    lineDTO.setModifiedAt(System.currentTimeMillis());
                                    if (hasStorage) {
                                        lineDTO.setState(ContentStateType.STORAGE.value);
                                    } else {
                                        lineDTO.setState(ContentStateType.SAVE.value);
                                    }
                                    // map添加
                                    Query query = new Query();
                                    lineDTO.setContent(query);
                                    tableDTO.getData().add(lineDTO);
                                    for (WidgetBase widget : line.getFieldList()) {
                                        if (widget instanceof WidgetBase) {
                                            this.handleFieldDTO(false, true, contentId, lineDTO, component,
                                                    (WidgetBase) widget, mainContentMap, componentList, tableList,
                                                    uploadList, null, null, hasStorage);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // 可控表格，做为超级组件处理
                        boolean useMainTable = false;
                        // 判定组件的库名与表名是否与表单主表相同
                        if (orgForm.getDbName().equals(contentGroup.getDbName())
                                && orgForm.getTableName().equals(contentGroup.getTable())) {
                            useMainTable = true;
                        }
                        // 遍历行
                        for (GroupLine line : contentGroup.getLineList()) {
                            // 遍历组字段
                            if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                    && line.getFieldList().size() > 0) {
                                for (WidgetBase widget : line.getFieldList()) {
                                    if (widget instanceof WidgetBase) {
                                        this.handleFieldDTO(useMainTable, false, contentId, null, component,
                                                (WidgetBase) widget, mainContentMap, componentList, tableList,
                                                uploadList, null, line.getLineNum(), hasStorage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return page;
    }

    /**
     * 获取内容组件
     *
     * @param component 查询的组件
     * @param nowPage   内容分页
     */
    private Group getContentGroup(FormContentBaseDTO<Group> component, FormPage nowPage) {
        // 遍历分页旗下超级组件
        for (FormFieldBase pageField : nowPage.getFieldList()) {
            if (pageField instanceof Group) {
                Group group = (Group) pageField;
                // 是否是该组
                if (component.getData().getId().equals(group.getId())) {
                    int workType = group.getWorkType() == null ? 0 : group.getWorkType();
                    group.setWorkType(workType);
                    return group;
                }
                if (group.getGroupType() == GroupType.SUPER.value && group.getFieldList() != null) {
                    // 遍历超级组件字段
                    for (FormFieldBase widget : group.getFieldList()) {
                        if (widget instanceof Group) {
                            // 处理超级组件旗下的组
                            Group tableGroup = (Group) widget;
                            if (tableGroup.getGroupType() == GroupType.TABLE.value) {
                                // 是否是该组
                                if (component.getData().getId().equals(tableGroup.getId())) {
                                    int workType = tableGroup.getWorkType() == null ? 0 : tableGroup.getWorkType();
                                    tableGroup.setWorkType(workType);
                                    return tableGroup;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查找table存储对象,如果没有则初始化
     *
     * @param component
     * @param tableList
     * @return
     */
    private FormContentBaseDTO<List<FormContentTableEntity>> getTableDTO(FormContentBaseDTO<Group> component,
                                                                         List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList) {
        // 查找table存储对象
        FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = null;
        for (FormContentBaseDTO<List<FormContentTableEntity>> tableItem : tableList) {
            if (tableItem.getDbName().equals(component.getDbName())
                    && tableItem.getDbTable().equals(component.getDbTable())
                    && tableItem.getWorkType().equals(component.getWorkType())) {
                tableDTO = tableItem;
                break;
            }
        }
        // 初始化组件存储对象
        if (tableDTO == null) {
            tableDTO = new FormContentBaseDTO<List<FormContentTableEntity>>();
            List<FormContentTableEntity> entity = new ArrayList<FormContentTableEntity>();

            tableDTO.setData(entity);
            tableDTO.setDbName(component.getDbName());
            tableDTO.setDbTable(component.getDbTable());
            tableDTO.setWorkType(component.getWorkType());
            // map添加
            tableDTO.setContent(new Query());
            tableList.add(tableDTO);
        }
        return tableDTO;
    }

    /**
     * 获取所有组件状态
     *
     * @param contentId
     * @return
     */
    private List<ComponentStateEntity> getComponentState(String dbTable, String contentId) {
        // 设置查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dbTable", dbTable);
        params.put("contentId", contentId);
        Query query = new Query(params);
        List<ComponentStateEntity> componentStates;
        try {
            DynamicDataSourceContextHolder.push(DBCutConstants.SMART_CORE);
            componentStates = this.componentStateDao.list(query);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }

        return componentStates;
    }

    /**
     * 处理固定的额外参数
     *
     * @param contentId
     * @param mappers
     * @param mainContentMap
     */
    private void handleParamExtraData(String contentId, List<DBFieldMapper> mappers,
                                      Map<String, Object> mainContentMap) {
        // 处理固定表单固定额外参数
        String extraData = formContentDao.getExtraData(contentId);
        if (mappers != null && mappers.size() > 0 && extraData != null && !extraData.equals("")) {
            JSONObject jsonObject = JSONObject.parseObject(extraData);
            // json对象转Map
            Map<String, Object> map = (Map<String, Object>) jsonObject;
            for (DBFieldMapper fmap : mappers) {
                // 将额外数据固定参数填入mainContentMap
                if (fmap.getMapperType() == DBFieldMapperType.PARAM.value) {
                    if (map.containsKey(fmap.getAlias())) {
                        Object obj = map.get(fmap.getAlias());
                        // 1，字符串；2，Long
                        if (fmap.getDataType() == 2) {
                            try {
                                mainContentMap.put(fmap.getDbAlias(), Long.parseLong(obj.toString()));
                            } catch (Exception ex) {
                            }
                        } else
                            mainContentMap.put(fmap.getDbAlias(), obj.toString());
                    }
                }
            }
        }
    }

    /**
     * 检查填报是否已经结束
     *
     * @param oldData
     */
    private void ckFillEndDate(SmartFormContent oldData) {
        logger.info("ckFillEndDate-start");
        logger.info("getId：" + oldData.getId());
        logger.info("getFormId：" + oldData.getFormId());
        logger.info("getName：" + oldData.getName());
        logger.info("getPageList：" + oldData.getPageList());
        logger.info("截止填报时间：" + oldData.getEndDate());
        if (oldData.getEndDate() != null && oldData.getEndDate() > 0
                && oldData.getEndDate() < System.currentTimeMillis()) {
            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String tips = format1.format(new Date(oldData.getEndDate()));
            logger.info("填报时间已截止于:" + tips);
            throw new GlobalException(60412);
            //throw new SmartFormError("填报时间已截止于 " + tips + "，无法再提交");
        }
        logger.info("ckFillEndDate-end");
    }

    /**
     * 执行表单复杂规则
     *
     * @param contentId
     * @param origForm
     * @param pageId
     * @param timeSave  保存时机，0.非保存，1.保存前，2.保存后
     */
    private void executeFormRule(String contentId, SmartForm origForm, String pageId, int timeSave) {
        // 判定存在复杂规则
        if (origForm.getConditionRules() != null && origForm.getConditionRules().size() > 0) {
            // 要查询的组件表
            List<FormContentBaseDTO<List<String>>> componentList = new ArrayList<FormContentBaseDTO<List<String>>>();

            this.handleConditionWidget(origForm, componentList, pageId, timeSave);

            // 查询组件表
            if (componentList.size() > 0) {
                for (FormContentBaseDTO<List<String>> componentData : componentList) {
                    Map<String, Object> dataMap = componentDao.getContent(componentData.getDbTable(), contentId,
                            componentData.getWorkType(), componentData.getData());
                    // componentData.setQueryData(dataMap);
                    // 如果有查询结果
                    if (dataMap != null && dataMap.size() > 0) {
                        List<Object> objs = componentData.getContexts();
                        // 遍历规则
                        for (Object item : objs) {
                            if (item instanceof ConditionRuleContext) {
                                ConditionRuleContext context = (ConditionRuleContext) item;
                                // 是否条件满足
                                if (this.ckConditionSuccess(context, dataMap)) {
                                    // 应用规则
                                    this.handleConditionSuccess(context.getRule(), origForm, pageId, timeSave);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理条件成功
     *
     * @param rule
     * @param origForm
     * @param pageId
     * @param timeSave 保存时机，0.非保存，1.保存前，2.保存后
     */
    private void handleConditionSuccess(ConditionalRule rule, SmartForm origForm, String pageId, int timeSave) {
        if (rule.getWidgetList() != null && rule.getWidgetList().size() > 0) {
            for (ConditionalWidget conWidget : rule.getWidgetList()) {
                // 应用widget，保存后刷新分页状态时不对字段进行规则应用
                if (conWidget.getType() == FormFieldType.WIDGET.value) {
                    // 先判定是否是当前页
                    if (timeSave != 2 && pageId.equals(conWidget.getPageId())) {
                        this.useConditionWidget(conWidget, origForm);
                    }
                }
                // 应用组，分页设置
                else {
                    this.useConditionWidget(conWidget, origForm);
                }
            }
            // 移除不填的分页
            if (origForm.getPageList() != null) {
                for (int i = origForm.getPageList().size() - 1; i > 0; i--) {
                    FormPage page = origForm.getPageList().get(i);
                    if (page.getHide() != null && page.getHide()) {
                        origForm.getPageList().remove(i);
                    }
                }
            }
        }
    }

    /**
     * 检查上下文判定的复杂规则是否成立
     *
     * @param context
     * @param dataMap
     * @return
     */
    private boolean ckConditionSuccess(ConditionRuleContext context, Map<String, Object> dataMap) {
        WidgetBase widget = context.getWidget();
        Integer dataType = widget.getFieldType();
        // 判定规则条件是否满足
        ConditionalRule rule = context.getRule();

        if (dataType == WidgetType.NUMBER.value) {
            if (widget.getAlias() != null
                    || !widget.getAlias().equals("") && dataMap != null && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());
                BigDecimal decimal = handleBigDecimal(obj);
                try {
                    BigDecimal condition = new BigDecimal(rule.getCondition());
                    int result = decimal.compareTo(condition);
                    // 数字判定==，!=，<,>,>=,<=
                    if (rule.getRelationOperator() == OperatorType.EQUAL.value) {
                        return result == 0;
                    } else if (rule.getRelationOperator() == OperatorType.NOTEQUAL.value) {
                        return result != 0;
                    } else if (rule.getRelationOperator() == OperatorType.GREATERTHAN.value) {
                        return result == 1;
                    } else if (rule.getRelationOperator() == OperatorType.LESSTHAN.value) {
                        return result == -1;
                    } else if (rule.getRelationOperator() == OperatorType.GREATERTHANANDEQUAL.value) {
                        return result == 1 && result == 0;
                    } else if (rule.getRelationOperator() == OperatorType.LESSTHANANDEQUAL.value) {
                        return result == -1 && result == 0;
                    }
                } catch (Exception ex) {
                }
            }
        } else if (dataType == WidgetType.RADIO.value) {
            // 单选
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
                    && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());
                // 选择只判定==和!=
                if (rule.getRelationOperator() == OperatorType.EQUAL.value) {
                    return obj.toString().equals(rule.getCondition());
                } else if (rule.getRelationOperator() == OperatorType.NOTEQUAL.value) {
                    return !obj.toString().equals(rule.getCondition());
                }
            }
        } else if (dataType == WidgetType.CHECKBOX.value || dataType == WidgetType.SELECT.value) {
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
                    && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());
                if (obj.toString() != null && !obj.toString().equals("")) {
                    String[] listValue = obj.toString().split(",");
                    // 拆分Sql中逗号分割的ID
                    // List<String> defs = new ArrayList<String>();
                    boolean hasValue = false;
                    if (listValue != null && listValue.length > 0) {
                        for (String value : listValue) {
                            // defs.add(value);
                            hasValue = value.toString().equals(rule.getCondition());
                        }
                    }
                    if (rule.getRelationOperator() == OperatorType.EQUAL.value) {
                        return hasValue;
                    } else if (rule.getRelationOperator() == OperatorType.NOTEQUAL.value) {
                        return !hasValue;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 应用条件规则
     *
     * @param origForm
     */
    private void useConditionWidget(ConditionalWidget conWidget, SmartForm origForm) {
        if (origForm.getPageList() != null) {
            for (FormPage page : origForm.getPageList()) {
                // 应用分页
                if (conWidget.getType() == FormFieldType.PAGE.value) {
                    // 分页查找
                    if (page.getId().equals(conWidget.getPageId())) {
                        page.setHide(conWidget.getHide());
                        // 禁用旗下超级组件
                        if (conWidget.getDisable() != null && conWidget.getDisable()) {
                            if (page.getFieldList() != null) {
                                for (FormFieldBase group : page.getFieldList()) {
                                    Group superGroup = (Group) group;
                                    superGroup.setDisable(true);
                                }
                            }
                        }
                        return;
                    }
                    continue;
                }
                if (page.getHide() != null && page.getHide()) {
                    continue;
                }
                if (page.getFieldList() != null) {
                    for (FormFieldBase group : page.getFieldList()) {
                        if (group instanceof Group) {
                            Group superGroup = (Group) group;
                            // 应用超级组件设置
                            if (conWidget.getType() == FormFieldType.GROUP.value) {
                                if (superGroup.getId().equals(conWidget.getGroupId())) {
                                    superGroup.setHide(conWidget.getHide());
                                    superGroup.setDisable(conWidget.getDisable());
                                    return;
                                }
                            }
                            if (superGroup.getHide() != null && superGroup.getHide()) {
                                continue;
                            }
                            if (superGroup.getFieldList() != null) {
                                for (FormFieldBase gfield : superGroup.getFieldList()) {
                                    if (gfield instanceof Group) {
                                        Group tableGroup = (Group) gfield;
                                        // 应用组件设置
                                        if (conWidget.getType() == FormFieldType.GROUP.value) {
                                            if (tableGroup.getId().equals(conWidget.getGroupId())) {
                                                tableGroup.setHide(conWidget.getHide());
                                                tableGroup.setDisable(conWidget.getDisable());
                                                return;
                                            }
                                        }
                                        // 应用字段设置
                                        else if (conWidget.getType() == FormFieldType.WIDGET.value) {
                                            if (tableGroup.getFieldList() != null) {
                                                // 样式组
                                                for (FormFieldBase field : tableGroup.getFieldList()) {
                                                    if (field instanceof WidgetBase) {
                                                        if (((WidgetBase) field).getId()
                                                                .equals(conWidget.getField().getId())) {
                                                            this.useConditionWidgetRule(conWidget, (WidgetBase) field);
                                                        }
                                                    }
                                                }
                                            } else if (tableGroup.getSteerable() != null && tableGroup.getSteerable()
                                                    && tableGroup.getLineList() != null) {
                                                // 汇总表格
                                                for (GroupLine line : tableGroup.getLineList()) {
                                                    // 遍历行字段
                                                    if (line.getFieldList() != null && line.getFieldList().size() > 0
                                                            && line.getLineNum() == conWidget.getLineNum()) {
                                                        for (WidgetBase widget : line.getFieldList()) {
                                                            if (widget.getId().equals(conWidget.getField().getId())) {
                                                                this.useConditionWidgetRule(conWidget, widget);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (tableGroup.getOriginalLine() != null) {
                                                GroupLine line = tableGroup.getOriginalLine();
                                                // 普通表格
                                                if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                                                    for (WidgetBase widget : line.getFieldList()) {
                                                        if (widget.getId().equals(conWidget.getField().getId())) {
                                                            this.useConditionWidgetRule(conWidget, widget);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (gfield instanceof WidgetBase) {
                                        WidgetBase field = (WidgetBase) gfield;
                                        // 应用字段设置
                                        if (conWidget.getType() == FormFieldType.WIDGET.value) {
                                            if (field.getId().equals(conWidget.getField().getId())) {
                                                this.useConditionWidgetRule(conWidget, field);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 应用字段级别的规则
     *
     * @param conWidget
     * @param field
     */
    private void useConditionWidgetRule(ConditionalWidget conWidget, WidgetBase field) {
        field.setDisable(conWidget.getDisable());
        field.setHide(conWidget.getHide());
        Integer dataType = field.getFieldType();
        // 应用规则
        if (conWidget.getField().getDefValue() != null) {
            if (dataType == WidgetType.NUMBER.value) {
                try {
                    BigDecimal defValue = new BigDecimal(conWidget.getField().getDefValue());
                    WidgetNumber number = (WidgetNumber) field;
                    number.setDefValue(defValue);
                } catch (Exception ex) {
                }
            } else if (dataType == WidgetType.INPUT.value || dataType == WidgetType.TEXTAREA.value) {
                WidgetInput input = (WidgetInput) field;
                input.setDefValue(conWidget.getField().getDefValue());
            }
        }
        // 设置SQL语句规则
        if (conWidget.getField().getSqlStr() != null) {
            if (dataType == WidgetType.NUMBER.value) {
                WidgetNumber number = (WidgetNumber) field;
                number.setUseSql(true);
                number.setSqlStr(conWidget.getField().getSqlStr());
            }
        }
        // 设置字段规则
        if (conWidget.getField().getRules() != null && conWidget.getField().getRules().size() > 0) {
            List<RuleBase> conditionRules = conWidget.getField().getRules();
            // 直接启用条件中的规则
            field.setRules(conditionRules);
        }
    }

    /**
     * 处理条件字段，筛选出要查询的条件字段
     *
     * @param origForm
     * @param componentList
     * @param pageId
     * @param timeSave      保存时机，0.非保存，1.保存前，2.保存后
     */
    private void handleConditionWidget(SmartForm origForm, List<FormContentBaseDTO<List<String>>> componentList,
                                       String pageId, int timeSave) {
        for (ConditionalRule rule : origForm.getConditionRules()) {
            // 保存分页前，只应用非本分页条件字段的规则
            if (timeSave == 1 && rule.getPageId().equals(pageId)) {
                continue;
            }
            if (origForm.getPageList() != null) {
                for (FormPage page : origForm.getPageList()) {
                    // 分页查找
                    if (page.getId().equals(rule.getPageId()) && page.getFieldList() != null) {
                        for (FormFieldBase group : page.getFieldList()) {
                            if (group instanceof Group) {
                                Group superGroup = (Group) group;
                                // 超级组件查找
                                if (superGroup.getId().equals(rule.getGroupId()) && superGroup.getFieldList() != null) {
                                    for (FormFieldBase gfield : superGroup.getFieldList()) {
                                        if (gfield instanceof Group) {
                                            Group tableGroup = (Group) gfield;
                                            if (tableGroup.getFieldList() != null) {
                                                // 样式组
                                                for (FormFieldBase field : tableGroup.getFieldList()) {
                                                    if (field instanceof WidgetBase) {
                                                        if (((WidgetBase) field).getId().equals(rule.getFieldId())) {
                                                            this.setConditionWidgetDto(rule, (WidgetBase) field,
                                                                    superGroup, componentList);
                                                        }
                                                    }
                                                }
                                            } else if (tableGroup.getSteerable() != null && tableGroup.getSteerable()
                                                    && tableGroup.getLineList() != null) {
                                                // 汇总表格
                                                for (GroupLine line : tableGroup.getLineList()) {
                                                    // 遍历行字段
                                                    if (line.getFieldList() != null && line.getFieldList().size() > 0
                                                            && line.getLineNum() == rule.getLineNum()) {
                                                        for (WidgetBase widget : line.getFieldList()) {
                                                            // 处理超级组件旗下字段
                                                            if (widget instanceof WidgetBase && ((WidgetBase) widget)
                                                                    .getId().equals(rule.getFieldId())) {
                                                                this.setConditionWidgetDto(rule, (WidgetBase) widget,
                                                                        tableGroup, componentList);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (gfield instanceof WidgetBase) {
                                            WidgetBase field = (WidgetBase) gfield;
                                            if (field.getId().equals(rule.getFieldId())) {
                                                this.setConditionWidgetDto(rule, field, superGroup, componentList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置条件字段查询列
     *
     * @param widget
     * @param group
     * @param componentList
     */
    private void setConditionWidgetDto(ConditionalRule rule, WidgetBase widget, Group group,
                                       List<FormContentBaseDTO<List<String>>> componentList) {
        FormContentBaseDTO<List<String>> componentDTO = null;
        for (FormContentBaseDTO<List<String>> item : componentList) {
            if (item.getDbTable().equals(group.getTable()) && item.getWorkType().equals(group.getWorkType())) {
                componentDTO = item;
            }
        }
        // 初始化组件存储对象
        if (componentDTO == null) {
            componentDTO = new FormContentBaseDTO<List<String>>();
            componentDTO.setData(new ArrayList<String>());
            componentDTO.setDbTable(group.getTable());
            componentDTO.setWorkType(group.getWorkType());
            componentDTO.setContexts(new ArrayList<Object>());
            componentList.add(componentDTO);
        }
        // 将规则加入上下文
        ConditionRuleContext context = new ConditionRuleContext();
        context.setRule(rule);
        context.setWidget(widget);
        componentDTO.getContexts().add(context);
        // 添加查询列
        componentDTO.getData().add(widget.getAlias());
    }

    /**
     * 根据内容ID获取主表基本数据，获取状态，修改/添加时间等
     *
     * @param id
     * @return
     */
    private SmartFormContent getMainContentBase(String dbName, String dbTable, String id, List<DBFieldMapper> mapper) {
        logger.info("getMainContentBase--start");
        logger.info("dbName:" + dbName);
        logger.info("dbTable:" + dbTable);
        logger.info("id:" + id);
        List<String> columns = null;
        String endDate = null;
        // 判定是否有从主表读取结束时间
        if (mapper != null) {
            logger.info("mapper:" + mapper);
            endDate = this.getMapperTable(mapper, DBFieldMapperType.ENDDATE);
            logger.info("endDate:" + endDate);
            if (endDate != null && !endDate.equals("")) {
                columns = new ArrayList<String>();
                columns.add(endDate);
            }
        }
        logger.info("columns:" + columns);
        // 切换数据源
        Map<String, Object> dataMap;
        try {
            DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
            dataMap = contentMainDao.getContent(dbTable, id, columns);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        logger.info("dataMap:" + dataMap);
        //获取project_main表中的一些字段
        if (dataMap != null) {
            SmartFormContent data = new SmartFormContent();
            data.setId(MapUtils.getString(dataMap, "id"));
            data.setFormId(MapUtils.getString(dataMap, "form_id"));
            data.setCreatedAt(MapUtils.getLong(dataMap, "created_at"));
            data.setModifiedAt(MapUtils.getLong(dataMap, "modified_at"));
            data.setState(handleInteger(dataMap.get("state")));
            data.setReasonInfo(MapUtils.getString(dataMap, "refuse_info"));
            // 设置填报截止日期
            if (endDate != null && dataMap.containsKey(endDate)) {
                Long time = handleLong(dataMap.get(endDate));
                data.setEndDate(time);
            }
            logger.info("getMainContentBase--end");
            return data;
        }
        logger.info("dataMap: null");
        return null;
    }

    private FormPage getPageContentById(SmartForm smartform, FormPage page, String contentId) throws ParseException {
        if (page != null) {
            //当contentid为空时，给定默认值，防止查出多条
            if(contentId==null){
                contentId="";
            }
            // 优先执行表单复杂规则
            //this.executeFormRule(data.getId(), orgForm, pageId, 0);
            if (page.getHide() != null && page.getHide()) {
                throw new GlobalException(60415);
            }

            // 状态表名
            String stateTable = this.getMapperTable(smartform.getFieldMapperList(), DBFieldMapperType.STATETABLE);
            // 上传表名
            String uploadTable = this.getMapperTable(smartform.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

            // 获取分页中要处理的组件
            List<FormContentBaseDTO<Group>> components = this.getPageComponents(page, null, true);
            if (components == null || components.size() == 0) {
                return page;
            }
            // 处理组件状态
            //this.handleGroupState(data, stateTable, components, pageList, nowPage);
            //this.handleNowPage(pageList, nowPage, justPageState);
//			if (justPageState) {
//				// 将表单分页交给内容实体
//				data.setPageList(pageList);
//				return data;
//			}
            // 组件状态
            List<ComponentStateEntity> componentStates = this.getComponentState(stateTable, contentId);
            // 读取分页状态
            setPageState(page, componentStates);
            // 处理固定行
            this.firstFixedLine(contentId, page, stateTable);

            // 要查询的主表字段
            List<String> mainColumns = new ArrayList<String>();
            // 要查询的组件表
            List<FormContentBaseDTO<List<String>>> componentList = new ArrayList<FormContentBaseDTO<List<String>>>();
            // 要查询的表格表
            List<FormContentBaseDTO<List<String>>> tableList = new ArrayList<FormContentBaseDTO<List<String>>>();
            // 要查询的上传列表
            List<FormContentBaseDTO<String>> uploadList = new ArrayList<FormContentBaseDTO<String>>();
            // 要执行的数字框SQL查询
            List<WidgetNumber> sqlList = new ArrayList<WidgetNumber>();

            // 遍历要存储的组件
            this.handlQueryPageDTO(components, smartform, contentId, mainColumns, componentList, tableList, uploadList,
                    sqlList);

            // 查询组件表
            if (componentList.size() > 0) {
                for (FormContentBaseDTO<List<String>> componentData : componentList) {
                    System.out.println("=======contentId:"+contentId);
                    Map<String, Object> dataMap = componentDao.getContent(componentData.getDbTable(), contentId,
                            componentData.getWorkType(), componentData.getData());
                    componentData.setQueryData(dataMap);
                }
            }
            // 查询表格表
            if (tableList.size() > 0) {
                for (FormContentBaseDTO<List<String>> tableData : tableList) {
                    List<Map<String, Object>> dataMaps = tableDao.getContentList(tableData.getDbTable(), contentId,
                            tableData.getWorkType(), tableData.getData());
                    tableData.setQueryData(dataMaps);
                }
            }
            // 查询上传表
            if (uploadList.size() > 0) {
                for (FormContentBaseDTO<String> uploadData : uploadList) {
                    List<FormContentUploadsEntity> dataMaps = uploadsDao.getContentList(uploadTable, contentId,
                            uploadData.getDbTable(), uploadData.getWorkType(), null, null);
                    uploadData.setQueryData(dataMaps);
                }
            }

            // 要执行的数字框SQL查询
            if (sqlList.size() > 0) {
                List<String> array = new ArrayList<String>();
                for (WidgetNumber sql : sqlList) {
                    array.add(sql.getSqlStr());
                }
                List<Object> numberList = contentMainDao.customSelect(array, contentId);
                int i = 0;
                // 将查询到的值赋值给表单原始字段
                for (Object value : numberList) {
                    WidgetNumber number = sqlList.get(i);
                    number.setDefValue(this.handleBigDecimal(value));
                    i++;
                }
            }
            Map<String, Object> mainDataMap = null;
            // 查询主表
            if (mainColumns.size() > 0) {
                mainDataMap = contentMainDao.getContent(smartform.getTableName(), contentId, mainColumns);
            }
            // 填充查询数据
            this.fillQueryPage(smartform, page, contentId, mainDataMap, componentList, tableList, uploadList);

            // 将表单分页交给内容实体
            //data.setPageList(pageList);
        }


        return page;
    }

    /**
     * 获取表单配置中的表名
     *
     * @param mapper
     * @param type
     * @return
     */
    private String getMapperTable(List<DBFieldMapper> mapper, DBFieldMapperType type) {
        String table = null;
        if (mapper != null) {
            for (DBFieldMapper fmap : mapper) {
                if (fmap.getMapperType() == type.value) {
                    table = fmap.getDbAlias();
                }
            }
        }
        return table;
    }

    /**
     * 获取当前分页中的超级组件和表格组
     *
     * @param nowPage
     * @param hasGridView 是否包含GridView
     * @return
     */
    private List<FormContentBaseDTO<Group>> getPageComponents(FormPage nowPage, Map<String, ConditionalWidget> replaceWidgets, boolean hasGridView) {
        // 获取当前分页内的组件列表
        List<FormContentBaseDTO<Group>> components = new ArrayList<FormContentBaseDTO<Group>>();
        List<FormFieldBase> pageFields = nowPage.getFieldList();

        // 遍历分页旗下的超级组件
        if (pageFields != null) {
            for (FormFieldBase field : pageFields) {
                if (field instanceof Group) {
                    Group group = (Group) field;
                    // 查找替换信息
                    if (replaceWidgets != null && replaceWidgets.containsKey(group.getId())) {
                        ConditionalWidget conditionalWidget = replaceWidgets.get(group.getId());
                        group.setHide(conditionalWidget.getHide());
                        group.setDisable(conditionalWidget.getDisable());
                    }
                    // 组件隐藏，则无需处理
                    if (group.getHide() != null && group.getHide())
                        continue;
                    if (group.getGroupType() == GroupType.SUPER.value) {
                        // 遍历超级组件旗下是否还有表格组    表单填充数据的集合
                        List<FormFieldBase> groupFields = group.getFieldList();
                        if (groupFields != null) {
                            boolean onlyGroup = true;
                            for (FormFieldBase gfield : groupFields) {
                                if (gfield instanceof Group) {
                                    Group tableGroup = (Group) gfield;
                                    // 查找替换信息
                                    if (replaceWidgets != null && replaceWidgets.containsKey(tableGroup.getId())) {
                                        ConditionalWidget conditionalWidget = replaceWidgets.get(tableGroup.getId());
                                        tableGroup.setHide(conditionalWidget.getHide());
                                        tableGroup.setDisable(conditionalWidget.getDisable());
                                    }
                                    // 组件隐藏，则无需处理
                                    if (tableGroup.getHide() != null && tableGroup.getHide())
                                        continue;
                                    if (tableGroup.getGroupType() == GroupType.TABLE.value) {
                                        // 排除GridView模式表格，此模式有单独的存取接口
                                        if (hasGridView || !tableGroup.hasGridView()) {
                                            FormContentBaseDTO<Group> component = new FormContentBaseDTO<Group>();
                                            component.setDbName(tableGroup.getDbName());
                                            component.setDbTable(tableGroup.getTable());
                                            int workType = tableGroup.getWorkType() == null ? 0
                                                    : tableGroup.getWorkType();
                                            tableGroup.setWorkType(workType);
                                            component.setWorkType(workType);
                                            component.setData(tableGroup);
                                            components.add(component);
                                        }
                                    } else if (tableGroup.getGroupType() == GroupType.STYLE.value) {
                                        onlyGroup = false;
                                    } else if (tableGroup.getGroupType() == GroupType.GRID.value) {
                                        onlyGroup = false;
                                    }
                                } else {
                                    onlyGroup = false;
                                }
                            }
                            // 超级组件中不只有表格组,否则不对超级组件进行单独存储
                            if (!onlyGroup) {
                                FormContentBaseDTO<Group> component = new FormContentBaseDTO<Group>();
                                component.setDbName(group.getDbName());
                                component.setDbTable(group.getTable());
                                int workType = group.getWorkType() == null ? 0 : group.getWorkType();
                                group.setWorkType(workType);
                                component.setWorkType(workType);
                                component.setData(group);
                                components.add(component);
                            }
                        }
                    }
                }
            }
        }
        return components;
    }

    /**
     * 判定是否首次填写固定行
     *
     * @param contentId
     * @param nowPage
     * @throws ParseException
     */
    @Transactional
    private void firstFixedLine(String contentId, FormPage nowPage, String stateTable) throws ParseException {
        if (nowPage.getFieldList() != null) {
            for (FormFieldBase field : nowPage.getFieldList()) {
                if (field instanceof Group) {
                    Group group = (Group) field;
                    if (group.getGroupType() == GroupType.SUPER.value) {
                        // 遍历超级组件
                        List<FormFieldBase> groupFields = group.getFieldList();
                        if (groupFields != null) {
                            for (FormFieldBase gfield : groupFields) {
                                if (gfield instanceof Group) {
                                    Group tableGroup = (Group) gfield;
                                    // 表格存在固定行，并且没有填，则自动添加固定行
                                    if (tableGroup.getGroupType() == GroupType.TABLE.value
                                            && tableGroup.getFixedLineList() != null
                                            && tableGroup.getFixedLineList().size() > 0
                                            && tableGroup.getState() == ContentStateType.UNFILL.value) {
                                        // 执行固定行加入
                                        this.addFixedLine(contentId, tableGroup, null);
                                        // 存储组件状态
                                        ComponentStateEntity comState = new ComponentStateEntity();
                                        comState.setId(UUIDUtil.getNextId());
                                        comState.setDbTable(stateTable);
                                        comState.setContentId(contentId);
                                        comState.setTableName(tableGroup.getTable());
                                        comState.setWorkType(tableGroup.getWorkType());
                                        comState.setCreatedAt(System.currentTimeMillis());
                                        comState.setModifiedAt(System.currentTimeMillis());
                                        comState.setState(ContentStateType.STORAGE.value);
                                        this.componentStateDao.saveContent(comState, stateTable);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理查询分页所需要的DTO
     *
     * @param components
     * @param orgForm
     * @param contentId
     * @param mainColumns
     * @param componentList
     * @param tableList
     * @param uploadList
     * @throws ParseException
     */
    private void handlQueryPageDTO(List<FormContentBaseDTO<Group>> components, SmartForm orgForm, String contentId,
                                   List<String> mainColumns, List<FormContentBaseDTO<List<String>>> componentList,
                                   List<FormContentBaseDTO<List<String>>> tableList, List<FormContentBaseDTO<String>> uploadList,
                                   List<WidgetNumber> sqlList) throws ParseException {
        for (FormContentBaseDTO<Group> component : components) {
            // 获取组件内容
            Group group = component.getData();
            if (group.getGroupType() == GroupType.SUPER.value) {
                // 组件表是否跟主表公用
                boolean inMainTable = group.getDbName().equals(orgForm.getDbName())
                        && group.getTable().equals(orgForm.getTableName());
                // 处理超级组件
                for (FormFieldBase widget : group.getFieldList()) {
                    if (widget instanceof Group) {
                        // 处理超级组件旗下的样式组
                        Group styleGroup = (Group) widget;
                        if (styleGroup.getGroupType() == GroupType.STYLE.value) {
                            // 遍历样式组字段
                            for (FormFieldBase styleWidget : styleGroup.getFieldList()) {
                                if (styleWidget instanceof WidgetBase) {
                                    this.handleQueryFieldDTO(inMainTable, false, contentId, group,
                                            (WidgetBase) styleWidget, mainColumns, componentList, tableList, uploadList,
                                            sqlList);
                                }
                            }
                        }
                    } else if (widget instanceof WidgetBase) {
                        this.handleQueryFieldDTO(inMainTable, false, contentId, group, (WidgetBase) widget, mainColumns,
                                componentList, tableList, uploadList, sqlList);
                    }
                }
            } else if (group.getGroupType() == GroupType.TABLE.value) {
                // 处理表格
                if (group.getSteerable() == null || !group.getSteerable()) {
                    // 遍历表格原始字段
                    if (group.getOriginalLine() != null) {
                        GroupLine line = group.getOriginalLine();
                        if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                            for (WidgetBase widget : line.getFieldList()) {
                                if (widget instanceof WidgetBase) {
                                    this.handleQueryFieldDTO(false, true, contentId, group, (WidgetBase) widget,
                                            mainColumns, componentList, tableList, uploadList, sqlList);
                                }
                            }
                        }
                        // 清理固定行信息
                        group.setFixedLineList(null);
                    }
                } else {
                    // 处理汇总表格
                    // 组件表是否跟主表公用
                    boolean inMainTable = group.getDbName().equals(orgForm.getDbName())
                            && group.getTable().equals(orgForm.getTableName());
                    if (group.getLineList() != null) {
                        for (GroupLine line : group.getLineList()) {
                            // 遍历组字段
                            if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                    && line.getFieldList().size() > 0) {
                                for (WidgetBase widget : line.getFieldList()) {
                                    if (widget instanceof WidgetBase) {
                                        this.handleQueryFieldDTO(inMainTable, false, contentId, group,
                                                (WidgetBase) widget, mainColumns, componentList, tableList, uploadList,
                                                sqlList);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 向表单分页中填充查询到的数据
     *
     * @param nowPage
     * @param mainDataMap
     * @param componentList
     * @param tableList
     * @param uploadList
     * @throws ParseException
     */
    private void fillQueryPage(SmartForm orgForm, FormPage nowPage, String contentId, Map<String, Object> mainDataMap,
                               List<FormContentBaseDTO<List<String>>> componentList, List<FormContentBaseDTO<List<String>>> tableList,
                               List<FormContentBaseDTO<String>> uploadList) throws ParseException {
        // 获取当前分页内的组件列表
        List<FormFieldBase> pageFields = nowPage.getFieldList();
        // 遍历分页旗下的超级组件
        if (pageFields != null) {
            for (FormFieldBase field : pageFields) {
                if (field instanceof Group) {
                    Group group = (Group) field;
                    // 组件表是否跟主表公用
                    boolean inMainTable = group.getDbName().equals(orgForm.getDbName())
                            && group.getTable().equals(orgForm.getTableName());
                    if (group.getGroupType() != GroupType.SUPER.value) {
                        continue;
                    }
                    // 遍历超级组件旗下是否还有表格组
                    List<FormFieldBase> groupFields = group.getFieldList();
                    if (groupFields == null) {
                        continue;
                    }
                    for (FormFieldBase gfield : groupFields) {
                        if (gfield instanceof Group) {
                            Group tableGroup = (Group) gfield;
                            if (tableGroup.getGroupType() == GroupType.TABLE.value) {
                                // 是否是可控表格
                                if (tableGroup.getSteerable() == null || !tableGroup.getSteerable()) {
                                    // 遍历表格原始字段
                                    if (tableGroup.getOriginalLine() == null) {
                                        continue;
                                    }
                                    GroupLine origline = tableGroup.getOriginalLine();

                                    if (origline.getFieldList() != null && origline.getFieldList().size() > 0) {
                                        // 填充g表格数据
                                        this.fillQueryTable(tableGroup, tableList, origline, uploadList);
                                    }
                                    if (CheckDataUtil.isNull(tableGroup.getGridViewRules())) {
                                        if (CheckDataUtil.isNull(tableGroup.getLineList())) {
                                            List<GroupLine> gl = new ArrayList<GroupLine>();
                                            gl.add(origline);
                                            tableGroup.setLineList(gl);
                                        }
                                        tableGroup.setOriginalLine(null);
                                    }


                                } else {
                                    // 处理汇总表格
                                    boolean inMainTableGroup = tableGroup.getDbName().equals(orgForm.getDbName())
                                            && tableGroup.getTable().equals(orgForm.getTableName());
                                    if (tableGroup.getLineList() != null) {
                                        for (GroupLine line : tableGroup.getLineList()) {
                                            // 遍历组字段
                                            if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                                                for (WidgetBase widget : line.getFieldList()) {
                                                    if (widget instanceof WidgetBase) {
                                                        this.fillQueryField(inMainTableGroup, contentId, tableGroup,
                                                                (WidgetBase) widget, null, mainDataMap, componentList,
                                                                uploadList);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (tableGroup.getGroupType() == GroupType.STYLE.value) {
                                if (tableGroup.getFieldList() == null) {
                                    continue;
                                }
                                // 遍历样式组字段
                                for (FormFieldBase styleWidget : tableGroup.getFieldList()) {
                                    if (styleWidget instanceof WidgetBase) {
                                        this.fillQueryField(inMainTable, contentId, group, (WidgetBase) styleWidget,
                                                null, mainDataMap, componentList, uploadList);
                                    }
                                }
                            }
                        } else if (gfield instanceof WidgetBase) {
                            this.fillQueryField(inMainTable, contentId, group, (WidgetBase) gfield, null, mainDataMap,
                                    componentList, uploadList);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Map<String, String> smartFormData(String id, String pageId) {
        return null;
    }

    /**
     * 获取组中选择控件的选项源
     *
     * @param group
     */
    private void getGroupSelectOptions(Group group, Set<String> optionsIDs) {
        if (group.getGroupType() == null || group.getGroupType() == GroupType.TABLE.value) {
            if (group.getLineList() != null && group.getLineList().size() > 0) {
                for (GroupLine line : group.getLineList()) {
                    // 遍历组字段
                    if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                        for (WidgetBase widget : line.getFieldList()) {
                            this.getSelectOptions(widget, optionsIDs);
                        }
                    }
                }
            } else if (group.getOriginalLine() != null) {
                GroupLine line = group.getOriginalLine();
                // 遍历组字段
                if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                    for (WidgetBase widget : line.getFieldList()) {
                        this.getSelectOptions(widget, optionsIDs);
                    }
                }
            }
        } else {
            // 遍历超级组件/样式组字段
            if (CheckDataUtil.isNotNull(group.getFieldList())) {
                for (FormFieldBase widget : group.getFieldList()) {
                    if (group.getGroupType() == GroupType.SUPER.value) {
                        if (widget instanceof Group) {
                            Group nowGroup = (Group) widget;
                            this.getGroupSelectOptions(nowGroup, optionsIDs);
                        } else {
                            this.getSelectOptions((WidgetBase) widget, optionsIDs);
                        }
                    } else {
                        this.getSelectOptions((WidgetBase) widget, optionsIDs);
                    }
                }
            }
            // 遍历栅格组
            if (CheckDataUtil.isNotNull(group.getRowList())) {

                for (GroupRow row : group.getRowList()) {
                    if (CheckDataUtil.isNotNull(row.getFieldList())) {
                        for (FormFieldBase widget : row.getFieldList()) {
                            this.getSelectOptions((WidgetBase) widget, optionsIDs);
                        }
                    }

                }
            }
        }
    }

    /**
     * 添加固定行,加入表格固定行
     *
     * @param group  要加的组
     * @param hasIds 已经有的固定行
     * @throws ParseException
     */
    private void addFixedLine(String contentId, Group group, HashMap<String, Boolean> hasIds) throws ParseException {
        // 处理表格
        if (group.getFixedLineList() != null && group.getFixedLineList().size() > 0) {
            // 拼接handleFieldDTO方法需要的格式
            FormContentBaseDTO<Group> component = new FormContentBaseDTO<Group>();
            component.setDbName(group.getDbName());
            component.setDbTable(group.getTable());
            int workType = group.getWorkType() == null ? 0 : group.getWorkType();
            group.setWorkType(workType);
            component.setWorkType(workType);
            component.setData(group);

            // 拼接handleFieldDTO方法需要的table存储对象
            FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = new FormContentBaseDTO<List<FormContentTableEntity>>();
            List<FormContentTableEntity> entity = new ArrayList<FormContentTableEntity>();
            tableDTO.setData(entity);
            tableDTO.setDbName(component.getDbName());
            tableDTO.setDbTable(component.getDbTable());
            tableDTO.setWorkType(component.getWorkType());
            // map添加
            tableDTO.setContent(new Query());

            int lineNum = 0;
            for (GroupLine line : group.getFixedLineList()) {
                // 确定固定行有内容，并且没有存在数据库
                if (line.getId() != null && line.getFieldList() != null && line.getFieldList().size() > 0
                        && (hasIds == null || !hasIds.containsKey(line.getId()))) {
                    lineNum++;
                    // 创建存储行
                    FormContentTableEntity lineDTO = new FormContentTableEntity();
                    String uuid = UUIDUtil.getNextId();
                    lineDTO.setId(uuid);
                    lineDTO.setContentId(contentId);
                    lineDTO.setLineNum(lineNum);
                    lineDTO.setFixedId(line.getId());
                    lineDTO.setSort(lineNum);
                    lineDTO.setWorkType(component.getWorkType());
                    lineDTO.setCreatedAt(System.currentTimeMillis());
                    lineDTO.setModifiedAt(System.currentTimeMillis());
                    lineDTO.setState(ContentStateType.STORAGE.value);
                    Query query = new Query();
                    // map添加
                    lineDTO.setContent(query);

                    tableDTO.getData().add(lineDTO);

                    for (WidgetBase widget : line.getFieldList()) {
                        if (widget instanceof WidgetBase) {
                            if (widget.getHide() != null && !widget.getHide()) {
                                this.handleFieldDTO(false, true, contentId, lineDTO, component, (WidgetBase) widget,
                                        null, null, null, null, null, null, true);
                            }
                        }
                    }
                }
            }
            if (lineNum != 0) {
                // 批量添加表格数据
                tableDao.batchSave(tableDTO.getData(), tableDTO.getDbTable());
            }
        }
    }

    /**
     * 将field填充到存储实体中
     *
     * @param inMainTable   是否存到主表
     * @param hasTable      是否是存到表格中
     * @param contentId     表单内容ID
     * @param widget
     * @param componentList
     * @param tableList
     * @param uploadList
     * @throws ParseException
     */
    private void handleQueryFieldDTO(boolean inMainTable, boolean hasTable, String contentId, Group group,
                                     WidgetBase widget, List<String> mainColumns, List<FormContentBaseDTO<List<String>>> componentList,
                                     List<FormContentBaseDTO<List<String>>> tableList, List<FormContentBaseDTO<String>> uploadList,
                                     List<WidgetNumber> sqlList) throws ParseException {
        // 越过空别名字段
        if (widget.getFieldType() != WidgetType.UPLOAD.value
                && (widget.getAlias() == null || widget.getAlias().equals("")))
            return;

        String endAlias = null;
        if (widget.getFieldType() == WidgetType.UPLOAD.value) {
            // 上传
            FormContentBaseDTO<String> uploadDTO = null;
            for (FormContentBaseDTO<String> item : uploadList) {
                if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
                        && item.getWorkType().equals(group.getWorkType())) {
                    uploadDTO = item;
                }
            }
            if (uploadDTO == null) {
                uploadDTO = new FormContentBaseDTO<String>();
                uploadDTO.setDbName(group.getDbName());
                uploadDTO.setDbTable(group.getTable());
                uploadDTO.setWorkType(group.getWorkType());
                uploadList.add(uploadDTO);
            }
            return;
        } else if (widget.getFieldType() == WidgetType.DATESPAN.value) {
            // 日期
            WidgetDate vData = (WidgetDate) widget;
            endAlias = vData.getEndAlias();
        } else if (widget.getFieldType() == WidgetType.TIMESPAN.value) {
            // 时间
            WidgetTime vData = (WidgetTime) widget;
            endAlias = vData.getEndAlias();
        } else if (widget.getFieldType() == WidgetType.NUMBER.value) {
            // 数字框
            WidgetNumber vData = (WidgetNumber) widget;
            // 如果数字框启用了Sql查询
            if (vData.getUseSql() != null && vData.getUseSql() && vData.getSqlStr() != null
                    && !vData.getSqlStr().equals("")) {
                sqlList.add(vData);
                return;
            }
        }
        // 是否从主表查询
        if (inMainTable)
            mainColumns.add(widget.getAlias());
        else if (!hasTable) {
            // 查找是否已经存在组件存储DTO
            FormContentBaseDTO<List<String>> componentDTO = null;
            for (FormContentBaseDTO<List<String>> item : componentList) {
                if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
                        && item.getWorkType().equals(group.getWorkType())) {
                    componentDTO = item;
                }
            }
            // 初始化组件存储对象
            if (componentDTO == null) {
                componentDTO = new FormContentBaseDTO<List<String>>();
                componentDTO.setData(new ArrayList<String>());
                componentDTO.setDbName(group.getDbName());
                componentDTO.setDbTable(group.getTable());
                componentDTO.setWorkType(group.getWorkType());
                componentList.add(componentDTO);
            }
            // 添加查询列
            componentDTO.getData().add(widget.getAlias());
            if (endAlias != null && !endAlias.equals(""))
                componentDTO.getData().add(endAlias);
        } else {
            // 查找是否已经存在组件存储DTO
            FormContentBaseDTO<List<String>> tableDTO = null;
            for (FormContentBaseDTO<List<String>> item : tableList) {
                if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
                        && item.getWorkType().equals(group.getWorkType())) {
                    tableDTO = item;
                }
            }
            // 初始化组件存储对象
            if (tableDTO == null) {
                tableDTO = new FormContentBaseDTO<List<String>>();
                tableDTO.setData(new ArrayList<String>());
                tableDTO.setDbName(group.getDbName());
                tableDTO.setDbTable(group.getTable());
                tableDTO.setWorkType(group.getWorkType());
                tableList.add(tableDTO);
            }
            // 添加查询列
            tableDTO.getData().add(widget.getAlias());
            if (endAlias != null && !endAlias.equals(""))
                tableDTO.getData().add(endAlias);
        }
    }

    /**
     * 将查询到的数据填充至表格
     *
     * @param tableGroup
     * @param tableList
     * @param origline
     * @param uploadList
     * @throws ParseException
     */
    private void fillQueryTable(Group tableGroup, List<FormContentBaseDTO<List<String>>> tableList, GroupLine origline,
                                List<FormContentBaseDTO<String>> uploadList) throws ParseException {
        // 查找查询到的表格数据
        FormContentBaseDTO<List<String>> tableData = null;
        for (FormContentBaseDTO<List<String>> item : tableList) {
            if (item.getDbName().equals(tableGroup.getDbName()) && item.getDbTable().equals(tableGroup.getTable())
                    && item.getWorkType().equals(tableGroup.getWorkType())) {
                tableData = item;
            }
        }
        if (tableData != null && tableData.getQueryData() != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dataMaps = (List<Map<String, Object>>) tableData.getQueryData();
            List<GroupLine> lineList = new ArrayList<GroupLine>();
            for (Map<String, Object> line : dataMaps) {
                GroupLine lineData = new GroupLine();
                lineData.setId((String) line.get("id"));
                lineData.setLineNum(handleInteger(line.get("line_num")));
                // 增加固定行ID和状态信息
                if (line.get("fixed_id") != null)
                    lineData.setDataId(String.valueOf(line.get("fixed_id")));
                lineData.setState(handleInteger(line.get("state")));

                lineData.setLineType(GroupLineType.COMMON.value);
                List<WidgetBase> fieldList = new ArrayList<WidgetBase>();
                lineData.setFieldList(fieldList);
                for (WidgetBase origWidget : origline.getFieldList()) {
                    WidgetBase newWidget = null;
                    if (origWidget.getFieldType() == WidgetType.UPLOAD.value) {
                        newWidget = this.getQueryUploadWidget(uploadList, tableGroup, lineData.getId(), origWidget,
                                true);
                    } else {
                        if (CheckDataUtil.isNull(tableGroup.getGridViewRules())) {
                            // 普通表格
                            newWidget = this.getQueryWidget(line, origWidget, false);
                        } else {
                            // gridview表格
                            newWidget = this.getQueryWidget(line, origWidget, true);
                        }
                    }
                    if (newWidget != null)
                        lineData.getFieldList().add(newWidget);
                }
                // 赋值创建时间
                lineData.setCreatedAt(Long.valueOf(String.valueOf(line.get("created_at"))));
                // 赋值排序
                if (line.containsKey("sort")) {
                    lineData.setSort(handleInteger(line.get("sort")));
                }
                lineList.add(lineData);
            }
            // 进行排序 数据查询时已做排序
            //lineList.sort(SmartFormServiceImpl::sortLineData);
            tableGroup.setLineList(lineList);
        }
    }

    /**
     * @param l1
     * @param l2
     * @return int    返回类型
     * @Title: sortLineData
     * @Description: 表格排序
     */
    public static int sortLineData(GroupLine l1, GroupLine l2) {
        if (l1.getSort() < l2.getSort()) {
            return -1;
        }
        if (l1.getSort() > l2.getSort()) {
            return 1;
        }
        if (l1.getCreatedAt() < l2.getCreatedAt()) {
            return -1;
        }
        return 1;
    }

    /**
     * 将field填充到存储实体中
     *
     * @param inMainTable   是否存到主表
     * @param contentId     表单内容ID
     * @param widget
     * @param componentList
     * @param uploadList
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    private void fillQueryField(boolean inMainTable, String contentId, Group group, WidgetBase widget, String lineId,
                                Map<String, Object> mainDataMap, List<FormContentBaseDTO<List<String>>> componentList,
                                List<FormContentBaseDTO<String>> uploadList) throws ParseException {
        if (widget.getFieldType() == WidgetType.UPLOAD.value) {
            this.getQueryUploadWidget(uploadList, group, lineId, widget, false);
            return;
        }
        // 是否从主表查询
        if (inMainTable) {
            this.getQueryWidget(mainDataMap, widget, false);
        } else {
            // 查找是否已经存在组件存储DTO
            FormContentBaseDTO<List<String>> componentDTO = null;
            for (FormContentBaseDTO<List<String>> item : componentList) {
                if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
                        && item.getWorkType().equals(group.getWorkType())) {
                    componentDTO = item;
                    break;
                }
            }
            Map<String, Object> dataMap = null;
            if (componentDTO != null) {
                dataMap = (Map<String, Object>) componentDTO.getQueryData();
            }
            if (dataMap != null)
                this.getQueryWidget(dataMap, widget, false);
            else {
                int dataType = widget.getFieldType();
                if (dataType == WidgetType.NUMBER.value) {
                    // 数字
                    WidgetBase simWidget = widget;
                    WidgetNumber orgWidget = (WidgetNumber) widget;
                    // 如果数字框启用了Sql查询,则不再应用数据库中的值
                    if (orgWidget.getUseSql() != null && orgWidget.getUseSql() && orgWidget.getSqlStr() != null
                            && !orgWidget.getSqlStr().equals("")) {
                        ((WidgetNumber) simWidget).setDefValue(orgWidget.getDefValue());
                    }
                }
            }
        }
    }

    /**
     * 将field填充到存储实体中
     *
     * @param useMainTable   是否存到主表
     * @param hasTable       是否是存到表格中
     * @param contentId      表单内容ID
     * @param component
     * @param widget
     * @param mainContentMap
     * @param componentList
     * @param tableList
     * @param uploadList
     * @param hasStorage
     * @throws ParseException
     */
    private void handleFieldDTO(boolean useMainTable, boolean hasTable, String contentId, FormContentTableEntity line,
                                FormContentBaseDTO<Group> component, WidgetBase widget, Map<String, Object> mainContentMap,
                                List<FormContentBaseDTO<FormContentComponentEntity>> componentList,
                                List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList,
                                List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, List<DBFieldMapper> mainMappers,
                                Integer lineNum, boolean hasStorage) throws ParseException {
        // 根据字段ID查询结构数据
        WidgetBase orgWidget = getOriginalWidget(component.getData(), lineNum, widget.getId());

        // 是否追加至主表中
        if (mainMappers != null && mainMappers.size() > 0) {
            for (DBFieldMapper fmap : mainMappers) {
                if (fmap.getMapperType() == DBFieldMapperType.ALIAS.value && fmap.getAlias() != null) {
                    String[] aliasConfig = fmap.getAlias().split("\\.");
                    // 分割表名与字段名
                    if (aliasConfig.length == 2) {
                        WidgetBase base = (WidgetBase) orgWidget;
                        // 是否要存储改字段
                        if (aliasConfig[0].equals(component.getDbTable()) && aliasConfig[1].equals(base.getAlias())) {
                            handleField((WidgetBase) widget, orgWidget, mainContentMap, uploadList, null,
                                    component.getData(), contentId, fmap.getDbAlias(), hasStorage);
                        }
                    }
                }
            }
        }

        // 处理超级组件旗下字段
        if (useMainTable) {
            handleField((WidgetBase) widget, orgWidget, mainContentMap, uploadList, null, component.getData(),
                    contentId, null, hasStorage);
        } else if (!hasTable) {
            // 查找是否已经存在组件存储DTO
            FormContentBaseDTO<FormContentComponentEntity> componentDTO = null;
            for (FormContentBaseDTO<FormContentComponentEntity> componentItem : componentList) {
                if (componentItem.getDbName().equals(component.getDbName())
                        && componentItem.getDbTable().equals(component.getDbTable())
                        && componentItem.getWorkType().equals(component.getWorkType())) {
                    componentDTO = componentItem;
                    break;
                }
            }
            // 初始化组件存储对象
            if (componentDTO == null) {
                componentDTO = new FormContentBaseDTO<FormContentComponentEntity>();
                FormContentComponentEntity entity = new FormContentComponentEntity();
                String uuid = UUIDUtil.getNextId();
                entity.setId(uuid);
                entity.setContentId(contentId);
                entity.setWorkType(component.getWorkType());
                entity.setCreatedAt(System.currentTimeMillis());
                entity.setModifiedAt(System.currentTimeMillis());
                componentDTO.setData(entity);
                componentDTO.setDbName(component.getDbName());
                componentDTO.setDbTable(component.getDbTable());
                componentDTO.setWorkType(component.getWorkType());
                // map添加
                componentDTO.setContent(new Query());
                componentList.add(componentDTO);
            }
            // 添加组件存储数据
            handleField((WidgetBase) widget, orgWidget, componentDTO.getContent(), uploadList, null,
                    component.getData(), contentId, null, hasStorage);
        } else {
            // 添加组件存储数据
            handleField((WidgetBase) widget, orgWidget, line.getContent(), uploadList, line.getId(),
                    component.getData(), contentId, null, hasStorage);
        }
    }

    /**
     * 获取查询数据
     *
     * @param dataMap
     * @param widget
     * @param useCopy
     * @return
     * @throws ParseException
     */
    private WidgetBase getQueryWidget(Map<String, Object> dataMap, WidgetBase widget, boolean useCopy)
            throws ParseException {
        int dataType = widget.getFieldType();
        WidgetBase simWidget = null;
        if (dataType == WidgetType.NUMBER.value) {
            // 数字
            simWidget = useCopy ? new WidgetNumber() : widget;
            WidgetNumber orgWidget = (WidgetNumber) widget;
            // 如果数字框启用了Sql查询,则不再应用数据库中的值
            if (orgWidget.getUseSql() != null && orgWidget.getUseSql() && orgWidget.getSqlStr() != null
                    && !orgWidget.getSqlStr().equals("")) {
                ((WidgetNumber) simWidget).setDefValue(orgWidget.getDefValue());
            } else {
                if (widget.getAlias() != null
                        || !widget.getAlias().equals("") && dataMap != null && dataMap.containsKey(widget.getAlias())) {
                    Object obj = dataMap.get(widget.getAlias());
                    ((WidgetNumber) simWidget).setDefValue(handleBigDecimal(obj));
                }
            }
        } else if (dataType == WidgetType.INPUT.value || dataType == WidgetType.TEXTAREA.value) {
            // 文本
            simWidget = useCopy ? new WidgetInput() : widget;
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
                    && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());
                ((WidgetInput) simWidget).setDefValue(obj.toString());
            }
        } else if (dataType == WidgetType.EDITOR.value) {
            // 文本
            simWidget = useCopy ? new WidgetEditor() : widget;
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
                    && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());
                // 做老数据兼融处理，部分老数据序列化会成WidgetInput
                if (simWidget instanceof WidgetEditor) {
                    ((WidgetEditor) simWidget).setDefValue(obj.toString());
                } else {
                    ((WidgetInput) simWidget).setDefValue(obj.toString());
                }
            }

        } else if (dataType == WidgetType.RADIO.value) {
            // 单选
            simWidget = useCopy ? new WidgeRadio() : widget;
            WidgeRadio vData = (WidgeRadio) widget;
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
                    && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());
                ((WidgeRadio) simWidget).setDefValue(obj.toString());
            }
            ((WidgeRadio) simWidget).setSourceID(vData.getSourceID());

        } else if (dataType == WidgetType.CHECKBOX.value || dataType == WidgetType.SELECT.value
                || dataType == WidgetType.CASCADER.value) {
            // 多选
            simWidget = useCopy ? new WidgeSelect() : widget;
            WidgeSelect vData = (WidgeSelect) widget;
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
                    && dataMap.containsKey(widget.getAlias())) {
                Object obj = dataMap.get(widget.getAlias());

                if (obj.toString() != null && !obj.toString().equals("")) {
                    String[] listValue = obj.toString().split(",");
                    // 拆分Sql中逗号分割的ID
                    List<String> defs = new ArrayList<String>();
                    if (listValue != null && listValue.length > 0) {
                        for (String value : listValue) {
                            defs.add(value);
                        }
                    }
                    ((WidgeSelect) simWidget).setDefValue(defs);
                } else
                    ((WidgeSelect) simWidget).setDefValue(null);
            }
            ((WidgeSelect) simWidget).setMulti(vData.getMulti());
            ((WidgeSelect) simWidget).setSourceID(vData.getSourceID());

        } else if (dataType == WidgetType.DATE.value || dataType == WidgetType.DATESPAN.value) {
            // 日期
            simWidget = useCopy ? new WidgetDate() : widget;
            WidgetDate vData = (WidgetDate) widget;

            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null) {
                if (dataMap.containsKey(widget.getAlias())) {
                    Long time = handleLong(dataMap.get(widget.getAlias()));
                    if (time != null && time != 0)
                        ((WidgetDate) simWidget).setDefStartDate(new Date(time));
                    else
                        ((WidgetDate) simWidget).setDefStartDate(null);
                }
                if (dataType == WidgetType.DATESPAN.value && dataMap.containsKey(vData.getEndAlias())) {
                    Long time = handleLong(dataMap.get(vData.getEndAlias()));
                    if (time != null && time != 0)
                        ((WidgetDate) simWidget).setDefEndDate(new Date(time));
                    else
                        ((WidgetDate) simWidget).setDefEndDate(null);
                }
            }
            ((WidgetDate) simWidget).setDateType(vData.getDateType());

        } else if (dataType == WidgetType.TIME.value || dataType == WidgetType.TIMESPAN.value) {
            // 时间简化
            simWidget = useCopy ? new WidgetTime() : widget;
            WidgetTime vData = (WidgetTime) widget;
            if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null) {
                if (dataMap.containsKey(widget.getAlias())) {
                    Integer time = handleInteger(dataMap.get(widget.getAlias()));
                    if (time != null && time != 0)
                        ((WidgetTime) simWidget).setDefStartTime(this.getTimeStampStr(time));
                    else
                        ((WidgetTime) simWidget).setDefStartTime(null);
                }
                if (dataType == WidgetType.TIMESPAN.value && dataMap.containsKey(vData.getEndAlias())) {
                    Integer time = handleInteger(dataMap.get(vData.getEndAlias()));
                    if (time != null && time != 0)
                        ((WidgetTime) simWidget).setDefEndTime(this.getTimeStampStr(time));
                    else
                        ((WidgetTime) simWidget).setDefEndTime(null);
                }
                ((WidgetTime) simWidget).setTimeType(vData.getTimeType());
            }
        }
        if (useCopy && simWidget != null) {
            simWidget.setId(widget.getId());
            simWidget.setType(widget.getType());
            simWidget.setFieldType(dataType);
            simWidget.setName(widget.getName());
        }
        return simWidget;
    }


    /**
     * 获取结构字段实体
     *
     * @param group
     * @param fieldId
     * @return
     */
    private WidgetBase getOriginalWidget(Group group, Integer lineNum, String fieldId) {
        if (group.getGroupType() == GroupType.SUPER.value) {
            if (group.getFieldList() != null) {
                // 遍历超级组件字段
                for (FormFieldBase widget : group.getFieldList()) {
                    if (widget instanceof Group) {
                        // 处理超级组件旗下的组
                        Group tableGroup = (Group) widget;
                        if (tableGroup.getGroupType() == GroupType.STYLE.value) {
                            // 遍历样式组字段
                            for (FormFieldBase styleWidget : tableGroup.getFieldList()) {
                                if (styleWidget instanceof WidgetBase
                                        && ((WidgetBase) styleWidget).getId().equals(fieldId))
                                    return (WidgetBase) styleWidget;
                            }
                        }
                    } else {
                        // 处理超级组件旗下字段
                        if (widget instanceof WidgetBase && ((WidgetBase) widget).getId().equals(fieldId))
                            return (WidgetBase) widget;
                    }
                }
            }
        } else if (group.getGroupType() == GroupType.TABLE.value) {
            // 处理表格
            if (group.getSteerable() == null || !group.getSteerable()) {
                // 遍历表格原始字段
                if (group.getOriginalLine() != null) {
                    GroupLine line = group.getOriginalLine();
                    // 遍历组字段
                    if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                        for (WidgetBase widget : line.getFieldList()) {
                            // 处理超级组件旗下字段
                            if (widget instanceof WidgetBase && ((WidgetBase) widget).getId().equals(fieldId))
                                return (WidgetBase) widget;
                        }
                    }
                }
            } else {
                // 可控表格，做为超级组件处理
                if (group.getLineList() != null && group.getLineList().size() > 0) {
                    for (GroupLine line : group.getLineList()) {
                        // 遍历组字段
                        if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
                                && line.getFieldList().size() > 0 && lineNum == line.getLineNum()) {
                            for (WidgetBase widget : line.getFieldList()) {
                                // 处理超级组件旗下字段
                                if (widget instanceof WidgetBase && ((WidgetBase) widget).getId().equals(fieldId))
                                    return (WidgetBase) widget;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param uploadList
     * @param group
     * @param widget
     * @param useCopy
     * @return
     */
    private WidgetUpload getQueryUploadWidget(List<FormContentBaseDTO<String>> uploadList, Group group, String lineId,
                                              WidgetBase widget, boolean useCopy) {
        WidgetUpload simWidget = useCopy ? new WidgetUpload() : (WidgetUpload) widget;
        // 找到上传查询数据
        FormContentBaseDTO<String> uploadDTO = null;
        for (FormContentBaseDTO<String> item : uploadList) {
            if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
                    && item.getWorkType().equals(group.getWorkType())) {
                uploadDTO = item;
                break;
            }
        }
        // 将数据填充到WidgetUpload
        if (uploadDTO != null && uploadDTO.getQueryData() != null) {
            @SuppressWarnings("unchecked")
            List<FormContentUploadsEntity> dataMaps = (List<FormContentUploadsEntity>) uploadDTO.getQueryData();
            // 上传
            WidgetUpload vData = (WidgetUpload) widget;
            List<UploadItem> defValue = new ArrayList<UploadItem>();
            for (FormContentUploadsEntity uploadData : dataMaps) {
                boolean ckLineId = lineId == null ? true : uploadData.getLineId().equals(lineId);
                if (uploadData.getUploadType().equals(vData.getUploadType()) && ckLineId) {
                    UploadItem item = new UploadItem();
                    item.setId(uploadData.getId());
                    item.setUrl(uploadData.getUrl());
                    item.setSize(uploadData.getSize());
                    item.setName(uploadData.getFileName());
                    item.setType(uploadData.getSuffix());
                    item.setCreatedAt(uploadData.getCreatedAt());
                    defValue.add(item);
                }
            }
            if (defValue.size() > 0) {
                // 先按创建时间排序，升序排序
                defValue.sort((UploadItem i1, UploadItem i2) -> i1.getCreatedAt() < i2.getCreatedAt() ? -1 : 1);
                simWidget.setDefValue(defValue);
            }
        }
        if (useCopy && simWidget != null) {
            simWidget.setId(widget.getId());
            simWidget.setType(widget.getType());
            simWidget.setFieldType(widget.getFieldType());
            simWidget.setName(widget.getName());
        }
        return simWidget;
    }

    /**
     * 简化字段值，只保留关键type，id，value，name等 验证必填项和简单规则 转换为Mysql存储实体
     *
     * @param widget     字段值
     * @param orgWidget  原始数据，用于检测规则和赋值基本属性
     * @param mainAlias  存到主表的字段名，存在则优先使用
     * @param hasStorage 是否暂存，暂存不做复杂与必填验证
     * @throws ParseException
     */
    private void handleField(WidgetBase widget, WidgetBase orgWidget, Map<String, Object> contentMap,
                             List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, String lineId, Group component,
                             String contentId, String mainAlias, boolean hasStorage) throws ParseException {
        //System.out.println("初始化：contentMap："+contentMap);
        int dataType = widget.getFieldType();
        //System.out.println("dataType:"+dataType);
        // 数据容错
        if (orgWidget == null)
            return;
        String alias = mainAlias != null ? mainAlias : orgWidget.getAlias();
        // 空别名字段不处理
        if (dataType != WidgetType.UPLOAD.value && (alias == null || alias.equals("")))
            return;
        // 简化字段
        if (dataType == WidgetType.NUMBER.value) {
            // 数字框
            WidgetNumber vData = ((WidgetNumber) widget);
            if (vData.getDefValue() == null || vData.getDefValue().equals(BigDecimal.ZERO))
                contentMap.put(alias, 0);
            else
                contentMap.put(alias, vData.getDefValue());
        } else if (dataType == WidgetType.INPUT.value || dataType == WidgetType.TEXTAREA.value) {
            // 文本框
            WidgetInput vData = (WidgetInput) widget;
            if (CheckDataUtil.isNull(vData.getDefValue()))
                contentMap.put(alias, "");
            else
                contentMap.put(alias, vData.getDefValue());
        } else if (dataType == WidgetType.EDITOR.value) {
            // 富文本
            // 兼融老数据
            if (widget instanceof WidgetEditor) {
                WidgetEditor vData = (WidgetEditor) widget;
                if (CheckDataUtil.isNull(vData.getDefValue()))
                    contentMap.put(alias, "");
                else
                    contentMap.put(alias, vData.getDefValue());
            } else {
                WidgetInput vData = (WidgetInput) widget;
                if (CheckDataUtil.isNull(vData.getDefValue()))
                    contentMap.put(alias, "");
                else
                    contentMap.put(alias, vData.getDefValue());
            }
        } else if (dataType == WidgetType.RADIO.value) {
            // 单选
            WidgeRadio vData = (WidgeRadio) widget;
            if (CheckDataUtil.isNull(vData.getDefValue()))
                contentMap.put(alias, "");
            else
                contentMap.put(alias, vData.getDefValue());
        } else if (dataType == WidgetType.CHECKBOX.value || dataType == WidgetType.SELECT.value
                || dataType == WidgetType.CASCADER.value) {
            // 多选
            WidgeSelect vData = (WidgeSelect) widget;
            if (CheckDataUtil.isNull(vData.getDefValue()))
                contentMap.put(alias, "");
            else {
                // 使用逗号拼接选项
                if (vData.getDefValue().size() == 1)
                    contentMap.put(alias, vData.getDefValue().get(0));
                else {
                    StringBuffer sb = new StringBuffer();
                    for (String value : vData.getDefValue()) {
                        sb.append(value + ",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    contentMap.put(alias, sb.toString());
                }
            }
        } else if (dataType == WidgetType.DATE.value || dataType == WidgetType.DATESPAN.value) {
            // 日期
            WidgetDate vData = (WidgetDate) widget;
            if (vData.getDefStartDate() == null)
                contentMap.put(alias, 0);
            else
                contentMap.put(alias, vData.getDefStartDate().getTime());
            if (dataType == WidgetType.DATESPAN.value) {
                Long endDate = vData.getDefEndDate() == null ? 0 : vData.getDefEndDate().getTime();
                contentMap.put(((WidgetDate) orgWidget).getEndAlias(), endDate);
            }

        } else if (dataType == WidgetType.TIME.value || dataType == WidgetType.TIMESPAN.value) {
            // 时间
            WidgetTime vData = (WidgetTime) widget;
            if (CheckDataUtil.isNull(vData.getDefStartTime()))
                contentMap.put(alias, 0);
            else {
                int timeStamp = this.getTimeStamp(vData.getDefStartTime());
                contentMap.put(alias, timeStamp);
            }
            if (dataType == WidgetType.TIMESPAN.value) {
                int timeStamp = CheckDataUtil.isNull(vData.getDefEndTime()) ? 0
                        : this.getTimeStamp(vData.getDefEndTime());
                contentMap.put(((WidgetTime) orgWidget).getEndAlias(), timeStamp);
            }
        } else if (dataType == WidgetType.UPLOAD.value && uploadList != null) {
            // 上传
            WidgetUpload vData = (WidgetUpload) widget;
            FormContentBaseDTO<List<FormContentUploadsEntity>> uploadDTO = this.getUploadData(vData, uploadList,
                    component);
            // 有上传控件，默认加一个空数据
            if (uploadDTO.getData().size() == 0) {
                FormContentUploadsEntity data = new FormContentUploadsEntity();
                data.setTableName(component.getTable());
                data.setWorkType(component.getWorkType());
                uploadDTO.getData().add(data);
            }
            if (vData.getDefValue() != null && vData.getDefValue().size() != 0) {
                long createTime = System.currentTimeMillis();
                for (UploadItem value : vData.getDefValue()) {
                    // 创建sql数据实体
                    FormContentUploadsEntity data = new FormContentUploadsEntity();
                    String uuid = UUIDUtil.getNextId();
                    data.setId(uuid);
                    data.setContentId(contentId);
                    data.setTableName(component.getTable());
                    data.setWorkType(component.getWorkType());
                    data.setLineId(lineId);
                    data.setUploadType(((WidgetUpload) orgWidget).getUploadType());
                    data.setCreatedAt(createTime);
                    data.setModifiedAt(createTime);

                    data.setUrl(value.getUrl());
                    data.setSize(value.getSize());
                    data.setFileName(value.getName());
                    data.setSuffix(value.getType());
                    uploadDTO.getData().add(data);
                    ++createTime;
                }
            }
        }
    }

    /**
     * 查找upload存储对象,如果没有则初始化
     *
     * @param component
     * @return
     */
    private FormContentBaseDTO<List<FormContentUploadsEntity>> getUploadData(WidgetUpload widget,
                                                                             List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, Group component) {
        // 查找uploadData存储对象
        FormContentBaseDTO<List<FormContentUploadsEntity>> uploadData = null;
        for (FormContentBaseDTO<List<FormContentUploadsEntity>> uploadItem : uploadList) {
            if (uploadItem.getDbTable().equals(component.getTable())
                    && uploadItem.getWorkType().equals(component.getWorkType())) {
                uploadData = uploadItem;
            }
        }
        // 初始化组件存储对象
        if (uploadData == null) {
            uploadData = new FormContentBaseDTO<List<FormContentUploadsEntity>>();
            List<FormContentUploadsEntity> entity = new ArrayList<FormContentUploadsEntity>();
            uploadData.setData(entity);
            uploadData.setDbTable(component.getTable());
            uploadData.setWorkType(component.getWorkType());
            uploadList.add(uploadData);
        }
        return uploadData;
    }

    /**
     * 将时分秒格式转为时间戳
     *
     * @param time
     * @return
     * @throws ParseException
     */
    private int getTimeStamp(String time) throws ParseException {
        // 只有5位，则补全后面的秒：00:00:00
        if (time.length() == 5)
            time += ":00";
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return (int) timeFormat.parse(time).getTime();
    }

    /**
     * 将时间戳格式转为时分秒
     *
     * @param time
     * @return
     * @throws ParseException
     */
    private String getTimeStampStr(int time) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(new Date(time));
    }

    /**
     * 处理短int
     *
     * @param date
     * @return
     */
    private Integer handleInteger(Object date) {
        if (date == null)
            return null;
        if (date instanceof Boolean) {
            return (Boolean) date ? 1 : 0;
        } else if (date instanceof Long) {
            return ((Long) date).intValue();
        } else if (date instanceof String) {
            String str = (String) date;
            if (CheckDataUtil.isNull(str))
                return null;
            return Integer.parseInt(str);
        }
        return (Integer) date;
    }

    /**
     * 处理Long
     *
     * @param date
     * @return
     */
    private Long handleLong(Object date) {
        if (date == null)
            return null;
        if (date instanceof Boolean) {
            return (Boolean) date ? 1L : 0L;
        } else if (date instanceof Integer) {
            return Long.parseLong(date.toString());
        } else if (date instanceof String) {
            return Long.parseLong(((String) date).trim());
        }
        return (Long) date;
    }

    /**
     * 处理BigDecimal
     *
     * @param date
     * @return
     */
    private BigDecimal handleBigDecimal(Object date) {
        if (date == null)
            return null;
        if (date instanceof Boolean) {
            return new BigDecimal((Boolean) date ? 1 : 0);
        } else if (date instanceof Long) {
            return new BigDecimal(((Long) date));
        } else if (date instanceof Float) {
            return new BigDecimal((Float.toString((Float) date)));
        } else if (date instanceof Double) {
            return new BigDecimal((Double.toString((Double) date)));
        } else if (date instanceof BigDecimal) {
            return (BigDecimal) date;
        } else if (date instanceof String) {
            return new BigDecimal((String) date);
        }
        return new BigDecimal((Integer) date);
    }

    /**
     * 获取选择控件的选项源
     *
     * @param widget
     */
    private void getSelectOptions(WidgetBase widget, Set<String> optionsIDs) {
        if (widget instanceof WidgeRadio) {
            if(StringUtils.isNotEmpty(((WidgeRadio) widget).getSourceID())){
                optionsIDs.add(((WidgeRadio) widget).getSourceID());
            }
        } else if (widget instanceof WidgeSelect) {
            if(StringUtils.isNotEmpty(((WidgeSelect) widget).getSourceID())){
                optionsIDs.add(((WidgeSelect) widget).getSourceID());
            }
        }
    }

    @Override
    public SmartFormPagination smartFormList(SmartFormInput page) {
        // 设置分页数和页码
        Page<SmartForm> pageSql = new Page<>(page.getNowPage(), page.getPageSize());

        // 设置查询条件
        QueryWrapper<SmartForm> queryWrapper = new QueryWrapper<>();
        // 状态查询
        if (page.getState().intValue() != StateType.All.value) {
            queryWrapper.eq("state", page.getState());
        }
        // 分类查询，""用于查询无分类表单,all代表查询所有，不筛选分类
        if (page.getCategoryId() != null && !"all".equals(page.getCategoryId())) {
            queryWrapper.eq("category_Id", page.getCategoryId());
        }
        // 名称模糊查询
        if (page.getName() != null && page.getName() != "") {
            queryWrapper.like("name", page.getName());
        }
        queryWrapper.orderByDesc("created_at");
        //List<SmartForm> list = smartFormMapper.listForPage(pageSql, query);
        Page<SmartForm> page1 = smartFormMapper.selectPage(pageSql, queryWrapper);
        List<SmartForm> list = page1.getRecords();

        SmartFormPagination pageList = new SmartFormPagination();
        pageList.setTotal(Math.toIntExact(pageSql.getTotal()));
        pageList.setRows(list);
        return pageList;
    }

    @Override
    @Transactional
    public String createSmartForm(String form) {
        SmartForm data = JSON.parseObject(form, SmartForm.class);
        if (data != null) {
            return addSmartForm(data, false);
        }
        return "0";
    }

    /**
     * fastJson 序列化时写入类型信息
     */
    SerializerFeature[] features = new SerializerFeature[]{SerializerFeature.WriteClassName,
            // SerializerFeature.SkipTransientField,
            // SerializerFeature.DisableCircularReferenceDetect
    };

    /**
     * 添加一个SmartForm 到数据库中
     *
     * @param data
     * @return
     */
    private String addSmartForm(SmartForm data, boolean isCopy) {
        data.setOptionsList(null);
        // 判定表单分类是否存在，不存在则直接复制""，为无分类表单
        if (data.getCategoryId() == null || !categoryDao.hasKey(data.getCategoryId()))
            data.setCategoryId("");
        if (data.getDes() == null)
            data.setDes("");
        data.setId(UUIDUtil.getNextId());
        // 创建时间
        data.setCreatedAt(new Date());
        // 修改时间
        data.setModifiedAt(new Date());
        // 设置状态
        data.setState(StateType.DEV.value);
        if (isCopy) {
            String json = data.getPagesJson();
            if (json != null && !json.equals("")) {
                List<FormPage> pageList = JSON.parseArray(json, FormPage.class);
                data.setPageList(pageList);
            }
            // 填充FieldMapperList用于redis保存
            String fieldMapperJson = data.getFieldMapperJson();
            if (fieldMapperJson != null && !fieldMapperJson.equals("")) {
                List<DBFieldMapper> fieldMappers = JSON.parseArray(fieldMapperJson, DBFieldMapper.class);
                data.setFieldMapperList(fieldMappers);
            }
            String ruleJson = data.getConditionRulesJson();
            if (ruleJson != null && !ruleJson.equals("")) {
                List<ConditionalRule> ruleList = JSON.parseArray(ruleJson, ConditionalRule.class);
                data.setConditionRules(ruleList);
            }
            String settingJson = data.getExtraSettingJson();
            if (settingJson != null && !settingJson.equals("")) {
                FormSettings extraSetting = JSON.parseObject(settingJson, FormSettings.class);
                data.setExtraSetting(extraSetting);
            }
        } else {
            // 将字段列表系列化后存储到mysql中
            String fieldsJson = JSON.toJSONString(data.getPageList(), features);
            data.setPagesJson(fieldsJson);
            String fieldMapperJson = "";
            if (data.getFieldMapperList() != null) {
                // 将表单内容主表字段映射关系序列化后存储到mysql中
                fieldMapperJson = JSON.toJSONString(data.getFieldMapperList(), features);
            }

            ///
            //临时添加
            if (data.getConditionRules() != null) {
                String ruleJson = JSON.toJSONString(data.getConditionRules(), features);
                ruleJson = ruleJson == null ? "" : ruleJson;
                data.setConditionRulesJson(ruleJson);
            }
            ///
            data.setFieldMapperJson(fieldMapperJson);
            String settingJson = "";
            if (data.getConditionRules() != null) {
                settingJson = JSON.toJSONString(data.getExtraSetting(), features);
            }
            data.setExtraSettingJson(settingJson);

        }
        if (data.getConditionRulesJson() == null)
            data.setConditionRulesJson("");
        //增加”跳过时效校验“ 默认0 不跳过校验 正常校验
        if (data.getSkipendtimevalidation() != null) {
            String skipendtimevalidation = data.getSkipendtimevalidation();
            if (skipendtimevalidation == "" || skipendtimevalidation.length() < 1) {
                skipendtimevalidation = "0";
            }
            data.setSkipendtimevalidation(skipendtimevalidation);
        } else {
            data.setSkipendtimevalidation("0");
        }
        // 先执行SQL，再存Redis，SQL使用事务提交
        smartFormMapper.save(data);
        data.setPagesJson(null);
        data.setFieldMapperJson(null);
        data.setConditionRulesJson(null);
        data.setExtraSettingJson(null);
        dao.update(data);
        return data.getId();
    }

    @Override
    @Transactional
    public String updateSmartForm(String form) {
        SmartForm data = JSON.parseObject(form, SmartForm.class);
        if (data != null) {
            SmartForm oldData = smartFormMapper.getSimpleInfoById(data.getId());
            if (oldData != null) {
                if (oldData.getState().intValue() == StateType.RELEASE.value) {
                    throw new GlobalException(60404);
                }
                data.setOptionsList(null);
                // 设置创建时间
                data.setCreatedAt(oldData.getCreatedAt());
                // 修改时间
                data.setModifiedAt(new Date());
                // 将字段列表序列化后存储到mysql中
                String fieldsJson = JSON.toJSONString(data.getPageList(), features);
                data.setPagesJson(fieldsJson);
                if (data.getFieldMapperList() != null) {
                    // 将表单内容主表字段映射关系序列化后存储到mysql中
                    String fieldMapperJson = JSON.toJSONString(data.getFieldMapperList(), features);
                    data.setFieldMapperJson(fieldMapperJson);
                }
                if (data.getConditionRules() != null) {
                    String ruleJson = JSON.toJSONString(data.getConditionRules(), features);
                    ruleJson = ruleJson == null ? "" : ruleJson;
                    data.setConditionRulesJson(ruleJson);
                }
                if (data.getConditionRules() != null) {
                    String settingJson = JSON.toJSONString(data.getExtraSetting(), features);
                    settingJson = settingJson == null ? "" : settingJson;
                    data.setExtraSettingJson(settingJson);
                }
                if (data.getSkipendtimevalidation() != null) {
                    String skipendtimevalidation = data.getSkipendtimevalidation();
                    if (skipendtimevalidation == "" || skipendtimevalidation.length() < 1) {
                        skipendtimevalidation = "0";
                    }
                    data.setSkipendtimevalidation(skipendtimevalidation);
                } else {
                    data.setSkipendtimevalidation("0");
                }
                // 先执行SQL，再存Redis，SQL使用事务提交
                smartFormMapper.updateById(data);
                dao.update(data);
                return "1";
            }
        }
        return "0";
    }

    @Override
    @Transactional
    public String updateSmartFormState(String id, Integer state) {
        SmartForm data = smartFormMapper.getSimpleInfoById(id);
        if (data != null) {
            //if (data.getState().intValue() == StateType.DEV.value) {
            // 目前只设置发布状态
            data.setState(state);
            data.setModifiedAt(new Date());
            // 先执行SQL，再存Redis，SQL使用事务提交
            smartFormMapper.updateById(data);
            if (state == StateType.RELEASE.value) {
                if (dao.hasKey(id)) {
                    dao.update(data, "state", "modifiedAt");
                }
            }
            return "1";
            //}
        }
        return "0";
    }

    @Override
    @Transactional
    public String copySmartForm(String id) {
        SmartForm data = smartFormMapper.selectById(id);
        if (data != null) {
            return addSmartForm(data, true);
        }
        return "0";
    }

    /**
     * 重置组的Id以及组旗下的字段
     *
     * @param group
     * @throws ParseException
     */
    @Deprecated
    private void resetGroupId(Group group) {
        if (group.getLineList() != null && group.getLineList().size() > 0) {
            boolean isfirst = true;
            // 记录组的首行id映射，其他行字段id与首行相同
            Map<String, String> idMap = new HashMap<String, String>();
            // 遍历组行
            for (GroupLine line : group.getLineList()) {
                if (isfirst) {
                    // 遍历组首行字段
                    if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                        for (WidgetBase widget : line.getFieldList()) {
                            String newId = UUIDUtil.getNextId();
                            idMap.put(widget.getId(), newId);
                            widget.setId(newId);
                        }
                    }
                } else {
                    // 遍历组其他行字段
                    if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                        for (WidgetBase widget : line.getFieldList()) {
                            String newId = idMap.get(widget.getId());
                            widget.setId(newId);
                        }
                    }
                }
                isfirst = false;
            }
        }
    }

    @Override
    @Transactional
    public String deleteSmartForm(String id) {
        SmartForm data = smartFormMapper.getSimpleInfoById(id);
        if (data != null) {
            if (data.getState().intValue() == StateType.RELEASE.value) {
                throw new GlobalException(60404);
            }
            // SQL移除
            smartFormMapper.batchRemove(new String[]{id});
            dao.delete(id);
            return "1";
        }
        return "1";
    }

    @Override
    public void findPageContentById(SmartForm smartform, FormPage page, String contentId) {
        try {
            DynamicDataSourceContextHolder.push(DBCutConstants.SMART_CORE);
            this.getPageContentById(smartform, page, contentId);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    @Override
    public List<SmartForm> smartFormByIds(List<String> ids, boolean hasOptions) {
        List<SmartForm> smartFormList = new ArrayList<>();
        if(ids.size()>0){
            for (String id : ids) {
                smartFormList.add(this.smartForm(id, hasOptions));
            }
        }else {
            return Collections.EMPTY_LIST;
        }
        return smartFormList;
    }
}
