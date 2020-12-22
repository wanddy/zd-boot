package webapi.workflowController;

import workflow.business.model.entity.UserTaskFinishedEntity;
import workflow.business.service.UserTaskFinishedService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户完成记录表
 */
//@Service("userTaskFinished")
@Controller
@RestController
@RequestMapping(value = "/workflow/userTaskFinished")
public class UserTaskFinishedServiceController {
	@Autowired
	private UserTaskFinishedService userTaskFinishedService;
    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listStcsmUserTaskFinished")
	Page<UserTaskFinishedEntity> listStcsmUserTaskFinished(Map<String, Object> params){
		return userTaskFinishedService.listStcsmUserTaskFinished(params);
	}

    /**
     * 新增
     * @param userTaskFinished
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("添加成功条数")
	@RequestMapping(value = "/saveStcsmUserTaskFinished")
	int saveStcsmUserTaskFinished(UserTaskFinishedEntity userTaskFinished){
		return userTaskFinishedService.saveStcsmUserTaskFinished(userTaskFinished);
	}

	/**
	 * 根据id查询
	 *
	 * @param id
	 * @return
	 */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getStcsmUserTaskFinishedById")
	UserTaskFinishedEntity getStcsmUserTaskFinishedById(Long id) {
		return userTaskFinishedService.getStcsmUserTaskFinishedById(id);
	}

	/**
	 * 修改
	 *
	 * @param userTaskFinished
	 * @return 更新成功条数
	 */
	@ResponseBody
	@ApiOperation("修改")
	@RequestMapping(value = "/updateStcsmUserTaskFinished")
	int updateStcsmUserTaskFinished(UserTaskFinishedEntity userTaskFinished) {
		return userTaskFinishedService.updateStcsmUserTaskFinished(userTaskFinished);
	}

	/**
	 * 删除
	 *
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除")
	@RequestMapping(value = "/batchRemove")
	int batchRemove(Long[] id) {
		return userTaskFinishedService.batchRemove(id);
	}

	/**
	 * @return List<UserTaskFinishedEntity>    返回类型
	 * @Title: simpleList
	 * @Description: 获取简单数据列表
	 */
	@ResponseBody
	@ApiOperation("获取简单数据列表")
	@RequestMapping(value = "/simpleList")
	public List<UserTaskFinishedEntity> simpleList() {
		return userTaskFinishedService.simpleList();
	}

}
