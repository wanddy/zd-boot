package workflow.business.service.impl;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workflow.business.mapper.TaskStatusMapper;
import workflow.business.model.TaskStatus;
import workflow.business.service.TaskStatusService;

import java.util.Map;

/**
 *
 */
@Service("stcsmTaskStatusService")
@DS("master")
public class TaskStatusServiceImpl implements TaskStatusService {

	@Autowired
	private TaskStatusMapper taskStatusMapper;

    /**
     * 分页查询
     * @param params
     * @return
     */
	@Override
	public Page<TaskStatus> listStcsmTaskStatus(Map<String, Object> params) {
		QueryWrapper<TaskStatus> query = new QueryWrapper<TaskStatus>();
		query.orderByDesc("id");
		Page<TaskStatus> page = new Page<>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
//		stcsmTaskStatusMapper.listForPage(page, query);
		taskStatusMapper.selectPage(page,query);
		return page;
	}

    /**
     * 新增
     * @param stcsmTaskStatus
     * @return 添加成功条数
     */
	@Override
	public int saveStcsmTaskStatus(TaskStatus stcsmTaskStatus) {
		int count = taskStatusMapper.save(stcsmTaskStatus);
		return count;
	}

    /**
     * 根据id查询
     * @param id
     * @return 返回查询到的实体
     */
	@Override
	public TaskStatus getStcsmTaskStatusById(String id) {
		TaskStatus stcsmTaskStatus = taskStatusMapper.getObjectById(id);
		return stcsmTaskStatus;
	}

    /**
     * 修改
     * @param stcsmTaskStatus
     * @return 更新成功条数
     */
	@Override
	public int updateStcsmTaskStatus(TaskStatus stcsmTaskStatus) {
		int count = taskStatusMapper.update(stcsmTaskStatus);
		return count;
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@Override
	public int batchRemove(String[] id) {
		int count = taskStatusMapper.batchRemove(id);
		return count;
	}

	@Override
	public int updateProcessInstToContent(String processInstanceId, String contentId) {
		QueryWrapper<TaskStatus> query = new QueryWrapper<TaskStatus>();
		if(processInstanceId!=null){
			query.eq("processInstanceId", processInstanceId);
		}
		if(contentId!=null){
			query.eq("contentId", contentId);
		}
		query.eq("contentId", contentId);
		return taskStatusMapper.updateProcessInstToContent(query);
	}

}
