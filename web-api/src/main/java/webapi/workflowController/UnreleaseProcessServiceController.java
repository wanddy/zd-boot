package webapi.workflowController;

import workflow.business.model.entity.UnreleaseProcessEntity;
import workflow.business.service.UnreleaseProcessService;
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
 * 未发布流程关联数据
 */
//@Service("unreleaseProcessService")
@Controller
@RestController
@RequestMapping(value = "/workflow/unreleaseProcess")
public class UnreleaseProcessServiceController {
	@Autowired
	private UnreleaseProcessService unreleaseProcessService;

	/**
	 * 分页查询
	 *
	 * @param params
	 * @return
	 */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listStcsmUnreleaseProcess")
	Page<UnreleaseProcessEntity> listStcsmUnreleaseProcess(Map<String, Object> params) {
		return unreleaseProcessService.listStcsmUnreleaseProcess(params);
	}

	/**
     * 新增
     * @param unreleaseProcess
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("新增")
	@RequestMapping(value = "/saveStcsmUnreleaseProcess")
	int saveStcsmUnreleaseProcess(UnreleaseProcessEntity unreleaseProcess){
		return unreleaseProcessService.saveStcsmUnreleaseProcess(unreleaseProcess);
	}

    /**
     * 根据id查询
     * @param id
     * @return
     */
//	UnreleaseProcessEntity getStcsmUnreleaseProcessById(Long id);
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getStcsmUnreleaseProcessById")
	List<UnreleaseProcessEntity> getStcsmUnreleaseProcessById(Long id){
		return unreleaseProcessService.getStcsmUnreleaseProcessById(id);
	}

	/**
	 * 修改
	 *
	 * @param unreleaseProcess
	 * @return 更新成功条数
	 */
	@ResponseBody
	@ApiOperation("修改")
	@RequestMapping(value = "/updateStcsmUnreleaseProcess")
	int updateStcsmUnreleaseProcess(UnreleaseProcessEntity unreleaseProcess) {
		return unreleaseProcessService.updateStcsmUnreleaseProcess(unreleaseProcess);
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
		return unreleaseProcessService.batchRemove(id);
	}
	/**
	 * 删除
	 *
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除")
	@RequestMapping(value = "/removeStcsmUnreleaseProcess")
	int removeStcsmUnreleaseProcess(Object id) {
		return unreleaseProcessService.removeStcsmUnreleaseProcess(id);
	}
}
