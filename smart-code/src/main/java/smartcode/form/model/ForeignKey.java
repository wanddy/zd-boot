package smartcode.form.model;

import lombok.Data;

@Data
public class ForeignKey {
    private String field;
    private String table;
    private String key;

    public ForeignKey(String var1, String var2) {
        this.key = var2;
        this.field = var1;
    }
}
