package workflow.business.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.UnreleaseProcessEntity;

/**
 * 未发布流程关联数据
 */
public interface UnreleaseProcessService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<UnreleaseProcessEntity> listStcsmUnreleaseProcess(Map<String, Object> params);

    /**
     * 新增
     * @param unreleaseProcess
     * @return 添加成功条数
     */
	int saveStcsmUnreleaseProcess(UnreleaseProcessEntity unreleaseProcess);

    /**
     * 根据id查询
     * @param id
     * @return
     */
//	UnreleaseProcessEntity getStcsmUnreleaseProcessById(Long id);
	List<UnreleaseProcessEntity> getStcsmUnreleaseProcessById(Long id);

    /**
     * 修改
     * @param unreleaseProcess
     * @return 更新成功条数
     */
	int updateStcsmUnreleaseProcess(UnreleaseProcessEntity unreleaseProcess);

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	int batchRemove(Long[] id);
	
	int removeStcsmUnreleaseProcess(Object id);
}
