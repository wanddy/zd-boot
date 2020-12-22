package webapi.smartFormController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartform.dto.PassParameter;
import smartform.form.model.FormCategory;
import smartform.form.service.FormCategoryService;

import java.util.List;
import java.util.logging.Logger;

/** 
* @ClassName: FormCategoryResolver 
* @Description: 表单分类接口
* @author hou
* @date 2018年9月18日 下午4:06:15
*  
*/
@Api(value="表单分类")
@RestController
@RequestMapping("formCategory")
public class FormCategoryController {

	Logger log = Logger.getLogger(FormCategoryController.class.getName());

	@Autowired
	private FormCategoryService formCategoryService;

	/**
	 * 表单分类查询
	 * param type
	 * @return
	 */
	@ApiOperation("表单分类查询")
	@GetMapping(value = "/formCategoryList")
	public List<FormCategory> formCategoryList(@RequestParam("categoryType") Integer type) {
		return formCategoryService.formCategoryList(type);
	}

	/**
	 * 创建表单分类
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("创建表单分类")
	@PostMapping(value = "/createFormCategory")
	public String createFormCategory(@RequestBody PassParameter passParameter) {
		return formCategoryService.createFormCategory(passParameter.getCategory());
	}

	/**
	 * 更新表单分类
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("更新表单分类")
	@PostMapping(value = "/updateFormCategory")
	public String updateFormCategory(@RequestBody PassParameter passParameter) {
		return formCategoryService.updateFormCategory(passParameter.getCategory());
	}

	/**
	 * 删除表单分类
	 * @param passParameter
	 * @return
	 */
	@ApiOperation("删除表单分类")
	@PostMapping(value = "/deleteFormCategory")
	public String deleteFormCategory(@RequestBody PassParameter passParameter) {
		return formCategoryService.deleteFormCategory(passParameter.getId());
	}
}
