package auth.domain.permission.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import auth.entity.Permission;
import auth.discard.model.TreeModel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Repository
public interface SysPermissionMapper extends BaseMapper<Permission> {
	/**
	   * 通过父菜单ID查询子菜单
	 * @param parentId
	 * @return
	 */
	List<TreeModel> queryListByParentId(@Param("parentId") String parentId);

	/**
	  *   根据用户查询用户权限
	 */
	List<Permission> queryByUser(@Param("username") String username);

	/**
	 *   修改菜单状态字段： 是否子节点
	 */
	@Update("update sys_permission set is_leaf=#{leaf} where id = #{id}")
	int setMenuLeaf(@Param("id") String id, @Param("leaf") int leaf);

	/**
	  *   获取模糊匹配规则的数据权限URL
	 */
	@Select("SELECT url FROM sys_permission WHERE del_flag = 0 and menu_type = 2 and url like '%*%'")
	List<String> queryPermissionUrlWithStar();


	/**
	 * 根据用户账号查询菜单权限
	 * @param permission
	 * @param username
	 * @return
	 */
	int queryCountByUsername(@Param("username") String username, @Param("permission") Permission permission);



}
