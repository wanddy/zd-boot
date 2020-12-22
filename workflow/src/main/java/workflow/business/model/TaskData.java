package workflow.business.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** 
* @ClassName: TaskData 
* @Description: 流程任务数据
* @author KaminanGTO
* @date 2018年9月11日 下午12:52:52 
*  
*/
public class TaskData implements Serializable{

	private static final long serialVersionUID = -9017256825824977364L;

	/** 
	* @Fields id : 任务ID
	*/ 
	private String id;
	
	/** 
	* @Fields taskDefId : 任务定义ID
	*/ 
	private String taskDefId;
	
	/** 
	* @Fields starter : 任务发起人
	*/ 
	private String starter;
	
	/** 
	* @Fields name : 任务名称
	*/ 
	private String name;
	
	/** 
	* @Fields createTime : 任务创建时间 
	*/ 
	private Date createTime;
	
	/** 
	* @Fields endTime : 任务结束时间
	*/ 
	private Date endTime;
	
	/** 
	* @Fields assigness : 任务办理人
	*/ 
	private String assigness;
	
	/** 
	* @Fields processInstanceId : 流程实例ID
	*/ 
	private String processInstanceId;
	
	/** 
	* @Fields processDefinitionId : 流程定义ID
	*/ 
	private String processDefinitionId;
	
	/** 
	* @Fields processDefinitionName : 流程定义名称
	*/ 
	private String processDefinitionName;
	
	/** 
	* @Fields processInstBusinessKey : 流程实例业务key
	*/ 
	private String processInstBusinessKey;
	
	/** 
	* @Fields executionId : 执行对象id
	*/ 
	private String executionId;
	
	/** 
	* @Fields propertyList : 流程实例变量列表 
	*/ 
	private List<ProcessProperty> propertyList;
	
	/** 
	* @Fields judgeList : 判断型变量列表
	*/ 
	private List<JudgeProperty> judgeList;
	
	/** 
	* @Fields formDataList : 表单列表
	*/ 
	private List<FormData> formDataList;
	
	/** 
	* @Fields singUsersList : 会签用户列表
	*/ 
	private List<SignUsersData> signUsersList;
	
	/** 
	* @Fields claimUsersList : 候选用户列表
	*/ 
	private List<ClaimUsersData> claimUsersList;
	
	/** 
	* @Fields claimGroupList : 候选组列表
	*/ 
	private List<ClaimGroupData> claimGroupList;
	
	/** 
	* @Fields retrieveTasks : 取回任务列表
	*/ 
	private List<String> retrieveTasks;
	
	/** 
	* @Fields exValues : 业务自定义数据
	*/ 
	private Map<String, String> exValues;
	
	/** 
	* @Fields businessValue : 业务参数，目前存放URL地址
	*/ 
	private String businessValue;
	
	/** 
	* @Fields businessExpired : 业务过期时间，使用自定义变量存放（单位小时）
	*/ 
	private int businessExpired;
	
	/** 
	* @Fields deleteReason : 删除原因（中止流程时赋值）
	*/ 
	private String deleteReason;
	
	/** 
	* @Fields dueDate : 任务过期时间 
	*/ 
	private Date dueDate;
	
	/** 
	* @Fields stateType : 任务状态类型，1待完成，2待候选
	*/ 
	private int stateType;
	
	/** 
	* @Fields canRetrieve : 是否可取回
	*/ 
	private boolean canRetrieve;
	
	/** 
	* @Fields assignessList : 待操作者列表
	*/ 
	private List<String> assignessList;
	
	/** 
	* @Fields parentTaskDefId : 父任务定义id
	*/ 
	private String parentTaskDefId;
	
	/** 
	* @Fields rootProcessInstanceId : 主流程实例id
	*/ 
	private String rootProcessInstanceId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	public String getStarter() {
		return starter;
	}

	public void setStarter(String starter) {
		this.starter = starter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getAssigness() {
		return assigness;
	}

	public void setAssigness(String assigness) {
		this.assigness = assigness;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public String getProcessInstBusinessKey() {
		return processInstBusinessKey;
	}

	public void setProcessInstBusinessKey(String processInstBusinessKey) {
		this.processInstBusinessKey = processInstBusinessKey;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public List<ProcessProperty> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<ProcessProperty> propertyList) {
		this.propertyList = propertyList;
	}

	public List<JudgeProperty> getJudgeList() {
		return judgeList;
	}

	public void setJudgeList(List<JudgeProperty> judgeList) {
		this.judgeList = judgeList;
	}

	public List<FormData> getFormDataList() {
		return formDataList;
	}

	public void setFormDataList(List<FormData> formDataList) {
		this.formDataList = formDataList;
	}

	public List<SignUsersData> getSignUsersList() {
		return signUsersList;
	}

	public void setSignUsersList(List<SignUsersData> signUsersList) {
		this.signUsersList = signUsersList;
	}

	public List<ClaimUsersData> getClaimUsersList() {
		return claimUsersList;
	}

	public void setClaimUsersList(List<ClaimUsersData> claimUsersList) {
		this.claimUsersList = claimUsersList;
	}

	public List<ClaimGroupData> getClaimGroupList() {
		return claimGroupList;
	}

	public void setClaimGroupList(List<ClaimGroupData> claimGroupList) {
		this.claimGroupList = claimGroupList;
	}

	public List<String> getRetrieveTasks() {
		return retrieveTasks;
	}

	public void setRetrieveTasks(List<String> retrieveTasks) {
		this.retrieveTasks = retrieveTasks;
	}

	public Map<String, String> getExValues() {
		return exValues;
	}

	public void setExValues(Map<String, String> exValues) {
		this.exValues = exValues;
	}

	public String getBusinessValue() {
		return businessValue;
	}

	public void setBusinessValue(String businessValue) {
		this.businessValue = businessValue;
	}

	public int getBusinessExpired() {
		return businessExpired;
	}

	public void setBusinessExpired(int businessExpired) {
		this.businessExpired = businessExpired;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public int getStateType() {
		return stateType;
	}

	public void setStateType(int stateType) {
		this.stateType = stateType;
	}

	public boolean isCanRetrieve() {
		return canRetrieve;
	}

	public void setCanRetrieve(boolean canRetrieve) {
		this.canRetrieve = canRetrieve;
	}

	public List<String> getAssignessList() {
		return assignessList;
	}

	public void setAssignessList(List<String> assignessList) {
		this.assignessList = assignessList;
	}

	public String getParentTaskDefId() {
		return parentTaskDefId;
	}

	public void setParentTaskDefId(String parentTaskDefId) {
		this.parentTaskDefId = parentTaskDefId;
	}

	public String getRootProcessInstanceId() {
		return rootProcessInstanceId;
	}

	public void setRootProcessInstanceId(String rootProcessInstanceId) {
		this.rootProcessInstanceId = rootProcessInstanceId;
	}

	
}
