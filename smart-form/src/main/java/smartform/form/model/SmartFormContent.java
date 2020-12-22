package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/**
 * 表单内容表：SmartFormContent
 */
public class SmartFormContent extends SmartFormBase implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 表单id
	 */
	private String formId;
	
//	/**
//	 * 填表用户id
//	 */
//	@Deprecated
//	private String userId;
	
	/**
	 * 业务类型(1.指南)
	 */
	private Integer workType;
	
	/**
	 * 业务ID
	 */
	private String workId;
	
	/**
	 * 工作流id
	 */
	@Deprecated
	private String processId;
	
	/**
	 * 1,暂存;2,保存;3,锁定 //提交中;3,已提交;4,退回;
	 */
	private Integer state;
	
	/**
	 * 退回原因
	 */
	@Deprecated
	private String reasonInfo;
	
	/**
	 * 字段列表（用于存放查询部分字段时的字段数据）
	 */
	private List<FormFieldBase> fieldList;
	
	/**
	 * 结束时间
	 */
	private Long endDate;
	
//	/**
//	 * 内容数据列表
//	 */
//	private List<Object> fieldList;

	/**
	 * 创建时间
	 */
	private Long createdAt;
	/**
	 * 修改时间
	 */
	private Long modifiedAt;
	
	/**
	 * 提交时间
	 */
	@Deprecated
	private Long submitTime;
	
	public SmartFormContent() {
		super();
	}
	
    /**
     * setter for formId
     * @param formId
     */
	public void setFormId(String formId) {
		this.formId = formId;
	}

    /**
     * getter for formId
     */
	public String getFormId() {
		return formId;
	}
	
//    /**
//     * setter for userId
//     * @param userId
//     */
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
//
//    /**
//     * getter for userId
//     */
//	public String getUserId() {
//		return userId;
//	}
	
    /**
     * setter for workType
     * @param workType
     */
	public void setWorkType(Integer workType) {
		this.workType = workType;
	}

    /**
     * getter for workType
     */
	public Integer getWorkType() {
		return workType;
	}
	
    /**
     * setter for workId
     * @param workId
     */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

    /**
     * getter for workId
     */
	public String getWorkId() {
		return workId;
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
     * setter for state
     * @param state
     */
	public void setState(Integer state) {
		this.state = state;
	}

    /**
     * getter for state
     */
	public Integer getState() {
		return state;
	}
	
	public String getReasonInfo() {
		return reasonInfo;
	}

	public void setReasonInfo(String reasonInfo) {
		this.reasonInfo = reasonInfo;
	}

	@Override
	public String toString() {
		return "SmartFormContent [formId=" + formId + ", workType=" + workType + ", workId="
				+ workId + ", processId=" + processId + ", state=" + state + ", reasonInfo=" + reasonInfo
				+ ", fieldList=" + fieldList + ", endDate=" + endDate + ", createdAt=" + createdAt + ", modifiedAt="
				+ modifiedAt + ", submitTime=" + submitTime + "]";
	}

	public List<FormFieldBase> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<FormFieldBase> fieldList) {
		this.fieldList = fieldList;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitAt) {
		this.submitTime = submitAt;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}


	
}
