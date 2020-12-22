package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: TaskClaimGroupInfo 
* @Description: 任务候选组信息
* @author KaminanGTO
* @date 2019年1月8日 下午3:49:04 
*  
*/
public class TaskClaimGroupInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields claimName : 候选任务名信息
	*/ 
	private String claimName;
	
	/** 
	* @Fields groupId : 候选组id
	*/ 
	private String groupId;

	public String getClaimName() {
		return claimName;
	}

	public void setClaimName(String claimName) {
		this.claimName = claimName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
}
