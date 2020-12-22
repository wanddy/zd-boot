package auth.domain.relation.depar.role.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import auth.entity.DepartRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Repository
public interface SysDepartRoleMapper extends BaseMapper<DepartRole> {
    /**
     * 根据用户id，部门id查询可授权所有部门角色
     * @param orgCode
     * @param userId
     * @return
     */
    List<DepartRole> queryDeptRoleByDeptAndUser(@Param("orgCode") String orgCode, @Param("userId") String userId);
}
