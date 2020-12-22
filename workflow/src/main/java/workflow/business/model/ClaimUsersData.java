package workflow.business.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: ClaimUsersData 
* @Description: 候选用户数据
* @author KaminanGTO
* @date 2018年12月4日 下午7:33:39 
*  
*/
public class ClaimUsersData implements Serializable{

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 候选用户id
	*/ 
	private String id;
	
	/** 
	* @Fields name : 候选用户名称
	*/ 
	private String name;
	
	/** 
	* @Fields userlist : 用户列表--老接口数据
	*/ 
	private List<UserInfo> userList;
	
	/** 
	* @Fields userDefList : 用户定义列表--老接口数据
	*/ 
	private List<UserInfo> userDefList;
	
	/** 
	* @Fields userlist : 用户id列表
	*/ 
	private List<String> users;
	
	/** 
	* @Fields userDefList : 用户定义id列表
	*/ 
	private List<String> defUsers;
	
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

	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}

	public List<UserInfo> getUserDefList() {
		return userDefList;
	}

	public void setUserDefList(List<UserInfo> userDefList) {
		this.userDefList = userDefList;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<String> getDefUsers() {
		return defUsers;
	}

	public void setDefUsers(List<String> defUsers) {
		this.defUsers = defUsers;
	}
	
	
}
