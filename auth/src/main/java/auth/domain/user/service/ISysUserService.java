package auth.domain.user.service;

import auth.discard.model.SysUserSysDepartModel;
import auth.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import commons.api.vo.Result;
import commons.auth.vo.SysUserCacheInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<User> {

	/**
	 * 重置密码
	 *
	 * @param username
	 * @param oldpassword
	 * @param newpassword
	 * @param confirmpassword
	 * @return
	 */
	public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

	/**
	 * 修改密码
	 *
	 * @param sysUser
	 * @return
	 */
	public Result<?> changePassword(User sysUser);

	/**
	 * 删除用户
	 * @param userId
	 * @return
	 */
	public boolean deleteUser(String userId);

	/**
	 * 批量删除用户
	 * @param userIds
	 * @return
	 */
	public boolean deleteBatchUsers(String userIds);

	public User getUserByName(String username);

	/**
	 * 添加用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	public void addUserWithRole(User user,String roles);


	/**
	 * 修改用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	public void editUserWithRole(User user,String roles);

	/**
	 * 获取用户的授权角色
	 * @param username
	 * @return
	 */
	public List<String> getRole(String username);

	/**
	  * 查询用户信息包括 部门信息
	 * @param username
	 * @return
	 */
	public SysUserCacheInfo getCacheUser(String username);

	/**
	 * 根据部门Id查询
	 * @param
	 * @return
	 */
	public IPage<User> getUserByDepId(Page<User> page, String departId, String username);

	/**
	 * 根据部门Ids查询
	 * @param
	 * @return
	 */
	public IPage<User> getUserByDepIds(Page<User> page, List<String> departIds, String username);

	/**
	 * 根据 userIds查询，查询用户所属部门的名称（多个部门名逗号隔开）
	 * @param
	 * @return
	 */
	public Map<String,String> getDepNamesByUserIds(List<String> userIds);

    /**
     * 根据部门 Id 和 QueryWrapper 查询
     *
     * @param page
     * @param departId
     * @param queryWrapper
     * @return
     */
    public IPage<User> getUserByDepartIdAndQueryWrapper(Page<User> page, String departId, QueryWrapper<User> queryWrapper);

	/**
	 * 根据 orgCode 查询用户，包括子部门下的用户
	 *
	 * @param orgCode
	 * @param userParams 用户查询条件，可为空
	 * @param page 分页参数
	 * @return
	 */
	IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, User userParams, IPage page);

	/**
	 * 根据角色Id查询
	 * @param
	 * @return
	 */
	public IPage<User> getUserByRoleId(Page<User> page, String roleId, String username);

	/**
	 * 通过用户名获取用户角色集合
	 *
	 * @param username 用户名
	 * @return 角色集合
	 */
	Set<String> getUserRolesSet(String username);

	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	Set<String> getUserPermissionsSet(String username);

	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(String username,String orgCode);

	/**
	 * 根据手机号获取用户名和密码
	 */
	public User getUserByPhone(String phone);


	/**
	 * 根据邮箱获取用户
	 */
	public User getUserByEmail(String email);


	/**
	 * 添加用户和用户部门关系
	 * @param user
	 * @param selectedParts
	 */
	void addUserWithDepart(User user, String selectedParts);

	/**
	 * 编辑用户和用户部门关系
	 * @param user
	 * @param departs
	 */
	void editUserWithDepart(User user, String departs);

	/**
	   * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(User sysUser);

	/**
	 * 查询被逻辑删除的用户
	 */
	List<User> queryLogicDeleted();

	/**
	 * 查询被逻辑删除的用户（可拼装查询条件）
	 */
	List<User> queryLogicDeleted(LambdaQueryWrapper<User> wrapper);

	/**
	 * 还原被逻辑删除的用户
	 */
	boolean revertLogicDeleted(List<String> userIds, User updateEntity);

	/**
	 * 彻底删除被逻辑删除的用户
	 */
	boolean removeLogicDeleted(List<String> userIds);

    /**
     * 更新手机号、邮箱空字符串为 null
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateNullPhoneEmail();

	/**
	 * 保存第三方用户信息
	 * @param sysUser
	 */
	void saveThirdUser(User sysUser);

	/**
	 * 根据部门Ids查询
	 * @param
	 * @return
	 */
	List<User> queryByDepIds(List<String> departIds, String username);
}
