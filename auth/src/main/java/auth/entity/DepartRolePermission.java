package auth.entity;

import java.io.Serializable;
import java.util.Date;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 部门角色权限
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_depart_role_permission")
public class DepartRolePermission extends BaseAuditingEntity implements Serializable {

    /**
     * 部门id
     */
    private String departId;
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 权限id
     */
    private String permissionId;
    /**
     * dataRuleIds
     */
    private String dataRuleIds;
    /**
     * 操作时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateDate;

    /**
     * 操作ip
     */
    private String operateIp;

    public DepartRolePermission(String roleId, String permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
