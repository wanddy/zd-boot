package workflow.business.model;

import java.io.Serializable;
import java.util.Date;

/** 
* @ClassName: SubProcessInstanceData 
* @Description: 子流程实例数据
* @author KaminanGTO
* @date 2018年10月25日 下午3:47:57 
*  
*/
public class SubProcessInstanceData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields id : 流程实例ID
	*/ 
	private String id;
	
	/** 
	* @Fields processDefId : 流程定义ID
	*/ 
	private String processDefId;
	
	/** 
	* @Fields name : 流程名称
	*/ 
	private String name;
	
	/** 
	* @Fields startTime : 流程实例开始时间
	*/ 
	private Date startTime;
	
	/** 
	* @Fields endTime : 流程实例结束时间
	*/ 
	private Date endTime;
	
	/** 
	* @Fields deleteReason : 流程实例删除原因（流程强制结束时赋值）
	*/ 
	private String deleteReason;
	
	/** 
	* @Fields parentProcessInstanceId : 父流程实例ID
	*/ 
	private String parentProcessInstanceId;
	
	/** 
	* @Fields state : 流程实例状态，0已暂停 1进行中 2已完成
	*/ 
	private int state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public String getParentProcessInstanceId() {
		return parentProcessInstanceId;
	}

	public void setParentProcessInstanceId(String parentProcessInstanceId) {
		this.parentProcessInstanceId = parentProcessInstanceId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	
	
}
