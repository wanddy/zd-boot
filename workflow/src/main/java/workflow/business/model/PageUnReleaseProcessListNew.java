package workflow.business.model;

import java.io.Serializable;
import java.util.List;

public class PageUnReleaseProcessListNew implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// # 总共有多少条数据
	private Integer total;
	// # 当前页号
	private Integer nowPage;
	// # 每页显示多少条数据
	private Integer pageSize;
	// # 每页显示多少条数据
	private Integer pageNum;
	// # 分页列表
	private List<UnReleaseProcessData> rows;

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

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public List<UnReleaseProcessData> getRows() {
		return rows;
	}

	public void setRows(List<UnReleaseProcessData> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "SmartFormPagination [total=" + total + ", nowPage=" + nowPage + ", pageSize=" + pageSize + ", rows="
				+ rows + "]";
	}
}
