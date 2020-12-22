package smartform.widget.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smartform.widget.model.Option;

/**
 * 选项列表
 */
@Mapper
public interface OptionMapper extends BaseMapper<Option> {

}
