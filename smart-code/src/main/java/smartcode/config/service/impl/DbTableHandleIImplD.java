package smartcode.config.service.impl;

import commons.util.oConvertUtils;
import smartcode.config.exception.DBException;
import smartcode.config.service.DbTableHandleI;
import smartcode.config.utils.ColumnMetaUtils;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/25 11:34
 * @Description: zdit.zdboot.smartcode.online.config.service.impl
 **/

public class DbTableHandleIImplD implements DbTableHandleI {
    @Override
    public String getReNameFieldName(ColumnMetaUtils columnMeta) {
        return "  sp_rename '" + columnMeta.getTableName() + "." + columnMeta.getOldColumnName() + "', '" + columnMeta.getColumnName() + "', 'COLUMN';";
    }

    @Override
    public String getMatchClassTypeByDataType(String dataType, int digits) {
        String var3 = "";
        if (!dataType.equalsIgnoreCase("varchar") && !dataType.equalsIgnoreCase("nvarchar")) {
            if (dataType.equalsIgnoreCase("float")) {
                var3 = "double";
            } else if (dataType.equalsIgnoreCase("int")) {
                var3 = "int";
            } else if (dataType.equalsIgnoreCase("Date")) {
                var3 = "date";
            } else if (dataType.equalsIgnoreCase("Datetime")) {
                var3 = "date";
            } else if (dataType.equalsIgnoreCase("numeric")) {
                var3 = "bigdecimal";
            } else if (!dataType.equalsIgnoreCase("varbinary") && !dataType.equalsIgnoreCase("image")) {
                if (dataType.equalsIgnoreCase("text") || dataType.equalsIgnoreCase("ntext")) {
                    var3 = "text";
                }
            } else {
                var3 = "blob";
            }
        } else {
            var3 = "string";
        }

        return var3;
    }

    @Override
    public String getDropColumnSql(String fieldName) {
        return " DROP COLUMN " + fieldName + ";";    }

    @Override
    public String getUpdateColumnSql(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) throws DBException {
        return " ALTER COLUMN  " + this.a(cgformcolumnMeta, datacolumnMeta) + ";";
    }

    @Override
    public String getSpecialHandle(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        return null;
    }

    @Override
    public String getAddColumnSql(ColumnMetaUtils columnMeta) {
        return " ADD  " + this.a(columnMeta) + ";";
    }

    @Override
    public String getCommentSql(ColumnMetaUtils columnMeta) {
        StringBuffer var2 = new StringBuffer("EXECUTE ");
        if (oConvertUtils.isEmpty(columnMeta.getOldColumnName())) {
            var2.append("sp_addextendedproperty");
        } else {
            var2.append("sp_updateextendedproperty");
        }

        var2.append(" N'MS_Description', '");
        var2.append(columnMeta.getComment());
        var2.append("', N'SCHEMA', N'dbo', N'TABLE', N'");
        var2.append(columnMeta.getTableName());
        var2.append("', N'COLUMN', N'");
        var2.append(columnMeta.getColumnName() + "'");
        return var2.toString();
    }

    @Override
    public String dropIndexs(String indexName, String tableName) {
        return "DROP INDEX " + indexName + " ON " + tableName;
    }

    @Override
    public String countIndex(String indexName, String tableName) {
        return "SELECT count(*) FROM sys.indexes WHERE object_id=OBJECT_ID('" + tableName + "') and NAME= '" + indexName + "'";
    }

    @Override
    public String dropTableSQL(String tableName) {
        return " DROP TABLE " + tableName + " ;";
    }

    private String a(ColumnMetaUtils columnMeta) {
        String var2 = "";
        if (columnMeta.getColunmType().equalsIgnoreCase("string")) {
            var2 = columnMeta.getColumnName() + " nvarchar(" + columnMeta.getColumnSize() + ") " + ("Y".equals(columnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (columnMeta.getColunmType().equalsIgnoreCase("date")) {
            var2 = columnMeta.getColumnName() + " datetime " + ("Y".equals(columnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (columnMeta.getColunmType().equalsIgnoreCase("int")) {
            var2 = columnMeta.getColumnName() + " int " + ("Y".equals(columnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (columnMeta.getColunmType().equalsIgnoreCase("double")) {
            var2 = columnMeta.getColumnName() + " float " + ("Y".equals(columnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (columnMeta.getColunmType().equalsIgnoreCase("bigdecimal")) {
            var2 = columnMeta.getColumnName() + " numeric(" + columnMeta.getColumnSize() + "," + columnMeta.getDecimalDigits() + ") " + ("Y".equals(columnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (columnMeta.getColunmType().equalsIgnoreCase("text")) {
            var2 = columnMeta.getColumnName() + " ntext " + ("Y".equals(columnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (columnMeta.getColunmType().equalsIgnoreCase("blob")) {
            var2 = columnMeta.getColumnName() + " image";
        }

        return var2;
    }

    private String a(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        String var3 = "";
        if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("string")) {
            var3 = cgformcolumnMeta.getColumnName() + " nvarchar(" + cgformcolumnMeta.getColumnSize() + ") " + ("Y".equals(cgformcolumnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("date")) {
            var3 = cgformcolumnMeta.getColumnName() + " datetime " + ("Y".equals(cgformcolumnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("int")) {
            var3 = cgformcolumnMeta.getColumnName() + " int " + ("Y".equals(cgformcolumnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("double")) {
            var3 = cgformcolumnMeta.getColumnName() + " float " + ("Y".equals(cgformcolumnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("bigdecimal")) {
            var3 = cgformcolumnMeta.getColumnName() + " numeric(" + cgformcolumnMeta.getColumnSize() + "," + cgformcolumnMeta.getDecimalDigits() + ") " + ("Y".equals(cgformcolumnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("text")) {
            var3 = cgformcolumnMeta.getColumnName() + " ntext " + ("Y".equals(cgformcolumnMeta.getIsNullable()) ? "NULL" : "NOT NULL");
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("blob")) {
            var3 = cgformcolumnMeta.getColumnName() + " image";
        }

        return var3;
    }
}
