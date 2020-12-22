package workflow.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.service.ProcessTaskService;
import workflow.business.service.WorkflowService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import workflow.business.model.PageList;
import workflow.business.model.ProcessInstData;
import workflow.business.model.TaskData;
import workflow.common.constant.ActivitiConstant;
import workflow.common.error.WorkFlowException;
import workflow.common.utils.WorkflowUtil;
import workflow.olddata.constant.SystemConstant;
import workflow.olddata.model.PageInput;
import workflow.olddata.util.IDGeneratorUtil;
import workflow.business.model.*;
import workflow.business.service.ActiveTaskService;
import workflow.business.service.AuditLogService;
import workflow.business.service.FinishedTaskService;
import workflow.business.service.TaskStatusService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 工作流任务dubbo接口
 */
@Service("processTask")
@Component
@DS("master")
public class ProcessTaskServiceImpl implements ProcessTaskService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	ActiveTaskService stcsmActiveTaskService;
	@Autowired
	FinishedTaskService stcsmFinishedTaskService;
	@Autowired
	AuditLogService stcsmAuditLogService;
	@Autowired
	WorkflowService workflowService;
	@Autowired
	TaskStatusService stcsmTaskStatusService;

	@Override
//	@Transactional
	public Boolean saveTask(TaskContentData data, List<TaskData> list) throws WorkFlowException {
		try {
			if(list != null && list.size() > 0) {
				// 查询流程名称
				ProcessInstData inst = workflowService.getProcessInstData(list.get(0).getProcessInstanceId());
				int count = stcsmActiveTaskService.batchSave(inst.getName(), null, null, list, data);
				if(count > 0) {
					// 提醒业务系统保存 流程实例id
					TaskStatus status = new TaskStatus();
					status.setId(IDGeneratorUtil.generatorId());
					status.setContentId(data.getContentId());
					status.setProcessInstanceId(list.get(0).getProcessInstanceId());
					status.setTaskDefId(list.get(0).getTaskDefId());
					status.setTaskName(list.get(0).getName());
					stcsmTaskStatusService.saveStcsmTaskStatus(status);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_SAVE_FAIL, e.getMessage());
		}
		return false;
	}

	@Override
	public ActiveTask saveTask(String userId, TaskContentData contentData,
			List<TaskData> taskList) throws WorkFlowException {
		Boolean bl = saveTask(contentData, taskList);
		if(bl) {
			ActiveTask task = getActiveTask(userId, taskList.get(0).getId());
			return task;
		}
		return null;
	}

	@Override
	public Boolean saveTask(String userName, String deptName, String opResult,
			String memo, ActiveTask activeTask, List<TaskData> taskList)
			throws WorkFlowException {
		return saveTask(activeTask.getAssignee(), userName, deptName, opResult, memo, activeTask, taskList);
	}
	
	@Override
//	@Transactional
	public Boolean saveTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, 
			List<TaskData> taskList) throws WorkFlowException {
		try {
			int res = stcsmAuditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList);
			if(res == SystemConstant.NORMAL_CODE) {
				// 保存下一个任务节点名称或流程终止状态
				TaskStatus status = new TaskStatus();
				status.setId(IDGeneratorUtil.generatorId());
				status.setContentId(activeTask.getContentId());
				status.setProcessInstanceId(activeTask.getProcessInstanceId());
		        if(taskList != null && taskList.size() > 0) {
		        	status.setTaskDefId(taskList.get(0).getTaskDefId());
		        	status.setTaskName(taskList.get(0).getName());
		        }else {
		        	status.setIsActivce(1);
		        }
				stcsmTaskStatusService.saveStcsmTaskStatus(status);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}

	@Override
	public Boolean saveTask(String userId, String userName, String deptName,
			String opResult, String memo, ActiveTask activeTask,
			List<TaskData> taskList, Map<String, String> exValues)
			throws WorkFlowException {
		try {
			int res = stcsmAuditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
			logger.info("stcsmAuditLogService.auditTask:"+res);
			if(res == SystemConstant.NORMAL_CODE) {
				// 保存下一个任务节点名称或流程终止状态
				TaskStatus status = new TaskStatus();
				status.setId(IDGeneratorUtil.generatorId());
				status.setContentId(activeTask.getContentId());
				status.setProcessInstanceId(activeTask.getProcessInstanceId());
		        if(taskList != null && taskList.size() > 0) {
		        	status.setTaskDefId(taskList.get(0).getTaskDefId());
		        	status.setTaskName(taskList.get(0).getName());
		        }else {
		        	status.setIsActivce(1);
		        }
				stcsmTaskStatusService.saveStcsmTaskStatus(status);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}

	@Override
	public Boolean saveTask(String userId, String userName, String deptName,
			String opResult, String memo, ActiveTask activeTask,
			List<TaskData> taskList, Map<String, String> exValues, String appellation)
			throws WorkFlowException {
		try {
			int res = stcsmAuditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues, appellation);
			if(res == SystemConstant.NORMAL_CODE) {
				// 保存下一个任务节点名称或流程终止状态
				TaskStatus status = new TaskStatus();
				status.setId(IDGeneratorUtil.generatorId());
				status.setContentId(activeTask.getContentId());
				status.setProcessInstanceId(activeTask.getProcessInstanceId());
				if(taskList != null && taskList.size() > 0) {
		        	status.setTaskDefId(taskList.get(0).getTaskDefId());
		        	status.setTaskName(taskList.get(0).getName());
		        }else {
		        	status.setIsActivce(1);
		        }
				stcsmTaskStatusService.saveStcsmTaskStatus(status);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}
	
	@Override
	public Boolean saveTask(String userId, String activeTaskId, List<TaskData> taskList, Map<String, String> exValues) throws WorkFlowException {
		/*if(exValues == null || exValues.isEmpty()) {
			throw new WorkFlowException("60501", "任务缺少审核相关参数");
		}
		if(StringUtils.isEmpty(exValues.get("userName"))) {
			throw new WorkFlowException("60501", "任务缺少审核参数-操作人");
		}*/
		ActiveTask activeTask = getActiveTaskById(activeTaskId);
		if(activeTask == null) {
			throw new WorkFlowException("60501", "任务不存在");
		}
		try {
			AuditLog log=null;
			if(exValues != null && !exValues.isEmpty()) {
				String json = JSONObject.toJSONString(exValues);
				log = JSONObject.parseObject(json, AuditLog.class);
			}
			/*System.out.println("userId:"+userId);
			System.out.println("log:"+log);
			System.out.println("activeTask:"+activeTask.toString());
			System.out.println("activeTask getProcessInstanceId:"+activeTask.getProcessInstanceId());
			System.out.println("taskList:"+taskList);
			System.out.println("exValues:"+exValues);*/
			//int res = stcsmAuditLogService.auditTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
			int res = stcsmAuditLogService.auditTask(userId, log, activeTask, taskList, exValues);
			if(res == SystemConstant.NORMAL_CODE) {
				// 保存下一个任务节点名称或流程终止状态
				TaskStatus status = new TaskStatus();
				status.setId(IDGeneratorUtil.generatorId());
				status.setContentId(activeTask.getContentId());
				status.setProcessInstanceId(activeTask.getProcessInstanceId());
		        if(taskList != null && taskList.size() > 0) {
		        	System.out.println("==========taskList:"+taskList.size());
		        	status.setTaskDefId(taskList.get(0).getTaskDefId());
		        	status.setTaskName(taskList.get(0).getName());
		        }else {
		        	status.setIsActivce(1);
		        }
				stcsmTaskStatusService.saveStcsmTaskStatus(status);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}

	@Override
	public Boolean retrieveTask(String userId, String finishedTaskId, List<TaskData> taskList,
			Map<String, String> exValues) throws WorkFlowException {
		if(exValues == null || exValues.isEmpty()) {
			throw new WorkFlowException("60501", "任务缺少审核相关参数");
		}
		if(StringUtils.isEmpty(exValues.get("userName"))) {
			throw new WorkFlowException("60501", "任务缺少审核参数-操作人");
		}
		FinishedTask finishedTask = stcsmFinishedTaskService.getFinishedTaskById(finishedTaskId);
		if(finishedTask == null) {
			throw new WorkFlowException("60501", "任务不存在");
		}
		try {
			String json = JSONObject.toJSONString(exValues);
			AuditLog log = JSONObject.parseObject(json, AuditLog.class);
			int res = stcsmAuditLogService.retrieveTask(userId, log, finishedTask, taskList, exValues);
			if(res == SystemConstant.NORMAL_CODE) {
				// 保存下一个任务节点名称或流程终止状态
				TaskStatus status = new TaskStatus();
				status.setId(IDGeneratorUtil.generatorId());
				status.setContentId(finishedTask.getContentId());
				status.setProcessInstanceId(finishedTask.getProcessInstanceId());
		        if(taskList != null && taskList.size() > 0) {
		        	status.setTaskDefId(taskList.get(0).getTaskDefId());
		        	status.setTaskName(taskList.get(0).getName());
		        }else {
		        	status.setIsActivce(1);
		        }
				stcsmTaskStatusService.saveStcsmTaskStatus(status);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean setTaskAssignee(String userId, String activeTaskId, Map<String, String> exValues)
			throws WorkFlowException {
		if(exValues == null || exValues.isEmpty()) {
			throw new WorkFlowException("60501", "任务缺少审核相关参数");
		}
		if(StringUtils.isEmpty(exValues.get("userName"))) {
			throw new WorkFlowException("60501", "任务缺少审核参数-操作人");
		}
		ActiveTask activeTask = getActiveTaskById(activeTaskId);
		if(activeTask == null) {
			throw new WorkFlowException("60501", "任务不存在");
		}
		try {
			// 操作日志
			String json = JSONObject.toJSONString(exValues);
			AuditLog log = JSONObject.parseObject(json, AuditLog.class);
			// 更新任务操作人
			ActiveTask item = new ActiveTask();
			item.setId(activeTask.getId());
			item.setAssignee(userId);
			item.setContentId(activeTask.getContentId());
			item.setProcessInstanceId(activeTask.getProcessInstanceId());
			item.setTaskName(activeTask.getTaskName());
			int res = stcsmAuditLogService.setTaskAssignee(log, item, exValues);
			if(res == SystemConstant.NORMAL_CODE) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}
	
	@Override
	public ActiveTask getActiveTaskById(String id) {
		return stcsmActiveTaskService.getActiveTaskById(id);
	}

	@Override
	public ActiveTask getActiveTask(String userId, String taskDataId) {
		// 查询条件
		ActiveTask params = new ActiveTask();
//		params.setAssignee(userId);
		params.setTaskId(taskDataId);
		List<ActiveTask> res = stcsmActiveTaskService.listAll(params);
		if(res != null && !res.isEmpty()) {
			return res.get(0);
		}
		return null;
	}
	
	@Override
	public ActiveTask getActiveTaskByProcessInstId(String userId,
			String deptCode, String processInstanceId) {
		// 查询条件
		ActiveTask params = new ActiveTask();
		params.setAssignee(userId);
		if(!StringUtils.isEmpty(deptCode)) {
			List<String> depts = new ArrayList<String>();
			depts.add(userId);
			depts.add(ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + deptCode);
			depts.add(ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + deptCode);
			params.setAssigneeGroup(depts);
		}
		params.setProcessInstanceId(processInstanceId);
		List<ActiveTask> res = stcsmActiveTaskService.listAll(params);
		if(res != null && !res.isEmpty()) {
			return res.get(0);
		}
		return null;
	}

	@Override
	public ActiveTask getActiveTaskByProcessInstId(String userId, String deptCode, String processInstanceId,
			String parentProcessInstanceId) {
		// 查询条件
		ActiveTask params = new ActiveTask();
		params.setAssignee(userId);
		if(!StringUtils.isEmpty(deptCode)) {
			List<String> depts = new ArrayList<String>();
			depts.add(userId);
			depts.add(ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + deptCode);
			depts.add(ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + deptCode);
			params.setAssigneeGroup(depts);
		}
		params.setProcessInstanceId(processInstanceId);
		params.setRootProcessInstanceId(parentProcessInstanceId);
		List<ActiveTask> res = stcsmActiveTaskService.listAll(params);
		if(res != null && !res.isEmpty()) {
			return res.get(0);
		}
		return null;
	}
	
	@Override
	public PageList<ActiveTask> pageTasks(String categoryType,
			List<String> accessScope, String deptCode, String processInstanceId,
			String parentTaskDefId, String taskDefId, String taskNo,
			String userId, int pageNum, int pageSize) {
		// 查询条件
		ActiveTask params = new ActiveTask();
		params.setCategoryType(categoryType);
		params.setAccessScopeList(accessScope);
		params.setParentTaskDefId(parentTaskDefId);
		params.setTaskDefId(taskDefId);
		params.setTaskNo(taskNo);
		params.setAssignee(userId);
		if(!StringUtils.isEmpty(deptCode)) {
			List<String> depts = new ArrayList<String>();
			depts.add(userId);
			depts.add(ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + deptCode);
			depts.add(ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + deptCode);
			params.setAssigneeGroup(depts);
		}
		params.setProcessInstanceId(processInstanceId);
		// 分页
		PageInput page = new PageInput();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		// 查询结果
		Page<ActiveTask> pageRes = stcsmActiveTaskService.pageList(params, page);
		PageList<ActiveTask> res = new PageList<ActiveTask>();
		if(pageRes != null) {
			res.setTotal((int)pageRes.getTotal());
			res.setRows(pageRes.getRecords());
			res.setPageNum((int)pageRes.getCurrent());
			res.setPageSize((int)pageRes.getSize());
		}
		return res;
	}

	@Override
	public PageList<FinishedTask> pageFinishedTasks(String categoryType,
			List<String> accessScope, String processInstanceId,
			String parentTaskDefId, String taskDefId, String taskNo,
			String userId, String userName, String opResult, int pageNum, int pageSize) {
		// 查询条件
		FinishedTask params = new FinishedTask();
		params.setCategoryType(categoryType);
		params.setAccessScopeList(accessScope);
		params.setParentTaskDefId(parentTaskDefId);
		params.setTaskDefId(taskDefId);
		params.setTaskNo(taskNo);
		params.setRealAssignee(userId);
		params.setRealAssigneeName(userName);
		params.setOpResult(opResult);
		params.setProcessInstanceId(processInstanceId);
		// 分页
		PageInput page = new PageInput();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		// 查询结果
		Page<FinishedTask> pageRes = stcsmFinishedTaskService.pageList(params, page);
		PageList<FinishedTask> res = new PageList<FinishedTask>();
		if(pageRes != null) {
			res.setTotal((int)pageRes.getTotal());
			res.setRows(pageRes.getRecords());
			res.setPageNum((int)pageRes.getCurrent());
			res.setPageSize((int)pageRes.getSize());
		}
		return res;
	}

	@Override
	public PageList<AuditLog> pageAuditLogs(String contentId, String processInstanceId,
			String businessNode, String userName, int pageNum, int pageSize) {
		// 查询条件
		AuditLog input = new AuditLog();
		input.setContentId(contentId);
		input.setProcessInstanceId(processInstanceId);
		input.setBusinessNode(businessNode);
		input.setUserName(userName);
		
		PageList<AuditLog> res = new PageList<AuditLog>();
		// 条件查询
		if(pageNum == 0 && pageSize == 0) {
			List<AuditLog> list = stcsmAuditLogService.listAll(input);
			res.setTotal(list==null ? 0 : list.size());
			res.setRows(list);
			res.setPageNum(pageNum);
			res.setPageSize(pageSize);
			return res;
		}
		// 分页
		PageInput page = new PageInput();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		// 查询结果
		Page<AuditLog> pageRes = stcsmAuditLogService.pageList(input, page);
		if(pageRes != null) {
			res.setTotal((int)pageRes.getTotal());
			res.setRows(pageRes.getRecords());
			res.setPageNum((int)pageRes.getCurrent());
			res.setPageSize((int)pageRes.getSize());
		}
		return res;
	}

	@Override
	public PageList<AuditLog> pageAuditLogs(int hasFile, String contentId, String processInstanceId, String processInitiator,
			String businessNode, String userName, int pageNum, int pageSize) {
		// 查询条件
		AuditLog input = new AuditLog();
		input.setContentId(contentId);
		input.setProcessInstanceId(processInstanceId);
		input.setBusinessNode(businessNode);
		input.setUserName(userName);
		input.setProcessInitiator(processInitiator);
		
		// 是否有附件(0查询全部1带附件的2不带附件的)
		input.setAuditFileUrl(String.valueOf(hasFile));
		
		PageList<AuditLog> res = new PageList<AuditLog>();
		// 条件查询
		if(pageNum == 0 && pageSize == 0) {
			List<AuditLog> list = stcsmAuditLogService.listAll(input);
			res.setTotal(list==null ? 0 : list.size());
			res.setRows(list);
			res.setPageNum(pageNum);
			res.setPageSize(pageSize);
			return res;
		}
		// 分页
		PageInput page = new PageInput();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		// 查询结果
		Page<AuditLog> pageRes = stcsmAuditLogService.pageList(input, page);
		if(pageRes != null) {
			res.setTotal((int)pageRes.getTotal());
			res.setRows(pageRes.getRecords());
			res.setPageNum((int)pageRes.getCurrent());
			res.setPageSize((int)pageRes.getSize());
		}
		return res;
	}

	@Override
	public AuditLog queryLastFile(String contentId, String processInstanceId) throws WorkFlowException {
		if(StringUtils.isEmpty(contentId) && StringUtils.isEmpty(processInstanceId)) {
			throw new WorkFlowException("60501", "任务缺少查询参数");
		}
		return stcsmAuditLogService.queryLastFile(contentId, processInstanceId);
	}
	
	@Override
	public List<TotalTasks> getTotalTasks(String userId, List<String> unitIds, List<String> adminUnitIds, List<String> itemsUnitIds, List<String> accessScope, List<String> businessTypeList) {
		// 整合查询条件(用户id+单位/部门code列表+是管理员的单位/部门code列表)
		List<String> assigneeGroup = WorkflowUtil.makeClaims(userId, null, unitIds, adminUnitIds, itemsUnitIds, null);
		QueryWrapper<TotalTasks> query = new QueryWrapper<TotalTasks>();
		if(assigneeGroup!=null){
			query.eq("assigneeGroup", assigneeGroup);
		}
		if(accessScope!=null) {
			query.eq("accessScopeList", accessScope);
		}
		if(businessTypeList!=null) {
			query.eq("businessTypeList", businessTypeList);
		}
		return stcsmActiveTaskService.getTotalTasks(query);
	}

	@Override
	public List<String> makeClaims(String userId, String region,
			List<String> unitIds, List<String> adminUnitIds, List<String> itemsUnitIds,
			List<String> jobIdList) {
		return WorkflowUtil.makeClaims(userId, null, unitIds, adminUnitIds, itemsUnitIds, null);
	}

	@Override
	public Integer countTaskByUser(String userId, List<String> businessTypeList) {
		return stcsmFinishedTaskService.countTaskByUser(userId, businessTypeList);
	}

	@Override
	public boolean updateStatus(String processDefinitionId,
			String processInstanceId, String processTaskId, int status) {
		if(StringUtils.isEmpty(processDefinitionId) 
				&& StringUtils.isEmpty(processInstanceId)
				&& StringUtils.isEmpty(processTaskId)) {
			return false;
		}
		stcsmActiveTaskService.updateStatus(processDefinitionId, processInstanceId, processTaskId, status);
		return true;
	}

	@Override
	public boolean complateTask(String userId, String processInstanceId,
			String reason) {
		if(StringUtils.isEmpty(processInstanceId)) {
			return false;
		}
		if(StringUtils.isEmpty(reason)) {
			return false;
		}
		stcsmAuditLogService.auditTask(userId, processInstanceId, reason);
		return true;
	}

	@Override
	public Boolean saveTask_forrws(String userId, String userName, String deptName, String opResult, String memo,
			ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues) throws WorkFlowException {
		try {
			int res = stcsmAuditLogService.auditTask_forrws(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
			if(res == SystemConstant.NORMAL_CODE) {
				// 保存下一个任务节点名称或流程终止状态
				TaskStatus status = new TaskStatus();
				status.setId(IDGeneratorUtil.generatorId());
				status.setContentId(activeTask.getContentId());
				status.setProcessInstanceId(activeTask.getProcessInstanceId());
		        if(taskList != null && taskList.size() > 0) {
		        	status.setTaskDefId(taskList.get(0).getTaskDefId());
		        	status.setTaskName(taskList.get(0).getName());
		        }else {
		        	status.setIsActivce(1);
		        }
				stcsmTaskStatusService.saveStcsmTaskStatus(status);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException(WorkflowConstant.TASK_COMPLATE_FAIL, e.getMessage());
		}
		return false;
	}
	

}
