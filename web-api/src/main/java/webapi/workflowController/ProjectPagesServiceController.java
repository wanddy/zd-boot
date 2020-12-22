package webapi.workflowController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import smartform.form.model.FormPage;
import workflow.business.model.entity.ProjectPagesEntity;
import workflow.business.service.ProjectPagesService;

import java.text.ParseException;
import java.util.Map;

/**
 * 业务主体
 */
@Controller
@RestController
@RequestMapping(value = "/workflow/progectpages")
public class ProjectPagesServiceController {
	@Autowired
	ProjectPagesService projectPagesService;
    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listProjectPages")
	Page<FormPage> listProjectPages(@RequestParam Map<String, Object> params){
		return projectPagesService.listProjectPages(params);
	}

    /**
     * 新增
     * @param projectMain
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("新增数据")
	@RequestMapping(value = "/saveProjectPages")
	int saveProjectPages(ProjectPagesEntity projectMain)throws ParseException {
		return projectPagesService.saveProjectPages(projectMain);
	}

    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getProjectPagesById")
	ProjectPagesEntity getProjectPagesById(String id){
		return projectPagesService.getProjectPagesById(id);
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
		return projectPagesService.batchRemove(id);
	}
	/**
	 * 单条删除
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除成功条数")
	@RequestMapping(value = "/removeProjectPages")
	int removeProjectPages(Object id){
		return projectPagesService.removeProjectPages(id);
	}

	/**
	 * 单条删除
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除成功条数")
	@RequestMapping(value = "/removebyContentId")
	int removeProjectPagesbyContentId(Object id){
		return projectPagesService.removeProjectPagesbyContentId(id);
	}
}