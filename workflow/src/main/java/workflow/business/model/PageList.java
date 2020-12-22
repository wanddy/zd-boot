package workflow.business.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: PageList 
* @Description: 通用列表数据
* @author KaminanGTO
* @date 2018年9月11日 上午11:55:02 
*  
*/
public class PageList<T> implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 2246748039424720995L;

	/** 
	* @Fields rows : 列表数据
	 *
	 *Mybatis-Plus：Records
	*/ 
	private List<T> rows;
	
	/** 
	* @Fields total : 总行数
	 *
	 * Mybatis-Plus：Total
	*/ 
	private int total;
	
	/** 
	* @Fields pageSize : 每页数量
	 *
	 * Mybatis-Plus：Size
	*/ 
	private int pageSize;
	
	/** 
	* @Fields pageNum : 当前页码
	 *
	 * Mybatis-Plus：current
	*/ 
	private int pageNum;

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
	
}
