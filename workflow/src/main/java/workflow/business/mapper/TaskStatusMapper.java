package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import workflow.business.model.TaskStatus;

import java.util.List;

//import framework.orm.mapper.BaseMapper;
//import framework.entity.Query;
//import framework.entity.Page;

/**
 *
 */
@Mapper
public interface TaskStatusMapper extends BaseMapper<TaskStatus> {
	/**
	 * 调用updateProcessInstToContent存储过程
	 * @param query
	 * @return
	 */
	int updateProcessInstToContent(Query query);

	/**
	 * 新增
	 * @param t
	 * @return
	 */
	int save(TaskStatus t);
	/**
	 * 根据id查询详情
	 * @param id
	 * @return
	 */
	TaskStatus getObjectById(String id);

	/**
	 * 批量删除
	 * @param id
	 * @return
	 */
	int batchRemove(String[] id);

	/**
	 * 分页查询列表
	 * @param page
	 * @param query
	 * @return
	 */
	List<TaskStatus> listForPage(Page<TaskStatus> page, Query query);

	/**
	 * 更新
	 * @param t
	 * @return
	 */
	int update(TaskStatus t);
}
