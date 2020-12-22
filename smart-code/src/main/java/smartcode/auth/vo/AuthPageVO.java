package smartcode.auth.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthPageVO implements Serializable {
    private static final long serialVersionUID = 724713901683956568L;
    private String id;
    private String code;
    private String title;
    private Integer page;
    private Integer control;
    private String relId;
    private Boolean checked;
}
