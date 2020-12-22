package smartform.widget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import smartform.widget.model.OptionSource;

import java.util.List;

/**
 * 表单选项源
 */
@Mapper
public interface OptionSourceMapper extends BaseMapper<OptionSource> {
	/**
	 * 批量查询
	 * @param id
	 * @return
	 */
	List<OptionSource> list(String[] id);

	/**
	 * 批量删除
	 * @param id
	 * @return
	 */
	int batchRemove(Object[] id);

	/**
	 * 根据id查询详情
	 * @param id
	 * @return
	 */
	OptionSource getObjectById(Object id);
}
