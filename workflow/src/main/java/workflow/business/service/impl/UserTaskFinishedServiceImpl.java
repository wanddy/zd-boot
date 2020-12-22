package workflow.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.service.UserTaskFinishedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workflow.business.mapper.UserTaskFinishedMapper;
import workflow.business.model.entity.UserTaskFinishedEntity;
import workflow.olddata.annotation.LogOperation;
import workflow.olddata.annotation.SysLog;
/*import zdit.zdboot.workflow.framework.entity.Page;
import zdit.zdboot.workflow.framework.entity.Query;*/
import java.util.List;
import java.util.Map;
/**
 * 用户完成记录表
 */
@Service("userTaskFinishedService")
@DS("master")
public class UserTaskFinishedServiceImpl implements UserTaskFinishedService {

	@Autowired
	private UserTaskFinishedMapper stcsmUserTaskFinishedMapper;

    /**
     * 分页查询
     * @param params
     * @return
     */
	@Override
	public Page<UserTaskFinishedEntity> listStcsmUserTaskFinished(Map<String, Object> params) {
		QueryWrapper<UserTaskFinishedEntity> query = new QueryWrapper<UserTaskFinishedEntity>();
		if(params.get("user_id")!=null){
			query.eq("user_id",params.get("user_id"));
		}
		if(params.get("user_name")!=null){
			query.like("user_name",params.get("user_name"));
		}
		if(params.get("unit_id")!=null){
			query.eq("unit_id",params.get("unit_id"));
		}
		if(params.get("unit_name")!=null){
			query.like("unit_name",params.get("unit_name"));
		}
		if(params.get("process_id")!=null){
			query.eq("process_id",params.get("process_id"));
		}
		if(params.get("task_name")!=null){
			query.like("task_name",params.get("task_name"));
		}
		if(params.get("orderInfo")!=null){
//			System.out.println("orderInfo!=null");
			if(params.get("orderInfo").toString().toLowerCase().indexOf("finish_time")>-1){
//				System.out.println("finish_time!=null");
				if(params.get("orderInfo").toString().toLowerCase().indexOf("desc")>-1){
//					System.out.println("desc!=null");
					query.orderByDesc("finish_time");
				} else {
//					System.out.println("asc!=null");
					query.orderByAsc("finish_time");
				}
			}
		}

		Page<UserTaskFinishedEntity> page = new Page<UserTaskFinishedEntity>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
		// 设置分页数和页码
		Page<UserTaskFinishedEntity> list= stcsmUserTaskFinishedMapper.selectPage(page,query);
		return page;
	}

    /**
     * 新增
     * @param stcsmUserTaskFinished
     * @return 添加成功条数
     */
	@Override
	public int saveStcsmUserTaskFinished(UserTaskFinishedEntity stcsmUserTaskFinished) {
		int count = stcsmUserTaskFinishedMapper.save(stcsmUserTaskFinished);
		return count;
	}

    /**
     * 根据id查询
     * @param id
     * @return 返回查询到的实体
     */
	@Override
	public UserTaskFinishedEntity getStcsmUserTaskFinishedById(Long id) {
		UserTaskFinishedEntity stcsmUserTaskFinished = stcsmUserTaskFinishedMapper.getObjectById(id);
		return stcsmUserTaskFinished;
	}

	@Override
	public UserTaskFinishedEntity getStcsmUserTaskFinishedByTaskId(String taskId) {
		return stcsmUserTaskFinishedMapper.getObjectBytaskId(taskId);
	}

	/**
     * 修改
     * @param stcsmUserTaskFinished
     * @return 更新成功条数
     */
	@Override
	public int updateStcsmUserTaskFinished(UserTaskFinishedEntity stcsmUserTaskFinished) {
		int count = stcsmUserTaskFinishedMapper.update(stcsmUserTaskFinished);
		return count;
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@Override
	@SysLog(value="未发布流程删除", table="wf_wf_user_task_finished", operation=LogOperation.BatchDelete)
	public int batchRemove(Long[] id) {
		int count = stcsmUserTaskFinishedMapper.batchRemove(id);
		return count;
	}

	@Override
	public int remove(String taskId) {
		return stcsmUserTaskFinishedMapper.remove(taskId);
	}

	/**
	* @Title: simpleList
	* @Description: 获取简单数据列表
	* @return  参数说明
	* @return List<UserTaskFinishedEntity>    返回类型
	*
	*/
	@Override
	public List<UserTaskFinishedEntity> simpleList()
	{
		return stcsmUserTaskFinishedMapper.simpleList();
	}

}
