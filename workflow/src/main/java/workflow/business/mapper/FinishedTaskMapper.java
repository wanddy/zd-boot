package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.FinishedTask;

import java.util.List;

//import framework.orm.mapper.BaseMapper;
//import framework.entity.Page;
//import framework.entity.Query;

/**
 *
 */
public interface FinishedTaskMapper extends BaseMapper<FinishedTask> {
	/**
	 * 条件查询
	 * @param query
	 * @return
	 */
	List<FinishedTask> listAll(Query query);

	/**
	 * 分页查询
	 * @param page
	 * @param query
	 * @return
	 */
	List<FinishedTask> listAll(Page<FinishedTask> page, Query query);

	/**
	 * 查询用户已完成任务数
	 * @param query
	 * @return
	 */
	Integer countTaskByUser(Query query);
	/**
	 * 根据id查询详情
	 * @param id
	 * @return
	 */
	FinishedTask getObjectById(String id);

	/**
	 * 批量删除
	 * @param id
	 * @return
	 */
	int batchRemove(String[] id);
	/**
	 * 批量新增
	 * @param items
	 * @return
	 */
	int batchSave(List<FinishedTask> items);
	/**
	 * 新增
	 * @param t
	 * @return
	 */
	int save(FinishedTask t);
}
