package auth.entity;

import java.io.Serializable;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 部门角色人员信息
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_depart_role_user")
public class DepartRoleUser extends BaseAuditingEntity implements Serializable{

	private String userId;

	private String droleId;
}
