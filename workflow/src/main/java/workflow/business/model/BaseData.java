package workflow.business.model;

/** 
* @ClassName: BaseDataModel 
* @Description: 所有需要持久化的实体基类
* @author quhanlin
* @date 2018年9月12日 上午10:35:41 
*  
*/
public abstract class BaseData {
	/** 
	* @Fields id : 实体的ID
	*/ 
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
