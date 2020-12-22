package workflow.business.model;

import java.io.Serializable;


/**
 * 
 */
public class TaskStatus implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4129495433375858289L;

	/**
	 * 
	 */
	private String id;
	
	/**
	 * 业务id
	 */
	private String contentId;
	
	/**
	 * 流程实例id
	 */
	private String processInstanceId;
	
	/**
	 * 任务定义id
	 */
	private String taskDefId;
	
	/**
	 * 任务节点名称
	 */
	private String taskName;
	
	/**
	 * 流程实例是否已终止 0未 1已终止
	 */
	private Integer isActivce;
	
    /**
     * setter for id
     * @param id
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * getter for id
     */
	public String getId() {
		return id;
	}
	
    /**
     * setter for contentId
     * @param contentId
     */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

    /**
     * getter for contentId
     */
	public String getContentId() {
		return contentId;
	}
	
    /**
     * setter for processInstanceId
     * @param processInstanceId
     */
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

    /**
     * getter for processInstanceId
     */
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	
    public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	/**
     * setter for taskName
     * @param taskName
     */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

    /**
     * getter for taskName
     */
	public String getTaskName() {
		return taskName;
	}
	
    /**
     * setter for isActivce
     * @param isActivce
     */
	public void setIsActivce(Integer isActivce) {
		this.isActivce = isActivce;
	}

    /**
     * getter for isActivce
     */
	public Integer getIsActivce() {
		return isActivce;
	}
	
	/**
	 * 实现InsertOrUpdate
	 * 用于insert语句前 <selectKey>
	 */
	private Integer count;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
