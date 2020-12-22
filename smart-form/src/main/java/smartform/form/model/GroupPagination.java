package smartform.form.model;

import auth.entity.Category;

import java.io.Serializable;
import java.util.List;

public class GroupPagination implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 总共有多少条数据
	 */
	private Integer total;
	/**
	 * 当前页号
	 */
	private Integer nowPage;
	/**
	 * 每页显示多少条数据
	 */
	private Integer pageSize;
	/**
	 * 组的列表
	 */
	private List<Group> rows;
	/**
	 * 选项源列表
	 */
	private List<Category> optionsList;
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
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
	public List<Group> getRows() {
		return rows;
	}
	public void setRows(List<Group> rows) {
		this.rows = rows;
	}

	public List<Category> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<Category> optionsList) {
		this.optionsList = optionsList;
	}

	@Override
	public String toString() {
		return "GroupPagination [total=" + total + ", nowPage=" + nowPage + ", pageSize=" + pageSize + ", rows=" + rows
				+ ", optionsList=" + optionsList + "]";
	}
}
