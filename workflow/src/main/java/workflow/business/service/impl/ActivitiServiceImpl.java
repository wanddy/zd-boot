package workflow.business.service.impl;

import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.service.ActivitiService;
import workflow.business.service.UserTaskFinishedService;
import com.alibaba.fastjson.JSONObject;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import workflow.business.mapper.UserReplaceDao;
import workflow.business.model.UserReplace;
import workflow.business.model.UserReplaceInfo;
import workflow.business.model.*;
import workflow.business.service.WorkflowService;
import workflow.common.constant.ActivitiConstant;
import workflow.common.constant.ActivitiConstant.EOrderType;
import workflow.common.error.WorkFlowException;
import workflow.common.redis.JedisMgr_wf;
import workflow.common.utils.CheckDataUtil;
import workflow.business.model.entity.UserTaskFinishedEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
/**
 * @ClassName: ActivitiServiceImpl
 * @Description: 老版工作流服务
 * @author KaminanGTO
 * @date 2018年9月11日 下午12:57:54
 *
 */
/*@Service(interfaceClass = ActivitiService.class, retries = 0)
@Component*/
@Component
@Service("activitiService")
@DS("master")
public class ActivitiServiceImpl implements ActivitiService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ManagementService managementService;

	@Autowired
	private UserTaskFinishedService userTaskFinishedService;

	@Autowired
	private JedisMgr_wf jedisMgrWf;

	@Autowired
	private UserReplaceDao userReplaceDao;

	@Autowired
	private AuthInfo authInfoUtil;

	@Autowired
	private WorkflowService workflowService;

	@Override
	public PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, int pageNum, int pageSize)
			throws WorkFlowException {
		return getProcessList(name, onlyLatestVersion, null, pageNum, pageSize);
	}

	@Override
	public PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, String businessType,
													  int pageNum, int pageSize) throws WorkFlowException {
		return workflowService.getProcessList(name, onlyLatestVersion, businessType, pageNum, pageSize);
	}

	@Override
	@Transactional
	public TaskData startProcessInstanceById(String userId, String processId, Map<String, Object> values)
			throws WorkFlowException {
		return startProcessInstanceById(userId, processId, values, null);
	}

	@Override
	public TaskData startProcessInstanceById(String userId, String processId, Map<String, Object> values,
											 String businessKey) throws WorkFlowException {
		CheckDataUtil.checkNull(processId, "processId");
		ProcessInstance pi = null;
		// 赋值启动者
		if (userId != null && !userId.isEmpty()) {
			if (values == null) {
				values = new HashMap<String, Object>();
			}
			values.put(ActivitiConstant.STARTER_KEY, userId);
		}
		// 如果业务key不存在，则赋值默认业务key
		if(CheckDataUtil.isNull(businessKey))
		{
			businessKey = ActivitiConstant.DEF_PROCESS_INST_BUSINESS_KEY;
		}
		try {
			if (values == null) {
				pi = runtimeService.startProcessInstanceById(processId, businessKey);
			} else {
				pi = runtimeService.startProcessInstanceById(processId, businessKey, values);
			}

		} catch (ActivitiObjectNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程不存在");
		}

		String taskId = initProcessParams(pi);
		if (taskId == null) {
			return null;
		}
		return getTaskInfo(taskId);
	}

	@Override
	@Transactional
	public TaskData startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values)
			throws WorkFlowException {
		return startProcessInstanceByKey(userId, processKey, values, null);
	}

	@Override
	public TaskData startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values,
											  String businessKey) throws WorkFlowException {
		CheckDataUtil.checkNull(processKey, "processKey");
		ProcessInstance pi = null;

		// 赋值启动者
		if (CheckDataUtil.isNotNull(userId)) {
			if (values == null) {
				values = new HashMap<String, Object>();
			}
			values.put(ActivitiConstant.STARTER_KEY, userId);
		}
		// 如果业务key不存在，则赋值默认业务key
		if(CheckDataUtil.isNull(businessKey))
		{
			businessKey = ActivitiConstant.DEF_PROCESS_INST_BUSINESS_KEY;
		}
		try {
			if (values == null) {
				pi = runtimeService.startProcessInstanceByKey(processKey, businessKey);
			} else {
				pi = runtimeService.startProcessInstanceByKey(processKey, businessKey, values);
			}
		} catch (ActivitiObjectNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程不存在");
		}

		String taskId = initProcessParams(pi);
		if (taskId == null) {
			return null;
		}
		return getTaskInfo(taskId);
	}

	/**
	 * 初始化流程参数
	 *
	 * @param pi
	 */
	private String initProcessParams(ProcessInstance pi) {
		Task task = null;
		try {
			task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		} catch (ActivitiException e) {
			return null;
		}
		if (task == null) {
			return null;
		}

		Map<String, Object> values = taskService.getVariables(task.getId());
		Map<String, List<String>> signUsers = new HashMap<String, List<String>>();
		Map<String, List<String>> claimUsers = new HashMap<String, List<String>>();
		Map<String, List<String>> claimJobs = new HashMap<String, List<String>>();
		for (String key : values.keySet()) {
			int idx = key.indexOf(ActivitiConstant.SIGN_USERS_ID_HEAD); // 会签用户
			if (idx == 0) {
				Object value = values.get(key);
				if (value != null) {
					String strValue = value.toString();
					if (!strValue.isEmpty()) {
						String[] users = strValue.split(",");
						List<String> userlist = java.util.Arrays.asList(users);
						signUsers.put(key, userlist);
					}
				}
				continue;
			}
			idx = key.indexOf(ActivitiConstant.CLAIM_USERS_ID_HEAD); // 候选用户
			if (idx == 0) {
				Object value = values.get(key);
				if (value != null) {
					String strValue = value.toString();
					if (!strValue.isEmpty()) {
						String[] users = strValue.split(",");
						List<String> userlist = java.util.Arrays.asList(users);
						claimUsers.put(key, userlist);
					}
				}
				continue;
			}
			idx = key.indexOf(ActivitiConstant.CLAIM_GROUP_JOBS_HEAD); // 候选角色
			if (idx == 0) {
				Object value = values.get(key);
				if (value != null) {
					String strValue = value.toString();
					if (!strValue.isEmpty()) {
						String[] jobs = strValue.split(",");
						List<String> jobList = java.util.Arrays.asList(jobs);
						claimJobs.put(key, jobList);
					}
				}
				continue;
			}
		}
		if (!signUsers.isEmpty()) {
			taskService.setVariables(task.getId(), signUsers);
		}
		if (!claimUsers.isEmpty()) {
			taskService.setVariables(task.getId(), claimUsers);
		}
		if (!claimJobs.isEmpty()) {
			taskService.setVariables(task.getId(), claimJobs);
		}
		return task.getId();
	}

	@Override
	@Transactional
	public boolean stopProcessInstance(String userId, String processInstanceId, String reason)
			throws WorkFlowException {
		return workflowService.stopProcessInstance(userId,processInstanceId,reason);

	}

	@Override
	@Transactional
	public boolean suspendProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
		return workflowService.suspendProcessInstance(userId, processInstanceId);
	}

	@Override
	@Transactional
	public boolean activateProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
		return workflowService.activateProcessInstance(userId, processInstanceId);
	}

	@Override
	public PageList<TaskSampleData> getTasksByUser(String userId, String processId, String taskName, String taskDefId,
												   boolean showClaim, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
		return getTasksByUser(userId, null, null, null, processId, null, taskName, taskDefId, showClaim, startTime,
				endTime, pageNum, pageSize);
	}

	@Override
	public PageList<TaskSampleData> getTasksByUser(String userId, String region, String unitId, List<String> jobIdList,
												   String processId, String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime,
												   int pageNum, int pageSize) throws WorkFlowException {
		return getTasksByUser(userId, region, unitId, jobIdList, processId, null, taskName, taskDefId, showClaim,
				startTime, endTime, pageNum, pageSize);
	}

	@Override
	public PageList<TaskSampleData> getTasksByUser(String userId, String region, String unitId, List<String> jobIdList,
												   String processId, String processBusinessType, String taskName, String taskDefId, boolean showClaim,
												   Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
		List<String> unitIds = null;
		if (CheckDataUtil.isNotNull(unitId)) {
			unitIds = new ArrayList<String>();
			unitIds.add(unitId);
		}
		return getTasksByUser(userId, region, unitIds, null, jobIdList, processId, processBusinessType, taskName,
				taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
	}

	@Override
	public PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds,
												   List<String> adminUnitIds, List<String> jobIdList, String processId, String processBusinessType,
												   String taskName, String taskDefId, boolean showClaim, Date startTime, Date endTime, int pageNum,
												   int pageSize) throws WorkFlowException {
		return getTasksByUser(userId, region, unitIds, adminUnitIds, jobIdList, processId, null, processBusinessType, null, taskName, taskDefId, showClaim, startTime, endTime, pageNum, pageSize);
	}

	@Override
	public PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds,
												   List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey,
												   String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId, boolean showClaim,
												   Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
		return getTasksByUser(userId, region, unitIds, adminUnitIds, jobIdList, processId, processKey, processBusinessType, processBusinessKeyList, taskName, taskDefId, showClaim, startTime, endTime, false, EOrderType.Desc, pageNum, pageSize);
	}

	@Override
	public PageList<TaskSampleData> getTasksByUser(String userId, String region, List<String> unitIds,
												   List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey,
												   String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId,
												   boolean showClaim, Date startTime, Date endTime, boolean hasBusinessKey, EOrderType orderByCreate, int pageNum,
												   int pageSize) throws WorkFlowException {
		// 不再强制要求查询用户id
		//CheckDataUtil.checkNull(userId, "userId");
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}

		int firstResult = (pageNum - 1) * pageSize;
		int maxResults = pageSize;

		TaskQuery taskQuery = taskService // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				.active(); // 指定只显示激活中的任务

		boolean isQueryJobs = false;
		List<String> claimGroupList = new ArrayList<String>();
		// 判断是否要查询候选角色
		if (CheckDataUtil.isNotNull(jobIdList)) {
			isQueryJobs = true;
			taskQuery = taskQuery.or();
			claimGroupList.addAll(jobIdList);
			// 判断是否要查询部门
			if (CheckDataUtil.isNotNull(unitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : unitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询部门管理员
			if (CheckDataUtil.isNotNull(adminUnitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : adminUnitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
						// 也判断非管理员部门
						claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询区域
			if (CheckDataUtil.isNotNull(region)) {
				for (String jobId : jobIdList) {
					String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ region;
					claimGroupList.add(claimGroup);
				}
			}

		}
		// 判断无角色部门保持
		if (CheckDataUtil.isNotNull(unitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : unitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色管理员部门保持
		if (CheckDataUtil.isNotNull(adminUnitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : adminUnitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
				// 也判断非管理员部门
				claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色区域保持
		if (CheckDataUtil.isNotNull(region)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION
					+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + region;
			claimGroupList.add(claimGroup);
		}
		if (!claimGroupList.isEmpty()) {
			taskQuery = taskQuery.taskCandidateGroupIn(claimGroupList);
		}
		if (CheckDataUtil.isNotNull(userId))
		{
			// 获取和检测是否有替换者信息
			List<String> replaceUsers = getReplaceUsers(userId, processId);
			if (replaceUsers != null) {
				replaceUsers.add(userId);
			}
			// 判断查询可候选任务
			if (showClaim) {
				if (replaceUsers != null) {
					if (isQueryJobs) {
						for (String u : replaceUsers) {
							taskQuery = taskQuery.taskCandidateOrAssigned(u);
						}
					} else {
						taskQuery = taskQuery.or();
						for (String u : replaceUsers) {
							taskQuery = taskQuery.taskCandidateOrAssigned(u);
						}
						taskQuery = taskQuery.endOr();
					}
				} else {
					taskQuery = taskQuery.taskCandidateOrAssigned(userId);
				}

			} else {
				if (replaceUsers != null) {
					taskQuery = taskQuery.taskAssigneeIds(replaceUsers);
				} else {
					taskQuery = taskQuery.taskAssignee(userId); // 制定个人任务查询，指定办理人
				}
			}
		}
		// 如果查询过候选组，则结束or判断
		if (isQueryJobs) {
			taskQuery = taskQuery.endOr();
		}

		if (CheckDataUtil.isNotNull(processId)) {
			taskQuery = taskQuery.processDefinitionId(processId); // 指定流程定义ID
		}
		if (CheckDataUtil.isNotNull(processKey)) {
			taskQuery = taskQuery.processDefinitionKey(processKey); // 指定流程定义key
		}
		if (CheckDataUtil.isNotNull(processBusinessType)) {
			taskQuery = taskQuery.processDefinitionKeyLike("%\\" + ActivitiConstant.PROCESS_KEY_SPAN + processBusinessType
					+ "\\" + ActivitiConstant.PROCESS_KEY_SPAN + "%"); // 指定查询业务类型
		}
		if (CheckDataUtil.isNotNull(taskName)) {
			taskQuery = taskQuery.taskNameLike(taskName + "%"); // 模糊查询任务名字
		}
		if (CheckDataUtil.isNotNull(taskDefId)) {
			taskQuery = taskQuery.taskDefinitionKey(taskDefId); // 精确查询任务定义ID
		}
		if (startTime != null) {
			taskQuery = taskQuery.taskCreatedAfter(startTime);
		}
		if (endTime != null) {
			taskQuery = taskQuery.taskCreatedBefore(endTime);
		}
		taskQuery = taskQuery.orderByTaskCreateTime().asc();
		List<Task> taskList = null;
		if (CheckDataUtil.isNotNull(processBusinessKeyList)) {
			taskList = new ArrayList<Task>();
			// 指定多个业务key，查询多次，查询所有列表
			for(String bKey : processBusinessKeyList)
			{
				taskList.addAll(taskQuery.processInstanceBusinessKey(bKey).list());
			}
		}
		else
		{
			// 默认业务key，组合在查询语句中，做单次查询
			taskQuery = taskQuery.processInstanceBusinessKey(ActivitiConstant.DEF_PROCESS_INST_BUSINESS_KEY); // 默认业务key
		}
		PageList<TaskSampleData> pageList = new PageList<TaskSampleData>();
		pageList.setPageNum(pageNum);
		pageList.setPageSize(pageSize);
		pageList.setTotal(taskList == null ? (int) taskQuery.count() : taskList.size());
		if (pageList.getTotal() == 0) {
			return pageList;
		}
		if(taskList == null)
		{
			// 排序
			switch(orderByCreate)
			{
				case Asc:
					taskQuery = taskQuery.orderByTaskCreateTime().asc();
					break;
				case Desc:
					taskQuery = taskQuery.orderByTaskCreateTime().desc();
					break;
				case None:
					break;
				default:
					break;

			}
			taskList = taskQuery.listPage(firstResult, maxResults);
		}
		else
		{
			// 先排序
			switch(orderByCreate)
			{
				case Asc:
					taskList.sort((t1, t2) -> Long.compare(t1.getCreateTime().getTime() , t2.getCreateTime().getTime()));
					break;
				case Desc:
					taskList.sort((t1, t2) -> Long.compare(t2.getCreateTime().getTime() , t1.getCreateTime().getTime()));
					break;
				case None:
					break;
				default:
					break;

			}
			// 内存分页任务
			int start = (pageNum - 1) * pageSize;
			int end = Math.min(taskList.size(), start + pageSize);
			if (start >= taskList.size())
			{
				return pageList;
			}
			taskList = taskList.subList(start, end);
		}

		List<TaskSampleData> rows = new ArrayList<TaskSampleData>();
		// 任务额外信息缓存，根据任务定义id分类
		Map<String, TaskSampleData> paramsCache = new HashMap<String, TaskSampleData>();
		for (Task task : taskList) {
			int stateType = 1;
			if (task.getAssignee() == null) {
				stateType = 2;
			}
			TaskSampleData taskSampleData = makeTaskSampleData(task, stateType, hasBusinessKey, paramsCache);

			rows.add(taskSampleData);
		}
		pageList.setRows(rows);
		return pageList;
	}

	@Override
	public List<String> getProcessInstIdsByUser(String userId, String region, List<String> unitIds,
												List<String> adminUnitIds, List<String> jobIdList, String processId, String processKey,
												String processBusinessType, List<String> processBusinessKeyList, String taskName, String taskDefId,
												boolean showClaim, Date startTime, Date endTime) throws WorkFlowException {

		CheckDataUtil.checkNull(userId, "userId");

		TaskQuery taskQuery = taskService // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				.active(); // 指定只显示激活中的任务

		boolean isQueryJobs = false;
		List<String> claimGroupList = new ArrayList<String>();
		// 判断是否要查询候选角色
		if (CheckDataUtil.isNotNull(jobIdList)) {
			isQueryJobs = true;
			taskQuery = taskQuery.or();
			claimGroupList.addAll(jobIdList);
			// 判断是否要查询部门
			if (CheckDataUtil.isNotNull(unitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : unitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询部门管理员
			if (CheckDataUtil.isNotNull(adminUnitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : adminUnitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
						// 也判断非管理员部门
						claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询区域
			if (CheckDataUtil.isNotNull(region)) {
				for (String jobId : jobIdList) {
					String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ region;
					claimGroupList.add(claimGroup);
				}
			}

		}
		// 判断无角色部门保持
		if (CheckDataUtil.isNotNull(unitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : unitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色管理员部门保持
		if (CheckDataUtil.isNotNull(adminUnitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : adminUnitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
				// 也判断非管理员部门
				claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色区域保持
		if (CheckDataUtil.isNotNull(region)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION
					+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + region;
			claimGroupList.add(claimGroup);
		}
		if (!claimGroupList.isEmpty()) {
			taskQuery = taskQuery.taskCandidateGroupIn(claimGroupList);
		}
		// 获取和检测是否有替换者信息
		List<String> replaceUsers = getReplaceUsers(userId, processId);
		if (replaceUsers != null) {
			replaceUsers.add(userId);
		}
		// 判断查询可候选任务
		if (showClaim) {
			if (replaceUsers != null) {
				if (isQueryJobs) {
					for (String u : replaceUsers) {
						taskQuery = taskQuery.taskCandidateOrAssigned(u);
					}
				} else {
					taskQuery = taskQuery.or();
					for (String u : replaceUsers) {
						taskQuery = taskQuery.taskCandidateOrAssigned(u);
					}
					taskQuery = taskQuery.endOr();
				}
			} else {
				taskQuery = taskQuery.taskCandidateOrAssigned(userId);
			}

		} else {
			if (replaceUsers != null) {
				taskQuery = taskQuery.taskAssigneeIds(replaceUsers);
			} else {
				taskQuery = taskQuery.taskAssignee(userId); // 制定个人任务查询，指定办理人
			}
		}
		// 如果查询过候选组，则结束or判断
		if (isQueryJobs) {
			taskQuery = taskQuery.endOr();
		}

		if (CheckDataUtil.isNotNull(processId)) {
			taskQuery = taskQuery.processDefinitionId(processId); // 指定流程定义ID
		}
		if (CheckDataUtil.isNotNull(processKey)) {
			taskQuery = taskQuery.processDefinitionKey(processKey); // 指定流程定义key
		}
		if (CheckDataUtil.isNotNull(processBusinessType)) {
			taskQuery = taskQuery.processDefinitionKeyLike("%\\" + ActivitiConstant.PROCESS_KEY_SPAN + processBusinessType
					+ "\\" + ActivitiConstant.PROCESS_KEY_SPAN + "%"); // 指定查询业务类型
		}
		if (CheckDataUtil.isNotNull(taskName)) {
			taskQuery = taskQuery.taskNameLike(taskName + "%"); // 模糊查询任务名字
		}
		if (CheckDataUtil.isNotNull(taskDefId)) {
			taskQuery = taskQuery.taskDefinitionKey(taskDefId); // 精确查询任务定义ID
		}
		if (startTime != null) {
			taskQuery = taskQuery.taskCreatedAfter(startTime);
		}
		if (endTime != null) {
			taskQuery = taskQuery.taskCreatedBefore(endTime);
		}
		taskQuery = taskQuery.orderByTaskCreateTime().asc();
		List<Task> taskList = null;
		if (CheckDataUtil.isNotNull(processBusinessKeyList)) {
			taskList = new ArrayList<Task>();
			// 指定多个业务key，查询多次，查询所有列表
			for(String bKey : processBusinessKeyList)
			{
				taskList.addAll(taskQuery.processInstanceBusinessKey(bKey).list());
			}
		}
		else
		{
			// 默认业务key，组合在查询语句中，做单次查询
			taskQuery = taskQuery.processInstanceBusinessKey(ActivitiConstant.DEF_PROCESS_INST_BUSINESS_KEY); // 默认业务key
		}
		List<String> list = new ArrayList<String>();
		int count = taskList == null ? (int) taskQuery.count() : taskList.size();
		if (count == 0) {
			return list;
		}
		if(taskList == null)
		{
			taskList = taskQuery.list();
		}
		for (Task task : taskList) {
			list.add(task.getProcessInstanceId());
		}
		return list;
	}

	@Override
	public PageList<TaskSampleData> getFinishedTasksByUser(String userId, String processId, String taskName,
														   Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
		return getFinishedTasksByUser(userId, processId, null, taskName, startTime, endTime, pageNum, pageSize);
	}

	@Override
	public PageList<TaskSampleData> getFinishedTasksByUser(String userId, String processId, String processKey,
														   String taskName, Date startTime, Date endTime, int pageNum, int pageSize) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		int firstResult = (pageNum - 1) * pageSize;
		int maxResults = pageSize;

		HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery().finished();

		// 获取和检测是否有替换者信息
		List<String> replaceUsers = getReplaceUsers(userId, processId);
		if (replaceUsers != null) {
			replaceUsers.add(userId);
			taskQuery = taskQuery.taskAssigneeIds(replaceUsers);
		} else {
			taskQuery = taskQuery.taskAssignee(userId); // 制定个人任务查询，指定办理人
		}

		if (CheckDataUtil.isNotNull(processId)) {
			taskQuery = taskQuery.processDefinitionId(processId); // 指定流程ID
		}
		if (CheckDataUtil.isNotNull(processKey)) {
			taskQuery = taskQuery.processDefinitionKey(processKey); // 指定流程Key
		}
		if (CheckDataUtil.isNotNull(taskName)) {
			taskQuery = taskQuery.taskNameLike(taskName + "%"); // 模糊查询流程名字
		}
		if (startTime != null) {
			taskQuery = taskQuery.taskCreatedAfter(startTime);
		}
		if (endTime != null) {
			taskQuery = taskQuery.taskCreatedBefore(endTime);
		}
		taskQuery = taskQuery.orderByTaskCreateTime().desc();
		PageList<TaskSampleData> pageList = new PageList<TaskSampleData>();
		pageList.setPageNum(pageNum);
		pageList.setPageSize(pageSize);
		pageList.setTotal((int) taskQuery.count());
		if (pageList.getTotal() == 0) {
			return pageList;
		}

		List<HistoricTaskInstance> taskList = taskQuery.listPage(firstResult, maxResults);
		List<TaskSampleData> rows = new ArrayList<TaskSampleData>();
		for (HistoricTaskInstance task : taskList) {
			TaskSampleData taskSampleData = makeTaskSampleData(task);

			rows.add(taskSampleData);
		}
		pageList.setRows(rows);

		return pageList;
	}

	@Override
	public Map<String, List<TaskSampleData>> getFinishedTasks(String userId, String processId, String processKey,
															  String taskName, Date startTime, Date endTime) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");

		HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery().finished();

		// 获取和检测是否有替换者信息
		List<String> replaceUsers = getReplaceUsers(userId, processId);
		if (replaceUsers != null) {
			replaceUsers.add(userId);
			taskQuery = taskQuery.taskAssigneeIds(replaceUsers);
		} else {
			taskQuery = taskQuery.taskAssignee(userId); // 制定个人任务查询，指定办理人
		}

		if (CheckDataUtil.isNotNull(processId)) {
			taskQuery = taskQuery.processDefinitionId(processId); // 指定流程ID
		}
		if (CheckDataUtil.isNotNull(processKey)) {
			taskQuery = taskQuery.processDefinitionKey(processKey); // 指定流程Key
		}
		if (CheckDataUtil.isNotNull(taskName)) {
			taskQuery = taskQuery.taskNameLike(taskName + "%"); // 模糊查询流程名字
		}
		if (startTime != null) {
			taskQuery = taskQuery.taskCreatedAfter(startTime);
		}
		if (endTime != null) {
			taskQuery = taskQuery.taskCreatedBefore(endTime);
		}
		taskQuery = taskQuery.orderByTaskCreateTime().desc();


		List<HistoricTaskInstance> taskList = taskQuery.list();
		Map<String, List<TaskSampleData>> tasks = new HashMap<String, List<TaskSampleData>>();
		for (HistoricTaskInstance task : taskList) {
			TaskSampleData taskSampleData = new TaskSampleData();
			taskSampleData.setId(task.getId());
			taskSampleData.setTaskDefId(task.getTaskDefinitionKey());
			taskSampleData.setName(task.getName());
			taskSampleData.setCreateTime(task.getCreateTime());
			taskSampleData.setEndTime(task.getEndTime());
			taskSampleData.setProcessDefinitionId(task.getProcessDefinitionId());
			taskSampleData.setProcessInstanceId(task.getProcessInstanceId());
			taskSampleData.setExecutionId(task.getExecutionId());
			taskSampleData.setAssignee(task.getAssignee());
			if(!tasks.containsKey(taskSampleData.getProcessInstanceId()))
			{
				tasks.put(taskSampleData.getProcessInstanceId(), new ArrayList<TaskSampleData>());
			}
			tasks.get(taskSampleData.getProcessInstanceId()).add(taskSampleData);
		}
		return tasks;
	}

	@Override
	public PageList<TaskSampleData> getClaimTasksByUser(String userId, String processId, int pageNum, int pageSize)
			throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}

		int firstResult = (pageNum - 1) * pageSize;
		int maxResults = pageSize;

		TaskQuery taskQuery = taskService // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				.taskCandidateUser(userId) // 查询候选人的任务
				.active(); // 指定只显示激活中的任务

		if (CheckDataUtil.isNotNull(processId)) {
			taskQuery = taskQuery.processDefinitionId(processId);
		}
		taskQuery = taskQuery.orderByTaskCreateTime().asc();
		PageList<TaskSampleData> pageList = new PageList<TaskSampleData>();
		pageList.setPageNum(pageNum);
		pageList.setPageSize(pageSize);
		pageList.setTotal((int) taskQuery.count());
		if (pageList.getTotal() == 0) {
			return pageList;
		}

		List<Task> taskList = taskQuery.listPage(firstResult, maxResults);
		List<TaskSampleData> rows = new ArrayList<TaskSampleData>();
		// 任务额外信息缓存，根据任务定义id分类
		Map<String, TaskSampleData> paramsCache = new HashMap<String, TaskSampleData>();
		for (Task task : taskList) {
			TaskSampleData taskSampleData = makeTaskSampleData(task, 2, false, paramsCache);

			rows.add(taskSampleData);
		}

		pageList.setRows(rows);

		return pageList;
	}

	@Override
	public PageList<TaskSampleData> getClaimTasksByUnit(String unitId, String processId, int pageNum, int pageSize)
			throws WorkFlowException {

		CheckDataUtil.checkNull(unitId, "unitId");
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}

		int firstResult = (pageNum - 1) * pageSize;
		int maxResults = pageSize;

		TaskQuery taskQuery = taskService // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				.taskCandidateGroup(unitId) // 查询候选组的任务
				.active(); // 指定只显示激活中的任务

		if (CheckDataUtil.isNotNull(processId)) {
			taskQuery = taskQuery.processDefinitionId(processId);
		}
		taskQuery = taskQuery.orderByTaskCreateTime().asc();
		PageList<TaskSampleData> pageList = new PageList<TaskSampleData>();
		pageList.setPageNum(pageNum);
		pageList.setPageSize(pageSize);
		pageList.setTotal((int) taskQuery.count());
		if (pageList.getTotal() == 0) {
			return pageList;
		}

		List<Task> taskList = taskQuery.listPage(firstResult, maxResults);
		List<TaskSampleData> rows = new ArrayList<TaskSampleData>();
		// 任务额外信息缓存，根据任务定义id分类
		Map<String, TaskSampleData> paramsCache = new HashMap<String, TaskSampleData>();
		for (Task task : taskList) {
			TaskSampleData taskSampleData = makeTaskSampleData(task, 2, false, paramsCache);
			rows.add(taskSampleData);
		}
		pageList.setRows(rows);

		return pageList;
	}

	@Override
	public TaskData getTaskInfo(String taskId) throws WorkFlowException {
		CheckDataUtil.checkNull(taskId, "taskId");
		Task task = findTask(taskId);
		int stateType = 1;
		if (task.getAssignee() == null) {
			stateType = 2;
		}
		return makeTaskData(task, stateType);
	}

	@Override
	public TaskData getUserTaskInfoByInstance(String userId, String processInstanceId) throws WorkFlowException {
		return getUserTaskInfoByInstance(userId, null, null, null, processInstanceId);
	}

	@Override
	public TaskData getUserTaskInfoByInstance(String userId, String region, String unitId, List<String> jobIdList,
											  String processInstanceId) throws WorkFlowException {
		List<String> unitIds = null;
		if (CheckDataUtil.isNotNull(unitId)) {
			unitIds = new ArrayList<String>();
			unitIds.add(unitId);
		}
		return getUserTaskInfoByInstance(userId, region, unitIds, null, jobIdList, processInstanceId);
	}

	@Override
	public TaskData getUserTaskInfoByInstance(String userId, String region, List<String> unitIds,
											  List<String> adminUnitIds, List<String> jobIdList, String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);

		boolean isQueryJobs = false;
		List<String> claimGroupList = new ArrayList<String>();
		// 判断是否要查询候选角色
		if (CheckDataUtil.isNotNull(jobIdList)) {
			isQueryJobs = true;
			taskQuery = taskQuery.or();
			claimGroupList.addAll(jobIdList);
			// 判断是否要查询部门
			if (CheckDataUtil.isNotNull(unitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : unitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询部门管理员
			if (CheckDataUtil.isNotNull(adminUnitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : adminUnitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
						// 也判断非管理员部门
						claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询区域
			if (CheckDataUtil.isNotNull(region)) {
				for (String jobId : jobIdList) {
					String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ region;
					claimGroupList.add(claimGroup);
				}
			}

		}
		// 判断无角色部门保持
		if (CheckDataUtil.isNotNull(unitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : unitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色管理员部门保持
		if (CheckDataUtil.isNotNull(adminUnitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : adminUnitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
				// 也判断非管理员部门
				claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色区域保持
		if (CheckDataUtil.isNotNull(region)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION
					+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + region;
			claimGroupList.add(claimGroup);
		}
		if (!claimGroupList.isEmpty()) {
			taskQuery = taskQuery.taskCandidateGroupIn(claimGroupList);
		}

		taskQuery = taskQuery.taskCandidateOrAssigned(userId);

//		//获取和检测是否有替换者信息
//		List<String> replaceUsers = getReplaceUsers(userId, processId);
//		if(replaceUsers != null)
//		{
//			replaceUsers.add(userId);
//		}
//		//判断查询可候选任务
//		if(replaceUsers != null)
//		{
//			taskQuery = taskQuery.or();
//			for(String u : replaceUsers)
//			{
//				taskQuery = taskQuery.taskCandidateOrAssigned(u);
//			}
//			taskQuery = taskQuery.endOr();
//		}
//		else
//		{
//			taskQuery = taskQuery.taskCandidateOrAssigned(userId);
//		}

		// 如果查询过候选组，则结束or判断
		if (isQueryJobs) {
			taskQuery = taskQuery.endOr();
		}

		Task task = taskQuery.singleResult();

		return makeTaskData(task, 1);
	}

	@Override
	public TaskData getFinishedTaskInfo(String taskId) throws WorkFlowException {
		CheckDataUtil.checkNull(taskId, "taskId");
		HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().finished().taskId(taskId)
				.singleResult();
		return makeTaskData(task);
	}

	@Override
	public List<TaskDefData> getProcessInfoByTask(String taskId) throws WorkFlowException {
		CheckDataUtil.checkNull(taskId, "taskId");
		Task task = findTask(taskId);
		if (task == null) {
			return null;
		}
		return getProcessInfoByDefId(task.getProcessDefinitionId());
	}

	@Override
	public List<TaskDefData> getProcessInfoByDefId(String processDefinitionId) throws WorkFlowException {
		CheckDataUtil.checkNull(processDefinitionId, "processDefinitionId");
		List<TaskDefData> list = new ArrayList<TaskDefData>();
		BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
		if (model != null) {

			Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
			for (FlowElement e : flowElements) {
				// System.out.println("flowelement id:" + e.getId() + " name:" +
				// e.getName() + " class:" + e.getClass().toString());
				// logger.debug(e.getName() + " ------ " + e.getDocumentation());
				String taskid = e.getId();
				if (taskid != null) {
					String[] names = taskid.split(ActivitiConstant.PROCESS_TASK_ID_SPAN);
					String type = names[0];
					if(ActivitiConstant.USERTASK_HEAD.equals(type))
					{
						TaskDefData taskDefData = new TaskDefData();
						taskDefData.setId(e.getId());
						taskDefData.setName(e.getName());
						int sort = 0;
						if(names.length > 1)
						{
							try {
								sort = Integer.parseInt(names[1]);
							} catch (NumberFormatException ex) {

							}
						}
						taskDefData.setSort(sort);
						list.add(taskDefData);
					}
				}
			}
		}
		if(CheckDataUtil.isNotNull(list))
		{
			//排序任务
			list.sort(ActivitiServiceImpl::TaskDefSort);
		}
		return list;
	}

	/**
	 * @Title: TaskDefSort
	 * @Description: 任务排序
	 * @param a
	 * @param b
	 * @return  参数说明
	 * @return int    返回类型
	 *
	 */
	public static int TaskDefSort(TaskDefData a, TaskDefData b)
	{
		if(a.getSort() < b.getSort())
			return -1;
		return 1;
	}

	@Override
	@Transactional
	public boolean updateTaskValues(String userId, String taskId, Map<String, String> values,
									Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers) throws WorkFlowException {
		return updateTaskValues(userId, taskId, values, signUsers, claimUsers, null);
	}

	@Override
	public boolean updateTaskValues(String userId, String taskId, Map<String, String> values,
									Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues)
			throws WorkFlowException {
		CheckDataUtil.checkNull(taskId, "taskId");
		if (signUsers != null) {
			taskService.setVariables(taskId, signUsers);
		}
		if (claimUsers != null) {
			taskService.setVariables(taskId, claimUsers);
		}
		if (exValues != null) {
			saveTaskExValues(taskId, exValues);
		}
		if (values != null) {
			formService.saveFormData(taskId, values);
			// 写入一份到本地自定义数据
			// taskService.setVariablesLocal(taskId, values);
		}
		return true;
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String taskId, Map<String, String> values,
								Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers) throws WorkFlowException {
		return complateTask(userId, null, null, taskId, values, signUsers, claimUsers, null);
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String taskId, Map<String, String> values,
								Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues)
			throws WorkFlowException {
		return complateTask(userId, null, null, taskId, values, signUsers, claimUsers, exValues);
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String region, String unitId, String taskId, Map<String, String> values,
								Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers, Map<String, String> exValues)
			throws WorkFlowException {
		return complateTask(userId, region, unitId, taskId, values, signUsers, claimUsers, null, exValues);
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String region, String unitId, String taskId, Map<String, String> values,
								Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
								Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		List<String> unitIds = null;
		if (CheckDataUtil.isNotNull(unitId)) {
			unitIds = new ArrayList<String>();
			unitIds.add(unitId);
		}
		return complateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String region, List<String> unitIds, String taskId,
								Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
								Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(taskId, "taskId");
		// 查询任务
		Task task = findTask(taskId);
		if (task == null) {
			throw new WorkFlowException("60501", "任务不存在");
		}

		if (signUsers != null) {
			taskService.setVariables(taskId, signUsers);
		}
		if (claimUsers != null) {
			taskService.setVariables(taskId, claimUsers);
		}
		if (exValues != null) {
			saveTaskExValues(taskId, exValues);
		}
		if (values != null) {
			formService.saveFormData(taskId, values);
			// 写入一份到本地自定义数据
			// taskService.setVariablesLocal(taskId, values);
		}

		// 查询任务是否是设置部门或者保持任务，如果是则进行设置
		TaskFormData fdata = formService.getTaskFormData(taskId);
		if (fdata != null) {
			Map<String, List<String>> cgs = new HashMap<String, List<String>>();
			List<FormProperty> fvalues = fdata.getFormProperties();
			for (FormProperty fvalue : fvalues) {
				String id = fvalue.getId();
				String[] propertyTypes = id.split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
				String propertyType = propertyTypes[0];
				switch (propertyType) {
					case ActivitiConstant.CLAIM_GROUPS_FORM_ID_HEAD: {
						if (propertyTypes.length < 2) {
							logger.warn("部门区域保持类型参数格式错误：" + id);
							continue;
						}
						// 保持类型
						String cgType = propertyTypes[1];
						// 目标id列表
						List<String> targetIds = new ArrayList<String>();
						switch (cgType) {
							case ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT: // 部门保持
							case ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT: // 管理员部门保持
							{
								// 检测是否有参数，如果无参数则报错
								if (!CheckDataUtil.isNotNull(unitIds)) {
									throw new WorkFlowException("60504", "该任务需要部门参数");
								}
								targetIds = unitIds;
							}
							break;
							case ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION: // 区域保持
							{
								// 检测是否有参数，如果无参数则报错
								if (!CheckDataUtil.isNotNull(region)) {
									throw new WorkFlowException("60504", "该任务需要区域参数");
								}
								targetIds.add(region);
							}
							break;
						}
						if (targetIds.isEmpty()) {
							logger.warn("未知部门区域保持类型参数：" + id);
							continue;
						}
						// 赋值对应数据
						String[] groupKeys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
						String groupsKey = groupKeys[0]; // 候选角色自定义变量key
						String groupsDefKey = null;
						if (groupKeys.length == 2) {
							groupsDefKey = groupKeys[1]; // 候选角色定义列表全局变量key
						}
						// 查询候选角色定义数据
						if (groupsDefKey != null) {
							Object defgroups = taskService.getVariable(task.getId(), groupsDefKey);
							if (defgroups != null) {
								// 赋值候选角色定义列表
								List<String> groupList = new ArrayList<String>();
								String[] groupidlist = defgroups.toString().split(",");
								for (String groupid : groupidlist) {
									// 候选角色ID=角色id+标示+部门id或区域 用_分割
									for (String targetId : targetIds) {
										String newgroupid = groupid + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + cgType
												+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + targetId;
										groupList.add(newgroupid);
									}
								}
								cgs.put(groupsKey, groupList);
							}
						} else {
							List<String> groupList = new ArrayList<String>();
							// 无角色保持任务 标示+部门id
							for (String targetId : targetIds) {
								String newgroupid = cgType + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + targetId;
								groupList.add(newgroupid);
							}
							cgs.put(groupsKey, groupList);
						}
					}
					break;
					case ActivitiConstant.CLAIM_GROUPS_VALUE_FORM_ID_HEAD: {
						if (propertyTypes.length < 2) {
							logger.warn("部门区域保持类型参数格式错误：" + id);
							continue;
						}
						// 保持类型
						String cgType = propertyTypes[1];
						String keysstr = fvalue.getValue();
						String[] keys = keysstr.split(ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN);
						String paramkey = keys[0]; // 部门数据key
						String defkey = null; // 角色定义数据存放key
						if (keys.length > 1) {
							defkey = keys[1];
						}
						// 先判断参数是否传递
						if (!claimGroups.containsKey(paramkey)) {
							throw new WorkFlowException("60504", "该任务需要候选组参数" + paramkey);
						}
						// 循环值
						List<String> cglist = claimGroups.get(paramkey);
						if (cglist == null || cglist.isEmpty()) {
							throw new WorkFlowException("60504", "候选组不能为空" + paramkey);
						}
						List<String> groupList = new ArrayList<String>();
						Object defgroups = null;
						if (defkey != null) {
							defgroups = taskService.getVariable(task.getId(), defkey);
						}
						// 无角色保持任务 标示+部门id
						if (defgroups == null) {
							for (String cg : cglist) {
								String newgroupid = cgType + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + cg;
								groupList.add(newgroupid);
							}
						} else {
							String[] groupidlist = defgroups.toString().split(",");
							for (String groupid : groupidlist) {
								for (String cg : cglist) {
									// 候选角色ID=角色id+标示+值 用_分割
									String newgroupid = groupid + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + cgType
											+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + cg;
									groupList.add(newgroupid);
								}
							}
						}
						if (!groupList.isEmpty()) {
							cgs.put(paramkey, groupList);
						}
					}
					break;
					case ActivitiConstant.CLAIM_GROUPS_DATA_FORM_ID_HEAD:
					{
						String keysstr = fvalue.getValue();
						String[] keys = keysstr.split(ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN);
						String paramkey = keys[0]; // 部门数据key
						// 先判断参数是否传递
						if (!claimGroups.containsKey(paramkey)) {
							throw new WorkFlowException("60504", "该任务需要候选组参数" + paramkey);
						}
						// 循环值
						List<String> groupList = claimGroups.get(paramkey);

						if (CheckDataUtil.isNotNull(groupList)) {
							cgs.put(paramkey, groupList);
						}
					}
					case ActivitiConstant.LOOP_COUNT_HEAD:		// 循环计数变量
					{
						String loopcountkey = fvalue.getValue();
						// 先读取变量
						Object olv = taskService.getVariable(taskId, loopcountkey);
						int loopcount = olv == null ? 0 : Integer.parseInt(String.valueOf(olv));
						++loopcount;
						// 增加计数后保存
						taskService.setVariable(taskId, loopcountkey, loopcount);
					}
					break;
					default: {

					}
					break;
				}

			}
			if (!cgs.isEmpty()) {
				taskService.setVariables(taskId, cgs);
			}
		}
		;

		saveTaskLocalValues(taskId, fdata);
		//判断数据库中是否已经有taskid
		if(userTaskFinishedService.getStcsmUserTaskFinishedByTaskId(taskId)!=null){
			//如果已经存在，先删除
			logger.info("-----add usertaskfinished Data duplication : "+taskId);
			userTaskFinishedService.remove(taskId);
		}
		// 写日志
		UserInfo userInfo = loadUserInfo(userId, true);
		UserTaskFinishedEntity taskFinished = new UserTaskFinishedEntity();
		taskFinished.setProcessId(task.getProcessDefinitionId());
		taskFinished.setProcessKey(getProcessKeyById(task.getProcessDefinitionId()));
		taskFinished.setProcessName(getProcessDefName(task.getProcessDefinitionId()));
		taskFinished.setProcessInstanceId(task.getProcessInstanceId());
		taskFinished.setTaskId(taskId);
		taskFinished.setTaskName(task.getName());
		taskFinished.setUnitId(userInfo.getUnitId());
		taskFinished.setUnitName(userInfo.getUnitName());
		taskFinished.setTaskDefId(task.getTaskDefinitionKey());
		taskFinished.setUserId(userInfo.getId());
		taskFinished.setUserName(userInfo.getNick());
		taskFinished.setFinishTime(new Date());
		userTaskFinishedService.saveStcsmUserTaskFinished(taskFinished);
		// 写缓存
		// 写用户完成记录缓存
		jedisMgrWf.pushIncrSortSet(getUserFinishedKey(), userId, 1d);
		// 写部门完成记录缓存
		if (taskFinished.getUnitId() != null) {
			jedisMgrWf.pushIncrSortSet(getUnitFinishedKey(), taskFinished.getUnitId(), 1d);
		}

		// 赋值操作者
		if (!setTaskAssignee(userId, taskId)) {
			return false;
		}
		//用于判断是否需要回退 ，完成任务成功-不回退，失败-回退
		boolean isreturnUsertaskfinished=false;
		try {
			taskService.complete(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			isreturnUsertaskfinished=true;
			throw new WorkFlowException("60501", "任务不存在");
		} catch (ActivitiException e) {
			isreturnUsertaskfinished=true;
			throw new WorkFlowException("60501", "任务已锁定或者缺少必要参数");
		}
		//失败时回退
		if(isreturnUsertaskfinished){
			logger.info("-----return usertaskfinished complete failure : "+taskId);
			userTaskFinishedService.remove(taskId);
		}
		return true;
	}

	@Override
	@Transactional
	public boolean complateTasks(String userId, String region, List<String> unitIds, List<String> taskIdList,
								 Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
								 Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		for (String taskId : taskIdList) {
			if (!complateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @Title: saveTaskLocalValues
	 * @Description: 保存任务本地参数--在任务完成或中止时调用
	 * @param taskId 参数说明
	 * @return void 返回类型
	 *
	 */
	public void saveTaskLocalValues(String taskId) {
		saveTaskLocalValues(taskId, null);
	}

	/**
	 * @Title: saveTaskLocalValues
	 * @Description: 保存任务本地参数--在任务完成或中止时调用
	 * @param taskId
	 * @param fdata  参数说明
	 * @return void 返回类型
	 *
	 */
	@Transactional
	public void saveTaskLocalValues(String taskId, TaskFormData fdata) {
		if (fdata == null) {
			fdata = formService.getTaskFormData(taskId);
			if (fdata == null) {
				return;
			}
		}

		// 写入特殊数据进本地自定义变量--目前写入业务URL和表单信息
		Map<String, Object> variables = new HashMap<String, Object>();
		List<FormProperty> fvalues = fdata.getFormProperties();
		for (FormProperty fvalue : fvalues) {
			String id = fvalue.getId();
			String[] propertyTypes = id.split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
			String propertyType = propertyTypes[0];

			switch (propertyType) {
				case ActivitiConstant.BUSINESS_KEY: // 业务URL
				{
					variables.put(ActivitiConstant.BUSINESS_KEY, fvalue.getValue());
				}
				break;
				case ActivitiConstant.EXPIRED_KEY: // 业务过期时间
				{
					variables.put(ActivitiConstant.EXPIRED_KEY, fvalue.getValue());
				}
				break;
				case ActivitiConstant.RETRIEVE_KEY: // 取回任务信息
				{
					variables.put(ActivitiConstant.RETRIEVE_KEY, fvalue.getValue());
				}
				break;
				case ActivitiConstant.SMARTFORM_FORM_ID_HEAD: // 表单
				{
					variables.put(propertyType + ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + propertyTypes[1]
							+ ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + fvalue.getName(), fvalue.getValue());
				}
				break;
				case ActivitiConstant.SIGN_USERS_FORM_ID_HEAD: // 会签用户
				{
					String[] userKeys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String usersKey = userKeys[0]; // 会签用户自定义变量key
					Object users = taskService.getVariable(taskId, usersKey);
					variables.put(propertyType + ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + usersKey
							+ ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + fvalue.getName(), users);
				}
				break;
				case ActivitiConstant.CLAIM_USERS_FORM_ID_HEAD: // 候选用户
				{
					String[] userKeys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String usersKey = userKeys[0]; // 候选用户自定义变量key
					Object users = taskService.getVariable(taskId, usersKey);
					variables.put(propertyType + ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + usersKey
							+ ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + fvalue.getName(), users);
				}
				break;
				case ActivitiConstant.CLAIM_GROUPS_FORM_ID_HEAD: // 候选角色--部门区域保持
				case ActivitiConstant.CLAIM_GROUPS_VALUE_FORM_ID_HEAD: // 自定义候选角色
				case ActivitiConstant.CLAIM_GROUPS_DATA_FORM_ID_HEAD: // 候选角色--指定角色
				{
					String[] groupKeys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String groupsKey = groupKeys[0]; // 候选角色自定义变量key
					Object users = taskService.getVariable(taskId, groupsKey);
					variables.put(propertyType + ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + groupsKey
							+ ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + fvalue.getName(), users);
				}
				break;
				default: // 自定义变量
				{
					variables.put(propertyType + ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + id
							+ ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + fvalue.getName(), fvalue.getValue());
				}
				break;
			}
		}
		taskService.setVariablesLocal(taskId, variables);
		return;
	}

	@Override
	@Transactional
	public boolean claimTask(String userId, String taskId) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(taskId, "taskId");
		try {
			taskService.claim(taskId, userId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new WorkFlowException("60501", "任务不存在");
		} catch (ActivitiTaskAlreadyClaimedException e) {
			throw new WorkFlowException("60501", "任务已被候选");
		}

		return true;
	}

	@Override
	@Transactional
	public boolean retrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(taskId, "taskId");
		HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new WorkFlowException("60501", "任务不存在");
		}

		if (!task.getAssignee().equals(userId)) {
			// 获取和检测是否有替换者信息
			List<String> replaceUsers = getReplaceUsers(userId, task.getProcessDefinitionId());
			if (replaceUsers == null || replaceUsers.indexOf(task.getAssignee()) < 0) {
				throw new WorkFlowException("60501", "只有任务完成者才能取回任务");
			}
		}

		HistoricVariableInstance value = historyService.createHistoricVariableInstanceQuery().taskId(task.getId())
				.variableName(ActivitiConstant.RETRIEVE_KEY).singleResult();
		if (value == null) {
			throw new WorkFlowException("60501", "该任务不可取回");
		}
		String[] values = value.getValue().toString().split(ActivitiConstant.RETRIEVE_PROPERTY_VALUE_SPAN);
//		// 查询当前任务定义id的任务是否已完成
//		String retrieveTaskDefIds = values[0];
//		if(!canRetrieveTask(task.getProcessInstanceId(), retrieveTaskDefIds))
//		{
//			throw new WorkFlowException("60501", "已完成的任务无法取回");
//		}
		if(values.length < 2)
		{
			throw new WorkFlowException("60501", "取回配置参数出错，请联系管理员 value:" + value.getValue().toString());
		}
		// 获取和发送信号
		String signalName = values[1];
		Execution execution = runtimeService.createExecutionQuery().signalEventSubscriptionName(signalName)
				.rootProcessInstanceId(task.getProcessInstanceId()).singleResult();
		if (execution == null) {
			throw new WorkFlowException("60501", "已完成的任务无法取回");
		}
		if(values.length == 2)
		{
			runtimeService.signalEventReceived(signalName, execution.getId());
		}
		else
		{
			String singleTaskDefId = values[2];
			Task singleTask = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(singleTaskDefId).singleResult();
			if(singleTask == null)
			{
				throw new WorkFlowException("60501", "取回配置参数出错，请联系管理员 value:" + value.getValue().toString());
			}
			taskService.complete(singleTask.getId());
		}
		// runtimeService.dispatchEvent(activitiEvent);
		return true;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put(retrieveParam, "1");
//		return complateTask(userId, retrieveTask.getId(), params, null, null, exValues);
	}


	@Override
	@Transactional
	public boolean jumpTask(String startTaskId, String targetTaskDefId) throws WorkFlowException {
		CheckDataUtil.checkNull(startTaskId, "startTaskId");
		CheckDataUtil.checkNull(targetTaskDefId, "targetTaskDefId");

		Task startTask = taskService.createTaskQuery().active().taskId(startTaskId).singleResult();
		if(startTask == null)
		{
			throw new WorkFlowException("任务不存在或已经结束，无法跳转");
		}
		jump(startTask, targetTaskDefId);
		return true;
	}

	@Override
	public boolean jumpTaskByProInst(String processInstId, String targetTaskDefId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstId, "startTaskId");
		CheckDataUtil.checkNull(targetTaskDefId, "targetTaskDefId");

		Task startTask = taskService.createTaskQuery().active().processInstanceId(processInstId).singleResult();
		if(startTask == null)
		{
			throw new WorkFlowException("任务不存在或已经结束，无法跳转");
		}
		jump(startTask, targetTaskDefId);
		return true;
	}

	@Override
	public boolean setTaskAssignee(String userId, String taskId) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(taskId, "taskId");
		try {
			taskService.setAssignee(taskId, userId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new WorkFlowException("60501", "任务不存在");
		}

		return true;
	}

	@Override
	public List<TaskSampleData> getTasksByProcessInstanceID(String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId);
		taskQuery = taskQuery.orderByTaskCreateTime().desc();
		List<HistoricTaskInstance> taskList = taskQuery.list();

		List<TaskSampleData> rows = new ArrayList<TaskSampleData>();
		for (HistoricTaskInstance task : taskList) {
			TaskSampleData taskSampleData = makeTaskSampleData(task);
			rows.add(taskSampleData);
		}
		return rows;
	}

	@Override
	public List<TaskSampleData> getActiveTasksByProcessInstanceID(String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);
		taskQuery = taskQuery.orderByTaskCreateTime().desc();
		List<Task> taskList = taskQuery.list();
		List<TaskSampleData> rows = new ArrayList<TaskSampleData>();
		// 任务额外信息缓存，根据任务定义id分类
		Map<String, TaskSampleData> paramsCache = new HashMap<String, TaskSampleData>();
		for (Task task : taskList) {
			int stateType = 1;
			if (task.getAssignee() == null) {
				stateType = 2;
			}
			TaskSampleData taskSampleData = makeTaskSampleData(task, stateType, false, paramsCache);
			rows.add(taskSampleData);
		}
		return rows;
	}

	@Override
	public List<TaskData> getTaskDatasByProcessInstanceID(String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId);
		taskQuery = taskQuery.orderByTaskCreateTime().desc();
		List<HistoricTaskInstance> taskList = taskQuery.list();

		List<TaskData> rows = new ArrayList<TaskData>();
		for (HistoricTaskInstance task : taskList) {
			TaskData taskData = makeTaskData(task);
			rows.add(taskData);
		}
		return rows;
	}

	@Override
	public Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, boolean showClaim)
			throws WorkFlowException {

		return getTasksInfoByUser(userId, null, null, null, showClaim);
	}

	@Override
	public Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, String unitId,
																  List<String> jobIdList, boolean showClaim) throws WorkFlowException {
		List<String> unitIds = null;
		if (CheckDataUtil.isNotNull(unitId)) {
			unitIds = new ArrayList<String>();
			unitIds.add(unitId);
		}
		return getTasksInfoByUser(userId, region, unitIds, null, jobIdList, showClaim);
	}

	@Override
	public Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds,
																  List<String> adminUnitIds, List<String> jobIdList, boolean showClaim) throws WorkFlowException {
		return getTasksInfoByUser(userId, region, unitIds, adminUnitIds, jobIdList, null, showClaim);
	}

	@Override
	public Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds,
																  List<String> adminUnitIds, List<String> jobIdList, List<String> businessTypeList, boolean showClaim)
			throws WorkFlowException {
		return getTasksInfoByUser(userId, region, unitIds, adminUnitIds, jobIdList, businessTypeList, null, showClaim);
	}

	@Override
	public Map<String, Map<String, TasksInfo>> getTasksInfoByUser(String userId, String region, List<String> unitIds,
																  List<String> adminUnitIds, List<String> jobIdList, List<String> businessTypeList, List<String> processBusinessKeyList,
																  boolean showClaim) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		TaskQuery taskQuery = taskService // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				.active(); // 指定只显示激活中的任务
		boolean isQueryJobs = false;
		List<String> claimGroupList = new ArrayList<String>();
		// 判断是否要查询候选角色
		if (CheckDataUtil.isNotNull(jobIdList)) {
			isQueryJobs = true;
			taskQuery = taskQuery.or();
			claimGroupList.addAll(jobIdList);
			// 判断是否要查询部门
			if (CheckDataUtil.isNotNull(unitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : unitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询部门管理员
			if (CheckDataUtil.isNotNull(adminUnitIds)) {
				for (String jobId : jobIdList) {
					for (String unitId : adminUnitIds) {
						String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
						// 也判断非管理员部门
						claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
								+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
								+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
						claimGroupList.add(claimGroup);
					}
				}
			}
			// 判断是否要查询区域
			if (CheckDataUtil.isNotNull(region)) {
				for (String jobId : jobIdList) {
					String claimGroup = jobId + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION + ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN
							+ region;
					claimGroupList.add(claimGroup);
				}
			}

		}
		// 判断无角色部门保持
		if (CheckDataUtil.isNotNull(unitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : unitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色管理员部门保持
		if (CheckDataUtil.isNotNull(adminUnitIds)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			for (String unitId : adminUnitIds) {
				String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
				// 也判断非管理员部门
				claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_UNIT
						+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + unitId;
				claimGroupList.add(claimGroup);
			}
		}
		// 判断无角色区域保持
		if (CheckDataUtil.isNotNull(region)) {
			if (!isQueryJobs) {
				isQueryJobs = true;
				taskQuery = taskQuery.or();
			}
			String claimGroup = ActivitiConstant.CLAIM_GROUPS_FORM_TYPE_REGION
					+ ActivitiConstant.CLAIM_GROUPS_VALUE_SPAN + region;
			claimGroupList.add(claimGroup);
		}
		if (!claimGroupList.isEmpty()) {
			taskQuery = taskQuery.taskCandidateGroupIn(claimGroupList);
		}
		// 获取和检测是否有替换者信息
		List<String> replaceUsers = getReplaceUsers(userId, null);
		if (replaceUsers != null) {
			replaceUsers.add(userId);
		}
		// 判断查询可候选任务
		if (showClaim) {
			if (replaceUsers != null) {
				if (isQueryJobs) {
					for (String u : replaceUsers) {
						taskQuery = taskQuery.taskCandidateOrAssigned(u);
					}
				} else {
					taskQuery = taskQuery.or();
					for (String u : replaceUsers) {
						taskQuery = taskQuery.taskCandidateOrAssigned(u);
					}
					taskQuery = taskQuery.endOr();
				}
			} else {
				taskQuery = taskQuery.taskCandidateOrAssigned(userId);
			}

		} else {
			if (replaceUsers != null) {
				taskQuery = taskQuery.taskAssigneeIds(replaceUsers);
			} else {
				taskQuery = taskQuery.taskAssignee(userId); // 指定个人任务查询，指定办理人
			}
		}

		// 如果查询过候选组，则结束or判断
		if (isQueryJobs) {
			taskQuery = taskQuery.endOr();
		}

		List<Task> taskList = new ArrayList<Task>();
		// 判断业务key查询数据，如果有多个，就查询多次，然后组合在一起
		if (CheckDataUtil.isNotNull(processBusinessKeyList))
		{
			for(String bKey : processBusinessKeyList)
			{
				taskList.addAll(taskQuery.processInstanceBusinessKey(bKey).list());
			}
		}
		else
		{
			taskQuery = taskQuery.processInstanceBusinessKey(ActivitiConstant.DEF_PROCESS_INST_BUSINESS_KEY); // 指定业务key
			taskList = taskQuery.list();
		}

		// 业务类型查询数据重新整合
		List<String> bTypeList = null;
		if (CheckDataUtil.isNotNull(businessTypeList))
		{
			bTypeList = new ArrayList<String>();
			for(String businessType : businessTypeList)
			{
				bTypeList.add(ActivitiConstant.PROCESS_KEY_SPAN + businessType + ActivitiConstant.PROCESS_KEY_SPAN); // 指定查询业务类型
			}
		}

		// Map<流程定义Key, Map<流程任务定义ID, TasksInfo>>
		Map<String, Map<String, TasksInfo>> tasksInfoMaps = new HashMap<String, Map<String, TasksInfo>>();
		// 流程定义ID对应流程Key缓存（加速查询用）
		Map<String, String> processKeys = new HashMap<String, String>();
		for (Task task : taskList) {
			// 判断是否符合业务类型
			if(bTypeList != null)
			{
				boolean isTrueType = false;
				for(String bType : bTypeList)
				{
					if(task.getProcessDefinitionId().indexOf(bType) > -1)
					{
						isTrueType = true;
						break;
					}
				}
				if(!isTrueType)
				{
					continue;
				}
			}
			String processKey = null;
			if (processKeys.containsKey(task.getProcessDefinitionId())) {
				processKey = processKeys.get(task.getProcessDefinitionId());
			} else {
				// 读取key
				processKey = getProcessKeyById(task.getProcessDefinitionId());
				processKeys.put(task.getProcessDefinitionId(), processKey);
			}
			Map<String, TasksInfo> tasksInfoMap = null;
			if (!tasksInfoMaps.containsKey(processKey)) {
				// 如果map数据不存在则创建新的
				tasksInfoMap = new HashMap<String, TasksInfo>();
				tasksInfoMaps.put(processKey, tasksInfoMap);
				TasksInfo tasksInfo = createTasksInfo(task, processKey);
				tasksInfoMap.put(task.getTaskDefinitionKey(), tasksInfo);
			} else {
				tasksInfoMap = tasksInfoMaps.get(processKey);
				if (!tasksInfoMap.containsKey(task.getTaskDefinitionKey())) {
					// 如果任务组信息不存在，则创建新的
					TasksInfo tasksInfo = createTasksInfo(task, processKey);
					tasksInfoMap.put(task.getTaskDefinitionKey(), tasksInfo);
				} else {
					TasksInfo tasksInfo = tasksInfoMap.get(task.getTaskDefinitionKey());
					tasksInfo.setCount(tasksInfo.getCount() + 1);
				}
			}
		}
		return tasksInfoMaps;
	}

	@Override
	public String getProcessPreview(String processId) throws WorkFlowException, IOException {
		CheckDataUtil.checkNull(processId, "processId");
		InputStream inputStream = null;
		try {
			inputStream = repositoryService.getProcessDiagram(processId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new WorkFlowException("60501", "流程定义不存在");
		}

		if (inputStream == null) {
			throw new WorkFlowException("60501", "流程预览图不存在");
		}
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}

		return result.toString("UTF-8");
	}

	@Override
	public ProcessInstData getProcessInstData(String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
		historicProcessInstanceQuery = historicProcessInstanceQuery.processInstanceId(processInstanceId);
		HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery.singleResult();
		ProcessInstData data = makeProcessInstanceData(historicProcessInstance);
		return data;
	}

	@Override
	public List<TaskSampleData> getProcessTasks(String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
		List<HistoricTaskInstance> list = historicTaskInstanceQuery.processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
		List<TaskSampleData> tasklist = new ArrayList<TaskSampleData>();
		for(HistoricTaskInstance task : list)
		{
			tasklist.add(makeTaskSampleData(task));
		}
		return tasklist;
	}

	/**
	 * @Title: makeTaskData
	 * @Description: 序列化TaskData
	 * @param task
	 * @return 参数说明
	 * @return TaskData 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	private TaskData makeTaskData(Task task, int stateType) {
		if (task == null)
			return null;
		TaskData taskData = new TaskData();
		taskData.setId(task.getId());
		taskData.setTaskDefId(task.getTaskDefinitionKey());
		taskData.setAssigness(task.getAssignee());
		taskData.setCreateTime(task.getCreateTime());
		taskData.setExecutionId(task.getExecutionId());
		taskData.setName(task.getName());
		taskData.setProcessDefinitionId(task.getProcessDefinitionId());
		taskData.setProcessInstanceId(task.getProcessInstanceId());
		taskData.setProcessDefinitionName(getProcessDefName(task.getProcessDefinitionId()));
		taskData.setDueDate(task.getDueDate());
		taskData.setStateType(stateType);
		taskData.setProcessInstBusinessKey(getProcessInstBusinessKey(task.getProcessInstanceId()));

		// 赋值自定义变量，表单，会签，URL数据
		List<FormData> formDataList = new ArrayList<FormData>();
		List<ProcessProperty> propertyList = new ArrayList<ProcessProperty>();
		List<SignUsersData> signUsersList = new ArrayList<SignUsersData>();
		List<ClaimUsersData> claimUsersList = new ArrayList<ClaimUsersData>();
		List<ClaimGroupData> claimGroupList = new ArrayList<ClaimGroupData>();
		List<JudgeProperty> judgeList = new ArrayList<JudgeProperty>();
		TaskFormData fdata = formService.getTaskFormData(task.getId());
		List<FormProperty> fvalues = fdata.getFormProperties();
		for (FormProperty fvalue : fvalues) {
			String id = fvalue.getId();
			String[] propertyTypes = id.split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
			String propertyType = propertyTypes[0];

			switch (propertyType) {
				case ActivitiConstant.SMARTFORM_FORM_ID_HEAD: // 表单
				{
					FormData form = new FormData();
					form.setId(propertyTypes[1]);
					form.setTitle(fvalue.getName());
					form.setReadable(fvalue.isReadable());
					form.setWritable(fvalue.isWritable());
					form.setRequired(fvalue.isRequired());
					if (fvalue.getValue() != null) {
						List<FormInstanceData> formInstanceList = JSONObject.parseArray(fvalue.getValue(),
								FormInstanceData.class);
						form.setDataList(formInstanceList);
					}

					formDataList.add(form);
				}
				break;
				case ActivitiConstant.RETRIEVE_KEY: // 取回信息
				{
					// 未完成任务不做处理
				}
				break;
				case ActivitiConstant.SIGN_USERS_FORM_ID_HEAD: // 会签用户
				{
					SignUsersData signUsers = new SignUsersData();
					String[] userKeys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String usersKey = null; // 会签用户自定义变量key
					String defusersKey = null; // 会签用户定义自定义变量key
					usersKey = userKeys[0];
					if (userKeys.length == 2) {
						defusersKey = userKeys[1];
					}

					signUsers.setId(usersKey);
					signUsers.setName(fvalue.getName());

					// 查询会签用户数据
					Object users = taskService.getVariable(task.getId(), usersKey);
					// 赋值会签用户列表
					List<UserInfo> userList = new ArrayList<UserInfo>();
					if (users != null) {
						List<String> useridlist = (List<String>) users;
						for (String userid : useridlist) {
							userList.add(loadUserInfo(userid, false));
						}
					}
					signUsers.setUserList(userList);
					if (defusersKey != null) {
						// 查询会签用户定义数据
						Object defusers = taskService.getVariable(task.getId(), defusersKey);
						// 赋值会签用户定义列表
						List<UserInfo> defUserList = new ArrayList<UserInfo>();
						if (defusers != null) {
							String[] useridlist = defusers.toString().split(",");
							for (String userid : useridlist) {
								defUserList.add(loadUserInfo(userid, false));
							}
						}
						signUsers.setUserDefList(defUserList);
					}
					signUsersList.add(signUsers);
				}
				break;
				case ActivitiConstant.CLAIM_USERS_FORM_ID_HEAD: // 候选用户
				{
					ClaimUsersData claimUsers = new ClaimUsersData();
					String[] userKeys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String usersKey = null; // 候选用户自定义变量key
					String defusersKey = null; // 候选用户定义自定义变量key
					usersKey = userKeys[0];
					if (userKeys.length == 2) {
						defusersKey = userKeys[1];
					}

					claimUsers.setId(usersKey);
					claimUsers.setName(fvalue.getName());

					// 查询候选用户数据
					Object users = taskService.getVariable(task.getId(), usersKey);
					// 赋值候选用户列表
					List<UserInfo> userList = new ArrayList<UserInfo>();
					if (users != null) {
						List<String> useridlist = (List<String>) users;
						for (String userid : useridlist) {
							userList.add(loadUserInfo(userid, false));
						}
					}
					claimUsers.setUserList(userList);
					if (defusersKey != null) {
						// 查询会签用户定义数据
						Object defusers = taskService.getVariable(task.getId(), defusersKey);
						// 赋值会签用户定义列表
						List<UserInfo> defUserList = new ArrayList<UserInfo>();
						if (defusers != null) {
							String[] useridlist = defusers.toString().split(",");
							for (String userid : useridlist) {
								defUserList.add(loadUserInfo(userid, true));
							}
						}
						claimUsers.setUserDefList(defUserList);
					}
					claimUsersList.add(claimUsers);
				}
				break;
				case ActivitiConstant.CLAIM_GROUPS_FORM_ID_HEAD: // 候选组
				{
					// 候选组信息不做处理
				}
				break;
				case ActivitiConstant.CLAIM_GROUPS_VALUE_FORM_ID_HEAD: // 候选组参数
				{
					ClaimGroupData claimGroupData = new ClaimGroupData();
					String[] keys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String groupKey = keys[0]; // 候选组自定义变量key
					claimGroupData.setId(groupKey);
					claimGroupData.setName(fvalue.getName());
					claimGroupList.add(claimGroupData);
				}
				break;
				case ActivitiConstant.CLAIM_GROUPS_DATA_FORM_ID_HEAD: // 候选组参数--指定值
				{
					ClaimGroupData claimGroupData = new ClaimGroupData();
					String[] keys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
					String groupKey = keys[0]; // 候选组自定义变量key
					claimGroupData.setId(groupKey);
					claimGroupData.setName(fvalue.getName());
					claimGroupList.add(claimGroupData);
				}
				break;
				case ActivitiConstant.TASK_JUDGE_HEAD: // 判断型变量
				{
					// 判断型变量处理
					JudgeProperty judge = new JudgeProperty();
					judge.setId(id);
					judge.setName(fvalue.getName());
					judge.setValue(fvalue.getValue());
					judge.setReadable(fvalue.isReadable());
					judge.setWritable(fvalue.isWritable());
					// 赋值选项值
					Map<String, String> judgeMap = (Map<String, String>) fvalue.getType().getInformation("values");
					if (judgeMap != null) {
						List<JudgeInfo> infoList = new ArrayList<JudgeInfo>();
						Iterator<Entry<String, String>> iter = judgeMap.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<String, String> entry = iter.next();
							JudgeInfo judgeInfo = new JudgeInfo();
							judgeInfo.setValue(entry.getKey());
							judgeInfo.setName(entry.getValue());
							infoList.add(judgeInfo);
						}
						judge.setInfoList(infoList);
					}
					judgeList.add(judge);
				}
				break;
				case ActivitiConstant.BUSINESS_KEY: // 业务url
				{
					taskData.setBusinessValue(fvalue.getValue());
				}
				break;
				case ActivitiConstant.EXPIRED_KEY: // 业务过期时间
				{
					taskData.setBusinessExpired(Integer.valueOf(fvalue.getValue()));
				}
				break;
				default: // 自定义变量
				{
					ProcessProperty property = new ProcessProperty();
					property.setId(id);
					property.setName(fvalue.getName());
					property.setValue(fvalue.getValue());
					property.setType(fvalue.getType().getName());
					property.setReadable(fvalue.isReadable());
					property.setWritable(fvalue.isWritable());
					property.setRequired(fvalue.isRequired());
					propertyList.add(property);
				}
				break;
			}
		}
		taskData.setClaimGroupList(claimGroupList);
		taskData.setFormDataList(formDataList);
		taskData.setPropertyList(propertyList);
		taskData.setSignUsersList(signUsersList);
		taskData.setClaimUsersList(claimUsersList);
		taskData.setJudgeList(judgeList);

		// 读取本地变量
		Map<String, String> exValues = new HashMap<String, String>();
		List<HistoricVariableInstance> values = historyService.createHistoricVariableInstanceQuery()
				.taskId(task.getId()).list();
		if (values != null && !values.isEmpty()) {
			for (HistoricVariableInstance value : values) {
				String id = value.getVariableName();
				String[] propertyTypes = id.split(ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN);
				String propertyType = propertyTypes[0];
				switch (propertyType) {
					case ActivitiConstant.BUSINESS_PROPERTY_KEY: // 业务自定义变量
					{
						exValues.put(propertyTypes[1], value.getValue().toString());
					}
					break;
					default: // 其余参数不做处理
						break;
				}
			}
		}
		taskData.setExValues(exValues);
		return taskData;
	}

	/**
	 * @Title: makeTaskData
	 * @Description: 序列化TaskData 根据历史任务
	 * @param task
	 * @return 参数说明
	 * @return TaskData 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	private TaskData makeTaskData(HistoricTaskInstance task) {
		if (task == null)
			return null;
		TaskData taskData = new TaskData();
		taskData.setId(task.getId());
		taskData.setTaskDefId(task.getTaskDefinitionKey());
		taskData.setAssigness(task.getAssignee());
		taskData.setCreateTime(task.getCreateTime());
		taskData.setEndTime(task.getEndTime());
		taskData.setExecutionId(task.getExecutionId());
		taskData.setName(task.getName());
		taskData.setProcessDefinitionId(task.getProcessDefinitionId());
		taskData.setProcessInstanceId(task.getProcessInstanceId());
		taskData.setDeleteReason(task.getDeleteReason());
		taskData.setProcessDefinitionName(getProcessDefName(task.getProcessDefinitionId()));
		taskData.setProcessInstBusinessKey(getProcessInstBusinessKey(task.getProcessInstanceId()));

		List<HistoricVariableInstance> values = historyService.createHistoricVariableInstanceQuery()
				.taskId(task.getId()).list();
		List<FormData> formDataList = new ArrayList<FormData>();
		List<ProcessProperty> propertyList = new ArrayList<ProcessProperty>();
		List<SignUsersData> signUsersList = new ArrayList<SignUsersData>();
		List<ClaimUsersData> claimUsersList = new ArrayList<ClaimUsersData>();
		Map<String, String> exValues = new HashMap<String, String>();
		if (values != null && !values.isEmpty()) {
			for (HistoricVariableInstance value : values) {
				String id = value.getVariableName();
				String[] propertyTypes = id.split(ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN);
				String propertyType = propertyTypes[0];
				switch (propertyType) {
					case ActivitiConstant.BUSINESS_KEY: // 业务URL
					{
						taskData.setBusinessValue(value.getValue().toString());
					}
					break;
					case ActivitiConstant.EXPIRED_KEY: // 业务过期时间
					{
						taskData.setBusinessExpired(Integer.valueOf(value.getValue().toString()));
					}
					break;
					case ActivitiConstant.SMARTFORM_FORM_ID_HEAD: // 表单
					{
						FormData form = new FormData();
						form.setId(propertyTypes[1]);
						form.setTitle(propertyTypes[2]);
						form.setReadable(true);
						form.setWritable(false);
						form.setRequired(false);
						if (value.getValue() != null) {
							List<FormInstanceData> formInstanceList = JSONObject.parseArray(value.getValue().toString(),
									FormInstanceData.class);
							form.setDataList(formInstanceList);
						}
						formDataList.add(form);
					}
					break;
					case ActivitiConstant.RETRIEVE_KEY: // 取回信息
					{

						String retrieveTaskDefIds = value.getValue().toString()
								.split(ActivitiConstant.RETRIEVE_PROPERTY_VALUE_SPAN)[0];
						taskData.setCanRetrieve(canRetrieveTask(task.getProcessInstanceId(), retrieveTaskDefIds));
					}
					break;
					case ActivitiConstant.SIGN_USERS_FORM_ID_HEAD: // 会签用户
					{
						SignUsersData signUsers = new SignUsersData();
						String usersKey = propertyTypes[1]; // 会签用户自定义变量key

						signUsers.setId(usersKey);
						signUsers.setName(propertyTypes[2]);

						// 查询会签用户数据
						Object users = value.getValue();
						// 赋值会签用户列表
						List<UserInfo> userList = new ArrayList<UserInfo>();
						if (users != null) {
							List<String> useridlist = (List<String>) users;
							for (String userid : useridlist) {
								userList.add(loadUserInfo(userid, false));
							}
						}
						signUsers.setUserList(userList);
						signUsersList.add(signUsers);
					}
					break;
					case ActivitiConstant.CLAIM_USERS_FORM_ID_HEAD: // 候选用户
					{
						ClaimUsersData claimUsers = new ClaimUsersData();
						String usersKey = propertyTypes[1]; // 候选用户自定义变量key

						claimUsers.setId(usersKey);
						claimUsers.setName(propertyTypes[2]);

						// 查询候选用户数据
						Object users = value.getValue();
						// 赋值候选用户列表
						List<UserInfo> userList = new ArrayList<UserInfo>();
						if (users != null) {
							List<String> useridlist = (List<String>) users;
							for (String userid : useridlist) {
								userList.add(loadUserInfo(userid, false));
							}
						}
						claimUsers.setUserList(userList);
						claimUsersList.add(claimUsers);

					}
					break;
					case ActivitiConstant.CLAIM_GROUPS_FORM_ID_HEAD: // 候选组
					{
						// 候选组信息不做处理
					}
					break;
					case ActivitiConstant.BUSINESS_PROPERTY_KEY: // 业务自定义变量
					{
						exValues.put(propertyTypes[1], String.valueOf(value.getValue()));
					}
					break;
					default: // 自定义变量
					{
						if (propertyTypes.length != 3) {
							break;
						}
						ProcessProperty property = new ProcessProperty();
						property.setId(propertyTypes[1]);
						property.setName(propertyTypes[2]);
						property.setValue(String.valueOf(value.getValue()));
						property.setType(value.getVariableTypeName());
						property.setReadable(true);
						property.setWritable(false);
						property.setRequired(false);
						propertyList.add(property);
					}
					break;
				}
			}
		}
		taskData.setFormDataList(formDataList);
		taskData.setPropertyList(propertyList);
		taskData.setSignUsersList(signUsersList);
		taskData.setClaimUsersList(claimUsersList);
		taskData.setExValues(exValues);
		return taskData;
	}

	/**
	 * @Title: makeTaskSampleData
	 * @Description: 序列化TaskSampleData
	 * @param task
	 * @return 参数说明
	 * @return TaskSampleData 返回类型
	 *
	 */
	@SuppressWarnings("unchecked")
	private TaskSampleData makeTaskSampleData(Task task, int stateType, boolean hasBusinessKey, Map<String, TaskSampleData> paramsCache) {
		TaskSampleData taskSampleData = new TaskSampleData();
		taskSampleData.setId(task.getId());
		taskSampleData.setTaskDefId(task.getTaskDefinitionKey());
		taskSampleData.setName(task.getName());
		taskSampleData.setCreateTime(task.getCreateTime());
		taskSampleData.setProcessDefinitionId(task.getProcessDefinitionId());
		taskSampleData.setProcessInstanceId(task.getProcessInstanceId());
		taskSampleData.setExecutionId(task.getExecutionId());
		taskSampleData.setDueDate(task.getDueDate());
		taskSampleData.setStateType(stateType);
		taskSampleData.setAssignee(task.getAssignee());
		if(hasBusinessKey)
		{
			taskSampleData.setProcessInstBusinessKey(getProcessInstBusinessKey(task.getProcessInstanceId()));
		}

		// 判断参数缓存是否存在
		if(paramsCache.containsKey(taskSampleData.getTaskDefId()))
		{
			TaskSampleData taskCache = paramsCache.get(taskSampleData.getTaskDefId());
			taskSampleData.setBusinessValue(taskCache.getBusinessValue());
			taskSampleData.setBusinessExpired(Integer.valueOf(taskCache.getBusinessExpired()));
			taskSampleData.setJudgeList(taskCache.getJudgeList());
		}
		else
		{
			// 不存在则从数据库获取并缓存
			List<JudgeProperty> judgeList = new ArrayList<JudgeProperty>();
			TaskFormData fdata = formService.getTaskFormData(task.getId());
			List<FormProperty> fvalues = fdata.getFormProperties();
			for (FormProperty fvalue : fvalues) {
				String id = fvalue.getId();
				String[] propertyTypes = id.split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
				String propertyType = propertyTypes[0];
				switch (propertyType) {
					case ActivitiConstant.BUSINESS_KEY: // 业务URL
					{
						taskSampleData.setBusinessValue(fvalue.getValue());
					}
					break;
					case ActivitiConstant.EXPIRED_KEY: // 业务过期时间
					{
						taskSampleData.setBusinessExpired(Integer.valueOf(fvalue.getValue()));
					}
					break;
					case ActivitiConstant.TASK_JUDGE_HEAD: // 判断型变量
					{
						// 判断型变量处理
						JudgeProperty judge = new JudgeProperty();
						judge.setId(id);
						judge.setName(fvalue.getName());
						judge.setValue(fvalue.getValue());
						judge.setReadable(fvalue.isReadable());
						judge.setWritable(fvalue.isWritable());
						// 赋值选项值
						Map<String, String> judgeMap = (Map<String, String>) fvalue.getType().getInformation("values");
						if (judgeMap != null) {
							List<JudgeInfo> infoList = new ArrayList<JudgeInfo>();
							Iterator<Entry<String, String>> iter = judgeMap.entrySet().iterator();
							while (iter.hasNext()) {
								Entry<String, String> entry = iter.next();
								JudgeInfo judgeInfo = new JudgeInfo();
								judgeInfo.setValue(entry.getKey());
								judgeInfo.setName(entry.getValue());
								infoList.add(judgeInfo);
							}
							judge.setInfoList(infoList);
						}
						judgeList.add(judge);
					}
					break;
				}

			}

			taskSampleData.setJudgeList(judgeList);

			// 加入缓存
			paramsCache.put(taskSampleData.getTaskDefId(), taskSampleData);
		}


		return taskSampleData;
	}

	/**
	 * @Title: makeTaskSampleData
	 * @Description: 序列化TaskSampleData 根据历史任务
	 * @param task
	 * @return 参数说明
	 * @return TaskSampleData 返回类型
	 *
	 */
	private TaskSampleData makeTaskSampleData(HistoricTaskInstance task) {
		TaskSampleData taskSampleData = new TaskSampleData();
		taskSampleData.setId(task.getId());
		taskSampleData.setTaskDefId(task.getTaskDefinitionKey());
		taskSampleData.setName(task.getName());
		taskSampleData.setCreateTime(task.getCreateTime());
		taskSampleData.setEndTime(task.getEndTime());
		taskSampleData.setProcessDefinitionId(task.getProcessDefinitionId());
		taskSampleData.setProcessInstanceId(task.getProcessInstanceId());
		taskSampleData.setExecutionId(task.getExecutionId());
		taskSampleData.setProcessInstBusinessKey(getProcessInstBusinessKey(task.getProcessInstanceId()));
		taskSampleData.setAssignee(task.getAssignee());
		HistoricVariableInstance value = historyService.createHistoricVariableInstanceQuery().taskId(task.getId())
				.variableName(ActivitiConstant.BUSINESS_KEY).singleResult();
		if (value != null) {
			taskSampleData.setBusinessValue(String.valueOf(value.getValue()));
		}

		value = historyService.createHistoricVariableInstanceQuery().taskId(task.getId())
				.variableName(ActivitiConstant.EXPIRED_KEY).singleResult();
		if (value != null) {
			taskSampleData.setBusinessExpired(Integer.valueOf(String.valueOf(value.getValue())));
		}

		value = historyService.createHistoricVariableInstanceQuery().taskId(task.getId())
				.variableName(ActivitiConstant.RETRIEVE_KEY).singleResult();
		if (value != null) {
			String retrieveTaskDefIds = value.getValue().toString()
					.split(ActivitiConstant.RETRIEVE_PROPERTY_VALUE_SPAN)[0];
			taskSampleData.setCanRetrieve(canRetrieveTask(task.getProcessInstanceId(), retrieveTaskDefIds));
		}

		return taskSampleData;
	}

	/**
	 * @Title: getBusinessValue
	 * @Description: 获取任务业务地址
	 * @param taskId
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getTaskBusinessValue(String taskId) {
		TaskFormData fdata = formService.getTaskFormData(taskId);
		List<FormProperty> fvalues = fdata.getFormProperties();
		for (FormProperty fvalue : fvalues) {
			String id = fvalue.getId();
			if (id.equals(ActivitiConstant.BUSINESS_KEY)) // 业务URL
			{
				return fvalue.getValue();
			}
		}
		return null;
	}

	/**
	 * @Title: loadUserInfo
	 * @Description: 加载用户详情--后续考虑通用性，工作流不再获取用户和部门详细信息
	 * @param userId
	 * @return 参数说明
	 * @return UserInfo 返回类型
	 *
	 */
	private UserInfo loadUserInfo(String userId, boolean loadUnit) {
		UserInfo info = new UserInfo();
		info.setId(userId);

		info.setNick(userId);
		// 请求用户中心获取用户名字
		try{
			List<UserDepartDto> userdeparlist=authInfoUtil.getUserById(userId);
			if(userdeparlist==null || userdeparlist.size()<1){
				info.setNick(userId);
			} else {
				info.setNick(userdeparlist.get(0).getName());
				if (loadUnit) {
					String unitid="";
					String unitname="";
					for(UserDepartDto userdepar : userdeparlist) {
						unitid+=userdepar.getDepartId();
						unitname+=userdepar.getDepartName();
					}
					info.setUnitId(unitid);
					info.setUnitName(unitname);
				}
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return info;
	}

	/**
	 * @Title: getProcessDefName
	 * @Description: 根据流程定义ID获取流程定义名称
	 * @param processDefId
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getProcessDefName(String processDefId) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefId).singleResult();
		if (processDefinition == null) {
			return null;
		}
		return processDefinition.getName();
	}

	/**
	 * @Title: getProcessInstBusinessKey
	 * @Description: 获取流程实例业务key
	 * @param processInstId
	 * @return  参数说明
	 * @return String    返回类型
	 *
	 */
	private String getProcessInstBusinessKey(String processInstId) {
		HistoricProcessInstance processInst = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstId)
				.singleResult();
		if (processInst == null) {
			return null;
		}
		return processInst.getBusinessKey();
	}


	/**
	 * @Title: createTasksInfo
	 * @Description: 创建任务组信息
	 * @param task
	 * @return 参数说明
	 * @return TasksInfo 返回类型
	 *
	 */
	private TasksInfo createTasksInfo(Task task, String processDefinitionKey) {
		TasksInfo tasksInfo = new TasksInfo();
		tasksInfo.setProcessDefinitionKey(processDefinitionKey);
		tasksInfo.setProcessDefinitionName(getProcessDefName(task.getProcessDefinitionId()));
		tasksInfo.setName(task.getName());
		tasksInfo.setTaskDefId(task.getTaskDefinitionKey());
		tasksInfo.setBusinessValue(getTaskBusinessValue(task.getId()));
		tasksInfo.setCount(1);
		tasksInfo.setProcessBusinessType(getProcessTypeByKey(processDefinitionKey));
		return tasksInfo;
	}

	/**
	 * @Title: getProcessKeyById
	 * @Description: 根据流程定义ID获取流程定义key
	 * @param processDefId
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getProcessKeyById(String processDefId) {
		return processDefId.substring(0, processDefId.indexOf(":"));
//		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
//		if(processDefinition == null)
//		{
//			return null;
//		}
//		return processDefinition.getKey();
	}

	/**
	 * @Title: getProcessTypeByKey
	 * @Description: 根据流程key获取流程业务类型，当前流程key规则为 流程类型_流程业务类型_key
	 * @param processKey
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getProcessTypeByKey(String processKey) {
		if (CheckDataUtil.isNull(processKey)) {
			return null;
		}
		String[] values = processKey.split(ActivitiConstant.PROCESS_KEY_SPAN);
		if (values.length != 3) {
			return null;
		}
		return values[1];
	}

	/**
	 * @Title: findTask
	 * @Description: 查询任务
	 * @param taskId
	 * @return 参数说明
	 * @return Task 返回类型
	 *
	 */
	private Task findTask(String taskId) {
		Task task = taskService // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				.taskId(taskId) // 查询候选组的任务
				.singleResult();
		return task;
	}

	/**
	 * @Title: getSubProcessInstanceByParent
	 * @Description: 根据主流程实例获取子流程实例列表
	 * @param parentProcessId
	 * @return 参数说明
	 * @return List<ProcessInstance> 返回类型
	 *
	 */
	private List<ProcessInstance> getSubProcessInstanceByParent(String parentProcessId) {
		return runtimeService.createProcessInstanceQuery().superProcessInstanceId(parentProcessId).list();
	}

	/**
	 * @ClassName: EProcessInstanceState
	 * @Description: 流程实例状态
	 * @author KaminanGTO
	 * @date 2018年11月9日 下午5:39:11
	 *
	 */
	enum EProcessInstanceState {

		/**
		 * @Fields All : 所有
		 */
		All(-1),
		/**
		 * @Fields Suspend : 已暂停
		 */
		Suspend(0),
		/**
		 * @Fields Active : 进行中
		 */
		Active(1),
		/**
		 * @Fields Finish : 已完成
		 */
		Finish(2),
		/**
		 * @Fields Delete : 已删除（已终止）
		 */
		Delete(3);

		public int value;

		EProcessInstanceState(int value) {
			this.value = value;
		}

		static EProcessInstanceState valueOf(int value) {
			EProcessInstanceState state = EProcessInstanceState.All;
			if (value > -1 && value < EProcessInstanceState.values().length - 1) {
				state = EProcessInstanceState.values()[value + 1];
			}
			return state;
		}
	}

	/**
	 * @Title: makeProcessInstanceData
	 * @Description: 序列化流程实例数据-根据历史记录
	 * @param historicProcessInstance
	 * @return 参数说明
	 * @return ProcessInstData 返回类型
	 *
	 */
	private ProcessInstData makeProcessInstanceData(HistoricProcessInstance historicProcessInstance) {
		ProcessInstData data = new ProcessInstData();
		data.setId(historicProcessInstance.getId());
		data.setName(historicProcessInstance.getProcessDefinitionName());
		data.setProcessDefId(historicProcessInstance.getProcessDefinitionId());
		data.setStartTime(historicProcessInstance.getStartTime());
		data.setEndTime(historicProcessInstance.getEndTime());
		data.setDeleteReason(historicProcessInstance.getDeleteReason());
		if (data.getEndTime() == null) {
			// 未结束流程，查询流程状态
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceId(data.getId()).singleResult();
			data.setState(processInstance.isSuspended() ? EProcessInstanceState.Suspend.value
					: EProcessInstanceState.Active.value);
		} else {
			// 已结束流程，判断流程状态
			if (historicProcessInstance.getDeleteReason() != null) {
				data.setState(EProcessInstanceState.Delete.value);
			} else {
				data.setState(EProcessInstanceState.Finish.value);
			}

		}
		return data;
	}

	/**
	 * @Title: saveTaskExValues
	 * @Description: 保存业务自定义变量数据到任务
	 * @param taskId
	 * @param exValues 参数说明
	 * @return void 返回类型
	 *
	 */
	@Transactional
	public void saveTaskExValues(String taskId, Map<String, String> exValues) {
		Map<String, String> values = new HashMap<String, String>();
		Iterator<Entry<String, String>> iter = exValues.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			values.put(
					ActivitiConstant.BUSINESS_PROPERTY_KEY + ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN + entry.getKey(),
					entry.getValue());
		}
		taskService.setVariablesLocal(taskId, values);
	}

	/**
	 * @Title: getUserFinishedKey
	 * @Description: 获取用户已完成任务视图key
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getUserFinishedKey() {
		return JedisMgr_wf.KeyHead + ActivitiConstant.REDIS_USER_FINISHED_VIEW_KEY;
	}

	/**
	 * @Title: getUnitFinishedKey
	 * @Description: 获取部门已完成任务视图key
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getUnitFinishedKey() {
		return JedisMgr_wf.KeyHead + ActivitiConstant.REDIS_UNIT_FINISHED_VIEW_KEY;
	}

	/**
	 * @Title: getReplaceUsers
	 * @Description: 根据顶替者获取被顶替用户
	 * @param userId
	 * @return 参数说明
	 * @return List<String> 返回类型
	 *
	 */
	private List<String> getReplaceUsers(String userId, String processId) {
		UserReplace userReplace = userReplaceDao.get(userId);
		if (userReplace == null) {
			return null;
		}
		List<String> userList = new ArrayList<String>(); // 被替换者用户列表
		List<String> delKeys = new ArrayList<String>(); // 待删除过期信息
		long now = System.currentTimeMillis();
		if (CheckDataUtil.isNotNull(processId)) {
			// 查询指定流程顶替信息
			if (!userReplace.getReplaceInfos().containsKey(processId)) {
				return null;
			}
			List<UserReplaceInfo> UserReplaceList = userReplace.getReplaceInfos().get(processId);
			for (UserReplaceInfo userReplaceInfo : UserReplaceList) {
				if (now > userReplaceInfo.getEndTime().getTime()) {
					// 已过期
					delKeys.add(makeReplaceKey(processId, userReplaceInfo.getReplaceUser()));
				} else if (userReplaceInfo.getStartTime().getTime() > now) {
					// 已开始
					userList.add(userReplaceInfo.getReplaceUser());
				}
			}
		} else {
			// 查询所有流程顶替信息
			Iterator<Entry<String, List<UserReplaceInfo>>> iter = userReplace.getReplaceInfos().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, List<UserReplaceInfo>> entry = iter.next();
				List<UserReplaceInfo> UserReplaceList = entry.getValue();
				for (UserReplaceInfo userReplaceInfo : UserReplaceList) {
					if (now > userReplaceInfo.getEndTime().getTime()) {
						// 已过期
						delKeys.add(makeReplaceKey(entry.getKey(), userReplaceInfo.getReplaceUser()));
					} else if (userReplaceInfo.getStartTime().getTime() > now) {
						// 已开始
						userList.add(userReplaceInfo.getReplaceUser());
					}
				}
			}
		}
		// 检测和清理过期替换信息
		if (!delKeys.isEmpty()) {
			userReplaceDao.deleteParams(userId, delKeys);
		}
		return userList.isEmpty() ? null : userList;
	}

	/**
	 * @Title: makeReplaceKey
	 * @Description: 组合替换信息hashkey
	 * @param processId
	 * @param userId
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String makeReplaceKey(String processId, String userId) {
		return processId + ActivitiConstant.REDIS_HASHKEY_SPAN + userId;
	}

	/**
	 * @Title: canRetrieveTask
	 * @Description: 是否有可取回任务
	 * @param processInstanceId
	 * @param taskDefIds
	 * @return 参数说明
	 * @return Boolean 返回类型
	 *
	 */
	private Boolean canRetrieveTask(String processInstanceId, String taskDefIds) {
		String[] taskDefIdList = taskDefIds.split(ActivitiConstant.RETRIEVE_TASK_SPAN);
		TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);
		for (String taskDefId : taskDefIdList) {
			if (taskQuery.taskDefinitionKey(taskDefId).count() == 1) {
				return true;
			}
		}
		return false;
	}

	//跳转方法
	@Transactional
	public void jump(Task startTask, String targetTaskDefId) throws WorkFlowException{
		//获取流程定义
		org.activiti.bpmn.model.Process process = repositoryService.getBpmnModel(startTask.getProcessDefinitionId()).getMainProcess();
		//获取目标节点定义
		FlowNode targetNode = (FlowNode)process.getFlowElement(targetTaskDefId);
		if(targetNode == null)
		{
			throw new WorkFlowException("跳转目标任务不存在. taskDefId:" + targetTaskDefId);
		}
		//删除当前运行任务
		String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(startTask.getId()));
		if("挂起的任务不能跳转".equals(executionEntityId))
		{
			throw new WorkFlowException(executionEntityId);
		}
		//流程执行到来源节点
		managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
	}

	//删除当前运行时任务命令，并返回当前任务的执行对象id
	//这里继承了NeedsActiveTaskCmd，主要时很多跳转业务场景下，要求不能时挂起任务。可以直接继承Command即可
	public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
		private static final long serialVersionUID = 1L;
		public DeleteTaskCmd(String taskId){
			super(taskId);
		}
		public String execute(CommandContext commandContext, TaskEntity currentTask){
			//获取所需服务
			TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl)commandContext.getTaskEntityManager();
			//获取当前任务的来源任务及来源节点信息
			ExecutionEntity executionEntity = currentTask.getExecution();
			//删除当前任务,来源任务
			taskEntityManager.deleteTask(currentTask, "手动跳转任务", false, false);
			return executionEntity.getId();
		}
		public String getSuspendedTaskException() {
			return "挂起的任务不能跳转";
		}
	}

	//根据提供节点和执行对象id，进行跳转命令
	public class SetFLowNodeAndGoCmd implements Command<Void> {
		private FlowNode flowElement;
		private String executionId;
		public SetFLowNodeAndGoCmd(FlowNode flowElement,String executionId){
			this.flowElement = flowElement;
			this.executionId = executionId;
		}

		public Void execute(CommandContext commandContext){
			//获取目标节点的来源连线
			List<SequenceFlow> flows = flowElement.getIncomingFlows();
			if(flows==null || flows.size()<1){
				throw new ActivitiException("回退错误，目标节点没有来源连线");
			}
			//随便选一条连线来执行，时当前执行计划为，从连线流转到目标节点，实现跳转
			ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
			executionEntity.setCurrentFlowElement(flows.get(0));
			commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
			return null;
		}
	}
}
