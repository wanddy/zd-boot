package workflow.business.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/** 
* @ClassName: TaskSampleData 
* @Description: 流程任务简单数据
* @author KaminanGTO
* @date 2018年9月11日 下午12:13:12 
*  
*/
public class TaskSampleData implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = -6036669520536520239L;

	/** 
	* @Fields id : 任务ID
	*/ 
	private String id;
	
	/** 
	* @Fields taskDefId : 任务定义ID
	*/ 
	private String taskDefId;
	
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
	* @Fields businessValue : 业务参数，目前存放URL地址
	*/ 
	private String businessValue;
	
	/** 
	* @Fields businessExpired : 业务过期时间，使用自定义变量存放（单位小时）
	*/ 
	private int businessExpired;
	
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
	* @Fields judgeList : 判断型变量列表
	*/ 
	private List<JudgeProperty> judgeList;
	
	/** 
	* @Fields assignee : 操作者
	*/ 
	private String assignee;

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

	public List<JudgeProperty> getJudgeList() {
		return judgeList;
	}

	public void setJudgeList(List<JudgeProperty> judgeList) {
		this.judgeList = judgeList;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	
	
}
