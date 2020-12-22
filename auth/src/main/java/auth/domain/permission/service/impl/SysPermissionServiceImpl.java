package auth.domain.permission.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.exception.ZdException;
import commons.util.oConvertUtils;
import auth.entity.Permission;
import auth.entity.PermissionDataRule;
import auth.domain.relation.permission.depart.mapper.SysDepartPermissionMapper;
import auth.domain.relation.permission.depart.role.mapper.SysDepartRolePermissionMapper;
import auth.domain.permission.mapper.SysPermissionMapper;
import auth.domain.relation.permission.role.mapper.SysRolePermissionMapper;
import auth.discard.model.TreeModel;
import auth.domain.relation.permission.rule.serivce.ISysPermissionDataRuleService;
import auth.domain.permission.service.ISysPermissionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 菜单权限 服务实现类
 */
@Slf4j
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, Permission> implements ISysPermissionService {

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private ISysPermissionDataRuleService permissionDataRuleService;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Resource
    private SysDepartPermissionMapper sysDepartPermissionMapper;

    @Resource
    private SysDepartRolePermissionMapper sysDepartRolePermissionMapper;

    @Override
    public List<TreeModel> queryListByParentId(String parentId) {
        return sysPermissionMapper.queryListByParentId(parentId);
    }

    /**
     * 真实删除
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void deletePermission(String id) throws ZdException {
        Permission permission = this.getById(id);
        if (permission == null) {
            throw new ZdException("未找到菜单信息");
        }
        String pid = permission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            int count = this.count(new QueryWrapper<Permission>().lambda().eq(Permission::getParentId, pid));
            if (count == 1) {
                //若父节点无其他子节点，则该父节点是叶子节点
                this.sysPermissionMapper.setMenuLeaf(pid, 1);
            }
        }
        sysPermissionMapper.deleteById(id);
        // 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
        this.removeChildrenBy(permission.getId());
        //关联删除
        Map map = new HashMap<>();
        map.put("permission_id", id);
        //删除数据规则
        this.deletePermRuleByPermId(id);
        //删除角色授权表
        sysRolePermissionMapper.deleteByMap(map);
        //删除部门权限表
        sysDepartPermissionMapper.deleteByMap(map);
        //删除部门角色授权
        sysDepartRolePermissionMapper.deleteByMap(map);
    }

    /**
     * 根据父id删除其关联的子节点数据
     *
     * @return
     */
    public void removeChildrenBy(String parentId) {
        LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<>();
        // 封装查询条件parentId为主键,
        query.eq(Permission::getParentId, parentId);
        // 查出该主键下的所有子级
        List<Permission> permissionList = this.list(query);
        if (permissionList != null && permissionList.size() > 0) {
            String id = ""; // id
            int num = 0; // 查出的子级数量
            // 如果查出的集合不为空, 则先删除所有
            this.remove(query);
            // 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
            for (int i = 0, len = permissionList.size(); i < len; i++) {
                id = permissionList.get(i).getId();
                Map map = new HashMap<>();
                map.put("permission_id", id);
                //删除数据规则
                this.deletePermRuleByPermId(id);
                //删除角色授权表
                sysRolePermissionMapper.deleteByMap(map);
                //删除部门权限表
                sysDepartPermissionMapper.deleteByMap(map);
                //删除部门角色授权
                sysDepartRolePermissionMapper.deleteByMap(map);
                num = this.count(new LambdaQueryWrapper<Permission>().eq(Permission::getParentId, id));
                // 如果有, 则递归
                if (num > 0) {
                    this.removeChildrenBy(id);
                }
            }
        }
    }

    /**
     * 逻辑删除
     */
    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    //@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true,condition="#sysPermission.menuType==2")
    public void deletePermissionLogical(String id) throws ZdException {
        Permission permission = this.getById(id);
        if (permission == null) {
            throw new ZdException("未找到菜单信息");
        }
        String pid = permission.getParentId();
        int count = this.count(new QueryWrapper<Permission>().lambda().eq(Permission::getParentId, pid));
        if (count == 1) {
            //若父节点无其他子节点，则该父节点是叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 1);
        }
        permission.setDelFlag(1);
        this.updateById(permission);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void addPermission(Permission permission) throws ZdException {
        //----------------------------------------------------------------------
        //判断是否是一级菜单，是的话清空父菜单
        if (CommonConstant.MENU_TYPE_0.equals(permission.getMenuType())) {
            permission.setParentId(null);
        }
        //----------------------------------------------------------------------
        String pid = permission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            //设置父节点不为叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 0);
        }
        permission.setDelFlag(0);
        permission.setLeaf(true);
        this.save(permission);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void editPermission(Permission permission) throws ZdException {
        Permission p = this.getById(permission.getId());
        //TODO 该节点判断是否还有子节点
        if (p == null) {
            throw new ZdException("未找到菜单信息");
        } else {
            //----------------------------------------------------------------------
            //Step1.判断是否是一级菜单，是的话清空父菜单ID
            if (CommonConstant.MENU_TYPE_0.equals(permission.getMenuType())) {
                permission.setParentId("");
            }
            //Step2.判断菜单下级是否有菜单，无则设置为叶子节点
            int count = this.count(new QueryWrapper<Permission>().lambda().eq(Permission::getParentId, permission.getId()));
            if (count == 0) {
                permission.setLeaf(true);
            }
            //----------------------------------------------------------------------
            this.updateById(permission);

            //如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
            String pid = permission.getParentId();
            if ((oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid) && oConvertUtils.isNotEmpty(p.getParentId())) {
                //AConfig.设置新的父菜单不为叶子节点
                this.sysPermissionMapper.setMenuLeaf(pid, 0);
                //BModel.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
                int cc = this.count(new QueryWrapper<Permission>().lambda().eq(Permission::getParentId, p.getParentId()));
                if (cc == 0) {
                    if (oConvertUtils.isNotEmpty(p.getParentId())) {
                        this.sysPermissionMapper.setMenuLeaf(p.getParentId(), 1);
                    }
                }

            }
        }

    }

    @Override
    public List<Permission> queryByUser(String username) {
        return this.sysPermissionMapper.queryByUser(username);
    }

    /**
     * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
     */
    @Override
    public void deletePermRuleByPermId(String id) {
        LambdaQueryWrapper<PermissionDataRule> query = new LambdaQueryWrapper<>();
        query.eq(PermissionDataRule::getPermissionId, id);
        int countValue = this.permissionDataRuleService.count(query);
        if (countValue > 0) {
            this.permissionDataRuleService.remove(query);
        }
    }

    /**
     * 获取模糊匹配规则的数据权限URL
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE)
    public List<String> queryPermissionUrlWithStar() {
        return this.baseMapper.queryPermissionUrlWithStar();
    }

    @Override
    public boolean hasPermission(String username, Permission permission) {
        int count = baseMapper.queryCountByUsername(username, permission);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasPermission(String username, String url) {
        Permission permission = new Permission();
        permission.setUrl(url);
        int count = baseMapper.queryCountByUsername(username, permission);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

}
