package auth.domain.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import auth.entity.Role;

/**
 * 角色服务
 */
public interface ISysRoleService extends IService<Role> {


    /**
     * 删除角色
     * @param roleId
     * @return
     */
    boolean deleteRole(String roleId);

    /**
     * 批量删除角色
     * @param roleIds
     * @return
     */
    boolean deleteBatchRole(String[] roleIds);

}
