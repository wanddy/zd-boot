package workflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.olddata.model.PageInput;
import workflow.business.model.ActiveTask;
import workflow.business.model.FinishedTask;

import java.util.List;


/**
 * 
 */
public interface FinishedTaskService {

    /**
     * 分页查询
     * @param params
     * @param page
     * @return
     */
    Page<FinishedTask> pageList(FinishedTask params, PageInput page);

	/**
	 * 根据查询条件查询所有已办任务
	 * @param params 查询条件
	 * @return
	 */
	List<FinishedTask> listAll(FinishedTask params);

	/**
     * 保存已办任务
     * @param userId   操作人
     * @param userName 操作人姓名
     * @param opResult 操作结果
     * @param opResultDesc 操作结果描述
     * @param taskList 需要保存成已办的任务列表
     * @return
     */
	int saveFinishedTask(String userId, String userName, String opResult, String opResultDesc, List<ActiveTask> taskList);

	/**
	 * 根据用户id查询用户实际已完成任务数 
	 * @param userId
	 * @return
	 */
	Integer countTaskByUser(String userId, List<String> businessTypeList);

    /**
     * 根据id查询
     * @param id
     * @return
     */
	FinishedTask getFinishedTaskById(String id);
}
