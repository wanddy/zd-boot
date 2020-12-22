package workflow.business.service.impl;

import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.model.JudgeInfo;
import workflow.business.model.JudgeProperty;
import workflow.business.model.TaskData;
import workflow.business.model.UserInfo;
import workflow.business.service.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.common.error.WorkFlowException;
import workflow.olddata.constant.SystemConstant;
import workflow.olddata.model.PageInput;
import workflow.olddata.util.IDGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import workflow.business.mapper.AuditLogMapper;
import workflow.business.model.ActiveTask;
import workflow.business.model.AuditLog;
import workflow.business.model.FinishedTask;
import workflow.business.model.TaskContentData;
//import commons.system.AuthInfoUtil;
//import commons.system.dto.UserDepartDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service("AuditLogService")
@DS("master")
public class AuditLogServiceImpl implements AuditLogService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AuditLogMapper auditLogMapper;
	@Autowired
	private FinishedTaskService stcsmFinishedTaskService;
	@Autowired
	private ActiveTaskService activeTaskService;
	@Autowired
	private WorkflowService workflowService;
	@Autowired
	private TaskStatusService taskStatusService;
	@Autowired
	private AuthInfo authInfoUtil;

	@Override
	public Page<AuditLog> pageList(AuditLog input, PageInput page) {
		QueryWrapper<AuditLog> query = new QueryWrapper<AuditLog>();
		if(input != null) {
			query.eq("contentId", input.getContentId());
			query.eq("processInstanceId", input.getProcessInstanceId());
			query.eq("businessNode", input.getBusinessNode());
			query.eq("userName", input.getUserName());
			query.eq("processInitiator", input.getProcessInitiator());
			query.eq("auditFileUrl", input.getAuditFileUrl());
		}
		Page<AuditLog> pageInfo = new Page<AuditLog>();
		if(page != null) {
			pageInfo = new Page<AuditLog>(page.getPageNum(), page.getPageSize());
		}
		auditLogMapper.listAll(pageInfo, query);
		return pageInfo;
	}

	@Override
	public List<AuditLog> listAll(AuditLog params) {
		QueryWrapper<AuditLog> query = new QueryWrapper<AuditLog>();
		if(params != null) {
			if(params.getContentId()!=null){
				query.eq("content_id", params.getContentId());
			}
			if(params.getProcessInstanceId()!=null) {
				query.eq("process_instance_id", params.getProcessInstanceId());
			}
			if(params.getBusinessNode()!=null){
				query.eq("business_node", params.getBusinessNode());
			}
			if(params.getUserName()!=null) {
				query.eq("user_name", params.getUserName());
			}
			if(params.getProcessInitiator()!=null) {
				query.eq("process_initiator", params.getProcessInitiator());
			}
			if(params.getAuditFileUrl()!=null) {
				query.eq("audit_file_url", params.getAuditFileUrl());
			}
			query.orderByAsc("operate_time");
		}
		Page<AuditLog> pageInfo = new Page<AuditLog>(1, Integer.MAX_VALUE);
		Page<AuditLog> pageList = auditLogMapper.selectPage(pageInfo, query);
		return  pageList.getRecords();
//		return auditLogMapper.listAll(query);
	}

	@Override
//	@Transactional
	public int auditTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask,
			List<TaskData> taskList) {
		List<ActiveTask> finishList = new ArrayList<ActiveTask>();
		// 1、将当前任务保存成已办任务
		finishList.add(activeTask);
		// 2、保存当前审核日志
		AuditLog log = createAuditLog(userName, deptName, opResult, memo, activeTask);
		auditLogMapper.save(log);
		// 3、获取工作流返回的待办任务，没有待办任务，将当前待办任务保存为已办，并删除当前待办，结束当前任务
		if(taskList == null || taskList.size() < 1) {
			// 根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
//			stcsmActiveTaskService.deleteById(activeTask.getId());
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
			return SystemConstant.NORMAL_CODE;
		}
		// 4、获取工作流返回的待办任务，有待办任务，根据taskId查询本地待办任务
		List<ActiveTask> activeList = null;
		if(taskList != null && taskList.size() > 0) {
			TaskData taskData = taskList.get(0);
			activeList = activeTaskService.listActiveTask(taskData.getId(), activeTask.getId());
		}
		// 4-1、查询有数据，根据activeTask的id删除待办数据
		if(activeList != null && activeList.size() > 0) {
			// 删除当前待办
			activeTaskService.deleteById(activeTask.getId());
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
		}
		// 4-2、查询无数据，根据activeTask的taskId获取所有待办任务
		if(activeList == null || activeList.size() < 1) {
			// 4-2-1、将查询到当前任务id的所有待办任务保存为已办，操作人为当前任务完成人
			activeList = activeTaskService.listActiveTask(activeTask.getTaskId(), activeTask.getId());
			finishList.addAll(activeList);
			stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
			// 4-2-2、根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
			// 4-2-3、将工作流返回的待办任务保存为最新的待办任务
			TaskContentData data = new TaskContentData();
			// 其他信息
			data.setCategoryType(activeTask.getCategoryType());
			data.setAccessScope(activeTask.getAccessScope());
			data.setContentId(activeTask.getContentId());
			data.setContentName(activeTask.getContentName());
			data.setTaskNo(activeTask.getTaskNo());
			data.setTaskType(activeTask.getTaskType());

			// 根据认领用户参数判断是否将单个任务保存为多个待办任务
			taskList = getClaimUserTasks(taskList);
			activeTaskService.batchSave(activeTask.getProcessDefinitionName(), userId, log.getBusinessOperate(), taskList, data);
		}
		return SystemConstant.NORMAL_CODE;
	}

	/**
	 * 处理日志数据
	 * @param userName
	 * @param deptName
	 * @param opResult
	 * @param memo
	 * @param activeTask
	 * @return
	 */
	private AuditLog createAuditLog(String userName, String deptName, String opResult, String memo, ActiveTask activeTask) {
		AuditLog log = new AuditLog();
		log.setId(IDGeneratorUtil.generatorId());
		log.setProcessInstanceId(activeTask.getProcessInstanceId());
		log.setUserName(userName);
		log.setUnitName(deptName);
		log.setOperateTime(System.currentTimeMillis());
		log.setMemo(memo);
		// 流程发起人 2019年9月23日11:15:17
		log.setProcessInitiator(activeTask.getProcessInitiator());
		// 节点名称
		log.setBusinessNode(activeTask.getTaskName());
		log.setTaskDefId(activeTask.getTaskDefId());
		log.setParentTaskDefId(activeTask.getParentTaskDefId());
		// 业务id
		log.setContentId(activeTask.getContentId());
		// 操作结果描述
		if(opResult != null) {
			// 获取此任务的操作列表
			String judgeJson = activeTask.getJudgeList();
			if(!StringUtils.isEmpty(judgeJson)) {
				List<JudgeProperty> judgeList = JSONObject.parseArray(judgeJson, JudgeProperty.class);
				if(judgeList != null && judgeList.size() > 0) {
					for (JudgeProperty prop : judgeList) {
						// 根据操作结果获取操作结果描述(pass->审核通过)
						List<JudgeInfo> list = prop.getInfoList();
						if(list != null && list.size() > 0) {
							for (JudgeInfo judgeInfo : list) {
								if(judgeInfo.getValue().equals(opResult)) {
									log.setBusinessOperate(judgeInfo.getName());
									break;
								}
							}
						}
						// 获取到操作结果描述后，直接退出循环
						if(!StringUtils.isEmpty(log.getBusinessOperate())) {
							break;
						}
					}
				}
			}
		}
		return log;
	}

	/**
	 * 处理日志数据
	 * @param log
	 * @param
	 */
	private AuditLog dealAuditLog(String opResult, AuditLog log, String processInstanceId, String taskName, String taskDefId, String parentTaskDefId, String contentId, String judgeJson) {
		String newids=IDGeneratorUtil.generatorId();
		/*System.out.println("newids:"+newids);*/
		if(log==null){
			log=new AuditLog();
		}
		log.setId(newids);
		//System.out.println("processInstanceId:"+processInstanceId);
		log.setProcessInstanceId(processInstanceId);
		log.setOperateTime(System.currentTimeMillis());
		// 节点名称
		/*System.out.println("taskName:"+taskName);
		System.out.println("taskDefId:"+taskDefId);
		System.out.println("parentTaskDefId:"+parentTaskDefId);
		System.out.println("contentId:"+taskDefId);*/
		log.setBusinessNode(taskName);
		log.setTaskDefId(taskDefId);
		log.setParentTaskDefId(parentTaskDefId);
		// 业务id
		log.setContentId(contentId);
		// 操作结果描述
		if(opResult != null) {
			// 获取此任务的操作列表
			if(!StringUtils.isEmpty(judgeJson)) {
				List<JudgeProperty> judgeList = JSONObject.parseArray(judgeJson, JudgeProperty.class);
				if(judgeList != null && judgeList.size() > 0) {
					for (JudgeProperty prop : judgeList) {
						// 根据操作结果获取操作结果描述(pass->审核通过)
						List<JudgeInfo> list = prop.getInfoList();
						if(list != null && list.size() > 0) {
							for (JudgeInfo judgeInfo : list) {
								if(judgeInfo.getValue().equals(opResult)) {
									log.setBusinessOperate(judgeInfo.getName());
									break;
								}
							}
						}
						// 获取到操作结果描述后，直接退出循环
						if(!StringUtils.isEmpty(log.getBusinessOperate())) {
							break;
						}
					}
				}
			}
		}
		return log;
	}

	/**
	 * 需要保存的待办任务包含认领用户参数，则判断是否包含了认领用户列表，有则根据列表将此任务保存成多个待办任务
	 * @param taskList
	 * @return
	 */
	private List<TaskData> getClaimUserTasks(List<TaskData> taskList) {
		List<TaskData> list = new ArrayList<TaskData>();
		if(taskList != null && taskList.size() > 0) {
			List<String> claimUserList = null;
			for (TaskData taskData : taskList) {
				// 返回认领用户列表对应的待办任务,包含claim_users_list认领用户列表
				claimUserList = taskData.getAssignessList();
				if(claimUserList == null || claimUserList.size() < 1) {
					// 没有认领用户列表则还是只保存此待办任务
					list.add(taskData);
				} else {
					String json = JSONObject.toJSONString(taskData);
					// 根据待办任务中包含的认领用户list将此待办任务保存成多个待办
					for (String userId : claimUserList) {
						TaskData newTask = JSONObject.parseObject(json, TaskData.class);
						newTask.setAssigness(userId);
						list.add(newTask);
					}
				}
			}
		}
		return list;
	}

	@Override
	@Transactional
	public Boolean complateTask(String userId, String region,
			List<String> unitIds, Map<String, String> values,
			Map<String, List<String>> signUsers,
			Map<String, List<String>> claimUsers, Map<String, List<String>> claimGroups, String userName,
			String deptName, String opResult, String memo, ActiveTask activeTask) throws WorkFlowException {
		// 调用合并后的方法
		boolean bl = workflowService.complateTask(userId, userName, deptName, opResult, memo, activeTask, region, unitIds, activeTask.getTaskId(), values, signUsers, claimUsers, claimGroups, null);
		logger.info("workflowService.complateTask："+bl);
		return bl;
//        // 执行任务
//        List<TaskData> taskList = workflowService.complateTask(activeTask.getAssignee(), region, unitIds, activeTask.getTaskId(), values, signUsers, claimUsers, claimGroups, null);
//		// 处理本地待办、已办数据
//        // 提醒业务系统保存 下一节点和流程终止状态
//        TaskStatus status = new TaskStatus();
//		status.setContentId(activeTask.getContentId());
//        if(taskList != null && taskList.size() > 0) {
//        	status.setTaskName(taskList.get(0).getName());
//        }else {
//        	status.setIsActivce(1);
//        }
//		stcsmTaskStatusService.updateStcsmTaskStatus(status);
//		// 保存待办、已办、审核日志等信息
//		int res = auditTask(userName, deptName, opResult, memo, activeTask, taskList);
//		if(res == SystemConstant.NORMAL_CODE) {
//			return true;
//		}
//		return false;
	}

	@Override
	@Transactional
	public Boolean startProcess(String userId, String processKey,
			String businessKey, TaskContentData data) throws WorkFlowException {
		ActiveTask task = workflowService.startProcessInstanceByKey(userId, processKey, null, businessKey, data);
		if(task != null) {
			// 通过MQ启动流程时，调用updateProcessInstToContent存储过程，使业务数据库保存流程实例id
			taskStatusService.updateProcessInstToContent(task.getProcessInstanceId(), task.getContentId());
			return true;
		}
//		List<TaskData> taskList = workflowService.startProcessInstanceByKey(userId, processKey, null, businessKey);
//		if(taskList != null && taskList.size() > 0) {
//			// 查询流程名称
//			ProcessInstData inst = workflowService.getProcessInstData(taskList.get(0).getProcessInstanceId());
//			int count = stcsmActiveTaskService.batchSave(inst.getName(), userId, taskList, data);
//			if(count > 0) {
//				// 提醒业务系统保存 流程实例id
//				TaskStatus status = new TaskStatus();
//				status.setId(IDGeneratorUtil.generatorId());
//				status.setContentId(data.getContentId());
//				status.setProcessInstanceId(taskList.get(0).getProcessInstanceId());
//				status.setTaskName(taskList.get(0).getName());
//				stcsmTaskStatusService.saveStcsmTaskStatus(status);
//				return true;
//			}
//		}
		return false;
	}

	@Override
	public int auditTask(String userId, String userName, String deptName,
			String opResult, String memo, ActiveTask activeTask,
			List<TaskData> taskList, Map<String, String> exValues) {
		List<ActiveTask> finishList = new ArrayList<ActiveTask>();
		// 1、将当前任务保存成已办任务
		finishList.add(activeTask);
		// 操作结果
		String opResultDesc = null;
		// 数据权限范围(事项id)
		String accessScope = activeTask.getAccessScope();
		// 审核材料
		String auditFileUrl = null;
		String auditFileName = null;
		if(exValues != null && !exValues.isEmpty()) {
			opResultDesc = exValues.get("opResultDesc");
			accessScope = StringUtils.isEmpty(exValues.get("accessScope")) ? accessScope : exValues.get("accessScope");
			auditFileUrl = exValues.get("auditFileUrl");
			auditFileName = exValues.get("auditFileName");
		}
	    // 2、保存当前审核日志
		AuditLog log = createAuditLog(userName, deptName, opResult, memo, activeTask);
		log.setAuditFileUrl(auditFileUrl);
		log.setAuditFileName(auditFileName);
		auditLogMapper.save(log);
		// 3、获取工作流返回的待办任务，没有待办任务，将当前待办任务保存为已办，并删除当前待办，结束当前任务
		if(taskList == null || taskList.size() < 1) {
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
			// 根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
			return SystemConstant.NORMAL_CODE;
		}
		// 4、获取工作流返回的待办任务，有待办任务，根据taskId查询本地待办任务
		List<ActiveTask> activeList = null;
		if(taskList != null && taskList.size() > 0) {
            TaskData taskData = taskList.get(0);
            activeList = activeTaskService.listActiveTask(taskData.getId(), activeTask.getId());
        }
        // 4-1、查询有数据，根据activeTask的id删除待办数据
        if(activeList != null && activeList.size() > 0) {
            // 保存已办
            stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
            // 删除当前待办
            activeTaskService.deleteById(activeTask.getId());
        }
        // 4-2、查询无数据，根据activeTask的taskId获取所有待办任务
        if(activeList == null || activeList.size() < 1) {
            // 4-2-1、将查询到当前任务id的所有待办任务保存为已办，操作人为当前任务完成人
            activeList = activeTaskService.listActiveTask(activeTask.getTaskId(), activeTask.getId());
            finishList.addAll(activeList);
            stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
            // 4-2-2、根据taskId删除所有待办任务
            activeTaskService.delete(null, activeTask.getTaskId());
            // 4-2-3、将工作流返回的待办任务保存为最新的待办任务
            TaskContentData data = new TaskContentData();
            // 其他信息
            data.setCategoryType(activeTask.getCategoryType());
            data.setAccessScope(accessScope);
            data.setContentId(activeTask.getContentId());
            data.setContentName(activeTask.getContentName());
            data.setTaskNo(activeTask.getTaskNo());
            data.setTaskType(activeTask.getTaskType());

            // 根据认领用户参数判断是否将单个任务保存为多个待办任务
            taskList = getClaimUserTasks(taskList);
            activeTaskService.batchSave(activeTask.getProcessDefinitionName(), activeTask.getAssignee(), opResultDesc, taskList, data);
		}

		return SystemConstant.NORMAL_CODE;
	}
	@Override
	public int auditTask(String userId, String userName, String deptName,
			String opResult, String memo, ActiveTask activeTask,
			List<TaskData> taskList, Map<String, String> exValues ,String appellation) {
		List<ActiveTask> finishList = new ArrayList<ActiveTask>();
		// 1、将当前任务保存成已办任务
		finishList.add(activeTask);
		// 操作结果
		String opResultDesc = null;
		// 数据权限范围(事项id)
		String accessScope = activeTask.getAccessScope();
		// 审核材料
		String auditFileUrl = null;
		String auditFileName = null;
		if(exValues != null && !exValues.isEmpty()) {
			opResultDesc = exValues.get("opResultDesc");
			accessScope = StringUtils.isEmpty(exValues.get("accessScope")) ? accessScope : exValues.get("accessScope");
			auditFileUrl = exValues.get("auditFileUrl");
			auditFileName = exValues.get("auditFileName");
		}
		// 2、保存当前审核日志
		AuditLog log = createAuditLog(userName, deptName, opResult, memo, activeTask);
		log.setAuditFileUrl(auditFileUrl);
		log.setAuditFileName(auditFileName);
		log.setAppellation(appellation);
		auditLogMapper.save(log);

		// 3、获取工作流返回的待办任务，没有待办任务，将当前待办任务保存为已办，并删除当前待办，结束当前任务
		if(taskList == null || taskList.size() < 1) {
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
			// 根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
			return SystemConstant.NORMAL_CODE;
		}
		List<TaskData> newtasklist=getClaimUserTasks(taskList);
		// 4、获取工作流返回的待办任务，有待办任务，根据taskId查询本地待办任务
		List<ActiveTask> activeList = null;
        String batch = String.valueOf(System.currentTimeMillis());//统一设置批次号，防止批次号在循环过程中出现轻微差别
		if(taskList != null && taskList.size() > 0) {
            for (int i = 0; i < taskList.size(); i++) {
            	TaskData taskData = taskList.get(i);
            	activeList = activeTaskService.listActiveTask(taskData.getId(), activeTask.getId());
    			// 4-1、查询有数据，根据activeTask的id删除待办数据
    			if(activeList != null && activeList.size() > 0) {
    				// 保存已办
    				stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
    				// 删除当前待办
    				activeTaskService.deleteById(activeTask.getId());
    			}
    			// 4-2、查询无数据，根据activeTask的taskId获取所有待办任务
    			if(activeList == null || activeList.size() < 1) {
    				// 4-2-1、将查询到当前任务id的所有待办任务保存为已办，操作人为当前任务完成人
    				activeList = activeTaskService.listActiveTask(activeTask.getTaskId(), activeTask.getId());
    				finishList.addAll(activeList);
    				stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
    				// 4-2-2、根据taskId删除所有待办任务
    				activeTaskService.delete(null, activeTask.getTaskId());
    				// 4-2-3、将工作流返回的待办任务保存为最新的待办任务
    				TaskContentData data = new TaskContentData();
    				// 其他信息
    				data.setCategoryType(activeTask.getCategoryType());
    				data.setAccessScope(accessScope);
    				data.setContentId(activeTask.getContentId());
    				data.setContentName(activeTask.getContentName());
    				data.setTaskNo(activeTask.getTaskNo());
    				data.setTaskType(activeTask.getTaskType());

    				//针对指南拟立项修改并行退回后其他处室会签同步删除操作 chenx
    				String contentid=activeTask.getContentId();
    				List<ActiveTask> activeList2= activeTaskService.listActiveTaskByContentId(contentid);
    				if(opResult.equals("backnow")){
    					if(activeList2!=null && activeList2.size()>0){
    						for (int j = 0; j < activeList2.size(); j++) {
    							ActiveTask acttask=activeList2.get(j);
    							activeTaskService.delete(null, acttask.getTaskId());
    						}
    					}
    				}

					// 根据认领用户参数判断是否将单个任务保存为多个待办任务
					taskList = getClaimUserTasks(taskList);
					if(taskList!=null && taskList.size()>0){
						for (int i1 = 0; i1 < taskList.size(); i1++) {
							TaskData taskData_new=taskList.get(i1);
							if(activeList2!=null && activeList2.size()>0){
								for (int j = 0; j < activeList2.size(); j++) {
									ActiveTask acttask=activeList2.get(j);
									String processKey = "";
									if(!StringUtils.isEmpty(taskData_new.getProcessDefinitionId())) {
										String[] arr = taskData_new.getProcessDefinitionId().split(":");
										processKey = arr[0];
									}
									if(acttask.getTaskId().equals(taskData_new.getId()) && acttask.getTaskDefId().equals(taskData_new.getTaskDefId()) && acttask.getAssignee().equals(taskData_new.getAssigness())
											&& acttask.getProcessKey().equals(processKey) ){
										newtasklist.remove(taskData_new);
									}
								}
							}
						}
					}

					if(newtasklist!=null && newtasklist.size()>0){
						logger.info("newtasklist:"+newtasklist.size());
						for (TaskData taskData_new : newtasklist) {
                            activeTaskService.batchSave(activeTask.getProcessDefinitionName(), activeTask.getAssignee(), opResultDesc, taskData_new, data, batch);
						}
					}

    			}
            }
		}


		return SystemConstant.NORMAL_CODE;
	}

	@Override
	public int auditTask(String userId, String processInstanceId, String reason) {
		// 查询待办任务
		// 查询条件
		ActiveTask params = new ActiveTask();
		params.setProcessInstanceId(processInstanceId);
		List<ActiveTask> list = activeTaskService.listAll(params);
		if(list != null && list.size() > 0) {
			// 获取用户信息
			UserInfo user = loadUserInfo(userId);
			// 保存当前审核日志
			AuditLog log = createAuditLog(user.getNick(), null, "finished", reason, list.get(0));
			auditLogMapper.save(log);
			// 将所有待办保存成已办
			stcsmFinishedTaskService.saveFinishedTask(userId, user.getNick(), "finished", reason, list);
			// 根据taskId删除所有待办任务
			activeTaskService.delete(null, list.get(0).getTaskId());
		}
		return SystemConstant.NORMAL_CODE;
	}

	/**
	 * @Title: loadUserInfo
	 * @Description: 加载用户详情
	 * @param userId
	 * @return 参数说明
	 * @return UserInfo 返回类型
	 *
	 */
	private UserInfo loadUserInfo(String userId) {
		UserInfo info = new UserInfo();
		info.setId(userId);
		info.setNick(userId);
		// 请求用户中心获取用户名字
		try{
			List<UserDepartDto> userdeparlist=authInfoUtil.getUserById(userId);
			if(userdeparlist==null || userdeparlist.size()<1){
				info.setNick(userId);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	@Override
	public int auditTask(String userId, AuditLog log, ActiveTask activeTask, List<TaskData> taskList,
			Map<String, String> exValues) {
		List<ActiveTask> finishList = new ArrayList<ActiveTask>();
		// 1、将当前任务保存成已办任务
		finishList.add(activeTask);
	    // 2、保存当前审核日志
		// 操作结果
		String opResult = null;
		// 操作结果描述
		String opResultDesc = null;
		// 操作结果描述(存入待办任务的上一节点操作结果字段)
		String preOpResultDesc = null;
		// 数据权限范围(事项id)
		String accessScope = activeTask.getAccessScope();
		if(exValues != null && !exValues.isEmpty()) {
			opResult = exValues.get("opResult");
			opResultDesc = exValues.get("opResultDesc");
			preOpResultDesc = exValues.get("preOpResultDesc");
			accessScope = StringUtils.isEmpty(exValues.get("accessScope")) ? accessScope : exValues.get("accessScope");
		}
		log= dealAuditLog(opResult, log, activeTask.getProcessInstanceId(), activeTask.getTaskName(), activeTask.getTaskDefId(), activeTask.getParentTaskDefId(), activeTask.getContentId(),activeTask.getJudgeList());
		if(StringUtils.isEmpty(log.getBusinessOperate())) {
			log.setBusinessOperate(opResultDesc);
		}
		auditLogMapper.save(log);
		// 3、获取工作流返回的待办任务，没有待办任务，将当前待办任务保存为已办，并删除当前待办，结束当前任务
		if(taskList == null || taskList.size() < 1) {
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, log.getUserName(), opResult, log.getBusinessOperate(), finishList);
			// 根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
			return SystemConstant.NORMAL_CODE;
		}
		// 4、获取工作流返回的待办任务，有待办任务，根据taskId查询本地待办任务
		List<ActiveTask> activeList = null;
		if(taskList != null && taskList.size() > 0) {
			System.out.println("=============taskList.size() :"+taskList.size() );
			TaskData taskData = taskList.get(0);
			activeList = activeTaskService.listActiveTask(taskData.getId(), activeTask.getId());
		}

		// 4-1、查询有数据，根据activeTask的id删除待办数据
		if(activeList != null && activeList.size() > 0) {
			/*System.out.println("activeList.size:"+activeList.size());*/
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, log.getUserName(), opResult, log.getBusinessOperate(), finishList);
			// 删除当前待办
			activeTaskService.deleteById(activeTask.getId());
		}
		// 4-2、查询无数据，根据activeTask的taskId获取所有待办任务
		if(activeList == null || activeList.size() < 1) {
			// 4-2-1、将查询到当前任务id的所有待办任务保存为已办，操作人为当前任务完成人
			activeList = activeTaskService.listActiveTask(activeTask.getTaskId(), activeTask.getId());
			finishList.addAll(activeList);
			stcsmFinishedTaskService.saveFinishedTask(userId, log.getUserName(), opResult, log.getBusinessOperate(), finishList);
			// 4-2-2、根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
			// 4-2-3、将工作流返回的待办任务保存为最新的待办任务
			TaskContentData data = new TaskContentData();
			// 其他信息
			data.setCategoryType(activeTask.getCategoryType());
			data.setAccessScope(accessScope);
			data.setContentId(activeTask.getContentId());
			data.setContentName(activeTask.getContentName());
			data.setTaskNo(activeTask.getTaskNo());
			data.setTaskType(activeTask.getTaskType());

			// 根据认领用户参数判断是否将单个任务保存为多个待办任务
			taskList = getClaimUserTasks(taskList);
			activeTaskService.batchSave(activeTask.getProcessDefinitionName(), activeTask.getAssignee(), preOpResultDesc, taskList, data);
		}
		return SystemConstant.NORMAL_CODE;
	}

	@Override
	public int setTaskAssignee(AuditLog log, ActiveTask activeTask, Map<String, String> exValues) {
		// 1、更新待办任务（修改操作人）
		activeTaskService.updateActiveTask(activeTask);
		// 2、保存当前审核日志
		// 操作结果描述
		String opResultDesc = null;
		if(exValues != null && !exValues.isEmpty()) {
			opResultDesc = exValues.get("opResultDesc");
		}
		dealAuditLog(null, log, activeTask.getProcessDefinitionId(), activeTask.getTaskName(), activeTask.getTaskDefId(), activeTask.getParentTaskDefId(), activeTask.getContentId(),activeTask.getJudgeList());
		log.setBusinessOperate(opResultDesc);
		auditLogMapper.save(log);
		return SystemConstant.NORMAL_CODE;
	}

	@Override
	public AuditLog queryLastFile(String contentId, String processInstanceId) {
		QueryWrapper<AuditLog> query = new QueryWrapper<AuditLog>();
		query.eq("contentId", contentId);
		query.eq("processInstanceId", processInstanceId);
		return auditLogMapper.queryLastFile(query);
	}

	@Override
	public int retrieveTask(String userId, AuditLog log, FinishedTask finishedTask, List<TaskData> taskList,
			Map<String, String> exValues) {
		List<ActiveTask> finishList = new ArrayList<ActiveTask>();
	    // 1、保存当前审核日志
		// 操作结果
		String opResult = null;
		// 操作结果描述
		String opResultDesc = null;
		// 操作结果描述(存入待办任务的上一节点操作结果字段)
		String preOpResultDesc = null;
		// 数据权限范围(事项id)
		String accessScope = finishedTask.getAccessScope();
		if(exValues != null && !exValues.isEmpty()) {
			opResult = exValues.get("opResult");
			opResultDesc = exValues.get("opResultDesc");
			preOpResultDesc = exValues.get("preOpResultDesc");
			accessScope = StringUtils.isEmpty(exValues.get("accessScope")) ? accessScope : exValues.get("accessScope");
		}
		dealAuditLog(opResult, log, finishedTask.getProcessInstanceId(), finishedTask.getTaskName(), finishedTask.getTaskDefId(), finishedTask.getParentTaskDefId(), finishedTask.getContentId(), null);
		log.setBusinessOperate(opResultDesc);
		auditLogMapper.save(log);
		// 2、将当前业务数据的待办任务保存成已办
		List<ActiveTask> activeList = activeTaskService.listActiveTaskByContentId(finishedTask.getContentId());
		if(activeList != null && activeList.size() > 0) {
			finishList.addAll(activeList);
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, log.getUserName(), opResult, log.getBusinessOperate(), finishList);
			// 删除待办
			for (ActiveTask activeTask : activeList) {
				activeTaskService.deleteById(activeTask.getId());
			}
		}
		// 3、保存最新待办任务
		if(taskList != null && taskList.size() > 0) {
			// 将工作流返回的待办任务保存为最新的待办任务
			TaskContentData data = new TaskContentData();
			// 其他信息
			data.setCategoryType(finishedTask.getCategoryType());
			data.setAccessScope(accessScope);
			data.setContentId(finishedTask.getContentId());
			data.setContentName(finishedTask.getContentName());
			data.setTaskNo(finishedTask.getTaskNo());
			data.setTaskType(finishedTask.getTaskType());

			// 根据认领用户参数判断是否将单个任务保存为多个待办任务
			taskList = getClaimUserTasks(taskList);
			activeTaskService.batchSave(finishedTask.getProcessDefinitionName(), userId, preOpResultDesc, taskList, data);
		}
		return SystemConstant.NORMAL_CODE;
	}

	@Override
	public int auditTask_forrws(String userId, String userName, String deptName, String opResult, String memo,
			ActiveTask activeTask, List<TaskData> taskList, Map<String, String> exValues) {
		List<ActiveTask> finishList = new ArrayList<ActiveTask>();
		// 1、将当前任务保存成已办任务
		finishList.add(activeTask);
		// 操作结果
		String opResultDesc = null;
		// 数据权限范围(事项id)
		String accessScope = activeTask.getAccessScope();
		// 审核材料
		String auditFileUrl = null;
		String auditFileName = null;
		//同一批次时间戳
		String task_batch=null;
		if(exValues != null && !exValues.isEmpty()) {
			opResultDesc = exValues.get("opResultDesc");
			accessScope = StringUtils.isEmpty(exValues.get("accessScope")) ? accessScope : exValues.get("accessScope");
			auditFileUrl = exValues.get("auditFileUrl");
			auditFileName = exValues.get("auditFileName");
			task_batch = StringUtils.isEmpty(exValues.get("task_batch")) ? task_batch : exValues.get("task_batch");
		}
	    // 2、保存当前审核日志
		AuditLog log = createAuditLog(userName, deptName, opResult, memo, activeTask);
		log.setAuditFileUrl(auditFileUrl);
		log.setAuditFileName(auditFileName);
		auditLogMapper.save(log);
		// 3、获取工作流返回的待办任务，没有待办任务，将当前待办任务保存为已办，并删除当前待办，结束当前任务
		if(taskList == null || taskList.size() < 1) {
			// 保存已办
			stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
			// 根据taskId删除所有待办任务
			activeTaskService.delete(null, activeTask.getTaskId());
			return SystemConstant.NORMAL_CODE;
		}
		List<TaskData> newtasklist=getClaimUserTasks(taskList);
		// 4、获取工作流返回的待办任务，有待办任务，根据taskId查询本地待办任务
		List<ActiveTask> activeList = null;
		if(taskList != null && taskList.size() > 0) {
			for (int i = 0; i < taskList.size(); i++) {
				TaskData taskData = taskList.get(i);
				activeList = activeTaskService.listActiveTask(taskData.getId(), activeTask.getId());
				// 4-1、查询有数据，根据activeTask的id删除待办数据
				if(activeList != null && activeList.size() > 0) {
					// 保存已办
					stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
					// 删除当前待办
					activeTaskService.deleteById(activeTask.getId());
				}
				// 4-2、查询无数据，根据activeTask的taskId获取所有待办任务
				if(activeList == null || activeList.size() < 1) {
					// 4-2-1、将查询到当前任务id的所有待办任务保存为已办，操作人为当前任务完成人
					activeList = activeTaskService.listActiveTask(activeTask.getTaskId(), activeTask.getId());
					finishList.addAll(activeList);
					stcsmFinishedTaskService.saveFinishedTask(userId, userName, opResult, log.getBusinessOperate(), finishList);
					// 4-2-2、根据taskId删除所有待办任务
					activeTaskService.delete(null, activeTask.getTaskId());
					// 4-2-3、将工作流返回的待办任务保存为最新的待办任务
					TaskContentData data = new TaskContentData();
					// 其他信息
					data.setCategoryType(activeTask.getCategoryType());
					data.setAccessScope(accessScope);
					data.setContentId(activeTask.getContentId());
					data.setContentName(activeTask.getContentName());
					data.setTaskNo(activeTask.getTaskNo());
					data.setTaskType(activeTask.getTaskType());

					String contentid=activeTask.getContentId();
					List<ActiveTask> activeList2= activeTaskService.listActiveTaskByContentId(contentid);


					// 根据认领用户参数判断是否将单个任务保存为多个待办任务
					taskList = getClaimUserTasks(taskList);
					if(taskList!=null && taskList.size()>0){
						for (int i1 = 0; i1 < taskList.size(); i1++) {
							TaskData taskData_new=taskList.get(i1);
							if(activeList2!=null && activeList2.size()>0){
								for (int j = 0; j < activeList2.size(); j++) {
									ActiveTask acttask=activeList2.get(j);
									String processKey = "";
									if(!StringUtils.isEmpty(taskData_new.getProcessDefinitionId())) {
										String[] arr = taskData_new.getProcessDefinitionId().split(":");
										processKey = arr[0];
									}
									if(acttask.getTaskId().equals(taskData_new.getId()) && acttask.getTaskDefId().equals(taskData_new.getTaskDefId()) && acttask.getAssignee().equals(taskData_new.getAssigness())
											&& acttask.getProcessKey().equals(processKey) ){
										newtasklist.remove(taskData_new);
									}
								}
							}
						}
					}

					if(newtasklist!=null && newtasklist.size()>0){
						for (TaskData taskData_new : newtasklist) {
							if(task_batch==null){
								activeTaskService.batchSave(activeTask.getProcessDefinitionName(), activeTask.getAssignee(), opResultDesc, taskData_new, data);
							}else{
								activeTaskService.batchSave(activeTask.getProcessDefinitionName(), activeTask.getAssignee(), opResultDesc, taskData_new, data ,task_batch);
							}
						}
					}
				}
			}
		}

		return SystemConstant.NORMAL_CODE;
	}
}
