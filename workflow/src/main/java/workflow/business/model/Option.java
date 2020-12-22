package workflow.business.model;

import java.util.List;

/** 
* @ClassName: Option 
* @Description: 选项列表
* @author hou
* @date 2018年9月9日 下午2:43:25 
*  
*/
public class Option extends BaseData {
	/**
	 * ID
	 */
	private String id;
	/**
	 * 选项源_ID
	 */
	private String sourceID;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 值
	 */
	private String value;
	/**
	 * 文本
	 */
	private String label;
	/**
	 * 父ID
	 */
	private String parentID;
	/**
	 * 子选项列表
	 */
	private List<Option> options;
	/**
	 * 创建时间
	 */
	private java.util.Date createdAt;
	/**
	 * 修改时间
	 */
	private java.util.Date modifiedAt;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}
	public java.util.Date getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(java.util.Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	@Override
	public String toString() {
		return "Option [id=" + id + ", sourceID=" + sourceID + ", sort=" + sort + ", value=" + value + ", label="
				+ label + ", parentID=" + parentID + ", options=" + options + ", createdAt=" + createdAt
				+ ", modifiedAt=" + modifiedAt + "]";
	}
}
