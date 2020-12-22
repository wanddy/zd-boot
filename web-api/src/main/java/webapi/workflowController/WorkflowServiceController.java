package webapi.workflowController;

import org.springframework.web.bind.annotation.RequestBody;
import workflow.business.model.*;
import workflow.business.service.WorkflowService;
import workflow.common.error.WorkFlowException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @ClassName: ActivitiNewService 
* @Description: 新版工作流服务
* @author KaminanGTO
* @date Jul 5, 2019 4:34:41 PM 
*  
*/
@Component
//@Service("workflowService")
@Controller
@RestController
@RequestMapping(value = "/workflow/workflow")
public class WorkflowServiceController {
	@Autowired
	private WorkflowService workflowService;

	/**
	 * @param name
	 * @param onlyLatestVersion
	 * @param businessType
	 * @param pageNum
	 * @param pageSize
	 * @return PageList<ProcessSampleData>    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: getProcessList
	 * @Description: 获取流程列表
	 */
	@ResponseBody
	@ApiOperation("获取流程列表")
	@RequestMapping(value = "/getProcessList")
	PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, String businessType, int pageNum, int pageSize) throws WorkFlowException {
		return workflowService.getProcessList(name, onlyLatestVersion, businessType, pageNum, pageSize);
	}

	/**
	 * @param userId
	 * @param processId
	 * @param values
	 * @param businessKey
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: startProcessInstanceById
	 * @Description: 根据流程ID启动流程实例
	 */
	@ResponseBody
	@ApiOperation("根据流程ID启动流程实例")
	@RequestMapping(value = "/startProcessInstanceById")
	ActiveTask startProcessInstanceById(String userId, String processId, Map<String, Object> values, String businessKey, TaskContentData contentData) throws WorkFlowException {
		return workflowService.startProcessInstanceById(userId, processId, values, businessKey, contentData);
	}
	@ResponseBody
	@ApiOperation("根据流程ID启动流程实例1")
	@RequestMapping(value = "/startProcessInstanceById1")
	List<TaskData> startProcessInstanceById(String userId, String processId, Map<String, Object> values, String businessKey) throws WorkFlowException {
		return workflowService.startProcessInstanceById(userId, processId, values, businessKey);
	}
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
	@ResponseBody
	@ApiOperation("根据流程key启动流程实例")
	@RequestMapping(value = "/startProcessInstanceByKey")
	ActiveTask startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values, String businessKey, TaskContentData contentData) throws WorkFlowException{
		return workflowService.startProcessInstanceByKey(userId, processKey, values, businessKey,contentData);
	}

	@ResponseBody
	@ApiOperation("根据流程key启动流程实例")
	@RequestMapping(value = "/startProcessInstanceByKey1")
	List<TaskData> startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values, String businessKey) throws WorkFlowException {
		return workflowService.startProcessInstanceByKey(userId, processKey, values, businessKey);
	}

	/**
	 * @param taskId
	 * @return TaskData    返回类型
	 * @throws WorkFlowException
	 * @Title: getTaskInfo
	 * @Description: 获取流程任务详情
	 */
	@ResponseBody
	@ApiOperation("获取流程任务详情")
	@RequestMapping(value = "/getTaskInfo")
	TaskData getTaskInfo(String taskId) throws WorkFlowException {
		return workflowService.getTaskInfo(taskId);
	}

	/**
	 * @param userId
	 * @param processInstanceId
	 * @param reason
	 * @return boolean    返回类型
	 * @throws WorkFlowException
	 * @Title: stopProcessInstance
	 * @Description: 中止流程实例
	 */
	@ResponseBody
	@ApiOperation("中止流程实例")
	@RequestMapping(value = "/stopProcessInstance")
	boolean stopProcessInstance(String userId, String processInstanceId, String reason) throws WorkFlowException {
		return workflowService.stopProcessInstance(userId, processInstanceId, reason);
	}

	/**
	 * @param userId
	 * @param processInstanceId
	 * @param reason
	 * @param needComplate      是否需要完成任务
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: stopProcessInstance
	 * @Description: 中止流程实例
	 */
	@ResponseBody
	@ApiOperation("中止流程实例")
	@RequestMapping(value = "/stopProcessInstance1")
	boolean stopProcessInstance(String userId, String processInstanceId, String reason, boolean needComplate) throws WorkFlowException {
		return workflowService.stopProcessInstance(userId, processInstanceId, reason, needComplate);
	}

	/**
	 * @param userId
	 * @param processInstanceId
	 * @return boolean    返回类型
	 * @throws WorkFlowException
	 * @Title: suspendProcessInstance
	 * @Description: 暂停流程实例
	 */
	@ResponseBody
	@ApiOperation("暂停流程实例")
	@RequestMapping(value = "/suspendProcessInstance")
	boolean suspendProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
		return workflowService.suspendProcessInstance(userId, processInstanceId);
	}

	/**
	 * @param userId
	 * @param processInstanceId
	 * @return boolean    返回类型
	 * @throws WorkFlowException
	 * @Title: activateProcessInstance
	 * @Description: 激活流程实例
	 */
	@ResponseBody
	@ApiOperation("激活流程实例")
	@RequestMapping(value = "/activateProcessInstance")
	boolean activateProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
		return workflowService.activateProcessInstance(userId, processInstanceId);
	}

	/** 根据任务ID获取流程任务节点列表
	 * @param taskId
	 * @return
	 * @throws WorkFlowException 
	 */
	@ResponseBody
	@ApiOperation("根据任务ID获取流程任务节点列表")
	@RequestMapping(value = "/getProcessInfoByTask")
	List<TaskDefData> getProcessInfoByTask(String taskId) throws WorkFlowException{
		return workflowService.getProcessInfoByTask(taskId);
	}
	
	/** 根据流程实例ID获取流程任务节点列表
	 * @param processInstanceId
	 * @return
	 * @throws WorkFlowException 
	 */
	@ResponseBody
	@ApiOperation("根据流程实例ID获取流程任务节点列表")
	@RequestMapping(value = "/getProcessInfoByProcessInstId")
	List<TaskDefData> getProcessInfoByProcessInstId(String processInstanceId) throws WorkFlowException{
		return workflowService.getProcessInfoByProcessInstId(processInstanceId);
	}

	/**
	 * @param processDefinitionKey
	 * @return List<TaskDefData>    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: getProcessInfoByProcessKey
	 * @Description: 根据流程Key获取流程任务节点列表（只会取最新流程定义的任务）
	 */
	@ResponseBody
	@ApiOperation("根据流程Key获取流程任务节点列表（只会取最新流程定义的任务）")
	@RequestMapping(value = "/getProcessInfoByProcessKey")
	List<TaskDefData> getProcessInfoByProcessKey(String processDefinitionKey) throws WorkFlowException {
		return workflowService.getProcessInfoByProcessKey(processDefinitionKey);
	}


	/**
	 * 根据流程定义ID获取流程任务节点列表
	 *
	 * @param processDefinitionId
	 * @return
	 * @throws WorkFlowException
	 */
	@ResponseBody
	@ApiOperation("根据流程定义ID获取流程任务节点列表")
	@RequestMapping(value = "/getProcessInfoByDefId")
	List<TaskDefData> getProcessInfoByDefId(String processDefinitionId) throws WorkFlowException {
		return workflowService.getProcessInfoByDefId(processDefinitionId);
	}

	/**
	 * @param complateTaskinfo
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明 参数内容参考complateTask4
	 * @Title: complateTask
	 * @Description: 完成任务
	 */
	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTasknew")
	boolean complateTasknew(@RequestBody ComplateTask complateTaskinfo) throws WorkFlowException {
		return workflowService.complateTask(complateTaskinfo);
	}

	/**
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
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: complateTask
	 * @Description: 完成任务
	 */
	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTask")
	boolean complateTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, String region,
						 List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers,
						 Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.complateTask(userId, userName, deptName, opResult, memo, activeTask, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}

	/**
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
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: complateTask_forrws
	 * @Description: 完成任务--任务书专用
	 */
	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTask_forrws")
	boolean complateTask_forrws(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask,
								String region, List<String> unitIds, String taskId, Map<String, String> values,
								Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
								Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.complateTask_forrws(userId, userName, deptName, opResult, memo, activeTask, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}


	/**
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
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: complateTask
	 * @Description: 完成任务
	 */
	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTask1")
	boolean complateTask(String userId, String activeTaskId, String region, List<String> unitIds, String taskId, Map<String, String> values,
						 Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.complateTask(userId, activeTaskId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}

	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTask2")
	List<TaskData> complateTask(String userId, String region, List<String> unitIds, String taskId, Map<String, String> values,
								Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.complateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}

	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTask3")
	boolean complateTask(String userId, String userName, String deptName, String opResult, String memo,
						 ActiveTask activeTask, String region, List<String> unitIds, String taskId, Map<String, String> values,
						 Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
						 Map<String, List<String>> claimGroups, Map<String, String> exValues, String appellation)throws WorkFlowException {
		return workflowService.complateTask(userId, userName, deptName, opResult, memo, activeTask, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues, appellation);
	}

	/**
	 * @param userId
	 * @param taskId
	 * @param exValues
	 * @return List    返回类型
	 * @throws WorkFlowException
	 * @Title: retrieveTask
	 * @Description: 取回任务
	 */
	@ResponseBody
	@ApiOperation("取回任务")
	@RequestMapping(value = "/retrieveTask")
	List<TaskData> retrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.retrieveTask(userId, taskId, exValues);
	}

	/**
	 * @param userId
	 * @param activeTaskId
	 * @param taskId
	 * @param exValues
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: retrieveTask
	 * @Description: 取回任务
	 */
	@ResponseBody
	@ApiOperation("取回任务")
	@RequestMapping(value = "/retrieveTask1")
	boolean retrieveTask(String userId, String activeTaskId, String taskId, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.retrieveTask(userId, activeTaskId, taskId, exValues);
	}

	/**
	 * @param startTaskId
	 * @param targetTaskDefId
	 * @return boolean    返回类型
	 * @throws WorkFlowException
	 * @Title: jumpTask
	 * @Description: 跳转任务
	 */
	@ResponseBody
	@ApiOperation("跳转任务")
	@RequestMapping(value = "/jumpTask")
	List<TaskData> jumpTask(String startTaskId, String targetTaskDefId) throws WorkFlowException {
		return workflowService.jumpTask(startTaskId, targetTaskDefId);
	}

	/**
	 * @param userId
	 * @param startTaskId
	 * @param targetTaskDefId
	 * @param activeTaskId
	 * @param values
	 * @param exValues
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: jumpTask
	 * @Description: 跳转任务
	 */
	@ResponseBody
	@ApiOperation("跳转任务")
	@RequestMapping(value = "/jumpTask1")
	boolean jumpTask(String userId, String startTaskId, String targetTaskDefId, String activeTaskId, Map<String, String> values, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.jumpTask(userId, startTaskId, targetTaskDefId, activeTaskId, values, exValues);
	}

	/**
	 * @param processInstId
	 * @param targetTaskDefId
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: jumpTaskByProInst
	 * @Description: 跳转指定流程实例的任务
	 */
	@ResponseBody
	@ApiOperation("跳转指定流程实例的任务")
	@RequestMapping(value = "/jumpTaskByProInst")
	List<TaskData> jumpTaskByProInst(String processInstId, String targetTaskDefId) throws WorkFlowException {
		return workflowService.jumpTaskByProInst(processInstId, targetTaskDefId);
	}

	/**
	 * @param userId
	 * @param processInstId
	 * @param targetTaskDefId
	 * @param activeTaskId
	 * @param values
	 * @param exValues
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: jumpTaskByProInst
	 * @Description: 跳转指定流程实例的任务
	 */
	@ResponseBody
	@ApiOperation("跳转指定流程实例的任务")
	@RequestMapping(value = "/jumpTaskByProInst1")
	boolean jumpTaskByProInst(String userId, String processInstId, String targetTaskDefId, String activeTaskId, Map<String, String> values, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.jumpTaskByProInst(userId, processInstId, targetTaskDefId, activeTaskId, values, exValues);
	}

	/**
	 * @param userId
	 * @param taskId
	 * @return boolean    返回类型
	 * @throws WorkFlowException
	 * @Title: setTaskAssignee
	 * @Description: 指派任务
	 */
	@ResponseBody
	@ApiOperation("指派任务")
	@RequestMapping(value = "/setTaskAssignee")
	boolean setTaskAssignee(String userId, String taskId) throws WorkFlowException {
		return workflowService.setTaskAssignee(userId, taskId);
	}

	/**
	 * @param userId
	 * @param taskId
	 * @param activeTaskId
	 * @param exValues
	 * @return boolean    返回类型
	 * @throws WorkFlowException 参数说明
	 * @Title: setTaskAssignee
	 * @Description: 指派任务
	 */
	@ResponseBody
	@ApiOperation("指派任务")
	@RequestMapping(value = "/setTaskAssignee1")
	boolean setTaskAssignee(String userId, String taskId, String activeTaskId, Map<String, String> exValues) throws WorkFlowException {
		return workflowService.setTaskAssignee(userId, taskId, activeTaskId, exValues);
	}

	/**
	 * @param processId
	 * @return String    返回类型
	 * @throws WorkFlowException
	 * @throws IOException
	 * @Title: getProcessPreview
	 * @Description: 根据流程定义获取流程图，返回base64加密png图片数据
	 */
	@ResponseBody
	@ApiOperation("根据流程定义获取流程图，返回base64加密png图片数据")
	@RequestMapping(value = "/getProcessPreview")
	String getProcessPreview(String processId) throws WorkFlowException, IOException {
		return workflowService.getProcessPreview(processId);
	}

	/**
	 * @param processInstanceId
	 * @return ProcessInstData    返回类型
	 * @throws WorkFlowException
	 * @Title: getProcessInstData
	 * @Description: 获取流程实例详情
	 */
	@ResponseBody
	@ApiOperation("获取流程实例详情")
	@RequestMapping(value = "/getProcessInstData")
	ProcessInstData getProcessInstData(String processInstanceId) throws WorkFlowException {
		return workflowService.getProcessInstData(processInstanceId);
	}


}
