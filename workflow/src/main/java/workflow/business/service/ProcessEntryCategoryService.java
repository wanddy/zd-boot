package workflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.ProcessEntryCategory;
import workflow.business.model.entity.ProcessEntryMainEntity;

import java.util.Map;

/**
 * 未发布流程数据
 */
public interface ProcessEntryCategoryService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<ProcessEntryCategory> listProcessEntryCategory(Map<String, Object> params);

    /**
     * 新增
     * @param processEntryCategory
     * @return 添加成功条数
     */
	int saveProcessEntryCategory(ProcessEntryCategory processEntryCategory);


    /**
     * 根据id查询
     * @param id
     * @return
     */
	ProcessEntryCategory getProcessEntryCategoryById(String id);


    /**
     * 修改
     * @param projectMain
     * @return 更新成功条数
     */
	int updateProcessEntryCategory(ProcessEntryCategory projectMain);

    /**
     * 批量删除
     * @param id
     * @return 删除成功条数
     */
	int batchRemove(Long[] id);
	/**
	 * 单条删除
	 * @param id
	 * @return 删除成功条数
	 */
	int removeProcessEntryCategory(Object id);

}