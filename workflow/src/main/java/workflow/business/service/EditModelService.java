package workflow.business.service;

import java.util.Map;


import workflow.business.model.entity.EditModelEntity;
import workflow.business.model.entity.SaveUndeployedEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.ide.core.Definitions;

/**
 * 未发布流程数据
 */
public interface EditModelService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<EditModelEntity> listStcsmEditModel(Map<String, Object> params);

    /**
     * 新增
     * @param stcsmEditModel
     * @return 添加成功条数
     */
	int saveStcsmEditModel(EditModelEntity stcsmEditModel);

    /**
     * 根据id查询
     * @param id
     * @return
     */
	EditModelEntity getStcsmEditModelById(String id);


	/**
	 * 根据key查询
	 * @param key
	 * @return
	 */
	EditModelEntity getStcsmEditModelByKey(String key);

    /**
     * 修改
     * @param stcsmEditModel
     * @return 更新成功条数
     */
	int updateStcsmEditModel(EditModelEntity stcsmEditModel);

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	int batchRemove(Long[] id);
	
	int removeStcsmEditModel(Object id);

	/**
	 * 保存未发布活动
	 * @param definitions
	 * @return
	 */
	public int saveUndeployedModel(String userId, Definitions definitions, String processId, int mode);

	/**
	 * 保存未发布活动
	 * @param saveUndeployedEntity
	 * @return
	 */
	public boolean saveUndeployedModel(SaveUndeployedEntity saveUndeployedEntity);
	
}