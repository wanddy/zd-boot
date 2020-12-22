package webapi.workflowController;

import workflow.business.model.*;
import workflow.business.service.AuditLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.common.error.WorkFlowException;
import io.swagger.annotations.ApiOperation;
import workflow.olddata.model.PageInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 
 */
//@Service("auditLogService")
@Controller
@RestController
@RequestMapping(value = "/workflow/auditLog")
public class AuditLogServiceController {
	@Autowired
	private AuditLogService auditLogService;

	/**
	 * 分页查询
	 *
	 * @param params
	 * @param page
	 * @return
	 */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/pageList")
	Page<AuditLog> pageList(AuditLog params, PageInput page) {
		return auditLogService.pageList(params, page);
	}

	/**
	 * 条件查询查询
	 *
	 * @param params
	 * @return
	 */
	@ResponseBody
	@ApiOperation("条件查询查询")
	@RequestMapping(value = "/listAll")
	List<AuditLog> listAll(AuditLog params) {
		return auditLogService.listAll(params);
	}

	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param userName    操作人
	 * @param deptName    操作人所属部门
	 * @param opResult    操作结果
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @return
	 */
	@ResponseBody
	@ApiOperation("完成待办任务后")
	@RequestMapping(value = "/auditTask")
	int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList){
		return auditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList);
	}

	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param userName    操作人
	 * @param deptName    操作人所属部门
	 * @param opResult    操作结果
	 * @param memo        审核意见
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @param exValues    扩展参数
	 * @return
	 */
	@ResponseBody
	@ApiOperation("完成待办任务后")
	@RequestMapping(value = "/auditTask1")
	int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues){
		return auditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
	}
	
	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param userName    操作人
	 * @param deptName    操作人所属部门
	 * @param opResult    操作结果
	 * @param memo        审核意见
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @param exValues    扩展参数
	 * @return
	 */
	@ResponseBody
	@ApiOperation("完成待办任务后")
	@RequestMapping(value = "/auditTask_forrws")
	int auditTask_forrws(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues){
		return auditLogService.auditTask_forrws(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
	}
	
	/**
	 * 完成待办任务后
	 * 1、将当前任务保存成已办任务
	 * 2、保存当前审核日志
	 * 3、获取工作流返回的待办任务，根据taskId查询待办任务
	 * 3-1、查询有数据，根据activeTask的id删除待办数据
	 * 3-2、查询无数据，根据activeTask的taskId获取所有待办任务
	 * 3-2-1、将查询到所有待办任务保存为已办，操作人为当前任务完成人
	 * 3-2-2、根据taskId删除所有待办任务
	 * 3-2-3、将工作流返回的待办任务保存为最新的待办任务
	 * @param userId      操作人id
	 * @param log    审核日志(操作人、操作人所属单位/部门、操作结果、审核意见)
	 * @param activeTask  当前完成的任务
	 * @param taskList    需保存的待办任务列表
	 * @param exValues    扩展参数
	 * @return
	 */
	@ResponseBody
	@ApiOperation("完成待办任务后")
	@RequestMapping(value = "/auditTask2")
	int auditTask(String userId, AuditLog log, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues){
		return auditLogService.auditTask(userId, log, activeTask, taskList, exValues);
	}
	/**
	 * 通过流程实例id完成所有待办任务
	 * @param userId
	 * @param processInstanceId
	 * @param reason
	 * @return
	 */
	@ResponseBody
	@ApiOperation("完成待办任务后")
	@RequestMapping(value = "/auditTask3")
	int auditTask(String userId, String processInstanceId, String reason){
		return auditLogService.auditTask(userId, processInstanceId, reason);
	}
	@ResponseBody
	@ApiOperation("完成待办任务后")
	@RequestMapping(value = "/auditTask4")
	int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask,
				  List<TaskData> taskList, Map<String, String> exValues, String appellation){
		return auditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues, appellation);
	}
	/**
	 * 取回已办任务后
	 * 
	 * @param userId
	 * @param log
	 * @param finishedTask
	 * @param taskList
	 * @param exValues
	 * @return
	 */
	@ResponseBody
	@ApiOperation("取回已办任务后")
	@RequestMapping(value = "/retrieveTask")
	int retrieveTask(String userId, AuditLog log, FinishedTask finishedTask, List<TaskData> taskList, Map<String, String> exValues){
		return auditLogService.retrieveTask(userId, log, finishedTask, taskList, exValues);
	}

	/**
	 * 指派待办任务后
	 * 1、修改当前任务操作人
	 * 2、保存当前审核日志
	 * @param log    审核日志(操作人、操作人所属单位/部门、操作结果、审核意见)
	 * @param activeTask  当前完成的任务
	 * @param exValues 扩展参数
	 * @return
	 */
	@ResponseBody
	@ApiOperation("指派待办任务后")
	@RequestMapping(value = "/setTaskAssignee")
	int setTaskAssignee(AuditLog log, ActiveTask activeTask, Map<String, String> exValues){
		return auditLogService.setTaskAssignee(log, activeTask, exValues);
	}
	

	/**
	* @Title: complateTask 
	* @Description: 完成任务
	* @param region  所属区县
	* @param unitIds 部门id
	* @param values      参数<字段名,字段值>
	* @param signUsers   会签用户信息<会签用户信息ID，<会签用户id列表>>
	* @param claimUsers  认领用户信息<认领用户信息ID，<认领用户id列表>>
	* @param claimGroups 认领用户组<认领用户组ID，<认领用户组id列表>>
	* @param userName    操作人
	* @param deptName    操作人所属部门
	* @param opResult    操作结果
	* @param activeTask  当前任务
	* @return
	* @throws WorkFlowException  参数说明 
	* @return List    返回类型 
	* 
	*/
	@ResponseBody
	@ApiOperation("完成任务")
	@RequestMapping(value = "/complateTask")
	Boolean complateTask(String userId, String region, List<String> unitIds, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, String userName, String deptName, String opResult, String memo, ActiveTask activeTask) throws WorkFlowException{
		return auditLogService.complateTask(userId, region, unitIds, values, signUsers, claimUsers, claimGroups, userName, deptName, opResult, memo, activeTask);
	}
	
	/**
	 * @Title: startProcess 
	 * @Description: 启动流程
	 * @param userId
	 * @param processKey
	 * @param businessKey
	 * @param data
	 * @return 流程实例id
	 * @throws WorkFlowException
	 */
	@ResponseBody
	@ApiOperation("启动流程")
	@RequestMapping(value = "/startProcess")
	Boolean startProcess(String userId, String processKey, String businessKey, TaskContentData data) throws WorkFlowException{
		return auditLogService.startProcess(userId, processKey, businessKey, data);
	}
	/**
	 * 查询业务数据审核最后一次上传材料的日志
	 * @param contentId
	 * @param processInstanceId
	 * @return
	 */
	@ResponseBody
	@ApiOperation("查询业务数据审核最后一次上传材料的日志")
	@RequestMapping(value = "/queryLastFile")
	AuditLog queryLastFile(String contentId, String processInstanceId){
		return auditLogService.queryLastFile(contentId, processInstanceId);
	}
}
