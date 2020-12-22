package webapi.authController;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.model.DepartIdModel;
import auth.discard.model.SysUserSysDepartModel;
import auth.discard.vo.SysDepartUsersVO;
import auth.discard.vo.SysUserRoleVO;
import auth.domain.depart.service.ISysDepartService;
import auth.domain.relation.depar.role.service.ISysDepartRoleService;
import auth.domain.relation.user.depart.role.service.ISysDepartRoleUserService;
import auth.domain.relation.user.depart.service.ISysUserDepartService;
import auth.domain.relation.user.role.service.ISysUserRoleService;
import auth.domain.user.dto.ChangePasswordDto;
import auth.domain.user.dto.UserDto;
import auth.domain.user.model.FreeZeOrThawModel;
import auth.domain.user.model.UserDepartModel;
import auth.domain.user.model.UserModel;
import auth.domain.user.model.UserSearchModel;
import auth.domain.user.service.DefUserService;
import auth.domain.user.service.UserService;
import auth.entity.*;
import commons.api.vo.ResponseResult;
import commons.api.vo.Result;
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

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final ISysBaseAPI sysBaseAPI;

    private final UserService userService;

    private final ISysDepartService sysDepartService;

    private final ISysUserRoleService sysUserRoleService;

    private final ISysUserDepartService sysUserDepartService;

    private final ISysDepartRoleUserService departRoleUserService;

    private final ISysDepartRoleService departRoleService;

    private final RedisUtil redisUtil;

    private final DefUserService defUserService;

    @Autowired
    public UserController(ISysBaseAPI sysBaseAPI, UserService userService, ISysDepartService sysDepartService, ISysUserRoleService sysUserRoleService, ISysUserDepartService sysUserDepartService, ISysDepartRoleUserService departRoleUserService, ISysDepartRoleService departRoleService, RedisUtil redisUtil, DefUserService defUserService) {
        this.sysBaseAPI = sysBaseAPI;
        this.userService = userService;
        this.sysDepartService = sysDepartService;
        this.sysUserRoleService = sysUserRoleService;
        this.sysUserDepartService = sysUserDepartService;
        this.departRoleUserService = departRoleUserService;
        this.departRoleService = departRoleService;
        this.redisUtil = redisUtil;
        this.defUserService = defUserService;
    }


    @PostMapping(value = "")
    public Result<UserDto> addUser(@RequestBody UserModel userModel) {
        return userService.addUser(userModel);
    }

    @DeleteMapping(value = "/{id}")
    public Result<?> removeUser(@PathVariable(value = "id", required = false) String id) {
        return userService.removeUser(id);
    }

    @DeleteMapping(value = "")
    public Result<?> removeUsers(@RequestParam(value = "ids", required = false) String ids) {
        return userService.removeUsers(ids);
    }

    @PutMapping(value = "")
    public Result<?> editUser(@RequestBody UserModel userModel) {
        return userService.editUser(userModel);
    }

    @PutMapping(value = "/freeze-thaw")
    public Result<?> freeZeOrThawUser(@RequestBody FreeZeOrThawModel freeZeOrThawModel) {
        return userService.freeZeOrThawUser(freeZeOrThawModel);
    }

    @PutMapping(value = "/change-password")
    public Result<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return userService.changePassword(changePasswordDto);
    }

    @GetMapping(value = "")
    public Result<IPage<UserDto>> getUsers(UserSearchModel userSearch, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest request) {
        return userService.getUsers(userSearch, pageNo, pageSize, request.getParameterMap());
    }

    @GetMapping(value = "/{id}")
    public ResponseResult<UserDto> getUser(@PathVariable(value = "id") String id) {
        return new ResponseResult<UserDto>().ok(userService.getUser(id));
    }

    @GetMapping(value = "/{id}/roles")
    public Result<List<String>> getUserRoles(@PathVariable(value = "id") String id) {
        return userService.getUserRoles(id);
    }

    @GetMapping(value = "/token")
    public ResponseResult<UserDepartModel> getUserByToken(HttpServletRequest request) {
        return new ResponseResult<UserDepartModel>().ok(userService.getUserByToken(request.getHeader("X-Access-Token")));
    }

    @GetMapping(value = "/{id}/departs")
    public ResponseResult<List<DepartIdModel>> getUserDeparts(@PathVariable(value = "id") String id) {
        return new ResponseResult<List<DepartIdModel>>().ok(userService.getUserDeparts(id));
    }


    @RequestMapping(value = "/{userId}/departs/{departId}/sub", method = RequestMethod.GET)
    public ResponseResult<List<UserDepartModel>> getUserDepartSub(@PathVariable(name = "userId") String userId, @PathVariable(name = "departId") String departId) {
        return new ResponseResult<List<UserDepartModel>>().ok(userService.getUserDepartSub(userId, departId));

    }

    @GetMapping(value = "/export")
    public ModelAndView exportXls(@RequestBody UserSearchModel userSearch, HttpServletRequest request) {
        return userService.exportUser(userSearch, request.getParameterMap(), request.getParameter("selections"));
    }

    /**
     * 批量查询
     */
    @RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
    public Result<Collection<User>> queryByIds(@RequestParam String userIds) {
        Result<Collection<User>> result = new Result<>();
        String[] userId = userIds.split(",");
        Collection<String> idList = Arrays.asList(userId);
        Collection<User> userRole = defUserService.listByIds(idList);
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
        User user = defUserService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在！");
        }
        return userService.resetPassword(username, oldpassword, password, confirmpassword);
    }

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<User>> userRoleList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<User>> result = new Result<>();
        Page<User> page = new Page<>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<User> pageList = userService.getUserByRoleId(page, roleId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定角色添加用户
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for (String sysUserId : sysUserRoleVO.getUserIdList()) {
                UserRole UserRole = new UserRole(sysUserId, sysRoleId);
                QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id", sysUserId);
                UserRole one = sysUserRoleService.getOne(queryWrapper);
                if (one == null) {
                    sysUserRoleService.save(UserRole);
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
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<UserRole> deleteUserRole(@RequestParam(name = "roleId") String roleId, @RequestParam(name = "userId") String userId) {
        Result<UserRole> result = new Result<>();
        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
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
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<UserRole> deleteUserRoleBatch(@RequestParam(name = "roleId") String roleId, @RequestParam(name = "userIds") String userIds) {
        Result<UserRole> result = new Result<>();
        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
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
        Result<IPage<User>> result = new Result<>();
        Page<User> page = new Page<>(pageNo, pageSize);
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
            IPage<User> pageList = userService.getUserByDepIds(page, subDepids, username);
            //批量查询用户的所属部门
            //step.1 先拿到全部的 useids
            //step.2 通过 useids，一次性查询用户的所属部门名字
            List<String> userIds = pageList.getRecords().stream().map(User::getId).collect(Collectors.toList());
            if (userIds.size() > 0) {
                Map<String, String> useDepNames = userService.getDepNamesByUserIds(userIds);
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
    public Result<?> queryByDepartId(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(name = "orgCode") String orgCode,
                                     User userParams) {
        IPage<SysUserSysDepartModel> pageList = userService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 针对通讯录模块做的接口，将多个部门的用户合并成一条记录，并转成对前端友好的格式
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public Result<?> queryByOrgCodeForAddressList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "orgCode", required = false) String orgCode,
                                                  User userParams) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = userService.queryUserByOrgCode(orgCode, userParams, page);
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
        Result<String> result = new Result<>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for (String sysUserId : sysDepartUsersVO.getUserIdList()) {
                val userDepart = UserDepart.builder()
                        .userId(sysUserId)
                        .depId(sysDepId)
                        .build();
                QueryWrapper<UserDepart> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id", sysUserId);
                UserDepart one = sysUserDepartService.getOne(queryWrapper);
                if (one == null) {
                    sysUserDepartService.save(userDepart);
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
                                                 @RequestParam(name = "userId") String userId
    ) {
        Result<UserDepart> result = new Result<>();
        try {
            QueryWrapper<UserDepart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("dep_id", depId).eq("user_id", userId);
            boolean b = sysUserDepartService.remove(queryWrapper);
            if (b) {
                List<DepartRole> departRoleList = departRoleService.list(new QueryWrapper<DepartRole>().eq("depart_id", depId));
                List<String> roleIds = departRoleList.stream().map(DepartRole::getId).collect(Collectors.toList());
                if (roleIds.size() > 0) {
                    QueryWrapper<DepartRoleUser> query = new QueryWrapper<>();
                    query.eq("user_id", userId).in("role_id", roleIds);
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
    public Result<UserDepart> deleteUserInDepartBatch(@RequestParam(name = "depId") String depId,
                                                      @RequestParam(name = "userIds") String userIds) {
        Result<UserDepart> result = new Result<>();
        try {
            QueryWrapper<UserDepart> queryWrapper = new QueryWrapper<>();
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
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String, Object>> getCurrentUserDeparts() {
        Result<Map<String, Object>> result = new Result<>();
        try {
            LoginUser User = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<Depart> list = this.sysDepartService.queryUserDeparts(User.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("list", list);
            map.put("orgCode", User.getOrgCode());
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
     */
    @PostMapping("/register")
    public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, User user) {
        Result<JSONObject> result = new Result<>();
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
        User sysUser1 = userService.getUserByName(username);
        if (sysUser1 != null) {
            result.setMessage("用户名已注册");
            result.setSuccess(false);
            return result;
        }
        User sysUser2 = userService.getUserByPhone(phone);
        if (sysUser2 != null) {
            result.setMessage("该手机号已注册");
            result.setSuccess(false);
            return result;
        }

        if (oConvertUtils.isNotEmpty(email)) {
            User sysUser3 = userService.getUserByEmail(email);
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
//            userService.addUserWithRole(user, "ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 test
            result.success("注册成功");
        } catch (Exception e) {
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * 根据用户名或手机号查询用户信息
     */
    @GetMapping("/querySysUser")
    public Result<Map<String, Object>> querySysUser(User User) {
        String phone = User.getPhone();
        String username = User.getUsername();
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> map = new HashMap<>();
        if (oConvertUtils.isNotEmpty(phone)) {
            User user = userService.getUserByPhone(phone);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        if (oConvertUtils.isNotEmpty(username)) {
            User user = userService.getUserByName(username);
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
        Result<Map<String, String>> result = new Result<>();
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
        User user = defUserService.getOne(query);
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
        Result<User> result = new Result<>();
        if (oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode) || oConvertUtils.isEmpty(phone)) {
            result.setMessage("重置密码失败！");
            result.setSuccess(false);
            return result;
        }

        User user;
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
        user = defUserService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username).eq(User::getPhone, phone));
        if (user == null) {
            result.setMessage("未找到用户！");
            result.setSuccess(false);
        } else {
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), password, salt);
            user.setPassword(passwordEncode);
            defUserService.updateById(user);
            result.setSuccess(true);
            result.setMessage("密码重置完成！");
        }
        return result;
    }


    /**
     * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
     */
    @GetMapping("/getUserSectionInfoByToken")
    public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        try {
            String username;
            // 如果没有传递token，就从header中获取token并获取用户信息
            if (oConvertUtils.isEmpty(token)) {
                username = JwtUtil.getUserNameByToken(request);
            } else {
                username = JwtUtil.getUsername(token);
            }

            log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

            // 根据用户名查询用户信息
            User User = userService.getUserByName(username);
            Map<String, Object> map = new HashMap<>();
            map.put("sysUserId", User.getId());
            map.put("sysUserCode", User.getUsername()); // 当前登录用户登录账号
            map.put("sysUserName", User.getRealname()); // 当前登录用户真实名称
            map.put("sysOrgCode", User.getOrgCode()); // 当前登录用户部门编号

            log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 【APP端接口】获取用户列表  根据用户名和真实名 模糊匹配
     */
    @GetMapping("/appUserList")
    public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
                                 @RequestParam(name = "username", required = false) String username,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(name = "syncFlow", required = false) String syncFlow) {
        try {
            //TODO 从查询效率上将不要用mp的封装的page分页查询 建议自己写分页语句
            LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
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
            IPage<User> res = defUserService.page(page, query);
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
    public Result<?> getRecycleBin() {
        List<User> logicDeletedUserList = userService.queryLogicDeleted();
        if (logicDeletedUserList.size() > 0) {
            // 批量查询用户的所属部门
            // step.1 先拿到全部的 userIds
            List<String> userIds = logicDeletedUserList.stream().map(User::getId).collect(Collectors.toList());
            // step.2 通过 userIds，一次性查询用户的所属部门名字
            Map<String, String> useDepNames = userService.getDepNamesByUserIds(userIds);
            logicDeletedUserList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return Result.ok(logicDeletedUserList);
    }

    /**
     * 还原被逻辑删除的用户
     */
    @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
    public Result<?> putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userIds = jsonObject.getString("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            User updateUser = new User();
            userService.revertLogicDeleted(Arrays.asList(userIds.split(",")), updateUser);
        }
        return Result.ok("还原成功");
    }

    /**
     * 彻底删除用户
     *
     * @param userIds 被删除的用户ID，多个id用半角逗号分割
     */
    @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
    public Result<?> deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            userService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return Result.ok("删除成功");
    }


    /**
     * 移动端修改用户信息
     */
    @RequestMapping(value = "/appEdit", method = RequestMethod.PUT)
    public Result<User> appEdit(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        Result<User> result = new Result<>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            User User = userService.getUserByName(username);
            sysBaseAPI.addLog("移动端编辑用户，id： " + jsonObject.getString("id"), CommonConstant.LOG_TYPE_2, 2);
            if (User == null) {
                result.error500("未找到对应用户!");
            } else {
                String realname = jsonObject.getString("realname");
                String avatar = jsonObject.getString("avatar");
                String sex = jsonObject.getString("sex");
                String phone = jsonObject.getString("phone");
                String email = jsonObject.getString("email");
                Date birthday = jsonObject.getDate("birthday");

                User.setRealname(realname);
                User.setAvatar(avatar);
                User.setSex(Integer.parseInt(sex));
                User.setBirthday(birthday);
                User.setPhone(phone);
                User.setEmail(email);
                defUserService.updateById(User);
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
    public Result<?> queryChildrenByUsername(@RequestParam("userId") String userId) {
        //获取用户信息
        Map<String, Object> map = new HashMap<>();
        User User = defUserService.getById(userId);
        String username = User.getUsername();
        Integer identity = User.getUserIdentity();
        map.put("User", User);
        if (identity != null && identity == 2) {
            //获取部门用户信息
            String departIds = User.getDepartIds();
            if (StringUtils.isNotBlank(departIds)) {
                List<String> departIdList = Arrays.asList(departIds.split(","));
                List<User> childrenUser = userService.queryByDepIds(departIdList, username);
                map.put("children", childrenUser);
            }
        }
        return Result.ok(map);
    }

    /**
     * 移动端查询部门用户信息
     */
    @GetMapping("/appQueryByDepartId")
    public Result<List<User>> appQueryByDepartId(@RequestParam(name = "departId", required = false) String departId) {
        Result<List<User>> result = new Result<>();
        List<String> list = new ArrayList<>();
        list.add(departId);
        List<User> childrenUser = userService.queryByDepIds(list, null);
        result.setResult(childrenUser);
        return result;
    }

    /**
     * 移动端查询用户信息(通过用户名模糊查询)
     */
    @GetMapping("/appQueryUser")
    public Result<List<User>> appQueryUser(@RequestParam(name = "keyword", required = false) String keyword) {
        Result<List<User>> result = new Result<>();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ne(User::getUsername, "_reserve_user_external");
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(i -> i.like(User::getUsername, keyword).or().like(User::getRealname, keyword));
        }
        List<User> list = defUserService.list(queryWrapper);
        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = list.stream().map(User::getId).collect(Collectors.toList());
        if (userIds.size() > 0) {
            Map<String, String> useDepNames = userService.getDepNamesByUserIds(userIds);
            list.forEach(item -> item.setOrgCodeTxt(useDepNames.get(item.getId())));
        }
        result.setResult(list);
        return result;
    }
}
