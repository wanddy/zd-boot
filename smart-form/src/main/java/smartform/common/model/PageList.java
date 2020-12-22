package smartform.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: PageList
 * @Description: 分页数据实体
 * @author Admin
 * @date 2017年6月27日 下午11:27:29
 * 
 */
public class PageList<T extends BaseData> implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * @Fields row : 列表
	 */
	private List<T> rows;
	/**
	 * @Fields total : 总行数
	 */
	private Long total;
	/**
	 * @Fields pageSize : 每页数量
	 */
	private Integer pageSize;
	/**
	 * @Fields pageNum : 当前页码
	 */
	private Integer pageNum;
	
	
	
	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public PageList(int pageSize, int pageNum)
	{
		this.rows = new ArrayList<T>();
		this.total = 0L;
		this.pageSize = pageSize;
		this.pageNum = pageNum;
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public PageList(long total, int pageSize, int pageNum, List<T> rows) {
		this.rows = rows;
		this.total = total;
		this.pageSize = pageSize;
		this.pageNum = pageNum;
	}
	
}
