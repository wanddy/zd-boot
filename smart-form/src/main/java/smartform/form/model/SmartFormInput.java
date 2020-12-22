package smartform.form.model;

import smartform.common.model.BaseData;

//选项源列表查询参数
public class SmartFormInput extends BaseData{
	private Integer nowPage;
	private Integer pageSize;
	private String name;
	private String categoryId;
	private Integer state;
	// # 指定的排序字段，日期，状态
	private Integer sortprop;
	// # false: descending降序: true: ascending升序
	private Boolean order;

	public Integer getNowPage() {
		return nowPage;
	}

	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getSortprop() {
		return sortprop;
	}

	public void setSortprop(Integer sortprop) {
		this.sortprop = sortprop;
	}

	public Boolean getOrder() {
		return order;
	}

	public void setOrder(Boolean order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "SmartFormInput [nowPage=" + nowPage + ", pageSize=" + pageSize + ", name=" + name + ", categoryId="
				+ categoryId + ", state=" + state + ", sortprop=" + sortprop + ", order=" + order + "]";
	}
}
