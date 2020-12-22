package workflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import smartform.form.model.FormPage;
import workflow.business.model.entity.ProjectPagesEntity;

import java.text.ParseException;
import java.util.Map;

/**
 * 未发布流程数据
 */
public interface ProjectPagesService {

    /**
     * 分页查询
     * @param params
     * @return
     */
	Page<FormPage> listProjectPages(Map<String, Object> params);

    /**
     * 新增
     * @param projectMain
     * @return 添加成功条数
     */
	int saveProjectPages(ProjectPagesEntity projectMain)throws ParseException;

    /**
     * 根据id查询
     * @param id
     * @return
     */
	ProjectPagesEntity getProjectPagesById(String id);


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
	int removeProjectPages(Object id);
	/**
	 * 单条删除
	 * @param contentid
	 * @return 删除成功条数
	 */
	int removeProjectPagesbyContentId(Object contentid);
}