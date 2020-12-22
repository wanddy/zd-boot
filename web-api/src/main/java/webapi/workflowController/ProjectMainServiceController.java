package webapi.workflowController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smartform.form.model.FormPage;
import workflow.business.model.entity.ProjectMainEntity;
import workflow.business.model.ProjectMainInputEntity;
import workflow.business.service.ProjectMainService;
import workflow.common.error.WorkFlowException;

import java.util.List;
import java.text.ParseException;
import java.util.Map;

/**
 * 业务主体
 */
@Controller
@RestController
@RequestMapping(value = "/workflow/progectmain")
public class ProjectMainServiceController {
	@Autowired
	private ProjectMainService projectMainService;
    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listProjectMain")
	Page<ProjectMainEntity> listProjectMain(@RequestParam Map<String, Object> params){
		return projectMainService.listProjectMain(params);
	}

    /**
     * 新增
     * @param projectMain
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("新增数据")
	@RequestMapping(value = "/saveProjectMain")
	int saveProjectMain(@RequestBody ProjectMainEntity projectMain){
		return projectMainService.saveProjectMain(projectMain);
	}

	/**
	 * 新增
	 * @param projectMain
	 * @return 添加成功条数
	 */
	@ResponseBody
	@ApiOperation("新增数据")
	@RequestMapping(value = "/saveProjectMain1")
	ProjectMainEntity saveProjectMain1(@RequestBody ProjectMainInputEntity projectMainInputEntity) throws ParseException {
		return projectMainService.saveProjectMain1(projectMainInputEntity);
	}
	/**
	 * 新增
	 * @param projectType
	 * @return 添加成功条数
	 */
	@ResponseBody
	@ApiOperation("新增数据")
	@RequestMapping(value = "/saveProjectMain2")
	int saveProjectMain(String projectType,String projectName,String formId){
		ProjectMainEntity projectMain = new ProjectMainEntity();
		projectMain.setProjectType(projectType);
		projectMain.setProjectName(projectName);
		projectMain.setFormId(formId);
		return projectMainService.saveProjectMain(projectMain);
	}

    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getProjectMainById")
	ProjectMainEntity getProjectMainById(String id){
		return projectMainService.getProjectMainById(id);
	}


    /**
     * 修改
     * @param projectMain
     * @return 更新成功条数
     */
	@ResponseBody
	@ApiOperation("更新成功条数")
	@RequestMapping(value = "/updateProjectMain")
	int updateProjectMain(ProjectMainEntity projectMain){
		return projectMainService.updateProjectMain(projectMain);
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
		return projectMainService.batchRemove(id);
	}
	/**
	 * 单条删除
	 * @param id
	 * @return 删除成功条数
	 */
	@ResponseBody
	@ApiOperation("删除成功条数")
	@RequestMapping(value = "/removeProjectMain")
	int removeProjectMain(Object id){
		return projectMainService.removeProjectMain(id);
	}

	@ResponseBody
	@RequestMapping(value = "/selectSmartFormPageAll")
	List<FormPage> selectSmartFormPageAll(String formId, String contentId){
		System.out.println("=============formId:"+formId);
		System.out.println("=============contentId:"+contentId);
	return 	projectMainService.selectSmartFormPageAll(formId,contentId);
	}

	@ResponseBody
	@RequestMapping(value = "/getProjectActHisList")
	Object getProjectActHisList(String contentId,String processInstanceId)throws WorkFlowException {
		return 	projectMainService.getProjectActHisList(contentId,processInstanceId);
	}
}