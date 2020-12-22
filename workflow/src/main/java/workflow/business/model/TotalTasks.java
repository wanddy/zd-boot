package workflow.business.model;

import java.io.Serializable;


public class TotalTasks implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5505998653042581057L;

	/** 
	* @Fields processKey : 流程key
	*/ 
	private String processKey;
	
	/** 
	* @Fields processDefinitionId : 流程定义Key
	*/ 
	private String processDefinitionKey;
	
	/** 
	* @Fields processDefinitionName : 流程定义名称
	*/ 
	private String processDefinitionName;
	
	/** 
	* @Fields count : 任务数量
	*/ 
	private int counts;

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

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

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}
	
	
}
