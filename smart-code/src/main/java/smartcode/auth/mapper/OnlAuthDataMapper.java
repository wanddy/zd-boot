package smartcode.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import commons.auth.vo.SysPermissionDataRuleModel;
import org.apache.ibatis.annotations.Param;
import smartcode.auth.entity.OnlAuthData;

import java.util.List;

public interface OnlAuthDataMapper extends BaseMapper<OnlAuthData> {
    List<SysPermissionDataRuleModel> queryOwnerAuth(@Param("userId") String userId, @Param("cgformId") String cgformId);
}
