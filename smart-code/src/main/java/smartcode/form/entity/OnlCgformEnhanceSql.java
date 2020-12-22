//
// Source code recreated from OnlCgreportAPI .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("onl_cgform_enhance_sql")
public class OnlCgformEnhanceSql implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
        type = IdType.UUID
    )
    private String id;
    private String cgformHeadId;
    private String buttonCode;
    private String cgbSql;
    private String cgbSqlName;
    private String content;
}
