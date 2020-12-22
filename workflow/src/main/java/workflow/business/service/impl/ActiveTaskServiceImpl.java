package workflow.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.mybatis.spring.annotation.MapperScan;
import workflow.business.model.TaskData;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.olddata.model.PageInput;
import workflow.olddata.util.IDGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import workflow.business.mapper.ActiveTaskMapper;
import workflow.business.model.ActiveTask;
import workflow.business.model.TaskContentData;
import workflow.business.model.TotalTasks;
import workflow.business.service.ActiveTaskService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service("ActiveTaskService")
@DS("master")
@MapperScan(basePackages = {"workflow.business.mapper"})
public class ActiveTaskServiceImpl implements ActiveTaskService {

	@Autowired
	private ActiveTaskMapper stcsmActiveTaskMapper;

	@Override
	public Page<ActiveTask> pageList(ActiveTask params, PageInput page) {
		QueryWrapper<ActiveTask> query = new QueryWrapper<ActiveTask>();
		if(params != null){
			if(params.getCategoryType() != null){
				query.eq("category_type", params.getCategoryType());
			}
			if(params.getAssignee() != null){
				query.eq("assignee", params.getAssignee());
			}
			/*if(params.getDeptCode() != null){
				query.eq("deptCode", params.getDeptCode());
			}*/
			if(params.getAccessScopeList() != null){
				query.in("access_scope",params.getAccessScopeList());
			}
			if(params.getParentTaskDefId() != null){
				query.eq("parent_task_def_id", params.getParentTaskDefId());
			}
			if(params.getTaskDefId() != null){
				query.eq("task_def_id", params.getTaskDefId());
			}
			if(params.getTaskNo() != null){
				query.eq("task_no", params.getTaskNo());
			}
			if(params.getProcessInstanceId() != null){
				query.eq("process_instance_id", params.getProcessInstanceId());
			}
			query.orderByDesc("created_at");
			Page<ActiveTask> pageInfo = new Page<ActiveTask>();
			if(page != null) {
				pageInfo = new Page<ActiveTask>(page.getPageNum(), page.getPageSize());
			}
			stcsmActiveTaskMapper.selectPage(pageInfo, query);
			return pageInfo;
		}
		return null;
	}

	@Override
	public List<ActiveTask> listAll(ActiveTask params) {
		QueryWrapper<ActiveTask> query = new QueryWrapper<ActiveTask>();
		if(params != null){
			System.out.println("==================params！=null");
			if(params.getAssignee() != null){
				query.eq("assignee", params.getAssignee());
			}
			if(params.getContentId() != null){
				query.eq("content_id", params.getContentId());
			}
			if(params.getProcessInstanceId() != null){
				query.eq("process_instance_id", params.getProcessInstanceId());
			}
			if(params.getTaskId() != null){
				query.eq("task_id", params.getTaskId());
			}
			if(params.getRootProcessInstanceId() != null){
				query.eq("root_process_instance_id", params.getRootProcessInstanceId());
			}
			query.orderByDesc("created_at");
			Page<ActiveTask> pageInfo = new Page<ActiveTask>(1, Integer.MAX_VALUE);
			Page<ActiveTask> pageList = stcsmActiveTaskMapper.selectPage(pageInfo, query);
			return  pageList.getRecords();
		}
		return null;
	}

	@Override
	public ActiveTask getActiveTaskById(String id) {
		return stcsmActiveTaskMapper.getActiveTaskById(id);
	}

	@Override
	public int batchSave(String processName, String initiator, String previousOpResultDesc, List<TaskData> list, TaskContentData data) {
		List<ActiveTask> taskList = new ArrayList<ActiveTask>();
		ActiveTask task = null;
		if(list != null && list.size() > 0) {
			String batch = String.valueOf(System.currentTimeMillis());
			for (TaskData taskData : list) {
				// 工作流待办任务信息
				taskData.setProcessDefinitionName(processName);
				taskData.setStarter(initiator);
				task = taskDataToActiveTask(taskData);
				// 其他信息
				task.setPreviousOpResultDesc(previousOpResultDesc);
				task.setCategoryType(data.getCategoryType());
				task.setAccessScope(data.getAccessScope());
				task.setContentId(data.getContentId());
				task.setContentName(data.getContentName());
				task.setTaskNo(data.getTaskNo());
				task.setTaskType(data.getTaskType());
				task.setCreatedAt(System.currentTimeMillis());
				task.setTaskBatch(batch);

				System.out.println("=============getContentId:"+data.getContentId());
				System.out.println("=============getFormDataList:"+task.getFormDataList());
				System.out.println("=============getSmartFormPropertyList:"+task.getSmartFormPropertyList());
				taskList.add(task);
			}
		}
		int cpsas=stcsmActiveTaskMapper.batchSave(taskList);
		return cpsas;
	}
	@Override
	public int batchSave(String processName, String initiator, String previousOpResultDesc, List<TaskData> list, TaskContentData data, String task_batch) {
		List<ActiveTask> taskList = new ArrayList<ActiveTask>();
		ActiveTask task = null;
		if(list != null && list.size() > 0) {
			for (TaskData taskData : list) {
				// 工作流待办任务信息
				taskData.setProcessDefinitionName(processName);
				taskData.setStarter(initiator);
				task = taskDataToActiveTask(taskData);
				// 其他信息
				task.setPreviousOpResultDesc(previousOpResultDesc);
				task.setCategoryType(data.getCategoryType());
				task.setAccessScope(data.getAccessScope());
				task.setContentId(data.getContentId());
				task.setContentName(data.getContentName());
				task.setTaskNo(data.getTaskNo());
				task.setTaskType(data.getTaskType());
				task.setCreatedAt(System.currentTimeMillis());
				task.setTaskBatch(task_batch);
				taskList.add(task);
			}
		}
		return stcsmActiveTaskMapper.batchSave(taskList);
	}
	@Override
	public int batchSave(String processName, String initiator, String previousOpResultDesc, TaskData taskData, TaskContentData data) {
		List<ActiveTask> taskList = new ArrayList<ActiveTask>();
		ActiveTask task = null;
		String batch = String.valueOf(System.currentTimeMillis());
		// 工作流待办任务信息
		taskData.setProcessDefinitionName(processName);
		taskData.setStarter(initiator);
		task = taskDataToActiveTask(taskData);
		// 其他信息
		task.setPreviousOpResultDesc(previousOpResultDesc);
		task.setCategoryType(data.getCategoryType());
		task.setAccessScope(data.getAccessScope());
		task.setContentId(data.getContentId());
		task.setContentName(data.getContentName());
		task.setTaskNo(data.getTaskNo());
		task.setTaskType(data.getTaskType());
		task.setCreatedAt(System.currentTimeMillis());
		task.setTaskBatch(batch);
		taskList.add(task);
		return stcsmActiveTaskMapper.batchSave(taskList);
	}

	@Override
	public int batchSave(String processName, String initiator, String previousOpResultDesc, TaskData taskData, TaskContentData data, String task_batch) {
		List<ActiveTask> taskList = new ArrayList<ActiveTask>();
		ActiveTask task = null;
		// 工作流待办任务信息
		taskData.setProcessDefinitionName(processName);
		taskData.setStarter(initiator);
		task = taskDataToActiveTask(taskData);
		// 其他信息
		task.setPreviousOpResultDesc(previousOpResultDesc);
		task.setCategoryType(data.getCategoryType());
		task.setAccessScope(data.getAccessScope());
		task.setContentId(data.getContentId());
		task.setContentName(data.getContentName());
		task.setTaskNo(data.getTaskNo());
		task.setTaskType(data.getTaskType());
		task.setCreatedAt(System.currentTimeMillis());
		task.setTaskBatch(task_batch);
		taskList.add(task);
		return stcsmActiveTaskMapper.batchSave(taskList);
	}


	@Override
	public List<ActiveTask> listActiveTask(String taskId, String exceptActiveTaskId) {
		QueryWrapper<ActiveTask> query = new QueryWrapper<ActiveTask>();
		query.eq("task_id", taskId);
//		query.eq("exceptActiveTaskId", exceptActiveTaskId);
		query.ne("id",exceptActiveTaskId);
		query.orderByDesc("created_at");
		Page<ActiveTask> pageInfo = new Page<ActiveTask>(1, Integer.MAX_VALUE);
		Page<ActiveTask> pageList = stcsmActiveTaskMapper.selectPage(pageInfo, query);
		return pageList.getRecords();
	}

	@Override
	public int delete(String userId, String taskId) {
		return stcsmActiveTaskMapper.deleteActiveTask(userId, taskId);
	}

	@Override
	public int deleteById(String id) {
		String[] ids = {id};
		return stcsmActiveTaskMapper.batchRemove(ids);
	}

	@Override
	public ActiveTask taskDataToActiveTask(TaskData taskData) {
		ActiveTask task = new ActiveTask();
		// 工作流待办任务信息
		task.setId(IDGeneratorUtil.generatorId());
		task.setTaskId(taskData.getId());
		task.setParentTaskDefId(taskData.getParentTaskDefId()); // 工作流返回 - 父任务id
		task.setTaskDefId(taskData.getTaskDefId());
		task.setTaskName(taskData.getName());
		task.setInitiator(taskData.getStarter()); // 任务发起人
		String processKey = "";
		if(!StringUtils.isEmpty(taskData.getProcessDefinitionId())) {
			String[] arr = taskData.getProcessDefinitionId().split(":");
			processKey = arr[0];
		}
		task.setProcessKey(processKey); // 流程key
		task.setProcessDefinitionId(taskData.getProcessDefinitionId()); // 流程定义id
		task.setProcessDefinitionName(taskData.getProcessDefinitionName()); // 流程定义名称
		task.setRootProcessInstanceId(taskData.getRootProcessInstanceId()); // 父流程实例id
		task.setProcessInstanceId(taskData.getProcessInstanceId()); // 流程实例id
		task.setAssignee(taskData.getAssigness()); // 被分配任务的人/部门
		if(taskData.getJudgeList() != null) {
			task.setJudgeList(JSONObject.toJSONString(taskData.getJudgeList()));
		}
		if(taskData.getPropertyList() != null) {
			task.setPropertyList(JSONObject.toJSONString(taskData.getPropertyList()));
		}
		if(taskData.getSignUsersList() != null) {
			task.setSignUsersParams(JSONObject.toJSONString(taskData.getSignUsersList()));
		}
		if(taskData.getClaimUsersList() != null) {
			task.setClaimUsersParams(JSONObject.toJSONString(taskData.getClaimUsersList()));
		}
		if(taskData.getRetrieveTasks() != null) {
			task.setCanRetrieve(JSONObject.toJSONString(taskData.getRetrieveTasks()));
		}
		if(taskData.getClaimGroupList() != null) {
			task.setClaimGroupParams(JSONObject.toJSONString(taskData.getClaimGroupList()));
		}
		if(taskData.getFormDataList() != null) {
			task.setFormDataList(JSONObject.toJSONString(taskData.getFormDataList()));
		}
		return task;
	}

	@Override
	public List<TotalTasks> getTotalTasks(Query query) {
		return stcsmActiveTaskMapper.totalTasks(query);
	}

	@Override
	public int updateStatus(String processDefinitionId,
			String processInstanceId, String processTaskId, int status) {
//		System.out.println("processDefinitionId:"+processDefinitionId);
//		System.out.println("processInstanceId:"+processInstanceId);
//		System.out.println("processTaskId:"+processTaskId);
//		System.out.println("status:"+status);

		QueryWrapper<ActiveTask> query = new QueryWrapper<>();
		if(processDefinitionId!=null){
			System.out.println("processDefinitionId  ！= null");
			query.eq("process_definition_id", processDefinitionId);
//			updateWrapper.eq("processDefinitionId",processDefinitionId);
		}
		if(processInstanceId!=null){
			System.out.println("process_instanceId  ！= null");
			query.eq("process_instance_id", processInstanceId);
//			updateWrapper.eq("processInstanceId",processInstanceId);
		}
		if(processTaskId!=null){
			query.eq("task_id", processTaskId);
//			updateWrapper.eq("taskId",processTaskId);
		}
		// 终止流程操作时，删除任务
		if(2 == status) {
			return stcsmActiveTaskMapper.deleteByProcess(query);
		}
		// 其他操作更新状态
		//query.eq("status", status);
		ActiveTask activeTask=new ActiveTask();
		activeTask.setStatus(status);
//		updateWrapper.set("status",status);
		return stcsmActiveTaskMapper.updateStatus(query,status);
		//return stcsmActiveTaskMapper.update(activeTask,query);
	}

	@Override
	public int updateActiveTask(ActiveTask item) {
		return stcsmActiveTaskMapper.update(item);
	}

	@Override
	public List<ActiveTask> listActiveTaskByContentId(String contentId) {
		QueryWrapper<ActiveTask> query = new QueryWrapper<ActiveTask>();
		query.eq("content_id", contentId);
		query.orderByDesc("created_at");
		Page<ActiveTask> pageInfo = new Page<ActiveTask>(1, Integer.MAX_VALUE);
		Page<ActiveTask> pageList = stcsmActiveTaskMapper.selectPage(pageInfo, query);
		return pageList.getRecords();
	}

	@Override
	public List<ActiveTask> listActiveTaskBytaskbatch(String taskbatch) {
		QueryWrapper<ActiveTask> query = new QueryWrapper<ActiveTask>();
		query.eq("task_batch", taskbatch);
		query.orderByDesc("created_at");
		Page<ActiveTask> pageInfo = new Page<ActiveTask>(1, Integer.MAX_VALUE);
		Page<ActiveTask> pageList = stcsmActiveTaskMapper.selectPage(pageInfo, query);
		return pageList.getRecords();
	}

}
