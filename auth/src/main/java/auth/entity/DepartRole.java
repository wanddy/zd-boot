package auth.entity;

import java.io.Serializable;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 部门角色
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_depart_role")
public class DepartRole extends BaseAuditingEntity implements Serializable {

    /**
     * 部门id
     */
    @commons.annotation.Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    private String departId;
    /**
     * 部门角色名称
     */
    private String roleName;
    /**
     * 部门角色编码
     */
    private String roleCode;
    /**
     * 描述
     */
    private String description;


}
