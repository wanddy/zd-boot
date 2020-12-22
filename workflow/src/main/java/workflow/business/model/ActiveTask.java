package workflow.business.model;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

@TableName("wf_active_task")
public class ActiveTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8604425980189098823L;

	/**
	 * 事项类别id列表
	 */
	@TableField(exist = false)
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
//	@TableField(exist = false)
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
	 * 操作部门
	 */
	@TableField(exist = false)
	private List<String> deptCode;
	
	/**
	 * 组合的操作者查询信息
	 */
	@TableField(exist = false)
	private List<String> assigneeGroup;
	
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
	 * 是否可撤回
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
	 * 表单列表JSON
	 */
	//@TableField(exist = false)
	private String formDataList;

	/**
	 * 创建时间
	 */
	private Long createdAt;

	/** 
	* @Fields processDefinitionId : 流程定义ID
	*/ 
	private String processDefinitionId;
	
	/** 
	* @Fields processDefinitionName : 流程定义名称
	*/ 
	private String processDefinitionName;
	
	/**
	 * @Fields previousOpResultDesc : 上一节点的操作结果
	 */
	private String previousOpResultDesc;

	/**
	 * 流程发起人
	 */
	@TableField(exist = false)
	private String processInitiator;
	
	/**
	 * 节点上有多个任务时，保存此值记录为一个节点任务
	 */
	private String taskBatch;

	/**
	 * 任务类型
	 */
	private Integer status;

	
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

	public List<String> getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(List<String> deptCode) {
		this.deptCode = deptCode;
	}

	public List<String> getAssigneeGroup() {
		return assigneeGroup;
	}

	public void setAssigneeGroup(List<String> assigneeGroup) {
		this.assigneeGroup = assigneeGroup;
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

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
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

	public String getPreviousOpResultDesc() {
		return previousOpResultDesc;
	}

	public void setPreviousOpResultDesc(String previousOpResultDesc) {
		this.previousOpResultDesc = previousOpResultDesc;
	}

	public String getProcessInitiator() {
		return processInitiator;
	}

	public void setProcessInitiator(String processInitiator) {
		this.processInitiator = processInitiator;
	}

	public String getTaskBatch() {
		return taskBatch;
	}

	public void setTaskBatch(String taskBatch) {
		this.taskBatch = taskBatch;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setFormDataList(String formDataList) {
		this.formDataList = formDataList;
	}

	public String getFormDataList() {
		return formDataList;
	}

	/**
	 * 判断变量：将JSON转成list
	 * @return List<JudgeProperty>
	 */
	public List<workflow.business.model.JudgeProperty> getJudgePropertyList() {
		if(StringUtils.isEmpty(judgeList)) {
			return null;
		}
		List<JudgeProperty> list = JSON.parseArray(judgeList, JudgeProperty.class);
		return list;
	}
	/**
	 * 流程变量：将JSON转成list
	 * @return List<ProcessProperty>
	 */
	public List<workflow.business.model.ProcessProperty> getProcessPropertyList() {
		if(StringUtils.isEmpty(propertyList)) {
			return null;
		}
		List<ProcessProperty> list = JSON.parseArray(propertyList, ProcessProperty.class);
		return list;
	}
	/**
	 * 会签用户：将JSON转成list
	 * @return List<SignUsersData>
	 */
	public List<SignUsersData> getSignUsersList() {
		if(StringUtils.isEmpty(signUsersParams)) {
			return null;
		}
		List<SignUsersData> list = JSON.parseArray(signUsersParams, SignUsersData.class);
		return list;
	}
	/**
	 * 认领用户：将JSON转成list
	 * @return List<ClaimUsersData>
	 */
	public List<ClaimUsersData> getClaimUsersList() {
		if(StringUtils.isEmpty(claimUsersParams)) {
			return null;
		}
		List<ClaimUsersData> list = JSON.parseArray(claimUsersParams, ClaimUsersData.class);
		return list;
	}
	/**
	 * 认领组：将JSON转成list
	 * @return List<ClaimGroupData>
	 */
	public List<ClaimGroupData> getClaimGroupList() {
		if(StringUtils.isEmpty(claimGroupParams)) {
			return null;
		}
		List<ClaimGroupData> list = JSON.parseArray(claimGroupParams, ClaimGroupData.class);
		return list;
	}

	/**
	 * 认领组：将JSON转成list
	 * @return List<ClaimGroupData>
	 */
	public List<FormData> getSmartFormPropertyList() {
		if(StringUtils.isEmpty(formDataList)) {
			return null;
		}
		List<FormData> list = JSON.parseArray(formDataList, FormData.class);
		return list;
	}
}
