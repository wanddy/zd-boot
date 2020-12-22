package tech.techActivity.mapper;

import java.util.List;
import tech.techActivity.entity.TechField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 报名表单配置
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
public interface TechFieldMapper extends BaseMapper<TechField> {

	public boolean deleteByMainId(@Param("mainId") String mainId);

	public List<TechField> selectByMainId(@Param("mainId") String mainId);
}
