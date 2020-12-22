package smartcode.form.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.auth.vo.LoginUser;
import commons.util.oConvertUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.mapper.OnlCgformFieldMapper;
import smartcode.form.mapper.OnlCgformHeadMapper;
import smartcode.form.service.OnlCgformFieldService;
import smartcode.form.utils.DbSelectUtils;
import java.util.*;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:36
 * @Description: zdit.zdboot.auth.online.service.impl
 **/
@Service
public class OnlCgformFieldServiceImpl extends ServiceImpl<OnlCgformFieldMapper, OnlCgformField> implements OnlCgformFieldService {

    @Autowired
    private OnlCgformHeadMapper cgformHeadMapper;

    @Autowired
    private OnlCgformFieldMapper onlCgformFieldMapper;

    @Override
    public List<OnlCgformField> queryAvailableFields(String cgFormId, String tbname, String taskId, boolean isList) {
        QueryWrapper var5 = new QueryWrapper<OnlCgformField>();
        var5.eq("cgform_head_id", cgFormId);
        if (isList) {
            var5.eq("is_show_list", 1);
        } else {
            var5.eq("is_show_form", 1);
        }

        var5.orderByAsc("order_num");
        List var6 = this.list(var5);
        String var7 = "online:" + tbname + "%";
        //TODO :获取用户信息
        LoginUser var8 = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String var9 = var8.getId();
//        String var9 = "e9ca23d68d884d4ebb19d07889727dae";
        ArrayList var10 = new ArrayList();
        List var11;
        if (oConvertUtils.isEmpty(taskId)) {
            var11 = this.baseMapper.selectOnlineHideColumns(var9, var7);
            if (var11 != null && var11.size() != 0 && var11.get(0) != null) {
                var10.addAll(var11);
            }
        } else if (oConvertUtils.isNotEmpty(taskId)) {
            var11 = this.baseMapper.selectFlowAuthColumns(tbname, taskId, "1");
            if (var11 != null && var11.size() > 0 && var11.get(0) != null) {
                var10.addAll(var11);
            }
        }

        if (var10.size() == 0) {
            return var6;
        } else {
            ArrayList var14 = new ArrayList();

            for(int var12 = 0; var12 < var6.size(); ++var12) {
                OnlCgformField var13 = (OnlCgformField)var6.get(var12);
                if (this.getDbFieldName(var13.getDbFieldName(), var10)) {
                    var14.add(var13);
                }
            }

            return var14;
        }
    }

    @Override
    public List<String> queryDisabledFields(String tableName) {
        String var2 = "online:" + tableName + "%";
        LoginUser var3 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        String var4 = var3.getId();
//        String var4 = "e9ca23d68d884d4ebb19d07889727dae";
        List var5 = this.baseMapper.selectOnlineDisabledColumns(var4, var2);
        return this.getStr(var5);
    }

    @Override
    public List<String> selectOnlineHideColumns(String tableName) {
        String var2 = "online:" + tableName + "%";
        LoginUser var3 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        String var4 = var3.getId();
        //TODO :获取用户信息
//        String var4 = "e9ca23d68d884d4ebb19d07889727dae";
        List var5 = this.baseMapper.selectOnlineHideColumns(var4, var2);
        return this.getList(var5);
    }

    @Override
    public Map<String, Object> queryAutolistPage(String tbname, String headId, Map<String, Object> params, List<String> needList) {
        HashMap var5 = new HashMap();
        QueryWrapper var6 = new QueryWrapper<OnlCgformField>();
        var6.eq("cgform_head_id", headId);
        var6.orderByAsc("order_num");
        List<OnlCgformField> var7 = this.list(var6);
        List var8 = this.queryAvailableFields(tbname, true, var7, needList);
        StringBuffer var9 = new StringBuffer();
        DbSelectUtils.a(tbname, var8, var9);
        String var10 = DbSelectUtils.a(var7, params, needList);
        String var11 = DbSelectUtils.a(params);
        var9.append(" where 1=1  " + var10 + var11);
        Object var12 = params.get("column");
        if (var12 != null) {
            String var13 = var12.toString();
            String var14 = params.get("order").toString();
            if (this.a(var13, var7)) {
                var9.append(" ORDER BY " + oConvertUtils.camelToUnderline(var13));
                if ("asc".equals(var14)) {
                    var9.append(" asc");
                } else {
                    var9.append(" desc");
                }
            }
        }

        Integer var17 = params.get("pageSize") == null ? 10 : Integer.parseInt(params.get("pageSize").toString());
        if (var17 == -521) {
            List var18 = this.onlCgformFieldMapper.queryListBySql(var9.toString());
            if (var18 != null && var18.size() != 0) {
                var5.put("total", var18.size());
                var5.put("fieldList", var8);
                var5.put("records", DbSelectUtils.d(var18));
            } else {
                var5.put("total", 0);
                var5.put("fieldList", var8);
            }
        } else {
            Integer var19 = params.get("pageNo") == null ? 1 : Integer.parseInt(params.get("pageNo").toString());
            Page var15 = new Page((long)var19, (long)var17);
            IPage var16 = this.onlCgformFieldMapper.selectPageBySql(var15, var9.toString());
            var5.put("total", var16.getTotal());
            var5.put("records", DbSelectUtils.d(var16.getRecords()));
        }

        return var5;
    }

    @Override
    public List<OnlCgformField> queryFormFieldsByTableName(String tableName) {
        OnlCgformHead var2 = this.cgformHeadMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",tableName));
        if (var2 != null) {
            QueryWrapper var3 = new QueryWrapper<OnlCgformField>();
            var3.eq("cgform_head_id", var2.getId());
            return this.list(var3);
        } else {
            return null;
        }
    }

    @Override
    public List<Map<String, String>> getAutoListQueryInfo(String code) {
        QueryWrapper var2 = new QueryWrapper<OnlCgformField>();
        var2.eq("cgform_head_id", code);
        var2.eq("is_query", 1);
        var2.orderByAsc("order_num");
        List var3 = this.list(var2);
        ArrayList var4 = new ArrayList();
        int var5 = 0;

        HashMap var8;
        for(Iterator var6 = var3.iterator(); var6.hasNext(); var4.add(var8)) {
            OnlCgformField var7 = (OnlCgformField)var6.next();
            var8 = new HashMap();
            var8.put("label", var7.getDbFieldTxt());
            var8.put("field", var7.getDbFieldName());
            var8.put("mode", var7.getQueryMode());
            String[] var9;
            String var10;
            if ("1".equals(var7.getQueryConfigFlag())) {
                var8.put("config", "1");
                var8.put("view", var7.getQueryShowType());
                var8.put("defValue", var7.getQueryDefVal());
                if ("cat_tree".equals(var7.getFieldShowType())) {
                    var8.put("pcode", var7.getQueryDictField());
                } else if ("sel_tree".equals(var7.getFieldShowType())) {
                    var9 = var7.getQueryDictText().split(",");
                    var10 = var7.getQueryDictTable() + "," + var9[2] + "," + var9[0];
                    var8.put("dict", var10);
                    var8.put("pidField", var9[1]);
                    var8.put("hasChildField", var9[3]);
                    var8.put("pidValue", var7.getQueryDictField());
                } else {
                    var8.put("dictTable", var7.getQueryDictTable());
                    var8.put("dictCode", var7.getQueryDictField());
                    var8.put("dictText", var7.getQueryDictText());
                }
            } else {
                var8.put("view", var7.getFieldShowType());
                if ("cat_tree".equals(var7.getFieldShowType())) {
                    var8.put("pcode", var7.getDictField());
                } else if ("sel_tree".equals(var7.getFieldShowType())) {
                    var9 = var7.getDictText().split(",");
                    var10 = var7.getDictTable() + "," + var9[2] + "," + var9[0];
                    var8.put("dict", var10);
                    var8.put("pidField", var9[1]);
                    var8.put("hasChildField", var9[3]);
                    var8.put("pidValue", var7.getDictField());
                } else if ("popup".equals(var7.getFieldShowType())) {
                    var8.put("dictTable", var7.getDictTable());
                    var8.put("dictCode", var7.getDictField());
                    var8.put("dictText", var7.getDictText());
                }

                var8.put("mode", var7.getQueryMode());
            }

            ++var5;
            if (var5 > 2) {
                var8.put("hidden", "1");
            }
        }

        return var4;
    }

    @Override
    public String queryTreeChildIds(OnlCgformHead head, String ids) {
        String var3 = head.getTreeParentIdField();
        String var4 = head.getTableName();
        String[] var5 = ids.split(",");
        StringBuffer var6 = new StringBuffer();
        String[] var7 = var5;
        int var8 = var5.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            if (var10 != null && !var6.toString().contains(var10)) {
                if (var6.toString().length() > 0) {
                    var6.append(",");
                }

                var6.append(var10);
                this.a(var10, var3, var4, var6);
            }
        }

        return var6.toString();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteAutoListMainAndSub(OnlCgformHead head, String ids) {
        if (head.getTableType() == 2) {
            String var3 = head.getId();
            String var4 = head.getTableName();
            String var5 = "tableName";
            String var6 = "linkField";
            String var7 = "linkValueStr";
            String var8 = "mainField";
            ArrayList var9 = new ArrayList();
            if (oConvertUtils.isNotEmpty(head.getSubTableStr())) {
                String[] var10 = head.getSubTableStr().split(",");
                int var11 = var10.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    String var13 = var10[var12];
                    OnlCgformHead var14 = this.cgformHeadMapper.selectOne(new QueryWrapper<OnlCgformHead>().eq("table_name",var13));
                    if (var14 != null) {
                        QueryWrapper var15 = new QueryWrapper<OnlCgformField>().eq("cgform_head_id",var14.getId()).eq("main_table",head.getTableName());
                        List var16 = this.list(var15);
                        if (var16 != null && var16.size() != 0) {
                            OnlCgformField var17 = (OnlCgformField)var16.get(0);
                            HashMap var18 = new HashMap();
                            var18.put(var6, var17.getDbFieldName());
                            var18.put(var8, var17.getMainField());
                            var18.put(var5, var13);
                            var18.put(var7, "");
                            var9.add(var18);
                        }
                    }
                }

                QueryWrapper var24 = new QueryWrapper<OnlCgformField>();
                var24.eq("cgform_head_id", var3);
                List var25 = this.list(var24);
                String[] var26 = ids.split(",");
                String[] var27 = var26;
                int var29 = var26.length;
                int var31 = 0;

                label52:
                while(true) {
                    if (var31 >= var29) {
                        Iterator var28 = var9.iterator();

                        while(true) {
                            if (!var28.hasNext()) {
                                break label52;
                            }

                            Map var30 = (Map)var28.next();
                            this.deleteAutoList((String)var30.get(var5), (String)var30.get(var6), (String)var30.get(var7));
                        }
                    }

                    String var32 = var27[var31];
                    String var33 = DbSelectUtils.a(var4, var25, var32);
                    Map var34 = this.onlCgformFieldMapper.queryFormData(var33);
                    new ArrayList();
                    Iterator var20 = var9.iterator();

                    while(var20.hasNext()) {
                        Map var21 = (Map)var20.next();
                        Object var22 = var34.get(((String)var21.get(var8)).toLowerCase());
                        if (var22 == null) {
                            var22 = var34.get(((String)var21.get(var8)).toUpperCase());
                        }

                        if (var22 != null) {
                            String var23 = (String)var21.get(var7) + String.valueOf(var22) + ",";
                            var21.put(var7, var23);
                        }
                    }

                    ++var31;
                }
            }

            this.deleteAutoListById(head.getTableName(), ids);
        }

    }

    public void deleteAutoListById(String tbname, String ids) {
        this.deleteAutoList(tbname, "id", ids);
    }

    @Override
    public void updateTreeNodeNoChild(String tableName, String filed, String id) {
        Map var4 = DbSelectUtils.a(tableName, filed, id);
        this.baseMapper.executeUpdatetSQL(var4);
    }

    @Override
    public void saveFormData(List<OnlCgformField> fieldList, String tbname, JSONObject json) {
        Map var4 = DbSelectUtils.a(tbname, fieldList, json);
        this.baseMapper.executeInsertSQL(var4);
    }

    @Override
    public void saveFormData(String code, String tbname, JSONObject json, boolean isCrazy) {
        QueryWrapper var5 = new QueryWrapper<OnlCgformField>();
        var5.eq("cgform_head_id", code);
        List var6 = this.list(var5);
        if (isCrazy) {
            this.baseMapper.executeInsertSQL(DbSelectUtils.c(tbname, var6, json));
        } else {
            this.baseMapper.executeInsertSQL(DbSelectUtils.a(tbname, var6, json));
        }

    }

    @Override
    public void editFormData(String code, String tbname, JSONObject json, boolean isCrazy) {
        QueryWrapper var5 = new QueryWrapper<OnlCgformField>();
        var5.eq("cgform_head_id", code);
        List var6 = this.list(var5);
        if (isCrazy) {
            this.baseMapper.executeUpdatetSQL(DbSelectUtils.d(tbname, var6, json));
        } else {
            this.baseMapper.executeUpdatetSQL(DbSelectUtils.b(tbname, var6, json));
        }

    }

    @Override
    public void editTreeFormData(String code, String tbname, JSONObject json, String hasChildField, String pidField) {
        String var6 = DbSelectUtils.f(tbname);
        String var7 = "select * from " + var6 + " where id = '" + json.getString("id") + "'";
        Map var8 = this.baseMapper.queryFormData(var7);
        Map var9 = DbSelectUtils.b(var8);
        String var10 = var9.get(pidField).toString();
        QueryWrapper var11 = new QueryWrapper<OnlCgformField>();
        var11.eq("cgform_head_id", code);
        List var12 = this.list(var11);
        Iterator var13 = var12.iterator();

        while(var13.hasNext()) {
            OnlCgformField var14 = (OnlCgformField)var13.next();
            if (pidField.equals(var14.getDbFieldName()) && oConvertUtils.isEmpty(json.get(pidField))) {
                var14.setIsShowForm(1);
                json.put(pidField, "0");
            }
        }

        Map var16 = DbSelectUtils.b(tbname, var12, json);
        this.baseMapper.executeUpdatetSQL(var16);
        if (!var10.equals(json.getString(pidField))) {
            if (!"0".equals(var10)) {
                String var17 = "select count(*) from " + var6 + " where " + pidField + " = '" + var10 + "'";
                Integer var15 = this.baseMapper.queryCountBySql(var17);
                if (var15 == null || var15 == 0) {
                    this.baseMapper.editFormData("update " + var6 + " set " + hasChildField + " = '0' where id = '" + var10 + "'");
                }
            }

            if (!"0".equals(json.getString(pidField))) {
                this.baseMapper.editFormData("update " + var6 + " set " + hasChildField + " = '1' where id = '" + json.getString(pidField) + "'");
            }
        }

    }

    @Override
    public void saveTreeFormData(String code, String tbname, JSONObject json, String hasChildField, String pidField) {
        QueryWrapper var6 = new QueryWrapper<OnlCgformField>();
        var6.eq("cgformhead_id", code);
        List var7 = this.list(var6);
        Iterator var8 = var7.iterator();

        while(true) {
            while(var8.hasNext()) {
                OnlCgformField var9 = (OnlCgformField)var8.next();
                if (hasChildField.equals(var9.getDbFieldName()) && var9.getIsShowForm() != 1) {
                    var9.setIsShowForm(1);
                    json.put(hasChildField, "0");
                } else if (pidField.equals(var9.getDbFieldName()) && oConvertUtils.isEmpty(json.get(pidField))) {
                    var9.setIsShowForm(1);
                    json.put(pidField, "0");
                }
            }

            Map var10 = DbSelectUtils.a(tbname, var7, json);
            this.baseMapper.executeInsertSQL(var10);
            if (!"0".equals(json.getString(pidField))) {
                this.baseMapper.editFormData("update " + tbname + " set " + hasChildField + " = '1' where id = '" + json.getString(pidField) + "'");
            }
            return;
        }
    }

    public Map<String, Object> queryFormData(String code, String tbname, String id) {
        QueryWrapper var4 = new QueryWrapper<OnlCgformField>();
        var4.eq("cgform_head_id", code);
        var4.eq("is_show_form", 1);
        List var5 = this.list(var4);
        String var6 = DbSelectUtils.a(tbname, var5, id);
        return this.onlCgformFieldMapper.queryFormData(var6);
    }

    public List<OnlCgformField> queryFormFields(String code, boolean isform) {
        QueryWrapper var3 = new QueryWrapper<OnlCgformField>();
        var3.eq("cgform_head_id", code);
        if (isform) {
            var3.eq("is_show_form", 1);
        }

        return this.list(var3);
    }

    public Map<String, Object> queryFormData(List<OnlCgformField> fieldList, String tbname, String id) {
        String var4 = DbSelectUtils.a(tbname, fieldList, id);
        return this.onlCgformFieldMapper.queryFormData(var4);
    }

    public List<Map<String, Object>> querySubFormData(List<OnlCgformField> fieldList, String tbname, String linkField, String value) {
        String var5 = DbSelectUtils.a(tbname, fieldList, linkField, value);
        return this.onlCgformFieldMapper.queryListData(var5);
    }

    public void deleteAutoList(String tbname, String linkField, String linkValue) {
        if (linkValue != null && !"".equals(linkValue)) {
            String[] var4 = linkValue.split(",");
            StringBuffer var5 = new StringBuffer();
            String[] var6 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String var9 = var6[var8];
                if (var9 != null && !"".equals(var9)) {
                    var5.append("'" + var9 + "',");
                }
            }

            String var10 = var5.toString();
            String var11 = "DELETE FROM " + DbSelectUtils.f(tbname) + " where " + linkField + " in(" + var10.substring(0, var10.length() - 1) + ")";
//            OnlCgreportAPI.info("--删除sql-->" + var11);
            this.onlCgformFieldMapper.deleteAutoList(var11);
        }

    }

    private StringBuffer a(String var1, String var2, String var3, StringBuffer var4) {
        String var5 = "select * from " + DbSelectUtils.f(var3) + " where " + var2 + "= '" + var1 + "'";
        List var6 = this.onlCgformFieldMapper.queryListBySql(var5);
        Map var8;
        if (var6 != null && var6.size() > 0) {
            for(Iterator var7 = var6.iterator(); var7.hasNext(); this.a(var8.get("id").toString(), var2, var3, var4)) {
                var8 = (Map)var7.next();
                if (!var4.toString().contains(var8.get("id").toString())) {
                    var4.append(",").append(var8.get("id"));
                }
            }
        }

        return var4;
    }

    public boolean a(String var1, List<OnlCgformField> var2) {
        boolean var3 = false;
        Iterator var4 = var2.iterator();

        while(var4.hasNext()) {
            OnlCgformField var5 = (OnlCgformField)var4.next();
            if (oConvertUtils.camelToUnderline(var1).equals(var5.getDbFieldName())) {
                var3 = true;
                break;
            }
        }

        return var3;
    }

    public List<OnlCgformField> queryAvailableFields(String tbname, boolean isList, List<OnlCgformField> List, List<String> needList) {
        ArrayList var5 = new ArrayList();
        String var6 = "online:" + tbname + "%";
        LoginUser var7 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        String var8 = var7.getId();
        List var9 = this.baseMapper.selectOnlineHideColumns(var8, var6);
        boolean var10 = true;
        if (var9 == null || var9.size() == 0 || var9.get(0) == null) {
            var10 = false;
        }

        Iterator var11 = List.iterator();

        while(true) {
            while(var11.hasNext()) {
                OnlCgformField var12 = (OnlCgformField)var11.next();
                String var13 = var12.getDbFieldName();
                if (needList != null && needList.contains(var13)) {
                    var12.setIsQuery(1);
                    var5.add(var12);
                } else {
                    if (isList) {
                        if (var12.getIsShowList() != 1) {
                            if (oConvertUtils.isNotEmpty(var12.getMainTable()) && oConvertUtils.isNotEmpty(var12.getMainField())) {
                                var5.add(var12);
                            }
                            continue;
                        }
                    } else if (var12.getIsShowForm() != 1) {
                        continue;
                    }

                    if (var10) {
                        if (this.getDbFieldName(var13, var9)) {
                            var5.add(var12);
                        }
                    } else {
                        var5.add(var12);
                    }
                }
            }

            return var5;
        }
    }


    private List<String> getList(List<String> var1) {
        ArrayList var2 = new ArrayList();
        if (var1 != null && var1.size() != 0 && var1.get(0) != null) {
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
                String var4 = (String)var3.next();
                if (!oConvertUtils.isEmpty(var4)) {
                    String var5 = var4.substring(var4.lastIndexOf(":") + 1);
                    if (!oConvertUtils.isEmpty(var5)) {
                        var2.add(var5);
                    }
                }
            }

            return var2;
        } else {
            return var2;
        }
    }

    private List<String> getStr(List<String> var1) {
        ArrayList var2 = new ArrayList();
        if (var1 != null && var1.size() != 0 && var1.get(0) != null) {
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
                String var4 = (String)var3.next();
                if (!oConvertUtils.isEmpty(var4)) {
                    String var5 = var4.substring(var4.lastIndexOf(":") + 1);
                    if (!oConvertUtils.isEmpty(var5)) {
                        var2.add(var5);
                    }
                }
            }

            return var2;
        } else {
            return var2;
        }
    }

    private boolean getDbFieldName(String var1, List<String> var2) {
        boolean var3 = true;

        for(int var4 = 0; var4 < var2.size(); ++var4) {
            String var5 = (String)var2.get(var4);
            if (!oConvertUtils.isEmpty(var5)) {
                String var6 = var5.substring(var5.lastIndexOf(":") + 1);
                if (!oConvertUtils.isEmpty(var6) && var6.equals(var1)) {
                    var3 = false;
                }
            }
        }

        return var3;
    }
}
