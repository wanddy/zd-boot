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

public class DbTableHandleIImplB implements DbTableHandleI {
    @Override
    public String getReNameFieldName(ColumnMetaUtils columnMeta) {
        return "RENAME COLUMN  " + columnMeta.getOldColumnName() + " TO " + columnMeta.getColumnName() + "";
    }

    @Override
    public String getMatchClassTypeByDataType(String dataType, int digits) {
        String var3 = "";
        if (dataType.equalsIgnoreCase("varchar2")) {
            var3 = "string";
        }

        if (dataType.equalsIgnoreCase("nvarchar2")) {
            var3 = "string";
        } else if (dataType.equalsIgnoreCase("double")) {
            var3 = "double";
        } else if (dataType.equalsIgnoreCase("number") && digits == 0) {
            var3 = "int";
        } else if (dataType.equalsIgnoreCase("number") && digits != 0) {
            var3 = "double";
        } else if (dataType.equalsIgnoreCase("int")) {
            var3 = "int";
        } else if (dataType.equalsIgnoreCase("Date")) {
            var3 = "date";
        } else if (dataType.equalsIgnoreCase("Datetime")) {
            var3 = "date";
        } else if (dataType.equalsIgnoreCase("blob")) {
            var3 = "blob";
        } else if (dataType.equalsIgnoreCase("clob")) {
            var3 = "text";
        }

        return var3;
    }

    @Override
    public String getDropColumnSql(String fieldName) {
        return " DROP COLUMN " + fieldName.toUpperCase() + "";
    }

    @Override
    public String getUpdateColumnSql(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) throws DBException {
        return " MODIFY   " + this.a(cgformcolumnMeta, datacolumnMeta) + "";
    }

    @Override
    public String getSpecialHandle(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        return null;
    }

    @Override
    public String getAddColumnSql(ColumnMetaUtils columnMeta) {
        return " ADD  " + this.a(columnMeta) + "";
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
        return "select count(*) from user_ind_columns where index_name=upper('" + indexName + "')";
    }

    @Override
    public String dropTableSQL(String tableName) {
        return " DROP TABLE  " + tableName.toLowerCase() + " ";
    }

    private String a(ColumnMetaUtils columnMeta) {
        String var2 = "";
        if (columnMeta.getColunmType().equalsIgnoreCase("string")) {
            var2 = columnMeta.getColumnName() + " varchar2(" + columnMeta.getColumnSize() + ")";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("date")) {
            var2 = columnMeta.getColumnName() + " date";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("int")) {
            var2 = columnMeta.getColumnName() + " NUMBER(" + columnMeta.getColumnSize() + ")";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("double")) {
            var2 = columnMeta.getColumnName() + " NUMBER(" + columnMeta.getColumnSize() + "," + columnMeta.getDecimalDigits() + ")";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("bigdecimal")) {
            var2 = columnMeta.getColumnName() + " NUMBER(" + columnMeta.getColumnSize() + "," + columnMeta.getDecimalDigits() + ")";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("text")) {
            var2 = columnMeta.getColumnName() + " CLOB ";
        } else if (columnMeta.getColunmType().equalsIgnoreCase("blob")) {
            var2 = columnMeta.getColumnName() + " BLOB ";
        }

        var2 = var2 + (StringUtils.isNotEmpty(columnMeta.getFieldDefault()) ? " DEFAULT " + columnMeta.getFieldDefault() : " ");
        var2 = var2 + ("Y".equals(columnMeta.getIsNullable()) ? " NULL" : " NOT NULL");
        return var2;
    }


    private String a(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) {
        String var3 = "";
        String var4 = "";
        if (!datacolumnMeta.getIsNullable().equals(cgformcolumnMeta.getIsNullable())) {
            var4 = cgformcolumnMeta.getIsNullable().equals("Y") ? "NULL" : "NOT NULL";
        }

        if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("string")) {
            var3 = cgformcolumnMeta.getColumnName() + " varchar2(" + cgformcolumnMeta.getColumnSize() + ")" + var4;
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("date")) {
            var3 = cgformcolumnMeta.getColumnName() + " date " + var4;
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("int")) {
            var3 = cgformcolumnMeta.getColumnName() + " NUMBER(" + cgformcolumnMeta.getColumnSize() + ") " + var4;
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("double")) {
            var3 = cgformcolumnMeta.getColumnName() + " NUMBER(" + cgformcolumnMeta.getColumnSize() + "," + cgformcolumnMeta.getDecimalDigits() + ") " + var4;
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("bigdecimal")) {
            var3 = cgformcolumnMeta.getColumnName() + " NUMBER(" + cgformcolumnMeta.getColumnSize() + "," + cgformcolumnMeta.getDecimalDigits() + ") " + var4;
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("blob")) {
            var3 = cgformcolumnMeta.getColumnName() + " BLOB " + var4;
        } else if (cgformcolumnMeta.getColunmType().equalsIgnoreCase("text")) {
            var3 = cgformcolumnMeta.getColumnName() + " CLOB " + var4;
        }

        var3 = var3 + (StringUtils.isNotEmpty(cgformcolumnMeta.getFieldDefault()) ? " DEFAULT " + cgformcolumnMeta.getFieldDefault() : " ");
        var3 = var3 + var4;
        return var3;
    }
}
