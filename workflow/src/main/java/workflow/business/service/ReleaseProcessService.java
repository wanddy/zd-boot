package workflow.business.service;

import java.util.Map;

import workflow.business.model.entity.ReleaseProcessEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 已发布流程关联数据
 */
public interface ReleaseProcessService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<ReleaseProcessEntity> listStcsmReleaseProcess(Map<String, Object> params);

    /**
     * 新增
     * @param stcsmReleaseProcess
     * @return 添加成功条数
     */
	int saveStcsmReleaseProcess(ReleaseProcessEntity stcsmReleaseProcess);

    /**
     * 根据id查询
     * @param id
     * @return
     */
	ReleaseProcessEntity getStcsmReleaseProcessById(Long id);

    /**
     * 修改
     * @param stcsmReleaseProcess
     * @return 更新成功条数
     */
	int updateStcsmReleaseProcess(ReleaseProcessEntity stcsmReleaseProcess);

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	int batchRemove(Long[] id);
	
}
