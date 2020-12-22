package workflow.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.service.ReleaseProcessService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workflow.business.mapper.ReleaseProcessMapper;
import workflow.business.model.entity.ReleaseProcessEntity;
import workflow.olddata.annotation.LogOperation;
import workflow.olddata.annotation.SysLog;


import java.util.Map;

/**
 * 已发布流程关联数据
 */
@Service("releaseProcessService")
@DS("master")
public class ReleaseProcessServiceImpl implements ReleaseProcessService {

	@Autowired
	private ReleaseProcessMapper stcsmReleaseProcessMapper;

    /**
     * 分页查询
     * @param params
     * @return
     */
	@Override
	public Page<ReleaseProcessEntity> listStcsmReleaseProcess(Map<String, Object> params) {
		QueryWrapper<ReleaseProcessEntity> query = new QueryWrapper<ReleaseProcessEntity>();
		if(params.get("process_id")!=null){
			query.eq("process_id",params.get("process_id"));
		}
		Page<ReleaseProcessEntity> page = new Page<>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
		stcsmReleaseProcessMapper.selectPage(page , query);
		return page;
	}

    /**
     * 新增
     * @param stcsmReleaseProcess
     * @return 添加成功条数
     */
	@Override
	@SysLog(value="已发布流程关联数据删除", table="wf_release_process", operation=LogOperation.BatchDelete)
	public int saveStcsmReleaseProcess(ReleaseProcessEntity stcsmReleaseProcess) {
		int count = stcsmReleaseProcessMapper.save(stcsmReleaseProcess);
		return count;
	}

    /**
     * 根据id查询
     * @param id
     * @return 返回查询到的实体
     */
	@Override
	public ReleaseProcessEntity getStcsmReleaseProcessById(Long id) {
		ReleaseProcessEntity stcsmReleaseProcess = stcsmReleaseProcessMapper.getObjectById(id);
		return stcsmReleaseProcess;
	}

    /**
     * 修改
     * @param stcsmReleaseProcess
     * @return 更新成功条数
     */
	@Override
	public int updateStcsmReleaseProcess(ReleaseProcessEntity stcsmReleaseProcess) {
		int count = stcsmReleaseProcessMapper.update(stcsmReleaseProcess);
		return count;
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@Override
	public int batchRemove(Long[] id) {
		int count = stcsmReleaseProcessMapper.batchRemove(id);
		return count;
	}

}
