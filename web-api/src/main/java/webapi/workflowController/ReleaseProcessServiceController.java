package webapi.workflowController;

import workflow.business.model.entity.ReleaseProcessEntity;
import workflow.business.service.ReleaseProcessService;
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
 * 已发布流程关联数据
 */
//@Service("releaseProcessService")
@Controller
@RestController
@RequestMapping(value = "/workflow/releaseProcess")
public class ReleaseProcessServiceController {
    @Autowired
    private ReleaseProcessService releaseProcessService;
    /**
     * 分页查询
     * @param params
     * @return
     */
    @ResponseBody
    @ApiOperation("分页查询")
    @RequestMapping(value = "/listStcsmReleaseProcess")
	Page<ReleaseProcessEntity> listStcsmReleaseProcess(Map<String, Object> params){
        return releaseProcessService.listStcsmReleaseProcess(params);
    }

    /**
     * 新增
     * @param releaseProcess
     * @return 添加成功条数
     */
    @ResponseBody
    @ApiOperation("新增")
    @RequestMapping(value = "/saveStcsmReleaseProcess")
	int saveStcsmReleaseProcess(ReleaseProcessEntity releaseProcess){
        return releaseProcessService.saveStcsmReleaseProcess(releaseProcess);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @ResponseBody
    @ApiOperation("根据id查询")
    @RequestMapping(value = "/getStcsmReleaseProcessById")
    ReleaseProcessEntity getStcsmReleaseProcessById(Long id) {
        return releaseProcessService.getStcsmReleaseProcessById(id);
    }

    /**
     * 修改
     *
     * @param releaseProcess
     * @return 更新成功条数
     */
    @ResponseBody
    @ApiOperation("修改")
    @RequestMapping(value = "/updateStcsmReleaseProcess")
    int updateStcsmReleaseProcess(ReleaseProcessEntity releaseProcess) {
        return releaseProcessService.updateStcsmReleaseProcess(releaseProcess);
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
        return releaseProcessService.batchRemove(id);
    }

}
