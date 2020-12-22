package workflow.business.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import workflow.business.model.PageList;
import workflow.business.model.ProcessInstData;
import workflow.business.model.ProcessSampleData;
import workflow.business.model.TaskData;
import workflow.business.model.TaskDefData;
import workflow.business.model.TaskSampleData;
import workflow.business.model.TasksInfo;
import workflow.common.constant.ActivitiConstant.EOrderType;
import workflow.common.error.WorkFlowException;

/** 
* @ClassName: ActivitiService 
* @Description: 工作流服务
* @author KaminanGTO
* @date 2018年9月11日 下午12:57:08 
*  
*/
public interface ActivitiService {

	/** 
	* @Title: getProcessList 
	* @Description: 获取流程列表
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageList<ProcessSampleData>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getProcessList 
	* @Description: 获取流程列表
	* @param name
	* @param onlyLatestVersion
	* @param businessType
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<ProcessSampleData>    返回类型 
	* 
	*/
	PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, String businessType, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: startProcessInstanceById 
	* @Description: 根据流程ID启动流程实例
	* @param userId
	* @param processId
	* @param values
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	TaskData startProcessInstanceById(String userId, String processId, Map<String, Object> values) throws WorkFlowException;

	/** 
	* @Title: startProcessInstanceById 
	* @Description: 根据流程ID启动流程实例
	* @param userId
	* @param processId
	* @param values
	* @param businessKey
	* @return
	* @throws WorkFlowException  参数说明 
	* @return TaskData    返回类型 
	* 
	*/
	TaskData startProcessInstanceById(String userId, String processId, Map<String, Object> values, String businessKey) throws WorkFlowException;
	
	/** 
	* @Title: startProcessInstanceByKey 
	* @Description: 根据流程key启动流程实例
	* @param userId
	* @param processKey
	* @param values
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	TaskData startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values) throws WorkFlowException;
	
	/** 
	* @Title: startProcessInstanceByKey 
	* @Description: 根据流程key启动流程实例
	* @param userId
	* @param processKey
	* @param values
	* @param businessKey
	* @return
	* @throws WorkFlowException  参数说明 
	* @return TaskData    返回类型 
	* 
	*/
	TaskData startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values, String businessKey) throws WorkFlowException;
	
	/** 
	* @Title: stopProcessInstance 
	* @Description: 中止流程实例
	* @param userId
	* @param processInstanceId
	* @param reason
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean stopProcessInstance(String userId, String processInstanceId, String reason) throws WorkFlowException;
	
	/** 
	* @Title: suspendProcessInstance 
	* @Description: 暂停流程实例
	* @param userId
	* @param processInstanceId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean suspendProcessInstance(String userId, String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: activateProcessInstance 
	* @Description: 激活流程实例
	* @param userId
	* @param processInstanceId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean activateProcessInstance(String userId, String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getTasksByUser 
	* @Description: 获取当前用户可操作任务列表
	* @param userId
	* @param processId
	* @param taskName
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageList<TaskSampleData> getTasksByUser(String userId, String processId, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;

	/** 
	* @Title: getTasksByUser 
	* @Description: 获取当前用户可操作任务列表
	* @param userId
	* @param region
	* @param unitId
	* @param jobIdList
	* @param processId
	* @param taskName
	* @param taskDefId
	* @param showClaim
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	* 
	*/
	PageList<TaskSampleData> getTasksByUser(String userId, String region, String unitId, List<String> jobIdList, String processId, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;

	/** 
	* @Title: getTasksByUser 
	* @Description: 获取当前用户可操作任务列表
	* @param userId
	* @param region
	* @param unitId
	* @param jobIdList
	* @param processId
	* @param processBusinessType
	* @param taskName
	* @param taskDefId
	* @param showClaim
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	* 
	*/
	PageList<TaskSampleData> getTasksByUser(String userId, String region, String unitId, List<String> jobIdList, String processId, String processBusinessType, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getTasksByUser 
	* @Description: 获取当前用户可操作任务列表
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param processId
	* @param processBusinessType
	* @param taskName
	* @param taskDefId
	* @param showClaim
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	* 
	*/
	PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processBusinessType, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getTasksByUser 
	* @Description: 获取当前用户可操作任务列表
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param processId
	* @param processKey
	* @param processBusinessType
	* @param processBusinessKeyList
	* @param taskName
	* @param taskDefId
	* @param showClaim
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	* 
	*/
	PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey, String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getTasksByUser 
	* @Description: 获取当前用户可操作任务列表 
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param processId
	* @param processKey
	* @param processBusinessType
	* @param processBusinessKeyList
	* @param taskName
	* @param taskDefId
	* @param showClaim
	* @param startTime
	* @param endTime
	* @param hasBusinessKey
	* @param orderByCreate
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	* 
	*/
	PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey, String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, boolean hasBusinessKey, EOrderType orderByCreate, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getProcessInstIdsByUser 
	* @Description: 获取用户代办流程实例id列表
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param processId
	* @param processKey
	* @param processBusinessType
	* @param processBusinessKeyList
	* @param taskName
	* @param taskDefId
	* @param showClaim
	* @param startTime
	* @param endTime
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List<String>    返回类型 
	* 
	*/
	List<String> getProcessInstIdsByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey, String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime) throws WorkFlowException;
	
	/** 
	* @Title: getFinishedTasksByUser 
	* @Description: 获取当前用户已完成任务列表
	* @param userId
	* @param processId
	* @param taskName
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageList<TaskSampleData> getFinishedTasksByUser(String userId, String processId, String taskName, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;

	/** 
	* @Title: getFinishedTasksByUser 
	* @Description: 获取当前用户已完成任务列表
	* @param userId
	* @param processId
	* @param processKey
	* @param taskName
	* @param startTime
	* @param endTime
	* @param pageNum
	* @param pageSize
	* @return
	* @throws WorkFlowException  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	* 
	*/
	PageList<TaskSampleData> getFinishedTasksByUser(String userId, String processId, String processKey, String taskName, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getFinishedTasks 
	* @Description: 获取已完成任务列表（无分页，只返回简单数据，按流程实例区分）
	* @param userId
	* @param processId
	* @param processKey
	* @param taskName
	* @param startTime
	* @param endTime
	* @return
	* @throws WorkFlowException  参数说明 
	* @return Set<String>    返回类型 
	* 
	*/
	Map<String, List<TaskSampleData>> getFinishedTasks(String userId, String processId, String processKey, String taskName, Date startTime, Date endTime) throws WorkFlowException;
	
	/** 
	* @Title: getClaimTasksByUser 
	* @Description: 获取当前用户可候选流程列表
	* @param userId
	* @param processId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageList<TaskSampleData> getClaimTasksByUser(String userId, String processId, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getClaimTasksByUnit 
	* @Description: 获取当前角色可候选流程列表
	* @param unitId
	* @param processId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageList<TaskSampleData> getClaimTasksByUnit(String unitId, String processId, int pageNum, int pageSize) throws WorkFlowException;
	
	/** 
	* @Title: getTaskInfo 
	* @Description: 获取流程任务详情
	* @param taskId
	* @return  参数说明 
	* @return TaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	TaskData getTaskInfo(String taskId) throws WorkFlowException;
	
	/** 
	* @Title: getUserTaskInfoByInstance 
	* @Description: 根据流程实例ID获取用户当前进行中的任务详情
	* @param userId
	* @param processInstanceId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return TaskData    返回类型 
	* 
	*/
	TaskData getUserTaskInfoByInstance(String userId, String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getUserTaskInfoByInstance 
	* @Description: 根据流程实例ID获取用户当前进行中的任务详情
	* @param userId
	* @param region
	* @param unitId
	* @param jobIdList
	* @param processInstanceId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return TaskData    返回类型 
	* 
	*/
	TaskData getUserTaskInfoByInstance(String userId, String region, String unitId, List<String> jobIdList, String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getUserTaskInfoByInstance 
	* @Description: 根据流程实例ID获取用户当前进行中的任务详情 
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param processInstanceId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return TaskData    返回类型 
	* 
	*/
	TaskData getUserTaskInfoByInstance(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processInstanceId) throws WorkFlowException;

	/** 
	* @Title: getFinishedTaskInfo 
	* @Description: 获取已完成流程任务详情
	* @param taskId
	* @return  参数说明 
	* @return TaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	TaskData getFinishedTaskInfo(String taskId) throws WorkFlowException;
	
	/** 根据任务ID获取流程任务节点列表
	 * @param taskId
	 * @return
	 * @throws WorkFlowException 
	 */
	List<TaskDefData> getProcessInfoByTask(String taskId) throws WorkFlowException;
	
	/** 根据流程定义ID获取流程任务节点列表
	 * @param processDefinitionId
	 * @return
	 * @throws WorkFlowException 
	 */
	List<TaskDefData> getProcessInfoByDefId(String processDefinitionId) throws WorkFlowException;
	
	/** 
	* @Title: updateTaskValues 
	* @Description: 更新当前流程变量
	* @param userId
	* @param taskId
	* @param values
	* @param signUsers
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean updateTaskValues(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers) throws WorkFlowException;
	
	/** 
	* @Title: updateTaskValues 
	* @Description: 更新当前流程变量
	* @param userId
	* @param taskId
	* @param values
	* @param signUsers
	* @param claimUsers
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean updateTaskValues(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues) throws WorkFlowException;
	
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param userId
	* @param taskId
	* @param values
	* @param signUsers
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean complateTask(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers) throws WorkFlowException;
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param userId
	* @param taskId
	* @param values
	* @param signUsers
	* @param claimUsers
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean complateTask(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param userId
	* @param region
	* @param unitId
	* @param taskId
	* @param values
	* @param signUsers
	* @param claimUsers
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean complateTask(String userId, String region, String unitId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param userId
	* @param region
	* @param unitId
	* @param taskId
	* @param values
	* @param signUsers
	* @param claimUsers
	* @param claimGroups
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean complateTask(String userId, String region, String unitId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param userId
	* @param region
	* @param unitIds
	* @param taskId
	* @param values
	* @param signUsers
	* @param claimUsers
	* @param claimGroups
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean complateTask(String userId, String region, List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: complateTasks 
	* @Description: 批量完成任务
	* @param userId
	* @param region
	* @param unitIds
	* @param taskIdList
	* @param values
	* @param signUsers
	* @param claimUsers
	* @param claimGroups
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean complateTasks(String userId, String region, List<String> unitIds, List<String> taskIdList, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: claimTask 
	* @Description: 候选任务
	* @param userId
	* @param taskId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean claimTask(String userId, String taskId) throws WorkFlowException;
	
	/** 
	* @Title: retrieveTask 
	* @Description: 取回任务
	* @param userId
	* @param taskId
	* @param exValues
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean retrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: jumpTask 
	* @Description: 跳转任务
	* @param startTaskId
	* @param targetTaskDefId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean jumpTask(String startTaskId, String targetTaskDefId) throws WorkFlowException;
	
	/** 
	* @Title: jumpTaskByProInst 
	* @Description: 跳转指定流程实例的任务
	* @param processInstId
	* @param targetTaskDefId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean jumpTaskByProInst(String processInstId, String targetTaskDefId) throws WorkFlowException;
	
	/** 
	* @Title: setTaskAssignee 
	* @Description: 指派任务
	* @param userId
	* @param taskId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean setTaskAssignee(String userId, String taskId) throws WorkFlowException;

	/** 
	* @Title: getTasksByProcessInstanceID 
	* @Description: 获取流程实例的任务列表
	* @param processInstanceId
	* @return  参数说明 
	* @return PageList<TaskSampleData>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	List<TaskSampleData> getTasksByProcessInstanceID(String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getActiveTasksByProcessInstanceID 
	* @Description: 获取流程实例的已激活任务列表
	* @param processInstanceId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List<TaskSampleData>    返回类型 
	* 
	*/
	List<TaskSampleData> getActiveTasksByProcessInstanceID(String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getTaskDatasByProcessInstanceID 
	* @Description: 获取流程实例的任务详情列表
	* @param processInstanceId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List<TaskData>    返回类型 
	* 
	*/
	List<TaskData> getTaskDatasByProcessInstanceID(String processInstanceId) throws WorkFlowException;

	/** 
	* @Title: getTasksInfoByUser 
	* @Description: 获取用户的所有代办列表，返回值按任务类型分类
	* @param userId
	* @param showClaim
	* @return
	* @throws WorkFlowException  参数说明 
	* @return Map<String,Map<String,TasksInfo>>    返回类型 
	* 
	*/
	Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, boolean showClaim) throws WorkFlowException;
	
	/** 
	* @Title: getTasksInfoByUser
	* @Description: 获取用户的所有代办列表，返回值按任务类型分类
	* @param userId
	* @param region
	* @param unitId
	* @param jobIdList
	* @param showClaim
	* @return
	* @throws WorkFlowException  参数说明 
	* @return Map<String,Map<String,TasksInfo>>    返回类型 
	* 
	*/
	Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, String unitId, List<String> jobIdList, boolean showClaim) throws WorkFlowException;
	
	/** 
	* @Title: getTasksInfoByUser 
	* @Description: 获取用户的所有代办列表，返回值按任务类型分类
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param showClaim
	* @return
	* @throws WorkFlowException  参数说明 
	* @return Map<String,Map<String,TasksInfo>>    返回类型 
	* 
	*/
	Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, boolean showClaim) throws WorkFlowException;
	
	/** 
	* @Title: getTasksInfoByUser 
	* @Description: 获取用户的所有代办列表，返回值按任务类型分类
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param businessTypeList
	* @param showClaim
	* @return
	* @throws WorkFlowException  参数说明 
	* @return Map<String,Map<String,TasksInfo>>    返回类型 
	* 
	*/
	Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, List<String> businessTypeList, boolean showClaim) throws WorkFlowException;
	
	/** 
	* @Title: getTasksInfoByUser 
	* @Description: 获取用户的所有代办列表，返回值按任务类型分类
	* @param userId
	* @param region
	* @param unitIds
	* @param adminUnitIds
	* @param jobIdList
	* @param businessTypeList
	* @param processBusinessKeyList
	* @param showClaim
	* @return
	* @throws WorkFlowException  参数说明 
	* @return Map<String,Map<String,TasksInfo>>    返回类型 
	* 
	*/
	Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, List<String> businessTypeList, List<String> processBusinessKeyList, boolean showClaim) throws WorkFlowException;
	
	/** 
	* @Title: getProcessPreview 
	* @Description: 根据流程定义获取流程图，返回base64加密png图片数据
	* @param processId
	* @return  参数说明 
	* @return String    返回类型 
	 * @throws WorkFlowException 
	 * @throws IOException 
	* 
	*/
	String getProcessPreview(String processId) throws WorkFlowException, IOException;
	
	/** 
	* @Title: getProcessInstData 
	* @Description: 获取流程实例详情
	* @param processInstanceId
	* @return  参数说明 
	* @return ProcessInstData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	ProcessInstData getProcessInstData(String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getProcessTasks 
	* @Description: 获取流程实例所有任务，按任务开始时间排序
	* @param processInstanceId
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List<TaskSampleData>    返回类型 
	* 
	*/
	List<TaskSampleData> getProcessTasks(String processInstanceId) throws WorkFlowException;
	
}
