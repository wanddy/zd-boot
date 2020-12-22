package webapi.smartFormController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smartform.widget.model.FormWidgetInput;
import smartform.widget.model.FormWidgetPagination;
import smartform.widget.model.WidgetBase;
import smartform.widget.service.FormWidgetService;

import java.util.logging.Logger;

/** 
* @ClassName: FormWidgetResolver 
* @Description: 选项源接口
* @author hou
* @date 2018年9月18日 下午2:30:55 
*  
*/
@Api(value="表单部件接口")
@RestController
@RequestMapping("formWidget")
public class FormWidgetController {
	@Autowired
	private FormWidgetService formWidgetService;
	Logger log = Logger.getLogger(FormWidgetController.class.getName());

	/**
	 * 根据id获取一个FormWidget实体，并转化成JSON
	 * 
	 * @param id
	 */
	@ApiOperation("根据id获取一个FormWidget实体，并转化成JSON")
	@RequestMapping(value = "/formWidget")
	public WidgetBase formWidget(String id) {
		return formWidgetService.formWidget(id);
	}
	/**
	 * 根据输入信息获取一个FormWidget的列表
	 * 
	 * @param page
	 */
	@ApiOperation("根据输入信息获取一个FormWidget的列表")
	@RequestMapping(value = "/formWidgetList")
	public FormWidgetPagination formWidgetList(FormWidgetInput page) {
		FormWidgetPagination fwp = new FormWidgetPagination();
		return fwp;
	}

	/**
	 * 创建预设控件
	 * 
	 * @param widget
	 */
	@ApiOperation("创建预设控件")
	@PostMapping(value = "/createFormWidget")
	public String createFormWidget(@RequestBody String widget) {

		return formWidgetService.createFormWidget(widget);
	}

	/**
	 * 更新预设控件
	 * 
	 * @param widget
	 * 
	 */
	@ApiOperation("更新预设控件")
	@PostMapping(value = "/updateFormWidget")
	public String updateFormWidget(@RequestBody String widget) {
		return formWidgetService.updateFormWidget(widget);
	}

	/**
	 * 删除预设控件
	 * 
	 * @param widget
	 */
	@ApiOperation("删除预设控件")
	@PostMapping(value = "/deleteFormWidget")
	public String deleteFormWidget(@RequestBody String id) {
		return formWidgetService.deleteFormWidget(id);
	}
}
