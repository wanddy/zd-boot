package workflow.business.model;

import java.io.Serializable;


/**
 * 
 */
public class TaskContentData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2651279029050391277L;

	/**
	 * 事项分类
	 */
	private String categoryType;
	
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
	

	public TaskContentData() {
		super();
	}
	
    /**
     * setter for categoryType
     * @param categoryType
     */
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

    /**
     * getter for categoryType
     */
	public String getCategoryType() {
		return categoryType;
	}
	
    /**
     * setter for taskType
     * @param taskType
     */
	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

    /**
     * getter for taskType
     */
	public Integer getTaskType() {
		return taskType;
	}
	
    /**
     * setter for accessScope
     * @param accessScope
     */
	public void setAccessScope(String accessScope) {
		this.accessScope = accessScope;
	}

    /**
     * getter for accessScope
     */
	public String getAccessScope() {
		return accessScope;
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
     * setter for contentName
     * @param contentName
     */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

    /**
     * getter for contentName
     */
	public String getContentName() {
		return contentName;
	}
	
    /**
     * setter for taskNo
     * @param taskNo
     */
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

    /**
     * getter for taskNo
     */
	public String getTaskNo() {
		return taskNo;
	}
}
