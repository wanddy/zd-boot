package workflow.business.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;

import workflow.common.error.WorkFlowException;
import workflow.business.model.PageProcessDefList;
import workflow.business.model.PageProcessInstanceList;
import workflow.business.model.PageProcessTaskList;
import workflow.business.model.PageSubProcessDefList;
import workflow.business.model.PageSubProcessInfoList;
import workflow.business.model.PageSubProcessInstanceList;
import workflow.business.model.PageUnReleaseProcessList;
import workflow.business.model.PageUnReleaseSubProcessList;
import workflow.business.model.PageUnitFinishedTaskList;
import workflow.business.model.PageUserFinishedTaskList;
import workflow.business.model.ProcessDefData;
import workflow.business.model.ProcessDeploymentData;
import workflow.business.model.ProcessInstanceData;
import workflow.business.model.ProcessTaskData;
import workflow.business.model.SimpleInfo;
import workflow.business.model.SubProcessDefData;
import workflow.business.model.SubProcessInstanceData;
import workflow.business.model.UnReleaseProcessData;
import workflow.business.model.UnReleaseSubProcessData;
import workflow.business.model.UnitFinishedTaskData;
import workflow.business.model.UserFinishedTaskData;

/** 
* @ClassName: ConsoleService 
* @Description: 控制台接口
* @author KaminanGTO
* @date 2018年11月7日 下午3:09:36 
*  
*/
public interface ConsoleService {

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
	PageUnReleaseProcessList getURProcessList(String key, String name, int showVersion, int pageNum, int pageSize, int orderByCreateTime, int orderByUpdateTime);
	
	/** 
	* @Title: getURProcess 
	* @Description: 根据流程ID获取未发布流程详情
	* @param id
	* @return  参数说明 
	* @return UnReleaseProcessData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	UnReleaseProcessData getURProcess(String id) throws WorkFlowException;
	
	/**
	 * @Title: getURProcess
	 * @Description: 根据流程KEY获取未发布流程详情
	 * @param key
	 * @return  参数说明
	 * @return UnReleaseProcessData    返回类型
	 * @throws WorkFlowException
	 *
	 */
	UnReleaseProcessData getURProcessByKey(String key) throws WorkFlowException;
	
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
	PageSubProcessInfoList getURSubProcessListByParent(String parentId, int pageNum, int pageSize) throws WorkFlowException;
	
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
	PageUnReleaseSubProcessList getURSubProcessList(String key, String name, int showVersion, int pageNum, int pageSize, int orderByCreateTime, int orderByUpdateTime);
	
	/** 
	* @Title: getURSubProcess 
	* @Description: 根据流程ID获取未发布子流程详情
	* @param id
	* @return  参数说明 
	* @return UnReleaseSubProcessData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	UnReleaseSubProcessData getURSubProcess(String id) throws WorkFlowException;
	
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
	PageProcessDefList getProcessDefList(String key, String name, int state, boolean onlyLatestVersion, int pageNum, int pageSize);
	
	/** 
	* @Title: getProcessDef 
	* @Description: 根据流程id获取流程详情
	* @param id
	* @return  参数说明 
	* @return ProcessDefData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	ProcessDefData getProcessDef(String id) throws WorkFlowException;
	
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
	PageProcessInstanceList getProcessInstanceListByDef(String processId, int pageNum, int pageSize) throws WorkFlowException;
	
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
	PageSubProcessDefList getSubProcessListByParent(String parentId, int pageNum, int pageSize) throws WorkFlowException;
	
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
	PageSubProcessDefList getSubProcessList(String key, String name, int state, boolean onlyLatestVersion, int pageNum, int pageSize);
	
	/** 
	* @Title: getSubProcessDef 
	* @Description: 获取子流程详情
	* @param id
	* @return  参数说明 
	* @return SubProcessDefData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	SubProcessDefData getSubProcessDef(String id) throws WorkFlowException;
	
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
	PageSubProcessInstanceList getSubProcessInstanceListByDef(String processId, int pageNum, int pageSize) throws WorkFlowException;
	
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
	PageProcessInstanceList getProcessInstanceList(String key, String name, int state, int pageNum, int pageSize, int orderByStartTime, int orderByEndTime);
	
	/** 
	* @Title: getProcessInstance 
	* @Description: 获取流程实例详情
	* @param id
	* @return  参数说明 
	* @return ProcessInstanceData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	ProcessInstanceData getProcessInstance(String id) throws WorkFlowException;
	
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
	PageProcessTaskList getProcessTaskListByInstance(String processInstanceId, int pageNum, int pageSize) throws WorkFlowException;
	
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
	PageSubProcessInstanceList getSubProcessInstanceList(String key, String name, int state, int pageNum, int pageSize, int orderByStartTime, int orderByEndTime);

	/** 
	* @Title: getSubProcessInstance 
	* @Description: 获取子流程实例详情
	* @param id
	* @return  参数说明 
	* @return SubProcessInstanceData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	SubProcessInstanceData getSubProcessInstance(String id) throws WorkFlowException;
	
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
	PageProcessTaskList getProcessTaskList(String key, String processDefId, String processInstanceId, String name, String assignee, int state, int pageNum, int pageSize, int orderByStartTime, int orderByEndTime);

	/** 
	* @Title: getProcessTask 
	* @Description: 获取流程任务详情
	* @param id
	* @return  参数说明 
	* @return ProcessTaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	ProcessTaskData getProcessTask(String id) throws WorkFlowException;

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
	PageUserFinishedTaskList getUserFinishedTaskList(String userName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException;
	
	/** 
	* @Title: getUserFinishedTask 
	* @Description: 获取用户任务完成信息
	* @param userId
	* @return  参数说明 
	* @return UserFinishedTaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	UserFinishedTaskData getUserFinishedTask(String userId) throws WorkFlowException;
	
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
	PageProcessTaskList getFinishedTasksByUser(String userId, String processDefId, String taskName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException;
	
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
	PageUnitFinishedTaskList getUnitFinishedTaskList(int pageNum, int pageSize, int orderByCount) throws WorkFlowException;
	
	/** 
	* @Title: getUnitFinishedTask 
	* @Description: 获取部门任务完成信息
	* @param unitId
	* @return  参数说明 
	* @return UserFinishedTaskData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	UnitFinishedTaskData getUnitFinishedTask(String unitId) throws WorkFlowException;
	
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
	PageProcessTaskList getFinishedTasksByUnit(String unitId, String processDefId, String taskName, String userName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException;
	
	/** 
	* @Title: getProcessPreview 
	* @Description: 获取流程预览图 根据流程定义获取流程图，返回base64加密png图片数据  传参内容：已发布流程key main_plantaskbook_zkt01:20:42504
	* @param id
	* @return  参数说明 
	* @return String    返回类型 
	 * @throws WorkFlowException 
	 * @throws IOException 
	* 
	*/
	String getProcessPreview(String id) throws WorkFlowException, IOException;
	
	/** 
	* @Title: getProcessPreview 
	* @Description: 获取未发布流程预览图
	* @param id
	* @return  参数说明 
	* @return String    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	String getUnProcessPreview(String id) throws WorkFlowException;
	
	/** 
	* @Title: getDeploymentData 
	* @Description: 获取部署信息
	* @param deploymentId
	* @return  参数说明 
	* @return ProcessDeploymentData    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	ProcessDeploymentData getDeploymentData(String deploymentId) throws WorkFlowException;
	
	/** 
	* @Title: getUnitList 
	* @Description: 获取部门列表，根据名称模糊查询
	* @param name
	* @return  参数说明 
	* @return List<SimpleInfo>    返回类型 
	* 
	*/
	List<SimpleInfo> getUnitList(String name);
	
	/** 
	* @Title: getSubUnitList 
	* @Description: 获取子部门列表
	* @param parentId
	* @return  参数说明 
	* @return List<SimpleInfo>    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	List<SimpleInfo> getSubUnitList(String parentId) throws WorkFlowException;
	
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
	List<SimpleInfo> getUserListByUnit(String unitId, String userName) throws WorkFlowException;
	
	/** 
	* @Title: deploymentProcessDefinition 
	* @Description: 部署流程定义
	* @param id
	* @return  参数说明 
	* @return ProcessDefinition    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	Object deploymentProcessDefinition(String id, boolean forceUpdateSub) throws WorkFlowException;
	
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
	ProcessDefinition deploymentSubProcessDefinition(String id, boolean useNewKey) throws WorkFlowException;
	
	/** 
	* @Title: deleteUnProcessDef 
	* @Description: 删除未发布流程定义
	* @param id
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean deleteUnProcessDef(String id);
	
	/** 
	* @Title: suspendProcessDef 
	* @Description: 终止流程定义
	* @param id
	* @param suspendInstance
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean suspendProcessDef(String id, boolean suspendInstance);
	
	/** 
	* @Title: activateProcessDef 
	* @Description: 激活流程定义
	* @param id
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean activateProcessDef(String id, boolean activateInstance);
	
	/** 
	* @Title: suspendProcessInst 
	* @Description: 暂停流程实例
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean suspendProcessInst(String userId, String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: activateProcessInst 
	* @Description: 激活流程实例
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean activateProcessInst(String userId, String processInstanceId) throws WorkFlowException;
	
	/** 
	* @Title: stopProcessInst 
	* @Description: 结束流程实例
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean stopProcessInst(String userId, String processInstanceId, String reason) throws WorkFlowException;
	
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
	boolean setTaskAssignee(String userId, String taskId) throws WorkFlowException;
	
	/** 
	* @Title: suspendProcessTask 
	* @Description: 暂停流程任务
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	* 
	*/
	boolean suspendProcessTask(String userId, String taskId) throws WorkFlowException;
	
	/** 
	* @Title: activateProcessTask 
	* @Description: 激活流程任务
	* @param userId
	* @return  参数说明 
	* @return boolean    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	boolean activateProcessTask(String userId, String taskId) throws WorkFlowException;
	
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
	Map<String,Object> copyUnProcess(String id, String newKey, String newName) throws WorkFlowException;

}
