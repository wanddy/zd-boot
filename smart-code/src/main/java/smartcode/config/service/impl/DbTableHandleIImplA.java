package smartcode.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import smartcode.config.service.DbTableHandleI;
import smartcode.config.utils.ColumnMetaUtils;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/25 11:15
 * @Description: zdit.zdboot.smartcode.online.config.service
 **/

public class DbTableHandleIImplA implements DbTableHandleI {
    public DbTableHandleIImplA() {
    }

    public String getAddColumnSql(ColumnMetaUtils columnMeta) {
        return " ADD COLUMN " + this.a(columnMeta) + ";";
    }

    public String getReNameFieldName(ColumnMetaUtils columnMeta) {
        return "CHANGE COLUMN " + columnMeta.getOldColumnName() + " " + this.b(columnMeta) + " ;";
    }

    public String getUpdateColumnSql(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        return " MODIFY COLUMN " + this.b(cgformcolumnMeta, datacolumnMeta) + ";";
    }

    public String getMatchClassTypeByDataType(String dataType, int digits) {
        String var3 = "";
        if (dataType.equalsIgnoreCase("varchar")) {
            var3 = "string";
        } else if (dataType.equalsIgnoreCase("double")) {
            var3 = "double";
        } else if (dataType.equalsIgnoreCase("int")) {
            var3 = "int";
        } else if (dataType.equalsIgnoreCase("Date")) {
            var3 = "date";
        } else if (dataType.equalsIgnoreCase("Datetime")) {
            var3 = "date";
        } else if (dataType.equalsIgnoreCase("decimal")) {
            var3 = "bigdecimal";
        } else if (dataType.equalsIgnoreCase("text")) {
            var3 = "text";
        } else if (dataType.equalsIgnoreCase("blob")) {
            var3 = "blob";
        }

        return var3;
    }

    public String dropTableSQL(String tableName) {
        return " DROP TABLE IF EXISTS " + tableName + " ;";
    }

    public String getDropColumnSql(String fieldName) {
        return " DROP COLUMN " + fieldName + ";";
    }

    private String a(ColumnMetaUtils var1, ColumnMetaUtils var2) {
        String var3 = "";
        if (var1.getColunmType().equalsIgnoreCase("string")) {
            var3 = var1.getColumnName() + " varchar(" + var1.getColumnSize() + ") " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (var1.getColunmType().equalsIgnoreCase("date")) {
            var3 = var1.getColumnName() + " datetime " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (var1.getColunmType().equalsIgnoreCase("int")) {
            var3 = var1.getColumnName() + " int(" + var1.getColumnSize() + ") " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (var1.getColunmType().equalsIgnoreCase("double")) {
            var3 = var1.getColumnName() + " double(" + var1.getColumnSize() + "," + var1.getDecimalDigits() + ") " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (var1.getColunmType().equalsIgnoreCase("bigdecimal")) {
            var3 = var1.getColumnName() + " decimal(" + var1.getColumnSize() + "," + var1.getDecimalDigits() + ") " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (var1.getColunmType().equalsIgnoreCase("text")) {
            var3 = var1.getColumnName() + " text " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (var1.getColunmType().equalsIgnoreCase("blob")) {
            var3 = var1.getColumnName() + " blob " + ("Y".equals(var1.getIsNullable()) ? "NULL" : "NOT NULL");
        }

        var3 = var3 + (StringUtils.isNotEmpty(var1.getComment()) ? " COMMENT '" + var1.getComment() + "'" : " ");
        var3 = var3 + (StringUtils.isNotEmpty(var1.getFieldDefault()) ? " DEFAULT " + var1.getFieldDefault() : " ");
        String var4 = var1.getPkType();
        if ("id".equalsIgnoreCase(var1.getColumnName()) && var4 != null && ("SEQUENCE".equalsIgnoreCase(var4) || "NATIVE".equalsIgnoreCase(var4))) {
            var3 = var3 + " AUTO_INCREMENT ";
        }

        return var3;
    }

    private String b(ColumnMetaUtils var1, ColumnMetaUtils var2) {
        String var3 = this.a(var1, var2);
        return var3;
    }

    private String a(ColumnMetaUtils var1) {
        String var2 = this.a(var1, (ColumnMetaUtils)null);
        return var2;
    }

    private String b(ColumnMetaUtils var1) {
        String var2 = this.a(var1, (ColumnMetaUtils)null);
        return var2;
    }

    public String getCommentSql(ColumnMetaUtils columnMeta) {
        return "";
    }

    public String getSpecialHandle(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        return null;
    }

    public String dropIndexs(String indexName, String tableName) {
        return "DROP INDEX " + indexName + " ON " + tableName;
    }

    public String countIndex(String indexName, String tableName) {
        return "select COUNT(*) from information_schema.statistics where table_name = '" + tableName + "'  AND index_name = '" + indexName + "'";
    }
}
