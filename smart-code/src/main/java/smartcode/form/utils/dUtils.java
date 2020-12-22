//
// Source code recreated from ColumnMetaUtils .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.utils;

import commons.util.SpringContextUtils;
import commons.util.oConvertUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.config.exception.DBException;
import smartcode.config.service.DbTableHandleI;
import smartcode.config.service.impl.DbTableHandleIImplA;
import smartcode.config.service.impl.DbTableHandleIImplB;
import smartcode.config.service.impl.DbTableHandleIImplC;
import smartcode.config.service.impl.DbTableHandleIImplD;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dUtils {
    private static final Logger logger = LoggerFactory.getLogger(dUtils.class);
    public static String exception = "";

    public dUtils() {
    }

    public static DbTableHandleI getTableHandle() throws SQLException, DBException {
        Object var0 = null;
        String var1 = getDatabaseType();
        byte var3 = -1;
        switch(var1.hashCode()) {
        case -1955532418:
            if (var1.equals("ORACLE")) {
                var3 = 1;
            }
            break;
        case -1620389036:
            if (var1.equals("POSTGRESQL")) {
                var3 = 3;
            }
            break;
        case 73844866:
            if (var1.equals("MYSQL")) {
                var3 = 0;
            }
            break;
        case 912124529:
            if (var1.equals("SQLSERVER")) {
                var3 = 2;
            }
        }

        switch(var3) {
        case 0:
            var0 = new DbTableHandleIImplA();
            break;
        case 1:
            var0 = new DbTableHandleIImplB();
            break;
        case 2:
            var0 = new DbTableHandleIImplD();
            break;
        case 3:
            var0 = new DbTableHandleIImplC();
        }

        return (DbTableHandleI)var0;
    }

    public static Connection getConnection() throws SQLException {
        DataSource var0 = (DataSource) SpringContextUtils.getApplicationContext().getBean(DataSource.class);
        return var0.getConnection();
    }

    public static String getDatabaseType() throws SQLException, DBException {
        if (oConvertUtils.isNotEmpty(exception)) {
            return exception;
        } else {
            DataSource var0 = (DataSource)SpringContextUtils.getApplicationContext().getBean(DataSource.class);
            return a(var0);
        }
    }

    public static Boolean a(String var0) {
        Connection var1 = null;
        ResultSet var2 = null;

        Boolean var7;
        try {
            String[] var3 = new String[]{"TABLE"};
            var1 = getConnection();
            DatabaseMetaData var4 = var1.getMetaData();
            String var5 = var4.getDatabaseProductName().toUpperCase();
            String var6 = a(var0, var5);
            var2 = var4.getTables((String)null, (String)null, var6, var3);
            if (var2.next()) {
                logger.info("数据库表：【" + var0 + "】已存在");
                var7 = true;
                return var7;
            }

            var7 = false;
        } catch (SQLException var17) {
            throw new RuntimeException();
        } finally {
            try {
                if (var2 != null) {
                    var2.close();
                }

                if (var1 != null) {
                    var1.close();
                }
            } catch (SQLException var16) {
                logger.error(var16.getMessage(), var16);
            }

        }

        return var7;
    }


    public static boolean a() {
        try {
            return "ORACLE".equals(getDatabaseType());
        } catch (SQLException var1) {
            var1.printStackTrace();
        } catch (DBException var2) {
            var2.printStackTrace();
        }

        return false;
    }

    public static String a(DataSource var0) throws SQLException, DBException {
        if ("".equals(exception)) {
            Connection var1 = var0.getConnection();

            try {
                DatabaseMetaData var2 = var1.getMetaData();
                String var3 = var2.getDatabaseProductName().toLowerCase();
                if (var3.indexOf("mysql") >= 0) {
                    exception = "MYSQL";
                } else if (var3.indexOf("oracle") >= 0) {
                    exception = "ORACLE";
                } else if (var3.indexOf("sqlserver") < 0 && var3.indexOf("sql server") < 0) {
                    if (var3.indexOf("postgresql") < 0) {
                        throw new DBException("数据库类型:[" + var3 + "]不识别!");
                    }

                    exception = "POSTGRESQL";
                } else {
                    exception = "SQLSERVER";
                }
            } catch (Exception var7) {
                logger.error(var7.getMessage(), var7);
            } finally {
                var1.close();
            }
        }

        return exception;
    }

    public static String a(Connection var0) throws SQLException, DBException {
        if ("".equals(exception)) {
            DatabaseMetaData var1 = var0.getMetaData();
            String var2 = var1.getDatabaseProductName().toLowerCase();
            if (var2.indexOf("mysql") >= 0) {
                exception = "MYSQL";
            } else if (var2.indexOf("oracle") >= 0) {
                exception = "ORACLE";
            } else if (var2.indexOf("sqlserver") < 0 && var2.indexOf("sql server") < 0) {
                if (var2.indexOf("postgresql") < 0) {
                    throw new DBException("数据库类型:[" + var2 + "]不识别!");
                }

                exception = "POSTGRESQL";
            } else {
                exception = "SQLSERVER";
            }
        }

        return exception;
    }

    public static String a(Session var0) throws SQLException, DBException {
        return getDatabaseType();
    }

    public static String a(String var0, String var1) {
        byte var3 = -1;
        switch(var1.hashCode()) {
        case -1955532418:
            if (var1.equals("ORACLE")) {
                var3 = 0;
            }
            break;
        case -1620389036:
            if (var1.equals("POSTGRESQL")) {
                var3 = 1;
            }
        }

        switch(var3) {
        case 0:
            return var0.toUpperCase();
        case 1:
            return var0.toLowerCase();
        default:
            return var0;
        }
    }

    public static Boolean table(String var0) {
        Connection var1 = null;
        ResultSet var2 = null;

        Boolean var7;
        try {
            String[] var3 = new String[]{"TABLE"};
            var1 = getConnection();
            DatabaseMetaData var4 = var1.getMetaData();
            String var5 = var4.getDatabaseProductName().toUpperCase();
            String var6 = a(var0, var5);
            var2 = var4.getTables((String)null, (String)null, var6, var3);
            if (!var2.next()) {
                var7 = false;
                return var7;
            }

            logger.info("数据库表：【" + var0 + "】已存在");
            var7 = true;
        } catch (SQLException var17) {
            throw new RuntimeException();
        } finally {
            try {
                if (var2 != null) {
                    var2.close();
                }

                if (var1 != null) {
                    var1.close();
                }
            } catch (SQLException var16) {
                logger.error(var16.getMessage(), var16);
            }

        }

        return var7;
    }

    public static Map<String, Object> a(List<Map<String, Object>> var0) {
        HashMap var1 = new HashMap();

        for(int var2 = 0; var2 < var0.size(); ++var2) {
            var1.put(((Map)var0.get(var2)).get("column_name").toString(), var0.get(var2));
        }

        return var1;
    }

    public static String getDialect() throws SQLException, DBException {
        String var0 = getDatabaseType();
        return dataType(var0);
    }

    public static String dataType(String var0) throws SQLException, DBException {
        String var1 = "org.hibernate.dialect.MySQL5InnoDBDialect";
        byte var3 = -1;
        switch(var0.hashCode()) {
        case -1955532418:
            if (var0.equals("ORACLE")) {
                var3 = 2;
            }
            break;
        case -1620389036:
            if (var0.equals("POSTGRESQL")) {
                var3 = 1;
            }
            break;
        case 912124529:
            if (var0.equals("SQLSERVER")) {
                var3 = 0;
            }
        }

        switch(var3) {
        case 0:
            var1 = "org.hibernate.dialect.SQLServerDialect";
            break;
        case 1:
            var1 = "org.hibernate.dialect.PostgreSQLDialect";
            break;
        case 2:
            var1 = "org.hibernate.dialect.OracleDialect";
        }

        return var1;
    }

    public static String c(String var0) {
        return var0;
    }
}
