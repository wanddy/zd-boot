package smartform.form.model.entity;

import java.io.Serializable;

/** 
* @ClassName: ComponentStateEntity 
* @Description: 用于超级组件状态存储
* @author quhanlin
* @date 2018年11月1日 下午5:59:44 
*/
public class ComponentStateEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 表单内容表id
	 */
	private String contentId;
	
	/**
	 * 超级组件表名
	 */
	private String tableName;
	
	/**
	 * 业务类型，当表名相同时用于区分识别组件
	 */
	private Integer workType;
	
	/**
	 * 退回原因
	 */
	private String refuseInfo;
	
	/**
	 * 1,暂存;2,提交中;3,已提交;4,退回;
	 */
	private Integer state;
	
	/**
	 * 创建时间
	 */
	private Long createdAt;
	
	/**
	 * 修改时间
	 */
	private Long modifiedAt;
	
	/**
	 * 存储的表名，用于mybaite表名拼接
	 */
	private String dbTable;
	
	/** 
	* @Fields pageId : 状态对应的分页id
	*/ 
	private String pageId;

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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getWorkType() {
		return workType;
	}

	public void setWorkType(Integer workType) {
		this.workType = workType;
	}

	public String getRefuseInfo() {
		return refuseInfo;
	}

	public void setRefuseInfo(String refuseInfo) {
		this.refuseInfo = refuseInfo;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
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

	@Override
	public String toString() {
		return "ComponentStateEntity [id=" + id + ", contentId=" + contentId + ", tableName=" + tableName
				+ ", workType=" + workType + ", refuseInfo=" + refuseInfo + ", state=" + state + ", createdAt="
				+ createdAt + ", modifiedAt=" + modifiedAt + "]";
	}

	public String getDbTable() {
		return dbTable;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
}
