package webapi.workflowController;
import workflow.business.model.*;
import workflow.business.service.ProcessTaskService;
import workflow.common.error.WorkFlowException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;



/**
 * 工作流任务dubbo接口
 */
//@Service("processTaskService")
@Controller
@RestController
@RequestMapping(value = "/workflow/processTask")
public class ProcessTaskServiceController {
    @Autowired
    ProcessTaskService processTaskService;
    /**
     * 启动流程时可用
     * 保存待办任务
     * @param contentData 业务数据
     * @param taskList 待办任务列表
     * @return
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask")
    Boolean saveTask(TaskContentData contentData, List<TaskData> taskList) throws WorkFlowException{
        return processTaskService.saveTask(contentData, taskList);
    }

    /**
     * 启动流程时可用
     * 保存待办任务
     * @param userId      用户id
     * @param contentData 业务数据
     * @param taskList 待办任务列表
     * @return
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask1")
    ActiveTask saveTask(String userId, TaskContentData contentData, List<TaskData> taskList) throws WorkFlowException{
        return processTaskService.saveTask(userId, contentData, taskList);
    }

    /**
     * 完成任务时可用
     * 保存待办任务、已办任务、审核日志
     * @param userName   操作人姓名
     * @param deptName   操作人部门名称
     * @param opResult   操作结果（审核通过、不通过、退回修改...）
     * @param memo       操作备注信息（审核意见）
     * @param activeTask 当前完成的任务
     * @param taskList   待办任务列表
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask2")
    Boolean saveTask(String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList) throws WorkFlowException{
        return processTaskService.saveTask(userName, deptName, opResult, memo, activeTask, taskList);
    }

    /**
     * 完成任务时可用
     * 保存待办任务、已办任务、审核日志
     * @param userId     操作人id
     * @param userName   操作人姓名
     * @param deptName   操作人部门名称
     * @param opResult   操作结果（审核通过、不通过、退回修改...）
     * @param memo       操作备注信息（审核意见）
     * @param activeTask 当前完成的任务
     * @param taskList   待办任务列表
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask3")
    Boolean saveTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList) throws WorkFlowException{
        return processTaskService.saveTask(userId, userName, deptName, opResult, memo, activeTask, taskList);

    }
    /**
     * 完成任务时可用
     * 保存待办任务、已办任务、审核日志
     * @param userId     操作人id
     * @param userName   操作人姓名
     * @param deptName   操作人部门名称
     * @param opResult   操作结果（审核通过、不通过、退回修改...）
     * @param memo       操作备注信息（审核意见）
     * @param activeTask 当前完成的任务
     * @param taskList   待办任务列表
     * @param exValues   其他扩展参数
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask4")
    Boolean saveTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues) throws WorkFlowException{
        return processTaskService.saveTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
    }

    /**
     * 完成任务时可用-任务书专用
     * 保存待办任务、已办任务、审核日志
     * @param userId     操作人id
     * @param userName   操作人姓名
     * @param deptName   操作人部门名称
     * @param opResult   操作结果（审核通过、不通过、退回修改...）
     * @param memo       操作备注信息（审核意见）
     * @param activeTask 当前完成的任务
     * @param taskList   待办任务列表
     * @param exValues   其他扩展参数
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask_forrws")
    Boolean saveTask_forrws(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues) throws WorkFlowException{
        return processTaskService.saveTask_forrws(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
    }


    /**
     * 完成任务时可用
     * 保存待办任务、已办任务、审核日志
     * @param userId     操作人id
     * @param userName   操作人姓名
     * @param deptName   操作人部门名称
     * @param opResult   操作结果（审核通过、不通过、退回修改...）
     * @param memo       操作备注信息（审核意见）
     * @param activeTask 当前完成的任务
     * @param taskList   待办任务列表
     * @param exValues   其他扩展参数
     * @param appellation   称谓
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask5")
    Boolean saveTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues, String appellation) throws WorkFlowException{
        return processTaskService.saveTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues, appellation);
    }


    /**
     * 完成任务时可用
     * 保存待办任务、已办任务、审核日志
     * @param userId     操作人id
     * @param activeTaskId 当前需完成的任务ID
     * @param taskList   待办任务列表
     * @param exValues   其他扩展参数(审核日志、其他业务参数)
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("保存待办任务")
    @RequestMapping(value = "/saveTask6")
    Boolean saveTask(String userId, String activeTaskId, List<TaskData> taskList, Map<String, String> exValues) throws WorkFlowException{
        return processTaskService.saveTask(userId, activeTaskId, taskList, exValues);
    }

    /**
     * 做取回任务时可用
     * 保存待办任务、已办任务、审核日志
     * @param userId     操作人id
     * @param finishedTaskId 已办任务ID
     * @param taskList   待办任务列表
     * @param exValues   其他扩展参数(审核日志、其他业务参数)
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("做取回任务时可用 保存待办任务、已办任务、审核日志")
    @RequestMapping(value = "/retrieveTask")
    Boolean retrieveTask(String userId, String finishedTaskId, List<TaskData> taskList, Map<String, String> exValues) throws WorkFlowException{
        return processTaskService.retrieveTask(userId, finishedTaskId, taskList, exValues);
    }

    /**
     * @Title: setTaskAssignee
     * @Description: 指派任务
     * @param userId         被指派人
     * @param activeTaskId   当前任务ID
     * @param exValues 其他扩展参数(审核日志、其他业务参数)
     * @return  参数说明
     * @return boolean    返回类型
     * @throws WorkFlowException
     *
     */
    @ResponseBody
    @ApiOperation("指派任务")
    @RequestMapping(value = "/setTaskAssignee")
    boolean setTaskAssignee(String userId, String activeTaskId, Map<String, String> exValues) throws WorkFlowException{
        return processTaskService.setTaskAssignee(userId, activeTaskId, exValues);
    }

    /**
     * 根据id查询待办任务
     * @param taskId ActiveTask.id
     * @return
     */
    @ResponseBody
    @ApiOperation("根据id查询待办任务")
    @RequestMapping(value = "/getActiveTaskById")
    ActiveTask getActiveTaskById(String taskId){
        return processTaskService.getActiveTaskById(taskId);
    }

    /**
     * 根据用户id、任务id查询待办任务
     * @param userId      用户id
     * @param taskDataId  TaskData.id
     * @return
     */
    @ResponseBody
    @ApiOperation("根据用户id、任务id查询待办任务")
    @RequestMapping(value = "/getActiveTask")
    ActiveTask getActiveTask(String userId, String taskDataId){
        return processTaskService.getActiveTask(userId, taskDataId);
    }

    /**
     * 已启动流程实例，根据流程实例ID查询任务
     * @param userId             用户id
     * @param deptCode           单位/部门code
     * @param processInstanceId  流程实例id
     * @return
     */
    @ResponseBody
    @ApiOperation("已启动流程实例，根据流程实例ID查询任务")
    @RequestMapping(value = "/getActiveTaskByProcessInstId")
    ActiveTask getActiveTaskByProcessInstId(String userId, String deptCode, String processInstanceId){
        return processTaskService.getActiveTaskByProcessInstId(userId, deptCode, processInstanceId);
    }

    /**
     * 已启动流程实例，根据流程实例ID/父流程实例id查询任务
     * @param userId             用户id
     * @param deptCode           单位/部门code
     * @param processInstanceId  流程实例id
     * @param parentProcessInstanceId 父流程实例id
     * @return
     */
    @ResponseBody
    @ApiOperation("已启动流程实例，根据流程实例ID查询任务")
    @RequestMapping(value = "/getActiveTaskByProcessInstId1")
    ActiveTask getActiveTaskByProcessInstId(String userId, String deptCode, String processInstanceId, String parentProcessInstanceId){
        return processTaskService.getActiveTaskByProcessInstId(userId, deptCode, processInstanceId, parentProcessInstanceId);
    }

    /**
     * 待办任务分页查询
     * @param categoryType 事项分类
     * @param accessScope  事项类别id列表
     * @param deptCode     单位/部门code
     * @param parentTaskDefId 父任务节点定义id
     * @param taskDefId    任务节点定义id
     * @param taskNo       任务流水号
     * @param userId       分配到任务的人userId
     * @param pageNum      页码
     * @param pageSize     每页多少
     * @return
     */
    @ResponseBody
    @ApiOperation("待办任务分页查询")
    @RequestMapping(value = "/pageTasks")
    PageList<ActiveTask> pageTasks(String categoryType, List<String> accessScope, String deptCode, String processInstanceId, String parentTaskDefId, String taskDefId, String taskNo, String userId, int pageNum, int pageSize){
        return processTaskService.pageTasks(categoryType, accessScope, deptCode, processInstanceId, parentTaskDefId, taskDefId, taskNo, userId, pageNum, pageSize);
    }

    /**
     * 已办任务分页查询
     *
     * @param categoryType    事项分类
     * @param accessScope     事项类别id列表
     * @param parentTaskDefId 父任务节点定义id
     * @param taskDefId       任务节点定义id
     * @param taskNo          任务流水号
     * @param userId          完成任务的人userId
     * @param userName        完成任务的人的名称
     * @param opResult        任务操作结果（审核通过、不通过、退回修改...）
     * @param pageNum         页码
     * @param pageSize        每页多少
     * @return
     */
    @ResponseBody
    @ApiOperation("已办任务分页查询")
    @RequestMapping(value = "/pageFinishedTasks")
    PageList<FinishedTask> pageFinishedTasks(String categoryType, List<String> accessScope, String processInstanceId, String parentTaskDefId, String taskDefId, String taskNo, String userId, String userName, String opResult, int pageNum, int pageSize) {
        return processTaskService.pageFinishedTasks(categoryType, accessScope, processInstanceId, parentTaskDefId, taskDefId, taskNo, userId, userName, opResult, pageNum, pageSize);
    }

    /**
     * 审核日志分页查询
     *
     * @param contentId         审批事项id
     * @param processInstanceId 流程实例id
     * @param businessNode      任务节点
     * @param userName          审核人名称
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @ApiOperation("审核日志分页查询")
    @RequestMapping(value = "/pageAuditLogs")
    PageList<AuditLog> pageAuditLogs(String contentId, String processInstanceId, String businessNode, String userName, int pageNum, int pageSize) {
        return processTaskService.pageAuditLogs(contentId, processInstanceId, businessNode, userName, pageNum, pageSize);
    }

    /**
     * 审核日志分页查询
     *
     * @param hasFile           是否有附件(0查询全部1带附件的2不带附件的)
     * @param contentId         审批事项id
     * @param processInstanceId 流程实例id
     * @param processInitiator  流程发起人id
     * @param businessNode      任务节点
     * @param userName          审核人名称
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @ApiOperation("审核日志分页查询")
    @RequestMapping(value = "/pageAuditLogs1")
    PageList<AuditLog> pageAuditLogs(int hasFile, String contentId, String processInstanceId, String processInitiator, String businessNode, String userName, int pageNum, int pageSize) {
        return processTaskService.pageAuditLogs(hasFile, contentId, processInstanceId, processInitiator, businessNode, userName, pageNum, pageSize);
    }

    /**
     * 查询业务数据审核最后一次上传材料的日志
     *
     * @param contentId
     * @param processInstanceId
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("查询业务数据审核最后一次上传材料的日志")
    @RequestMapping(value = "/queryLastFile")
    AuditLog queryLastFile(String contentId, String processInstanceId) throws WorkFlowException {
        return processTaskService.queryLastFile(contentId, processInstanceId);
    }

    /**
     * 获取待办任务总览
     *
     * @param userId           用户id
     * @param unitIds          单位/部门的id/code列表
     * @param adminUnitIds     是管理员的单位/部门的id/code列表
     * @param itemsUnitIds     事项部门列表
     * @param accessScopeList  事项类别list
     * @param businessTypeList 业务类型list
     * @return
     */
    @ResponseBody
    @ApiOperation("获取待办任务总览")
    @RequestMapping(value = "/getTotalTasks")
    List<TotalTasks> getTotalTasks(String userId, List<String> unitIds, List<String> adminUnitIds, List<String> itemsUnitIds, List<String> accessScopeList, List<String> businessTypeList) {
        return processTaskService.getTotalTasks(userId, unitIds, adminUnitIds, itemsUnitIds, accessScopeList, businessTypeList);
    }

    /**
     * @param userId       用户id
     * @param region       用户所属区县
     * @param unitIds      单位/部门code列表
     * @param adminUnitIds 任务指明是由单位/部门的管理员操作的单位/部门code列表
     * @param itemsUnitIds 事项部门列表
     * @param jobIdList    用户所属角色列表
     * @return List<String>    返回类型
     * @Title: makeClaims
     * @Description: 组合可操作者查询信息
     */
    @ResponseBody
    @ApiOperation("获取待办任务总览")
    @RequestMapping(value = "/makeClaims")
    List<String> makeClaims(String userId, String region, List<String> unitIds,
                            List<String> adminUnitIds, List<String> itemsUnitIds, List<String> jobIdList) {
        return processTaskService.makeClaims(userId, region, unitIds, adminUnitIds, itemsUnitIds, jobIdList);
    }

    /**
     * 根据用户id查询用户实际已完成任务数
     *
     * @param userId
     * @param businessTypeList 业务类型list
     * @return
     */
    @ResponseBody
    @ApiOperation("获取待办任务总览")
    @RequestMapping(value = "/countTaskByUser")
    Integer countTaskByUser(String userId, List<String> businessTypeList) {
        return processTaskService.countTaskByUser(userId, businessTypeList);
    }

    /**
     * 进行暂停、停止、恢复操作时，更新对应状态
     *
     * @param processDefinitionId 流程定义id
     * @param processInstanceId   流程实例id
     * @param processTaskId       流程任务id
     * @param status              状态 0正常1暂停2终止
     * @return
     */
    @ResponseBody
    @ApiOperation("进行暂停、停止、恢复操作时，更新对应状态")
    @RequestMapping(value = "/updateStatus")
    boolean updateStatus(String processDefinitionId, String processInstanceId, String processTaskId, int status) {
        return processTaskService.updateStatus(processDefinitionId, processInstanceId, processTaskId, status);
    }

    /**
     * 通过流程实例id完成所有待办任务
     *
     * @param userId
     * @param processInstanceId
     * @param reason
     * @return
     */
    @ResponseBody
    @ApiOperation("通过流程实例id完成所有待办任务")
    @RequestMapping(value = "/complateTask")
    boolean complateTask(String userId, String processInstanceId, String reason) {
        return processTaskService.complateTask(userId, processInstanceId, reason);
    }
}
