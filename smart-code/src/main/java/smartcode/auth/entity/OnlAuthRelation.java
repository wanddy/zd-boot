package smartcode.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import commons.poi.excel.annatotion.Excel;

import java.io.Serializable;

@Data
@TableName("onl_auth_relation")
public class OnlAuthRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
        type = IdType.ASSIGN_ID
    )
    private String id;
    @Excel(
        name = "角色id",
        width = 15.0D
    )
    private String roleId;
    @Excel(
        name = "权限id",
        width = 15.0D
    )
    private String authId;
    @Excel(
        name = "1字段 2按钮 3数据权限",
        width = 15.0D
    )
    private Integer type;
    private String cgformId;


}
