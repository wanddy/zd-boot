package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 部门权限表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_depart_permission")
public class DepartPermission extends BaseAuditingEntity implements Serializable {


    /**
     * 部门id
     */
    private String departId;

    /**
     * 权限id
     */
    private String permissionId;

    /**
     * 数据规则id
     */
    private String dataRuleIds;

    public DepartPermission(String departId, String permissionId) {
        this.departId = departId;
        this.permissionId = permissionId;
    }
}
