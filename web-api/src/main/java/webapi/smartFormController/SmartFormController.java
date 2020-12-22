package webapi.smartFormController;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartform.dto.PassParameter;
import smartform.form.model.*;
import smartform.form.service.FormCategoryService;
import smartform.form.service.SmartFormService;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** 
* @ClassName: SmartFormResolver 
* @Description: 表单接口
* @author hou
* @date 2018年9月18日 下午3:05:40
*  
*/
@Api(value="表单接口")
@RestController
@RequestMapping("smartForm")
public class SmartFormController {
	Logger log = Logger.getLogger(SmartFormController.class.getName());

	@Autowired
	private SmartFormService smartFormService;

	@Autowired
	private FormCategoryService formCategoryService;


	/**
	 * 查询组件列表
	 * @param formId
	 * @param contentId
	 * @return
	 */
	@ApiOperation("获取表单组件列表")
	@GetMapping(value = "/smartFormStatus")
	public List<FormPage> smartFormStatus(String formId,String contentId) {
		if(formId.length() != 32)
			return null;
		List<FormPage> formPageList = smartFormService.smartFormStatus(formId, contentId);
		return formPageList;
	}

	/**
	 * 获取表单
	 * @param id
	 * @return
	 */
	@ApiOperation("获取表单")
	@GetMapping(value = "/smartForm")
	public SmartForm smartForm(String id) {
		// 剔除非法ID
		if(id.length() != 32)
			return null;
		return smartFormService.smartForm(id, true);
	}

	/**
	 * 获取一个或多个表单
	 * @param ids
	 * @return
	 */
	@ApiOperation("多个表单")
	@GetMapping(value = "/smartFormByIds")
	public List<SmartForm> smartFormByIds(@RequestParam("ids") List<String> ids) {
		return smartFormService.smartFormByIds(ids, true);
	}
	
	/** 
	* @Title: smartFormSimple 
	* @Description: 获取表单简单数据（基础数据加分页信息）
	* @param id    formId
	* @param hasPage    是否包含分页数据
	* @return  参数说明 
	* @return SmartForm    返回类型 
	* 
	*/
	@ApiOperation("获取表单简单数据")
	@RequestMapping(value = "/smartFormInfo")
	public SmartForm smartFormInfo(String id, boolean hasPage) {
		// 剔除非法ID
		if(id.length() != 32)
			return null;
		return smartFormService.smartFormInfo(id, hasPage);
	}
	
	/**
	 * 获取表单分页列表
	 * @param id
	 * @return
	 * @throws ParseException 
	 */
	@ApiOperation("获取表单分页列表")
	@RequestMapping(value = "/smartFormPageList")
	public List<FormPage> smartFormPageList(String id, String contentId, boolean loadAll) throws ParseException
	{
		// 剔除非法ID
		if(id.length() != 32)
			return null;
		return smartFormService.smartFormPageList(id, contentId, loadAll);
	}
	
	/**
	 * 获取表单分页详细信息
	 * @param id
	 * @param pageId
	 * @param contentId 填报id
	 * @return
	 * @throws ParseException 
	 */
	@ApiOperation("获取表单分页详细信息")
	@GetMapping(value = "/smartFormPage")
	public FormPage smartFormPage(String id, String pageId, String contentId) throws ParseException
	{
		// 剔除非法ID
		if(id.length() != 32)
			return null;
		return smartFormService.smartFormPage(id, pageId, contentId);
	}
	
	/** 
	* @Title: saveFormPage 
	* @Description: 保存分页数据
	* @return
	* @throws ParseException  参数说明 
	* @return FormPage    返回类型 
	* 
	*/
	@ApiOperation("保存分页数据")
	@PostMapping(value = "/saveFormPage")
	public FormPage saveFormPage(@RequestBody PassParameter passParameter) throws ParseException
	{
		return smartFormService.saveFormPage(passParameter.getData(), passParameter.isStorage());
	}
	
	/** 
	* @Title: saveGroupLine 
	* @Description: 添加或编辑表格行
	* @return void    返回类型
	 * @throws ParseException 
	* 
	*/
	@ApiOperation("添加或编辑表格行")
	@PostMapping(value = "/saveGroupLine")
	public GroupLine saveGroupLine(@RequestBody PassParameter passParameter) throws ParseException
	{
		return smartFormService.saveGroupLine(passParameter.getData(), passParameter.isStorage());
	}
	
	/** 
	* @Title: delGroupLine 
	* @Description: 删除表格行
	* @param contentId
	* @param lineId  参数说明 
	* @return void    返回类型 
	* 
	*/
	@ApiOperation("删除表格行")
	@PostMapping(value = "/delGroupLine")
	public String delGroupLine(@RequestBody Map<String,String> params)
	{
		smartFormService.delGroupLine(params.get("formId"), params.get("contentId"), params.get("groupId"), params.get("lineId"), params.get("pageId"));
		return "1";
	}
	
	/** 
	* @Title: swapGroupLine 
	* @Description: 互换表格行
	* @param formId
	* @param contentId
	* @param groupId
	* @param lineId1
	* @param lineId2
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	@ApiOperation("互换表格行")
	@RequestMapping(value = "/swapGroupLine")
	public String swapGroupLine(String formId, String contentId, String groupId, String lineId1, String lineId2, String pageId)
	{
		smartFormService.swapGroupLine(formId, contentId, groupId, lineId1, lineId2, pageId);
		return "1";
	}
	
	/** 
	* @Title: updateGroupLineSort 
	* @Description: 更新表格行排序值
	* @param formId
	* @param contentId
	* @param groupId
	* @param lineId
	* @param sort
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	@ApiOperation("更新表格行排序值")
	@RequestMapping(value = "/updateGroupLineSort")
	public String updateGroupLineSort(String formId, String contentId, String groupId, String lineId, Integer sort, String pageId)
	{
		smartFormService.updateGroupLineSort(formId, contentId, groupId, lineId, sort, pageId);
		return "1";
	}
	
	/** 
	* @Title: resetSortGroupLine 
	* @Description: 重置表格组排序
	* @param formId
	* @param contentId
	* @param groupId
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	@ApiOperation("重置表格组排序")
	@RequestMapping(value = "/resetSortGroupLine")
	public String resetSortGroupLine(String formId, String contentId, String groupId, String pageId)
	{
		smartFormService.resetSortGroupLine(formId, contentId, groupId, pageId);
		return "1";
	}
	
	/**
	 * 获取表单分页数据，还未实现，请勿使用
	 * @param id
	 * @param pageId
	 * @return
	 */
	@Deprecated
	@ApiOperation("获取表单分页数据")
	@RequestMapping(value = "/smartFormData")
	public String smartFormData(String id, String pageId)
	{
		// 剔除非法ID
		if(id.length() != 32)
			return null;
		Map<String, String> datamap = smartFormService.smartFormData(id, pageId);
		return JSONObject.toJSONString(datamap);
	}

	/**
	 * 表单列表
	 * @param page
	 * @return
	 */
	@ApiOperation("表单列表")
	@PostMapping(value = "/smartFormList")
	public SmartFormPagination smartFormList(@RequestBody SmartFormInput page) {
		return smartFormService.smartFormList(page);
	}

	@ApiOperation("表单列表")
	@RequestMapping(value = "/smartFormListnew")
	public SmartFormPagination smartFormListnew(int state,String categoryId,String name,int nowPage,int pageSize) {
		SmartFormInput page=new  SmartFormInput();
		page.setState(state);
		page.setCategoryId(categoryId);
		page.setName(name);
		page.setNowPage(nowPage);
		page.setPageSize(pageSize);
		return smartFormService.smartFormList(page);
	}

	/**
	 * 创建表单
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("创建表单")
	@PostMapping(value = "/createSmartForm")
	public String createSmartForm(@RequestBody PassParameter passParameter) {
		return smartFormService.createSmartForm(passParameter.getForm());
	}

	/**
	 * 更新表单
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("更新表单")
	@PostMapping(value = "/updateSmartForm")
	public String updateSmartForm(@RequestBody PassParameter passParameter) {
		return smartFormService.updateSmartForm(passParameter.getForm());
	}

	/**
	 * 发布表单
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("发布表单")
	@PostMapping(value = "/updateSmartFormState")
	public String updateSmartFormState(@RequestBody PassParameter passParameter) {
		return smartFormService.updateSmartFormState(passParameter.getId(),passParameter.getState());
	}

	/**
	 * 复制表单
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("复制表单")
	@PostMapping(value = "/copySmartForm")
	public String copySmartForm(@RequestBody PassParameter passParameter) {
		return smartFormService.copySmartForm(passParameter.getId());
	}

	/**
	 * 刪除表單
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("刪除表單")
	@PostMapping(value = "/deleteSmartForm")
	public String deleteSmartForm(@RequestBody PassParameter passParameter) {
		return smartFormService.deleteSmartForm(passParameter.getId());
	}
}
