package tech.signUp.mapper;

import java.util.List;

import commons.auth.vo.LoginUser;
import org.apache.ibatis.annotations.Param;
import tech.signUp.entity.SignUp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;

/**
 * @Description: 报名表
 * @Author: zd-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
public interface SignUpMapper extends BaseMapper<SignUp> {

    List<TechField> getFieldList();

    boolean batchUpdate(@Param("asList") List<String> asList, @Param("audit") String audit);

    List<SignUp> getList(@Param("signUp") SignUp signUp, @Param("user") LoginUser user);

    List<SignUp> getByTech(@Param("list") List<TechActivity> list);

    List<SignUp> queryList(@Param("sign") SignUp sign, @Param("user") LoginUser user);

}
