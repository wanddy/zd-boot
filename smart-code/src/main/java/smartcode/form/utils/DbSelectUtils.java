package smartcode.form.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import commons.auth.query.MatchTypeEnum;
import commons.auth.query.QueryGenerator;
import commons.auth.query.QueryRuleEnum;
import commons.auth.vo.DictModel;
import commons.auth.vo.LoginUser;
import commons.auth.vo.SysPermissionDataRuleModel;
import commons.exception.ZdException;
import commons.poi.utils.ApplicationContextUtil;
import commons.system.api.ISysBaseAPI;
import commons.util.DateUtils;
import commons.util.SpringContextUtils;
import commons.util.UUIDGenerator;
import commons.util.jsonschema.BaseColumn;
import commons.util.jsonschema.CommonProperty;
import commons.util.jsonschema.JsonSchemaDescrip;
import commons.util.jsonschema.JsonschemaUtil;
import commons.util.jsonschema.validate.*;
import commons.util.oConvertUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.config.exception.DBException;
import smartcode.config.utils.FUtils;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.enums.CgformValidPatternEnum;
import smartcode.form.mapper.OnlCgformHeadMapper;
import smartcode.form.model.EModel;
import smartcode.form.service.OnlCgformFieldService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/25 14:27
 * @Description: zdit.zdboot.smartcode.online.util
 **/
@Data
public class DbSelectUtils {
    private static final Logger as = LoggerFactory.getLogger(DbSelectUtils.class);
    public static final String a = "SELECT ";
    public static final String b = " FROM ";
    public static final String c = " AND ";
    public static final String d = " like ";
    public static final String e = " COUNT(*) ";
    public static final String f = " where 1=1  ";
    public static final String g = " ORDER BY ";
    public static final String h = "asc";
    public static final String i = "desc";
    public static final String j = "=";
    public static final String k = "!=";
    public static final String l = ">=";
    public static final String m = ">";
    public static final String n = "<=";
    public static final String o = "<";
    public static final String p = "Y";
    public static final String q = "$";
    public static final String r = "CREATE_TIME";
    public static final String s = "CREATE_BY";
    public static final String t = "UPDATE_TIME";
    public static final String u = "UPDATE_BY";
    public static final String v = "SYS_ORG_CODE";
    public static final int w = 2;
    public static final String x = "'";
    public static final String y = "N";
    public static final String z = ",";
    public static final String A = "single";
    public static final String B = "id";
    public static final String C = "bpm_status";
    public static final String D = "1";
    public static final String E = "force";
    public static final String F = "normal";
    public static final String G = "switch";
    public static final String H = "popup";
    public static final String I = "image";
    public static final String J = "sel_tree";
    public static final String K = "cat_tree";
    public static final String L = "link_down";
    public static final String M = "SYS_USER";
    public static final String N = "REALNAME";
    public static final String O = "USERNAME";
    public static final String P = "SYS_DEPART";
    public static final String Q = "DEPART_NAME";
    public static final String R = "ID";
    public static final String S = "SYS_CATEGORY";
    public static final String T = "NAME";
    public static final String U = "CODE";
    public static final String V = "ID";
    public static final String W = "PID";
    public static final String X = "HAS_CHILD";
    public static final String Y = "sel_search";
    public static final String Z = "sub-table-design_";
    public static final String aa = "import";
    public static final String ab = "export";
    public static final String ac = "query";
    public static final String ad = "form";
    public static final String ae = "list";
    public static final String af = "1";
    public static final String ag = "start";
    public static final String ah = "erp";
    public static final String ai = "exportSingleOnly";
    public static final String aj = "isSingleTableImport";
    public static final String ak = "foreignKeys";
    public static final int al = 1;
    public static final int am = 2;
    public static final int an = 0;
    public static final int ao = 1;
    public static final String ap = "1";
    public static final String aq = "id";
    public static final String ar = "center";
    private static final String at = "beforeAdd,beforeEdit,afterAdd,afterEdit,beforeDelete,afterDelete,mounted,created";
    private static String au;

    public static String a() {
        long var0 = IdWorker.getId();
        return String.valueOf(var0);
    }

    public static boolean a(OnlCgformHead var0, OnlCgformHead var1) {
        return !a((Object)var0.getTableName(), (Object)var1.getTableName()) || !a((Object)var0.getTableTxt(), (Object)var1.getTableTxt());
    }

    public static boolean a(Object var0, Object var1) {
        if (oConvertUtils.isEmpty(var0) && oConvertUtils.isEmpty(var1)) {
            return true;
        } else {
            return oConvertUtils.isNotEmpty(var0) && var0.equals(var1);
        }
    }

    public static String a(Exception var0) {
        String var1 = var0.getCause() != null ? var0.getCause().getMessage() : var0.getMessage();
        if (var1.indexOf("ORA-01452") != -1) {
            var1 = "ORA-01452: 无法 CREATE UNIQUE INDEX; 找到重复的关键字";
        } else if (var1.indexOf("duplicate key") != -1) {
            var1 = "无法 CREATE UNIQUE INDEX; 找到重复的关键字";
        }

        return var1;
    }

    public static Map<String, Object> d(String var0, List<OnlCgformField> var1, JSONObject var2) {
        StringBuffer var3 = new StringBuffer();
        HashMap var4 = new HashMap();
        String var5 = "";

        try {
            var5 = dUtils.getDatabaseType();
        } catch (SQLException var11) {
            var11.printStackTrace();
        } catch (DBException var12) {
            var12.printStackTrace();
        }

        LoginUser var6 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if (var6 == null) {
            throw new ZdException("online保存表单数据异常:系统未找到当前登陆用户信息");
        } else {
            Iterator var7 = var1.iterator();

            while(true) {
                while(var7.hasNext()) {
                    OnlCgformField var8 = (OnlCgformField)var7.next();
                    String var9 = var8.getDbFieldName();
                    if (null == var9) {
                        as.info("--------online修改表单数据遇见空名称的字段------->>" + var8.getId());
                    } else if (!"id".equals(var9) && (var2.get(var9) != null || "UPDATE_BY".equalsIgnoreCase(var9) || "UPDATE_TIME".equalsIgnoreCase(var9) || "SYS_ORG_CODE".equalsIgnoreCase(var9))) {
                        a(var8, var6, var2, "UPDATE_BY", "UPDATE_TIME", "SYS_ORG_CODE");
                        String var10;
                        if ("".equals(var2.get(var9))) {
                            var10 = var8.getDbType();
                            if (FUtils.a(var10) || FUtils.b(var10)) {
                                continue;
                            }
                        }

                        var10 = FUtils.a(var5, var8, var2, var4);
                        var3.append(var9 + "=" + var10 + ",");
                    }
                }

                String var13 = var3.toString();
                if (var13.endsWith(",")) {
                    var13 = var13.substring(0, var13.length() - 1);
                }

                String var14 = "update " + f(var0) + " set " + var13 + " where 1=1  " + " AND " + "id" + "=" + "'" + var2.getString("id") + "'";
                as.info("--表单设计器表单编辑sql-->" + var14);
                var4.put("execute_sql_string", var14);
                return var4;
            }
        }
    }

    public static Map<String, Object> b(String var0, List<OnlCgformField> var1, JSONObject var2) {
        StringBuffer var3 = new StringBuffer();
        HashMap var4 = new HashMap();
        String var5 = "";

        try {
            var5 = dUtils.getDatabaseType();
        } catch (SQLException var12) {
            var12.printStackTrace();
        } catch (DBException var13) {
            var13.printStackTrace();
        }

        LoginUser var6 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if (var6 == null) {
            throw new ZdException("online修改表单数据异常:系统未找到当前登陆用户信息");
        } else {
            Set var7 = a(var1);
            Iterator var8 = var1.iterator();

            while(true) {
                while(var8.hasNext()) {
                    OnlCgformField var9 = (OnlCgformField)var8.next();
                    String var10 = var9.getDbFieldName();
                    if (null == var10) {
                        as.info("--------online修改表单数据遇见空名称的字段------->>" + var9.getId());
                    } else {
                        a(var9, var6, var2, "UPDATE_BY", "UPDATE_TIME", "SYS_ORG_CODE");
                        String var11;
                        if (var7.contains(var10) && var2.get(var10) != null && !"".equals(var2.getString(var10))) {
                            var11 = FUtils.a(var5, var9, var2, var4);
                            var3.append(var10 + "=" + var11 + ",");
                        } else if (var9.getIsShowForm() == 1 && !"id".equals(var10)) {
                            if ("".equals(var2.get(var10))) {
                                var11 = var9.getDbType();
                                if (FUtils.a(var11) || FUtils.b(var11)) {
                                    continue;
                                }
                            }

                            if (!oConvertUtils.isNotEmpty(var9.getMainTable()) || !oConvertUtils.isNotEmpty(var9.getMainField())) {
                                var11 = FUtils.a(var5, var9, var2, var4);
                                var3.append(var10 + "=" + var11 + ",");
                            }
                        }
                    }
                }

                String var14 = var3.toString();
                if (var14.endsWith(",")) {
                    var14 = var14.substring(0, var14.length() - 1);
                }

                String var15 = "update " + f(var0) + " set " + var14 + " where 1=1  " + " AND " + "id" + "=" + "'" + var2.getString("id") + "'";
                as.info("--动态表单编辑sql-->" + var15);
                var4.put("execute_sql_string", var15);
                return var4;
            }
        }
    }

    public static Map<String, Object> b(Map<String, Object> var0) {
        HashMap var1 = new HashMap();
        if (var0 != null && !var0.isEmpty()) {
            Set var2 = var0.keySet();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
                String var4 = (String)var3.next();
                Object var5 = var0.get(var4);
                if (var5 instanceof Clob) {
                    var5 = a((Clob)var5);
                } else if (var5 instanceof byte[]) {
                    var5 = new String((byte[])((byte[])var5));
                } else if (var5 instanceof Blob) {
                    try {
                        if (var5 != null) {
                            Blob var6 = (Blob)var5;
                            var5 = new String(var6.getBytes(1L, (int)var6.length()), "UTF-8");
                        }
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }
                }

                String var8 = var4.toLowerCase();
                var1.put(var8, var5);
            }

            return var1;
        } else {
            return var1;
        }
    }

    public static Map<String, Object> a(String var0, List<OnlCgformField> var1, JSONObject var2) {
        StringBuffer var3 = new StringBuffer();
        StringBuffer var4 = new StringBuffer();
        String var5 = "";

        try {
            var5 = dUtils.getDatabaseType();
        } catch (SQLException var15) {
            var15.printStackTrace();
        } catch (DBException var16) {
            var16.printStackTrace();
        }

        HashMap var6 = new HashMap();
        boolean var7 = false;
        String var8 = null;
        LoginUser var9 = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (var9 == null) {
            throw new ZdException("online保存表单数据异常:系统未找到当前登陆用户信息");
        } else {
            Set var10 = a(var1);
            Iterator var11 = var1.iterator();

            while(true) {
                while(var11.hasNext()) {
                    OnlCgformField var12 = (OnlCgformField)var11.next();
                    String var13 = var12.getDbFieldName();
                    if (null == var13) {
                        as.info("--------online保存表单数据遇见空名称的字段------->>" + var12.getId());
                    } else if ("id".equals(var13.toLowerCase())) {
                        var7 = true;
                        var8 = var2.getString(var13);
                    } else {
                        a(var12, var9, var2, "CREATE_BY", "CREATE_TIME", "SYS_ORG_CODE");
                        if ("bpm_status".equals(var13.toLowerCase())) {
                            var3.append("," + var13);
                            var4.append(",'1'");
                        } else {
                            String var14;
                            if (var10.contains(var13)) {
                                var3.append("," + var13);
                                var14 = FUtils.a(var5, var12, var2, var6);
                                var4.append("," + var14);
                            } else if (var12.getIsShowForm() == 1 || !oConvertUtils.isEmpty(var12.getMainField()) || !oConvertUtils.isEmpty(var12.getDbDefaultVal())) {
                                if (var2.get(var13) == null) {
                                    if (oConvertUtils.isEmpty(var12.getDbDefaultVal())) {
                                        continue;
                                    }

                                    var2.put(var13, var12.getDbDefaultVal());
                                }

                                if ("".equals(var2.get(var13))) {
                                    var14 = var12.getDbType();
                                    if (FUtils.a(var14) || FUtils.b(var14)) {
                                        continue;
                                    }
                                }

                                var3.append("," + var13);
                                var14 = FUtils.a(var5, var12, var2, var6);
                                var4.append("," + var14);
                            }
                        }
                    }
                }

                if (var7) {
                    if (oConvertUtils.isEmpty(var8)) {
                        var8 = a();
                    }
                } else {
                    var8 = a();
                }

                String var17 = "insert into " + f(var0) + "(" + "id" + var3.toString() + ") values(" + "'" + var8 + "'" + var4.toString() + ")";
                var6.put("execute_sql_string", var17);
                as.info("--动态表单保存sql-->" + var17);
                return var6;
            }
        }
    }

    public static void a(OnlCgformField var0, LoginUser var1, JSONObject var2, String... var3) {
        String var4 = var0.getDbFieldName();
        boolean var5 = false;
        String[] var6 = var3;
        int var7 = var3.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            if (var4.toUpperCase().equals(var9)) {
                if (var0.getIsShowForm() == 1) {
                    if (var2.get(var4) == null) {
                        var5 = true;
                    }
                } else {
                    var0.setIsShowForm(1);
                    var5 = true;
                }

                if (var5) {
                    byte var11 = -1;
                    switch(var9.hashCode()) {
                        case -909973894:
                            if (var9.equals("CREATE_BY")) {
                                var11 = 0;
                            }
                            break;
                        case -99751974:
                            if (var9.equals("SYS_ORG_CODE")) {
                                var11 = 4;
                            }
                            break;
                        case 837427085:
                            if (var9.equals("UPDATE_BY")) {
                                var11 = 2;
                            }
                            break;
                        case 1609067651:
                            if (var9.equals("UPDATE_TIME")) {
                                var11 = 3;
                            }
                            break;
                        case 1688939568:
                            if (var9.equals("CREATE_TIME")) {
                                var11 = 1;
                            }
                    }

                    switch(var11) {
                        case 0:
                            var2.put(var4, var1.getUsername());
                            return;
                        case 1:
                            var0.setFieldShowType("datetime");
                            var2.put(var4, DateUtils.formatDateTime());
                            return;
                        case 2:
                            var2.put(var4, var1.getUsername());
                            return;
                        case 3:
                            var0.setFieldShowType("datetime");
                            var2.put(var4, DateUtils.formatDateTime());
                            return;
                        case 4:
                            var2.put(var4, var1.getOrgCode());
                    }
                }
                break;
            }
        }

    }

    public static Set<String> a(List<OnlCgformField> var0) {
        HashSet var1 = new HashSet();
        Iterator var2 = var0.iterator();

        OnlCgformField var3;
        String var4;
        while(var2.hasNext()) {
            var3 = (OnlCgformField)var2.next();
            if ("popup".equals(var3.getFieldShowType())) {
                var4 = var3.getDictText();
                if (var4 != null && !var4.equals("")) {
                    var1.addAll((Collection)Arrays.stream(var4.split(",")).collect(Collectors.toSet()));
                }
            }

            if ("cat_tree".equals(var3.getFieldShowType())) {
                var4 = var3.getDictText();
                if (oConvertUtils.isNotEmpty(var4)) {
                    var1.add(var4);
                }
            }
        }

        var2 = var0.iterator();

        while(var2.hasNext()) {
            var3 = (OnlCgformField)var2.next();
            var4 = var3.getDbFieldName();
            if (var3.getIsShowForm() == 1 && var1.contains(var4)) {
                var1.remove(var4);
            }
        }

        return var1;
    }

    public static String findKeyword(Exception var0) {
        String var1 = var0.getCause() != null ? var0.getCause().getMessage() : var0.getMessage();
        if (var1.indexOf("ORA-01452") != -1) {
            var1 = "ORA-01452: 无法 CREATE UNIQUE INDEX; 找到重复的关键字";
        } else if (var1.indexOf("duplicate key") != -1) {
            var1 = "无法 CREATE UNIQUE INDEX; 找到重复的关键字";
        }
        return var1;
    }

    public static String a(String var0, JSONObject var1) {
        if (var1 == null) {
            return var0;
        } else {
            var0 = var0.replace("#{UUID}", UUIDGenerator.generate());
            Set var2 = QueryGenerator.getSqlRuleParams(var0);
            Iterator var3 = var2.iterator();

            while(true) {
                while(var3.hasNext()) {
                    String var4 = (String)var3.next();
                    String var5;
                    if (var1.get(var4.toUpperCase()) == null && var1.get(var4.toLowerCase()) == null) {
                        var5 = QueryGenerator.converRuleValue(var4);
                        var0 = var0.replace("#{" + var4 + "}", var5);
                    } else {
                        var5 = null;
                        if (var1.containsKey(var4.toLowerCase())) {
                            var5 = var1.getString(var4.toLowerCase());
                        } else if (var1.containsKey(var4.toUpperCase())) {
                            var5 = var1.getString(var4.toUpperCase());
                        }

                        var0 = var0.replace("#{" + var4 + "}", var5);
                    }
                }

                return var0;
            }
        }
    }

    public static String a(Clob var0) {
        String var1 = "";

        try {
            Reader var2 = var0.getCharacterStream();
            char[] var3 = new char[(int)var0.length()];
            var2.read(var3);
            var1 = new String(var3);
            var2.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

        return var1;
    }

    public static Map<String, Object> a(String var0, String var1, String var2) {
        HashMap var3 = new HashMap();
        String var4 = "update " + f(var0) + " set " + var1 + "=" + "'" + 0 + "'" + " where 1=1  " + " AND " + "id" + "=" + "'" + var2 + "'";
        as.info("--修改树节点状态：为无子节点sql-->" + var4);
        var3.put("execute_sql_string", var4);
        return var3;
    }

    public static String a(String var0, List<OnlCgformField> var1, String var2) {
        return a(var0, var1, "id", var2);
    }

    public static String a(String var0, List<OnlCgformField> var1, String var2, String var3) {
        StringBuffer var4 = new StringBuffer();
        var4.append("SELECT ");
        int var5 = var1.size();
        boolean var6 = false;

        for(int var7 = 0; var7 < var5; ++var7) {
            String var8 = ((OnlCgformField)var1.get(var7)).getDbFieldName();
            if ("id".equals(var8)) {
                var6 = true;
            }

            var4.append(var8);
            if (var5 > var7 + 1) {
                var4.append(",");
            }
        }

        if (!var6) {
            var4.append(",id");
        }

        var4.append(" FROM " + f(var0) + " where 1=1  " + " AND " + var2 + "=" + "'" + var3 + "'");
        return var4.toString();
    }

    public static List<Map<String, Object>> d(List<Map<String, Object>> var0) {
        ArrayList var1 = new ArrayList();
        Iterator var2 = var0.iterator();

        while(var2.hasNext()) {
            Map var3 = (Map)var2.next();
            HashMap var4 = new HashMap();
            Set var5 = var3.keySet();
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
                String var7 = (String)var6.next();
                Object var8 = var3.get(var7);
                if (var8 instanceof Clob) {
                    var8 = a((Clob)var8);
                } else if (var8 instanceof byte[]) {
                    var8 = new String((byte[])((byte[])var8));
                } else if (var8 instanceof Blob) {
                    try {
                        if (var8 != null) {
                            Blob var9 = (Blob)var8;
                            var8 = new String(var9.getBytes(1L, (int)var9.length()), "UTF-8");
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }

                String var11 = var7.toLowerCase();
                var4.put(var11, var8);
            }

            var1.add(var4);
        }

        return var1;
    }

    public static void sortDb(List<String> var0) {
        Collections.sort(var0);
    }

    public static String regex(String var0) {
        return Pattern.matches("^[a-zA-z].*\\$\\d+$", var0) ? var0.substring(0, var0.lastIndexOf("$")) : var0;
    }

    public static String a(Map<String, Object> var0) {
        Object var1 = var0.get("superQueryParams");
        if (var1 != null && !StringUtils.isBlank(var1.toString())) {
            OnlCgformFieldService var2 = SpringContextUtils.getBean(OnlCgformFieldService.class);
            String var3 = null;

            try {
                var3 = URLDecoder.decode(var1.toString(), "UTF-8");
            } catch (UnsupportedEncodingException var19) {
                var19.printStackTrace();
                return "";
            }

            JSONArray var4 = JSONArray.parseArray(var3);
            Object var5 = var0.get("superQueryMatchType");
            MatchTypeEnum var6 = MatchTypeEnum.getByValue(var5);
            if (var6 == null) {
                var6 = MatchTypeEnum.AND;
            }

            HashMap var7 = new HashMap();
            StringBuilder var8 = (new StringBuilder(" AND ")).append("(");

            for(int var9 = 0; var9 < var4.size(); ++var9) {
                JSONObject var10 = var4.getJSONObject(var9);
                String var11 = var10.getString("field");
                String[] var12 = var11.split(",");
                if (var12.length == 1) {
                    a(var8, var11, var10, var6, (JSONObject)null, var9 == 0);
                } else if (var12.length == 2) {
                    String var13 = var12[0];
                    String var14 = var12[1];
                    JSONObject var15 = (JSONObject)var7.get(var13);
                    if (var15 == null) {
                        List var16 = var2.queryFormFieldsByTableName(var13);
                        var15 = new JSONObject(3);
                        Iterator var17 = var16.iterator();

                        while(var17.hasNext()) {
                            OnlCgformField var18 = (OnlCgformField)var17.next();
                            if (StringUtils.isNotBlank(var18.getMainTable())) {
                                var15.put("subTableName", var13);
                                var15.put("subField", var18.getDbFieldName());
                                var15.put("mainTable", var18.getMainTable());
                                var15.put("mainField", var18.getMainField());
                            }
                        }

                        var7.put(var13, var15);
                    }

                    a(var8, var14, var10, var6, var15, var9 == 0);
                }
            }

            return var8.append(")").toString();
        } else {
            return "";
        }
    }

    private static void a(StringBuilder var0, String var1, JSONObject var2, MatchTypeEnum var3, JSONObject var4, boolean var5) {
        if (!var5) {
            var0.append(" ").append(var3.getValue()).append(" ");
        }

        String var6 = var2.getString("type");
        String var7 = var2.getString("val");
        String var8 = a(var6, var7);
        QueryRuleEnum var9 = QueryRuleEnum.getByValue(var2.getString("rule"));
        if (var9 == null) {
            var9 = QueryRuleEnum.EQ;
        }

        if (var4 != null) {
            String var10 = var4.getString("subTableName");
            String var11 = var4.getString("subField");
            String var12 = var4.getString("mainTable");
            String var13 = var4.getString("mainField");
            var0.append("(").append(var13).append(" IN (SELECT ").append(var11).append(" FROM ").append(var10).append(" WHERE ").append(var1);
            a(var0, var9, var7, var8, var6);
            var0.append("))");
        } else {
            var0.append(var1);
            a(var0, var9, var7, var8, var6);
        }

    }

    private static String getDatabseType() {
        if (oConvertUtils.isNotEmpty(au)) {
            return au;
        } else {
            try {
                ISysBaseAPI var0 = (ISysBaseAPI) ApplicationContextUtil.getContext().getBean(ISysBaseAPI.class);
                au = var0.getDatabaseType();
                return au;
            } catch (Exception var1) {
                var1.printStackTrace();
                return au;
            }
        }
    }

    private static void a(StringBuilder var0, QueryRuleEnum var1, String var2, String var3, String var4) {
        if ("date".equals(var4) && "ORACLE".equalsIgnoreCase(getDatabseType())) {
            var3 = var3.replace("'", "");
            if (var3.length() == 10) {
                var3 = b(var3);
            } else {
                var3 = a(var3);
            }
        }

        switch(var1.ordinal()) {
            case 1:
                var0.append(">").append(var3);
                break;
            case 2:
                var0.append(">=").append(var3);
                break;
            case 3:
                var0.append("<").append(var3);
                break;
            case 4:
                var0.append("<=").append(var3);
                break;
            case 5:
                var0.append("!=").append(var3);
                break;
            case 6:
                var0.append(" IN (");
                String[] var5 = var2.split(",");

                for(int var6 = 0; var6 < var5.length; ++var6) {
                    String var7 = var5[var6];
                    if (StringUtils.isNotBlank(var7)) {
                        String var8 = a(var4, var7);
                        var0.append(var8);
                        if (var6 < var5.length - 1) {
                            var0.append(",");
                        }
                    }
                }

                var0.append(")");
                break;
            case 7:
                var0.append(" like ").append("N").append("'").append("%").append(var2).append("%").append("'");
                break;
            case 8:
                var0.append(" like ").append("N").append("'").append("%").append(var2).append("'");
                break;
            case 9:
                var0.append(" like ").append("N").append("'").append(var2).append("%").append("'");
                break;
            case 10:
            default:
                var0.append("=").append(var3);
        }

    }

    private static String a(String var0, String var1) {
        if (!"int".equals(var0) && !"number".equals(var0)) {
            if ("date".equals(var0)) {
                return "'" + var1 + "'";
            } else {
                return "SQLSERVER".equals(getDatabseType()) ? "N'" + var1 + "'" : "'" + var1 + "'";
            }
        } else {
            return var1;
        }
    }

    public static void a(String var0, List<OnlCgformField> var1, StringBuffer var2) {
        if (var1 != null && var1.size() != 0) {
            var2.append("SELECT ");
            int var3 = var1.size();
            boolean var4 = false;

            for(int var5 = 0; var5 < var3; ++var5) {
                OnlCgformField var6 = (OnlCgformField)var1.get(var5);
                if ("id".equals(var6.getDbFieldName())) {
                    var4 = true;
                }

                if ("cat_tree".equals(var6.getFieldShowType()) && oConvertUtils.isNotEmpty(var6.getDictText())) {
                    var2.append(var6.getDictText() + ",");
                }

                if (var5 == var3 - 1) {
                    var2.append(var6.getDbFieldName() + " ");
                } else {
                    var2.append(var6.getDbFieldName() + ",");
                }
            }

            if (!var4) {
                var2.append(",id");
            }
        } else {
            var2.append("SELECT id");
        }

        var2.append(" FROM " + f(var0));
    }

    public static String f(String var0) {
        return Pattern.matches("^[a-zA-z].*\\$\\d+$", var0) ? var0.substring(0, var0.lastIndexOf("$")) : var0;
    }

    public static String a(List<OnlCgformField> var0, Map<String, Object> var1, List<String> var2) {
        StringBuffer var3 = new StringBuffer();
        String var4 = "";

        try {
            var4 = dUtils.getDatabaseType();
        } catch (SQLException var13) {
            var13.printStackTrace();
        } catch (DBException var14) {
            var14.printStackTrace();
        }

        Map var5 = QueryGenerator.getRuleMap();
        Iterator var6 = var5.keySet().iterator();

        while(var6.hasNext()) {
            String var7 = (String)var6.next();
            if (oConvertUtils.isNotEmpty(var7) && var7.startsWith("SQL_RULES_COLUMN")) {
                var3.append(" AND (" + QueryGenerator.getSqlRuleValue(((SysPermissionDataRuleModel)var5.get(var7)).getRuleValue()) + ")");
            }
        }

        var6 = var0.iterator();

        while(true) {
            while(true) {
                String var8;
                String var9;
                Object var10;
                do {
                    while(true) {
                        OnlCgformField var15;
                        do {
                            if (!var6.hasNext()) {
                                return var3.toString();
                            }

                            var15 = (OnlCgformField)var6.next();
                            var8 = var15.getDbFieldName();
                            var9 = var15.getDbType();
                            if (var5.containsKey(var8)) {
                                a(var4, (SysPermissionDataRuleModel)var5.get(var8), var8, var9, var3);
                            }

                            if (var5.containsKey(oConvertUtils.camelNames(var8))) {
                                a(var4, (SysPermissionDataRuleModel)var5.get(var8), var8, var9, var3);
                            }

                            if (var2 != null && var2.contains(var8)) {
                                var15.setIsQuery(1);
                                var15.setQueryMode("single");
                            }

                            if (oConvertUtils.isNotEmpty(var15.getMainField()) && oConvertUtils.isNotEmpty(var15.getMainTable())) {
                                var15.setIsQuery(1);
                                var15.setQueryMode("single");
                            }
                        } while(1 != var15.getIsQuery());

                        if ("single".equals(var15.getQueryMode())) {
                            var10 = var1.get(var8);
                            break;
                        }

                        var10 = var1.get(var8 + "_begin");
                        if (var10 != null) {
                            var3.append(" AND " + var8 + ">=");
                            if (FUtils.a(var9)) {
                                var3.append(var10.toString());
                            } else if ("ORACLE".equals(var4) && var9.toLowerCase().indexOf("date") >= 0) {
                                var3.append(a(var10.toString()));
                            } else {
                                var3.append("'" + var10.toString() + "'");
                            }
                        }

                        Object var11 = var1.get(var8 + "_end");
                        if (var11 != null) {
                            var3.append(" AND " + var8 + "<=");
                            if (FUtils.a(var9)) {
                                var3.append(var11.toString());
                            } else if ("ORACLE".equals(var4) && var9.toLowerCase().indexOf("date") >= 0) {
                                var3.append(a(var11.toString()));
                            } else {
                                var3.append("'" + var11.toString() + "'");
                            }
                        }
                    }
                } while(var10 == null);

                if ("ORACLE".equals(var4) && var9.toLowerCase().indexOf("date") >= 0) {
                    var3.append(" AND " + var8 + "=" + a(var10.toString()));
                } else {
                    boolean var16 = !FUtils.a(var9);
                    String var12 = QueryGenerator.getSingleQueryConditionSql(var8, "", var10, var16);
                    var3.append(" AND " + var12);
                }
            }
        }
    }

    private static String a(String var0, boolean var1) {
        return var1 ? "'" + QueryGenerator.converRuleValue(var0) + "'" : QueryGenerator.converRuleValue(var0);
    }

    public static String b(String var0) {
        return " to_date('" + var0 + "','yyyy-MM-dd')";
    }

    public static String a(String var0) {
        return " to_date('" + var0 + "','yyyy-MM-dd HH24:mi:ss')";
    }

    private static void a(String var0, SysPermissionDataRuleModel var1, String var2, String var3, StringBuffer var4) {
        QueryRuleEnum var5 = QueryRuleEnum.getByValue(var1.getRuleConditions());
        boolean var6 = !FUtils.a(var3);
        String var7 = a(var1.getRuleValue(), var6);
        if (var7 != null && var5 != null) {
            if ("ORACLE".equalsIgnoreCase(var0) && "Date".equals(var3)) {
                var7 = var7.replace("'", "");
                if (var7.length() == 10) {
                    var7 = b(var7);
                } else {
                    var7 = a(var7);
                }
            }

            switch(var5.ordinal()) {
                case 1:
                    var4.append(" AND " + var2 + ">" + var7);
                    break;
                case 2:
                    var4.append(" AND " + var2 + ">=" + var7);
                    break;
                case 3:
                    var4.append(" AND " + var2 + "<" + var7);
                    break;
                case 4:
                    var4.append(" AND " + var2 + "<=" + var7);
                    break;
                case 5:
                    var4.append(" AND " + var2 + " <> " + var7);
                    break;
                case 6:
                    var4.append(" AND " + var2 + " IN " + var7);
                    break;
                case 7:
                    var4.append(" AND " + var2 + " LIKE '%" + QueryGenerator.trimSingleQuote(var7) + "%'");
                    break;
                case 8:
                    var4.append(" AND " + var2 + " LIKE '%" + QueryGenerator.trimSingleQuote(var7) + "'");
                    break;
                case 9:
                    var4.append(" AND " + var2 + " LIKE '" + QueryGenerator.trimSingleQuote(var7) + "%'");
                    break;
                case 10:
                    var4.append(" AND " + var2 + "=" + var7);
                    break;
                default:
                    as.info("--查询规则未匹配到---");
            }

        }
    }


    public static List<DictModel> a(OnlCgformField var0) {
        ArrayList var1 = new ArrayList();
        String var2 = var0.getFieldExtendJson();
        String var3 = "是";
        String var4 = "否";
        JSONArray var5 = JSONArray.parseArray("[\"Y\",\"N\"]");
        if (oConvertUtils.isNotEmpty(var2)) {
            var5 = JSONArray.parseArray(var2);
        }

        DictModel var6 = new DictModel(var5.getString(0), var3);
        DictModel var7 = new DictModel(var5.getString(1), var4);
        var1.add(var6);
        var1.add(var7);
        return var1;
    }

    public static Map<String, Object> a(HttpServletRequest var0) {
        Map var1 = var0.getParameterMap();
        HashMap var2 = new HashMap();
        Iterator var3 = var1.entrySet().iterator();
        String var5 = "";
        String var6 = "";

        for(Object var7 = null; var3.hasNext(); var2.put(var5, var6)) {
            Map.Entry var4 = (Map.Entry)var3.next();
            var5 = (String)var4.getKey();
            var7 = var4.getValue();
            if (!"_t".equals(var5) && null != var7) {
                if (!(var7 instanceof String[])) {
                    var6 = var7.toString();
                } else {
                    String[] var8 = (String[])((String[])var7);

                    for(int var9 = 0; var9 < var8.length; ++var9) {
                        var6 = var8[var9] + ",";
                    }

                    var6 = var6.substring(0, var6.length() - 1);
                }
            } else {
                var6 = "";
            }
        }

        return var2;
    }

    public static String e(String var0) {
        return var0 != null && !"".equals(var0) && !"0".equals(var0) ? "CODE like '" + var0 + "%" + "'" : "";
    }

    public static void a(LinkDownProperty var0, List<OnlCgformField> var1, List<String> var2) {
        String var3 = var0.getDictTable();
        JSONObject var4 = JSONObject.parseObject(var3);
        String var5 = var4.getString("linkField");
        ArrayList var6 = new ArrayList();
        if (oConvertUtils.isNotEmpty(var5)) {
            String[] var7 = var5.split(",");
            Iterator var8 = var1.iterator();

            label26:
            while(true) {
                while(true) {
                    if (!var8.hasNext()) {
                        break label26;
                    }

                    OnlCgformField var9 = (OnlCgformField)var8.next();
                    String var10 = var9.getDbFieldName();
                    String[] var11 = var7;
                    int var12 = var7.length;

                    for(int var13 = 0; var13 < var12; ++var13) {
                        String var14 = var11[var13];
                        if (var14.equals(var10)) {
                            var2.add(var10);
                            var6.add(new BaseColumn(var9.getDbFieldTxt(), var10));
                            break;
                        }
                    }
                }
            }
        }

        var0.setOtherColumns(var6);
    }

    private static String c(OnlCgformField var0) {
        if ("checkbox".equals(var0.getFieldShowType())) {
            return "checkbox";
        } else if ("list".equals(var0.getFieldShowType())) {
            return "select";
        } else if ("switch".equals(var0.getFieldShowType())) {
            return "switch";
        } else if (!"image".equals(var0.getFieldShowType()) && !"file".equals(var0.getFieldShowType()) && !"radio".equals(var0.getFieldShowType()) && !"popup".equals(var0.getFieldShowType()) && !"list_multi".equals(var0.getFieldShowType()) && !"sel_search".equals(var0.getFieldShowType())) {
            if ("datetime".equals(var0.getFieldShowType())) {
                return "datetime";
            } else if ("date".equals(var0.getFieldShowType())) {
                return "date";
            } else if ("int".equals(var0.getDbType())) {
                return "inputNumber";
            } else {
                return !"double".equals(var0.getDbType()) && !"BigDecimal".equals(var0.getDbType()) ? "input" : "inputNumber";
            }
        } else {
            return var0.getFieldShowType();
        }
    }

    public static Map<String, Object> c(String var0, List<OnlCgformField> var1, JSONObject var2) {
        StringBuffer var3 = new StringBuffer();
        StringBuffer var4 = new StringBuffer();
        String var5 = "";

        try {
            var5 = dUtils.getDatabaseType();
        } catch (SQLException var14) {
            var14.printStackTrace();
        } catch (DBException var15) {
            var15.printStackTrace();
        }

        HashMap var6 = new HashMap();
        boolean var7 = false;
        String var8 = null;
        LoginUser var9 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if (var9 == null) {
            throw new ZdException("online保存表单数据异常:系统未找到当前登陆用户信息");
        } else {
            Iterator var10 = var1.iterator();

            while(true) {
                while(var10.hasNext()) {
                    OnlCgformField var11 = (OnlCgformField)var10.next();
                    String var12 = var11.getDbFieldName();
                    if (null == var12) {
                        as.info("--------online保存表单数据遇见空名称的字段------->>" + var11.getId());
                    } else if (var2.get(var12) != null || "CREATE_BY".equalsIgnoreCase(var12) || "CREATE_TIME".equalsIgnoreCase(var12) || "SYS_ORG_CODE".equalsIgnoreCase(var12)) {
                        a(var11, var9, var2, "CREATE_BY", "CREATE_TIME", "SYS_ORG_CODE");
                        String var13;
                        if ("".equals(var2.get(var12))) {
                            var13 = var11.getDbType();
                            if (FUtils.a(var13) || FUtils.b(var13)) {
                                continue;
                            }
                        }

                        if ("id".equals(var12.toLowerCase())) {
                            var7 = true;
                            var8 = var2.getString(var12);
                        } else {
                            var3.append("," + var12);
                            var13 = FUtils.a(var5, var11, var2, var6);
                            var4.append("," + var13);
                        }
                    }
                }

                if (!var7 || oConvertUtils.isEmpty(var8)) {
                    var8 = a();
                }

                String var16 = "insert into " + f(var0) + "(" + "id" + var3.toString() + ") values(" + "'" + var8 + "'" + var4.toString() + ")";
                var6.put("execute_sql_string", var16);
                as.info("--表单设计器表单保存sql-->" + var16);
                return var6;
            }
        }
    }

    public static boolean c(String var0) {
        if ("list".equals(var0)) {
            return true;
        } else if ("radio".equals(var0)) {
            return true;
        } else if ("checkbox".equals(var0)) {
            return true;
        } else {
            return "list_multi".equals(var0);
        }
    }

    public static JSONArray a(List<OnlCgformField> var0, List<String> var1) {
        JSONArray var2 = new JSONArray();
        ISysBaseAPI var3 = SpringContextUtils.getBean(ISysBaseAPI.class);
        Iterator var4 = var0.iterator();

        while(true) {
            OnlCgformField var5;
            String var6;
            do {
                if (!var4.hasNext()) {
                    return var2;
                }

                var5 = (OnlCgformField)var4.next();
                var6 = var5.getDbFieldName();
            } while("id".equals(var6));

            JSONObject var7 = new JSONObject();
            if (var1.indexOf(var6) >= 0) {
                var7.put("disabled", true);
            }

            var7.put("title", var5.getDbFieldTxt());
            var7.put("key", var6);
            var7.put("width", "186px");
            String var8 = c(var5);
            var7.put("type", var8);
            if (var8.equals("file") || var8.equals("image")) {
                var7.put("responseName", "message");
                var7.put("token", true);
            }

            if (var8.equals("switch")) {
                var7.put("type", "checkbox");
                JSONArray var9 = new JSONArray();
                if (oConvertUtils.isEmpty(var5.getFieldExtendJson())) {
                    var9.add("Y");
                    var9.add("N");
                } else {
                    var9 = JSONArray.parseArray(var5.getFieldExtendJson());
                }

                var7.put("customValue", var9);
            }

            if (var8.equals("popup")) {
                var7.put("popupCode", var5.getDictTable());
                var7.put("orgFields", var5.getDictField());
                var7.put("destFields", var5.getDictText());
                String var17 = var5.getDictText();
                if (var17 != null && !var17.equals("")) {
                    ArrayList var10 = new ArrayList();
                    String[] var11 = var17.split(",");
                    String[] var12 = var11;
                    int var13 = var11.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        String var15 = var12[var14];
                        if (!a(var15, var0)) {
                            var10.add(var15);
                            JSONObject var16 = new JSONObject();
                            var16.put("title", var15);
                            var16.put("key", var15);
                            var16.put("type", "hidden");
                            var2.add(var16);
                        }
                    }
                }
            }

            var7.put("defaultValue", var5.getDbDefaultVal());
            var7.put("fieldDefaultValue", var5.getFieldDefaultValue());
            var7.put("placeholder", "请输入" + var5.getDbFieldTxt());
            var7.put("validateRules", b(var5));
            if ("list".equals(var5.getFieldShowType()) || "radio".equals(var5.getFieldShowType()) || "checkbox_meta".equals(var5.getFieldShowType()) || "list_multi".equals(var5.getFieldShowType()) || "sel_search".equals(var5.getFieldShowType())) {
                Object var18 = new ArrayList();
                if (oConvertUtils.isNotEmpty(var5.getDictTable())) {
                    var18 = var3.queryTableDictItemsByCode(var5.getDictTable(), var5.getDictText(), var5.getDictField());
                } else if (oConvertUtils.isNotEmpty(var5.getDictField())) {
                    var18 = var3.queryDictItemsByCode(var5.getDictField());
                }

                var7.put("options", var18);
                if ("list_multi".equals(var5.getFieldShowType())) {
                    var7.put("width", "230px");
                }
            }

            var2.add(var7);
        }
    }

    public static boolean a(String var0, List<OnlCgformField> var1) {
        Iterator var2 = var1.iterator();

        OnlCgformField var3;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            var3 = (OnlCgformField)var2.next();
        } while(!var0.equals(var3.getDbFieldName()));

        return true;
    }

    private static JSONArray b(OnlCgformField var0) {
        JSONArray var1 = new JSONArray();
        JSONObject var2;
        if (var0.getDbIsNull() == 0 || "1".equals(var0.getFieldMustInput())) {
            var2 = new JSONObject();
            var2.put("required", true);
            var2.put("message", var0.getDbFieldTxt() + "不能为空!");
            var1.add(var2);
        }

        if (oConvertUtils.isNotEmpty(var0.getFieldValidType())) {
            var2 = new JSONObject();
            if ("only".equals(var0.getFieldValidType())) {
                var2.put("pattern", (Object)null);
            } else {
                var2.put("pattern", var0.getFieldValidType());
            }

            var2.put("message", var0.getDbFieldTxt() + "格式不正确");
            var1.add(var2);
        }

        return var1;
    }

    public static JSONObject a(List<OnlCgformField> var0, List<String> var1, EModel var2) {
        new JSONObject();
        ArrayList var4 = new ArrayList();
        ArrayList var5 = new ArrayList();
        ISysBaseAPI var6 = (ISysBaseAPI) SpringContextUtils.getBean(ISysBaseAPI.class);
        OnlCgformHeadMapper var7 = SpringContextUtils.getBean(OnlCgformHeadMapper.class);
        ArrayList var8 = new ArrayList();
        Iterator var9 = var0.iterator();

        while(true) {
            OnlCgformField var10;
            String var11;
            do {
                do {
                    if (!var9.hasNext()) {
                        JSONObject var3;
                        JsonSchemaDescrip var23;
                        if (var4.size() > 0) {
                            var23 = new JsonSchemaDescrip(var4);
                            var3 = JsonschemaUtil.getJsonSchema(var23, var5);
                        } else {
                            var23 = new JsonSchemaDescrip();
                            var3 = JsonschemaUtil.getJsonSchema(var23, var5);
                        }

                        return var3;
                    }

                    var10 = (OnlCgformField)var9.next();
                    var11 = var10.getDbFieldName();
                } while("id".equals(var11));
            } while(var8.contains(var11));

            String var12 = var10.getDbFieldTxt();
            if ("1".equals(var10.getFieldMustInput())) {
                var4.add(var11);
            }

            String var13 = var10.getFieldShowType();
            Object var14 = null;
            if ("switch".equals(var13)) {
                var14 = new SwitchProperty(var11, var12, var10.getFieldExtendJson());
            } else if (c(var13)) {
                Object var30 = new ArrayList();
                if (oConvertUtils.isNotEmpty(var10.getDictTable())) {
                    var30 = var6.queryTableDictItemsByCode(var10.getDictTable(), var10.getDictText(), var10.getDictField());
                } else if (oConvertUtils.isNotEmpty(var10.getDictField())) {
                    var30 = var6.queryDictItemsByCode(var10.getDictField());
                }

                var14 = new StringProperty(var11, var12, var13, var10.getDbLength(), (List)var30);
                if (FUtils.a(var10.getDbType())) {
                    ((CommonProperty)var14).setType("number");
                }
            } else if (FUtils.a(var10.getDbType())) {
                NumberProperty var29 = new NumberProperty(var11, var12, "number");
                if (CgformValidPatternEnum.INTEGER.getType().equals(var10.getFieldValidType())) {
                    var29.setPattern(CgformValidPatternEnum.INTEGER.getPattern());
                }

                var14 = var29;
            } else {
                String var16;
                if (!"popup".equals(var13)) {
                    if ("sel_search".equals(var13)) {
                        var14 = new DictProperty(var11, var12, var10.getDictTable(), var10.getDictField(), var10.getDictText());
                    } else if ("link_down".equals(var13)) {
                        LinkDownProperty var27 = new LinkDownProperty(var11, var12, var10.getDictTable());
                        a(var27, var0, var8);
                        var14 = var27;
                    } else {
                        String var25;
                        String var32;
                        if ("sel_tree".equals(var13)) {
                            var25 = var10.getDictText();
                            String[] var31 = var25.split(",");
                            var32 = var10.getDictTable() + "," + var31[2] + "," + var31[0];
                            TreeSelectProperty var34 = new TreeSelectProperty(var11, var12, var32, var31[1], var10.getDictField());
                            if (var31.length > 3) {
                                var34.setHasChildField(var31[3]);
                            }

                            var14 = var34;
                        } else if ("cat_tree".equals(var13)) {
                            var25 = var10.getDictText();
                            var16 = var10.getDictField();
                            var32 = "0";
                            if (oConvertUtils.isNotEmpty(var16) && !"0".equals(var16)) {
                                var32 = var7.queryCategoryIdByCode(var16);
                            }

                            if (oConvertUtils.isEmpty(var25)) {
                                var14 = new TreeSelectProperty(var11, var12, var32);
                            } else {
                                var14 = new TreeSelectProperty(var11, var12, var32, var25);
                                HiddenProperty var33 = new HiddenProperty(var25, var25);
                                var5.add(var33);
                            }
                        } else if (var2 != null && var11.equals(var2.getFieldName())) {
                            var25 = var2.getTableName() + "," + var2.getTextField() + "," + var2.getCodeField();
                            TreeSelectProperty var28 = new TreeSelectProperty(var11, var12, var25, var2.getPidField(), var2.getPidValue());
                            var28.setHasChildField(var2.getHsaChildField());
                            var28.setPidComponent(1);
                            var14 = var28;
                        } else {
                            StringProperty var24 = new StringProperty(var11, var12, var13, var10.getDbLength());
                            if (oConvertUtils.isNotEmpty(var10.getFieldValidType())) {
                                CgformValidPatternEnum var26 = CgformValidPatternEnum.getPatternInfoByType(var10.getFieldValidType());
                                if (var26 != null) {
                                    if (CgformValidPatternEnum.NOTNULL == var26) {
                                        var4.add(var11);
                                    } else {
                                        var24.setPattern(var26.getPattern());
                                        var24.setErrorInfo(var26.getMsg());
                                    }
                                } else {
                                    var24.setPattern(var10.getFieldValidType());
                                    var24.setErrorInfo("输入的值不合法");
                                }
                            }

                            var14 = var24;
                        }
                    }
                } else {
                    PopupProperty var15 = new PopupProperty(var11, var12, var10.getDictTable(), var10.getDictText(), var10.getDictField());
                    var16 = var10.getDictText();
                    if (var16 != null && !var16.equals("")) {
                        String[] var17 = var16.split(",");
                        String[] var18 = var17;
                        int var19 = var17.length;

                        for(int var20 = 0; var20 < var19; ++var20) {
                            String var21 = var18[var20];
                            if (!a(var21, var0)) {
                                HiddenProperty var22 = new HiddenProperty(var21, var21);
                                var22.setOrder(var10.getOrderNum());
                                var5.add(var22);
                            }
                        }
                    }

                    var14 = var15;
                }
            }

            if (var10.getIsReadOnly() == 1 || var1 != null && var1.indexOf(var11) >= 0) {
                ((CommonProperty)var14).setDisabled(true);
            }

            ((CommonProperty)var14).setOrder(var10.getOrderNum());
            ((CommonProperty)var14).setDefVal(var10.getFieldDefaultValue());
            var5.add(var14);
        }
    }
}
