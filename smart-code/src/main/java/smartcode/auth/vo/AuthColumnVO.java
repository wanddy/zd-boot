package smartcode.auth.vo;

import lombok.Data;
import smartcode.form.entity.OnlCgformField;

import java.io.Serializable;

@Data
public class AuthColumnVO implements Serializable {
    private static final long serialVersionUID = 5445993027926933917L;
    private String id;
    private String cgformId;
    private Integer type = 1;
    private String code;
    private String title;
    private Integer status;
    private boolean listShow;
    private boolean formShow;
    private boolean formEditable;
    private Integer isShowForm;
    private Integer isShowList;
    private int switchFlag;

    public AuthColumnVO() {
    }

    public AuthColumnVO(OnlCgformField field) {
        this.cgformId = field.getCgformHeadId();
        this.code = field.getDbFieldName();
        this.title = field.getDbFieldTxt();
        this.type = 1;
        this.isShowForm = field.getIsShowForm();
        this.isShowList = field.getIsShowList();
    }
}
