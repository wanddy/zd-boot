//
// Source code recreated from QuerySqlConfig .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.config.utils;

import com.google.api.client.util.Lists;
import commons.util.oConvertUtils;
import freemarker.template.TemplateException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.config.entity.DataBaseConfig;
import smartcode.config.entity.TableTypeEntity;
import smartcode.config.exception.DBException;
import smartcode.config.service.DbTableHandleI;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.utils.dUtils;

public class TableTemplateUtils {
    private static final Logger a = LoggerFactory.getLogger(TableTemplateUtils.class);
    private static final String b = "smartcode/config/engine/tableTemplate.ftl";
    private static DbTableHandleI c;

    public TableTemplateUtils() throws SQLException, DBException {
        c = dUtils.getTableHandle();
    }

    public static void a(TableTypeEntity tableTypeEntity) throws IOException, TemplateException, HibernateException, SQLException, DBException {
        String databaseType = dUtils.getDatabaseType();
        if ("ORACLE".equals(databaseType)) {
            ArrayList list = Lists.newArrayList();

            OnlCgformField onlCgformField;
            for(Iterator iterator = tableTypeEntity.getColumns().iterator(); iterator.hasNext(); list.add(onlCgformField)) {
                onlCgformField = (OnlCgformField)iterator.next();
                if ("int".equals(onlCgformField.getDbType())) {
                    onlCgformField.setDbType("double");
                    onlCgformField.setDbPointLength(0);
                }
            }

            tableTypeEntity.setColumns(list);
        }

        String type = FreemarkerHelper.type("smartcode/config/engine/tableTemplate.ftl", a(tableTypeEntity, databaseType));
        a.info(type);
        HashMap map = new HashMap();
        DataBaseConfig dataBaseConfig = tableTypeEntity.getDbConfig();
        map.put("hibernate.connection.driver_class", dataBaseConfig.getDriverClassName());
        map.put("hibernate.connection.url", dataBaseConfig.getUrl());
        map.put("hibernate.connection.username", dataBaseConfig.getUsername());
        map.put("hibernate.connection.password", dataBaseConfig.getPassword());
        map.put("hibernate.show_sql", true);
        map.put("hibernate.format_sql", true);
        map.put("hibernate.temp.use_jdbc_metadata_defaults", false);
        map.put("hibernate.dialect", dUtils.dataType(databaseType));
        map.put("hibernate.hbm2ddl.auto", "create");
        map.put("hibernate.connection.autocommit", false);
        map.put("hibernate.current_session_context_class", "thread");
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(map).build();
        MetadataSources sources = new MetadataSources(registry);
        ByteArrayInputStream stream = new ByteArrayInputStream(type.getBytes());
        sources.addInputStream(stream);
        Metadata metadata = sources.buildMetadata();
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.create(EnumSet.of(TargetType.DATABASE), metadata);
        stream.close();
        List exportExceptions = schemaExport.getExceptions();
        Iterator iterator = exportExceptions.iterator();

        Exception exception;
        while(true) {
            if (!iterator.hasNext()) {
                return;
            }

            exception = (Exception)iterator.next();
            if ("java.sql.SQLSyntaxErrorException".equals(exception.getCause().getClass().getName())) {
                SQLSyntaxErrorException var13 = (SQLSyntaxErrorException)exception.getCause();
                if ("42000".equals(var13.getSQLState())) {
                    continue;
                }
                break;
            } else {
                if (!"com.microsoft.sqlserver.jdbc.SQLServerException".equals(exception.getCause().getClass().getName())) {
                    break;
                }

                if (exception.getCause().toString().indexOf("Incorrect syntax near the keyword") != -1) {
                    exception.printStackTrace();
                    throw new DBException(exception.getCause().getMessage());
                }

                a.error(exception.getMessage());
            }
        }

        throw new DBException(exception.getMessage());
    }

    public List<String> b(TableTypeEntity tableTypeEntity) throws DBException, SQLException {
        String var2 = dUtils.getDatabaseType();
        String var3 = dUtils.a(tableTypeEntity.getTableName(), var2);
        String var4 = "alter table  " + var3 + " ";
        ArrayList var5 = new ArrayList();

        try {
            Map var6 = this.c(null, var3);
            Map var7 = this.c(tableTypeEntity);
            Map var8 = this.a(tableTypeEntity.getColumns());
            Iterator var9 = var7.keySet().iterator();

            label72:
            while(true) {
                while(true) {
                    String var10;
                    while(var9.hasNext()) {
                        var10 = (String)var9.next();
                        ColumnMetaUtils var11;
                        if (!var6.containsKey(var10)) {
                            var11 = (ColumnMetaUtils)var7.get(var10);
                            String var17 = (String)var8.get(var10);
                            if (var8.containsKey(var10) && var6.containsKey(var17)) {
                                ColumnMetaUtils var13 = (ColumnMetaUtils)var6.get(var17);
                                String var14 = c.getReNameFieldName(var11);
                                if ("SQLSERVER".equals(var2)) {
                                    var5.add(var14);
                                } else {
                                    var5.add(var4 + var14);
                                }

                                String var15 = this.d(var10, var11.getColumnId());
                                var5.add(var15);
                                if (!var13.equals(var11)) {
                                    var5.add(var4 + this.a(var11, var13));
                                    if ("POSTGRESQL".equals(var2)) {
                                        var5.add(var4 + this.b(var11, var13));
                                    }
                                }

                                if (!"SQLSERVER".equals(var2) && !var13.b(var11)) {
                                    var5.add(this.c(var11));
                                }
                            } else {
                                var5.add(var4 + this.b(var11));
                                if (!"SQLSERVER".equals(var2) && StringUtils.isNotEmpty(var11.getComment())) {
                                    var5.add(this.c(var11));
                                }
                            }
                        } else {
                            var11 = (ColumnMetaUtils)var6.get(var10);
                            ColumnMetaUtils var12 = (ColumnMetaUtils)var7.get(var10);
                            if (!var11.a(var12, var2)) {
                                var5.add(var4 + this.a(var12, var11));
                            }

                            if (!"SQLSERVER".equals(var2) && !"ORACLE".equals(var2) && !var11.b(var12)) {
                                var5.add(this.c(var12));
                            }
                        }
                    }

                    var9 = var6.keySet().iterator();

                    while(var9.hasNext()) {
                        var10 = (String)var9.next();
                        if (!var7.containsKey(var10.toLowerCase()) && !var8.containsValue(var10.toLowerCase())) {
                            var5.add(var4 + this.b(var10));
                        }
                    }
                    break label72;
                }
            }
        } catch (SQLException var16) {
            throw new RuntimeException();
        }

        a.info(" db update sql : " + var5.toString());
        return var5;
    }

    private static Map<String, Object> a(TableTypeEntity var0, String var1) {
        HashMap var2 = new HashMap();
        Iterator var3 = var0.getColumns().iterator();

        while(var3.hasNext()) {
            OnlCgformField var4 = (OnlCgformField)var3.next();
            var4.setDbDefaultVal(c(var4.getDbDefaultVal()));
        }

        var2.put("entity", var0);
        var2.put("dataType", var1);
        return var2;
    }

    private Map<String, ColumnMetaUtils> c(String var1, String var2) throws SQLException {
        HashMap var3 = new HashMap();
        Connection var4 = null;

        try {
            var4 = dUtils.getConnection();
        } catch (Exception var17) {
            a.error(var17.getMessage(), var17);
        }

        DatabaseMetaData var5 = var4.getMetaData();
        ResultSet var6 = var5.getColumns((String)null, var1, var2, "%");

        while(var6.next()) {
            ColumnMetaUtils var7 = new ColumnMetaUtils();
            var7.setTableName(var2);
            String var8 = var6.getString("COLUMN_NAME").toLowerCase();
            var7.setColumnName(var8);
            String var9 = var6.getString("TYPE_NAME");
            int var10 = var6.getInt("DECIMAL_DIGITS");
            String var11 = c.getMatchClassTypeByDataType(var9, var10);
            var7.setColunmType(var11);
            int var12 = var6.getInt("COLUMN_SIZE");
            var7.setColumnSize(var12);
            var7.setDecimalDigits(var10);
            String var13 = var6.getInt("NULLABLE") == 1 ? "Y" : "N";
            var7.setIsNullable(var13);
            String var14 = var6.getString("REMARKS");
            var7.setComment(var14);
            String var15 = var6.getString("COLUMN_DEF");
            String var16 = c(var15) == null ? "" : c(var15);
            var7.setFieldDefault(var16);
            a.info("getColumnMetadataFormDataBase --->COLUMN_NAME:" + var8.toUpperCase() + " TYPE_NAME :" + var9 + " DECIMAL_DIGITS:" + var10 + " COLUMN_SIZE:" + var12);
            var3.put(var8, var7);
        }

        return var3;
    }

    private Map<String, ColumnMetaUtils> c(TableTypeEntity var1) {
        HashMap var2 = new HashMap();
        List var3 = var1.getColumns();
        Iterator var5 = var3.iterator();

        while(var5.hasNext()) {
            OnlCgformField var6 = (OnlCgformField)var5.next();
            ColumnMetaUtils var4 = new ColumnMetaUtils();
            var4.setTableName(var1.getTableName().toLowerCase());
            var4.setColumnId(var6.getId());
            var4.setColumnName(var6.getDbFieldName().toLowerCase());
            var4.setColumnSize(var6.getDbLength());
            var4.setColunmType(var6.getDbType().toLowerCase());
            var4.setIsNullable(var6.getDbIsNull() == 1 ? "Y" : "N");
            var4.setComment(var6.getDbFieldTxt());
            var4.setDecimalDigits(var6.getDbPointLength());
            var4.setFieldDefault(c(var6.getDbDefaultVal()));
            var4.setPkType(var1.getJformPkType() == null ? "UUID" : var1.getJformPkType());
            var4.setOldColumnName(var6.getDbFieldNameOld() != null ? var6.getDbFieldNameOld().toLowerCase() : null);
            a.info("getColumnMetadataFormCgForm ----> DbFieldName: " + var6.getDbFieldName().toLowerCase() + " | DbType: " + var6.getDbType().toLowerCase() + " | DbPointLength:" + var6.getDbPointLength() + " | DbLength:" + var6.getDbLength());
            var2.put(var6.getDbFieldName().toLowerCase(), var4);
        }

        return var2;
    }

    private Map<String, String> a(List<OnlCgformField> var1) {
        HashMap var2 = new HashMap();
        Iterator var3 = var1.iterator();

        while(var3.hasNext()) {
            OnlCgformField var4 = (OnlCgformField)var3.next();
            var2.put(var4.getDbFieldName(), var4.getDbFieldNameOld());
        }

        return var2;
    }

    private String b(String var1) {
        return c.getDropColumnSql(var1);
    }

    private String a(ColumnMetaUtils var1, ColumnMetaUtils var2) throws DBException {
        return c.getUpdateColumnSql(var1, var2);
    }

    private String b(ColumnMetaUtils var1, ColumnMetaUtils var2) {
        return c.getSpecialHandle(var1, var2);
    }

    private String a(ColumnMetaUtils var1) {
        return c.getReNameFieldName(var1);
    }

    private String b(ColumnMetaUtils var1) {
        return c.getAddColumnSql(var1);
    }

    private String c(ColumnMetaUtils var1) {
        return c.getCommentSql(var1);
    }

    private String d(String var1, String var2) {
        return "update onl_cgform_field set DB_FIELD_NAME_OLD = '" + var1 + "' where ID ='" + var2 + "'";
    }

    private int a(String var1, String var2, Session var3) {
        return var3.createSQLQuery("update onl_cgform_field set DB_FIELD_NAME_OLD= '" + var1 + "' where ID ='" + var2 + "'").executeUpdate();
    }

    private static String c(String var0) {
        if (StringUtils.isNotEmpty(var0)) {
            try {
                Double.valueOf(var0);
            } catch (Exception var2) {
                if (!var0.startsWith("'") || !var0.endsWith("'")) {
                    var0 = "'" + var0 + "'";
                }
            }
        }

        return var0;
    }

    public String a(String var1, String var2) {
        return c.dropIndexs(var1, var2);
    }

    public String b(String var1, String var2) {
        return c.countIndex(var1, var2);
    }

    public static List<String> a(String var0) throws SQLException {
        Connection var1 = null;
        ResultSet var2 = null;
        ArrayList var3 = new ArrayList();

        try {
            var1 = dUtils.getConnection();
            DatabaseMetaData var4 = var1.getMetaData();
            var2 = var4.getIndexInfo(null, null, var0, false, false);
            ResultSetMetaData var5 = var2.getMetaData();

            while(var2.next()) {
                String var6 = var2.getString("INDEX_NAME");
                if (oConvertUtils.isEmpty(var6)) {
                    var6 = var2.getString("index_name");
                }

                if (oConvertUtils.isNotEmpty(var6)) {
                    var3.add(var6);
                }
            }
        } catch (SQLException var10) {
            a.error(var10.getMessage(), var10);
        } finally {
            if (var1 != null) {
                var1.close();
            }

        }

        return var3;
    }
}
