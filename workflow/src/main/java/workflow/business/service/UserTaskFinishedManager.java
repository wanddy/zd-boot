package workflow.business.service;

import workflow.common.error.WorkFlowException;
import workflow.business.model.PageProcessTaskList;
import workflow.business.model.PageUnitFinishedTaskList;
import workflow.business.model.PageUserFinishedTaskList;

public interface UserTaskFinishedManager {
	
	/** 
	* @Title: getUserFinishTaskList 
	* @Description: 获取用户任务管理信息
	* @param userName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageUserFinishTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageUserFinishedTaskList getUserFinishedTaskList(String userName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException;
	
	/** 
	* @Title: getFinishedTasksByUser 
	* @Description: 获取用户已完成任务列表
	* @param userId
	* @param processDefId
	* @param taskName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageProcessTaskList getFinishedTasksByUser(String userId, String processDefId, String taskName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException;
	
	/** 
	* @Title: getUnitFinishedTaskList 
	* @Description: 获取部门任务管理信息
	* @param unitName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageUnitFinishedTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageUnitFinishedTaskList getUnitFinishedTaskList(String unitName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException;
	
	/** 
	* @Title: getFinishedTasksByUnit 
	* @Description: 获取部门已完成任务列表
	* @param unitId
	* @param processDefId
	* @param taskName
	* @param userName
	* @param pageNum
	* @param pageSize
	* @return  参数说明 
	* @return PageProcessTaskList    返回类型 
	 * @throws WorkFlowException 
	* 
	*/
	PageProcessTaskList getFinishedTasksByUnit(String unitId, String processDefId, String taskName, String userName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException;
	

}
