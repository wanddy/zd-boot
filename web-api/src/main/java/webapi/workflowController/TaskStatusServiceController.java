package webapi.workflowController;

import workflow.business.model.TaskStatus;
import workflow.business.service.TaskStatusService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 
 */
//@Service("taskStatusService")
@Controller
@RestController
@RequestMapping(value = "/workflow/taskStatus")
public class TaskStatusServiceController {
	@Autowired
	private TaskStatusService taskStatusService;
    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listStcsmTaskStatus")
	Page<TaskStatus> listStcsmTaskStatus(Map<String, Object> params){
		return taskStatusService.listStcsmTaskStatus(params);
	}

    /**
     * 新增
     * @param taskStatus
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("新增")
	@RequestMapping(value = "/saveStcsmTaskStatus")
	int saveStcsmTaskStatus(TaskStatus taskStatus){
		return taskStatusService.saveStcsmTaskStatus(taskStatus);
	}

    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getStcsmTaskStatusById")
	TaskStatus getStcsmTaskStatusById(String id){
		return taskStatusService.getStcsmTaskStatusById(id);
	}

    /**
     * 修改
     * @param taskStatus
     * @return 更新成功条数
     */
	@ResponseBody
	@ApiOperation("修改")
	@RequestMapping(value = "/updateStcsmTaskStatus")
	int updateStcsmTaskStatus(TaskStatus taskStatus){
		return taskStatusService.updateStcsmTaskStatus(taskStatus);
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@ResponseBody
	@ApiOperation("删除")
	@RequestMapping(value = "/batchRemove")
	int batchRemove(String[] id){
		return taskStatusService.batchRemove(id);
	}
	
	/**
	 * 调用updateProcessInstToContent存储过程
	 * @param processInstanceId
	 * @param contentId
	 * @return
	 */
	@ResponseBody
	@ApiOperation("调用updateProcessInstToContent存储过程")
	@RequestMapping(value = "/updateProcessInstToContent")
	int updateProcessInstToContent(String processInstanceId, String contentId){
		return taskStatusService.updateProcessInstToContent(processInstanceId, contentId);
	}
}
