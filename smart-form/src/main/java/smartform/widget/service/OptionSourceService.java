package smartform.widget.service;

import smartform.widget.model.OptionSource;
import smartform.widget.model.OptionSourceInput;
import smartform.widget.model.OptionSourcePagination;

import java.util.List;

/**
 * @ClassName: OptionSourceService
 * @Description: 选项源服务实现
 * @author hou
 * @date 2018年9月18日 下午2:24:57
 */
public interface OptionSourceService {
	/**
	 * 根据ID查询一个选项源
	 * 
	 * @param id
	 * @return
	 */
	OptionSource optionSourceById(String id);

	/**
	 * 分页查询选项源
	 * 
	 * @param page
	 * @return
	 */
	OptionSourcePagination optionSourceList(OptionSourceInput page);

	/**
	 * 创建一个选项源
	 * 
	 * @param page
	 * @return
	 */
	String createOptionSource(String source);

	/**
	 * 更新选项源
	 * 
	 * @param source
	 * @return
	 */
	String updateOptionSource(String source);

	/**
	 * 发布选项源
	 * 
	 * @param id
	 * @param state
	 * @return
	 */
	String updateOptionSourceState(String id, Integer state);

	/**
	 * 复制选项源
	 * 
	 * @param id
	 * @return
	 */
	String copyOptionSource(String id);

	/**
	 * 删除选项源
	 * 
	 * @param id
	 * @return
	 */
	String deleteOptionSource(String id);
	
	/**
	 * 批量查询
	 * @param id
	 * @return
	 */
	List<OptionSource> list(String[] id);
}
