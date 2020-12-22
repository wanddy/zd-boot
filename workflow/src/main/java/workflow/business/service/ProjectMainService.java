package workflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.ProjectMainEntity;
import workflow.business.model.ProjectMainInputEntity;

import java.text.ParseException;
import smartform.form.model.FormPage;
import workflow.common.error.WorkFlowException;

import java.util.List;
import java.util.Map;

/**
 * 未发布流程数据
 */
public interface ProjectMainService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<ProjectMainEntity> listProjectMain(Map<String, Object> params);

    /**
     * 新增
     * @param projectMain
     * @return 添加成功条数
     */
	int saveProjectMain(ProjectMainEntity projectMain);

	/**
	 * 新增
	 * @param projectMainInputEntity
	 * @return 添加成功条数
	 */
	ProjectMainEntity saveProjectMain1(ProjectMainInputEntity projectMainInputEntity) throws ParseException;

    /**
     * 根据id查询
     * @param id
     * @return
     */
	ProjectMainEntity getProjectMainById(String id);


    /**
     * 修改
     * @param projectMain
     * @return 更新成功条数
     */
	int updateProjectMain(ProjectMainEntity projectMain);

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
	int removeProjectMain(Object id);

	/**
	 * 查询所有表单页信息
	 * @param formId
	 * @param contentId
	 * @return
	 */
	List<FormPage> selectSmartFormPageAll(String formId, String contentId);

	Object getProjectActHisList(String contentId,String processInstanceId)throws WorkFlowException;
}