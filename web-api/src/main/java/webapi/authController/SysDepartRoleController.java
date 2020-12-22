package webapi.authController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import auth.domain.relation.depar.role.service.ISysDepartRoleService;
import auth.domain.relation.permission.depart.role.service.ISysDepartRolePermissionService;
import auth.domain.relation.permission.depart.service.ISysDepartPermissionService;
import auth.domain.relation.user.depart.role.service.ISysDepartRoleUserService;
import auth.entity.DepartRole;
import auth.entity.DepartRolePermission;
import auth.entity.DepartRoleUser;
import auth.entity.PermissionDataRule;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;
import commons.system.base.controller.JeecgController;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门角色
 */
@Slf4j
@Api(tags = "部门角色")
@RestController
@RequestMapping("/sys/sysDepartRole")
public class SysDepartRoleController extends JeecgController<DepartRole, ISysDepartRoleService> {

    private final ISysDepartRoleService sysDepartRoleService;

    private final ISysDepartRoleUserService departRoleUserService;

    private final ISysDepartPermissionService sysDepartPermissionService;

    private final ISysDepartRolePermissionService sysDepartRolePermissionService;

    @Autowired
    public SysDepartRoleController(ISysDepartRoleService sysDepartRoleService, ISysDepartRoleUserService departRoleUserService, ISysDepartPermissionService sysDepartPermissionService, ISysDepartRolePermissionService sysDepartRolePermissionService) {
        this.sysDepartRoleService = sysDepartRoleService;
        this.departRoleUserService = departRoleUserService;
        this.sysDepartPermissionService = sysDepartPermissionService;
        this.sysDepartRolePermissionService = sysDepartRolePermissionService;
    }

    /**
     * 分页列表查询
     */
    @ApiOperation(value = "部门角色-分页列表查询", notes = "部门角色-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(DepartRole departRole,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(name = "deptId", required = false) String deptId,
                                   HttpServletRequest req) {
        QueryWrapper<DepartRole> queryWrapper = QueryGenerator.initQueryWrapper(departRole, req.getParameterMap());
        Page<DepartRole> page = new Page<>(pageNo, pageSize);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> deptIds = null;
//		if(oConvertUtils.isEmpty(deptId)){
//			if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2) ){
//				deptIds = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
//			}else{
//				return Result.ok(null);
//			}
//		}else{
//			deptIds = sysDepartService.getSubDepIdsByDepId(deptId);
//		}
//		queryWrapper.in("depart_id",deptIds);

        //我的部门，选中部门只能看当前部门下的角色
        queryWrapper.eq("depart_id", deptId);
        IPage<DepartRole> pageList = sysDepartRoleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     */
    @ApiOperation(value = "部门角色-添加", notes = "部门角色-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DepartRole departRole) {
        sysDepartRoleService.save(departRole);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     */
    @ApiOperation(value = "部门角色-编辑", notes = "部门角色-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DepartRole departRole) {
        sysDepartRoleService.updateById(departRole);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "部门角色-通过id删除")
    @ApiOperation(value = "部门角色-通过id删除", notes = "部门角色-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        sysDepartRoleService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "部门角色-批量删除")
    @ApiOperation(value = "部门角色-批量删除", notes = "部门角色-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.sysDepartRoleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @ApiOperation(value = "部门角色-通过id查询", notes = "部门角色-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        DepartRole departRole = sysDepartRoleService.getById(id);
        return Result.ok(departRole);
    }

    /**
     * 获取部门下角色
     */
    @RequestMapping(value = "/getDeptRoleList", method = RequestMethod.GET)
    public Result<List<DepartRole>> getDeptRoleList(@RequestParam(value = "departId") String departId) {
        Result<List<DepartRole>> result = new Result<>();
        //查询选中部门的角色
        List<DepartRole> deptRoleList = sysDepartRoleService.list(new LambdaQueryWrapper<DepartRole>().eq(DepartRole::getDepartId, departId));
        result.setSuccess(true);
        result.setResult(deptRoleList);
        return result;
    }

    /**
     * 设置
     */
    @RequestMapping(value = "/deptRoleUserAdd", method = RequestMethod.POST)
    public Result<?> deptRoleAdd(@RequestBody JSONObject json) {
        String newRoleId = json.getString("newRoleId");
        String oldRoleId = json.getString("oldRoleId");
        String userId = json.getString("userId");
        departRoleUserService.deptRoleUserAdd(userId, newRoleId, oldRoleId);
        return Result.ok("添加成功！");
    }

    /**
     * 根据用户id获取已设置部门角色
     */
    @RequestMapping(value = "/getDeptRoleByUserId", method = RequestMethod.GET)
    public Result<List<DepartRoleUser>> getDeptRoleByUserId(@RequestParam(value = "userId") String userId, @RequestParam(value = "departId") String departId) {
        Result<List<DepartRoleUser>> result = new Result<>();
        //查询部门下角色
        List<DepartRole> roleList = sysDepartRoleService.list(new QueryWrapper<DepartRole>().eq("depart_id", departId));
        List<String> roleIds = roleList.stream().map(DepartRole::getId).collect(Collectors.toList());
        //根据角色id,用户id查询已授权角色
        List<DepartRoleUser> roleUserList = departRoleUserService.list(new QueryWrapper<DepartRoleUser>().eq("user_id", userId).in("drole_id", roleIds));
        result.setSuccess(true);
        result.setResult(roleUserList);
        return result;
    }

    /**
     * 查询数据规则数据
     */
    @GetMapping(value = "/datarule/{permissionId}/{departId}/{roleId}")
    public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId, @PathVariable("departId") String departId, @PathVariable("roleId") String roleId) {
        //查询已授权的部门规则
        List<PermissionDataRule> list = sysDepartPermissionService.getPermRuleListByDeptIdAndPermId(departId, permissionId);
        if (list == null || list.size() == 0) {
            return Result.error("未找到权限配置信息");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("datarule", list);
            LambdaQueryWrapper<DepartRolePermission> query = new LambdaQueryWrapper<DepartRolePermission>()
                    .eq(DepartRolePermission::getPermissionId, permissionId)
                    .eq(DepartRolePermission::getRoleId, roleId);
            DepartRolePermission sysRolePermission = sysDepartRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
//                return Result.error("未找到角色菜单配置信息");
            } else {
                String drChecked = sysRolePermission.getDataRuleIds();
                if (oConvertUtils.isNotEmpty(drChecked)) {
                    map.put("drChecked", drChecked.endsWith(",") ? drChecked.substring(0, drChecked.length() - 1) : drChecked);
                }
            }
            return Result.ok(map);
            //TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
        }
    }

    /**
     * 保存数据规则至角色菜单关联表
     */
    @PostMapping(value = "/datarule")
    public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
        try {
            String permissionId = jsonObject.getString("permissionId");
            String roleId = jsonObject.getString("roleId");
            String dataRuleIds = jsonObject.getString("dataRuleIds");
            log.info("保存数据规则>>" + "菜单ID:" + permissionId + "角色ID:" + roleId + "数据权限ID:" + dataRuleIds);
            LambdaQueryWrapper<DepartRolePermission> query = new LambdaQueryWrapper<DepartRolePermission>()
                    .eq(DepartRolePermission::getPermissionId, permissionId)
                    .eq(DepartRolePermission::getRoleId, roleId);
            DepartRolePermission sysRolePermission = sysDepartRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
                return Result.error("请先保存角色菜单权限!");
            } else {
                sysRolePermission.setDataRuleIds(dataRuleIds);
                this.sysDepartRolePermissionService.updateById(sysRolePermission);
            }
        } catch (Exception e) {
            log.error("SysRoleController.saveDatarule()发生异常：" + e.getMessage(), e);
            return Result.error("保存失败");
        }
        return Result.ok("保存成功!");
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DepartRole departRole) {
        return super.exportXls(request, departRole, DepartRole.class, "部门角色");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DepartRole.class);
    }

}
