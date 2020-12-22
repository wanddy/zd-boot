package workflow.business.service;

import workflow.business.model.TaskData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.common.error.WorkFlowException;
import workflow.olddata.model.PageInput;
import workflow.business.model.ActiveTask;
import workflow.business.model.AuditLog;
import workflow.business.model.FinishedTask;
import workflow.business.model.TaskContentData;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface AuditLogService {

    /**
     * 分页查询
     * @param params
     * @param page
     * @return
     */
	Page<AuditLog> pageList(AuditLog params, PageInput page);

    /**
     * 条件查询查询
     * @param params
     * @return
     */
	List<AuditLog> listAll(AuditLog params);

	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param userName    操作人
	 * @param deptName    操作人所属部门
	 * @param opResult    操作结果
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @return
	 */
	int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList);

	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param userName    操作人
	 * @param deptName    操作人所属部门
	 * @param opResult    操作结果
	 * @param memo        审核意见
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @param exValues    扩展参数
	 * @return
	 */
	int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues);
	
	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param userName    操作人
	 * @param deptName    操作人所属部门
	 * @param opResult    操作结果
	 * @param memo        审核意见
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @param exValues    扩展参数
	 * @return
	 */
	int auditTask_forrws(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues);
	
	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param log    审核日志(操作人、操作人所属单位/部门、操作结果、审核意见)
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @param exValues    扩展参数
	 * @return
	 */
	int auditTask(String userId, AuditLog log, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues);
	
	/**
	 * 取回已办任务后
	 * 
	 * @param userId
	 * @param log
	 * @param finishedTask
	 * @param taskList
	 * @param exValues
	 * @return
	 */
	int retrieveTask(String userId, AuditLog log, FinishedTask finishedTask, List<TaskData> taskList, Map<String, String> exValues);

	/**
	 * 指派待办任务后
	 * 1、修改当前任务操作人
	 * 2、保存当前审核日志
	 * @param log    审核日志(操作人、操作人所属单位/部门、操作结果、审核意见)
	 * @param activeTask  当前完成的任务
	 * @param exValues 扩展参数
	 * @return
	 */
	int setTaskAssignee(AuditLog log, ActiveTask activeTask, Map<String, String> exValues);
	
	/**
	 * 通过流程实例id完成所有待办任务
	 * @param userId
	 * @param processInstanceId
	 * @param reason
	 * @return
	 */
	int auditTask(String userId, String processInstanceId, String reason);
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param region  所属区县
	* @param unitIds 部门id
	* @param values      参数<字段名,字段值>
	* @param signUsers   会签用户信息<会签用户信息ID，<会签用户id列表>>
	* @param claimUsers  认领用户信息<认领用户信息ID，<认领用户id列表>>
	* @param claimGroups 认领用户组<认领用户组ID，<认领用户组id列表>>
	* @param userName    操作人
	* @param deptName    操作人所属部门
	* @param opResult    操作结果
	* @param activeTask  当前任务
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List    返回类型 
	* 
	*/
	Boolean complateTask(String userId, String region, List<String> unitIds, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, String userName, String deptName, String opResult, String memo, ActiveTask activeTask) throws WorkFlowException;
	
	/**
	 * @Title: startProcess 
	 * @Description: 启动流程
	 * @param userId
	 * @param processKey
	 * @param businessKey
	 * @param data
	 * @return 流程实例id
	 * @throws WorkFlowException
	 */
	Boolean startProcess(String userId, String processKey, String businessKey, TaskContentData data) throws WorkFlowException;

	/**
	 * 查询业务数据审核最后一次上传材料的日志
	 * @param contentId
	 * @param processInstanceId
	 * @return
	 */
	AuditLog queryLastFile(String contentId, String processInstanceId);

	int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask,
			List<TaskData> taskList, Map<String, String> exValues, String appellation);
}
