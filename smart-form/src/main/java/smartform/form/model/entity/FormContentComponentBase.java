package smartform.form.model.entity;

import java.io.Serializable;

/** 
* @ClassName: FormContentComponentBase 
* @Description: 用于表单内容组件表.表格表基类
* @author quhanlin
* @date 2018年11月1日 下午5:59:44
*/
public class FormContentComponentBase implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 表单组件表id
	 */
	private String id;
	
	/**
	 * 表单内容主表id
	 */
	private String contentId;
	
	/**
	 * 业务类型，当表名相同时用于区分识别组件
	 */
	private Integer workType;
	
	/**
	 * 创建时间
	 */
	private Long createdAt;
	
	/**
	 * 修改时间
	 */
	private Long modifiedAt;

	@Override
	public String toString() {
		return "FormContentComponentBase [id=" + id + ", contentId=" + contentId + ", workType=" + workType
				+ ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Integer getWorkType() {
		return workType;
	}

	public void setWorkType(Integer workType) {
		this.workType = workType;
	}
	
}
