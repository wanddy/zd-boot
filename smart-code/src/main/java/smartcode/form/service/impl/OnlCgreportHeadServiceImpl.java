package smartcode.form.service.impl;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.DynamicDataSourceModel;
import commons.exception.ZdException;
import commons.system.api.ISysBaseAPI;
import commons.util.oConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartcode.form.dynamic.db.DataSourceCachePool;
import smartcode.form.dynamic.db.DynamicDBUtil;
import smartcode.form.dynamic.db.SqlUtils;
import smartcode.form.entity.OnlCgreportHead;
import smartcode.form.entity.OnlCgreportItem;
import smartcode.form.entity.OnlCgreportModel;
import smartcode.form.entity.OnlCgreportParam;
import smartcode.form.mapper.OnlCgreportHeadMapper;
import smartcode.form.service.OnlCgreportHeadService;
import smartcode.form.service.OnlCgreportItemService;
import smartcode.form.service.OnlCgreportParamService;
import smartcode.form.utils.DbSelectUtils;
import smartcode.form.utils.SqlUtil;
import smartcode.form.utils.cgreportAUtils;
import smartcode.form.utils.dUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 15:39
 * @Description: zdit.zdboot.auth.online.service.impl
 **/
@Service
public class OnlCgreportHeadServiceImpl extends ServiceImpl<OnlCgreportHeadMapper, OnlCgreportHead> implements OnlCgreportHeadService {

    private static final Logger logger = LoggerFactory.getLogger(OnlCgreportHeadServiceImpl.class);

    @Autowired
    private OnlCgreportParamService onlCgreportParamService;
    @Autowired
    private OnlCgreportItemService onlCgreportItemService;
    @Autowired
    private OnlCgreportHeadMapper onlCgreportHeadMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public Map<String, Object> executeSelectSqlDynamic(String dbKey, String sql, Map<String, Object> params, String onlCgreportHeadId) {
        DynamicDataSourceModel var5 = DataSourceCachePool.getCacheDynamicDataSourceModel(dbKey);
        String var6 = (String)params.get("order");
        String var7 = (String)params.get("column");
        int var8 = oConvertUtils.getInt(params.get("pageNo"), 1);
        int var9 = oConvertUtils.getInt(params.get("pageSize"), 10);
//        cgreportAUtils.info("【Online多数据源逻辑】报表查询参数params: " + JSON.toJSONString(params));
        QueryWrapper var10 = new QueryWrapper<OnlCgreportParam>();
        var10.eq("cgrhead_id", onlCgreportHeadId);
        List var11 = this.onlCgreportParamService.list(var10);
        OnlCgreportParam var13;
        String var15;
        if (var11 != null && var11.size() > 0) {
            for(Iterator var12 = var11.iterator(); var12.hasNext(); sql = sql.replace("${" + var13.getParamName() + "}", var15)) {
                var13 = (OnlCgreportParam)var12.next();
                Object var14 = params.get("self_" + var13.getParamName());
                var15 = "";
                if (var14 != null) {
                    var15 = var14.toString();
                } else if (var14 == null && oConvertUtils.isNotEmpty(var13.getParamValue())) {
                    var15 = var13.getParamValue();
                }
            }
        }

        QueryWrapper var23 = new QueryWrapper<OnlCgreportItem>();
        var23.eq("cgrhead_id", onlCgreportHeadId);
        var23.eq("is_search", 1);
        List var24 = this.onlCgreportItemService.list(var23);
        if (ReUtil.contains(" order\\s+by ", sql.toLowerCase()) && "3".equalsIgnoreCase(var5.getDbType())) {
            throw new ZdException("SqlServer不支持SQL内排序!");
        } else {
            String var25 = "jeecg_rp_temp.";
            var15 = cgreportAUtils.a(var24, params, var25);
            String var16 = "select * from (" + sql + ") jeecg_rp_temp  where 1=1 " + var15;
            var16 = SqlUtil.b(var16);
            String var17 = SqlUtils.getCountSql(var16);
            Object var18 = params.get("column");
            if (var18 != null) {
                var16 = var16 + " order by jeecg_rp_temp." + var18.toString() + " " + params.get("order").toString();
            }

            String var19 = var16;
            if (!Boolean.valueOf(String.valueOf(params.get("getAll")))) {
                var19 = SqlUtils.createPageSqlByDBType(var5.getDbType(), var16, var8, var9);
            }

//            OnlCgreportAPI.info("多数据源 报表查询sql=>querySql: " + var16);
//            OnlCgreportAPI.info("多数据源 报表查询sql=>pageSQL: " + var19);
//            OnlCgreportAPI.info("多数据源 报表查询sql=>countSql: " + var17);
            HashMap var20 = new HashMap();
            Map var21 = (Map) DynamicDBUtil.findOne(dbKey, var17, new Object[0]);
            var20.put("total", var21.get("total"));
            List var22 = DynamicDBUtil.findList(dbKey, var19, new Object[0]);
            var20.put("records", DbSelectUtils.d(var22));
            return var20;
        }
    }

    @Override
    public Map<String, Object> executeSelectSql(String sql, String onlCgreportHeadId, Map<String, Object> params) throws SQLException {
        String var4 = this.sysBaseAPI.getDatabaseType();
        QueryWrapper var5 = new QueryWrapper<OnlCgreportParam>();
        var5.eq("cgrhead_id", onlCgreportHeadId);
        List var6 = this.onlCgreportParamService.list(var5);
        OnlCgreportParam var8;
        String var10;
        if (var6 != null && var6.size() > 0) {
            for(Iterator var7 = var6.iterator(); var7.hasNext(); sql = sql.replace("${" + var8.getParamName() + "}", var10)) {
                var8 = (OnlCgreportParam)var7.next();
                Object var9 = params.get("self_" + var8.getParamName());
                var10 = "";
                if (var9 != null) {
                    var10 = var9.toString();
                } else if (var9 == null && oConvertUtils.isNotEmpty(var8.getParamValue())) {
                    var10 = var8.getParamValue();
                }
            }
        }

        HashMap var19 = new HashMap();
        Integer var20 = oConvertUtils.getInt(params.get("pageSize"), 10);
        Integer var21 = oConvertUtils.getInt(params.get("pageNo"), 1);
        Page var22 = new Page((long)var21, (long)var20);
        QueryWrapper var11 = new QueryWrapper<OnlCgreportItem>();
        var11.eq("cgrhead_id", onlCgreportHeadId);
        var11.eq("is_search", 1);
        List var12 = this.onlCgreportItemService.list(var11);
        String var13 = "jeecg_rp_temp.";
        String var14 = cgreportAUtils.a(var12, params, var13);
        if (ReUtil.contains(" order\\s+by ", sql.toLowerCase()) && "SQLSERVER".equalsIgnoreCase(var4)) {
            throw new ZdException("SqlServer不支持SQL内排序!");
        } else {
            String var15 = "select * from (" + sql + ") jeecg_rp_temp  where 1=1 " + var14;
            var15 = SqlUtil.b(var15);
            Object var16 = params.get("column");
            if (var16 != null) {
                var15 = var15 + " order by jeecg_rp_temp." + var16.toString() + " " + params.get("order").toString();
            }

//            OnlCgreportAPI.info("报表查询sql=>\r\n" + var15);
            Object var17;
            if (Boolean.valueOf(String.valueOf(params.get("getAll")))) {
                List var18 = this.onlCgreportHeadMapper.executeSelect(var15);
                var17 = new Page();
                ((IPage)var17).setRecords(var18);
                ((IPage)var17).setTotal((long)var18.size());
            } else {
                var17 = this.onlCgreportHeadMapper.selectPageBySql(var22, var15);
            }

            var19.put("total", ((IPage)var17).getTotal());
            var19.put("records", DbSelectUtils.d(((IPage)var17).getRecords()));
            return var19;
        }
    }

    @Override
    public List getSqlFields(String sql, String dbKey) throws SQLException{
        List list;
        if (StringUtils.isNotBlank(dbKey)) {
            list = this.parsing(sql, dbKey);
        } else {
            list = this.parsing(sql, null);
        }

        return list;
    }

    @Override
    public List getSqlParams(String sql) {
        if (oConvertUtils.isEmpty(sql)) {
            return null;
        } else {
            ArrayList list = new ArrayList();
            String str = "\\$\\{\\w+\\}";
            Pattern pattern = Pattern.compile(str);
            Matcher matcher = pattern.matcher(sql);

            while(matcher.find()) {
                String group = matcher.group();
                list.add(group.substring(group.indexOf("{") + 1, group.indexOf("}")));
            }

            return list;
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Result<?> editAll(OnlCgreportModel onlCgreportModel) {
        OnlCgreportHead head = onlCgreportModel.getHead();
        OnlCgreportHead onlCgreportHead = super.getById(head.getId());
        if (onlCgreportHead == null) {
            return Result.error("未找到对应实体");
        } else {
            super.updateById(head);
            QueryWrapper queryWrapper = new QueryWrapper<OnlCgreportItem>();
            queryWrapper.eq("cgrhead_id", head.getId());
            this.onlCgreportItemService.remove(queryWrapper);
            QueryWrapper query = new QueryWrapper<OnlCgreportParam>();
            query.eq("cgrhead_id", head.getId());
            this.onlCgreportParamService.remove(query);
            Iterator iterator = onlCgreportModel.getParams().iterator();

            while(iterator.hasNext()) {
                OnlCgreportParam onlCgreportParam = (OnlCgreportParam)iterator.next();
                onlCgreportParam.setCgrheadId(head.getId());
            }

            iterator = onlCgreportModel.getItems().iterator();

            while(iterator.hasNext()) {
                OnlCgreportItem onlCgreportItem = (OnlCgreportItem)iterator.next();
                onlCgreportItem.setFieldName(onlCgreportItem.getFieldName().trim().toLowerCase());
                onlCgreportItem.setCgrheadId(head.getId());
            }

            this.onlCgreportItemService.saveBatch(onlCgreportModel.getItems());
            this.onlCgreportParamService.saveBatch(onlCgreportModel.getParams());
            return Result.ok("全部修改成功");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result<?> delete(String id) {
        if (super.removeById(id)) {
            QueryWrapper queryWrapper = new QueryWrapper<OnlCgreportItem>();
            queryWrapper.eq("cgrhead_id", id);
            this.onlCgreportItemService.remove(queryWrapper);
            QueryWrapper query = new QueryWrapper<OnlCgreportParam>();
            query.eq("cgrhead_d", id);
            this.onlCgreportParamService.remove(query);
        }
        return Result.ok("删除成功");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result<?> bathDelete(String[] ids) {
        for(int i = 0; i < ids.length; i++) {
            String id = ids[i];
            if (super.removeById(id)) {
                QueryWrapper queryWrapper = new QueryWrapper<OnlCgreportItem>();
                queryWrapper.eq("cgrhead_id", id);
                this.onlCgreportItemService.remove(queryWrapper);
                QueryWrapper query = new QueryWrapper<OnlCgreportParam>();
                query.eq("cgrhead_id", id);
                this.onlCgreportParamService.remove(query);
            }
        }

        return Result.ok("删除成功");
    }

    @Override
    public Map<String, Object> queryCgReportConfig(String reportId) {
        HashMap var2 = new HashMap(0);
        Map var3 = this.onlCgreportHeadMapper.queryCgReportMainConfig(reportId);
        List var4 = this.onlCgreportHeadMapper.queryCgReportItems(reportId);
        List var5 = this.onlCgreportHeadMapper.queryCgReportParams(reportId);
        if (dUtils.a()) {
            var2.put("main", DbSelectUtils.b(var3));
            var2.put("items", DbSelectUtils.d(var4));
        } else {
            var2.put("main", var3);
            var2.put("items", var4);
        }

        var2.put("params", var5);
        return var2;
    }

    private List<String> parsing(String sql, String dbKey) throws SQLException {
        if (oConvertUtils.isEmpty(sql)) {
            return null;
        } else {
            sql = sql.trim();
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length() - 1);
            }

            sql = QueryGenerator.convertSystemVariables(sql);
            sql = SqlUtil.a(sql);
            Set set;
            if (StringUtils.isNotBlank(dbKey)) {
                logger.info("parse sql : " + sql);
                DynamicDataSourceModel model = DataSourceCachePool.getCacheDynamicDataSourceModel(dbKey);
                if (ReUtil.contains(" order\\s+by ", sql.toLowerCase()) && "3".equalsIgnoreCase(model.getDbType())) {
                    throw new ZdException("SqlServer不支持SQL内排序!");
                }

                if ("1".equals(model.getDbType())) {
                    sql = "SELECT * FROM (" + sql + ") temp LIMIT 1";
                } else if ("2".equals(model.getDbType())) {
                    sql = "SELECT * FROM (" + sql + ") temp WHERE ROWNUM <= 1";
                } else if ("3".equals(model.getDbType())) {
                    sql = "SELECT TOP 1 * FROM (" + sql + ") temp";
                }

                logger.info("parse sql with page : " + sql);
                Map map = (Map)DynamicDBUtil.findOne(dbKey, sql, new Object[0]);
                if (map == null) {
                    throw new ZdException("该报表sql没有数据");
                }

                set = map.keySet();
            } else {
                logger.info("parse sql: " + sql);
                if (ReUtil.contains(" order\\s+by ", sql.toLowerCase()) && "SQLSERVER".equalsIgnoreCase(this.sysBaseAPI.getDatabaseType())) {
                    throw new ZdException("SqlServer不支持SQL内排序!");
                }

                IPage page = onlCgreportHeadMapper.selectPageBySql(new Page(1L, 1L), sql);
                List list = page.getRecords();
                if (list.size() < 1) {
                    throw new ZdException("该报表sql没有数据");
                }

                set = ((Map)list.get(0)).keySet();
            }

            if (set != null) {
                set.remove("ROW_ID");
            }

            return new ArrayList(set);
        }
    }
}
