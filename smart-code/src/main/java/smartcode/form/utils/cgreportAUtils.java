//
// Source code recreated from cgreportAUtils .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import commons.auth.query.QueryGenerator;
import commons.util.DateUtils;
import commons.util.oConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.config.exception.DBException;
import smartcode.form.entity.OnlCgreportItem;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class cgreportAUtils {
    private static final Logger a = LoggerFactory.getLogger(cgreportAUtils.class);

    public cgreportAUtils() {
    }

    public static void a(HttpServletRequest var0, Map<String, Object> var1, Map<String, Object> var2, Map<String, Object> var3) {
        String var4 = (String)var1.get("field_name");
        String var5 = (String)var1.get("search_mode");
        String var6 = (String)var1.get("field_type");
        String var7;
        String var8;
        String var9;
        if ("single".equals(var5)) {
            var7 = var0.getParameter(var4.toLowerCase());

            try {
                if (oConvertUtils.isEmpty(var7)) {
                    return;
                }

                var8 = var0.getQueryString();
                if (var8.contains(var4 + "=")) {
                    var9 = new String(var7.getBytes("ISO-8859-1"), "UTF-8");
                    var7 = var9;
                }
            } catch (UnsupportedEncodingException var10) {
                a.error(var10.getMessage(), var10);
                return;
            }

            if (oConvertUtils.isNotEmpty(var7)) {
                if (var7.contains("*")) {
                    var7 = var7.replaceAll("\\*", "%");
                    var2.put(var4, " LIKE :" + var4);
                } else {
                    var2.put(var4, " = :" + var4);
                }
            }

            var3.put(var4, a(var6, var7, true));
        } else if ("group".equals(var5)) {
            var7 = var0.getParameter(var4.toLowerCase() + "_begin");
            var8 = var0.getParameter(var4.toLowerCase() + "_end");
            if (oConvertUtils.isNotEmpty(var7)) {
                var9 = " >= :" + var4 + "_begin";
                var2.put(var4, var9);
                var3.put(var4 + "_begin", a(var6, var7, true));
            }

            if (oConvertUtils.isNotEmpty(var8)) {
                var9 = " <= :" + var4 + "_end";
                var2.put(new String(var4), var9);
                var3.put(var4 + "_end", a(var6, var8, false));
            }
        }

    }

    private static Object a(String var0, String var1, boolean var2) {
        Object var3 = null;
        if (oConvertUtils.isNotEmpty(var1)) {
            if ("String".equalsIgnoreCase(var0)) {
                var3 = var1;
            } else if ("Date".equalsIgnoreCase(var0)) {
                if (var1.length() != 19 && var1.length() == 10) {
                    if (var2) {
                        var1 = var1 + " 00:00:00";
                    } else {
                        var1 = var1 + " 23:59:59";
                    }
                }

                SimpleDateFormat var4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                var3 = DateUtils.str2Date(var1, var4);
            } else if ("Double".equalsIgnoreCase(var0)) {
                var3 = var1;
            } else if ("Integer".equalsIgnoreCase(var0)) {
                var3 = var1;
            } else {
                var3 = var1;
            }
        }

        return var3;
    }

    public static String a(List<Map<String, Object>> var0, Long var1) {
        JSONObject var2 = new JSONObject();
        JSONArray var3 = new JSONArray();
        var2.put("total", var1);
        if (var0 != null) {
            Iterator var4 = var0.iterator();

            while(var4.hasNext()) {
                Map var5 = (Map)var4.next();
                JSONObject var6 = new JSONObject();

                String var8;
                String var9;
                for(Iterator var7 = var5.keySet().iterator(); var7.hasNext(); var6.put(var8, var9)) {
                    var8 = (String)var7.next();
                    var9 = String.valueOf(var5.get(var8));
                    var8 = var8.toLowerCase();
                    if (var8.contains("time") || var8.contains("date")) {
                        var9 = a(var9);
                    }
                }

                var3.add(var6);
            }
        }

        var2.put("rows", var3);
        return var2.toString();
    }

    public static String a(List<Map<String, Object>> var0) {
        JSONArray var1 = new JSONArray();
        Iterator var2 = var0.iterator();

        while(var2.hasNext()) {
            Map var3 = (Map)var2.next();
            JSONObject var4 = new JSONObject();

            String var6;
            String var7;
            for(Iterator var5 = var3.keySet().iterator(); var5.hasNext(); var4.put(var6, var7)) {
                var6 = (String)var5.next();
                var7 = String.valueOf(var3.get(var6));
                var6 = var6.toLowerCase();
                if (var6.contains("time") || var6.contains("date")) {
                    var7 = a(var7);
                }
            }

            var1.add(var4);
        }

        return var1.toString();
    }

    public static String a(String var0) {
        SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date var3 = null;

        try {
            var3 = var1.parse(var0);
            return var2.format(var3);
        } catch (Exception var5) {
            return var0;
        }
    }

    public static String a(List<OnlCgreportItem> var0, Map<String, Object> var1, String var2) {
        StringBuffer var3 = new StringBuffer();
        String var4 = "";

        try {
            var4 = dUtils.getDatabaseType();
        } catch (SQLException var11) {
            var11.printStackTrace();
        } catch (DBException var12) {
            var12.printStackTrace();
        }

        Iterator var5 = var0.iterator();

        while(true) {
            while(true) {
                String var7;
                String var8;
                Object var13;
                do {
                    while(true) {
                        OnlCgreportItem var6;
                        do {
                            if (!var5.hasNext()) {
                                return var3.toString();
                            }

                            var6 = (OnlCgreportItem)var5.next();
                            var7 = var6.getFieldName();
                            var8 = var6.getFieldType();
                        } while(1 != var6.getIsSearch());

                        Object var9;
                        if ("group".equals(var6.getSearchMode())) {
                            var9 = var1.get(var7 + "_begin");
                            if (var9 != null) {
                                var3.append(" and " + var2 + var7 + " >= ");
                                if (!"Long".equals(var8) && !"Integer".equals(var8)) {
                                    if ("ORACLE".equals(var4)) {
                                        if (var8.toLowerCase().equals("datetime")) {
                                            var3.append(DbSelectUtils.a(var9.toString()));
                                        } else if (var8.toLowerCase().equals("date")) {
                                            var3.append(DbSelectUtils.b(var9.toString()));
                                        }
                                    } else {
                                        var3.append("'" + var9.toString() + "'");
                                    }
                                } else {
                                    var3.append(var9.toString());
                                }
                            }

                            var13 = var1.get(var7 + "_end");
                            break;
                        }

                        var9 = var1.get(var7);
                        if (var9 != null) {
                            String var10 = QueryGenerator.getSingleQueryConditionSql(var7, var2, var9, !"Long".equals(var8) && !"Integer".equals(var8));
                            var3.append(" and " + var10);
                        }
                    }
                } while(var13 == null);

                var3.append(" and " + var2 + var7 + " <= ");
                if (!"Long".equals(var8) && !"Integer".equals(var8)) {
                    if ("ORACLE".equals(var4)) {
                        if (var8.toLowerCase().equals("datetime")) {
                            var3.append(DbSelectUtils.a(var13.toString()));
                        } else if (var8.toLowerCase().equals("date")) {
                            var3.append(DbSelectUtils.b(var13.toString()));
                        }
                    } else {
                        var3.append("'" + var13.toString() + "'");
                    }
                } else {
                    var3.append(var13.toString());
                }
            }
        }
    }
}
