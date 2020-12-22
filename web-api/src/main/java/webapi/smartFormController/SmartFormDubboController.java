package webapi.smartFormController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smartform.form.model.*;
import smartform.form.service.SmartFormDubboService;

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
@Api(value="表单接口")
@RestController
@RequestMapping("smartFormDubbo")
public class SmartFormDubboController {

	@Autowired
	private SmartFormDubboService smartFormDubboService;

	/**
	 * 表单列表查询
	 * @param nowPage 当前页码
	 * @param pageSize 每页数量
	 * @param name 表单名称检索
	 * @param categoryId 分类ID
	 * @param state 表单状态 10, 全部状态; 1, 新建; 2, 发布
	 * @return
	 */
	@ApiOperation("表单列表查询")
	@RequestMapping(value = "/smartFormList")
	public SmartFormPagination smartFormList(Integer nowPage,Integer pageSize,
			String name, String categoryId, Integer state){
		return smartFormDubboService.smartFormList(nowPage, pageSize, name, categoryId, state);
	}
	
	/**
	 * 表单分类查询
	 * @param codes 
	 * @return
	 */
	@ApiOperation("表单分类查询")
	@RequestMapping(value = "/formCategoryList")
	public List<FormCategory> formCategoryList(List<String> codes){
		return smartFormDubboService.formCategoryList(codes);
	}
	
	
	/**
	 * 表单详情获取（获取简单信息）
	 * @param ids
	 * @return
	 */
	@ApiOperation("表单详情获取")
	@RequestMapping(value = "/smartFormSimple")
	public List<SmartForm> smartFormSimple(List<String> ids) {
		return smartFormDubboService.smartFormSimple(ids);
	}
	
	/**
	 * 只获取当前表单分页状态
	 * @param formId
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	@ApiOperation("只获取当前表单分页状态")
	@RequestMapping(value = "/smartFormContentPageState")
	public SmartFormContent smartFormContentPageState(String formId, String id) throws ParseException{
		return smartFormDubboService.smartFormContentPageState(formId, id);
	}
	
	/**
	 * 获取表单结构
	 * @param id
	 * @param pageId 按分页查询表单（目前只提出了分页外的选项源），
	 * 			pageId = null 自动获取第一页结构；
	 * 			pageId = 0 只获取分页列表，不获取分页内字段结构；
	 * @param hasOptions 是否获取当前分页的选项源信息（pageId = 0 时不获取选项信息）
	 * @return
	 */
	@ApiOperation("获取表单结构")
	@RequestMapping(value = "/smartForm1")
	public SmartForm smartForm(String id, String pageId, boolean hasOptions){
		return smartFormDubboService.smartForm(id, pageId, hasOptions);
	}
	
	/**
	 * 获取表单结构
	 * @param id
	 * @param pageId
	 * @param hasOptions
	 * @param hasFieldMapperList
	 * @return
	 */
	@ApiOperation("获取表单结构")
	@RequestMapping(value = "/smartForm2")
	public	SmartForm smartForm(String id, String pageId, boolean hasOptions, boolean hasFieldMapperList){
		return smartFormDubboService.smartForm(id, pageId, hasOptions, hasFieldMapperList);
	}
	
	/**
	 * 获取表单验证规则
	 * @param id
	 * @return Map<pageid, List<SuperGroupRule>>
	 */
	@ApiOperation("获取表单验证规则")
	@RequestMapping(value = "/smartFormRules")
	public Map<String, List<SuperGroupRule>> smartFormRules(String id){
		return  smartFormDubboService.smartFormRules(id);
	}
}
