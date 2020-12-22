package workflow.business.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: TaskSignUserData 
* @Description: 任务会签用户数据
* @author KaminanGTO
* @date 2018年10月30日 下午6:40:15 
*  
*/
public class TaskSignUserData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields id : 会签用户数据id
	*/ 
	private String id;
	
	/** 
	* @Fields name : 用钱用户数据名称
	*/ 
	private String name;
	
	/** 
	* @Fields userList : 会签用户列表
	*/ 
	private List<TaskUserInfo> userList;

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

	public List<TaskUserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<TaskUserInfo> userList) {
		this.userList = userList;
	}

	
}
