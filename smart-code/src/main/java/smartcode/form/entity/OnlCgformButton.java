//
// Source code recreated from QuerySqlConfig .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("onl_cgform_button")
public class OnlCgformButton implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
        type = IdType.UUID
    )
    private String id;
    private String cgformHeadId;
    private String buttonCode;
    private String buttonName;
    private String buttonStyle;
    private String optType;
    private String exp;
    private String buttonStatus;
    private Integer orderNum;
    private String buttonIcon;
    private String optPosition;
}
