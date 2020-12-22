package smartcode.auth.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthDataVO implements Serializable {
    private static final long serialVersionUID = 1057819436991228603L;
    private String id;
    private String title;
    private String relId;
    private Boolean checked;

}
