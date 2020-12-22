package workflow.business.model;

import java.io.Serializable;
import java.util.List;


public class FinishedTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2549900671313598359L;
	
	private List<String> accessScopeList;
	
	/**
	 * 
	 */
	private String id;
	
	/**
	 * 事项分类
	 */
	private String categoryType;
	
	/**
	 * 任务id
	 */
	private String taskId;

	/** 
	* @Fields parentTaskDefId : 父任务定义id
	*/ 
	private String parentTaskDefId;
	
	/** 
	* @Fields rootProcessInstanceId : 主流程实例id
	*/ 
	private String rootProcessInstanceId;
	
	/**
	 * 任务节点id
	 */
	private String taskDefId;
	
	/**
	 * 任务名称
	 */
	private String taskName;
	
	/**
	 * 发起人
	 */
	private String initiator;
	
	/**
	 * 流程key

	 */
	private String processKey;
	
	/**
	 * 流程实例id
	 */
	private String processInstanceId;
	
	/**
	 * 任务节点操作列表JSON
	 */
	private String judgeList;
	
	/**
	 * 操作人
	 */
	private String assignee;
	
	/**
	 * 实际操作人
	 */
	private String realAssignee;
	
	/**
	 * 实际操作人姓名
	 */
	private String realAssigneeName;
	
	/**
	 * 任务类型
	 */
	private Integer taskType;
	
	/**
	 * 事项id
	 */
	private String accessScope;
	
	/**
	 * 审批事项id
	 */
	private String contentId;
	
	/**
	 * 审批事项名称
	 */
	private String contentName;
	
	/**
	 * 任务编号
	 */
	private String taskNo;
	
	/**
	 * 可撤回节点列表
	 */
	private String canRetrieve;
	
	/**
	 * 流程变量列表JSON
	 */
	private String propertyList;
	
	/**
	 * 会签用户参数JSON
	 */
	private String signUsersParams;
	
	/**
	 * 认领用户参数JSON
	 */
	private String claimUsersParams;

	/**
	 * 认领组参数JSON
	 */
	private String claimGroupParams;
	
	/**
	 * 操作结果pass、nopass、back...
	 */
	private String opResult;

	/**
	 * 操作结果 审核通过、审核不通过、退回修改...
	 */
	private String opResultDesc;
	
	/**
	 * 发起时间
	 */
	private Long startTime;
	
	/**
	 * 完成时间
	 */
	private Long finishTime;

	/** 
	* @Fields processDefinitionId : 流程定义ID
	*/ 
	private String processDefinitionId;
	
	/** 
	* @Fields processDefinitionName : 流程定义名称
	*/ 
	private String processDefinitionName;

	/**
	 * 节点上有多个任务时，保存此值记录为一个节点任务
	 */
	private String taskBatch;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getJudgeList() {
		return judgeList;
	}

	public void setJudgeList(String judgeList) {
		this.judgeList = judgeList;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getRealAssignee() {
		return realAssignee;
	}

	public void setRealAssignee(String realAssignee) {
		this.realAssignee = realAssignee;
	}

	public String getRealAssigneeName() {
		return realAssigneeName;
	}

	public void setRealAssigneeName(String realAssigneeName) {
		this.realAssigneeName = realAssigneeName;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public String getAccessScope() {
		return accessScope;
	}

	public void setAccessScope(String accessScope) {
		this.accessScope = accessScope;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getCanRetrieve() {
		return canRetrieve;
	}

	public void setCanRetrieve(String canRetrieve) {
		this.canRetrieve = canRetrieve;
	}

	public String getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(String propertyList) {
		this.propertyList = propertyList;
	}

	public String getSignUsersParams() {
		return signUsersParams;
	}

	public void setSignUsersParams(String signUsersParams) {
		this.signUsersParams = signUsersParams;
	}

	public String getClaimUsersParams() {
		return claimUsersParams;
	}

	public void setClaimUsersParams(String claimUsersParams) {
		this.claimUsersParams = claimUsersParams;
	}

	public String getClaimGroupParams() {
		return claimGroupParams;
	}

	public void setClaimGroupParams(String claimGroupParams) {
		this.claimGroupParams = claimGroupParams;
	}

	public String getOpResult() {
		return opResult;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}

	public String getOpResultDesc() {
		return opResultDesc;
	}

	public void setOpResultDesc(String opResultDesc) {
		this.opResultDesc = opResultDesc;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public List<String> getAccessScopeList() {
		return accessScopeList;
	}

	public void setAccessScopeList(List<String> accessScopeList) {
		this.accessScopeList = accessScopeList;
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

	public String getTaskBatch() {
		return taskBatch;
	}

	public void setTaskBatch(String taskBatch) {
		this.taskBatch = taskBatch;
	}
	
}
