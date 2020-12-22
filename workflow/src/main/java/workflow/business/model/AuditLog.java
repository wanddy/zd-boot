package workflow.business.model;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("wf_audit_log")
public class AuditLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2810685568555559578L;

	/**
	 * UUID，32位
	 */
	private String id;
	
	/**
	 * 审批事项id
	 */
	private String contentId;
	
	/**
	 * 流程实例id
	 */
	private String processInstanceId;
	
	/**
	 * 操作人部门
	 */
	private String unitName;
	
	/**
	 * 操作人
	 */
	private String userName;
	
	/**
	 * 操作时间yyyy-mm-dd hh:mm:ss
	 */
	private Long operateTime;
	
	/**
	 * 业务节点
	 */
	private String businessNode;
	
	/**
	 * 业务操作
	 */
	private String businessOperate;
	
	/**
	 * 备注
	 */
	private String memo;

	/**
	 * 流程发起人
	 */
	private String processInitiator;
	
	/**
	 * 审核时上传的附件
	 */
	private String auditFileUrl;

	/**
	 * 任务定义Id
	 */
	private String taskDefId;
	
	/**
	 * 父任务定义Id
	 */
	private String parentTaskDefId;

	/**
	 * 审核时上传的附件名称
	 */
	private String auditFileName;
	
	/*
	 * 称谓
	 * */
	private String appellation;

	public String getAppellation() {
		return appellation;
	}

	public void setAppellation(String appellation) {
		this.appellation = appellation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Long operateTime) {
		this.operateTime = operateTime;
	}

	public String getBusinessNode() {
		return businessNode;
	}

	public void setBusinessNode(String businessNode) {
		this.businessNode = businessNode;
	}

	public String getBusinessOperate() {
		return businessOperate;
	}

	public void setBusinessOperate(String businessOperate) {
		this.businessOperate = businessOperate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getProcessInitiator() {
		return processInitiator;
	}

	public void setProcessInitiator(String processInitiator) {
		this.processInitiator = processInitiator;
	}

	public String getAuditFileUrl() {
		return auditFileUrl;
	}

	public void setAuditFileUrl(String auditFileUrl) {
		this.auditFileUrl = auditFileUrl;
	}

	public String getTaskDefId() {
		return taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	public String getParentTaskDefId() {
		return parentTaskDefId;
	}

	public void setParentTaskDefId(String parentTaskDefId) {
		this.parentTaskDefId = parentTaskDefId;
	}

	public String getAuditFileName() {
		return auditFileName;
	}

	public void setAuditFileName(String auditFileName) {
		this.auditFileName = auditFileName;
	}
	
}
