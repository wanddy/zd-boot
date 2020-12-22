package smartform.form.service;

import smartform.form.model.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SmartFormService
 * @Description: 表单服务
 * @author hou
 * @date 2018年9月22日 下午2:05:56
 */
public interface SmartFormService {

	/**
	 * 查询组件列表
	 * @param formId
	 * @param contentId
	 * @return
	 */
	public List<FormPage> smartFormStatus(String formId,String contentId);

	/**
	 * 获取表单
	 * @param id
	 * @return
	 */
	SmartForm smartForm(String id, boolean hasOptions);

	/**
	 * 获取表单
	 * @param id
	 * @return
	 */
	SmartForm smartForm(String id, String pageId, boolean hasOptions);
	
	/**
	 * 获取表单基本信息
	 * @param id
	 * @param hasPage 是否包含分页数据
	 * @return
	 */
	SmartForm smartFormInfo(String id, boolean hasPage);
	
	/**
	 * 获取表单分页列表
	 * @param id
	 * @return
	 * @throws ParseException 
	 */
	List<FormPage> smartFormPageList(String id, String contentId, boolean loadAll) throws ParseException;
	
	/**
	 * 获取表单分页详细信息
	 * @param pageId
	 * @param contentId 数据实体id，为空时不读数据
	 * @return
	 */
	FormPage smartFormPage(String formId, String pageId, String contentId) throws ParseException;
	
	/** 
	* @Title: saveFormPage 
	* @Description: 保存表单分页信息
	* @param data
	* @param isStorage 是否是暂存
	* @return  参数说明 
	* @return FormPage    返回类型 
	* 
	*/
	FormPage saveFormPage(String data, boolean isStorage) throws ParseException;
	
	/** 
	* @Title: saveGroupLine 
	* @Description: 添加或编辑表格行
	* @param data  参数说明 
	* @return GroupLine    返回类型 
	* 
	*/
	GroupLine saveGroupLine(String data, boolean isStorage) throws ParseException;
	
	/** 
	* @Title: delGroupLine 
	* @Description: 删除表格行
	* @param formId
	* @param contentId
	* @param groupId
	* @param lineId  参数说明 
	* @return void    返回类型 
	* 
	*/
	void delGroupLine(String formId, String contentId, String groupId, String lineId, String pageId);
	
	/**
	 * 获取表单分页数据
	 * @param id
	 * @param pageId
	 * @return
	 */
	Map<String, String> smartFormData(String id, String pageId);

	/**
	 * 表单列表
	 * @param page
	 * @return
	 */
	SmartFormPagination smartFormList(SmartFormInput page);

	/**
	 * 创建表单
	 * @param form
	 * @return
	 */
	String createSmartForm(String form);

	/**
	 * 更新表单
	 * @param form
	 * @return
	 */
	String updateSmartForm(String form);

	/**
	 * 发布表单
	 * @param id
	 * @param state
	 * @return
	 */
	String updateSmartFormState(String id, Integer state);

	/**
	 *  复制表单
	 * @param id
	 * @return
	 */
	String copySmartForm(String id);

	/**
	 * 删除表单
	 * @param id
	 * @return
	 */
	String deleteSmartForm(String id);

	/** 
	* @Title: swapGroupLine 
	* @Description: 调整表格行排序 
	* @param formId
	* @param contentId
	* @param groupId
	* @param lineId1
	* @param lineId2  参数说明 
	* @return void    返回类型 
	* 
	*/
	void swapGroupLine(String formId, String contentId, String groupId, String lineId1, String lineId2, String pageId);

	/** 
	* @Title: updateGroupLineSort 
	* @Description: 更新表格行排序值
	* @param formId
	* @param contentId
	* @param groupId
	* @param lineId
	* @param sort  参数说明 
	* @return void    返回类型 
	* 
	*/
	void updateGroupLineSort(String formId, String contentId, String groupId, String lineId, Integer sort, String pageId);
	
	/** 
	* @Title: resetSortGroupLine 
	* @Description: 重置表格组排序
	* @param formId
	* @param contentId
	* @param groupId  参数说明 
	* @return void    返回类型 
	* 
	*/
	void resetSortGroupLine(String formId, String contentId, String groupId, String pageId);

	/**
	 * 填充页面数据
	 * @param smartform
	 * @param page
	 * @param contentId
	 * @return
	 */
	void findPageContentById(SmartForm smartform, FormPage page, String contentId);

	/**
	 * 根据多个id获取多个表单
	 * @param ids
	 * @param hasOptions
	 * @return
	 */
	List<SmartForm> smartFormByIds(List<String> ids, boolean hasOptions);
}
