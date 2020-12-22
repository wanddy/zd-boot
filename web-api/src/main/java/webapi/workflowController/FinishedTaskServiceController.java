package webapi.workflowController;

import workflow.business.model.ActiveTask;
import workflow.business.model.FinishedTask;
import workflow.business.service.FinishedTaskService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import workflow.olddata.model.PageInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 
 */
//@Service("finishedTaskService")
@Controller
@RestController
@RequestMapping(value = "/workflow/finishedTask")
public class FinishedTaskServiceController {
	@Autowired
	private FinishedTaskService finishedTaskService;

	/**
	 * 分页查询
	 *
	 * @param params
	 * @param page
	 * @return
	 */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/pageList")
	Page<FinishedTask> pageList(FinishedTask params, PageInput page) {
		return finishedTaskService.pageList(params, page);
	}
	/**
	 * 根据查询条件查询所有待办任务
	 *
	 * @param params 查询条件
	 * @return
	 */
	@ResponseBody
	@ApiOperation("根据查询条件查询所有待办任务")
	@RequestMapping(value = "/listAll")
	List<FinishedTask> listAll(FinishedTask params) {
		return finishedTaskService.listAll(params);
	}

	/**
     * 保存已办任务
     * @param userId   操作人
     * @param userName 操作人姓名
     * @param opResult 操作结果
     * @param opResultDesc 操作结果描述
     * @param taskList 需要保存成已办的任务列表
     * @return
     */
	@ResponseBody
	@ApiOperation("保存已办任务")
	@RequestMapping(value = "/saveFinishedTask")
	int saveFinishedTask(String userId, String userName, String opResult, String opResultDesc, List<ActiveTask> taskList){
		return finishedTaskService.saveFinishedTask(userId, userName, opResult, opResultDesc, taskList);
	}

	/**
	 * 根据用户id查询用户实际已完成任务数 
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@ApiOperation("根据用户id查询用户实际已完成任务数")
	@RequestMapping(value = "/countTaskByUser")
	Integer countTaskByUser(String userId, List<String> businessTypeList){
		return finishedTaskService.countTaskByUser(userId, businessTypeList);
	}

    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getFinishedTaskById")
	FinishedTask getFinishedTaskById(String id){
		return finishedTaskService.getFinishedTaskById(id);
	}
}
