package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: UnitFinishTaskData 
* @Description: 部门完成任务数据
* @author KaminanGTO
* @date 2018年11月27日 下午5:42:20 
*  
*/
public class UnitFinishedTaskData implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
