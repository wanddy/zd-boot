package auth.domain.relation.permission.rule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import auth.entity.PermissionDataRule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 权限规则 Mapper 接口
 * </p>
 *
 * @Author huangzhilin
 * @since 2019-04-01
 */
public interface SysPermissionDataRuleMapper extends BaseMapper<PermissionDataRule> {

	/**
	  * 根据用户名和权限id查询
	 * @param username
	 * @param permissionId
	 * @return
	 */
	public List<String> queryDataRuleIds(@Param("username") String username,@Param("permissionId") String permissionId);

}
