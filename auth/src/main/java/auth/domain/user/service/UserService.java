package auth.domain.user.service;

import auth.discard.model.DepartIdModel;
import auth.discard.model.SysUserSysDepartModel;
import auth.domain.user.dto.ChangePasswordDto;
import auth.domain.user.dto.UserDto;
import auth.domain.user.model.FreeZeOrThawModel;
import auth.domain.user.model.UserDepartModel;
import auth.domain.user.model.UserModel;
import auth.domain.user.model.UserSearchModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.api.vo.Result;
import commons.auth.vo.SysUserCacheInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import auth.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {


    Result<UserDto> addUser(UserModel userModel);

    Result<?> removeUser(String id);

    Result<?> removeUsers(String ids);

    Result<?> editUser(UserModel userModel);

    Result<?> freeZeOrThawUser(FreeZeOrThawModel freeZeOrThawModel);

    Result<?> changePassword(ChangePasswordDto changePasswordDto);

    Result<IPage<UserDto>> getUsers(UserSearchModel userSearch, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap);

    UserDto getUser(String id);

    Result<List<UserDto>> getUsers(String ids);

    Result<List<String>> getUserRoles(String id);

    UserDepartModel getUserByToken(String token);

    List<DepartIdModel> getUserDeparts(String id);

    List<UserDepartModel> getUserDepartSub(String userId, String departId);

    ModelAndView exportUser(UserSearchModel userSearch, Map<String, String[]> parameterMap, String selections);


    /**
     * 重置密码
     *
     * @param username
     * @param oldpassword
     * @param newpassword
     * @param confirmpassword
     * @return
     */
    Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    boolean deleteUser(String userId);

    /**
     * 批量删除用户
     *
     * @param userIds
     * @return
     */
    boolean deleteBatchUsers(String userIds);

    User getUserByName(String username);

    /**
     * 获取用户的授权角色
     *
     * @param username
     * @return
     */
    List<String> getRole(String username);

    /**
     * 查询用户信息包括 部门信息
     *
     * @param username
     * @return
     */
    SysUserCacheInfo getCacheUser(String username);

    /**
     * 根据部门Id查询
     *
     * @param
     * @return
     */
    IPage<User> getUserByDepId(Page<User> page, String departId, String username);

    /**
     * 根据部门Ids查询
     *
     * @param
     * @return
     */
    IPage<User> getUserByDepIds(Page<User> page, List<String> departIds, String username);

    /**
     * 根据 userIds查询，查询用户所属部门的名称（多个部门名逗号隔开）
     *
     * @param
     * @return
     */
    Map<String, String> getDepNamesByUserIds(List<String> userIds);

    /**
     * 根据部门 Id 和 QueryWrapper 查询
     *
     * @param page
     * @param departId
     * @param queryWrapper
     * @return
     */
    IPage<User> getUserByDepartIdAndQueryWrapper(Page<User> page, String departId, QueryWrapper<User> queryWrapper);

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     *
     * @param orgCode
     * @param userParams 用户查询条件，可为空
     * @param page       分页参数
     * @return
     */
    IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, User userParams, IPage page);

    /**
     * 根据角色Id查询
     *
     * @param
     * @return
     */
    IPage<User> getUserByRoleId(Page<User> page, String roleId, String username);

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
     *
     * @param username
     * @param orgCode
     */
    void updateUserDepart(String username, String orgCode);

    /**
     * 根据手机号获取用户名和密码
     */
    User getUserByPhone(String phone);


    /**
     * 根据邮箱获取用户
     */
    User getUserByEmail(String email);

    /**
     * 校验用户是否有效
     *
     * @param User
     * @return
     */
    Result checkUserIsEffective(User User);

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
     * 保存第三方用户信息
     *
     * @param User
     */
    void saveThirdUser(User User);

    /**
     * 根据部门Ids查询
     *
     * @param
     * @return
     */
    List<User> queryByDepIds(List<String> departIds, String username);
}
