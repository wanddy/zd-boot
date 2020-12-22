package workflow.business.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.UserTaskFinishedEntity;

/**
 * 用户完成记录表
 */
public interface UserTaskFinishedService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<UserTaskFinishedEntity> listStcsmUserTaskFinished(Map<String, Object> params);

    /**
     * 新增
     * @param stcsmUserTaskFinished
     * @return 添加成功条数
     */
	int saveStcsmUserTaskFinished(UserTaskFinishedEntity stcsmUserTaskFinished);

    /**
     * 根据id查询
     * @param id
     * @return
     */
	UserTaskFinishedEntity getStcsmUserTaskFinishedById(Long id);

	/**
	 * 根据id查询
	 * @param taskId
	 * @return
	 */
	UserTaskFinishedEntity getStcsmUserTaskFinishedByTaskId(String taskId);
    /**
     * 修改
     * @param stcsmUserTaskFinished
     * @return 更新成功条数
     */
	int updateStcsmUserTaskFinished(UserTaskFinishedEntity stcsmUserTaskFinished);

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	int batchRemove(Long[] id);
	/**
	 * 删除
	 * @param taskId
	 * @return 删除成功条数
	 */
	int remove(String taskId);

	/** 
	* @Title: simpleList 
	* @Description: 获取简单数据列表
	* @return  参数说明 
	* @return List<UserTaskFinishedEntity>    返回类型 
	* 
	*/
	public List<UserTaskFinishedEntity> simpleList();
	
}
