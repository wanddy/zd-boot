package auth.domain.role.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import auth.domain.user.mapper.UserMapper;
import auth.entity.Role;
import commons.api.vo.Result;
import commons.constant.CommonConstant;
import commons.util.ImportExcelUtil;
import auth.domain.role.mapper.SysRoleMapper;
import auth.domain.role.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, Role> implements ISysRoleService {

    private final SysRoleMapper sysRoleMapper;

    private final UserMapper sysUserMapper;

    @Autowired
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper, UserMapper sysUserMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String roleId) {
        //1.删除角色和用户关系
        sysRoleMapper.deleteRoleUserRelation(roleId);
        //2.删除角色和权限关系
        sysRoleMapper.deleteRolePermissionRelation(roleId);
        //3.删除角色
        return this.removeById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchRole(String[] roleIds) {
        //1.删除角色和用户关系
        sysUserMapper.deleteBathRoleUserRelation(roleIds);
        //2.删除角色和权限关系
        sysUserMapper.deleteBathRolePermissionRelation(roleIds);
        //3.删除角色
        return this.removeByIds(Arrays.asList(roleIds));
    }
}
