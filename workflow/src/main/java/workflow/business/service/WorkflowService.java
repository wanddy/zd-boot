package workflow.business.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import workflow.business.model.*;
import workflow.common.error.WorkFlowException;

/** 
* @ClassName: ActivitiNewService 
* @Description: 新版工作流服务
* @author KaminanGTO
* @date Jul 5, 2019 4:34:41 PM 
*  
*/
public interface WorkflowService {

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
	* @param businessKey
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	ActiveTask startProcessInstanceById(String userId, String processId, Map<String, Object> values, String businessKey, TaskContentData contentData) throws WorkFlowException;
	
	/** 
	* @Title: startProcessInstanceByKey 
	* @Description: 根据流程key启动流程实例
	* @param userId
	* @param processKey
	* @param values
	* @param businessKey
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	ActiveTask startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values, String businessKey, TaskContentData contentData) throws WorkFlowException;
	
	List<TaskData> startProcessInstanceById(String userId, String processId, Map<String, Object> values, String businessKey) throws WorkFlowException;
	List<TaskData> startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values, String businessKey) throws WorkFlowException;
	
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
	* @Title: stopProcessInstance 
	* @Description: 中止流程实例
	* @param userId
	* @param processInstanceId
	* @param reason
	* @param needComplate 是否需要完成任务
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean stopProcessInstance(String userId, String processInstanceId, String reason, boolean needComplate) throws WorkFlowException;
	
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
	
	/** 根据任务ID获取流程任务节点列表
	 * @param taskId
	 * @return
	 * @throws WorkFlowException 
	 */
	List<TaskDefData> getProcessInfoByTask(String taskId) throws WorkFlowException;
	
	/** 根据流程实例ID获取流程任务节点列表
	 * @param processInstanceId
	 * @return
	 * @throws WorkFlowException 
	 */
	List<TaskDefData> getProcessInfoByProcessInstId(String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: getProcessInfoByProcessKey 
	* @Description: 根据流程Key获取流程任务节点列表（只会取最新流程定义的任务）
	* @param processDefinitionKey
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List<TaskDefData>    返回类型 
	* 
	*/
	List<TaskDefData> getProcessInfoByProcessKey(String processDefinitionKey) throws WorkFlowException;
	
	
	/** 根据流程定义ID获取流程任务节点列表
	 * @param processDefinitionId
	 * @return
	 * @throws WorkFlowException 
	 */
	List<TaskDefData> getProcessInfoByDefId(String processDefinitionId) throws WorkFlowException;

	/**
	 * @param complateTaskinfo
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明 参数内容参考complateTask4
	 * @Title: complateTask
	 * @Description: 完成任务
	 */
	boolean complateTask(ComplateTask complateTaskinfo) throws WorkFlowException;

	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	 * @param userId     操作人id           必填
	 * @param userName   操作人部门名称
	 * @param deptName   操作人部门名称
	 * @param opResult   操作结果（yes，no，pass.....）
	 * @param memo       操作备注信息（审核意见）
	 * @param activeTask 当前完成的任务         必填
	 * @param region     区域
	 * @param unitIds    部门id列表
	 * @param taskId     任务id(ActiveTask.taskId)          必填
	 * @param values     自定义参数列表<字段名，字段值>
	 * @param signUsers  会签用户信息<会签用户信息ID
	 * @param claimUsers 认领用户信息<认领用户信息ID，<认领用户id列表>>
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean complateTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, String region, List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;

	/** 
	* @Title: complateTask_forrws 
	* @Description: 完成任务--任务书专用
	* @param userId
	* @param userName
	* @param deptName
	* @param opResult
	* @param memo
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
	boolean complateTask_forrws(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, String region, List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;
	
	
	/** 
	* @Title: complateTask 
	* @Description: 完成任务
	* @param userId
	* @param activeTaskId
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
	boolean complateTask(String userId, String activeTaskId, String region, List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;
	
	List<TaskData> complateTask(String userId, String region, List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: retrieveTask 
	* @Description: 取回任务
	* @param userId
	* @param taskId
	* @param exValues
	* @return  参数说明 
	* @return List    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	List<TaskData> retrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException;
	
	/** 
	* @Title: retrieveTask 
	* @Description: 取回任务
	* @param userId
	* @param activeTaskId
	* @param taskId
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean retrieveTask(String userId, String activeTaskId, String taskId, Map<String, String> exValues) throws WorkFlowException;
	
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
	List<TaskData> jumpTask(String startTaskId, String targetTaskDefId) throws WorkFlowException;
	
	/** 
	* @Title: jumpTask 
	* @Description: 跳转任务
	* @param userId
	* @param startTaskId
	* @param targetTaskDefId
	* @param activeTaskId
	* @param values
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean jumpTask(String userId, String startTaskId, String targetTaskDefId, String activeTaskId, Map<String, String> values, Map<String, String> exValues) throws WorkFlowException;
	
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
	List<TaskData> jumpTaskByProInst(String processInstId, String targetTaskDefId) throws WorkFlowException;
	
	/** 
	* @Title: jumpTaskByProInst 
	* @Description: 跳转指定流程实例的任务
	* @param userId
	* @param processInstId
	* @param targetTaskDefId
	* @param activeTaskId
	* @param values
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean jumpTaskByProInst(String userId, String processInstId, String targetTaskDefId, String activeTaskId, Map<String, String> values, Map<String, String> exValues) throws WorkFlowException;
	
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
	* @Title: setTaskAssignee 
	* @Description: 指派任务
	* @param userId
	* @param taskId
	* @param activeTaskId
	* @param exValues
	* @return
	* @throws WorkFlowException  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean setTaskAssignee(String userId, String taskId, String activeTaskId, Map<String, String> exValues) throws WorkFlowException;

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

	boolean complateTask(String userId, String userName, String deptName, String opResult, String memo,
			ActiveTask activeTask, String region, List<String> unitIds, String taskId, Map<String, String> values,
			Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues, String appellation)
			throws WorkFlowException;
	
}
