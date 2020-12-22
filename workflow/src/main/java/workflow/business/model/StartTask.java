package workflow.business.model;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 */
public class StartTask implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2081434769767866726L;
	/**
	 * 操作人
	 */
	private String userId;
	/**
	 * 流程key
	 */
	private String processKey;
	/**
	 * 业务key
	 */
	private String businessKey;
	
	/**
	 * 业务数据
	 */
	private TaskContentData data;

	/**
	 * 错误信息code
	 */
	private String errorCode;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	/**
	 * 操作时间
	 */
	private Date createdAt;

	public StartTask() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public TaskContentData getData() {
		return data;
	}

	public void setData(TaskContentData data) {
		this.data = data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
}
