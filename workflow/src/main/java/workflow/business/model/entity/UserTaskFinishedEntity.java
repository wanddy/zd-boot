package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户完成记录表
 */
@TableName("wf_user_task_finished")
public class UserTaskFinishedEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程id
	 */
	private String processId;
	
	/**
	 * 流程key
	 */
	private String processKey;
	
	/**
	 * 流程名称
	 */
	private String processName;
	
	/**
	 * 流程实例id
	 */
	private String processInstanceId;
	
	/**
	 * 流程任务id
	 */
	private String taskId;
	
	/**
	 * 任务名称
	 */
	private String taskName;
	
	/** 
	* @Fields taskDefId : 任务定义ID
	*/ 
	private String taskDefId;
	
	/**
	 * 完成时间
	 */
	private Date finishTime;
	
	/**
	 * 完成部门id
	 */
	private String unitId;
	
	/**
	 * 完成部门名称
	 */
	private String unitName;
	
	/**
	 * 完成用户id
	 */
	private String userId;
	
	/**
	 * 完成用户姓名
	 */
	private String userName;
	

	public UserTaskFinishedEntity() {
		super();
	}

    /**
     * setter for processId
     * @param processId
     */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

    /**
     * getter for processId
     */
	public String getProcessId() {
		return processId;
	}
	
    /**
     * setter for processKey
     * @param processKey
     */
	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

    /**
     * getter for processKey
     */
	public String getProcessKey() {
		return processKey;
	}
	
    /**
     * setter for processName
     * @param processName
     */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

    /**
     * getter for processName
     */
	public String getProcessName() {
		return processName;
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
	
    /**
     * setter for taskId
     * @param taskId
     */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

    /**
     * getter for taskId
     */
	public String getTaskId() {
		return taskId;
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
	
    public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	/**
     * setter for finishTime
     * @param finishTime
     */
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

    /**
     * getter for finishTime
     */
	public Date getFinishTime() {
		return finishTime;
	}
	
    /**
     * setter for unitId
     * @param unitId
     */
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

    /**
     * getter for unitId
     */
	public String getUnitId() {
		return unitId;
	}
	
    /**
     * setter for unitName
     * @param unitName
     */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

    /**
     * getter for unitName
     */
	public String getUnitName() {
		return unitName;
	}
	
    /**
     * setter for userId
     * @param userId
     */
	public void setUserId(String userId) {
		this.userId = userId;
	}

    /**
     * getter for userId
     */
	public String getUserId() {
		return userId;
	}
	
    /**
     * setter for userName
     * @param userName
     */
	public void setUserName(String userName) {
		this.userName = userName;
	}

    /**
     * getter for userName
     */
	public String getUserName() {
		return userName;
	}
	
}