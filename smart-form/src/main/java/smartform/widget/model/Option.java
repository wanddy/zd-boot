package smartform.widget.model;

import smartform.common.model.BaseData;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: Option
 * @Description: 选项
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class Option extends BaseData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 选项源_ID
	 */
	private String sourceId;
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
	private String parentId;
	/**
	 * 子选项列表
	 */
	private List<Option> children;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<Option> getChildren() {
		return children;
	}

	public void setChildren(List<Option> children) {
		this.children = children;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	@Override
	public String toString() {
		return "Option [id=" + getId() + ", sourceID=" + sourceId + ", sort=" + sort + ", value=" + value + ", label="
				+ label + ", parentID=" + parentId + ", options=" + children + ", createdAt=" + createdAt
				+ ", modifiedAt=" + modifiedAt + "]";
	}
}