package webapi.authController;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.model.TreeModel;
import auth.domain.permission.service.ISysPermissionService;
import auth.domain.relation.permission.role.service.ISysRolePermissionService;
import auth.domain.relation.permission.rule.serivce.ISysPermissionDataRuleService;
import auth.domain.role.service.ISysRoleService;
import auth.entity.Permission;
import auth.entity.PermissionDataRule;
import auth.entity.Role;
import auth.entity.RolePermission;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;
import commons.constant.CommonConstant;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =
 * 角色表 前端控制器=
 */
@RestController
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    private final ISysPermissionDataRuleService sysPermissionDataRuleService;

    private final ISysRolePermissionService sysRolePermissionService;

    private final ISysPermissionService sysPermissionService;

    @Autowired
    public SysRoleController(ISysRoleService sysRoleService, ISysPermissionDataRuleService sysPermissionDataRuleService, ISysRolePermissionService sysRolePermissionService, ISysPermissionService sysPermissionService) {
        this.sysRoleService = sysRoleService;
        this.sysPermissionDataRuleService = sysPermissionDataRuleService;
        this.sysRolePermissionService = sysRolePermissionService;
        this.sysPermissionService = sysPermissionService;
    }

    /**
     * 分页列表查询=
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Role>> queryPageList(Role role,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest req) {
        Result<IPage<Role>> result = new Result<>();
        QueryWrapper<Role> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
        Page<Role> page = new Page<>(pageNo, pageSize);
        IPage<Role> pageList = sysRoleService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加=
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Role> add(@RequestBody Role role) {
        Result<Role> result = new Result<>();
        try {
            sysRoleService.save(role);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<Role> edit(@RequestBody Role role) {
        Result<Role> result = new Result<>();
        Role Role = sysRoleService.getById(role.getId());
        if (Role == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysRoleService.updateById(role);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id") String id) {
        sysRoleService.deleteRole(id);
        return Result.ok("删除角色成功");
    }

    /**
     * 批量删除
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<Role> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<Role> result = new Result<>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("未选中角色！");
        } else {
            sysRoleService.deleteBatchRole(ids.split(","));
            result.success("删除角色成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<Role> queryById(@RequestParam(name = "id") String id) {
        Result<Role> result = new Result<>();
        Role Role = sysRoleService.getById(id);
        if (Role == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(Role);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryall", method = RequestMethod.GET)
    public Result<List<Role>> queryall() {
        Result<List<Role>> result = new Result<>();
        List<Role> list = sysRoleService.list();
        if (list == null || list.size() <= 0) {
            result.error500("未找到角色信息");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 校验角色编码唯一
     */
    @RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
    public Result<Boolean> checkUsername(String id, String roleCode) {
        Result<Boolean> result = new Result<>();
        result.setResult(true);//如果此参数为false则程序发生异常
        log.info("--验证角色编码是否唯一---id:" + id + "--roleCode:" + roleCode);
        try {
            Role role = null;
            if (oConvertUtils.isNotEmpty(id)) {
                role = sysRoleService.getById(id);
            }
            Role newRole = sysRoleService.getOne(new QueryWrapper<Role>().lambda().eq(Role::getRoleCode, roleCode));
            if (newRole != null) {
                //如果根据传入的roleCode查询到信息了，那么就需要做校验了。
                if (role == null) {
                    //role为空=>新增模式=>只要roleCode存在则返回false
                    result.setSuccess(false);
                    result.setMessage("角色编码已存在");
                    return result;
                } else if (!id.equals(newRole.getId())) {
                    //否则=>编辑模式=>判断两者ID是否一致-
                    result.setSuccess(false);
                    result.setMessage("角色编码已存在");
                    return result;
                }
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 查询数据规则数据
     */
    @GetMapping(value = "/datarule/{permissionId}/{roleId}")
    public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId, @PathVariable("roleId") String roleId) {
        List<PermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
        if (list == null || list.size() == 0) {
            return Result.error("未找到权限配置信息");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("datarule", list);
            LambdaQueryWrapper<RolePermission> query = new LambdaQueryWrapper<RolePermission>()
                    .eq(RolePermission::getPermissionId, permissionId)
                    .isNotNull(RolePermission::getDataRuleIds)
                    .eq(RolePermission::getRoleId, roleId);
            RolePermission rolePermission = sysRolePermissionService.getOne(query);
            if (rolePermission == null) {
//                return Result.error("未找到角色菜单配置信息");
            } else {
                String drChecked = rolePermission.getDataRuleIds();
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
            LambdaQueryWrapper<RolePermission> query = new LambdaQueryWrapper<RolePermission>()
                    .eq(RolePermission::getPermissionId, permissionId)
                    .eq(RolePermission::getRoleId, roleId);
            RolePermission rolePermission = sysRolePermissionService.getOne(query);
            if (rolePermission == null) {
                return Result.error("请先保存角色菜单权限!");
            } else {
                rolePermission.setDataRuleIds(dataRuleIds);
                this.sysRolePermissionService.updateById(rolePermission);
            }
        } catch (Exception e) {
            log.error("SysRoleController.saveDatarule()发生异常：" + e.getMessage(), e);
            return Result.error("保存失败");
        }
        return Result.ok("保存成功!");
    }


    /**
     * 用户角色授权功能，查询菜单权限树
     */
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeList() {
        Result<Map<String, Object>> result = new Result<>();
        //全部权限ids
        List<String> ids = new ArrayList<>();
        try {
            LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<>();
            query.eq(Permission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(Permission::getSortNo);
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
