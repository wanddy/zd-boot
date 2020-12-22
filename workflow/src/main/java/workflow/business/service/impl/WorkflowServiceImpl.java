package workflow.business.service.impl;

import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import workflow.business.mapper.UserReplaceDao;
import workflow.business.model.*;
import workflow.business.model.entity.UserTaskFinishedEntity;
import workflow.business.service.ActiveTaskService;
import workflow.business.service.ProcessTaskService;
import workflow.business.service.WorkflowService;
import workflow.business.service.impl.ActivitiServiceImpl.EProcessInstanceState;
import workflow.common.constant.ActivitiConstant;
import workflow.common.error.WorkFlowException;
import workflow.common.redis.JedisMgr_wf;
import workflow.common.utils.CheckDataUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;


/**
* @ClassName: ActivitiNewServiceImpl
* @Description: 新版工作流服务
* @author KaminanGTO
* @date Jul 5, 2019 4:35:21 PM
*
*/
@Service("workflowService")
@Component
@DS("master")
public class WorkflowServiceImpl implements WorkflowService {

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
	private UserTaskFinishedServiceImpl userTaskFinishedService;

	@Autowired
	private JedisMgr_wf jedisMgrWf;

	@Autowired
	private UserReplaceDao userReplaceDao;

	@Autowired
	private ProcessTaskService processTaskService;

	@Autowired
	private AuthInfo authInfoUtil;
	@Autowired
	private ActiveTaskService activeTaskService;
	@Override
	public PageList<ProcessSampleData> getProcessList(String name, boolean onlyLatestVersion, String businessType,
			int pageNum, int pageSize) throws WorkFlowException {
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		int firstResult = (pageNum - 1) * pageSize;
		int maxResults = pageSize;
		// 只查激活中流程
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().active();
		if (CheckDataUtil.isNotNull(name)) {
			processDefinitionQuery = processDefinitionQuery.processDefinitionNameLike("%" + name + "%");
		}
		if (onlyLatestVersion) {
			processDefinitionQuery = processDefinitionQuery.latestVersion();
		}
		if (CheckDataUtil.isNotNull(businessType)) {
			processDefinitionQuery = processDefinitionQuery.processDefinitionKeyLike(
					"main\\" + ActivitiConstant.PROCESS_KEY_SPAN + businessType + "\\" + ActivitiConstant.PROCESS_KEY_SPAN + "%");
		}
		processDefinitionQuery = processDefinitionQuery.orderByProcessDefinitionVersion().desc();
		PageList<ProcessSampleData> pageList = new PageList<ProcessSampleData>();
		pageList.setPageNum(pageNum);
		pageList.setPageSize(pageSize);
		pageList.setTotal((int) processDefinitionQuery.count());
		if (pageList.getTotal() == 0) {
			return pageList;
		}

		List<ProcessDefinition> processList = processDefinitionQuery.listPage(firstResult, maxResults);
		List<ProcessSampleData> list = new ArrayList<ProcessSampleData>();
		for (ProcessDefinition process : processList) {
			ProcessSampleData data = new ProcessSampleData();
			data.setId(process.getId());
			data.setKey(process.getKey());
			data.setName(process.getName());
			list.add(data);
		}
		pageList.setRows(list);

		return pageList;
	}

	@Override
	@Transactional
	public ActiveTask startProcessInstanceById(String userId, String processId, Map<String, Object> values,
			String businessKey, TaskContentData contentData) throws WorkFlowException {
		CheckDataUtil.checkNull(processId, "processId");
		CheckDataUtil.checkNull(contentData, "contentData");
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
		boolean isaddtask = true;
		try {
			if (values == null) {
				pi = runtimeService.startProcessInstanceById(processId, businessKey);
			} else {
				pi = runtimeService.startProcessInstanceById(processId, businessKey, values);
			}

		} catch (ActivitiObjectNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程不存在");
		} catch(Exception e) {
			isaddtask = false;
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "任务启动失败");
		}
		/*新增校验，启动失败时不做其他操作*/
		if(pi == null || isaddtask==false){
			logger.info("----workflowService.startProcessInstanceById-----工作流启动失败--------");
			return null;
		}

		initProcessParams(pi);
		ActiveTask activeTask = processTaskService.saveTask(userId, contentData, getProcessInstActiveTaskList(pi.getId(), true));
		return activeTask;
	}

	@Override
	@Transactional
	public ActiveTask startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values,
			String businessKey, TaskContentData contentData) throws WorkFlowException {
		CheckDataUtil.checkNull(processKey, "processKey");
		CheckDataUtil.checkNull(contentData, "contentData");
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
		boolean isaddtask = true;
		try {
			if (values == null) {
				pi = runtimeService.startProcessInstanceByKey(processKey, businessKey);
			} else {
				pi = runtimeService.startProcessInstanceByKey(processKey, businessKey, values);
			}
		} catch (ActivitiObjectNotFoundException e) {
			isaddtask = false;
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程不存在");
		} catch(Exception e){
			isaddtask = false;
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "任务启动失败");
		}
		/*新增校验，启动失败时不做其他操作*/
		if(pi == null || isaddtask==false){
			logger.info("----workflowService.startProcessInstanceByKey-----工作流启动失败--------");
			throw new WorkFlowException("60501", "任务启动失败，空");
		}
		initProcessParams(pi);
		ActiveTask activeTask = processTaskService.saveTask(userId, contentData, getProcessInstActiveTaskList(pi.getId(), true));
		return activeTask;
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
	@Transactional
	public boolean stopProcessInstance(String userId, String processInstanceId, String reason)
			throws WorkFlowException {
		return stopProcessInstance(userId, processInstanceId, reason, false);
	}

	@Override
	public boolean stopProcessInstance(String userId, String processInstanceId, String reason, boolean needComplate)
			throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		CheckDataUtil.checkNull(reason, "reason");
		/*System.out.println("===========userId:"+userId);
		System.out.println("===========processInstanceId:"+processInstanceId);
		System.out.println("===========reason:"+reason);*/
		// 赋值当前任务本地自定义变量
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		for (Task task : tasks) {
			saveTaskLocalValues(task.getId());
		}

		try {
			runtimeService.deleteProcessInstance(processInstanceId, reason);
		} catch (ActivitiObjectNotFoundException e) {
			throw new WorkFlowException("60501", "流程实例不存在");
		}

		// 查询停止子流程实例
		List<ProcessInstance> subList = getSubProcessInstanceByParent(processInstanceId);
		if (subList != null && !subList.isEmpty()) {
			for (ProcessInstance subProcess : subList) {
				if (!subProcess.isEnded()) {
					runtimeService.suspendProcessInstanceById(processInstanceId);
					tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
					for (Task task : tasks) {
						saveTaskLocalValues(task.getId());
					}

					try {
						runtimeService.deleteProcessInstance(subProcess.getProcessInstanceId(), reason);
					} catch (ActivitiObjectNotFoundException e) {
						throw new WorkFlowException("60501", "流程实例不存在");
					}
				}
			}
		}
		if(needComplate)
		{
			// 如果需要完成任务，则调用完成任务
			processTaskService.complateTask(userId, processInstanceId, reason);
		}
		else
		{
			processTaskService.updateStatus(null, processInstanceId, null, 2);
		}

		return true;
	}

	@Override
	@Transactional
	public boolean suspendProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");

		try {
			runtimeService.suspendProcessInstanceById(processInstanceId);
		} catch (ActivitiObjectNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程实例不存在");
		} catch (ActivitiException e) {
			logger.warn(e.getMessage());
			throw new WorkFlowException("60501", "流程实例已经暂停");
		}
		// 查询子流程实例
		List<ProcessInstance> subList = getSubProcessInstanceByParent(processInstanceId);
		if (subList != null && !subList.isEmpty()) {
			for (ProcessInstance subProcess : subList) {
				if (!subProcess.isSuspended()) {
					runtimeService.suspendProcessInstanceById(processInstanceId);
				}
			}
		}
		processTaskService.updateStatus(null, processInstanceId, null, 1);
		return true;
	}

	@Override
	@Transactional
	public boolean activateProcessInstance(String userId, String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		try {
			runtimeService.activateProcessInstanceById(processInstanceId);
		} catch (ActivitiObjectNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程实例不存在");
		} catch (ActivitiException e) {
			logger.warn(e.getMessage());
			throw new WorkFlowException("60501", "流程实例已经激活");
		}
		// 查询子流程实例
		List<ProcessInstance> subList = getSubProcessInstanceByParent(processInstanceId);
		if (subList != null && !subList.isEmpty()) {
			for (ProcessInstance subProcess : subList) {
				if (subProcess.isSuspended()) {
					runtimeService.activateProcessInstanceById(processInstanceId);
				}
			}
		}
		processTaskService.updateStatus(null, processInstanceId, null, 0);
		return true;
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
	public List<TaskDefData> getProcessInfoByProcessInstId(String processInstanceId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
		HistoricProcessInstance processInstance = findProcessInstByhistory(processInstanceId);
		if (processInstance == null) {
			return null;
		}
		return getProcessInfoByDefId(processInstance.getProcessDefinitionId());
	}

	@Override
	public List<TaskDefData> getProcessInfoByProcessKey(String processDefinitionKey) throws WorkFlowException {
		CheckDataUtil.checkNull(processDefinitionKey, "processDefinitionKey");
		// 查询最新的流程定义id
		ProcessDefinition process = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
		if (process == null)
		{
			return null;
		}
		return getProcessInfoByDefId(process.getId());
	}

	@Override
	public List<TaskDefData> getProcessInfoByDefId(String processDefinitionId) throws WorkFlowException {
		CheckDataUtil.checkNull(processDefinitionId, "processDefinitionId");
		List<TaskDefData> list = new ArrayList<TaskDefData>();
		BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
		if (model != null) {

			Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
			for (FlowElement e : flowElements) {
				 System.out.println("flowelement id:" + e.getId() + " name:" +
				 e.getName() + " class:" + e.getClass().toString());
				// logger.debug(e.getName() + " ------ " + e.getDocumentation());
				if(e instanceof UserTask)
				{
					UserTask userTask = (UserTask)e;
					String taskid = userTask.getId();
					if (taskid != null) {
						String[] names = taskid.split(ActivitiConstant.PROCESS_TASK_ID_SPAN);
						System.out.println("============names:"+names.toString());
						// 判断任务名是否规范，不规范的不加入任务列表
						if(!ActivitiConstant.USERTASK_HEAD.equals(names[0]))
						{
							break;
						}
						TaskDefData taskDefData = new TaskDefData();
						List<JudgeProperty> judgeList = new ArrayList<JudgeProperty>();
						// 赋值自定义变量，表单，会签，URL数据
						List<FormData> formDataList = new ArrayList<FormData>();
						taskDefData.setId(taskid);
						taskDefData.setName(userTask.getName());
						int sort = 0;
						if(names.length > 1)
						{
							try {
								sort = Integer.parseInt(names[1]);
							} catch (NumberFormatException ex) {

							}
						}

						taskDefData.setSort(sort);
						// 赋值判断型变量
						for(org.activiti.bpmn.model.FormProperty property : userTask.getFormProperties())
						{
							String id = property.getId();
							String[] propertyTypes = id.split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
							String propertyType = propertyTypes[0];
							System.out.println("=========propertyType:"+propertyType);
							switch (propertyType) {
								case ActivitiConstant.TASK_JUDGE_HEAD: // 判断型变量
								{
									// 判断型变量处理
									JudgeProperty judge = new JudgeProperty();
									judge.setId(id);
									judge.setName(property.getName());
									judge.setValue(property.getVariable());
									judge.setReadable(property.isReadable());
									judge.setWritable(property.isWriteable());
									// 赋值选项值
									if(CheckDataUtil.isNotNull(property.getFormValues()))
									{
										List<JudgeInfo> infoList = new ArrayList<JudgeInfo>();
										for(FormValue formValue : property.getFormValues())
										{
											JudgeInfo judgeInfo = new JudgeInfo();
											judgeInfo.setValue(formValue.getId());
											judgeInfo.setName(formValue.getName());
											infoList.add(judgeInfo);
										}
										judge.setInfoList(infoList);
									}
									judgeList.add(judge);
								}
								break;
								case  ActivitiConstant.SMARTFORM_FORM_ID_HEAD: //表单形变量
								{
									System.out.println("=====SMARTFORM_FORM_ID_HEAD");
									System.out.println("id:"+id);
									System.out.println("property.getName():"+property.getName());
									System.out.println("property.isReadable():"+property.isReadable());
									System.out.println("property.isWriteable():"+property.isWriteable());
									System.out.println("property.getVariable():"+property.getVariable());
									System.out.println("property.getDefaultExpression():"+property.getDefaultExpression());
									System.out.println("property.getDatePattern():"+property.getDatePattern());
									System.out.println("property.getFormValues():"+property.getFormValues().toString());
									System.out.println("=====SMARTFORM_FORM_ID_HEAD end");

									FormData formData = new FormData();
									formData.setId(id);
									formData.setTitle(property.getName());
									formData.setReadable(property.isReadable());
									formData.setWritable(property.isWriteable());
									if(property.getDefaultExpression()!=null){
										List<FormInstanceData> formInstanceList = new ArrayList<>();
										String[] values = property.getDefaultExpression().toString().split(ActivitiConstant.FORM_PROPERTY_ARRAY_KEY_SPAN);
										for (int i = 0; i < values.length; i++) {
											FormInstanceData formInstanceData=new FormInstanceData();
											formInstanceData.setId(values[i]);
											formInstanceList.add(formInstanceData);
										}
										formData.setDataList(formInstanceList);

									}
									formDataList.add(formData);
								}
							}

						}
						taskDefData.setJudgeList(judgeList);
						taskDefData.setFormDataList(formDataList);
						list.add(taskDefData);
					}
				}
				else if(e instanceof CallActivity)
				{
					CallActivity callActivity = (CallActivity)e;
					String taskid = callActivity.getId();
					if (taskid != null) {
						String[] names = taskid.split(ActivitiConstant.PROCESS_TASK_ID_SPAN);
						// 判断任务名是否规范，不规范的不加入任务列表
						if(!ActivitiConstant.CALLTASK_HEAD.equals(names[0]))
						{
							break;
						}
						TaskDefData taskDefData = new TaskDefData();
						taskDefData.setId(taskid);
						taskDefData.setName(callActivity.getName());
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

	@Override
	@Transactional
	public boolean complateTask(ComplateTask complateTaskinfo) throws WorkFlowException{
		String userId =complateTaskinfo.getUserId()==null?authInfoUtil.getUserInfo().getId():complateTaskinfo.getUserId();
		System.out.println("===========userId:"+userId);
		String userName=complateTaskinfo.getUserName();
		String deptName=complateTaskinfo.getDeptName();
		String opResult=complateTaskinfo.getOpResult();
		String memo=complateTaskinfo.getMemo();
		String activeTaskId=complateTaskinfo.getActiveTaskId();
		System.out.println("=======activeTaskId:"+activeTaskId);
		ActiveTask activeTask=activeTaskService.getActiveTaskById(activeTaskId);
		System.out.println("=======activeTask:"+activeTask);
		String region=complateTaskinfo.getRegion();
		List<String> unitIds =complateTaskinfo.getUnitIds();
		String taskId=complateTaskinfo.getTaskId();
		Map<String, String> values=complateTaskinfo.getValues();
		System.out.println("============values:"+values.toString());
		Map<String, List<String>> signUsers=complateTaskinfo.getSignUsers();
		Map<String, List<String>> claimUsers=complateTaskinfo.getClaimUsers();
		Map<String, List<String>> claimGroups=complateTaskinfo.getClaimGroups();
		Map<String, String> exValues=complateTaskinfo.getExValues();
		return complateTask(userId, userName, deptName, opResult, memo, activeTask, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, String region, List<String> unitIds, String taskId,
			Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(activeTask, "activeTask");
		logger.info("complateTask.start");
        logger.info("userId:"+userId);
        logger.info("region:"+region);
        if(unitIds!=null && unitIds.size()>0){
            logger.info("unitIds:"+unitIds.size());
            for (int i = 0; i < unitIds.size(); i++) {
                logger.info(unitIds.get(i).toString());
            }
        }
        logger.info("taskId:"+taskId);
        if(values!=null){
        	logger.info("values:"+values.toString());
        }else{
        	logger.info("values：null");
        }
        if(signUsers!=null && signUsers.size()>0){
            logger.info("signUsers:"+signUsers.size());
            for (int i = 0; i < signUsers.size(); i++) {
                logger.info(signUsers.get(i).toString());
            }
        }
        if(claimUsers!=null){
            logger.info("claimUsers:"+claimUsers.toString());
        }
        if(claimGroups!=null){
            logger.info("claimGroups:"+claimGroups.toString());
        }
        if(exValues!=null){
            logger.info("exValues:"+exValues.toString());
        }
		List<TaskData> taskList = doComplateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
		if(taskList!=null){
			logger.info("taskList:"+taskList.size());
		}
		Boolean bl=processTaskService.saveTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
		logger.info("processTaskService.saveTask:"+bl);
		logger.info("complateTask.end");
		return bl;
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String userName, String deptName, String opResult, String memo, ActiveTask activeTask, String region, List<String> unitIds, String taskId,
			Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues,String appellation) throws WorkFlowException {
		CheckDataUtil.checkNull(activeTask, "activeTask");
		List<TaskData> taskList = doComplateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
		return processTaskService.saveTask(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues,appellation);
	}

	@Override
	@Transactional
	public boolean complateTask(String userId, String activeTaskId, String region, List<String> unitIds, String taskId,
			Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(activeTaskId , "activeTaskId ");
		CheckDataUtil.checkNull(exValues, "exValues");
		List<TaskData> taskList = doComplateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
		return processTaskService.saveTask(userId, activeTaskId, taskList, exValues);
	}

	@Override
	@Transactional
	public List<TaskData> retrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(taskId, "taskId");
		return doRetrieveTask(userId, taskId, exValues);
	}

    private List<TaskData> doRetrieveTask(String userId, String taskId, Map<String, String> exValues) throws WorkFlowException {

        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new WorkFlowException("60501", "任务不存在");
        }

        if (!task.getAssignee().equals(userId)) {
            throw new WorkFlowException("60501", "只有任务完成者才能取回任务");
            // 获取和检测是否有替换者信息
//            List<String> replaceUsers = getReplaceUsers(userId, task.getProcessDefinitionId());
//            if (replaceUsers == null || replaceUsers.indexOf(task.getAssignee()) < 0) {
//                throw new WorkFlowException("60501", "只有任务完成者才能取回任务");
//            }
        }

        HistoricVariableInstance value = historyService.createHistoricVariableInstanceQuery().taskId(task.getId())
                .variableNameLike(ActivitiConstant.RETRIEVE_KEY + "%").singleResult();
        if (value == null) {
            throw new WorkFlowException("60501", "该任务不可取回");
        }
        String[] values = value.getValue().toString().split(ActivitiConstant.RETRIEVE_PROPERTY_VALUE_SPAN);
        if(values.length < 2)
        {
            throw new WorkFlowException("60501", "取回配置参数出错，请联系管理员 value:" + value.getValue().toString());
        }
        // 获取和发送信号
        String signalName = values[1];
        // 先查找当前流程
        Execution jumpexecution = runtimeService.createExecutionQuery().signalEventSubscriptionName(signalName)
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        if (jumpexecution == null) {
            // 查不到再查子流程
            jumpexecution = runtimeService.createExecutionQuery().signalEventSubscriptionName(signalName)
                    .rootProcessInstanceId(task.getProcessInstanceId()).singleResult();
        }
        if (jumpexecution == null) {
            throw new WorkFlowException("60501", "已完成的任务无法取回");
        }
        // 查找主流程实例id
        String rootProcessProcId = task.getProcessInstanceId();
        if(!isMainProcess(task.getProcessDefinitionId()))
        {
            // 如果是子流程，查找主流程定义id
            Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            if(execution != null && execution.getRootProcessInstanceId() != null)
            {
                Execution rootexecution = runtimeService.createExecutionQuery().parentId(execution.getRootProcessInstanceId()).singleResult();
                if (rootexecution != null)
                {
                    rootProcessProcId = rootexecution.getRootProcessInstanceId();
                }
            }
        }

        if(values.length == 2)
        {
            runtimeService.signalEventReceived(signalName, jumpexecution.getId());
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
        return getProcessInstActiveTaskList(rootProcessProcId, true);
    }
	@Override
	@Transactional
	public boolean retrieveTask(String userId, String activeTaskId, String taskId, Map<String, String> exValues)
			throws WorkFlowException {
		CheckDataUtil.checkNull(activeTaskId, "activeTaskId");
		CheckDataUtil.checkNull(exValues, "exValues");
		List<TaskData> taskList = doRetrieveTask(userId, taskId, exValues);
		return processTaskService.retrieveTask(userId, activeTaskId, taskList, exValues);
	}

	@Override
	@Transactional
	public List<TaskData> jumpTask(String startTaskId, String targetTaskDefId) throws WorkFlowException {
		CheckDataUtil.checkNull(startTaskId, "startTaskId");
		CheckDataUtil.checkNull(targetTaskDefId, "targetTaskDefId");

		Task startTask = taskService.createTaskQuery().active().taskId(startTaskId).singleResult();
		if(startTask == null)
		{
			throw new WorkFlowException("任务不存在或已经结束，无法跳转");
		}
		return doJumpTask(startTask, targetTaskDefId);
	}

	@Override
	@Transactional
	public boolean jumpTask(String userId, String startTaskId, String targetTaskDefId, String activeTaskId, Map<String, String> values,
			Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(activeTaskId, "activeTaskId");
		CheckDataUtil.checkNull(exValues, "exValues");
		if(CheckDataUtil.isNotNull(values))
		{
			taskService.setVariables(startTaskId, values);
		}
		List<TaskData> taskList = jumpTask(startTaskId, targetTaskDefId);
		return processTaskService.saveTask(userId, activeTaskId, taskList, exValues);
	}

	@Override
	@Transactional
	public List<TaskData> jumpTaskByProInst(String processInstId, String targetTaskDefId) throws WorkFlowException {
		CheckDataUtil.checkNull(processInstId, "startTaskId");
		CheckDataUtil.checkNull(targetTaskDefId, "targetTaskDefId");

		Task startTask = taskService.createTaskQuery().active().processInstanceId(processInstId).singleResult();
		if(startTask == null)
		{
			throw new WorkFlowException("任务不存在或已经结束，无法跳转");
		}
		return doJumpTask(startTask, targetTaskDefId);
	}

	@Override
	@Transactional
	public boolean jumpTaskByProInst(String userId, String processInstId, String targetTaskDefId, String activeTaskId,
			Map<String, String> values, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(activeTaskId, "activeTaskId");
		/*CheckDataUtil.checkNull(exValues, "exValues");*/
		Task startTask = taskService.createTaskQuery().active().processInstanceId(processInstId).singleResult();
		if(startTask == null)
		{
			throw new WorkFlowException("任务不存在或已经结束，无法跳转");
		}
		if(CheckDataUtil.isNotNull(values))
		{
			taskService.setVariables(startTask.getId(), values);
		}
		List<TaskData> taskList = doJumpTask(startTask, targetTaskDefId);
		return processTaskService.saveTask(userId, activeTaskId, taskList, exValues);
	}

	/**
	* @Title: doJumpTask
	* @Description: 进行跳转任务，并返回最新任务列表
	* @param startTask
	* @param targetTaskDefId
	* @return  参数说明
	* @return List<TaskData>    返回类型
	 * @throws WorkFlowException
	*
	*/
	private List<TaskData> doJumpTask(Task startTask, String targetTaskDefId) throws WorkFlowException
	{
		jump(startTask, targetTaskDefId);
		// 查找主流程实例id
		String rootProcessProcId = startTask.getProcessInstanceId();
		if(!isMainProcess(startTask.getProcessDefinitionId()))
		{
			// 如果是子流程，查找主流程定义id
			Execution execution = runtimeService.createExecutionQuery().executionId(startTask.getExecutionId()).singleResult();
			if(execution != null && execution.getRootProcessInstanceId() != null)
			{
				Execution rootexecution = runtimeService.createExecutionQuery().parentId(execution.getRootProcessInstanceId()).singleResult();
				if (rootexecution != null)
				{
					rootProcessProcId = rootexecution.getRootProcessInstanceId();
				}
			}
		}
		return getProcessInstActiveTaskList(rootProcessProcId, true);
	}

	@Override
	@Transactional
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
	@Transactional
	public boolean setTaskAssignee(String userId, String taskId, String activeTaskId, Map<String, String> exValues)
			throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "activeTaskId");
		CheckDataUtil.checkNull(taskId, "exValues");
		setTaskAssignee(userId, taskId);
		return processTaskService.setTaskAssignee(userId, activeTaskId, exValues);
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

	/**
	 * 初始化流程参数
	 *
	 * @param pi
	 */
	private void initProcessParams(ProcessInstance pi) {
		Task task = null;
		try {
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
			if(tasks.size() > 0)
			{
				task = tasks.get(0);
			}
		} catch (ActivitiException e) {
			return;
		}
		if (task == null) {
			return;
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
						List<String> userlist = Arrays.asList(users);
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
						List<String> userlist = Arrays.asList(users);
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
						List<String> jobList = Arrays.asList(jobs);
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
		taskData.setDueDate(task.getDueDate());
		taskData.setStateType(stateType);

		// taskData.setProcessDefinitionName(getProcessDefName(task.getProcessDefinitionId()));
		// taskData.setProcessInstBusinessKey(getProcessInstBusinessKey(task.getProcessInstanceId()));

		// 赋值自定义变量，表单，会签，URL数据
		List<FormData> formDataList = new ArrayList<FormData>();
		List<ProcessProperty> propertyList = new ArrayList<ProcessProperty>();
		List<SignUsersData> signUsersList = new ArrayList<SignUsersData>();
		List<ClaimUsersData> claimUsersList = new ArrayList<ClaimUsersData>();
		List<ClaimGroupData> claimGroupList = new ArrayList<ClaimGroupData>();
		List<JudgeProperty> judgeList = new ArrayList<JudgeProperty>();
		List<String> retrieveTasks = new ArrayList<String>();
		List<String> assignessList = new ArrayList<String>();
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


				/*System.out.println("fvalue.getName()");
				System.out.println(propertyTypes[1]);
				System.out.println(fvalue.getName());
				System.out.println(fvalue.isReadable());
				System.out.println(fvalue.isWritable());
				System.out.println(fvalue.isRequired());
				System.out.println("fvalue.isRequired()");*/

				form.setTitle(fvalue.getName());
				form.setReadable(fvalue.isReadable());
				form.setWritable(fvalue.isWritable());
				form.setRequired(fvalue.isRequired());

				/*System.out.println(fvalue.getValue().toString());*/
				if (fvalue.getValue() != null) {
					/*List<FormInstanceData> formInstanceList = JSONObject.parseArray(fvalue.getValue(),
							FormInstanceData.class);*/
					List<FormInstanceData> formInstanceList = new ArrayList<>();
					String[] values = fvalue.getValue().toString().split(ActivitiConstant.FORM_PROPERTY_ARRAY_KEY_SPAN);
					for (int i = 0; i < values.length; i++) {
						FormInstanceData formInstanceData=new FormInstanceData();
						formInstanceData.setId(values[i]);
						formInstanceList.add(formInstanceData);
					}
					form.setDataList(formInstanceList);
				}

				formDataList.add(form);
			}
				break;
			case ActivitiConstant.RETRIEVE_KEY: // 取回信息
			{
				String[] values = fvalue.getValue().toString().split(ActivitiConstant.RETRIEVE_PROPERTY_VALUE_SPAN);
				String[] tasks = values[0].split(ActivitiConstant.FORM_PROPERTY_ARRAY_KEY_SPAN);
				retrieveTasks = Arrays.asList(tasks);
				//retrieveTasks.addAll(Arrays.asList(tasks));//修复 多个变量多个取回任务 chen
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
				if(users != null)
				{
					signUsers.setUsers((List<String>) users);
				}
				else
				{
					signUsers.setUsers(new ArrayList<String>());
				}

				if (defusersKey != null) {
					// 查询会签用户定义数据
					Object defusers = taskService.getVariable(task.getId(), defusersKey);
					// 赋值会签用户定义列表
					if(defusers != null)
					{
						String[] useridlist = defusers.toString().split(",");
						signUsers.setDefUsers(new ArrayList<String>(Arrays.asList(useridlist)));
					}
					else
					{
						signUsers.setDefUsers(new ArrayList<String>());
					}

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
				if (users != null) {
					claimUsers.setUsers((List<String>) users);
				}
				else
				{
					claimUsers.setUsers(new ArrayList<String>());
				}

				if (defusersKey != null) {
					// 查询会签用户定义数据
					Object defusers = taskService.getVariable(task.getId(), defusersKey);
					if (defusers != null)
					{
						String[] useridlist = defusers.toString().split(",");
						claimUsers.setDefUsers(new ArrayList<String>(Arrays.asList(useridlist)));
					}
					else
					{
						claimUsers.setDefUsers(new ArrayList<String>());
					}
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
			case ActivitiConstant.TASK_JUDGE_HEAD: // 判断型变量--暂时兼容在自定变量中
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
			case ActivitiConstant.CURRENT_TASK_CLAIM_USERS_FORM_ID: // 当前任务候选人列表信息
			{
				String[] keys = fvalue.getValue().split(ActivitiConstant.FORM_PROPERTY_ARRAY_KEY_SPAN);
				if(keys.length > 0)
				{
					for(String key : keys)
					{
						// 查询候选用户数据
						Object users = taskService.getVariable(task.getId(), key);
						// 赋值候选用户列表
						if (users != null) {
							assignessList.addAll(((List<String>) users));
						}
					}
				}
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
		taskData.setRetrieveTasks(retrieveTasks);
		taskData.setAssignessList(assignessList);

		// 如果是在子流程中，则查询父流程信息
		if(!isMainProcess(task.getProcessDefinitionId()))
		{
			Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
			if(execution != null && execution.getRootProcessInstanceId() != null)
			{
				List<Execution> rootexecutions = runtimeService.createExecutionQuery().parentId(execution.getRootProcessInstanceId()).list();
				Execution rootexecution = null;
				if(CheckDataUtil.isNotNull(rootexecutions))
				{
					rootexecution = rootexecutions.get(0);
				}
				if(rootexecution != null)
				{
					taskData.setRootProcessInstanceId(rootexecution.getRootProcessInstanceId());
					taskData.setParentTaskDefId(rootexecution.getActivityId());
				}
			}

		}

		// 赋值发起人
		//taskData.setStarter(String.valueOf(taskService.getVariable(task.getId(), ActivitiConstant.STARTER_KEY)));

		// 读取本地变量
//		Map<String, String> exValues = new HashMap<String, String>();
//		List<HistoricVariableInstance> values = historyService.createHistoricVariableInstanceQuery()
//				.taskId(task.getId()).list();
//		if (values != null && !values.isEmpty()) {
//			for (HistoricVariableInstance value : values) {
//				String id = value.getVariableName();
//				String[] propertyTypes = id.split(ActivitiConstant.LOCAL_PROPERTY_KEY_SPAN);
//				String propertyType = propertyTypes[0];
//				switch (propertyType) {
//				case ActivitiConstant.BUSINESS_PROPERTY_KEY: // 业务自定义变量
//				{
//					exValues.put(propertyTypes[1], value.getValue().toString());
//				}
//					break;
//				default: // 其余参数不做处理
//					break;
//				}
//			}
//		}
//		taskData.setExValues(exValues);
		if(taskData.getFormDataList()!=null && taskData.getFormDataList().size()>0){
			System.out.println("===========taskData.getFormDataList().size():"+taskData.getFormDataList().size());
			for (int i = 0; i < taskData.getFormDataList().size(); i++) {
				FormData formData=taskData.getFormDataList().get(i);
				System.out.println(formData.getId());
			}
		}

		return taskData;
	}

	/**
	* @Title: isMainProcess
	* @Description: 是否是主流程
	* @param processDefId
	* @return  参数说明
	* @return boolean    返回类型
	*
	*/
	private boolean isMainProcess(String processDefId)
	{
		return processDefId.indexOf(ActivitiConstant.MAIN_PROCESS_KEY_HEAD) == 0;
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
	* @Title: findProcessInst
	* @Description: 查询流程实例
	* @param processInstanceId
	* @return  参数说明
	* @return ProcessInstance    返回类型
	*
	*/
	private ProcessInstance findProcessInst(String processInstanceId)
	{
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		return processInstance;
	}

	/**
	* @Title: findProcessInstByhistory
	* @Description: 查询流程实例根据历史记录
	* @param processInstanceId
	* @return  参数说明
	* @return HistoricProcessInstance    返回类型
	*
	*/
	private HistoricProcessInstance findProcessInstByhistory(String processInstanceId)
	{
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		return historicProcessInstance;
	}

	/**
	* @Title: getProcessInstActiveTaskList
	* @Description: 获取流程实例进行中任务列表
	* @param processInstId
	* @return  参数说明
	* @return List<TaskData>    返回类型
	*
	*/
	private List<TaskData> getProcessInstActiveTaskList(String processInstId, boolean isMain)
	{
		List<TaskData> list = new ArrayList<TaskData>();
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstId).list();
		for(Task task : tasks)
		{
			list.add(makeTaskData(task, 1));
		}
		// 如果是主流程，则再尝试查询子流程
		if(isMain)
		{
			List<ProcessInstance> subList = getSubProcessInstanceByParent(processInstId);
			for(ProcessInstance subInst : subList)
			{
				list.addAll(getProcessInstActiveTaskList(subInst.getProcessInstanceId(), false));
			}
		}
		return list;
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
		System.out.println("===========taskId:"+taskId);
		System.out.println("===========variables:"+variables.toString());
		taskService.setVariablesLocal(taskId, variables);
		return;
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
	 * @Title: getProcessKeyById
	 * @Description: 根据流程定义ID获取流程定义key
	 * @param processDefId
	 * @return 参数说明
	 * @return String 返回类型
	 *
	 */
	private String getProcessKeyById(String processDefId) {
		return processDefId.substring(0, processDefId.indexOf(":"));
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
		}catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return info;
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

	@Override
	@Transactional
	public List<TaskData> startProcessInstanceById(String userId, String processId, Map<String, Object> values,
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
		boolean isaddtask = true;
		try {
			if (values == null) {
				pi = runtimeService.startProcessInstanceById(processId, businessKey);
			} else {
				pi = runtimeService.startProcessInstanceById(processId, businessKey, values);
			}

		} catch (ActivitiObjectNotFoundException e) {
			isaddtask = false;
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "流程不存在");
		} catch(Exception e) {
			isaddtask = false;
			logger.error(e.getMessage());
			throw new WorkFlowException("60501", "任务启动失败");
		}
		/*新增校验，启动失败时不做其他操作*/
		if(pi == null || isaddtask==false){
			logger.info("----workflowService.startProcessInstanceById-----工作流启动失败--------");
			return null;
		}
		initProcessParams(pi);
		return getProcessInstActiveTaskList(pi.getId(), true);
	}

	@Override
	@Transactional
	public List<TaskData> startProcessInstanceByKey(String userId, String processKey, Map<String, Object> values,
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

		initProcessParams(pi);
		return getProcessInstActiveTaskList(pi.getId(), true);
	}

	@Override
	@Transactional
	public List<TaskData> complateTask(String userId, String region, List<String> unitIds, String taskId,
			Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(userId, "userId");
		CheckDataUtil.checkNull(taskId, "taskId");
		return doComplateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
	}

	private List<TaskData> doComplateTask(String userId, String region, List<String> unitIds, String taskId,
			Map<String, String> values, Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		System.out.println("===========123userId:"+userId);
		// 查询任务
		Task task = findTask(taskId);
		if (task == null) {
			logger.info("findTask=null");
			throw new WorkFlowException("60501", "任务不存在findTask");
		}

		if (signUsers != null) {
			taskService.setVariables(taskId, signUsers);
		}
		if (claimUsers != null) {
			taskService.setVariables(taskId, claimUsers);
		}
//		if (exValues != null) {
//			saveTaskExValues(taskId, exValues);
//		}
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
					if (claimGroups == null || !claimGroups.containsKey(paramkey)) {
						// 如果是无角色保持任务，允许不给参数，跳过此判断
						if(defkey == null)
							break;
						throw new WorkFlowException("60504", "该任务需要候选组参数" + paramkey);
					}
					// 循环值
					List<String> cglist = claimGroups.get(paramkey);
					if (cglist == null || cglist.isEmpty()) {
						// 如果是无角色保持任务，允许不给参数，跳过此判断
						if(defkey == null)
							break;
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
					if (claimGroups == null || !claimGroups.containsKey(paramkey)) {
						break;
						// throw new WorkFlowException("60504", "该任务需要候选组参数" + paramkey);
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
		System.out.println("=============userId:"+userId);
		System.out.println("=============userInfo.getId():"+userInfo.getId());
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
			throw new WorkFlowException("60501", "赋值任务操作者失败");
		}

		// 查找主流程实例id
		String rootProcessProcId = task.getProcessInstanceId();
		if(!isMainProcess(task.getProcessDefinitionId()))
		{
			// 如果是子流程，查找主流程定义id
			Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
			if(execution != null && execution.getRootProcessInstanceId() != null)
			{
				Execution rootexecution = runtimeService.createExecutionQuery().parentId(execution.getRootProcessInstanceId()).singleResult();
				if (rootexecution != null)
				{
					rootProcessProcId = rootexecution.getRootProcessInstanceId();
				}
			}
		}
		//用于判断是否需要回退 ，完成任务成功-不回退，失败-回退
		boolean isreturnUsertaskfinished=false;
		try {
			logger.info("doComplateTask.taskId:"+taskId);
	        taskService.complete(taskId);
	    } catch (ActivitiObjectNotFoundException e) {
	        logger.error(e.toString());
			isreturnUsertaskfinished=true;
	        throw new WorkFlowException("60501", "任务不存在");
	    } catch (ActivitiException e) {
	        logger.error(e.toString());
			isreturnUsertaskfinished=true;
			throw new WorkFlowException("60501", "任务已锁定或者缺少必要参数");
		} catch (Exception e) {
			logger.error(e.toString());
			isreturnUsertaskfinished=true;
			throw new WorkFlowException("60501", "任务执行失败");
		}
		logger.info("taskService.complete 执行完毕");
		//失败时回退
		if(isreturnUsertaskfinished){
			logger.info("-----return usertaskfinished complete failure : "+taskId);
			userTaskFinishedService.remove(taskId);
		}
		List<TaskData> taskList = getProcessInstActiveTaskList(rootProcessProcId, true);
		boolean hasLoop = false;
		// 判断是否有循环任务
		for(TaskData taskdata : taskList)
		{
			if(taskdata.getTaskDefId().indexOf(ActivitiConstant.TEMPTASK_HEAD) > -1)
			{
				// 只有是特殊任务才做判断
				// 检查是否存在循环任务的变量
				for(ProcessProperty ProcessProperty : taskdata.getPropertyList())
				{
					if(ActivitiConstant.CLAIM_LOOP_TASK_FORM_ID_HEAD.equals(ProcessProperty.getId()))
					{
						// 是循环任务，查询参数
						String[] params = ProcessProperty.getValue().split(ActivitiConstant.RETRIEVE_PROPERTY_VALUE_SPAN);
						// 验证参数长度
						if(params.length != 4)
						{
							// 参数长度不正确，不做处理，并打印异常
							logger.error("循环任务参数错误，任务id：" + taskdata.getId() + "  任务定义id:" + taskdata.getTaskDefId());
							break;
						}
						String userKey = params[0];
						String taskUserKey = params[1];
						String loopCountKey = params[2];
						String loopEndKey = params[3];
						startLoopTask(taskdata, userKey, taskUserKey, loopCountKey, loopEndKey);
						hasLoop = true;
						break;
					}
				}
				if(!hasLoop)
			    {
			    	// 不是循环任务的临时任务
			    	taskService.complete(taskdata.getId());
			    	hasLoop = true;
			    }

			}
		}
		logger.info("hasLoop:"+hasLoop);
		if(hasLoop)
		{
			taskList = getProcessInstActiveTaskList(rootProcessProcId, true);
		}
		return taskList;
	}

	/**
	* @Title: startLoopTask
	* @Description: 开始循环任务
	* @param taskdata
	* @param userKey
	* @param taskUserKey
	* @param loopCountKey
	* @param loopEndKey
	* @return  参数说明
	* @return List<TaskData>    返回类型
	*
	*/
	@SuppressWarnings({ "unchecked" })
	private void startLoopTask(TaskData taskdata, String userKey, String taskUserKey, String loopCountKey,
			String loopEndKey) {
		// 读取值
		Object users = taskService.getVariable(taskdata.getId(), userKey);
		if (users == null) {
			return;
		}
		List<String> useridlist = (List<String>) users;
		if(useridlist.isEmpty())
		{
			return;
		}
		int count = useridlist.size();
		taskService.setVariable(taskdata.getId(), loopCountKey, count);
		int index = 0;
		String taskId = taskdata.getId();
		for(String userid : useridlist)
		{
			++index;
			boolean isEnd = index >= count;
			checkLoopTask(taskId, userid, taskUserKey, loopEndKey, isEnd);
			if(isEnd)
			{
				return;
			}
			// 循环完成任务后，重新读取循环任务信息
			Task task = taskService // 与正在执行的任务管理相关的Service
					.createTaskQuery() // 创建任务查询对象
					.processInstanceId(taskdata.getProcessInstanceId()) // 流程实例id
					.taskDefinitionKey(taskdata.getTaskDefId()) // 查询候选组的任务
					.singleResult();
			if(task == null)
			{
				logger.error("完成循环任务失败: " + taskdata.getTaskDefId());
				return;
			}
			taskId = task.getId();
		}
	}

	/**
	* @Title: checkLoopTask
	* @Description: 监测并新建循环任务
	* @param taskId
	* @param userId
	* @param taskUserKey
	* @param loopEndKey
	* @return  参数说明
	* @return boolean    返回类型
	*
	*/
	private void checkLoopTask(String taskId, String userId, String taskUserKey, String loopEndKey, boolean isEnd)
	{

		Map<String, String> params = new HashMap<String, String>();
		params.put(taskUserKey, userId);
		params.put(loopEndKey, isEnd ? ActivitiConstant.LOOP_END_VALUE : ActivitiConstant.LOOP_CONTINUE_VALUE);

		taskService.setVariables(taskId, params);
		try {
			taskService.complete(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			logger.error("完成循环任务失败: " + e.getMessage());
		} catch (ActivitiException e) {
			logger.error("完成循环任务失败: " + e.getMessage());
		}
	}

	@Override
	public boolean complateTask_forrws(String userId, String userName, String deptName, String opResult, String memo,
			ActiveTask activeTask, String region, List<String> unitIds, String taskId, Map<String, String> values,
			Map<String, List<String>> signUsers, Map<String, List<String>> claimUsers,
			Map<String, List<String>> claimGroups, Map<String, String> exValues) throws WorkFlowException {
		CheckDataUtil.checkNull(activeTask, "activeTask");
		List<TaskData> taskList = doComplateTask(userId, region, unitIds, taskId, values, signUsers, claimUsers, claimGroups, exValues);
		return processTaskService.saveTask_forrws(userId, userName, deptName, opResult, memo, activeTask, taskList, exValues);
	}
}
