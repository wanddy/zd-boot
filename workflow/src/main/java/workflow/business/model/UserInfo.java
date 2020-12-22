package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: UserInfo 
* @Description: 用户信息
* @author KaminanGTO
* @date 2018年9月11日 下午12:51:33 
*  
*/
public class UserInfo implements Serializable{
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 6614860798872618344L;

	/** 
	* @Fields id : 用户id
	*/ 
	private String id;
	
	/** 
	* @Fields nick : 用户昵称
	*/ 
	private String nick;
	
	/** 
	* @Fields unitId : 部门ID
	*/ 
	private String unitId;
	
	/** 
	* @Fields unitName : 部门名字
	*/ 
	private String unitName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	
}
