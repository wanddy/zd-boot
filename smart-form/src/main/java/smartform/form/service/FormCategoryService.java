package smartform.form.service;

import smartform.form.model.FormCategory;

import java.util.List;

/**
 * @ClassName: FormCategoryService
 * @Description: 表单分类服务
 * @author hou
 * @date 2018年9月22日 上午11:15:45
 */
public interface FormCategoryService {
	/**
	 * 表单分类查询
	 * 
	 * @return
	 */
	List<FormCategory> formCategoryList(Integer type);
	

	/**
	 * 根据codes表单分类查询
	 * @param codes
	 * @return
	 */
	List<FormCategory> formCategoryList(List<String> codes);

	/**
	 * 创建表单分类
	 * 
	 * @param category
	 * @return
	 */
	String createFormCategory(String category);

	/**
	 * 更新表单分类
	 * 
	 * @param category
	 * @return
	 */
	String updateFormCategory(String category);

	/**
	 * 删除表单分类
	 * 
	 * @param id
	 * @return
	 */
	String deleteFormCategory(String id);
}
