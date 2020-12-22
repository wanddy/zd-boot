package webapi.workflowController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import workflow.business.service.ConsoleService;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.ProcessDefinition;

import workflow.common.error.WorkFlowException;
import workflow.business.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @ClassName: ConsoleService 
* @Description: 控制台接口
* @author KaminanGTO
* @date 2018年11月7日 下午3:09:36 
*  
*/
//@Service("consoleService")
@Controller
@RestController
@RequestMapping(value = "/workflow/console")
public class ConsoleServiceController {
	@Autowired
	private ConsoleService consoleService;
	/**
	* @Title: getURProcessList 
	* @Description: 获取未发布主流程列表
	* @param key
	* @param name
	* @param showVersion
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageUnReleaseProcessList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取未发布主流程列表")
	@RequestMapping(value = "/getURProcessList")
	PageUnReleaseProcessList getURProcessList(String key, String name, int showVersion, int pageNum, int pageSize, int orderByCreateTime, int orderByUpdateTime){
		return consoleService.getURProcessList(key, name, showVersion, pageNum, pageSize, orderByCreateTime, orderByUpdateTime);
	}

	/**
	* @Title: getURProcess 
	* @Description: 根据流程ID获取未发布流程详情
	* @param id
	* @return  参数说明 
	* @return UnReleaseProcessData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("根据流程ID获取未发布流程详情")
	@RequestMapping(value = "/getURProcess")
	UnReleaseProcessData getURProcess(String id) throws WorkFlowException{
		return consoleService.getURProcess(id);
	}
	
	/**
	 * @Title: getURProcess
	 * @Description: 根据流程KEY获取未发布流程详情
	 * @param key
	 * @return  参数说明
	 * @return UnReleaseProcessData    返回类型
	 * @throws WorkFlowException
	 *
	 */
	@ResponseBody
	@ApiOperation("根据流程KEY获取未发布流程详情")
	@RequestMapping(value = "/getURProcessByKey")
	UnReleaseProcessData getURProcessByKey(String key) throws WorkFlowException{
		return consoleService.getURProcessByKey(key);
	}
	
	/**
	* @Title: getURSubProcessListByParent 
	* @Description: 获取主流程的子流程列表
	* @param parentId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageSubProcessInfoList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取主流程的子流程列表")
	@RequestMapping(value = "/getURSubProcessListByParent")
	PageSubProcessInfoList getURSubProcessListByParent(String parentId, int pageNum, int pageSize) throws WorkFlowException{
		return consoleService.getURSubProcessListByParent(parentId, pageNum, pageSize);
	}
	
	/**
	* @Title: getURSubProcessList 
	* @Description: 获取未发布子流程列表
	* @param key
	* @param name
	* @param showVersion
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageUnReleaseSubProcessList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取未发布子流程列表")
	@RequestMapping(value = "/getURSubProcessList")
	PageUnReleaseSubProcessList getURSubProcessList(String key, String name, int showVersion, int pageNum, int pageSize, int orderByCreateTime, int orderByUpdateTime){
		return consoleService.getURSubProcessList(key, name, showVersion, pageNum, pageSize, orderByCreateTime, orderByUpdateTime);
	}
	
	/**
	* @Title: getURSubProcess 
	* @Description: 根据流程ID获取未发布子流程详情
	* @param id
	* @return  参数说明 
	* @return UnReleaseSubProcessData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取未发布子流程列表")
	@RequestMapping(value = "/getURSubProcess")
	UnReleaseSubProcessData getURSubProcess(String id) throws WorkFlowException{
		return consoleService.getURSubProcess(id);
	}
	
	/**
	* @Title: getProcessDefList 
	* @Description: 获取已发布主流程列表 
	* @param key
	* @param name
	* @param state
	* @param onlyLatestVersion
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessDefList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取未发布子流程列表")
	@RequestMapping(value = "/getProcessDefList")
	PageProcessDefList getProcessDefList(String key, String name, int state, boolean onlyLatestVersion, int pageNum, int pageSize){
		return consoleService.getProcessDefList(key, name, state, onlyLatestVersion, pageNum, pageSize);
	}
	
	/**
	* @Title: getProcessDef 
	* @Description: 根据流程id获取流程详情
	* @param id
	* @return  参数说明 
	* @return ProcessDefData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("根据流程id获取流程详情")
	@RequestMapping(value = "/getProcessDef")
	ProcessDefData getProcessDef(String id) throws WorkFlowException{
		return consoleService.getProcessDef(id);
	}
	
	/**
	* @Title: getProcessInstanceListByDef 
	* @Description: 根据流程定义ID获取流程实例列表
	* @param processId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessInstanceList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("根据流程定义ID获取流程实例列表")
	@RequestMapping(value = "/getProcessInstanceListByDef")
	PageProcessInstanceList getProcessInstanceListByDef(String processId, int pageNum, int pageSize) throws WorkFlowException{
		return consoleService.getProcessInstanceListByDef(processId, pageNum, pageSize);
	}
	
	/**
	* @Title: getSubProcessListByParent 
	* @Description: 获取当前主流程所用子流程列表
	* @param parentId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageSubProcessDefList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取当前主流程所用子流程列表")
	@RequestMapping(value = "/getSubProcessListByParent")
	PageSubProcessDefList getSubProcessListByParent(String parentId, int pageNum, int pageSize) throws WorkFlowException{
		return consoleService.getSubProcessListByParent(parentId, pageNum, pageSize);
	}
	
	/**
	* @Title: getSubProcessList 
	* @Description: 获取未发布子流程列表
	* @param key
	* @param name
	* @param state
	* @param onlyLatestVersion
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageSubProcessDefList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取已发布子流程列表")
	@RequestMapping(value = "/getSubProcessList")
	PageSubProcessDefList getSubProcessList(String key, String name, int state, boolean onlyLatestVersion, int pageNum, int pageSize){
		return consoleService.getSubProcessList(key, name, state, onlyLatestVersion, pageNum, pageSize);
	}
	
	/**
	* @Title: getSubProcessDef 
	* @Description: 获取子流程详情
	* @param id
	* @return  参数说明 
	* @return SubProcessDefData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取子流程详情")
	@RequestMapping(value = "/getSubProcessDef")
	SubProcessDefData getSubProcessDef(String id) throws WorkFlowException{
		return consoleService.getSubProcessDef(id);
	}
	
	/**
	* @Title: getSubProcessInstanceListByDef 
	* @Description: 根据子流程定义ID获取流程实例列表
	* @param processId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageSubProcessInstanceList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("根据子流程定义ID获取流程实例列表")
	@RequestMapping(value = "/getSubProcessInstanceListByDef")
	PageSubProcessInstanceList getSubProcessInstanceListByDef(String processId, int pageNum, int pageSize) throws WorkFlowException{
		return consoleService.getSubProcessInstanceListByDef(processId, pageNum, pageSize);
	}
	
	/**
	* @Title: getProcessInstanceList 
	* @Description: 获取主流程实例列表
	* @param key
	* @param name
	* @param state
	* @param pageNum
	* @param pageSize
	* @param orderByStartTime
	* @param orderByEndTime
	* @return  参数说明 
	* @return PageProcessInstanceList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取主流程实例列表")
	@RequestMapping(value = "/getProcessInstanceList")
	PageProcessInstanceList getProcessInstanceList(String key, String name, int state, int pageNum, int pageSize, int orderByStartTime, int orderByEndTime){
		return consoleService.getProcessInstanceList(key, name, state, pageNum, pageSize, orderByStartTime, orderByEndTime);
	}
	
	/**
	* @Title: getProcessInstance 
	* @Description: 获取流程实例详情
	* @param id
	* @return  参数说明 
	* @return ProcessInstanceData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取流程实例详情")
	@RequestMapping(value = "/getProcessInstance")
	ProcessInstanceData getProcessInstance(String id) throws WorkFlowException{
		return consoleService.getProcessInstance(id);
	}
	
	/**
	* @Title: getProcessTaskListByInstance 
	* @Description: 获取当前流程实例所有的任务
	* @param processInstanceId
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取当前流程实例所有的任务")
	@RequestMapping(value = "/getProcessTaskListByInstance")
	PageProcessTaskList getProcessTaskListByInstance(String processInstanceId, int pageNum, int pageSize) throws WorkFlowException{
		return consoleService.getProcessTaskListByInstance(processInstanceId, pageNum, pageSize);
	}
	
	/**
	* @Title: getSubProcessInstanceList 
	* @Description: 获取子流程实例列表
	* @param key
	* @param name
	* @param state
	* @param pageNum
	* @param pageSize
	* @param orderByStartTime
	* @param orderByEndTime
	* @return  参数说明 
	* @return PageSubProcessInstanceList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取子流程实例列表")
	@RequestMapping(value = "/getSubProcessInstanceList")
	PageSubProcessInstanceList getSubProcessInstanceList(String key, String name, int state, int pageNum, int pageSize, int orderByStartTime, int orderByEndTime){
		return consoleService.getSubProcessInstanceList(key, name, state, pageNum, pageSize, orderByStartTime, orderByEndTime);
	}

	/**
	* @Title: getSubProcessInstance 
	* @Description: 获取子流程实例详情
	* @param id
	* @return  参数说明 
	* @return SubProcessInstanceData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取子流程实例详情")
	@RequestMapping(value = "/getSubProcessInstance")
	SubProcessInstanceData getSubProcessInstance(String id) throws WorkFlowException{
		return consoleService.getSubProcessInstance(id);
	}
	
	/**
	* @Title: getProcessTaskList 
	* @Description: 获取流程任务列表
	* @param key
	* @param processDefId
	* @param processInstanceId
	* @param name
	* @param assignee
	* @param state
	* @param pageNum
	* @param pageSize
	* @param orderByStartTime
	* @param orderByEndTime
	* @return  参数说明 
	* @return PageProcessTaskList    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取流程任务列表")
	@RequestMapping(value = "/getProcessTaskList")
	PageProcessTaskList getProcessTaskList(String key, String processDefId, String processInstanceId, String name, String assignee, int state, int pageNum, int pageSize, int orderByStartTime, int orderByEndTime){
		return consoleService.getProcessTaskList(key, processDefId, processInstanceId, name, assignee, state, pageNum, pageSize, orderByStartTime, orderByEndTime);
	}

	/**
	* @Title: getProcessTask 
	* @Description: 获取流程任务详情
	* @param id
	* @return  参数说明 
	* @return ProcessTaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取流程任务详情")
	@RequestMapping(value = "/getProcessTask")
	ProcessTaskData getProcessTask(String id) throws WorkFlowException{
		return consoleService.getProcessTask(id);
	}

	/**
	* @Title: getUserFinishTaskList 
	* @Description: 获取用户任务管理信息
	* @param userName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageUserFinishTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取用户任务管理信息")
	@RequestMapping(value = "/getUserFinishedTaskList")
	PageUserFinishedTaskList getUserFinishedTaskList(String userName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException{
		return consoleService.getUserFinishedTaskList(userName, pageNum, pageSize, orderByCount);
	}
	
	/**
	* @Title: getUserFinishedTask 
	* @Description: 获取用户任务完成信息
	* @param userId
	* @return  参数说明 
	* @return UserFinishedTaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取用户任务完成信息")
	@RequestMapping(value = "/getUserFinishedTask")
	UserFinishedTaskData getUserFinishedTask(String userId) throws WorkFlowException{
		return consoleService.getUserFinishedTask(userId);
	}
	
	/**
	* @Title: getFinishedTasksByUser 
	* @Description: 获取用户已完成任务列表
	* @param userId
	* @param processDefId
	* @param taskName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取用户已完成任务列表")
	@RequestMapping(value = "/getFinishedTasksByUser")
	PageProcessTaskList getFinishedTasksByUser(String userId, String processDefId, String taskName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException{
		return consoleService.getFinishedTasksByUser(userId, processDefId, taskName, pageNum, pageSize, orderByFinishedTime);
	}
	
	/**
	* @Title: getUnitFinishedTaskList 
	* @Description: 获取部门任务管理信息
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageUnitFinishedTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取部门任务管理信息")
	@RequestMapping(value = "/getUnitFinishedTaskList")
	PageUnitFinishedTaskList getUnitFinishedTaskList(int pageNum, int pageSize, int orderByCount) throws WorkFlowException{
		return consoleService.getUnitFinishedTaskList(pageNum, pageSize, orderByCount);
	}
	
	/**
	* @Title: getUnitFinishedTask 
	* @Description: 获取部门任务完成信息
	* @param unitId
	* @return  参数说明 
	* @return UserFinishedTaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取部门任务完成信息")
	@RequestMapping(value = "/getUnitFinishedTask")
	UnitFinishedTaskData getUnitFinishedTask(String unitId) throws WorkFlowException{
		return consoleService.getUnitFinishedTask(unitId);
	}
	
	/**
	* @Title: getFinishedTasksByUnit 
	* @Description: 获取部门已完成任务列表
	* @param unitId
	* @param processDefId
	* @param taskName
	* @param userName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取部门已完成任务列表")
	@RequestMapping(value = "/getFinishedTasksByUnit")
	PageProcessTaskList getFinishedTasksByUnit(String unitId, String processDefId, String taskName, String userName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException{
		return consoleService.getFinishedTasksByUnit(unitId, processDefId, taskName, userName, pageNum, pageSize, orderByFinishedTime);
	}
	
	/**
	* @Title: getProcessPreview 
	* @Description: 获取流程预览图
	* @param id
	* @return  参数说明 
	* @return String    返回类型 
	 * @throws WorkFlowException 
	 * @throws IOException 
	* 
	*/
	@ResponseBody
	@ApiOperation("根据流程定义获取流程图，返回base64加密png图片数据  传参内容：已发布流程key main_plantaskbook_zkt01:20:42504")
	@RequestMapping(value = "/getProcessPreview")
	String getProcessPreview(String id) throws WorkFlowException, IOException{
		return consoleService.getProcessPreview(id);
	}
	
	/**
	* @Title: getProcessPreview
	* @Description: 获取未发布流程预览图
	* @param id
	* @return  参数说明 
	* @return String    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取未发布流程预览图-传参内容 流程id 392baa9c58a34edab63c3ca3df5b7af3")
	@RequestMapping(value = "/getUnProcessPreview")
	String getUnProcessPreview(String id) throws WorkFlowException{
		return consoleService.getUnProcessPreview(id);
	}
	
	/**
	* @Title: getDeploymentData 
	* @Description: 获取部署信息
	* @param deploymentId
	* @return  参数说明 
	* @return ProcessDeploymentData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取部署信息")
	@RequestMapping(value = "/getDeploymentData")
	ProcessDeploymentData getDeploymentData(String deploymentId) throws WorkFlowException{
		return consoleService.getDeploymentData(deploymentId);
	}
	
	/**
	* @Title: getUnitList 
	* @Description: 获取部门列表，根据名称模糊查询
	* @param name
	* @return  参数说明 
	* @return List<SimpleInfo>    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取部门列表，根据名称模糊查询")
	@RequestMapping(value = "/getUnitList")
	List<SimpleInfo> getUnitList(String name){
		return consoleService.getUnitList(name);
	}
	
	/**
	* @Title: getSubUnitList 
	* @Description: 获取子部门列表
	* @param parentId
	* @return  参数说明 
	* @return List<SimpleInfo>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取子部门列表")
	@RequestMapping(value = "/getSubUnitList")
	List<SimpleInfo> getSubUnitList(String parentId) throws WorkFlowException{
		return consoleService.getSubUnitList(parentId);
	}
	
	/**
	* @Title: getUserListByUnit 
	* @Description: 获取部门用户列表，根据用户名称模糊查询
	* @param unitId
	* @param userName
	* @return  参数说明 
	* @return List<SimpleInfo>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("获取子部门列表")
	@RequestMapping(value = "/getUserListByUnit")
	List<SimpleInfo> getUserListByUnit(String unitId, String userName) throws WorkFlowException{
		return consoleService.getUserListByUnit(unitId, userName);
	}
	
	/**
	* @Title: deploymentProcessDefinition 
	* @Description: 部署流程定义
	* @param id
	* @return  参数说明 
	* @return ProcessDefinition    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@Transactional
	@ResponseBody
	@ApiOperation("部署流程定义")
	@RequestMapping(value = "/deploymentProcessDefinition",produces = "application/json" )
	Object deploymentProcessDefinition(String id, boolean forceUpdateSub) throws WorkFlowException{
		return consoleService.deploymentProcessDefinition(id, forceUpdateSub);
	}
	
	/**
	* @Title: deploymentSubProcessDefinition 
	* @Description: 部署子流程定义
	* @param id
	* @param useNewKey
	* @return
	* @throws WorkFlowException  参数说明 
	* @return ProcessDefinition    返回类型 
	* 
	*/
	@Transactional
	@ResponseBody
	@ApiOperation("部署子流程定义")
	@RequestMapping(value = "/deploymentSubProcessDefinition")
	ProcessDefinition deploymentSubProcessDefinition(String id, boolean useNewKey) throws WorkFlowException{
		return consoleService.deploymentSubProcessDefinition(id, useNewKey);
	}
	
	/**
	* @Title: deleteUnProcessDef 
	* @Description: 删除未发布流程定义
	* @param id
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	@Transactional
	@ResponseBody
	@ApiOperation("删除未发布流程定义")
	@RequestMapping(value = "/deleteUnProcessDef")
	boolean deleteUnProcessDef(String id){
		return consoleService.deleteUnProcessDef(id);
	}
	
	/**
	* @Title: suspendProcessDef 
	* @Description: 终止流程定义
	* @param id
	* @param suspendInstance
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("终止流程定义")
	@RequestMapping(value = "/suspendProcessDef")
	boolean suspendProcessDef(String id, boolean suspendInstance){
		return consoleService.suspendProcessDef(id, suspendInstance);
	}
	
	/**
	* @Title: activateProcessDef 
	* @Description: 激活流程定义
	* @param id
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("激活流程定义")
	@RequestMapping(value = "/activateProcessDef")
	boolean activateProcessDef(String id, boolean activateInstance){
		return consoleService.activateProcessDef(id, activateInstance);
	}
	
	/**
	* @Title: suspendProcessInst 
	* @Description: 暂停流程实例
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("暂停流程实例")
	@RequestMapping(value = "/suspendProcessInst")
	boolean suspendProcessInst(String userId, String processInstanceId) throws WorkFlowException{
		return consoleService.suspendProcessInst(userId, processInstanceId);
	}
	
	/**
	* @Title: activateProcessInst 
	* @Description: 激活流程实例
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("激活流程实例")
	@RequestMapping(value = "/activateProcessInst")
	boolean activateProcessInst(String userId, String processInstanceId) throws WorkFlowException{
		return consoleService.activateProcessInst(userId, processInstanceId);
	}
	
	/**
	* @Title: stopProcessInst 
	* @Description: 结束流程实例
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("结束流程实例")
	@RequestMapping(value = "/stopProcessInst")
	boolean stopProcessInst(String userId, String processInstanceId, String reason) throws WorkFlowException{
		return consoleService.stopProcessInst(userId, processInstanceId, reason);
	}

	
	/**
	* @Title: setTaskAssignee 
	* @Description: 指派任务操作者
	* @param userId
	* @param taskId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("指派任务操作者")
	@RequestMapping(value = "/setTaskAssignee")
	boolean setTaskAssignee(String userId, String taskId) throws WorkFlowException{
		return consoleService.setTaskAssignee(userId, taskId);
	}
	
	/**
	* @Title: suspendProcessTask 
	* @Description: 暂停流程任务
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("暂停流程任务")
	@RequestMapping(value = "/suspendProcessTask")
	boolean suspendProcessTask(String userId, String taskId) throws WorkFlowException{
		return consoleService.suspendProcessTask(userId, taskId);
	}
	
	/**
	* @Title: activateProcessTask 
	* @Description: 激活流程任务
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("激活流程任务")
	@RequestMapping(value = "/activateProcessTask")
	boolean activateProcessTask(String userId, String taskId) throws WorkFlowException{
		return consoleService.activateProcessTask(userId, taskId);
	}
	
	/**
	* @Title: copyUnProcess 
	* @Description: 复制未发布流程
	* @param id
	* @param newKey
	* @param newName
	* @return  参数说明 
	* @return String    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	@ResponseBody
	@ApiOperation("复制未发布流程")
	@RequestMapping(value = "/copyUnProcess")
	Map<String,Object> copyUnProcess(String id, String newKey, String newName) throws WorkFlowException{
		return consoleService.copyUnProcess(id, newKey, newName);
	}

}
