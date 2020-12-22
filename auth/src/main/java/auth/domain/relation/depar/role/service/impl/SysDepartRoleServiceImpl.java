package auth.domain.relation.depar.role.service.impl;

import lombok.extern.slf4j.Slf4j;
import auth.entity.DepartRole;
import auth.domain.relation.depar.role.mapper.SysDepartRoleMapper;
import auth.domain.relation.depar.role.service.ISysDepartRoleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 部门角色
 */
@Slf4j
@Service
public class SysDepartRoleServiceImpl extends ServiceImpl<SysDepartRoleMapper, DepartRole> implements ISysDepartRoleService {

    @Override
    public List<DepartRole> queryDeptRoleByDeptAndUser(String orgCode, String userId) {
        return this.baseMapper.queryDeptRoleByDeptAndUser(orgCode, userId);
    }
}
