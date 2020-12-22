package smartcode.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import smartcode.auth.entity.OnlAuthPage;
import smartcode.auth.vo.AuthPageVO;

import java.util.List;

public interface OnlAuthPageMapper extends BaseMapper<OnlAuthPage> {
    List<AuthPageVO> queryRoleAuthByFormId(@Param("roleId") String roleId, @Param("cgformId") String cgformId, @Param("type") int type);

    List<AuthPageVO> queryAuthColumnByFormId(@Param("cgformId") String cgformId);

    List<AuthPageVO> queryAuthButtonByFormId(@Param("cgformId") String cgformId);

    List<AuthPageVO> queryRoleDataAuth(@Param("roleId") String roleId, @Param("cgformId") String cgformId);

    List<String> queryRoleNoAuthCode(@Param("userId") String userId, @Param("cgformId") String cgformId, @Param("control") Integer control, @Param("page") Integer page, @Param("type") Integer type);
}
