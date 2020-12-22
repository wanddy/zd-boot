package workflow.business.model;

import java.io.Serializable;
import java.util.Date;

/** 
* @ClassName: UserReplaceInfo 
* @Description: 顶替用户数据
* @author KaminanGTO
* @date 2019年1月3日 上午9:51:35 
*  
*/
public class UserReplaceInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields replaceUserList : 被顶替者
	*/ 
	private String replaceUser;
	
	/** 
	* @Fields startTime : 顶替开始时间
	*/ 
	private Date startTime;
	
	/** 
	* @Fields endTime : 顶替结束时间
	*/ 
	private Date endTime;

	public String getReplaceUser() {
		return replaceUser;
	}

	public void setReplaceUser(String replaceUser) {
		this.replaceUser = replaceUser;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}
