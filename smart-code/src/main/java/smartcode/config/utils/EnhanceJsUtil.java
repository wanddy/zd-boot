//
// Source code recreated from QuerySqlConfig .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.config.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commons.util.oConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.form.entity.OnlCgformButton;
import smartcode.form.entity.OnlCgformEnhanceJs;
import smartcode.form.entity.OnlCgformField;

public class EnhanceJsUtil {
    private static final Logger a = LoggerFactory.getLogger(EnhanceJsUtil.class);
    private static final String b = "beforeAdd,beforeEdit,afterAdd,afterEdit,beforeDelete,afterDelete,mounted,created,show,loaded";
    private static final String c = "\\}\\s*\r*\n*\\s*";
    private static final String d = ",";

    public static void a(OnlCgformEnhanceJs var0, String var1, List<OnlCgformField> var2) {
        if (var0 != null && !oConvertUtils.isEmpty(var0.getCgJs())) {
            String var3 = " " + var0.getCgJs();
            a.info("one enhanceJs begin==> " + var3);
            Pattern var4 = Pattern.compile("(\\s{1}onlChange\\s*\\(\\)\\s*\\{)");
            Matcher var5 = var4.matcher(var3);
            if (var5.find()) {
                a.info("---JS 增强转换-main--enhanceJsFunctionName----onlChange");
                var3 = a(var3, "onlChange", "\\s{1}");

                OnlCgformField var7;
                for(Iterator var6 = var2.iterator(); var6.hasNext(); var3 = b(var3, var7.getDbFieldName())) {
                    var7 = (OnlCgformField)var6.next();
                }
            }

            a.info("one enhanceJs end==> " + var3);
            var0.setCgJs(var3);
        }
    }

    public static String b(String var0, List<OnlCgformButton> var1) {
        var0 = c(var0, var1);
        String var2 = "function OnlineEnhanceJs(getAction,postAction,deleteAction){return {_getAction:getAction,_postAction:postAction,_deleteAction:deleteAction," + var0 + "}}";
        a.info("最终的增强JS", var2);
        return var2;
    }

    public static String b(String var0, String var1) {
        String var2 = "(\\s+" + var1 + "\\s*\\(\\)\\s*\\{)";
        String var3 = var1 + ":function(that,event){";
        String var4 = b(var0, "\\}\\s*\r*\n*\\s*" + var2, "}," + var3);
        if (var4 == null) {
            var0 = c(var0, var2, var3);
        } else {
            var0 = var4;
        }

        return var0;
    }

    public static String a(String var0, String var1, String var2) {
        String var3 = "(" + oConvertUtils.getString(var2) + var1 + "\\s*\\(\\)\\s*\\{)";
        String var4 = var1 + ":function(that){const getAction=this._getAction,postAction=this._postAction,deleteAction=this._deleteAction;";
        String var5 = b(var0, "\\}\\s*\r*\n*\\s*" + var3, "}," + var4);
        if (var5 == null) {
            var0 = c(var0, var3, var4);
        } else {
            var0 = var5;
        }

        return var0;
    }

    public static void b(OnlCgformEnhanceJs var0, String var1, List<OnlCgformField> var2) {
        if (var0 != null && !oConvertUtils.isEmpty(var0.getCgJs())) {
            a.info(" sub enhanceJs begin==> " + var0);
            String var3 = var0.getCgJs();
            String var4 = var1 + "_" + "onlChange";
            Pattern var5 = Pattern.compile("(" + var4 + "\\s*\\(\\)\\s*\\{)");
            Matcher var6 = var5.matcher(var3);
            if (var6.find()) {
                a.info("---JS 增强转换-sub-- enhanceJsFunctionName----" + var4);
                var3 = a((String)var3, var4, (String)null);

                OnlCgformField var8;
                for(Iterator var7 = var2.iterator(); var7.hasNext(); var3 = b(var3, var8.getDbFieldName())) {
                    var8 = (OnlCgformField)var7.next();
                }
            }

            a.info(" sub enhanceJs end==> " + var3);
            var0.setCgJs(var3);
        }
    }

    public static String b(String var0, String var1, String var2) {
        Pattern var3 = Pattern.compile(var1);
        Matcher var4 = var3.matcher(var0);
        if (var4.find()) {
            var0 = var0.replace(var4.group(0), var2);
            return var0;
        } else {
            return null;
        }
    }

    public static String c(String var0, String var1, String var2) {
        String var3 = b(var0, var1, var2);
        return var3 != null ? var3 : var0;
    }


    public static String a(String var0) {
        String var1 = "function OnlineEnhanceJs(getAction,postAction,deleteAction){return {_getAction:getAction,_postAction:postAction,_deleteAction:deleteAction," + var0 + "}}";
        a.info("最终的增强JS", var1);
        return var1;
    }

    public static String c(String var0, List<OnlCgformButton> var1) {
        if (var1 != null) {
            Iterator var2 = var1.iterator();

            label37:
            while(true) {
                while(true) {
                    if (!var2.hasNext()) {
                        break label37;
                    }

                    OnlCgformButton var3 = (OnlCgformButton)var2.next();
                    String var4 = var3.getButtonCode();
                    if ("link".equals(var3.getButtonStyle())) {
                        var0 = a(var0, var4);
                    } else if ("button".equals(var3.getButtonStyle()) || "form".equals(var3.getButtonStyle())) {
                        var0 = a((String)var0, var4, (String)null);
                    }
                }
            }
        }

        String[] var6 = "beforeAdd,beforeEdit,afterAdd,afterEdit,beforeDelete,afterDelete,mounted,created,show,loaded".split(",");
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            String var5 = var6[var8];
            if ("beforeAdd,afterAdd,mounted,created,show,loaded".indexOf(var5) >= 0) {
                var0 = a((String)var0, var5, (String)null);
            } else {
                var0 = a(var0, var5);
            }
        }

        return var0;
    }

    public static String a(String var0, String var1) {
        String var2 = "(" + var1 + "\\s*\\(row\\)\\s*\\{)";
        String var3 = var1 + ":function(that,row){const getAction=this._getAction,postAction=this._postAction,deleteAction=this._deleteAction;";
        String var4 = b(var0, "\\}\\s*\r*\n*\\s*" + var2, "}," + var3);
        if (var4 == null) {
            var0 = c(var0, var2, var3);
        } else {
            var0 = var4;
        }

        var0 = a((String)var0, var1, (String)null);
        return var0;
    }

}
