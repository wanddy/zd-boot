package workflow.business.service.impl;

import auth.discard.model.SysDepartTreeModel;
import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.stereotype.Service;
import workflow.business.service.ConsoleService;
import workflow.business.service.UserTaskFinishedService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.common.constant.ActivitiConstant;
import workflow.common.error.WorkFlowException;
import workflow.common.redis.JedisMgr_wf;
import workflow.common.utils.CheckDataUtil;
import workflow.business.model.*;
import workflow.business.model.entity.UserTaskFinishedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import workflow.business.service.UserTaskFinishedManager;

import java.util.*;
@Service("uerTaskFinishedManager")
@DS("master")
public class UserTaskFinishedManagerImpl implements UserTaskFinishedManager {

	@Autowired
	private JedisMgr_wf jedisMgrWf;
	
	@Autowired
	private UserTaskFinishedService userTaskFinishedService;

	@Autowired
	private ConsoleService consoleService;



	@Autowired
	private AuthInfo authInfoUtil;
	
	public UserTaskFinishedManagerImpl()
	{
		init();
	}
	
	/** 
	* @Title: init 
	* @Description: 初始化数据
	* @return void    返回类型 
	* 
	*/
	private void init()
	{
		
	}
	
	@Override
	public PageUserFinishedTaskList getUserFinishedTaskList(String userName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException {
		return consoleService.getUserFinishedTaskList(userName, pageNum, pageSize, orderByCount);
	}

	@Override
	public PageProcessTaskList getFinishedTasksByUser(String userId, String processDefId, String taskName, int pageNum,
			int pageSize, int orderByFinishedTime) throws WorkFlowException {
		return consoleService.getFinishedTasksByUser(userId, processDefId, taskName, pageNum, pageSize, orderByFinishedTime);
	}

	@Override
	public PageUnitFinishedTaskList getUnitFinishedTaskList(String unitName, int pageNum, int pageSize, int orderByCount) throws WorkFlowException {
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		//先判断redis数据是否再初始化中，如果是，则抛异常
		if(jedisMgrWf.hasKey(getLoadingKey()))
		{
			throw new WorkFlowException("正在刷新缓存，请稍后再试");
		}
		PageUnitFinishedTaskList pageList = new PageUnitFinishedTaskList();
		pageList.setPageNum(pageNum);
		pageList.setPageSize(pageSize);
		pageList.setTotal(0);
		String key = getUnitFinishedKey();
		//判断redis是否有数据
		if(!jedisMgrWf.hasKey(key))
		{
			//如果无数据，则返回空数据
			return pageList;
		}
		//判断有没有查询部门
		List<SysDepartTreeModel> infolist = authInfoUtil.getDeparts();
		if(CheckDataUtil.isNotNull(unitName))
		{
			// 根据部门名查找部门列表
			List<SysDepartTreeModel> page = null;
			if(infolist!=null && infolist.size()>0){
				for (SysDepartTreeModel md : infolist ){
					if(md.getDepartName().equals(unitName)){
						page.add(md);
					}
				}
			}
			if(page.size()<1){
				return pageList;
			}
			int index = -1;
			int firstResult = (pageNum - 1) * pageSize;
			int maxResults = firstResult + pageSize;
			List<UnitFinishedTaskData> rows = new ArrayList<UnitFinishedTaskData>();
			for(SysDepartTreeModel unit : page)
			{
				// 精确查询id和数量
				int score = jedisMgrWf.getSortSetScore(key, unit.getId()).intValue();
				if(score > 0)
				{

					if(index >= firstResult && index < maxResults)
					{
						//如果在查询范围内
						UnitFinishedTaskData unitFinishedTaskData = new UnitFinishedTaskData();
						unitFinishedTaskData.setUnitId(unit.getId());
						unitFinishedTaskData.setUnitName(unit.getDepartName());
						unitFinishedTaskData.setFinishedCount(score);
						rows.add(unitFinishedTaskData);
					}
					++index;
				}
			}
			pageList.setTotal(index);
			pageList.setRows(rows);
		}
		else
		{
			//从redis获取总数量
			int total = jedisMgrWf.getSortSetLength(key).intValue();
			if(total == 0)
			{
				return pageList;
			}
			pageList.setTotal(total);
			int offset = (pageNum - 1) * pageSize;
			int count = pageSize;
			//从redis拉取id列表
			List<UnitFinishedTaskData> rows = new ArrayList<UnitFinishedTaskData>();
			Set<TypedTuple<String>> list = jedisMgrWf.getSortSetRevListWithScores(key, offset, count);
			for(TypedTuple<String> tuple : list)
			{
				// 根据部门名查找部门列表
				List<SysDepartTreeModel> page = null;
				if(infolist!=null && infolist.size()>0){
					for (SysDepartTreeModel md : infolist ){
						if(md.getId().equals(tuple.getValue())){
							page.add(md);
						}
					}
				}
				UnitFinishedTaskData userFinishedTaskData = new UnitFinishedTaskData();
				userFinishedTaskData.setUnitId(tuple.getValue());
				userFinishedTaskData.setUnitName("");
				if(page.size()<1)
				{
					userFinishedTaskData.setUnitId(tuple.getValue());
					userFinishedTaskData.setUnitName("");
				}
				else
				{
					userFinishedTaskData.setUnitId(page.get(0).getId());
					userFinishedTaskData.setUnitName(page.get(0).getDepartName());
				}
				userFinishedTaskData.setFinishedCount(tuple.getScore().intValue());
			}
			pageList.setRows(rows);
		}
		return pageList;
	}

	@Override
	public PageProcessTaskList getFinishedTasksByUnit(String unitId, String processDefId, String taskName,
			String userName, int pageNum, int pageSize, int orderByFinishedTime) throws WorkFlowException {
		CheckDataUtil.checkNull(unitId, "unitId");
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("unit_id", unitId);
		if(CheckDataUtil.isNotNull(processDefId))
		{
			params.put("process_id", processDefId);
		}
		if(CheckDataUtil.isNotNull(taskName))
		{
			params.put("task_name", taskName);
		}
		if(CheckDataUtil.isNotNull(userName))
		{
			params.put("user_name", userName);
		}

		//分页数据
		params.put("pageNumber", pageNum);
		params.put("pageSize", pageSize);
		Page<UserTaskFinishedEntity> pageEntity = userTaskFinishedService.listStcsmUserTaskFinished(params);
		PageProcessTaskList pageList = new PageProcessTaskList();
		/*pageList.setPageNum(pageEntity.getPageNo());
		pageList.setPageSize(pageEntity.getPageSize());
		pageList.setTotal(pageEntity.getTotal());
		List<ProcessTaskData> rows = new ArrayList<ProcessTaskData>();
		for(UserTaskFinishedEntity entity : pageEntity.getRows())*/
		pageList.setPageNum((int)pageEntity.getCurrent());
		pageList.setPageSize((int)pageEntity.getSize());
		pageList.setTotal((int)pageEntity.getTotal());
		List<ProcessTaskData> rows = new ArrayList<ProcessTaskData>();
		for(UserTaskFinishedEntity entity : pageEntity.getRecords())
		{
			ProcessTaskData task = new ProcessTaskData();
			task.setId(entity.getTaskId());
			task.setName(entity.getTaskName());
			task.setProcessDefId(entity.getProcessId());
			task.setProcessInstanceId(entity.getProcessInstanceId());
			task.setEndTime(entity.getFinishTime());
			TaskUserInfo assignee = new TaskUserInfo();
			assignee.setUserId(entity.getUserId());
			assignee.setUserName(entity.getUserName());
			task.setAssignee(assignee);
			rows.add(task);
		}
		pageList.setRows(rows);
		return pageList;
	}
	
	/** 
	* @Title: getUserFinishedKey 
	* @Description: 获取用户已完成任务视图key
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	private String getUserFinishedKey()
	{
		return JedisMgr_wf.KeyHead + ActivitiConstant.REDIS_USER_FINISHED_VIEW_KEY;
	}
	
	/** 
	* @Title: getUnitFinishedKey 
	* @Description: 获取部门已完成任务视图key
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	private String getUnitFinishedKey()
	{
		return JedisMgr_wf.KeyHead + ActivitiConstant.REDIS_UNIT_FINISHED_VIEW_KEY;
	}
	
	/** 
	* @Title: getLoadingKey 
	* @Description: 获取已完成数据加载中key
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	private String getLoadingKey()
	{
		return JedisMgr_wf.KeyHead + ActivitiConstant.REDIS_FINISHED_LOADING_KEY;
	}
	
	/** 
	* @Title: loadUserInfo 
	* @Description: 加载用户详情
	* @param userId userid
	* @return  参数说明 
	* @return UserInfo    返回类型 
	* 
	*/
	private TaskUserInfo loadUserInfo(String userId, boolean loadUnit)
	{
		TaskUserInfo info = new TaskUserInfo();
		if(userId == null)
		{
			info.setUserId("");
			info.setUserName("");
			if(loadUnit)
			{
				info.setUnitId("");
				info.setUnitName("");
			}
			return info;
		}
		info.setUserId(userId);
		// 请求用户中心获取用户名字
		info.setUnitId("userinfo.getDepartId()");
		info.setUnitName("userinfo.getDepartName()");
		try {
			List<UserDepartDto> userlist = authInfoUtil.getUserById(userId);
			if(userlist!=null && userlist.size()>0){
				UserDepartDto userinfo=userlist.get(0);
				info.setUserName(userinfo.getName());
				if(loadUnit)
				{
					info.setUnitId(userinfo.getDepartId());
					info.setUnitName(userinfo.getDepartName());
				}
			}
		} catch (Exception e) {

		}
		if(info.getUserName() == null)
			info.setUserName(userId);
		return info;
	}
	
}
