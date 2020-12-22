package smartcode.form.utils;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 17:48
 * @Description: zdit.zdboot.auth.online.util
 **/
//TODO:需修改
@Data
public class PathCodeUtils {
    private static final Logger r = LoggerFactory.getLogger(PathCodeUtils.class);
    private static final String s = "jeecg/jeecg_database";
    private static final String t = "jeecg/jeecg_config";
    private static ResourceBundle u = c("jeecg/jeecg_database");
    private static ResourceBundle v;
    public static String a;
    public static String b;
    public static String c;
    public static String d;
    public static String e;
    public static String f;
    public static String g;
    public static String h;
    public static String i;
    public static String j;
    public static String k;
    public static boolean l;
    public static String m;
    public static String n;
    public static String o;
    public static String p;
    public static String q;

    public PathCodeUtils() {
    }

    private static ResourceBundle c(String var0) {
        PropertyResourceBundle var1 = null;
        BufferedInputStream var2 = null;
        String var3 = System.getProperty("user.dir") + File.separator + "config" + File.separator + var0 + ".properties";

        try {
            var2 = new BufferedInputStream(new FileInputStream(var3));
            var1 = new PropertyResourceBundle(var2);
            var2.close();
            if (var1 != null) {
                r.debug(" JAR方式部署，通过config目录读取配置：" + var3);
            }
        } catch (IOException var13) {
        } finally {
            if (var2 != null) {
                try {
                    var2.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

        return var1;
    }

    private void n() {
    }

    public static final String a() {
        return u.getString("diver_name");
    }

    public static final String b() {
        return u.getString("url");
    }

    public static final String c() {
        return u.getString("username");
    }

    public static final String d() {
        return u.getString("password");
    }

    public static final String e() {
        return u.getString("database_name");
    }

    public static final boolean f() {
        String var0 = v.getString("db_filed_convert");
        return !var0.toString().equals("false");
    }

    private static String o() {
        return v.getString("bussi_package");
    }

    private static String p() {
        return v.getString("templatepath");
    }

    public static final String g() {
        return v.getString("source_root_package");
    }

    public static final String h() {
        return v.getString("webroot_package");
    }

    public static final String i() {
        return v.getString("db_table_id");
    }

    public static final String j() {
        return v.getString("page_filter_fields");
    }

    public static final String k() {
        return v.getString("page_search_filed_num");
    }

    public static final String l() {
        return v.getString("page_field_required_num");
    }

    public static String m() {
        String var0 = v.getString("project_path");
        if (var0 != null && !"".equals(var0)) {
            g = var0;
        }

        return g;
    }

    public static void a(String var0) {
        g = var0;
    }

    public static void b(String var0) {
        k = var0;
    }

    static {
        if (u == null) {
            r.debug("通过class目录加载配置文件 jeecg/jeecg_database");
            u = ResourceBundle.getBundle("jeecg/jeecg_database");
        }

        v = c("jeecg/jeecg_config");
        if (v == null) {
            r.debug("通过class目录加载配置文件 jeecg/jeecg_config");
            v = ResourceBundle.getBundle("jeecg/jeecg_config");
        }

        a = "mysql";
        b = "com.mysql.jdbc.Driver";
        c = "jdbc:mysql://localhost:3306/jeecg-boot?useUnicode=true&characterEncoding=UTF-8";
        d = "root";
        e = "123456";
        f = "jeecg-boot";
        g = "cUtils:/workspace/jeecg";
        h = "com.jeecg";
        i = "src";
        j = "WebRoot";
        k = "/jeecg/code-template/";
        l = true;
        n = "4";
        o = "3";
        q = "1";
        b = a();
        c = b();
        d = c();
        e = d();
        f = e();
        i = g();
        j = h();
        h = o();
        k = p();
        g = m();
        m = i();
        l = f();
        p = j();
        o = k();
        if (c.indexOf("mysql") < 0 && c.indexOf("MYSQL") < 0) {
            if (c.indexOf("oracle") < 0 && c.indexOf("ORACLE") < 0) {
                if (c.indexOf("postgresql") < 0 && c.indexOf("POSTGRESQL") < 0) {
                    if (c.indexOf("sqlserver") >= 0 || c.indexOf("sqlserver") >= 0) {
                        a = "sqlserver";
                    }
                } else {
                    a = "postgresql";
                }
            } else {
                a = "oracle";
            }
        } else {
            a = "mysql";
        }

        i = i.replace(".", "/");
        j = j.replace(".", "/");
    }
}
