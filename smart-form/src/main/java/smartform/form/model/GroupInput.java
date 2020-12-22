package smartform.form.model;


/** 
* @ClassName: GroupInput 
* @Description: 超级组件查询input
* @author quhanlin
* @date 2018年10月25日 上午10:06:39 
*  
*/
public class GroupInput{
	private Integer nowPage;
	private Integer pageSize;
	private String name;
	private Integer state;
	private String categoryId;

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

	public void setCategoryId(String categoryID) {
		this.categoryId = categoryID;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
