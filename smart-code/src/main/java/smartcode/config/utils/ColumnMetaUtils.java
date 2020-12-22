//
// Source code recreated from ColumnMetaUtils .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.config.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ColumnMetaUtils {
    private String tableName;
    private String columnId;
    private String columnName;
    private int columnSize;
    private String colunmType;
    private String comment;
    private String fieldDefault;
    private int decimalDigits;
    private String isNullable;
    private String pkType;
    private String oldColumnName;

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ColumnMetaUtils)) {
            return false;
        } else {
            ColumnMetaUtils var2 = (ColumnMetaUtils)obj;
            if (!this.colunmType.contains("date") && !this.colunmType.contains("blob") && !this.colunmType.contains("text")) {
                return this.colunmType.equals(var2.getColunmType()) && this.isNullable.equals(this.isNullable) && this.columnSize == var2.getColumnSize() && this.tableName(this.comment, var2.getComment()) && this.tableName(this.fieldDefault, var2.getFieldDefault());
            } else {
                return this.columnName.equals(var2.getColumnName()) && this.isNullable.equals(this.isNullable) && this.tableName(this.comment, var2.getComment()) && this.tableName(this.fieldDefault, var2.getFieldDefault());
            }
        }
    }

    public boolean a(Object var1, String var2) {
        if (var1 == this) {
            return true;
        } else if (!(var1 instanceof ColumnMetaUtils)) {
            return false;
        } else {
            ColumnMetaUtils var3 = (ColumnMetaUtils)var1;
            if ("SQLSERVER".equals(var2)) {
                if (!this.colunmType.contains("date") && !this.colunmType.contains("blob") && !this.colunmType.contains("text")) {
                    return this.colunmType.equals(var3.getColunmType()) && this.isNullable.equals(this.isNullable) && this.columnSize == var3.getColumnSize() && this.tableName(this.fieldDefault, var3.getFieldDefault());
                } else {
                    return this.columnName.equals(var3.getColumnName()) && this.isNullable.equals(this.isNullable);
                }
            } else if ("POSTGRESQL".equals(var2)) {
                if (!this.colunmType.contains("date") && !this.colunmType.contains("blob") && !this.colunmType.contains("text")) {
                    return this.colunmType.equals(var3.getColunmType()) && this.isNullable.equals(this.isNullable) && this.columnSize == var3.getColumnSize() && this.tableName(this.fieldDefault, var3.getFieldDefault());
                } else {
                    return this.columnName.equals(var3.getColumnName()) && this.isNullable.equals(this.isNullable);
                }
            } else if ("ORACLE".equals(var2)) {
                if (!this.colunmType.contains("date") && !this.colunmType.contains("blob") && !this.colunmType.contains("text")) {
                    return this.colunmType.equals(var3.getColunmType()) && this.isNullable.equals(this.isNullable) && this.columnSize == var3.getColumnSize() && this.tableName(this.fieldDefault, var3.getFieldDefault());
                } else {
                    return this.columnName.equals(var3.getColumnName()) && this.isNullable.equals(this.isNullable);
                }
            } else if (!this.colunmType.contains("date") && !this.colunmType.contains("blob") && !this.colunmType.contains("text")) {
                return this.colunmType.equals(var3.getColunmType()) && this.isNullable.equals(this.isNullable) && this.columnSize == var3.getColumnSize() && this.tableName(this.comment, var3.getComment()) && this.tableName(this.fieldDefault, var3.getFieldDefault());
            } else {
                return this.colunmType.equals(var3.getColunmType()) && this.columnName.equals(var3.getColumnName()) && this.isNullable.equals(this.isNullable) && this.tableName(this.comment, var3.getComment()) && this.tableName(this.fieldDefault, var3.getFieldDefault());
            }
        }
    }

    public boolean a(ColumnMetaUtils var1) {
        return var1 == this ? true : this.tableName(this.comment, var1.getComment());
    }

    public boolean b(ColumnMetaUtils var1) {
        return var1 == this ? true : this.tableName(this.comment, var1.getComment());
    }

    private boolean tableName(String var1, String var2) {
        boolean var3 = StringUtils.isNotEmpty(var1);
        boolean var4 = StringUtils.isNotEmpty(var2);
        if (var3 != var4) {
            return false;
        } else {
            return var3 ? var1.equals(var2) : true;
        }
    }

    public int hashCode() {
        return this.columnSize + this.colunmType.hashCode() * this.columnName.hashCode();
    }


    public String toString() {
        return this.columnName + "," + this.colunmType + "," + this.isNullable + "," + this.columnSize;
    }

}
