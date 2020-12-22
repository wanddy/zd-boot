//
// Source code recreated from TableTypeEntity .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.config.entity;

import java.util.List;
import lombok.Data;
import smartcode.form.entity.OnlCgformField;
import smartcode.form.entity.OnlCgformIndex;

@Data
public class TableTypeEntity {
    private String tableName;
    private String isDbSynch;
    private String content;
    private String jformVersion;
    private Integer jformType;
    private String jformPkType;
    private String jformPkSequence;
    private Integer relationType;
    private String subTableStr;
    private Integer tabOrder;
    private List<OnlCgformField> columns;
    private List<OnlCgformIndex> indexes;
    private String treeParentIdFieldName;
    private String treeIdFieldname;
    private String treeFieldname;
    private DataBaseConfig dbConfig;
}
