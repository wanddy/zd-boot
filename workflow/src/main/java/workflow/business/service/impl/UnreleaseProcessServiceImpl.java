package workflow.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.service.UnreleaseProcessService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workflow.business.mapper.UnreleaseProcessMapper;
import workflow.business.model.entity.UnreleaseProcessEntity;
import workflow.olddata.annotation.LogOperation;
import workflow.olddata.annotation.SysLog;

import java.util.List;
import java.util.Map;
/**
 * 未发布流程关联数据
 */
@Service("unreleaseProcessService")
@DS("master")
public class UnreleaseProcessServiceImpl implements UnreleaseProcessService {

	@Autowired
	private UnreleaseProcessMapper stcsmUnreleaseProcessMapper;

    /**
     * 分页查询
     * @param params
     * @return
     */
	@Override
	public Page<UnreleaseProcessEntity> listStcsmUnreleaseProcess(Map<String, Object> params) {
		QueryWrapper<UnreleaseProcessEntity> query = new QueryWrapper<UnreleaseProcessEntity>();
		if(params.get("process_id")!=null){
			query.eq("process_id",params.get("process_id"));
		}
		query.orderByAsc("sub_id");
		Page<UnreleaseProcessEntity> page = new Page<>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
		stcsmUnreleaseProcessMapper.selectPage(page,query);
		return page;
	}

    /**
     * 新增
     * @param stcsmUnreleaseProcess
     * @return 添加成功条数
     */
	@Override
	public int saveStcsmUnreleaseProcess(UnreleaseProcessEntity stcsmUnreleaseProcess) {
		int count = stcsmUnreleaseProcessMapper.save(stcsmUnreleaseProcess);
		return count;
	}

	/**
     * 根据id查询
     * @param id
     * @return 返回查询到的实体
     */

	/*public UnreleaseProcessEntity getStcsmUnreleaseProcessById(Long id) {
		UnreleaseProcessEntity stcsmUnreleaseProcess = stcsmUnreleaseProcessMapper.getObjectById(id);
		return stcsmUnreleaseProcess;
	}*/
	@Override
	public List<UnreleaseProcessEntity> getStcsmUnreleaseProcessById(Long id) {
		QueryWrapper query=new QueryWrapper();
		if(id!=null){
			query.eq("process_id",id);
		}
		query.orderByAsc("sub_id");
		List<UnreleaseProcessEntity> list = stcsmUnreleaseProcessMapper.selectList(query);
		/*if(list!=null && list.size()>0){
			for (UnreleaseProcessEntity stcsmUnreleaseProcess : list){
				System.out.println(stcsmUnreleaseProcess.getSubKey());
			}
		}*/
		return list;
	}
    /**
     * 修改
     * @param stcsmUnreleaseProcess
     * @return 更新成功条数
     */
	@Override
	public int updateStcsmUnreleaseProcess(UnreleaseProcessEntity stcsmUnreleaseProcess) {
		int count = stcsmUnreleaseProcessMapper.update(stcsmUnreleaseProcess);
		return count;
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@Override
	@SysLog(value="未发布流程关联数据删除", table="wf_release_process", operation=LogOperation.BatchDelete)
	public int batchRemove(Long[] id) {
		int count = stcsmUnreleaseProcessMapper.batchRemove(id);
		return count;
	}

	@Override
	public int removeStcsmUnreleaseProcess(Object id) {
		return stcsmUnreleaseProcessMapper.remove(id);
	}

}
