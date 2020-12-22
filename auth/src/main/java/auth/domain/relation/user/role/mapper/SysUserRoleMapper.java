package auth.domain.relation.user.role.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import auth.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Repository
public interface SysUserRoleMapper extends BaseMapper<UserRole> {

    @Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
    List<String> getRoleByUserName(@Param("username") String username);

    @Select("select id from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
    List<String> getRoleIdByUserName(@Param("username") String username);

    void removeUserRoleRelationByUserId(@Param("userId") String userId);

    void removeUserRoleRelationByUserIds(@Param("userIds") List<String> userIds);

}
