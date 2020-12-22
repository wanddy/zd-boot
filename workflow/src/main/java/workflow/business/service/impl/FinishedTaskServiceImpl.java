package workflow.business.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.olddata.model.PageInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import workflow.business.mapper.ActiveTaskMapper;
import workflow.business.mapper.FinishedTaskMapper;
import workflow.business.model.ActiveTask;
import workflow.business.model.FinishedTask;
import workflow.business.service.FinishedTaskService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service("finishedTaskService")
@DS("master")
public class FinishedTaskServiceImpl implements FinishedTaskService {


	@Autowired
	private FinishedTaskMapper finishedTaskMapper;
	@Autowired
	private ActiveTaskMapper activeTaskMapper;

	@Override
	public Page<FinishedTask> pageList(FinishedTask params, PageInput page) {
		QueryWrapper<FinishedTask> query = new QueryWrapper<FinishedTask>();
		if(params != null) {
			query.eq("categoryType", params.getCategoryType());
			query.eq("realAssignee", params.getRealAssignee());
			query.eq("realAssigneeName", params.getRealAssigneeName());
			query.eq("accessScopeList", params.getAccessScopeList());
			query.eq("parentTaskDefId", params.getParentTaskDefId());
			query.eq("taskDefId", params.getTaskDefId());
			query.eq("taskNo", params.getTaskNo());
			query.eq("opResult", params.getOpResult());
			query.eq("processInstanceId", params.getProcessInstanceId());
		}
		Page<FinishedTask> pageInfo = new Page<FinishedTask>();
		if(page != null) {
			pageInfo = new Page<FinishedTask>(page.getPageNum(), page.getPageSize());
		}
		finishedTaskMapper.listAll(pageInfo, query);
		return pageInfo;
	}

	@Override
	public List<FinishedTask> listAll(FinishedTask params) {
		QueryWrapper<FinishedTask> query = new QueryWrapper<FinishedTask>();
		if(params != null){
			if(params.getAssignee() != null){
				query.eq("assignee", params.getAssignee());
			}
			/*if(params.getAssigneeGroup() != null){
				query.eq("assigneeGroup", params.getAssigneeGroup());
			}*/
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
			Page<FinishedTask> pageInfo = new Page<FinishedTask>(1, Integer.MAX_VALUE);
			Page<FinishedTask> pageList = finishedTaskMapper.selectPage(pageInfo, query);
			return  pageList.getRecords();
		}
		return null;
	}

	@Override
	public int saveFinishedTask(String userId, String userName, String opResult, String opResultDesc, List<ActiveTask> taskList) {
		List<FinishedTask> list = new ArrayList<FinishedTask>();
		FinishedTask task = null;
		if(taskList != null && taskList.size() > 0) {
			for (ActiveTask taskData : taskList) {
				if(getFinishedTaskById(taskData.getId())!=null){
					continue;
				}else{
					String json = JSONObject.toJSONString(taskData);
					task = JSONObject.parseObject(json, FinishedTask.class);
					dealItemsIdByZJProcess(taskData.getId(), task);
					task.setRealAssignee(userId);
					task.setRealAssigneeName(userName);
					task.setOpResult(opResult);
					task.setOpResultDesc(opResultDesc);
					task.setStartTime(taskData.getCreatedAt());
					task.setFinishTime(System.currentTimeMillis());
					list.add(task);
				}
			}
		}
        if(list.size()<1){
            return 0;
        }
		return finishedTaskMapper.batchSave(list);
	}

	@Override
	public Integer countTaskByUser(String userId, List<String> businessTypeList) {
		QueryWrapper<FinishedTask> query = new QueryWrapper<FinishedTask>();
		query.eq("realAssignee", userId);
		query.eq("businessTypeList", businessTypeList);
		return finishedTaskMapper.countTaskByUser(query);
	}

	/**
	 * 2019年8月21日15:14:14
	 * 单位推荐 已办表不保存事项code，专家指派  需要从代办表中查出事项code保存到已办表中
	 * @param task
	 */
	private void dealItemsIdByZJProcess(String taskId, FinishedTask task) {
		if("单位推荐".equals(task.getTaskName())) {
			task.setCategoryType("");
		}
		if("专家指派".equals(task.getTaskName())) {
			if(StringUtils.isEmpty(task.getCategoryType())) {
				ActiveTask taskData = activeTaskMapper.getActiveTaskById(taskId);
				task.setCategoryType(taskData.getCategoryType());
			}
		}
	}

	@Override
	public FinishedTask getFinishedTaskById(String id) {
		return finishedTaskMapper.getObjectById(id);
	}
}
