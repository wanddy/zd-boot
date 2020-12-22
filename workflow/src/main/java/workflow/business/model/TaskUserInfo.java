package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: TaskSignUserInfo 
* @Description: 会签用户信息
* @author KaminanGTO
* @date 2018年10月30日 下午7:02:46 
*  
*/
public class TaskUserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields signName : 会签名称
	*/ 
	private String signName;

	/** 
	* @Fields userId : 用户ID
	*/ 
	private String userId;
	
	/** 
	* @Fields userName : 用户姓名
	*/ 
	private String userName;
	
	/** 
	* @Fields unitId : 用户部门ID
	*/ 
	private String unitId;
	
	/** 
	* @Fields unitName : 用户部门 名称
	*/ 
	private String unitName;
	
	public String getSignName() {
		return signName;
	}

	public void setSignName(String signName) {
		this.signName = signName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
