package auth.domain.relation.depar.role.service;

import auth.entity.DepartRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 部门角色
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
public interface ISysDepartRoleService extends IService<DepartRole> {

    /**
     * 根据用户id，部门id查询可授权所有部门角色
     * @param orgCode
     * @param userId
     * @return
     */
    List<DepartRole> queryDeptRoleByDeptAndUser(String orgCode, String userId);

}
