package webapi.authController;


import auth.domain.user.service.ISysUserService;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.PermissionData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import auth.discard.model.DepartIdModel;
import auth.discard.model.SysUserSysDepartModel;
import auth.discard.vo.SysDepartUsersVO;
import auth.discard.vo.SysUserRoleVO;
import auth.domain.depart.service.ISysDepartService;
import auth.domain.relation.depar.role.service.ISysDepartRoleService;
import auth.domain.relation.user.depart.role.service.ISysDepartRoleUserService;
import auth.domain.relation.user.depart.service.ISysUserDepartService;
import auth.domain.relation.user.role.service.ISysUserRoleService;
import auth.entity.*;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;
import commons.constant.CommonConstant;
import commons.auth.utils.JwtUtil;
import commons.system.api.ISysBaseAPI;
import commons.util.PasswordUtil;
import commons.util.RedisUtil;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserDepartService sysUserDepartService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Autowired
    private ISysDepartRoleUserService departRoleUserService;

    @Autowired
    private ISysDepartRoleService departRoleService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户列表数据
     *
     * @param user
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/UserList")
    @RequiresPermissions("user:add")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<User>> queryPageList(User user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<User>> result = new Result<IPage<User>>();
        QueryWrapper<User> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ne("username", "_reserve_user_external");
        Page<User> page = new Page<User>(pageNo, pageSize);
        IPage<User> pageList = sysUserService.page(page, queryWrapper);

        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = pageList.getRecords().stream().map(User::getId).collect(Collectors.toList());
        if (userIds != null && userIds.size() > 0) {
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            pageList.getRecords().forEach(item -> {
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
        result.setSuccess(true);
        result.setResult(pageList);
        log.info(pageList.toString());
        return result;
    }

    /**
     * 获取用户数据
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Result<?> getUsers() {
        return Result.ok(sysUserService.list());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<User> add(@RequestBody JSONObject jsonObject) {
        Result<User> result = new Result<User>();
        String selectedRoles = jsonObject.getString("selectedroles");
        String selectedDeparts = jsonObject.getString("selecteddeparts");
        try {
            User user = JSON.parseObject(jsonObject.toJSONString(), User.class);
            user.setCreateTime(new Date());//设置创建时间
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
            user.setPassword(passwordEncode);
            user.setStatus(1);
            user.setDelFlag(CommonConstant.DEL_FLAG_0);
            user.setDepartIds(selectedDeparts);
            sysUserService.addUserWithRole(user, selectedRoles);
            sysUserService.addUserWithDepart(user, selectedDeparts);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<User> edit(@RequestBody JSONObject jsonObject) {
        Result<User> result = new Result<User>();
        try {
            User sysUser = sysUserService.getById(jsonObject.getString("id"));
            sysBaseAPI.addLog("编辑用户，id： " + jsonObject.getString("id"), CommonConstant.LOG_TYPE_2, 2);
            if (sysUser == null) {
                result.error500("未找到对应实体");
            } else {
                User user = JSON.parseObject(jsonObject.toJSONString(), User.class);
                user.setUpdateTime(new Date());
                //String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
                user.setPassword(sysUser.getPassword());
                String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
                sysUserService.editUserWithRole(user, roles);
                sysUserService.editUserWithDepart(user, departs);
                sysUserService.updateNullPhoneEmail();
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysBaseAPI.addLog("删除用户，id： " + id, CommonConstant.LOG_TYPE_2, 3);
        this.sysUserService.deleteUser(id);
        return Result.ok("删除用户成功");
    }

    /**
     * 批量删除用户
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        sysBaseAPI.addLog("批量删除用户， ids： " + ids, CommonConstant.LOG_TYPE_2, 3);
        this.sysUserService.deleteBatchUsers(ids);
        return Result.ok("批量删除用户成功");
    }

    /**
     * 冻结&解冻用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
    public Result<User> frozenBatch(@RequestBody JSONObject jsonObject) {
        Result<User> result = new Result<User>();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    this.sysUserService.update(new User().setStatus(Integer.parseInt(status)),
                            new UpdateWrapper<User>().lambda().eq(User::getId, id));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<User> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<User> result = new Result<User>();
        User sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<UserRole> userRole = sysUserRoleService.list(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (UserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
     * 校验用户账号是否唯一<br>
     * 可以校验其他 需要检验什么就传什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(User sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            User user = sysUserService.getOne(new QueryWrapper<User>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public Result<?> changePassword(@RequestBody User sysUser) {
        User u = this.sysUserService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, sysUser.getUsername()));
        if (u == null) {
            return Result.error("用户不存在！");
        }
        sysUser.setId(u.getId());
        return sysUserService.changePassword(sysUser);
    }

    /**
     * 查询指定用户和部门关联的数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("查找成功");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("查找失败");
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("查找过程中出现了异常: " + e.getMessage());
            return result;
        }
    }

    /**
     * 生成在添加用户情况下没有主键的问题,返回给前端,根据该id绑定部门数据
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("我执行了,生成用户ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * 根据部门id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<User>> queryUserByDepId(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "realname", required = false) String realname) {
        Result<List<User>> result = new Result<>();
        //List<User> userList = sysUserDepartService.queryUserByDepId(id);
        Depart sysDepart = sysDepartService.getById(id);
        List<User> userList = sysUserDepartService.queryUserByDepCode(sysDepart.getOrgCode(), realname);

        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        if (userIds != null && userIds.size() > 0) {
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            userList.forEach(item -> {
                //TODO 临时借用这个字段用于页面展示
                item.setOrgCode(useDepNames.get(item.getId()));
            });
        }

        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * @param userIds
     * @return
     * @功能：根据id 批量查询
     */
    @RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
    public Result<Collection<User>> queryByIds(@RequestParam String userIds) {
        Result<Collection<User>> result = new Result<>();
        String[] userId = userIds.split(",");
        Collection<String> idList = Arrays.asList(userId);
        Collection<User> userRole = sysUserService.listByIds(idList);
        result.setSuccess(true);
        result.setResult(userRole);
        return result;
    }

    /**
     * 首页用户重置密码
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public Result<?> changPassword(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String oldpassword = json.getString("oldpassword");
        String password = json.getString("password");
        String confirmpassword = json.getString("confirmpassword");
        User user = this.sysUserService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在！");
        }
        return sysUserService.resetPassword(username, oldpassword, password, confirmpassword);
    }

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<User>> userRoleList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<User>> result = new Result<IPage<User>>();
        Page<User> page = new Page<User>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<User> pageList = sysUserService.getUserByRoleId(page, roleId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定角色添加用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<String>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for (String sysUserId : sysUserRoleVO.getUserIdList()) {
                UserRole sysUserRole = new UserRole(sysUserId, sysRoleId);
                QueryWrapper<UserRole> queryWrapper = new QueryWrapper<UserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id", sysUserId);
                UserRole one = sysUserRoleService.getOne(queryWrapper);
                if (one == null) {
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<UserRole> deleteUserRole(@RequestParam(name = "roleId") String roleId,
                                           @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<UserRole> result = new Result<UserRole>();
        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<UserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id", userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<UserRole> deleteUserRoleBatch(
            @RequestParam(name = "roleId") String roleId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<UserRole> result = new Result<UserRole>();
        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<UserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id", Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 部门用户列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<User>> departUserList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<User>> result = new Result<IPage<User>>();
        Page<User> page = new Page<User>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        //根据部门ID查询,当前和下级所有的部门IDS
        List<String> subDepids = new ArrayList<>();
        //部门id为空时，查询我的部门下所有用户
        if (oConvertUtils.isEmpty(depId)) {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            int userIdentity = user.getUserIdentity() != null ? user.getUserIdentity() : CommonConstant.USER_IDENTITY_1;
            if (oConvertUtils.isNotEmpty(userIdentity) && userIdentity == CommonConstant.USER_IDENTITY_2) {
                subDepids = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
            }
        } else {
            subDepids = sysDepartService.getSubDepIdsByDepId(depId);
        }
        if (subDepids != null && subDepids.size() > 0) {
            IPage<User> pageList = sysUserService.getUserByDepIds(page, subDepids, username);
            //批量查询用户的所属部门
            //step.1 先拿到全部的 useids
            //step.2 通过 useids，一次性查询用户的所属部门名字
            List<String> userIds = pageList.getRecords().stream().map(User::getId).collect(Collectors.toList());
            if (userIds != null && userIds.size() > 0) {
                Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
                pageList.getRecords().forEach(item -> {
                    //批量查询用户的所属部门
                    item.setOrgCode(useDepNames.get(item.getId()));
                });
            }
            result.setSuccess(true);
            result.setResult(pageList);
        } else {
            result.setSuccess(true);
            result.setResult(null);
        }
        return result;
    }


    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 若某个用户包含多个部门，则会显示多条记录，可自行处理成单条记录
     */
    @GetMapping("/queryByOrgCode")
    public Result<?> queryByDepartId(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            User userParams
    ) {
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 针对通讯录模块做的接口，将多个部门的用户合并成一条记录，并转成对前端友好的格式
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public Result<?> queryByOrgCodeForAddressList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode", required = false) String orgCode,
            User userParams
    ) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, page);
        List<SysUserSysDepartModel> list = pageList.getRecords();

        // 记录所有出现过的 user, key = userId
        Map<String, JSONObject> hasUser = new HashMap<>(list.size());

        JSONArray resultJson = new JSONArray(list.size());

        for (SysUserSysDepartModel item : list) {
            String userId = item.getId();
            // userId
            JSONObject getModel = hasUser.get(userId);
            // 之前已存在过该用户，直接合并数据
            if (getModel != null) {
                String departName = getModel.get("departName").toString();
                getModel.put("departName", (departName + " | " + item.getDepartName()));
            } else {
                // 将用户对象转换为json格式，并将部门信息合并到 json 中
                JSONObject json = JSON.parseObject(JSON.toJSONString(item));
                json.remove("id");
                json.put("userId", userId);
                json.put("departId", item.getDepartId());
                json.put("departName", item.getDepartName());
//                json.put("avatar", item.getSysUser().getAvatar());
                resultJson.add(json);
                hasUser.put(userId, json);
            }
        }

        IPage<JSONObject> result = new Page<>(pageNo, pageSize, pageList.getTotal());
        result.setRecords(resultJson.toJavaList(JSONObject.class));
        return Result.ok(result);
    }

    /**
     * 给指定部门添加对应的用户
     */
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for (String sysUserId : sysDepartUsersVO.getUserIdList()) {
                UserDepart sysUserDepart = new UserDepart(sysUserId, sysDepId);
                QueryWrapper<UserDepart> queryWrapper = new QueryWrapper<UserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id", sysUserId);
                UserDepart one = sysUserDepartService.getOne(queryWrapper);
                if (one == null) {
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<UserDepart> deleteUserInDepart(@RequestParam(name = "depId") String depId,
                                                 @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<UserDepart> result = new Result<UserDepart>();
        try {
            QueryWrapper<UserDepart> queryWrapper = new QueryWrapper<UserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id", userId);
            boolean b = sysUserDepartService.remove(queryWrapper);
            if (b) {
                List<DepartRole> sysDepartRoleList = departRoleService.list(new QueryWrapper<DepartRole>().eq("depart_id", depId));
                List<String> roleIds = sysDepartRoleList.stream().map(DepartRole::getId).collect(Collectors.toList());
                if (roleIds != null && roleIds.size() > 0) {
                    QueryWrapper<DepartRoleUser> query = new QueryWrapper<>();
                    query.eq("user_id", userId).in("drole_id", roleIds);
                    departRoleUserService.remove(query);
                }
                result.success("删除成功!");
            } else {
                result.error500("当前选中部门与用户无关联关系!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<UserDepart> deleteUserInDepartBatch(
            @RequestParam(name = "depId") String depId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<UserDepart> result = new Result<UserDepart>();
        try {
            QueryWrapper<UserDepart> queryWrapper = new QueryWrapper<UserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id", Arrays.asList(userIds.split(",")));
            boolean b = sysUserDepartService.remove(queryWrapper);
            if (b) {
                departRoleUserService.removeDeptRoleUser(Arrays.asList(userIds.split(",")), depId);
            }
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 查询当前用户的所有部门/当前部门编码
     *
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String, Object>> getCurrentUserDeparts() {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<Depart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查询失败！");
        }
        return result;
    }


    /**
     * 用户注册接口
     *
     * @param jsonObject
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, User user) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        String username = jsonObject.getString("username");
        //未设置用户名，则用手机号作为用户名
        if (oConvertUtils.isEmpty(username)) {
            username = phone;
        }
        //未设置密码，则随机生成一个密码
        String password = jsonObject.getString("password");
        if (oConvertUtils.isEmpty(password)) {
            password = RandomUtil.randomString(8);
        }
        String email = jsonObject.getString("email");
        User sysUser1 = sysUserService.getUserByName(username);
        if (sysUser1 != null) {
            result.setMessage("用户名已注册");
            result.setSuccess(false);
            return result;
        }
        User sysUser2 = sysUserService.getUserByPhone(phone);
        if (sysUser2 != null) {
            result.setMessage("该手机号已注册");
            result.setSuccess(false);
            return result;
        }

        if (oConvertUtils.isNotEmpty(email)) {
            User sysUser3 = sysUserService.getUserByEmail(email);
            if (sysUser3 != null) {
                result.setMessage("邮箱已被注册");
                result.setSuccess(false);
                return result;
            }
        }

        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }

        try {
            user.setCreateTime(new Date());// 设置创建时间
            String salt = oConvertUtils.randomGen(8);
            String passwordEncode = PasswordUtil.encrypt(username, password, salt);
            user.setSalt(salt);
            user.setUsername(username);
            user.setRealname(username);
            user.setPassword(passwordEncode);
            user.setEmail(email);
            user.setPhone(phone);
            user.setStatus(CommonConstant.USER_UNFREEZE);
            user.setDelFlag(CommonConstant.DEL_FLAG_0);
            user.setActivitiSync(CommonConstant.ACT_SYNC_0);
            sysUserService.addUserWithRole(user, "ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 test
            result.success("注册成功");
        } catch (Exception e) {
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * 根据用户名或手机号查询用户信息
     *
     * @param
     * @return
     */
    @GetMapping("/querySysUser")
    public Result<Map<String, Object>> querySysUser(User sysUser) {
        String phone = sysUser.getPhone();
        String username = sysUser.getUsername();
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (oConvertUtils.isNotEmpty(phone)) {
            User user = sysUserService.getUserByPhone(phone);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        if (oConvertUtils.isNotEmpty(username)) {
            User user = sysUserService.getUserByName(username);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        result.setSuccess(false);
        result.setMessage("验证失败");
        return result;
    }

    /**
     * 用户手机号验证
     */
    @PostMapping("/phoneVerification")
    public Result<Map<String, String>> phoneVerification(@RequestBody JSONObject jsonObject) {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }
        //设置有效时间
        redisUtil.set(phone, smscode, 600);
        //新增查询用户名
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getPhone, phone);
        User user = sysUserService.getOne(query);
        Map<String, String> map = new HashMap<>();
        map.put("smscode", smscode);
        map.put("username", user.getUsername());
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户更改密码
     */
    @GetMapping("/passwordChange")
    public Result<User> passwordChange(@RequestParam(name = "username") String username,
                                       @RequestParam(name = "password") String password,
                                       @RequestParam(name = "smscode") String smscode,
                                       @RequestParam(name = "phone") String phone) {
        Result<User> result = new Result<User>();
        if (oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode) || oConvertUtils.isEmpty(phone)) {
            result.setMessage("重置密码失败！");
            result.setSuccess(false);
            return result;
        }

        User sysUser = new User();
        Object object = redisUtil.get(phone);
        if (null == object) {
            result.setMessage("短信验证码失效！");
            result.setSuccess(false);
            return result;
        }
        if (!smscode.equals(object)) {
            result.setMessage("短信验证码不匹配！");
            result.setSuccess(false);
            return result;
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username).eq(User::getPhone, phone));
        if (sysUser == null) {
            result.setMessage("未找到用户！");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密码重置完成！");
            return result;
        }
    }


    /**
     * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
     *
     * @return
     */
    @GetMapping("/getUserSectionInfoByToken")
    public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        try {
            String username = null;
            // 如果没有传递token，就从header中获取token并获取用户信息
            if (oConvertUtils.isEmpty(token)) {
                username = JwtUtil.getUserNameByToken(request);
            } else {
                username = JwtUtil.getUsername(token);
            }

            log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

            // 根据用户名查询用户信息
            User sysUser = sysUserService.getUserByName(username);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sysUserId", sysUser.getId());
            map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
            map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
            map.put("sysOrgCode", sysUser.getOrgCode()); // 当前登录用户部门编号

            log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 【APP端接口】获取用户列表  根据用户名和真实名 模糊匹配
     *
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/appUserList")
    public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
                                 @RequestParam(name = "username", required = false) String username,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(name = "syncFlow", required = false) String syncFlow) {
        try {
            //TODO 从查询效率上将不要用mp的封装的page分页查询 建议自己写分页语句
            LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>();
            if (oConvertUtils.isNotEmpty(syncFlow)) {
                query.eq(User::getActivitiSync, "1");
            }
            query.eq(User::getDelFlag, "0");
            if (oConvertUtils.isNotEmpty(username)) {
                if (username.contains(",")) {
                    query.in(User::getUsername, username.split(","));
                } else {
                    query.eq(User::getUsername, username);
                }
            } else {
                query.and(i -> i.like(User::getUsername, keyword).or().like(User::getRealname, keyword));
            }
            Page<User> page = new Page<>(pageNo, pageSize);
            IPage<User> res = this.sysUserService.page(page, query);
            return Result.ok(res);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }

    }

    /**
     * 获取被逻辑删除的用户列表，无分页
     *
     * @return logicDeletedUserList
     */
    @GetMapping("/recycleBin")
    public Result getRecycleBin() {
        List<User> logicDeletedUserList = sysUserService.queryLogicDeleted();
        if (logicDeletedUserList.size() > 0) {
            // 批量查询用户的所属部门
            // step.1 先拿到全部的 userIds
            List<String> userIds = logicDeletedUserList.stream().map(User::getId).collect(Collectors.toList());
            // step.2 通过 userIds，一次性查询用户的所属部门名字
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            logicDeletedUserList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return Result.ok(logicDeletedUserList);
    }

    /**
     * 还原被逻辑删除的用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
    public Result putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userIds = jsonObject.getString("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            User updateUser = new User();
            updateUser.setUpdateBy(JwtUtil.getUserNameByToken(request));
            updateUser.setUpdateTime(new Date());
            sysUserService.revertLogicDeleted(Arrays.asList(userIds.split(",")), updateUser);
        }
        return Result.ok("还原成功");
    }

    /**
     * 彻底删除用户
     *
     * @param userIds 被删除的用户ID，多个id用半角逗号分割
     * @return
     */
    @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
    public Result deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            sysUserService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return Result.ok("删除成功");
    }


    /**
     * 移动端修改用户信息
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/appEdit", method = RequestMethod.PUT)
    public Result<User> appEdit(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        Result<User> result = new Result<User>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            User sysUser = sysUserService.getUserByName(username);
            sysBaseAPI.addLog("移动端编辑用户，id： " + jsonObject.getString("id"), CommonConstant.LOG_TYPE_2, 2);
            if (sysUser == null) {
                result.error500("未找到对应用户!");
            } else {
                String realname = jsonObject.getString("realname");
                String avatar = jsonObject.getString("avatar");
                String sex = jsonObject.getString("sex");
                String phone = jsonObject.getString("phone");
                String email = jsonObject.getString("email");
                Date birthday = jsonObject.getDate("birthday");

                sysUser.setRealname(realname);
                sysUser.setAvatar(avatar);
                sysUser.setSex(Integer.parseInt(sex));
                sysUser.setBirthday(birthday);
                sysUser.setPhone(phone);
                sysUser.setEmail(email);
                sysUser.setUpdateTime(new Date());
                sysUserService.updateById(sysUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败!");
        }
        return result;
    }

    /**
     * 根据userid获取用户信息和部门员工信息
     *
     * @return Result
     */
    @GetMapping("/queryChildrenByUsername")
    public Result queryChildrenByUsername(@RequestParam("userId") String userId) {
        //获取用户信息
        Map<String, Object> map = new HashMap<String, Object>();
        User sysUser = sysUserService.getById(userId);
        String username = sysUser.getUsername();
        Integer identity = sysUser.getUserIdentity();
        map.put("sysUser", sysUser);
        if (identity != null && identity == 2) {
            //获取部门用户信息
            String departIds = sysUser.getDepartIds();
            if (StringUtils.isNotBlank(departIds)) {
                List<String> departIdList = Arrays.asList(departIds.split(","));
                List<User> childrenUser = sysUserService.queryByDepIds(departIdList, username);
                map.put("children", childrenUser);
            }
        }
        return Result.ok(map);
    }

    /**
     * 移动端查询部门用户信息
     *
     * @param departId
     * @return
     */
    @GetMapping("/appQueryByDepartId")
    public Result<List<User>> appQueryByDepartId(@RequestParam(name = "departId", required = false) String departId) {
        Result<List<User>> result = new Result<List<User>>();
        List<String> list = new ArrayList<String>();
        list.add(departId);
        List<User> childrenUser = sysUserService.queryByDepIds(list, null);
        result.setResult(childrenUser);
        return result;
    }

    /**
     * 移动端查询用户信息(通过用户名模糊查询)
     *
     * @param keyword
     * @return
     */
    @GetMapping("/appQueryUser")
    public Result<List<User>> appQueryUser(@RequestParam(name = "keyword", required = false) String keyword) {
        Result<List<User>> result = new Result<List<User>>();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ne(User::getUsername, "_reserve_user_external");
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(i -> i.like(User::getUsername, keyword).or().like(User::getRealname, keyword));
        }
        List<User> list = sysUserService.list(queryWrapper);
        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = list.stream().map(User::getId).collect(Collectors.toList());
        if (userIds != null && userIds.size() > 0) {
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            list.forEach(item -> {
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
        result.setResult(list);
        return result;
    }
}
