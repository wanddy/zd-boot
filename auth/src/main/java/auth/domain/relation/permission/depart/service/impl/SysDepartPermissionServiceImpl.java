package auth.domain.relation.permission.depart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import commons.util.oConvertUtils;
import auth.entity.DepartPermission;
import auth.entity.DepartRole;
import auth.entity.DepartRolePermission;
import auth.entity.PermissionDataRule;
import auth.domain.relation.permission.depart.mapper.SysDepartPermissionMapper;
import auth.domain.relation.depar.role.mapper.SysDepartRoleMapper;
import auth.domain.relation.permission.depart.role.mapper.SysDepartRolePermissionMapper;
import auth.domain.relation.permission.rule.mapper.SysPermissionDataRuleMapper;
import auth.domain.relation.permission.depart.service.ISysDepartPermissionService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门权限
 */
@Slf4j
@Service
public class SysDepartPermissionServiceImpl extends ServiceImpl<SysDepartPermissionMapper, DepartPermission> implements ISysDepartPermissionService {
    @Resource
    private SysPermissionDataRuleMapper ruleMapper;

    @Resource
    private SysDepartRoleMapper sysDepartRoleMapper;

    @Resource
    private SysDepartRolePermissionMapper departRolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDepartPermission(String departId, String permissionIds, String lastPermissionIds) {
        List<String> add = getDiff(lastPermissionIds, permissionIds);
        if (add != null && add.size() > 0) {
            List<DepartPermission> list = new ArrayList<DepartPermission>();
            for (String p : add) {
                if (oConvertUtils.isNotEmpty(p)) {
                    DepartPermission rolepms = new DepartPermission(departId, p);
                    list.add(rolepms);
                }
            }
            this.saveBatch(list);
        }
        List<String> delete = getDiff(permissionIds, lastPermissionIds);
        if (delete != null && delete.size() > 0) {
            for (String permissionId : delete) {
                this.remove(new QueryWrapper<DepartPermission>().lambda().eq(DepartPermission::getDepartId, departId).eq(DepartPermission::getPermissionId, permissionId));
                //删除部门权限时，删除部门角色中已授权的权限
                List<DepartRole> departRoleList = sysDepartRoleMapper.selectList(new LambdaQueryWrapper<DepartRole>().eq(DepartRole::getDepartId, departId));
                List<String> roleIds = departRoleList.stream().map(DepartRole::getId).collect(Collectors.toList());
                if (roleIds.size() > 0) {
                    departRolePermissionMapper.delete(new LambdaQueryWrapper<DepartRolePermission>().eq(DepartRolePermission::getPermissionId, permissionId));
                }
            }
        }
    }

    @Override
    public List<PermissionDataRule> getPermRuleListByDeptIdAndPermId(String departId, String permissionId) {
        DepartPermission departPermission = this.getOne(new QueryWrapper<DepartPermission>().lambda().eq(DepartPermission::getDepartId, departId).eq(DepartPermission::getPermissionId, permissionId));
        if (departPermission != null) {
            LambdaQueryWrapper<PermissionDataRule> query = new LambdaQueryWrapper<PermissionDataRule>();
            query.in(PermissionDataRule::getId, Arrays.asList(departPermission.getDataRuleIds().split(",")));
            query.orderByDesc(PermissionDataRule::getCreateTime);
            return this.ruleMapper.selectList(query);
        } else {
            return null;
        }
    }

    /**
     * 从diff中找出main中没有的元素
     *
     * @param main
     * @param diff
     * @return
     */
    private List<String> getDiff(String main, String diff) {
        if (oConvertUtils.isEmpty(diff)) {
            return null;
        }
        if (oConvertUtils.isEmpty(main)) {
            return Arrays.asList(diff.split(","));
        }

        String[] mainArr = main.split(",");
        String[] diffArr = diff.split(",");
        Map<String, Integer> map = new HashMap<>();
        for (String string : mainArr) {
            map.put(string, 1);
        }
        List<String> res = new ArrayList<String>();
        for (String key : diffArr) {
            if (oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
                res.add(key);
            }
        }
        return res;
    }
}
