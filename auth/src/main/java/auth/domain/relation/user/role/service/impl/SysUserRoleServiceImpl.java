package auth.domain.relation.user.role.service.impl;

import lombok.extern.slf4j.Slf4j;
import auth.entity.UserRole;
import auth.domain.relation.user.role.mapper.SysUserRoleMapper;
import auth.domain.relation.user.role.service.ISysUserRoleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 用户角色 服务实现类
 */
@Slf4j
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, UserRole> implements ISysUserRoleService {
}
