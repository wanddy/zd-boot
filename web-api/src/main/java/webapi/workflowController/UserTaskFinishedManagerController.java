package webapi.workflowController;

import workflow.business.model.PageProcessTaskList;
import workflow.business.model.PageUnitFinishedTaskList;
import workflow.business.model.PageUserFinishedTaskList;
import workflow.business.service.UserTaskFinishedManager;
import workflow.common.error.WorkFlowException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@Service("stcsmUserTaskFinishedService")
@Controller
@RestController
@RequestMapping(value = "/workflow/userTaskManager")
public class UserTaskFinishedManagerController {
	@Autowired
	private UserTaskFinishedManager userTaskFinishedManager;

	/**
	 * @param userName
	 * @param pageNum
	 * @param pageSize
	 * @return PageUserFinishTaskList    返回类型
	 * @throws WorkFlowException
	 * @Title: getUserFinishTaskList
	 * @Description: 获取用户任务管理信息
	 */
	@ResponseBody
	@ApiOperation("获取用户任务管理信息")
	@RequestMapping(value = "/getUserFinishedTaskList")
	PageUserFinishedTaskList getUserFinishedTaskList(String userName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException {
		return userTaskFinishedManager.getUserFinishedTaskList(userName, pageNum, pageSize, orderByCount);
	}

	/**
	 * @param userId
	 * @param processDefId
	 * @param taskName
	 * @param pageNum
	 * @param pageSize
	 * @return PageProcessTaskList    返回类型
	 * @throws WorkFlowException
	 * @Title: getFinishedTasksByUser
	 * @Description: 获取用户已完成任务列表
	 */
	@ResponseBody
	@ApiOperation("获取用户已完成任务列表")
	@RequestMapping(value = "/getFinishedTasksByUser")
	PageProcessTaskList getFinishedTasksByUser(String userId, String processDefId, String taskName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException {
		return userTaskFinishedManager.getFinishedTasksByUser(userId, processDefId, taskName, pageNum, pageSize, orderByFinishedTime);
	}

	/**
	 * @param unitName
	 * @param pageNum
	 * @param pageSize
	 * @return PageUnitFinishedTaskList    返回类型
	 * @throws WorkFlowException
	 * @Title: getUnitFinishedTaskList
	 * @Description: 获取部门任务管理信息
	 */
	@ResponseBody
	@ApiOperation("获取部门任务管理信息")
	@RequestMapping(value = "/getUnitFinishedTaskList")
	PageUnitFinishedTaskList getUnitFinishedTaskList(String unitName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException {
		return userTaskFinishedManager.getUnitFinishedTaskList(unitName, pageNum, pageSize, orderByCount);
	}

	/**
	 * @param unitId
	 * @param processDefId
	 * @param taskName
	 * @param userName
	 * @param pageNum
	 * @param pageSize
	 * @return PageProcessTaskList    返回类型
	 * @throws WorkFlowException
	 * @Title: getFinishedTasksByUnit
	 * @Description: 获取部门已完成任务列表
	 */
	@ResponseBody
	@ApiOperation("获取部门已完成任务列表")
	@RequestMapping(value = "/getFinishedTasksByUnit")
	PageProcessTaskList getFinishedTasksByUnit(String unitId, String processDefId, String taskName, String userName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException {
		return userTaskFinishedManager.getFinishedTasksByUnit(unitId, processDefId, taskName, userName, pageNum, pageSize, orderByFinishedTime);
	}


}
