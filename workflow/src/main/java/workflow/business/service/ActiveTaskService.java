package workflow.business.service;

import workflow.business.model.TaskData;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.olddata.model.PageInput;
import workflow.business.model.ActiveTask;
import workflow.business.model.TaskContentData;
import workflow.business.model.TotalTasks;

import java.util.List;


/**
 * 
 */
public interface ActiveTaskService {

    /**
     * 分页查询
     * @param params
     * @param page
     * @return
     */
    Page<ActiveTask> pageList(ActiveTask params, PageInput page);

	/**
     * 根据查询条件查询所有待办任务
     * @param params 查询条件
     * @return
     */
	List<ActiveTask> listAll(ActiveTask params);
	
    /**
     * 根据id查询
     * @param id
     * @return
     */
	ActiveTask getActiveTaskById(String id);

    /**
     * 保存待办任务
     * @param processName 流程名称
     * @param initiator 发起人
     * @param previousOpResultDesc 上一个任务节点的操作结果
     * @param list 工作流返回的待办任务信息
     * @param data 待办任务其他信息
     * @return
     */
	int batchSave(String processName, String initiator, String previousOpResultDesc, List<TaskData> list, TaskContentData data);

	 /**
     * 保存待办任务
     * @param processName 流程名称
     * @param initiator 发起人
     * @param previousOpResultDesc 上一个任务节点的操作结果
     * @param list 工作流返回的待办任务信息
     * @param data 待办任务其他信息
     * @return
     */
	int batchSave(String processName, String initiator, String previousOpResultDesc, List<TaskData> list, TaskContentData data, String task_batch);

    /**
     * 保存待办任务
     * @param processName 流程名称
     * @param initiator 发起人
     * @param previousOpResultDesc 上一个任务节点的操作结果
     * @param taskData 工作流返回的待办任务信息
     * @param data 待办任务其他信息
     * @return
     */
	int batchSave(String processName, String initiator, String previousOpResultDesc, TaskData taskData, TaskContentData data);

	/**
	 * 根据任务id查询待办任务列表
	 * @param taskId
	 * @param exceptActiveTaskId 有值时表示查询除此id外的taskId列表 
	 * @return
	 */
	List<ActiveTask> listActiveTask(String taskId, String exceptActiveTaskId);

	/**
	 * 根据业务数据id查询待办任务列表
	 * @param contentId
	 * @return
	 */
	List<ActiveTask> listActiveTaskByContentId(String contentId);
	
	/**
	 * 根据业务数据id查询待办任务列表
	 * @param taskbatch
	 * @return
	 */
	List<ActiveTask> listActiveTaskBytaskbatch(String taskbatch);
	/**
	 * 删除用户userId的某个待办任务
	 * 删除taskId的所有任务
	 * @param userId
	 * @param taskId
	 * @return
	 */
	int delete(String userId, String taskId);
	
	/**
	 * 根据id删除待办任务
	 * @param id
	 * @return
	 */
	int deleteById(String id);
	
	/**
	 * 将工作流任务对象转换成待办任务对象
	 * @param taskData
	 * @return
	 */
	ActiveTask taskDataToActiveTask(TaskData taskData);
	
	/**
     * 获取待办任务总览
     * @param query  查询条件
     * @return
     */
    List<TotalTasks> getTotalTasks(Query query);
    
    /**
     * 进行暂停、停止、恢复操作时，更新对应状态
     * @param processDefinitionId  流程定义id
     * @param processInstanceId   流程实例id
     * @param processTaskId       流程任务id
     * @param status 状态 0正常1暂停2终止
     * @return
     */
    int updateStatus(String processDefinitionId, String processInstanceId, String processTaskId, int status);

    /**
     * 更新数据
     * @param item
     * @return
     */
    int updateActiveTask(ActiveTask item);

	int batchSave(String processName, String initiator, String previousOpResultDesc, TaskData taskData,
			TaskContentData data, String task_batch);
}
