package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: ClaimGroupData 
* @Description: 候选组数据
* @author KaminanGTO
*  
*/
public class ClaimGroupData implements Serializable{

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 候选组id
	*/ 
	private String id;
	
	/** 
	* @Fields name : 候选组名称
	*/ 
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
