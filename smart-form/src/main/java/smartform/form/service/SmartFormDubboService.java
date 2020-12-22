package smartform.form.service;

import smartform.form.model.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/** 
* @ClassName: SmartFormDubboService 
* @Description: 智能表单dubbo接口
* @author quhanlin
* @date 2018年10月5日 下午2:36:17 
*  
*/
public interface SmartFormDubboService {

	/**
	 * 表单列表查询
	 * @param nowPage 当前页码
	 * @param pageSize 每页数量
	 * @param name 表单名称检索
	 * @param categoryId 分类ID
	 * @param state 表单状态 10, 全部状态; 1, 新建; 2, 发布
	 * @return
	 */
	SmartFormPagination smartFormList(Integer nowPage,Integer pageSize,
			String name, String categoryId, Integer state);
	
	/**
	 * 表单分类查询
	 * @param codes 
	 * @return
	 */
	List<FormCategory> formCategoryList(List<String> codes);
	
	
	/**
	 * 表单详情获取（获取简单信息）
	 * @param ids
	 * @return
	 */
	List<SmartForm> smartFormSimple(List<String> ids);
	
	/**
	 * 只获取当前表单分页状态
	 * @param formId
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	SmartFormContent smartFormContentPageState(String formId, String id)
			throws ParseException;
	
	/**
	 * 获取表单结构
	 * @param id
	 * @param pageId 按分页查询表单（目前只提出了分页外的选项源），
	 * 			pageId = null 自动获取第一页结构；
	 * 			pageId = 0 只获取分页列表，不获取分页内字段结构；
	 * @param hasOptions 是否获取当前分页的选项源信息（pageId = 0 时不获取选项信息）
	 * @return
	 */
	SmartForm smartForm(String id, String pageId, boolean hasOptions);
	
	/**
	 * 获取表单结构
	 * @param id
	 * @param pageId
	 * @param hasOptions
	 * @param hasFieldMapperList
	 * @return
	 */
	SmartForm smartForm(String id, String pageId, boolean hasOptions, boolean hasFieldMapperList);
	
	/**
	 * 获取表单验证规则
	 * @param id
	 * @return Map<pageid, List<SuperGroupRule>>
	 */
	Map<String, List<SuperGroupRule>> smartFormRules(String id);
}
