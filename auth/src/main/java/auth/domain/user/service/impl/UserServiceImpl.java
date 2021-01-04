package auth.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.model.DepartIdModel;
import auth.domain.depart.mapper.SysDepartMapper;
import auth.domain.depart.service.ISysDepartService;
import auth.domain.permission.mapper.SysPermissionMapper;
import auth.domain.relation.depar.role.mapper.SysDepartRoleMapper;
import auth.domain.relation.user.depart.mapper.SysUserDepartMapper;
import auth.domain.relation.user.depart.role.mapper.SysDepartRoleUserMapper;
import auth.domain.relation.user.depart.service.ISysUserDepartService;
import auth.domain.relation.user.role.mapper.SysUserRoleMapper;
import auth.domain.relation.user.role.service.ISysUserRoleService;
import auth.domain.role.mapper.SysRoleMapper;
import auth.domain.user.dto.ChangePasswordDto;
import auth.domain.user.dto.UserDto;
import auth.domain.user.mapper.UserMapper;
import auth.domain.user.model.FreeZeOrThawModel;
import auth.domain.user.model.UserDepartModel;
import auth.domain.user.model.UserModel;
import auth.domain.user.model.UserSearchModel;
import auth.domain.user.service.DefUserService;
import auth.domain.user.service.UserService;
import auth.entity.*;
import auth.discard.model.SysUserSysDepartModel;
import auth.discard.vo.SysUserDepVo;
import commons.api.vo.Result;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.exception.FilterFailureReason;
import commons.exception.ZdException;
import commons.auth.utils.JwtUtil;
import commons.system.api.ISysBaseAPI;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;
import commons.auth.vo.SysUserCacheInfo;
import commons.util.PasswordUtil;
import commons.util.UUIDGenerator;
import commons.util.oConvertUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final SysPermissionMapper sysPermissionMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysUserDepartMapper sysUserDepartMapper;

    private final ISysBaseAPI sysBaseAPI;

    private final SysDepartMapper sysDepartMapper;

    private final SysRoleMapper sysRoleMapper;

    private final SysDepartRoleUserMapper departRoleUserMapper;

    private final SysDepartRoleMapper sysDepartRoleMapper;

    private final ISysUserRoleService sysUserRoleService;

    private final ISysUserDepartService sysUserDepartService;

    private final DefUserService defUserService;

    private final UserMapper userMapper;

    private final ISysDepartService sysDepartService;

    @Autowired
    public UserServiceImpl(SysPermissionMapper sysPermissionMapper, SysUserRoleMapper sysUserRoleMapper, SysUserDepartMapper sysUserDepartMapper, ISysBaseAPI sysBaseAPI, SysDepartMapper sysDepartMapper, SysRoleMapper sysRoleMapper, SysDepartRoleUserMapper departRoleUserMapper, SysDepartRoleMapper sysDepartRoleMapper, ISysUserRoleService sysUserRoleService, ISysUserDepartService sysUserDepartService, DefUserService defUserService, UserMapper userMapper, ISysDepartService sysDepartService) {
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysUserDepartMapper = sysUserDepartMapper;
        this.sysBaseAPI = sysBaseAPI;
        this.sysDepartMapper = sysDepartMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.departRoleUserMapper = departRoleUserMapper;
        this.sysDepartRoleMapper = sysDepartRoleMapper;
        this.sysUserRoleService = sysUserRoleService;
        this.sysUserDepartService = sysUserDepartService;
        this.defUserService = defUserService;
        this.userMapper = userMapper;
        this.sysDepartService = sysDepartService;
    }

    @Override
    @Transactional
    public Result<UserDto> addUser(UserModel userModel) {
        Result<UserDto> result = new Result<>();
        try {

            if (checkUser(userModel)) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

            val salt = oConvertUtils.randomGen(8);
            val passwordEncode = PasswordUtil.encrypt(userModel.getUsername(), userModel.getPassword(), salt);

            val user = User.builder()
                    .username(userModel.getUsername())
                    .realname(userModel.getRealname())
                    .birthday(new SimpleDateFormat("yyyy-MM-dd").parse(userModel.getBirthday()))
                    .sex(userModel.getSex())
                    .email(userModel.getEmail())
                    .activitiSync(userModel.getActivitiSync())
                    .workNo(userModel.getWorkNo())
                    .post(userModel.getPost())
                    .telephone(userModel.getTelephone())
                    .userIdentity(userModel.getUserIdentity())
                    .salt(salt)
                    .password(passwordEncode)
                    .status(1)
                    .delFlag(CommonConstant.DEL_FLAG_0)
                    .build();

            this.addUserWithRole(user, userModel.getSelectedroles());
            this.addUserWithDepart(user, userModel.getSelecteddeparts());

            return defUserService.save(user) ? result.success("添加成功！") : result.error500("操作失败");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return result.error500("操作失败");
        }
    }

    private void addUserWithRole(User user, String roles) {
        if (StringUtils.isNotEmpty(roles)) {
            val arr = roles.split(",");
            for (val roleId : arr) {
                sysUserRoleMapper.insert(UserRole.builder()
                        .userId(user.getId())
                        .roleId(roleId)
                        .build());
            }
        }
    }

    private void addUserWithDepart(User user, String selectedParts) {
        if (StringUtils.isNotEmpty(selectedParts)) {
            val arr = selectedParts.split(",");
            for (val departId : arr) {
                sysUserDepartMapper.insert(UserDepart.builder()
                        .userId(user.getId())
                        .depId(departId)
                        .build());
            }
        }
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Result<?> removeUser(String id) {
        sysBaseAPI.addLog(String.format("删除用户，id：%s", id), CommonConstant.LOG_TYPE_2, 3);

        sysUserRoleMapper.removeUserRoleRelationByUserId(id);
        sysUserDepartMapper.removeUserDepartRelationByUserId(id);

        return defUserService.removeById(id) ? Result.ok("删除用户成功") : Result.ok("删除用户失败");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Result<?> removeUsers(String ids) {
        Result<UserDto> result = new Result<>();

        if (StringUtils.isEmpty(ids)) {
            result.error500("ids is empty");
            return result;
        }

        val userIds = Arrays.asList(ids.split(","));

        sysBaseAPI.addLog(String.format("删除用户，id：%s", ids), CommonConstant.LOG_TYPE_2, 3);

        sysUserDepartMapper.removeUserDepartRelationByUserIds(userIds);
        sysUserRoleMapper.removeUserRoleRelationByUserIds(userIds);

        return defUserService.removeByIds(userIds) ? Result.ok("删除用户成功") : Result.ok("删除用户失败");
    }


    @Override
    @Transactional
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> editUser(UserModel userModel) {
        Result<UserDto> result = new Result<>();
        try {
            val user = defUserService.getById(userModel.getId());

            sysBaseAPI.addLog(String.format("编辑用户，id：%s", userModel.getId()), CommonConstant.LOG_TYPE_2, 2);

            if (ObjectUtils.isEmpty(user)) {
                return result.error500("未找到对应实体");
            }

            this.editUserWithRole(user, userModel.getSelectedroles());
            this.editUserWithDepart(user, userModel.getSelecteddeparts());

            userMapper.updateNullByEmptyString("email");
            userMapper.updateNullByEmptyString("phone");

            defUserService.updateById(user);

            return result.success("修改成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return result.error500("操作失败");
        }
    }

    public void editUserWithRole(User user, String roles) {
        sysUserRoleMapper.delete(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, user.getId()));
        if (StringUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                UserRole userRole = new UserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    public void editUserWithDepart(User user, String departs) {
        String[] arr = {};
        if (StringUtils.isNotEmpty(departs)) {
            arr = departs.split(",");
        }
        List<UserDepart> userDepartList = sysUserDepartMapper.selectList(new QueryWrapper<UserDepart>().lambda().eq(UserDepart::getUserId, user.getId()));
        if (userDepartList != null && userDepartList.size() > 0) {
            for (UserDepart depart : userDepartList) {
                if (!Arrays.asList(arr).contains(depart.getDepId())) {
                    List<DepartRole> departRoleList = sysDepartRoleMapper.selectList(
                            new QueryWrapper<DepartRole>().lambda().eq(DepartRole::getDepartId, depart.getDepId()));
                    List<String> roleIds = departRoleList.stream().map(DepartRole::getId).collect(Collectors.toList());
                    if (roleIds.size() > 0) {
                        departRoleUserMapper.delete(new QueryWrapper<DepartRoleUser>().lambda().eq(DepartRoleUser::getUserId, user.getId())
                                .in(DepartRoleUser::getDroleId, roleIds));
                    }
                }
            }
        }
        sysUserDepartMapper.delete(new QueryWrapper<UserDepart>().lambda().eq(UserDepart::getUserId, user.getId()));
        if (StringUtils.isNotEmpty(departs)) {
            for (String departId : arr) {
                UserDepart userDepart = new UserDepart(user.getId(), departId);
                sysUserDepartMapper.insert(userDepart);
            }
        }
    }

    @Override
    public Result<?> freeZeOrThawUser(FreeZeOrThawModel freeZeOrThawModel) {
        Result<User> result = new Result<>();

        if (freeZeOrThawModel.getIds().contains(",")) {
            userMapper.freeZeOrThawUsers(Arrays.asList(freeZeOrThawModel.getIds().split(",")), freeZeOrThawModel.getStatus());
        } else {
            defUserService.update(User.builder().status(freeZeOrThawModel.getStatus()).build(), new UpdateWrapper<User>().lambda().eq(User::getId, freeZeOrThawModel.getIds()));
        }

        result.success("操作成功!");
        return result;
    }

    @Override
    public Result<?> changePassword(ChangePasswordDto changePasswordDto) {
        val user = defUserService.getById(changePasswordDto.getId());

        if (ObjectUtils.isEmpty(user)) {
            return Result.error("用户不存在！");
        }

        val salt = oConvertUtils.randomGen(8);
        user.setSalt(salt);
        val passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
        user.setPassword(passwordEncode);
        userMapper.updateById(user);

        return Result.ok("密码修改成功!");
    }

    @Override
    public Result<IPage<UserDto>> getUsers(UserSearchModel userSearch, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap) {
        Result<IPage<UserDto>> result = new Result<>();
        val user = User.builder().build();
        BeanUtils.copyProperties(userSearch, user);

        QueryWrapper<User> queryWrapper = QueryGenerator.initQueryWrapper(user, parameterMap);
        queryWrapper.ne("username", "_reserve_user_external");
        Page<User> page = new Page<>(pageNo, pageSize);
        IPage<User> pageList = defUserService.page(page, queryWrapper);

        val userIds = pageList.getRecords().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (userIds.size() > 0) {
            List<SysUserDepVo> list = userMapper.getDepNamesByUserIds(userIds);
            Map<String, String> res = new HashMap<>();
            list.forEach(item -> res.merge(item.getUserId(), item.getDepartName(), (a, b) -> a + "," + b));
            pageList.getRecords().forEach(item -> item.setOrgCodeTxt(res.get(item.getId())));
        }

        IPage<UserDto> users = new Page<>();
        BeanUtils.copyProperties(pageList, users);
        result.setSuccess(true);
        result.setResult(users);

        return result;
    }

    @Override
    public UserDto getUser(String id) {
        val sysUser = defUserService.getById(id);
        if (sysUser == null) {
            throw new ZdException(FilterFailureReason.NOT_FOUND, "实体为空");
        }

        val user = UserDto.builder().build();
        BeanUtils.copyProperties(sysUser, user);
        return user;
    }


    @Override
    public Result<List<UserDto>> getUsers(String ids) {
        Result<List<UserDto>> result = new Result<>();
        result.setSuccess(true);
        result.setResult(defUserService.listByIds(Arrays.asList(ids.split(","))).stream()
                .map(x -> UserDto.builder()
                        .id(x.getId())
                        .username(x.getUsername())
                        .realname(x.getRealname())
                        .avatar(x.getAvatar())
                        .build())
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Result<List<String>> getUserRoles(String id) {
        Result<List<String>> result = new Result<>();

        val userRoles = sysUserRoleService
                .list(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, id))
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        if (userRoles.isEmpty()) {
            return result.error500("未找到用户相关角色信息");
        }

        result.setSuccess(true);
        result.setResult(userRoles);

        return result;
    }

    @Override
    public UserDepartModel getUserByToken(String token) {
        String username = JwtUtil.getUsername(token);
        LoginUser loginUser = sysBaseAPI.getUserByName(username);
        val departs = sysUserDepartService.queryDepartIdsOfUser(loginUser.getId());
        return UserDepartModel.builder()
                .id(loginUser.getId())
                .name(loginUser.getRealname())
                .departId(departs.isEmpty() ? "" : departs.get(0).getKey())
                .departName(departs.isEmpty() ? "" : departs.get(0).getTitle())
                .build();
    }

    @Override
    public List<DepartIdModel> getUserDeparts(String id) {
        return sysUserDepartService.queryDepartIdsOfUser(id);
    }

    @Override
    public List<UserDepartModel> getUserDepartSub(String userId, String departId) {
        Depart sysDepart = sysDepartService.getById(departId);
        List<User> userList = sysUserDepartService.queryUserByDepCode(sysDepart.getOrgCode(), userId);

        return userList.stream()
                .map(x -> UserDepartModel.builder()
                        .id(x.getId())
                        .name(x.getRealname())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ModelAndView exportUser(UserSearchModel userSearch, Map<String, String[]> parameterMap, String selections) {
        return null;
    }

    private boolean checkUser(UserModel userModel) {
        val user = User.builder().build();
        BeanUtils.copyProperties(userModel, user);
        return defUserService.count(new QueryWrapper<>(user)) > 0;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword) {
        User user = userMapper.getUserByName(username);
        String passwordEncode = PasswordUtil.encrypt(username, oldpassword, user.getSalt());
        if (!user.getPassword().equals(passwordEncode)) {
            return Result.error("旧密码输入错误!");
        }
        if (oConvertUtils.isEmpty(newpassword)) {
            return Result.error("新密码不允许为空!");
        }
        if (!newpassword.equals(confirmpassword)) {
            return Result.error("两次输入密码不一致!");
        }
        String password = PasswordUtil.encrypt(username, newpassword, user.getSalt());
        userMapper.update(User.builder().password(password).build(), new LambdaQueryWrapper<User>().eq(User::getId, user.getId()));
        return Result.ok("密码重置成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String userId) {
        //1.删除用户
        defUserService.removeById(userId);
        return false;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchUsers(String userIds) {
        //1.删除用户
        defUserService.removeByIds(Arrays.asList(userIds.split(",")));
        return false;
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }


    @Override
    public List<String> getRole(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    @Override
    public Set<String> getUserRolesSet(String username) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
        log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return new HashSet<>(roles != null ? roles : Collections.emptyList());
    }

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    @Override
    public Set<String> getUserPermissionsSet(String username) {
        Set<String> permissionSet = new HashSet<>();
        List<Permission> permissionList = sysPermissionMapper.queryByUser(username);
        for (Permission po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
            if (oConvertUtils.isNotEmpty(po.getPerms())) {
                permissionSet.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + permissionSet.size());
        return permissionSet;
    }

    @Override
    public SysUserCacheInfo getCacheUser(String username) {
        SysUserCacheInfo info = new SysUserCacheInfo();
        info.setOneDepart(true);
//		User user = userMapper.getUserByName(username);
//		info.setSysUserCode(user.getUsername());
//		info.setSysUserName(user.getRealname());


        LoginUser user = sysBaseAPI.getUserByName(username);
        if (!ObjectUtils.isEmpty(user)) {
            info.setSysUserCode(user.getUsername());
            info.setSysUserName(user.getRealname());
            info.setSysOrgCode(user.getOrgCode());


            //多部门支持in查询
            List<Depart> list = sysDepartMapper.queryUserDeparts(user.getId());
            List<String> sysMultiOrgCode = new ArrayList<>();
            if (list == null || list.size() == 0) {
                //当前用户无部门
                //sysMultiOrgCode.add("0");
            } else if (list.size() == 1) {
                sysMultiOrgCode.add(list.get(0).getOrgCode());
            } else {
                info.setOneDepart(false);
                for (Depart dpt : list) {
                    sysMultiOrgCode.add(dpt.getOrgCode());
                }
            }
            info.setSysMultiOrgCode(sysMultiOrgCode);
        }
        return info;
    }

    // 根据部门Id查询
    @Override
    public IPage<User> getUserByDepId(Page<User> page, String departId, String username) {
        return userMapper.getUserByDepId(page, departId, username);
    }

    @Override
    public IPage<User> getUserByDepIds(Page<User> page, List<String> departIds, String username) {
        return userMapper.getUserByDepIds(page, departIds, username);
    }

    @Override
    public Map<String, String> getDepNamesByUserIds(List<String> userIds) {
        List<SysUserDepVo> list = userMapper.getDepNamesByUserIds(userIds);

        Map<String, String> res = new HashMap<>();
        list.forEach(item -> res.merge(item.getUserId(), item.getDepartName(), (a, b) -> a + "," + b)
        );
        return res;
    }

    @Override
    public IPage<User> getUserByDepartIdAndQueryWrapper(Page<User> page, String departId, QueryWrapper<User> queryWrapper) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = queryWrapper.lambda();

        lambdaQueryWrapper.eq(User::getDelFlag, "0");
        lambdaQueryWrapper.inSql(User::getId, "SELECT user_id FROM sys_user_depart WHERE dep_id = '" + departId + "'");

        return userMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, User userParams, IPage page) {
        List<SysUserSysDepartModel> list = userMapper.getUserByOrgCode(page, orgCode, userParams);
        Integer total = userMapper.getUserByOrgCodeTotal(orgCode, userParams);

        IPage<SysUserSysDepartModel> result = new Page<>(page.getCurrent(), page.getSize(), total);
        result.setRecords(list);

        return result;
    }

    // 根据角色Id查询
    @Override
    public IPage<User> getUserByRoleId(Page<User> page, String roleId, String username) {
        return userMapper.getUserByRoleId(page, roleId, username);
    }


    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, key = "#username")
    public void updateUserDepart(String username, String orgCode) {
        userMapper.updateUserDepart(username, orgCode);
    }


    @Override
    public User getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }


    /**
     * 校验用户是否有效
     *
     * @return
     */
    @Override
    public Result<?> checkUserIsEffective(User User) {
        Result<?> result = new Result<Object>();
        //情况1：根据用户信息查询，该用户不存在
        if (User == null) {
            result.error500("该用户不存在，请注册");
            sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        //update-begin---author:王帅   Date:20200601  for：if条件永远为falsebug------------
        if (CommonConstant.DEL_FLAG_1.equals(User.getDelFlag())) {
            //update-end---author:王帅   Date:20200601  for：if条件永远为falsebug------------
            sysBaseAPI.addLog("用户登录失败，用户名:" + User.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(User.getStatus())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + User.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public List<User> queryLogicDeleted() {
        return this.queryLogicDeleted(null);
    }

    @Override
    public List<User> queryLogicDeleted(LambdaQueryWrapper<User> wrapper) {
        if (wrapper == null) {
            wrapper = new LambdaQueryWrapper<>();
        }
        wrapper.eq(User::getDelFlag, "1");
        return userMapper.selectLogicDeleted(wrapper);
    }

    @Override
    public boolean revertLogicDeleted(List<String> userIds, User updateEntity) {
        String ids = String.format("'%s'", String.join("','", userIds));
        return userMapper.revertLogicDeleted(ids, updateEntity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeLogicDeleted(List<String> userIds) {
        String ids = String.format("'%s'", String.join("','", userIds));
        // 1. 删除用户
        int line = userMapper.deleteLogicDeleted(ids);
        // 2. 删除用户部门关系
        line += sysUserDepartMapper.delete(new LambdaQueryWrapper<UserDepart>().in(UserDepart::getUserId, userIds));
        //3. 删除用户角色关系
        line += sysUserRoleMapper.delete(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, userIds));
        return line != 0;
    }


    @Override
    public void saveThirdUser(User User) {
        //保存用户
        String userid = UUIDGenerator.generate();
        User.setId(userid);
        userMapper.insert(User);
        //获取第三方角色
        Role role = sysRoleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "third_role"));
        //保存用户角色
        UserRole userRole = new UserRole();
        userRole.setRoleId(role.getId());
        userRole.setUserId(userid);
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public List<User> queryByDepIds(List<String> departIds, String username) {
        return userMapper.queryByDepIds(departIds, username);
    }

    @Override
    public void updateByOpenId(User user) {
        userMapper.updateById(user);
    }

}
