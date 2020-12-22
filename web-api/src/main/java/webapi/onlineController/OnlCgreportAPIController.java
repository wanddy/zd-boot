package webapi.onlineController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import commons.annotation.PermissionData;
import commons.api.vo.Result;
import commons.auth.vo.DictModel;
import commons.auth.vo.DynamicDataSourceModel;
import commons.exception.ZdException;
import commons.system.api.ISysBaseAPI;
import commons.util.BrowserUtils;
import commons.util.oConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smartcode.form.entity.OnlCgreportHead;
import smartcode.form.entity.OnlCgreportItem;
import smartcode.form.mapper.OnlCgreportHeadMapper;
import smartcode.form.service.OnlCgreportHeadService;
import smartcode.form.service.OnlCgreportItemService;
import smartcode.form.service.OnlCgreportParamService;
import smartcode.form.utils.SqlUtil;
import commons.poi.excel.ExcelExportUtil;
import commons.poi.excel.entity.ExportParams;
import commons.poi.excel.entity.params.ExcelExportEntity;

@RestController("onlCgreportAPI")
@RequestMapping({"/online/cgreport/api"})
public class OnlCgreportAPIController {
    private static final Logger logger = LoggerFactory.getLogger(OnlCgreportAPIController.class);
    @Autowired
    private OnlCgreportHeadService onlCgreportHeadService;
    @Autowired
    private OnlCgreportItemService onlCgreportItemService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private OnlCgreportParamService onlCgreportParamService;


    @GetMapping({"/getColumnsAndData/{code}"})
    @PermissionData
    public Result<?> getColumnsAndData(@PathVariable("code") String code, HttpServletRequest request) {
        OnlCgreportHead onlCgreportHead = this.onlCgreportHeadService.getById(code);
        if (onlCgreportHead == null) {
            return Result.error("实体不存在");
        } else {
            Result result = this.getData(code, request);
            if (result.getCode().equals(200)) {
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(result.getResult()));
                JSONArray jsonArray = jsonObject.getJSONArray("records");
                QueryWrapper queryWrapper = new QueryWrapper();
                ((QueryWrapper)((QueryWrapper)queryWrapper.eq("cgrhead_id", code)).eq("is_show", 1)).orderByAsc("order_num");
                List list = this.onlCgreportItemService.list(queryWrapper);
                HashMap map = new HashMap();
                JSONArray json = new JSONArray();
                JSONArray jsonArray1 = new JSONArray();
                Iterator iterator = list.iterator();

                while(iterator.hasNext()) {
                    OnlCgreportItem onlCgreportItem = (OnlCgreportItem)iterator.next();
                    JSONObject jsonObject1 = new JSONObject(4);
                    jsonObject1.put("title", onlCgreportItem.getFieldTxt());
                    jsonObject1.put("dataIndex", onlCgreportItem.getFieldName());
                    jsonObject1.put("align", "center");
                    jsonObject1.put("sorter", "true");
                    String str;
                    if (StringUtils.isNotBlank(onlCgreportItem.getFieldHref())) {
                        str = "fieldHref_" + onlCgreportItem.getFieldName();
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("customRender", str);
                        jsonObject1.put("scopedSlots", jsonObject2);
                        JSONObject jsonObject3 = new JSONObject();
                        jsonObject3.put("slotName", str);
                        jsonObject3.put("href", onlCgreportItem.getFieldHref());
                        json.add(jsonObject3);
                    }

                    jsonArray1.add(jsonObject1);
                    str = onlCgreportItem.getDictCode();
                    List list1 = this.getDictMidel(str, jsonArray, onlCgreportItem.getFieldName());
                    if (list1 != null) {
                        map.put(onlCgreportItem.getFieldName(), list1);
                        jsonObject1.put("customRender", onlCgreportItem.getFieldName());
                    }
                }

                HashMap hashMap = new HashMap(3);
                hashMap.put("data", result.getResult());
                hashMap.put("columns", jsonArray1);
                hashMap.put("dictOptions", map);
                hashMap.put("fieldHrefSlots", json);
                hashMap.put("cgreportHeadName", onlCgreportHead.getName());
                return Result.ok(hashMap);
            } else {
                return result;
            }
        }
    }

    private List<DictModel> getDictMidel(String fieldName, JSONArray jsonArray, String itemFieldName) {
        List<DictModel> list = null;
        if (oConvertUtils.isNotEmpty(fieldName)) {
            if (fieldName.trim().toLowerCase().indexOf("select ") == 0 && (itemFieldName == null || jsonArray.size() > 0)) {
                fieldName = fieldName.trim();
                int index = fieldName.lastIndexOf(";");
                if (index == fieldName.length() - 1) {
                    fieldName = fieldName.substring(0, index);
                }

                String sql = "SELECT * FROM (" + fieldName + ") temp ";
                String str;
                if (jsonArray != null) {
                    ArrayList arrayList = new ArrayList();

                    for(int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String string = jsonObject.getString(itemFieldName);
                        if (StringUtils.isNotBlank(string)) {
                            arrayList.add(string);
                        }
                    }

                    str = "'" + StringUtils.join(arrayList, "','") + "'";
                    sql = sql + "WHERE temp.value IN (" + str + ")";
                }

                List onlCgreportHeadList = ((OnlCgreportHeadMapper)this.onlCgreportHeadService.getBaseMapper()).executeSelete(sql);
                if (onlCgreportHeadList != null && onlCgreportHeadList.size() != 0) {
                    str = JSON.toJSONString(onlCgreportHeadList);
                    list = JSON.parseArray(str, DictModel.class);
                }
            } else {
                list = this.sysBaseAPI.queryDictItemsByCode(fieldName);
            }
        }
        return list;
    }

    /** @deprecated */
    @Deprecated
    @GetMapping({"/getColumns/{code}"})
    public Result<?> getColumns(@PathVariable("code") String code) {
        OnlCgreportHead onlCgreportHead = this.onlCgreportHeadService.getById(code);
        if (onlCgreportHead == null) {
            return Result.error("实体不存在");
        } else {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("cgrhead_id", code);
            queryWrapper.eq("is_show", 1);
            queryWrapper.orderByAsc("order_num");
            List list = this.onlCgreportItemService.list(queryWrapper);
            ArrayList arrayList = new ArrayList();
            HashMap hashMap = new HashMap();
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
                OnlCgreportItem onlCgreportItem = (OnlCgreportItem)iterator.next();
                HashMap map = new HashMap(3);
                map.put("title", onlCgreportItem.getFieldTxt());
                map.put("dataIndex", onlCgreportItem.getFieldName());
                map.put("align", "center");
                map.put("sorter", "true");
                arrayList.add(map);
                String dictCode = onlCgreportItem.getDictCode();
                if (oConvertUtils.isNotEmpty(dictCode)) {
                    List list1 = null;
                    if (dictCode.toLowerCase().indexOf("select ") == 0) {
                        List onlCgreportHeadList = ((OnlCgreportHeadMapper)this.onlCgreportHeadService.getBaseMapper()).executeSelete(dictCode);
                        if (onlCgreportHeadList != null && onlCgreportHeadList.size() != 0) {
                            String headStr = JSON.toJSONString(onlCgreportHeadList);
                            list1 = JSON.parseArray(headStr, DictModel.class);
                        }
                    } else {
                        list1 = this.sysBaseAPI.queryDictItemsByCode(dictCode);
                    }

                    if (list1 != null) {
                        hashMap.put(onlCgreportItem.getFieldName(), list1);
                        map.put("customRender", onlCgreportItem.getFieldName());
                    }
                }
            }

            HashMap map = new HashMap(1);
            map.put("columns", arrayList);
            map.put("dictOptions", hashMap);
            map.put("cgreportHeadName", onlCgreportHead.getName());
            return Result.ok(map);
        }
    }

    @GetMapping({"/getData/{code}"})
    @PermissionData
    public Result<?> getData(@PathVariable("code") String code, HttpServletRequest request) {
        OnlCgreportHead onlCgreportHead = this.onlCgreportHeadService.getById(code);
        if (onlCgreportHead == null) {
            return Result.error("实体不存在");
        } else {
            String cgrSql = onlCgreportHead.getCgrSql().trim();
            String dbSource = onlCgreportHead.getDbSource();

            try {
                Map map = SqlUtil.a(request);
                map.put("getAll", request.getAttribute("getAll"));
                Map map1;
                if (StringUtils.isNotBlank(dbSource)) {
                    logger.info("Online报表: 走了多数据源逻辑");
                    map1 = this.onlCgreportHeadService.executeSelectSqlDynamic(dbSource, cgrSql, map, onlCgreportHead.getId());
                } else {
                    logger.info("Online报表: 走了稳定逻辑");
                    map1 = this.onlCgreportHeadService.executeSelectSql(cgrSql, onlCgreportHead.getId(), map);
                }

                return Result.ok(map1);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return Result.error("SQL执行失败：" + e.getMessage());
            }
        }
    }

    @GetMapping({"/getQueryInfo/{code}"})
    public Result<?> getQueryInfo(@PathVariable("code") String code) {
        try {
            List list = this.onlCgreportItemService.getAutoListQueryInfo(code);
            return Result.ok(list);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return Result.error("查询失败");
        }
    }

    @GetMapping({"/getParamsInfo/{code}"})
    public Result<?> getParamsInfo(@PathVariable("code") String code) {
        try {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("cgrhead_id", code);
            List list = this.onlCgreportParamService.list(queryWrapper);
            return Result.ok(list);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return Result.error("查询失败");
        }
    }

    @PermissionData
    @RequestMapping({"/exportXls/{reportId}"})
    public void exportXls(@PathVariable("reportId") String reportId, HttpServletRequest request, HttpServletResponse response) {
        String str = "报表";
        String str1 = "导出信息";
        if (!oConvertUtils.isNotEmpty(reportId)) {
            throw new ZdException("参数错误");
        } else {
            Map map = null;

            try {
                map = this.onlCgreportHeadService.queryCgReportConfig(reportId);
            } catch (Exception var31) {
                throw new ZdException("动态报表配置不存在!");
            }

            List list = (List)map.get("items");
            request.setAttribute("getAll", true);
            Result result = this.getData(reportId, request);
            List list1 = null;
            if (result.getCode().equals(200)) {
                list1 = (List) ((Map)result.getResult()).get("records");
            }

            ArrayList var32 = new ArrayList();

            String var12;
            for(int var11 = 0; var11 < list.size(); var11++) {
                if ("1".equals(oConvertUtils.getString(((Map)list.get(var11)).get("is_show")))) {
                    var12 = ((Map)list.get(var11)).get("field_name").toString();
                    ExcelExportEntity var13 = new ExcelExportEntity(((Map)list.get(var11)).get("field_txt").toString(), var12, 15);
                    Object var14 = ((Map)list.get(var11)).get("dict_code");
                    JSONArray var15 = JSONObject.parseArray(JSONObject.toJSONString(list1));
                    List var16 = this.getDictMidel(oConvertUtils.getString(var14), var15, var12);
                    if (var16 != null && var16.size() > 0) {
                        ArrayList var17 = new ArrayList();
                        Iterator var18 = var16.iterator();

                        while(var18.hasNext()) {
                            DictModel var19 = (DictModel)var18.next();
                            var17.add(var19.getText() + "_" + var19.getValue());
                        }

                        var13.setReplace((String[])var17.toArray(new String[var17.size()]));
                    }

                    Object var36 = ((Map)list.get(var11)).get("replace_val");
                    if (oConvertUtils.isNotEmpty(var36)) {
                        var13.setReplace(var36.toString().split(","));
                    }

                    var32.add(var13);
                }
            }

            response.setContentType("application/vnd.ms-excel");
            ServletOutputStream var33 = null;

            try {
                var12 = BrowserUtils.checkBrowse(request);
                if ("MSIE".equalsIgnoreCase(var12.substring(0, 4))) {
                    response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(str, "UTF-8") + ".xls");
                } else {
                    String var34 = new String(str.getBytes("UTF-8"), "ISO8859-1");
                    response.setHeader("content-disposition", "attachment;filename=" + var34 + ".xls");
                }

                Workbook var35 = ExcelExportUtil.exportExcel(new ExportParams(null, str1), var32, list1);
                var33 = response.getOutputStream();
                var35.write(var33);
            } catch (Exception var29) {
            } finally {
                try {
                    var33.flush();
                    var33.close();
                } catch (Exception var28) {
                }

            }

        }
    }

    @GetMapping({"/getRpColumns/{code}"})
    public Result<?> getRpColumns(@PathVariable("code") String code) {
        QueryWrapper var2 = new QueryWrapper();
        var2.eq("code", code);
        OnlCgreportHead var3 = this.onlCgreportHeadService.getOne(var2);
        if (var3 == null) {
            return Result.error("实体不存在");
        } else {
            QueryWrapper var4 = new QueryWrapper();
            var4.eq("cgrhead_id", var3.getId());
            var4.eq("is_show", 1);
            var4.orderByAsc("order_num");
            List var5 = this.onlCgreportItemService.list(var4);
            ArrayList var6 = new ArrayList();
            HashMap var7 = new HashMap();

            HashMap var10;
            for(Iterator var8 = var5.iterator(); var8.hasNext(); var6.add(var10)) {
                OnlCgreportItem var9 = (OnlCgreportItem)var8.next();
                var10 = new HashMap(3);
                var10.put("title", var9.getFieldTxt());
                var10.put("dataIndex", var9.getFieldName());
                var10.put("align", "center");
                String var11 = var9.getFieldType();
                if ("Integer".equals(var11) || "Date".equals(var11) || "Long".equals(var11)) {
                    var10.put("sorter", "true");
                }

                String var12 = var9.getDictCode();
                if (oConvertUtils.isNotEmpty(var12)) {
                    List var13 = this.getDictMidel(var9.getDictCode(), (JSONArray)null, (String)null);
                    var7.put(var9.getFieldName(), var13);
                    var10.put("customRender", var9.getFieldName());
                }
            }

            HashMap var14 = new HashMap(1);
            var14.put("columns", var6);
            var14.put("dictOptions", var7);
            var14.put("cgRpConfigId", var3.getId());
            var14.put("cgRpConfigName", var3.getName());
            return Result.ok(var14);
        }
    }

    @PostMapping({"/testConnection"})
    public Result testConnection(@RequestBody DynamicDataSourceModel sourceModel) {
        Connection var2 = null;

        Result var3;
        try {
            Result var4;
            try {
                Class.forName(sourceModel.getDbDriver());
                var2 = DriverManager.getConnection(sourceModel.getDbUrl(), sourceModel.getDbUsername(), sourceModel.getDbPassword());
                if (var2 == null) {
                    var3 = Result.ok("数据库连接失败：错误未知");
                    return var3;
                }
                var3 = Result.ok("数据库连接成功");
            } catch (ClassNotFoundException var17) {
                logger.error(var17.toString());
                var4 = Result.error("数据库连接失败：驱动类不存在");
                return var4;
            } catch (Exception var18) {
                logger.error(var18.toString());
                var4 = Result.error("数据库连接失败：" + var18.getMessage());
                return var4;
            }
        } finally {
            try {
                if (var2 != null && !var2.isClosed()) {
                    var2.close();
                }
            } catch (SQLException var16) {
                logger.error(var16.toString());
            }

        }

        return var3;
    }
}
