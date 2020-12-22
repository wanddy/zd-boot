package auth.domain.relation.user.depart.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import auth.entity.UserDepart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Repository
public interface SysUserDepartMapper extends BaseMapper<UserDepart> {

    List<UserDepart> getUserDepartByUid(@Param("userId") String userId);

    void removeUserDepartRelationByUserId(@Param("userId") String userId);

    void removeUserDepartRelationByUserIds(@Param("userIds") List<String> userIds);
}
