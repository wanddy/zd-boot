package workflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.TaskStatus;

import java.util.Map;

/**
 * 
 */
public interface TaskStatusService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<TaskStatus> listStcsmTaskStatus(Map<String, Object> params);

    /**
     * 新增
     * @param taskStatus
     * @return 添加成功条数
     */
	int saveStcsmTaskStatus(TaskStatus taskStatus);

    /**
     * 根据id查询
     * @param id
     * @return
     */
	TaskStatus getStcsmTaskStatusById(String id);

    /**
     * 修改
     * @param taskStatus
     * @return 更新成功条数
     */
	int updateStcsmTaskStatus(TaskStatus taskStatus);

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	int batchRemove(String[] id);
	
	/**
	 * 调用updateProcessInstToContent存储过程
	 * @param processInstanceId
	 * @param contentId
	 * @return
	 */
	int updateProcessInstToContent(String processInstanceId, String contentId);
}
