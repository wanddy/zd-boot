package smartcode.config.service;

import smartcode.config.exception.DBException;
import smartcode.config.utils.ColumnMetaUtils;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/25 11:14
 * @Description: zdit.zdboot.smartcode.online.config.service
 **/
public interface DbTableHandleI {
    String getReNameFieldName(ColumnMetaUtils columnMeta);

    String getMatchClassTypeByDataType(String dataType, int digits);

    String getDropColumnSql(String fieldName);

    String getUpdateColumnSql(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta) throws DBException;

    String getSpecialHandle(ColumnMetaUtils cgformcolumnMeta, ColumnMetaUtils datacolumnMeta);

    String getAddColumnSql(ColumnMetaUtils columnMeta);

    String getCommentSql(ColumnMetaUtils columnMeta);

    String dropIndexs(String indexName, String tableName);

    String countIndex(String indexName, String tableName);

    String dropTableSQL(String tableName);

}
