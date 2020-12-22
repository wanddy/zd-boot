package smartcode.form.model;

import lombok.Data;

@Data
public class EModel {
    private String fieldName;
    private String tableName;
    private String codeField;
    private String textField;
    private String pidField;
    private String pidValue;
    private String hsaChildField;
}
