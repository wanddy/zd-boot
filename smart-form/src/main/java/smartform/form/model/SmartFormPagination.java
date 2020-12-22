package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: SmartFormPagination 
* @Description: 表单分页对象
* @author quhanlin
* @date 2018年10月29日 下午2:27:36 
*  
*/
public class SmartFormPagination implements Serializable {
	
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
	// # 分页列表
	private List<SmartForm> rows;

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

	public List<SmartForm> getRows() {
		return rows;
	}

	public void setRows(List<SmartForm> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "SmartFormPagination [total=" + total + ", nowPage=" + nowPage + ", pageSize=" + pageSize + ", rows="
				+ rows + "]";
	}
}
