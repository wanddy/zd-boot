package workflow.business.model;

import java.util.Date;
import java.util.List;

/** 
* @ClassName: OptionSource 
* @Description: 选项源
* @author hou
* @date 2018年9月9日 下午2:44:07 
*  
*/
public class OptionSource {
	/**
	 * ID
	 */
	private String id;
	/**
	 * 分类ID
	 */
	private String categoryID;
	/**
	 * 是否是单级
	 */
	private Boolean single;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 源名称
	 */
	private String name;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;
	/**
	 * 状态,0,删除;1,发布;2.新建
	 */
	private Integer state;
	/**
	 * 选项列表
	 */
	private List<Option> options;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}
	public Boolean getSingle() {
		return single;
	}
	public void setSingle(Boolean single) {
		this.single = single;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	@Override
	public String toString() {
		return "OptionSource [id=" + id + ", categoryID=" + categoryID + ", single=" + single + ", sort=" + sort
				+ ", name=" + name + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", state=" + state
				+ ", options=" + options + "]";
	}
}
