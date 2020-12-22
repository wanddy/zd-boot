package smartcode.form.model;

import lombok.Data;

@Data
public class DModel {
    private String customRender;
    public DModel(String var1) {
        this.customRender = var1;
    }
}
