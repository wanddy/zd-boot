package webapi.workflowController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import workflow.business.model.entity.ProcessEntryMainEntity;
import workflow.business.service.ProcessEntryMainService;
import java.util.Map;

/**
 * 业务主体
 */
@Controller
@RestController
@RequestMapping(value = "/workflow/processmain")
public class ProjcessEntryMainServiceController {
	@Autowired
	private ProcessEntryMainService processEntryMainService;

    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listProcessEntryMain")
	Page<ProcessEntryMainEntity> listProcessEntryMain(@RequestParam Map<String, Object> params){
		return processEntryMainService.listProcessEntryMain(params);
	}

    /**
     * 新增
     * @param projectMain
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("新增数据")
	@RequestMapping(value = "/saveProcessEntryMain")
	int saveProcessEntryMain(@RequestBody ProcessEntryMainEntity projectMain){
		return processEntryMainService.saveProcessEntryMain(projectMain);
	}


    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getProcessEntryMainById")
	ProcessEntryMainEntity getProcessEntryMainById(String id){
		return processEntryMainService.getProcessEntryMainById(id);
	}


    /**
     * 修改
     * @param projectMain
     * @return 更新成功条数
     */
	@ResponseBody
	@ApiOperation("更新成功条数")
	@RequestMapping(value = "/updateProcessEntryMain")
	int updateProcessEntryMain(ProcessEntryMainEntity projectMain){
		return processEntryMainService.updateProcessEntryMain(projectMain);
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
		return processEntryMainService.batchRemove(id);
	}
	/**
	 * 单条删除
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除成功条数")
	@RequestMapping(value = "/removeProcessEntryMain")
	int removeProjectMain(Object id){
		return processEntryMainService.removeProcessEntryMain(id);
	}

}