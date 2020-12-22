package workflow.business.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/** 
* @ClassName: ProcessTaskData 
* @Description: 流程任务数据
* @author KaminanGTO
* @date 2018年10月25日 下午3:44:38 
*  
*/
public class ProcessTaskData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields id : 任务ID
	*/ 
	private String id;
	
	/** 
	* @Fields processInstanceId : 流程实例ID
	*/ 
	private String processInstanceId;
	
	/** 
	* @Fields processDefIf : 流程定义ID
	*/ 
	private String processDefId;
	
	/** 
	* @Fields startTime : 任务开始时间
	*/ 
	private Date startTime;
	
	/** 
	* @Fields endTime : 任务完成时间
	*/ 
	private Date endTime;
	
	/** 
	* @Fields name : 任务名称
	*/ 
	private String name;
	
	/** 
	* @Fields assignee : 任务操作者
	*/ 
	private TaskUserInfo assignee;
	
	/** 
	* @Fields parentTaskId : 父任务ID
	*/ 
	private String parentTaskId;
	
	/** 
	* @Fields description : 任务描述
	*/ 
	private String description;
	
	/** 
	* @Fields deleteReason : 任务删除原因
	*/ 
	private String deleteReason;
	
	/** 
	* @Fields state : 任务状态，0已暂停，1进行中，2已完成，3已删除
	*/ 
	private int state;
	
	/** 
	* @Fields propertyList : 流程实例变量列表 
	*/ 
	private List<TaskVariableData> taskVariableDataList;
	
	/** 
	* @Fields formDataList : 表单列表
	*/ 
	private List<TaskFormData> taskFormDataList;
	
	/** 
	* @Fields singUsersList : 会签用户列表
	*/ 
	private List<TaskUserInfo> taskSignUserInfoList;
	
	/** 
	* @Fields taskClaimUserInfoList : 待候选用户列表
	*/ 
	private List<TaskUserInfo> taskClaimUserInfoList;
	
	/** 
	* @Fields taskClaimGroupInfoList : 待候选组列表
	*/ 
	private List<TaskClaimGroupInfo> taskClaimGroupInfoList;
	
	/** 
	* @Fields taskExValues : 业务自定义变量数据
	*/ 
	private List<TaskExVariableData> taskExVariableDataList;
	
	/** 
	* @Fields businessValue : 业务参数，目前存放URL地址
	*/ 
	private String businessValue;
	
	/** 
	* @Fields dueDate : 任务过期时间
	*/ 
	private Date dueDate;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefIf) {
		this.processDefId = processDefIf;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskUserInfo getAssignee() {
		return assignee;
	}

	public void setAssignee(TaskUserInfo assignee) {
		this.assignee = assignee;
	}

	public String getParentTaskId() {
		return parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public List<TaskVariableData> getTaskVariableDataList() {
		return taskVariableDataList;
	}

	public void setTaskVariableDataList(List<TaskVariableData> taskVariableDataList) {
		this.taskVariableDataList = taskVariableDataList;
	}

	public List<TaskFormData> getTaskFormDataList() {
		return taskFormDataList;
	}

	public void setTaskFormDataList(List<TaskFormData> taskFormDataList) {
		this.taskFormDataList = taskFormDataList;
	}

	public List<TaskUserInfo> getTaskSignUserInfoList() {
		return taskSignUserInfoList;
	}

	public void setTaskSignUserInfoList(List<TaskUserInfo> taskSignUserInfoList) {
		this.taskSignUserInfoList = taskSignUserInfoList;
	}

	public List<TaskUserInfo> getTaskClaimUserInfoList() {
		return taskClaimUserInfoList;
	}

	public void setTaskClaimUserInfoList(List<TaskUserInfo> taskClaimUserInfoList) {
		this.taskClaimUserInfoList = taskClaimUserInfoList;
	}

	public List<TaskClaimGroupInfo> getTaskClaimGroupInfoList() {
		return taskClaimGroupInfoList;
	}

	public void setTaskClaimGroupInfoList(List<TaskClaimGroupInfo> taskClaimGroupInfoList) {
		this.taskClaimGroupInfoList = taskClaimGroupInfoList;
	}

	public List<TaskExVariableData> getTaskExVariableDataList() {
		return taskExVariableDataList;
	}

	public void setTaskExVariableDataList(List<TaskExVariableData> taskExVariableDataList) {
		this.taskExVariableDataList = taskExVariableDataList;
	}

	public String getBusinessValue() {
		return businessValue;
	}

	public void setBusinessValue(String businessValue) {
		this.businessValue = businessValue;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
}
