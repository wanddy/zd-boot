package webapi.authController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.model.TreeModel;
import auth.domain.permission.service.ISysPermissionService;
import auth.domain.relation.permission.depart.role.service.ISysDepartRolePermissionService;
import auth.domain.relation.permission.depart.service.ISysDepartPermissionService;
import auth.domain.relation.permission.rule.serivce.ISysPermissionDataRuleService;
import auth.entity.DepartPermission;
import auth.entity.DepartRolePermission;
import auth.entity.Permission;
import auth.entity.PermissionDataRule;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.constant.CommonConstant;
import commons.system.base.controller.JeecgController;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门权限表
 */
@Slf4j
@Api(tags = "部门权限表")
@RestController
@RequestMapping("/sys/sysDepartPermission")
public class SysDepartPermissionController extends JeecgController<DepartPermission, ISysDepartPermissionService> {

    private final ISysDepartPermissionService sysDepartPermissionService;

    private final ISysPermissionDataRuleService sysPermissionDataRuleService;

    private final ISysPermissionService sysPermissionService;

    private final ISysDepartRolePermissionService sysDepartRolePermissionService;


    @Autowired
    public SysDepartPermissionController(ISysDepartPermissionService sysDepartPermissionService, ISysPermissionDataRuleService sysPermissionDataRuleService, ISysPermissionService sysPermissionService, ISysDepartRolePermissionService sysDepartRolePermissionService) {
        this.sysDepartPermissionService = sysDepartPermissionService;
        this.sysPermissionDataRuleService = sysPermissionDataRuleService;
        this.sysPermissionService = sysPermissionService;
        this.sysDepartRolePermissionService = sysDepartRolePermissionService;
    }

    /**
     * 分页列表查询
     */
    @ApiOperation(value = "部门权限表-分页列表查询", notes = "部门权限表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(DepartPermission departPermission,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<DepartPermission> queryWrapper = QueryGenerator.initQueryWrapper(departPermission, req.getParameterMap());
        Page<DepartPermission> page = new Page<>(pageNo, pageSize);
        IPage<DepartPermission> pageList = sysDepartPermissionService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     */
    @ApiOperation(value = "部门权限表-添加", notes = "部门权限表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DepartPermission departPermission) {
        sysDepartPermissionService.save(departPermission);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     */
    @ApiOperation(value = "部门权限表-编辑", notes = "部门权限表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DepartPermission departPermission) {
        sysDepartPermissionService.updateById(departPermission);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     */
    @ApiOperation(value = "部门权限表-通过id删除", notes = "部门权限表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        sysDepartPermissionService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     */
    @ApiOperation(value = "部门权限表-批量删除", notes = "部门权限表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.sysDepartPermissionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @ApiOperation(value = "部门权限表-通过id查询", notes = "部门权限表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        DepartPermission departPermission = sysDepartPermissionService.getById(id);
        return Result.ok(departPermission);
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DepartPermission departPermission) {
        return super.exportXls(request, departPermission, DepartPermission.class, "部门权限表");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DepartPermission.class);
    }

    /**
     * 部门管理授权查询数据规则数据
     */
    @GetMapping(value = "/datarule/{permissionId}/{departId}")
    public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId, @PathVariable("departId") String departId) {
        List<PermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
        if (list == null || list.size() == 0) {
            return Result.error("未找到权限配置信息");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("datarule", list);
            LambdaQueryWrapper<DepartPermission> query = new LambdaQueryWrapper<DepartPermission>()
                    .eq(DepartPermission::getPermissionId, permissionId)
                    .eq(DepartPermission::getDepartId, departId);
            DepartPermission departPermission = sysDepartPermissionService.getOne(query);
            if (departPermission == null) {
//                return Result.error("未找到角色菜单配置信息");
            } else {
                String drChecked = departPermission.getDataRuleIds();
                if (oConvertUtils.isNotEmpty(drChecked)) {
                    map.put("drChecked", drChecked.endsWith(",") ? drChecked.substring(0, drChecked.length() - 1) : drChecked);
                }
            }
            return Result.ok(map);
            //TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
        }
    }

    /**
     * 保存数据规则至部门菜单关联表
     */
    @PostMapping(value = "/datarule")
    public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
        try {
            String permissionId = jsonObject.getString("permissionId");
            String departId = jsonObject.getString("departId");
            String dataRuleIds = jsonObject.getString("dataRuleIds");
            log.info("保存数据规则>>" + "菜单ID:" + permissionId + "部门ID:" + departId + "数据权限ID:" + dataRuleIds);
            LambdaQueryWrapper<DepartPermission> query = new LambdaQueryWrapper<DepartPermission>()
                    .eq(DepartPermission::getPermissionId, permissionId)
                    .eq(DepartPermission::getDepartId, departId);
            DepartPermission departPermission = sysDepartPermissionService.getOne(query);
            if (departPermission == null) {
                return Result.error("请先保存部门菜单权限!");
            } else {
                departPermission.setDataRuleIds(dataRuleIds);
                this.sysDepartPermissionService.updateById(departPermission);
            }
        } catch (Exception e) {
            log.error("SysDepartPermissionController.saveDatarule()发生异常：" + e.getMessage(), e);
            return Result.error("保存失败");
        }
        return Result.ok("保存成功!");
    }

    /**
     * 查询角色授权
     */
    @RequestMapping(value = "/queryDeptRolePermission", method = RequestMethod.GET)
    public Result<List<String>> queryDeptRolePermission(@RequestParam(name = "roleId") String roleId) {
        Result<List<String>> result = new Result<>();
        try {
            List<DepartRolePermission> list = sysDepartRolePermissionService.list(new QueryWrapper<DepartRolePermission>().lambda().eq(DepartRolePermission::getRoleId, roleId));
            result.setResult(list.stream().map(SysDepartRolePermission -> String.valueOf(SysDepartRolePermission.getPermissionId())).collect(Collectors.toList()));
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 保存角色授权
     */
    @RequestMapping(value = "/saveDeptRolePermission", method = RequestMethod.POST)
    public Result<String> saveDeptRolePermission(@RequestBody JSONObject json) {
        long start = System.currentTimeMillis();
        Result<String> result = new Result<>();
        try {
            String roleId = json.getString("roleId");
            String permissionIds = json.getString("permissionIds");
            String lastPermissionIds = json.getString("lastpermissionIds");
            this.sysDepartRolePermissionService.saveDeptRolePermission(roleId, permissionIds, lastPermissionIds);
            result.success("保存成功！");
            log.info("======部门角色授权成功=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
        } catch (Exception e) {
            result.error500("授权失败！");
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 用户角色授权功能，查询菜单权限树
     */
    @RequestMapping(value = "/queryTreeListForDeptRole", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeListForDeptRole(@RequestParam(name = "departId") String departId) {
        Result<Map<String, Object>> result = new Result<>();
        //全部权限ids
        List<String> ids = new ArrayList<>();
        try {
            LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<>();
            query.eq(Permission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(Permission::getSortNo);
            query.inSql(Permission::getId, "select permission_id  from sys_depart_permission where depart_id='" + departId + "'");
            List<Permission> list = sysPermissionService.list(query);
            for (Permission sysPer : list) {
                ids.add(sysPer.getId());
            }
            List<TreeModel> treeList = new ArrayList<>();
            getTreeModelList(treeList, list, null);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("treeList", treeList); //全部树节点数据
            resMap.put("ids", ids);//全部树ids
            result.setResult(resMap);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    private void getTreeModelList(List<TreeModel> treeList, List<Permission> metaList, TreeModel temp) {
        for (Permission permission : metaList) {
            String tempPid = permission.getParentId();
            TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(), permission.getRuleFlag(), permission.isLeaf());
            if (temp == null && oConvertUtils.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
                temp.getChildren().add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            }

        }
    }

}
