package auth.domain.user.service.impl;

import auth.discard.model.SysUserSysDepartModel;
import auth.discard.vo.SysUserDepVo;
import auth.domain.depart.mapper.SysDepartMapper;
import auth.domain.permission.mapper.SysPermissionMapper;
import auth.domain.relation.depar.role.mapper.SysDepartRoleMapper;
import auth.domain.relation.user.depart.mapper.SysUserDepartMapper;
import auth.domain.relation.user.depart.role.mapper.SysDepartRoleUserMapper;
import auth.domain.relation.user.role.mapper.SysUserRoleMapper;
import auth.domain.role.mapper.SysRoleMapper;
import auth.domain.user.mapper.UserMapper;
import auth.domain.user.service.ISysUserService;
import auth.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import commons.auth.vo.SysUserCacheInfo;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.system.api.ISysBaseAPI;
import commons.util.PasswordUtil;
import commons.util.UUIDGenerator;
import commons.util.oConvertUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<UserMapper, User> implements ISysUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserDepartMapper sysUserDepartMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private SysDepartMapper sysDepartMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysDepartRoleUserMapper departRoleUserMapper;
    @Autowired
    private SysDepartRoleMapper sysDepartRoleMapper;

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
        this.userMapper.update(new User().setPassword(password), new LambdaQueryWrapper<User>().eq(User::getId, user.getId()));
        return Result.ok("密码重置成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> changePassword(User sysUser) {
        String salt = oConvertUtils.randomGen(8);
        sysUser.setSalt(salt);
        String password = sysUser.getPassword();
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
        sysUser.setPassword(passwordEncode);
        this.userMapper.updateById(sysUser);
        return Result.ok("密码修改成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String userId) {
        //1.删除用户
        this.removeById(userId);
        return false;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchUsers(String userIds) {
        //1.删除用户
        this.removeByIds(Arrays.asList(userIds.split(",")));
        return false;
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }


    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional
    public void addUserWithRole(User user, String roles) {
        this.save(user);
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                UserRole userRole = new UserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional
    public void editUserWithRole(User user, String roles) {
        this.updateById(user);
        //先删后加
        sysUserRoleMapper.delete(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                UserRole userRole = new UserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
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
        return new HashSet<>(roles);
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
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + (permissionSet == null ? 0 : permissionSet.size()));
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
        if (user != null) {
            info.setSysUserCode(user.getUsername());
            info.setSysUserName(user.getRealname());
            info.setSysOrgCode(user.getOrgCode());
        }

        //多部门支持in查询
        List<Depart> list = sysDepartMapper.queryUserDeparts(user.getId());
        List<String> sysMultiOrgCode = new ArrayList<String>();
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
        List<SysUserDepVo> list = this.baseMapper.getDepNamesByUserIds(userIds);

        Map<String, String> res = new HashMap<String, String>();
        list.forEach(item -> {
                    if (res.get(item.getUserId()) == null) {
                        res.put(item.getUserId(), item.getDepartName());
                    } else {
                        res.put(item.getUserId(), res.get(item.getUserId()) + "," + item.getDepartName());
                    }
                }
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
        List<SysUserSysDepartModel> list = baseMapper.getUserByOrgCode(page, orgCode, userParams);
        Integer total = baseMapper.getUserByOrgCodeTotal(orgCode, userParams);

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
        baseMapper.updateUserDepart(username, orgCode);
    }


    @Override
    public User getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    @Transactional
    public void addUserWithDepart(User user, String selectedParts) {
//		this.save(user);  //保存角色的时候已经添加过一次了
        if (oConvertUtils.isNotEmpty(selectedParts)) {
            String[] arr = selectedParts.split(",");
            for (String deaprtId : arr) {
                UserDepart userDeaprt = new UserDepart(user.getId(), deaprtId);
                sysUserDepartMapper.insert(userDeaprt);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public void editUserWithDepart(User user, String departs) {
        this.updateById(user);  //更新角色的时候已经更新了一次了，可以再跟新一次
        String[] arr = {};
        if (oConvertUtils.isNotEmpty(departs)) {
            arr = departs.split(",");
        }
        //查询已关联部门
        List<UserDepart> userDepartList = sysUserDepartMapper.selectList(new QueryWrapper<UserDepart>().lambda().eq(UserDepart::getUserId, user.getId()));
        if (userDepartList != null && userDepartList.size() > 0) {
            for (UserDepart depart : userDepartList) {
                //修改已关联部门删除部门用户角色关系
                if (!Arrays.asList(arr).contains(depart.getDepId())) {
                    List<DepartRole> sysDepartRoleList = sysDepartRoleMapper.selectList(
                            new QueryWrapper<DepartRole>().lambda().eq(DepartRole::getDepartId, depart.getDepId()));
                    List<String> roleIds = sysDepartRoleList.stream().map(DepartRole::getId).collect(Collectors.toList());
                    if (roleIds != null && roleIds.size() > 0) {
                        departRoleUserMapper.delete(new QueryWrapper<DepartRoleUser>().lambda().eq(DepartRoleUser::getUserId, user.getId())
                                .in(DepartRoleUser::getDroleId, roleIds));
                    }
                }
            }
        }
        //先删后加
        sysUserDepartMapper.delete(new QueryWrapper<UserDepart>().lambda().eq(UserDepart::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(departs)) {
            for (String departId : arr) {
                UserDepart userDepart = new UserDepart(user.getId(), departId);
                sysUserDepartMapper.insert(userDepart);
            }
        }
    }


    /**
     * 校验用户是否有效
     *
     * @param sysUser
     * @return
     */
    @Override
    public Result<?> checkUserIsEffective(User sysUser) {
        Result<?> result = new Result<Object>();
        //情况1：根据用户信息查询，该用户不存在
        if (sysUser == null) {
            result.error500("该用户不存在，请注册");
            sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        //update-begin---author:王帅   Date:20200601  for：if条件永远为falsebug------------
        if (CommonConstant.DEL_FLAG_1 == sysUser.getDelFlag()) {
            //update-end---author:王帅   Date:20200601  for：if条件永远为falsebug------------
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
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
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNullPhoneEmail() {
        userMapper.updateNullByEmptyString("email");
        userMapper.updateNullByEmptyString("phone");
        return true;
    }

    @Override
    public void saveThirdUser(User sysUser) {
        //保存用户
        String userid = UUIDGenerator.generate();
        sysUser.setId(userid);
        baseMapper.insert(sysUser);
        //获取第三方角色
        Role sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "third_role"));
        //保存用户角色
        UserRole userRole = new UserRole();
        userRole.setRoleId(sysRole.getId());
        userRole.setUserId(userid);
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public List<User> queryByDepIds(List<String> departIds, String username) {
        return userMapper.queryByDepIds(departIds, username);
    }

}
