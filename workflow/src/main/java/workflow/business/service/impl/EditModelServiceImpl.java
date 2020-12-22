package workflow.business.service.impl;

import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import workflow.business.service.EditModelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import workflow.common.utils.UUIDUtil;
import workflow.business.mapper.EditModelMapper;
import workflow.business.mapper.UnreleaseProcessMapper;
import workflow.business.model.entity.EditModelEntity;
import workflow.business.model.entity.SaveUndeployedEntity;
import workflow.business.model.entity.UnreleaseProcessEntity;
import workflow.olddata.core.exception.GraphQLException;
import workflow.ide.C2AParser;
import workflow.ide.core.BpmnProcess;
import workflow.ide.core.CallActivity;
import workflow.ide.core.Definitions;
import workflow.ide.utils.XmlUtil;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * 未发布流程数据
 */
@Service("editModelService")
@DS("master")
public class EditModelServiceImpl implements EditModelService {

	@Autowired
	private EditModelMapper stcsmEditModelMapper;

	@Autowired
	private UnreleaseProcessMapper unreleaseProcessMapper;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	EditModelService stcsmEditModelService;

	@Autowired
	private AuthInfo authInfoUtil;//权限系统
	/**
     * 分页查询
     * @param params
     * @return
     */
	@Override
	public Page<EditModelEntity> listStcsmEditModel(Map<String, Object> params) {
		QueryWrapper<EditModelEntity> query = new QueryWrapper<EditModelEntity>();
		if(params.get("process_key")!=null){
			query.like("process_key",params.get("process_key"));
		}
		if(params.get("process_name")!=null){
			query.like("process_name",params.get("process_name"));
		}
		if(params.get("process_type")!=null){
			query.eq("process_type",params.get("process_type"));
		}
		if(params.get("versionJudge")!=null){
			int versionJudge=Integer.parseInt(params.get("versionJudge").toString());
//			System.out.println("===========================versionJudge:"+versionJudge);
			if (versionJudge==0){
				query.eq("release_version",0);
			}else{
				query.gt("release_version",0);
			}
		}
		if(params.get("orderInfo")!=null){
			if(params.get("orderInfo").toString().toLowerCase().indexOf("update_time")>-1){
				if(params.get("orderInfo").toString().toLowerCase().indexOf("desc")>-1){
					query.orderByDesc("update_time");
				} else {
					query.orderByAsc("update_time");
				}
			}else{
				if(params.get("orderInfo").toString().toLowerCase().indexOf("desc")>-1){
					query.orderByDesc("create_time");
				}else {
					query.orderByAsc("create_time");
				}
			}
		}
		Page<EditModelEntity> page = new Page<EditModelEntity>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
		// 设置分页数和页码
		Page<EditModelEntity> list= stcsmEditModelMapper.selectPage(page,query);
		return page;
	}

    /**
     * 新增
     * @param stcsmEditModel
     * @return 添加成功条数
     */
	@Override
	public int saveStcsmEditModel(EditModelEntity stcsmEditModel) {
		int count = stcsmEditModelMapper.save(stcsmEditModel);
		return count;
	}

    /**
     * 根据id查询
     * @param id
     * @return 返回查询到的实体
     */
	@Override
    public EditModelEntity getStcsmEditModelById(String id) {
//		EditModelEntity stcsmEditModel = stcsmEditModelMapper.getObjectById(id);
		QueryWrapper query=new QueryWrapper();
		if(id!=null){
			query.eq("process_id",id);
		}
		EditModelEntity stcsmEditModel =stcsmEditModelMapper.selectOne(query);
		return stcsmEditModel;
	}

	/**
	 * 根据key查询
	 * @param key
	 * @return 返回查询到的实体
	 */
	@Override
	public EditModelEntity getStcsmEditModelByKey(String key) {
//		EditModelEntity stcsmEditModel = stcsmEditModelMapper.getObjectByKey(key);
		System.out.println("===================key:"+key);
		QueryWrapper query=new QueryWrapper();
		if(key!=null){
			query.eq("process_key",key);
		}
		EditModelEntity stcsmEditModel =stcsmEditModelMapper.selectOne(query);
		return stcsmEditModel;
	}

    /**
     * 修改
     * @param stcsmEditModel
     * @return 更新成功条数
     */
	@Override
	public int updateStcsmEditModel(EditModelEntity stcsmEditModel) {
		int count = stcsmEditModelMapper.update(stcsmEditModel);
		return count;
	}

    /**
     * 删除
     * @param id
     * @return 删除成功条数
     */
	@Override
	public int batchRemove(Long[] id) {
		int count = stcsmEditModelMapper.batchRemove(id);
		return count;
	}

	@Override
	public int removeStcsmEditModel(Object id) {
		return stcsmEditModelMapper.remove(id);
	}

	/**
	 * 新增
	 * @param userId 实体类 封装了以下内容
	 * @param definitions
	 * @param processId
	 * @param mode 1: 新增 2：更新
	 * @return 添加成功条数
	 * String userId, Definitions definitions, String processId, int mode 1: 新增 2：更新
	 */

	@Override
	public int saveUndeployedModel(String userId, Definitions definitions, String processId, int mode) {

		System.out.println("--- test");
		/*String userId=saveUndeployedEntity.getUserId();
		Definitions definitions=saveUndeployedEntity.getDefinitions();
		String processId=saveUndeployedEntity.getProcessId();
		int mode=saveUndeployedEntity.getMode();*/
		// 流程
		/*System.out.println("================userId:"+userId);
		System.out.println("================definitions:"+definitions.getActivitiXml().toString());
		System.out.println("================processId:"+processId);
		System.out.println("================mode:"+mode);*/

		BpmnProcess bpmnProcess = definitions.getProcess();

		String processIdNew;

		int releaseVersion = 0;
		if (mode == 1) {
			processIdNew = UUIDUtil.getNextId();

			EditModelEntity entity = stcsmEditModelMapper.getObjectByKey(bpmnProcess.getId());

			if (entity != null) {
				// 不在强制要求编辑还是保存，如果有数据则自动转为编辑
				mode = 2;
				//throw new GraphQLException(601);
			}
		} else {
			processIdNew = processId;

			EditModelEntity entity = stcsmEditModelMapper.getObjectByKey(bpmnProcess.getId());

			// 数据不存在
			if (entity == null) {
				// 不在强制要求编辑还是保存，如果无数据则自动转为新建
				mode = 1;
				//throw new GraphQLException(601);
			}
			releaseVersion = entity.getReleaseVersion();
			// Key 已经被占用
//			if (entity != null && !processId.equals(entity.getProcessKey())) {
//				throw new GraphQLException(601);
//			}

		}
//		stcsmEditModelMapper.

		// 检查Key的合法性

		EditModelEntity stcsmEditModel = new EditModelEntity();
		stcsmEditModel.setProcessId(processIdNew);
		stcsmEditModel.setProcessKey(bpmnProcess.getId());
		stcsmEditModel.setProcessName(bpmnProcess.getName());
		stcsmEditModel.setDescription(bpmnProcess.getDocumentation());
		stcsmEditModel.setProcessType(bpmnProcess.getProcessType());

		stcsmEditModel.setUpdater(userId);

		stcsmEditModel.setUpdateTime(new Date());
		stcsmEditModel.setInfo(definitions.getActivitiXml());
		stcsmEditModel.setThumbnail(definitions.getThumbnail());

		stcsmEditModel.setReleaseVersion(releaseVersion);

		int count;
		// 新增
		if (mode == 1) {
			stcsmEditModel.setCreateTime(new Date());
			stcsmEditModel.setCreater(userId);
			count = stcsmEditModelMapper.save(stcsmEditModel);
		} else {
			count = stcsmEditModelMapper.update(stcsmEditModel);
		}

		unreleaseProcessMapper.batchRemove(new String[]{processIdNew});

		for (CallActivity callActivity : bpmnProcess.getCallActivitys()) {
			String subProcess = callActivity.getCalledElement();

			// 检索子流程的ID
			EditModelEntity subEntity = stcsmEditModelMapper.getObjectByKey(subProcess);

			if (subEntity != null) {
				UnreleaseProcessEntity unreleaseProcessEntity = new UnreleaseProcessEntity();
				unreleaseProcessEntity.setProcessId(processIdNew);
				unreleaseProcessEntity.setProcessKey(stcsmEditModel.getProcessKey());
				unreleaseProcessEntity.setSubId(subEntity.getProcessId());
				unreleaseProcessEntity.setSubKey(subProcess);
				unreleaseProcessMapper.save(unreleaseProcessEntity);
			} else {

			}
		}
		return count;
	}

	/**
	 * 保存未发布流程
	 * @param saveUndeployedEntity
	 * xmlText processKey
	 * @return
	 */
	@Override
	public boolean saveUndeployedModel(@RequestBody SaveUndeployedEntity saveUndeployedEntity) {
		String xmlText=saveUndeployedEntity.getXmlText();
		String processId=saveUndeployedEntity.getProcessId();

		int mode = 2;
		if ("0".equals(processId) || StringUtils.isEmpty(processId)) {
			mode = 1;
		}

		//BpmnModel bpmnModel = C2AParser.convertBpmnModel(xmlText);
		Definitions definitions = C2AParser.convert(XmlUtil.unescape(xmlText));

		if (definitions == null) { // 解析错误
			throw new GraphQLException(603);
		}

		BpmnModel bpmnModel = definitions.getBpmnModel();

		if (bpmnModel == null) {
			throw new GraphQLException(603);
		}

		ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
		InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png",
				Collections.emptyList(), Collections.emptyList(), "宋体", "宋体","宋体",null,1.0);
		String thumbnail = XmlUtil.getBase64FromInputStream(imageStream);

		definitions.setThumbnail(thumbnail);
		UserDepartDto userinf = authInfoUtil.getUserInfo();
		String userId = userinf.getId();
		stcsmEditModelService.saveUndeployedModel(userId, definitions, processId, mode);

		return true;
	}
}
