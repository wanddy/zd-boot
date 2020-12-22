package webapi.workflowController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import workflow.business.model.entity.ProcessEntryCategory;
import workflow.business.service.ProcessEntryCategoryService;
import workflow.business.service.ProcessEntryMainService;

import java.util.Map;

/**
 * 业务主体
 */
@Controller
@RestController
@RequestMapping(value = "/workflow/processcategory")
public class ProjcessEntryCategoryServiceController {
	@Autowired
	private ProcessEntryCategoryService processEntryCategoryService ;

    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listProcessEntryCategory")
	Page<ProcessEntryCategory> listProcessEntryCategory(@RequestParam Map<String, Object> params){
		return processEntryCategoryService.listProcessEntryCategory(params);
	}

    /**
     * 新增
     * @param projectMain
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("新增数据")
	@RequestMapping(value = "/saveProcessEntryCategory")
	int saveProcessEntryCategory(@RequestBody ProcessEntryCategory projectMain){
		return processEntryCategoryService.saveProcessEntryCategory(projectMain);
	}


    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getProcessEntryCategoryById")
	ProcessEntryCategory getProcessEntryCategoryById(String id){
		return processEntryCategoryService.getProcessEntryCategoryById(id);
	}


    /**
     * 修改
     * @param projectMain
     * @return 更新成功条数
     */
	@ResponseBody
	@ApiOperation("更新成功条数")
	@RequestMapping(value = "/updateProcessEntryCategory")
	int updateProcessEntryCategory(ProcessEntryCategory projectMain){
		return processEntryCategoryService.updateProcessEntryCategory(projectMain);
	}

    /**
     * 批量删除
     * @param id
     * @return 删除成功条数
     */
	@ResponseBody
	@ApiOperation("删除成功条数")
	@RequestMapping(value = "/batchRemove")
	int batchRemove(Long[] id){
		return processEntryCategoryService.batchRemove(id);
	}
	/**
	 * 单条删除
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除成功条数")
	@RequestMapping(value = "/removeProcessEntryCategory")
	int removeProjectMain(Object id){
		return processEntryCategoryService.removeProcessEntryCategory(id);
	}

}