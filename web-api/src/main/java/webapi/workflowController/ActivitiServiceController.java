package webapi.workflowController;

import workflow.business.service.ActivitiService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import workflow.business.model.*;
import workflow.common.constant.ActivitiConstant.EOrderType;
import workflow.common.error.WorkFlowException;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月08日 11:19
 */
//@Service("activitiService")
@Controller
@RestController
@RequestMapping(value = "/workflow/activiti")
public class ActivitiServiceController {
    @Autowired
    private ActivitiService activitiService;

    /**
     * @param pageNum
     * @param pageSize
     * @return PageList<ProcessSampleData>    返回类型
     * @throws
     * @Title: getProcessList
     * @Description: 获取流程列表
     */
    @ResponseBody
    @ApiOperation("获取流程列表")
    @RequestMapping(value = "/getProcessList")
    PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getProcessList(name, onlyLatestVersion, pageNum, pageSize);
    }

    /**
     * @param name
     * @param onlyLatestVersion
     * @param businessType
     * @param pageNum
     * @param pageSize
     * @return PageList<ProcessSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getProcessList
     * @Description: 获取流程列表
     */
    @ResponseBody
    @ApiOperation("获取流程列表")
    @RequestMapping(value = "/getProcessList1")
    PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, String businessType, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getProcessList(name, onlyLatestVersion, businessType, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param processId
     * @param values
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: startProcessInstanceById
     * @Description: 根据流程ID启动流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("根据流程ID启动流程实例")
    @RequestMapping(value = "/startProcessInstanceById")
    TaskData startProcessInstanceById(String userId, String processId, Map<String, Object> values) throws WorkFlowException {
        return activitiService.startProcessInstanceById(userId, processId, values);
    }

    /**
     * @param userId
     * @param processId
     * @param values
     * @param businessKey
     * @return TaskData    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: startProcessInstanceById
     * @Description: 根据流程ID启动流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("根据流程key启动流程实例")
    @RequestMapping(value = "/startProcessInstanceByKey1")
    TaskData startProcessInstanceById(String userId, String processId, Map<String, Object> values, String businessKey) throws WorkFlowException {
        return activitiService.startProcessInstanceById(userId, processId, values, businessKey);
    }

    /**
     * @param userId
     * @param processKey
     * @param values
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: startProcessInstanceByKey
     * @Description: 根据流程key启动流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("根据流程key启动流程实例")
    @RequestMapping(value = "/startProcessInstanceByKey")
    TaskData startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values) throws WorkFlowException {
        return activitiService.startProcessInstanceByKey(userId, processKey, values);
    }

    /**
     * @param userId
     * @param processKey
     * @param values
     * @param businessKey
     * @return TaskData    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: startProcessInstanceByKey
     * @Description: 根据流程key启动流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("根据流程key启动流程实例")
    @RequestMapping(value = "/startProcessInstanceByKey2")
    TaskData startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values, String businessKey) throws WorkFlowException {
        return activitiService.startProcessInstanceByKey(userId, processKey, values, businessKey);
    }

    /**
     * @param userId
     * @param processInstanceId
     * @param reason
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: stopProcessInstance
     * @Description: 中止流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("中止流程实例")
    @RequestMapping(value = "/stopProcessInstance")
    boolean stopProcessInstance(String userId, String processInstanceId, String reason) throws WorkFlowException {
        return activitiService.stopProcessInstance(userId, processInstanceId, reason);
    }

    /**
     * @param userId
     * @param processInstanceId
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: suspendProcessInstance
     * @Description: 暂停流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("暂停流程实例")
    @RequestMapping(value = "/suspendProcessInstance")
    boolean suspendProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
        return activitiService.suspendProcessInstance(userId, processInstanceId);
    }

    /**
     * @param userId
     * @param processInstanceId
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: activateProcessInstance
     * @Description: 激活流程实例
     */
    @Transactional
    @ResponseBody
    @ApiOperation("激活流程实例")
    @RequestMapping(value = "/activateProcessInstance")
    boolean activateProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
        return activitiService.activateProcessInstance(userId, processInstanceId);
    }

    /**
     * @param userId
     * @param processId
     * @param taskName
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException
     * @Title: getTasksByUser
     * @Description: 获取当前用户可操作任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可操作任务列表")
    @RequestMapping(value = "/getTasksByUser")
    PageList<TaskSampleData> getTasksByUser(String userId, String processId, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getTasksByUser(userId, processId, taskName, taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param region
     * @param unitId
     * @param jobIdList
     * @param processId
     * @param taskName
     * @param taskDefId
     * @param showClaim
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksByUser
     * @Description: 获取当前用户可操作任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可操作任务列表")
    @RequestMapping(value = "/getTasksByUser1")
    PageList<TaskSampleData> getTasksByUser(String userId, String region, String unitId, List<String> jobIdList, String processId, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getTasksByUser(userId, region, unitId, jobIdList, processId, taskName, taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param region
     * @param unitId
     * @param jobIdList
     * @param processId
     * @param processBusinessType
     * @param taskName
     * @param taskDefId
     * @param showClaim
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksByUser
     * @Description: 获取当前用户可操作任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可操作任务列表")
    @RequestMapping(value = "/getTasksByUser2")
    PageList<TaskSampleData> getTasksByUser(String userId, String region, String unitId, List<String> jobIdList, String processId, String processBusinessType, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getTasksByUser(userId, region, unitId, jobIdList, processId, processBusinessType, taskName, taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param processId
     * @param processBusinessType
     * @param taskName
     * @param taskDefId
     * @param showClaim
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksByUser
     * @Description: 获取当前用户可操作任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可操作任务列表")
    @RequestMapping(value = "/getTasksByUser3")
    PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processBusinessType, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getTasksByUser(userId, region, unitIds, adminUnitIds, jobIdList, processId, processBusinessType, taskName, taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param processId
     * @param processKey
     * @param processBusinessType
     * @param processBusinessKeyList
     * @param taskName
     * @param taskDefId
     * @param showClaim
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksByUser
     * @Description: 获取当前用户可操作任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可操作任务列表")
    @RequestMapping(value = "/getTasksByUser4")
    PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey, String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getTasksByUser(userId, region, unitIds, adminUnitIds, jobIdList, processId, processKey, processBusinessType, processBusinessKeyList, taskName, taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param processId
     * @param processKey
     * @param processBusinessType
     * @param processBusinessKeyList
     * @param taskName
     * @param taskDefId
     * @param showClaim
     * @param startTime
     * @param endTime
     * @param hasBusinessKey
     * @param orderByCreate
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksByUser
     * @Description: 获取当前用户可操作任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可操作任务列表")
    @RequestMapping(value = "/getTasksByUser5")
    PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey, String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, boolean hasBusinessKey, EOrderType orderByCreate, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getTasksByUser(userId, region, unitIds, adminUnitIds, jobIdList, processId, processKey, processBusinessType, processBusinessKeyList, taskName, taskDefId, showClaim, startTime, endTime, hasBusinessKey, orderByCreate, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param processId
     * @param processKey
     * @param processBusinessType
     * @param processBusinessKeyList
     * @param taskName
     * @param taskDefId
     * @param showClaim
     * @param startTime
     * @param endTime
     * @return List<String>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getProcessInstIdsByUser
     * @Description: 获取用户代办流程实例id列表
     */
    @ResponseBody
    @ApiOperation("获取用户代办流程实例id列表")
    @RequestMapping(value = "/getProcessInstIdsByUser")
    List<String> getProcessInstIdsByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey, String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime) throws WorkFlowException {
        return activitiService.getProcessInstIdsByUser(userId, region, unitIds, adminUnitIds, jobIdList, processId, processKey, processBusinessType, processBusinessKeyList, taskName, taskDefId, showClaim, startTime, endTime);
    }

    /**
     * @param userId
     * @param processId
     * @param taskName
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException
     * @Title: getFinishedTasksByUser
     * @Description: 获取当前用户已完成任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户已完成任务列表")
    @RequestMapping(value = "/getFinishedTasksByUser")
    PageList<TaskSampleData> getFinishedTasksByUser(String userId, String processId, String taskName, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getFinishedTasksByUser(userId, processId, taskName, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param processId
     * @param processKey
     * @param taskName
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getFinishedTasksByUser
     * @Description: 获取当前用户已完成任务列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户已完成任务列表")
    @RequestMapping(value = "/getFinishedTasksByUser1")
    PageList<TaskSampleData> getFinishedTasksByUser(String userId, String processId, String processKey, String taskName, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getFinishedTasksByUser(userId, processId, taskName, startTime, endTime, pageNum, pageSize);
    }

    /**
     * @param userId
     * @param processId
     * @param processKey
     * @param taskName
     * @param startTime
     * @param endTime
     * @return Set<String>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getFinishedTasks
     * @Description: 获取已完成任务列表（无分页，只返回简单数据，按流程实例区分）
     */
    @ResponseBody
    @ApiOperation("获取已完成任务列表（无分页，只返回简单数据，按流程实例区分）")
    @RequestMapping(value = "/getFinishedTasks")
    Map<String, List<TaskSampleData>> getFinishedTasks(String userId, String processId, String processKey, String taskName, Date startTime, Date endTime) throws WorkFlowException {
        return activitiService.getFinishedTasks(userId, processId, processKey, taskName, startTime, endTime);
    }

    /**
     * @param userId
     * @param processId
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException
     * @Title: getClaimTasksByUser
     * @Description: 获取当前用户可候选流程列表
     */
    @ResponseBody
    @ApiOperation("获取当前用户可候选流程列表")
    @RequestMapping(value = "/getClaimTasksByUser")
    PageList<TaskSampleData> getClaimTasksByUser(String userId, String processId, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getClaimTasksByUser(userId, processId, pageNum, pageSize);
    }

    /**
     * @param unitId
     * @param processId
     * @param pageNum
     * @param pageSize
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException
     * @Title: getClaimTasksByUnit
     * @Description: 获取当前角色可候选流程列表
     */
    @ResponseBody
    @ApiOperation("获取当前部门可候选流程列表")
    @RequestMapping(value = "/getClaimTasksByUnit")
    PageList<TaskSampleData> getClaimTasksByUnit(String unitId, String processId, int pageNum, int pageSize) throws WorkFlowException {
        return activitiService.getClaimTasksByUnit(unitId, processId, pageNum, pageSize);
    }

    /**
     * @param taskId
     * @return TaskData    返回类型
     * @throws WorkFlowException
     * @Title: getTaskInfo
     * @Description: 获取流程任务详情
     */
    @ResponseBody
    @ApiOperation("获取流程任务详情")
    @RequestMapping(value = "/getTaskInfo")
    TaskData getTaskInfo(String taskId) throws WorkFlowException {
        return activitiService.getTaskInfo(taskId);
    }

    /**
     * @param userId
     * @param processInstanceId
     * @return TaskData    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getUserTaskInfoByInstance
     * @Description: 根据流程实例ID获取用户当前进行中的任务详情
     */
    @ResponseBody
    @ApiOperation("根据流程实例ID获取用户当前进行中的任务详情")
    @RequestMapping(value = "/getUserTaskInfoByInstance")
    TaskData getUserTaskInfoByInstance(String userId, String processInstanceId) throws WorkFlowException {
        return activitiService.getUserTaskInfoByInstance(userId, processInstanceId);
    }

    /**
     * @param userId
     * @param region
     * @param unitId
     * @param jobIdList
     * @param processInstanceId
     * @return TaskData    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getUserTaskInfoByInstance
     * @Description: 根据流程实例ID获取用户当前进行中的任务详情
     */
    @ResponseBody
    @ApiOperation("根据流程实例ID获取用户当前进行中的任务详情")
    @RequestMapping(value = "/getUserTaskInfoByInstance1")
    TaskData getUserTaskInfoByInstance(String userId, String region, String unitId, List<String> jobIdList, String processInstanceId) throws WorkFlowException {
        return activitiService.getUserTaskInfoByInstance(userId, region, unitId, jobIdList, processInstanceId);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param processInstanceId
     * @return TaskData    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getUserTaskInfoByInstance
     * @Description: 根据流程实例ID获取用户当前进行中的任务详情
     */
    @ResponseBody
    @ApiOperation("根据流程实例ID获取用户当前进行中的任务详情")
    @RequestMapping(value = "/getUserTaskInfoByInstance2")
    TaskData getUserTaskInfoByInstance(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, String processInstanceId) throws WorkFlowException {
        return activitiService.getUserTaskInfoByInstance(userId, region, unitIds, adminUnitIds, jobIdList, processInstanceId);
    }

    /**
     * @param taskId
     * @return TaskData    返回类型
     * @throws WorkFlowException
     * @Title: getFinishedTaskInfo
     * @Description: 获取已完成流程任务详情
     */
    @ResponseBody
    @ApiOperation("获取已完成流程任务详情")
    @RequestMapping(value = "/getFinishedTaskInfo")
    TaskData getFinishedTaskInfo(String taskId) throws WorkFlowException {
        return activitiService.getFinishedTaskInfo(taskId);
    }

    /**
     * 根据任务ID获取流程任务节点列表
     *
     * @param taskId
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("根据任务ID获取流程任务节点列表")
    @RequestMapping(value = "/getProcessInfoByTask")
    List<TaskDefData> getProcessInfoByTask(String taskId) throws WorkFlowException {
        return activitiService.getProcessInfoByTask(taskId);
    }

    /**
     * 根据流程定义ID获取流程任务节点列表
     *
     * @param processDefinitionId
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @ApiOperation("根据流程定义ID获取流程任务节点列表")
    @RequestMapping(value = "/getProcessInfoByDefId")
    List<TaskDefData> getProcessInfoByDefId(String processDefinitionId) throws WorkFlowException {
        return activitiService.getProcessInfoByDefId(processDefinitionId);
    }

    /**
     * @param userId
     * @param taskId
     * @param values
     * @param signUsers
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: updateTaskValues
     * @Description: 更新当前流程变量
     */
    @Transactional
    @ResponseBody
    @ApiOperation("更新当前流程变量")
    @RequestMapping(value = "/updateTaskValues")
    boolean updateTaskValues(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers) throws WorkFlowException {
        return activitiService.updateTaskValues(userId, taskId, values, signUsers, claimUsers);
    }

    /**
     * @param userId
     * @param taskId
     * @param values
     * @param signUsers
     * @param claimUsers
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: updateTaskValues
     * @Description: 更新当前流程变量
     */
    @Transactional
    @ResponseBody
    @ApiOperation("更新当前流程变量")
    @RequestMapping(value = "/updateTaskValues1")
    boolean updateTaskValues(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.updateTaskValues(userId, taskId, values, signUsers, claimUsers, exValues);
    }


    /**
     * @param userId
     * @param taskId
     * @param values
     * @param signUsers
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: complateTask
     * @Description: 完成任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("完成任务")
    @RequestMapping(value = "/complateTask")
    boolean complateTask(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers) throws WorkFlowException {
        return activitiService.complateTask(userId, taskId, values, signUsers, claimUsers);
    }

    /**
     * @param userId
     * @param taskId
     * @param values
     * @param signUsers
     * @param claimUsers
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: complateTask
     * @Description: 完成任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("完成任务")
    @RequestMapping(value = "/complateTask1")
    boolean complateTask(String userId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.complateTask(userId, taskId, values, signUsers, claimUsers, exValues);
    }

    /**
     * @param userId
     * @param region
     * @param unitId
     * @param taskId
     * @param values
     * @param signUsers
     * @param claimUsers
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: complateTask
     * @Description: 完成任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("完成任务")
    @RequestMapping(value = "/complateTask2")
    boolean complateTask(String userId, String region, String unitId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.complateTask(userId, region, unitId, taskId, values, signUsers, claimUsers, exValues);
    }

    /**
     * @param userId
     * @param region
     * @param unitId
     * @param taskId
     * @param values
     * @param signUsers
     * @param claimUsers
     * @param claimGroups
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: complateTask
     * @Description: 完成任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("完成任务")
    @RequestMapping(value = "/complateTask3")
    boolean complateTask(String userId, String region, String unitId, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.complateTask(userId, region, unitId, taskId, values, signUsers, claimUsers, claimGroups, exValues);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param taskId
     * @param values
     * @param signUsers
     * @param claimUsers
     * @param claimGroups
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: complateTask
     * @Description: 完成任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("完成任务")
    @RequestMapping(value = "/complateTask4")
    boolean complateTask(String userId, String region, List<String> unitIds, String taskId, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.complateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param taskIdList
     * @param values
     * @param signUsers
     * @param claimUsers
     * @param claimGroups
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: complateTasks
     * @Description: 批量完成任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("完成任务")
    @RequestMapping(value = "/complateTasks")
    boolean complateTasks(String userId, String region, List<String> unitIds, List<String> taskIdList, Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.complateTasks(userId, region, unitIds, taskIdList, values, signUsers, claimUsers, claimGroups, exValues);
    }

    /**
     * @param userId
     * @param taskId
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: claimTask
     * @Description: 候选任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("候选任务")
    @RequestMapping(value = "/claimTask")
    boolean claimTask(String userId, String taskId) throws WorkFlowException {
        return activitiService.claimTask(userId, taskId);
    }

    /**
     * @param userId
     * @param taskId
     * @param exValues
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: retrieveTask
     * @Description: 取回任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("取回任务")
    @RequestMapping(value = "/retrieveTask")
    boolean retrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException {
        return activitiService.retrieveTask(userId, taskId, exValues);
    }

    /**
     * @param startTaskId
     * @param targetTaskDefId
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: jumpTask
     * @Description: 跳转任务
     */
    @Transactional
    @ResponseBody
    @ApiOperation("跳转任务")
    @RequestMapping(value = "/jumpTask")
    boolean jumpTask(String startTaskId, String targetTaskDefId) throws WorkFlowException {
        return activitiService.jumpTask(startTaskId, targetTaskDefId);
    }

    /**
     * @param processInstId
     * @param targetTaskDefId
     * @return boolean    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: jumpTaskByProInst
     * @Description: 跳转指定流程实例的任务
     */
    @ResponseBody
    @ApiOperation("跳转指定流程实例的任务")
    @RequestMapping(value = "/jumpTaskByProInst")
    boolean jumpTaskByProInst(String processInstId, String targetTaskDefId) throws WorkFlowException {
        return activitiService.jumpTaskByProInst(processInstId, targetTaskDefId);
    }

    /**
     * @param userId
     * @param taskId
     * @return boolean    返回类型
     * @throws WorkFlowException
     * @Title: setTaskAssignee
     * @Description: 指派任务
     */
    @ResponseBody
    @ApiOperation("指派任务")
    @RequestMapping(value = "/setTaskAssignee")
    boolean setTaskAssignee(String userId, String taskId) throws WorkFlowException {
        return activitiService.setTaskAssignee(userId, taskId);
    }

    /**
     * @param processInstanceId
     * @return PageList<TaskSampleData>    返回类型
     * @throws WorkFlowException
     * @Title: getTasksByProcessInstanceID
     * @Description: 获取流程实例的任务列表
     */
    @ResponseBody
    @ApiOperation("获取流程实例的任务列表")
    @RequestMapping(value = "/getTasksByProcessInstanceID")
    List<TaskSampleData> getTasksByProcessInstanceID(String processInstanceId) throws WorkFlowException {
        return activitiService.getTasksByProcessInstanceID(processInstanceId);
    }

    /**
     * @param processInstanceId
     * @param
     * @return List<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getActiveTasksByProcessInstanceID
     * @Description: 获取流程实例的已激活任务列表
     */
    @ResponseBody
    @ApiOperation("获取流程实例的已激活任务列表")
    @RequestMapping(value = "/getActiveTasksByProcessInstanceID")
    List<TaskSampleData> getActiveTasksByProcessInstanceID(String processInstanceId) throws WorkFlowException {
        return activitiService.getActiveTasksByProcessInstanceID(processInstanceId);
    }

    /**
     * @param processInstanceId
     * @return List<TaskData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTaskDatasByProcessInstanceID
     * @Description: 获取流程实例的任务详情列表
     */
    @ResponseBody
    @ApiOperation("获取流程实例的任务详情列表")
    @RequestMapping(value = "/getTaskDatasByProcessInstanceID")
    List<TaskData> getTaskDatasByProcessInstanceID(String processInstanceId) throws WorkFlowException {
        return activitiService.getTaskDatasByProcessInstanceID(processInstanceId);
    }

    /**
     * @param userId
     * @param showClaim
     * @return Map<String, Map < String, TasksInfo>>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksInfoByUser
     * @Description: 获取用户的所有代办列表，返回值按任务类型分类
     */
    @ResponseBody
    @ApiOperation("获取用户的所有代办列表，返回值按任务类型分类")
    @RequestMapping(value = "/getTasksInfoByUser")
    Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, boolean showClaim) throws WorkFlowException {
        return activitiService.getTasksInfoByUser(userId, showClaim);
    }

    /**
     * @param userId
     * @param region
     * @param unitId
     * @param jobIdList
     * @param showClaim
     * @return Map<String, Map < String, TasksInfo>>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksInfoByUser
     * @Description: 获取用户的所有代办列表，返回值按任务类型分类
     */
    @ResponseBody
    @ApiOperation("获取用户的所有代办列表，返回值按任务类型分类")
    @RequestMapping(value = "/getTasksInfoByUser1")
    Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, String unitId, List<String> jobIdList, boolean showClaim) throws WorkFlowException {
        return activitiService.getTasksInfoByUser(userId, region, unitId, jobIdList, showClaim);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param showClaim
     * @return Map<String, Map < String, TasksInfo>>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksInfoByUser
     * @Description: 获取用户的所有代办列表，返回值按任务类型分类
     */
    @ResponseBody
    @ApiOperation("获取用户的所有代办列表，返回值按任务类型分类")
    @RequestMapping(value = "/getTasksInfoByUser2")
    Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, boolean showClaim) throws WorkFlowException {
        return activitiService.getTasksInfoByUser(userId, region, unitIds, adminUnitIds, jobIdList, showClaim);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param businessTypeList
     * @param showClaim
     * @return Map<String, Map < String, TasksInfo>>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksInfoByUser
     * @Description: 获取用户的所有代办列表，返回值按任务类型分类
     */
    @ResponseBody
    @ApiOperation("获取用户的所有代办列表，返回值按任务类型分类")
    @RequestMapping(value = "/getTasksInfoByUser3")
    Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, List<String> businessTypeList, boolean showClaim) throws WorkFlowException {
        return activitiService.getTasksInfoByUser(userId, region, unitIds, adminUnitIds, jobIdList, businessTypeList, showClaim);
    }

    /**
     * @param userId
     * @param region
     * @param unitIds
     * @param adminUnitIds
     * @param jobIdList
     * @param businessTypeList
     * @param processBusinessKeyList
     * @param showClaim
     * @return Map<String, Map < String, TasksInfo>>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getTasksInfoByUser
     * @Description: 获取用户的所有代办列表，返回值按任务类型分类
     */
    @ResponseBody
    @ApiOperation("获取用户的所有代办列表，返回值按任务类型分类")
    @RequestMapping(value = "/getTasksInfoByUser4")
    Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds, List<String> adminUnitIds, List<String> jobIdList, List<String> businessTypeList, List<String> processBusinessKeyList, boolean showClaim) throws WorkFlowException {
        return activitiService.getTasksInfoByUser(userId, region, unitIds, adminUnitIds, jobIdList, businessTypeList, processBusinessKeyList, showClaim);
    }

    /**
     * @param processId
     * @return String    返回类型
     * @throws WorkFlowException
     * @throws IOException
     * @Title: getProcessPreview
     * @Description: 根据流程定义获取流程图，返回base64加密png图片数据
     */
    @ResponseBody
    @ApiOperation("根据流程定义获取流程图，返回base64加密png图片数据")
    @RequestMapping(value = "/getProcessPreview")
    String getProcessPreview(String processId) throws WorkFlowException, IOException {
        return activitiService.getProcessPreview(processId);
    }

    /**
     * @param processInstanceId
     * @return ProcessInstData    返回类型
     * @throws WorkFlowException
     * @Title: getProcessInstData
     * @Description: 获取流程实例详情
     */
    @ResponseBody
    @ApiOperation("获取流程实例详情")
    @RequestMapping(value = "/getProcessInstData")
    ProcessInstData getProcessInstData(String processInstanceId) throws WorkFlowException {
        return activitiService.getProcessInstData(processInstanceId);
    }

    /**
     * @param processInstanceId
     * @return List<TaskSampleData>    返回类型
     * @throws WorkFlowException 参数说明
     * @Title: getProcessTasks
     * @Description: 获取流程实例所有任务，按任务开始时间排序
     */
    @ResponseBody
    @ApiOperation("获取流程实例详情")
    @RequestMapping(value = "/getProcessTasks")
    List<TaskSampleData> getProcessTasks(String processInstanceId) throws WorkFlowException {
        return activitiService.getProcessTasks(processInstanceId);
    }


}
