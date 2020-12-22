package webapi.workflowController;

import java.util.Map;

import workflow.business.model.entity.SaveUndeployedEntity;
import workflow.business.service.EditModelService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.EditModelEntity;
import workflow.ide.core.Definitions;
import io.swagger.annotations.ApiOperation;
import workflow.olddata.annotation.LogOperation;
import workflow.olddata.annotation.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * 未发布流程数据
 */
//@Service("editModelService")
@Controller
@RestController
@RequestMapping(value = "/workflow/editModel")
public class EditModelServiceController {
	@Autowired
	private EditModelService editModelService;
    /**
     * 分页查询
     * @param params
     * @return
     */
	@ResponseBody
	@ApiOperation("分页查询")
	@RequestMapping(value = "/listStcsmEditModel")
	Page<EditModelEntity> listStcsmEditModel(Map<String, Object> params){
		return editModelService.listStcsmEditModel(params);
	}

    /**
     * 新增
     * @param stcsmEditModel
     * @return 添加成功条数
     */
	@ResponseBody
	@ApiOperation("添加成功条数")
	@RequestMapping(value = "/saveStcsmEditModel")
	int saveStcsmEditModel(EditModelEntity stcsmEditModel){
		return editModelService.saveStcsmEditModel(stcsmEditModel);
	}

    /**
     * 根据id查询
     * @param id
     * @return
     */
	@ResponseBody
	@ApiOperation("根据id查询")
	@RequestMapping(value = "/getStcsmEditModelById")
	EditModelEntity getStcsmEditModelById(String id){
		return editModelService.getStcsmEditModelById(id);
	}


	/**
	 * 根据key查询
	 * @param key
	 * @return
	 */
	@ResponseBody
	@ApiOperation("根据key查询")
	@RequestMapping(value = "/getStcsmEditModelByKey")
	EditModelEntity getStcsmEditModelByKey(String key){
		return editModelService.getStcsmEditModelByKey(key);
	}

    /**
     * 修改
     * @param stcsmEditModel
     * @return 更新成功条数
     */
	@ResponseBody
	@ApiOperation("更新成功条数")
	@RequestMapping(value = "/updateStcsmEditModel")
	int updateStcsmEditModel(EditModelEntity stcsmEditModel){
		return editModelService.updateStcsmEditModel(stcsmEditModel);
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@ResponseBody
	@ApiOperation("未发布流程删除")
	@RequestMapping(value = "/batchRemove")
	@SysLog(value="未发布流程删除", table="wf_edit_model", operation= LogOperation.BatchDelete)
	int batchRemove(Long[] id){
		return editModelService.batchRemove(id);
	}
	@ResponseBody
	@ApiOperation("未发布流程删除")
	@RequestMapping(value = "/removeStcsmEditModel")
	@SysLog(value="未发布流程删除", table="wf_edit_model", operation=LogOperation.Delete)
	int removeStcsmEditModel(Object id){
		return editModelService.removeStcsmEditModel(id);
	}

	/**
	 * 保存未发布活动
	 * @param definitions
	 * @return
	 */
	@ResponseBody
	@ApiOperation("保存未发布活动")
	@RequestMapping(value = "/saveUndeployedModel")
	public int saveUndeployedModel(String userId, Definitions definitions, String processId, int mode){
		return editModelService.saveUndeployedModel(userId, definitions, processId, mode);
	}

	/**
	 * 保存未发布活动
	 * @param saveUndeployedEntity
	 * @return
	 */
	@ResponseBody
	@ApiOperation("保存未发布活动 保存未发布流程 601 是“流程编号已经存在。” 603 是“XML解析错误”")
	@RequestMapping(value = "/saveUndeployedModel",method = RequestMethod.POST)
	public boolean saveUndeployedModel(@RequestBody SaveUndeployedEntity saveUndeployedEntity){
		return editModelService.saveUndeployedModel(saveUndeployedEntity);
	}
	
}