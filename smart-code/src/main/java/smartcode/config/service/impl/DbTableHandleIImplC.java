package smartcode.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import smartcode.config.exception.DBException;
import smartcode.config.service.DbTableHandleI;
import smartcode.config.utils.ColumnMetaUtils;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/25 11:33
 * @Description: zdit.zdboot.smartcode.online.config.service.impl
 **/

public class DbTableHandleIImplC implements DbTableHandleI {
    @Override
    public String getReNameFieldName(ColumnMetaUtils columnMeta) {
        return " RENAME  COLUMN  " + columnMeta.getOldColumnName() + " to " + columnMeta.getColumnName() + ";";
    }

    @Override
    public String getMatchClassTypeByDataType(String dataType, int digits) {
        String var3 = "";
        if (dataType.equalsIgnoreCase("varchar")) {
            var3 = "string";
        } else if (dataType.equalsIgnoreCase("double")) {
            var3 = "double";
        } else if (dataType.contains("int")) {
            var3 = "int";
        } else if (dataType.equalsIgnoreCase("Date")) {
            var3 = "date";
        } else if (dataType.equalsIgnoreCase("timestamp")) {
            var3 = "date";
        } else if (dataType.equalsIgnoreCase("bytea")) {
            var3 = "blob";
        } else if (dataType.equalsIgnoreCase("text")) {
            var3 = "text";
        } else if (dataType.equalsIgnoreCase("decimal")) {
            var3 = "bigdecimal";
        } else if (dataType.equalsIgnoreCase("numeric")) {
            var3 = "bigdecimal";
        }

        return var3;
    }

    @Override
    public String getDropColumnSql(String fieldName) {
        return " DROP COLUMN " + fieldName + ";";
    }

    @Override
    public String getUpdateColumnSql(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) throws DBException {
        return "  ALTER  COLUMN   " + this.a(cgformcolumnMeta, datacolumnMeta) + ";";
    }

    @Override
    public String getSpecialHandle(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        return "  ALTER  COLUMN   " + this.b(cgformcolumnMeta, datacolumnMeta) + ";";
    }

    @Override
    public String getAddColumnSql(ColumnMetaUtils columnMeta) {
        return " ADD COLUMN " + this.a(columnMeta) + ";";
    }

    @Override
    public String getCommentSql(ColumnMetaUtils columnMeta) {
        return "COMMENT ON COLUMN " + columnMeta.getTableName() + "." + columnMeta.getColumnName() + " IS '" + columnMeta.getComment() + "'";
    }

    @Override
    public String dropIndexs(String indexName, String tableName) {
        return "DROP INDEX " + indexName;
    }

    @Override
    public String countIndex(String indexName, String tableName) {
        return "SELECT count(*) FROM pg_indexes WHERE indexname = '" + indexName + "' and tablename = '" + tableName + "'";
    }

    @Override
    public String dropTableSQL(String tableName) {
        return " DROP TABLE  " + tableName + " ;";
    }
    private String a(ColumnMetaUtils columnMeta) {
        String var2 = "";
        if (columnMeta.getColunmType().equalsIgnoreCase("string")) {
            var2 = columnMeta.getColumnName() + " character varying(" + columnMeta.getColumnSize() + ") ";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("date")) {
            var2 = columnMeta.getColumnName() + " timestamp ";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("int")) {
            var2 = columnMeta.getColumnName() + " int4";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("double")) {
            var2 = columnMeta.getColumnName() + " numeric(" + columnMeta.getColumnSize() + "," + columnMeta.getDecimalDigits() + ") ";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("bigdecimal")) {
            var2 = columnMeta.getColumnName() + " decimal(" + columnMeta.getColumnSize() + "," + columnMeta.getDecimalDigits() + ") ";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("blob")) {
            var2 = columnMeta.getColumnName() + " bytea(" + columnMeta.getColumnSize() + ") ";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("text")) {
            var2 = columnMeta.getColumnName() + " text ";
        }

        var2 = var2 + (StringUtils.isNotEmpty(columnMeta.getFieldDefault()) ? " DEFAULT " + columnMeta.getFieldDefault() : " ");
        return var2;
    }

    private String a(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) throws DBException {
        String var3 = "";
        if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("string")) {
            var3 = cgformcolumnMeta.getColumnName() + "  type character varying(" + cgformcolumnMeta.getColumnSize() + ") ";
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("date")) {
            var3 = cgformcolumnMeta.getColumnName() + "  type timestamp ";
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("int")) {
            var3 = cgformcolumnMeta.getColumnName() + " type int4 ";
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("double")) {
            var3 = cgformcolumnMeta.getColumnName() + " type  numeric(" + cgformcolumnMeta.getColumnSize() + "," + cgformcolumnMeta.getDecimalDigits() + ") ";
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("BigDecimal")) {
            var3 = cgformcolumnMeta.getColumnName() + " type  decimal(" + cgformcolumnMeta.getColumnSize() + "," + cgformcolumnMeta.getDecimalDigits() + ") ";
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("text")) {
            var3 = cgformcolumnMeta.getColumnName() + " text ";
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("blob")) {
            throw new DBException("blob类型不可修改");
        }

        return var3;
    }

    private String b(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        String var3 = "";
        if (!cgformcolumnMeta.a(datacolumnMeta)) {
            if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("string")) {
                var3 = cgformcolumnMeta.getColumnName();
                var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " SET DEFAULT " + cgformcolumnMeta.getFieldDefault() : " DROP DEFAULT");
            } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("date")) {
                var3 = cgformcolumnMeta.getColumnName();
                var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " SET DEFAULT " + cgformcolumnMeta.getFieldDefault() : " DROP DEFAULT");
            } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("int")) {
                var3 = cgformcolumnMeta.getColumnName();
                var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " SET DEFAULT " + cgformcolumnMeta.getFieldDefault() : " DROP DEFAULT");
            } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("double")) {
                var3 = cgformcolumnMeta.getColumnName();
                var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " SET DEFAULT " + cgformcolumnMeta.getFieldDefault() : " DROP DEFAULT");
            } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("bigdecimal")) {
                var3 = cgformcolumnMeta.getColumnName();
                var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " SET DEFAULT " + cgformcolumnMeta.getFieldDefault() : " DROP DEFAULT");
            } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("text")) {
                var3 = cgformcolumnMeta.getColumnName();
                var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " SET DEFAULT " + cgformcolumnMeta.getFieldDefault() : " DROP DEFAULT");
            }
        }

        return var3;
    }

}
