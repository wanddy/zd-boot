package smartcode.form.vo;

import lombok.Data;

import java.util.List;

@Data
public class MainTableVo {
    private String entityPackage;
    private String tableName;
    private String entityName;
    private String ftlDescription;
    private String primaryKeyPolicy;
    private String sequenceCode;
    private String ftl_mode = "A";
    private List<SubTableVo> subTables;
    public Integer fieldRowNum;
    public Integer searchFieldNum;
    public Integer fieldRequiredNum;

}
