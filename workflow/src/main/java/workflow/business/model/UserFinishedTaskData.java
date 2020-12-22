package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: UserFinishTaskData 
* @Description: 用户完成任务数据
* @author KaminanGTO
* @date 2018年11月27日 下午5:39:53 
*  
*/
public class UserFinishedTaskData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields userId : 用户ID
	*/ 
	public String userId;
	
	/** 
	* @Fields userName : 用户姓名
	*/ 
	public String userName;
	
	/** 
	* @Fields unitId : 部门ID 
	*/ 
	public String unitId;
	
	/** 
	* @Fields unitName : 部门名称
	*/ 
	public String unitName;
	
	/** 
	* @Fields finishedCount : 完成任务数
	*/ 
	public int finishedCount;

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

	public int getFinishedCount() {
		return finishedCount;
	}

	public void setFinishedCount(int finishedCount) {
		this.finishedCount = finishedCount;
	}
	
	
	
}
