package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: TasksInfo 
* @Description: 任务组信息--根据用户ID查询所有代办数量时使用
* @author KaminanGTO
* @date 2018年11月22日 上午10:02:29 
*  
*/
public class TasksInfo implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = -8058847552764436597L;

	/** 
	* @Fields processDefinitionId : 流程定义Key
	*/ 
	private String processDefinitionKey;
	
	/** 
	* @Fields processDefinitionName : 流程定义名称
	*/ 
	private String processDefinitionName;
	
	/** 
	* @Fields processBusinessType : 流程业务类型
	*/ 
	private String processBusinessType;
	
	/** 
	* @Fields name : 任务名称
	*/ 
	private String name;
	
	/** 
	* @Fields taskDefId : 任务定义ID
	*/ 
	private String taskDefId;
	
	/** 
	* @Fields businessValue : 业务参数，目前存放URL地址
	*/ 
	private String businessValue;
	
	/** 
	* @Fields count : 任务数量
	*/ 
	private int count;
	
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public String getProcessBusinessType() {
		return processBusinessType;
	}

	public void setProcessBusinessType(String processBusinessType) {
		this.processBusinessType = processBusinessType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}
	
	public String getBusinessValue() {
		return businessValue;
	}

	public void setBusinessValue(String businessValue) {
		this.businessValue = businessValue;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
