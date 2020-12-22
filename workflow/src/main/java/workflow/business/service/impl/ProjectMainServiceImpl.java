package workflow.business.service.impl;

import auth.domain.common.dto.UserDepartDto;
import auth.domain.common.service.AuthInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import smartform.common.util.DBCutConstants;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import smartform.form.mapper.SmartFormMapper;
import smartform.form.model.*;
import smartform.form.service.SmartFormService;
import smartform.form.model.FormPage;
import smartform.form.model.FormPageContent;
import workflow.business.mapper.ProjectMainMapper;
import workflow.business.mapper.ProjectPagesMapper;
import workflow.business.model.*;
import workflow.business.model.entity.ProjectMainEntity;
import workflow.business.model.entity.ProjectPagesEntity;
import workflow.business.model.ProjectMainInputEntity;
import workflow.business.service.*;
import workflow.common.constant.ActivitiConstant;
import workflow.common.error.WorkFlowException;
import workflow.common.utils.CheckDataUtil;
import workflow.common.utils.UUIDUtil;
import workflow.common.utils.WorkflowUtil;

import java.util.*;
import java.text.ParseException;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月15日 11:05
 *
 * 业务主体
 */
@Service("ProjectMainService")
@DS("master")
public class ProjectMainServiceImpl implements ProjectMainService {
    @Autowired
    private ProjectMainMapper projectMainMapper;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    private ProjectPagesMapper projectPagesMapper;
    @Autowired
    SmartFormMapper smartFormMapper;
    @Autowired
    SmartFormService smartFormService;
    @Autowired
    ProjectPagesService projectPagesService;
    @Autowired
    private AuthInfo authInfoUtil;
    @Autowired
    private ActiveTaskService activeTaskService;
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Page<ProjectMainEntity> listProjectMain(Map<String, Object> params) {
        QueryWrapper<ProjectMainEntity> query = new QueryWrapper<ProjectMainEntity>();
        if(params.get("projectType")!=null){
            query.eq("project_type",params.get("projectType"));
        }
        if(params.get("categoryId")!=null){
            query.eq("category_id",params.get("categoryId"));
        }
        if(params.get("projectName")!=null){
            query.like("project_name",params.get("projectName"));
        }
        if(params.get("userId")!=null){
            query.eq("user_id",params.get("userId"));
        }
        if(params.get("state")!=null){
            query.eq("state",params.get("state"));
        }
        query.orderByDesc("created_at");
        Page<ProjectMainEntity> page = new Page<ProjectMainEntity>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
        // 设置分页数和页码
        Page<ProjectMainEntity> list= projectMainMapper.selectPage(page,query);
        return page;
    }

    @Override
    public int saveProjectMain(ProjectMainEntity projectMain) {
        String newid = UUIDUtil.getNextId();
        if(null == projectMain.getProcessKey() && null ==  projectMain.getFormId()){
            return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
        }

        //对业务库进行修改
        if(projectMain.getId()!=null){
            ProjectMainEntity hasprojectMain = projectMainMapper.getObjectById(projectMain.getId());
            if(hasprojectMain!=null){
                projectMain.setModifiedAt(System.currentTimeMillis());
                try {
                    projectMainMapper.update(projectMain);
                } catch (Exception e) {
                    e.printStackTrace();
                    return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
                }
                return HttpResponseStatus.OK.getCode();
            }else{
                newid = projectMain.getId();
            }
        }

        // 1 表示于工作流进行绑定
        if(projectMain.getProjectType().equals("1") || projectMain.getProjectType().equals("3") ){
            if(null != projectMain.getProcessKey()){
                try {
                    TaskContentData contentData = new TaskContentData();
                    contentData.setContentId(newid);
                    contentData.setTaskNo(projectMain.getProcessKey());
                    contentData.setContentName(projectMain.getProjectName());
                    ActiveTask activeTask = workflowService.startProcessInstanceByKey(projectMain.getUserId(),projectMain.getProcessKey(),new HashMap<>(),null,contentData);
                    projectMain.setProcessKey(activeTask.getProcessKey());
                    projectMain.setProcessDefinitionId(activeTask.getProcessDefinitionId());
                    System.out.println("=========activeTask.getProcessInstanceId():"+activeTask.getProcessInstanceId());
                    projectMain.setProcessInstanceId(activeTask.getProcessInstanceId());
                } catch (WorkFlowException e) {
                    e.printStackTrace();
                    return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
                }
            }else {
                return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
            }
        }
        projectMain.setId(newid);
        projectMain.setModifiedAt(System.currentTimeMillis());
        projectMain.setCreatedAt(System.currentTimeMillis());
        projectMain.setAuditState("1");
        projectMain.setProjectStatus("1");
        projectMain.setState("1");
        try {
            projectMainMapper.insert(projectMain);
        } catch (Exception e) {
            e.printStackTrace();
          return  HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
        }
        return HttpResponseStatus.OK.getCode();
    }

    @Override
    public ProjectMainEntity saveProjectMain1(ProjectMainInputEntity projectMainInputEntity) throws ParseException {
        ProjectMainEntity projectMain = projectMainInputEntity.getProjectMainEntity();
        String data = projectMainInputEntity.getData();
        boolean isStorage =projectMainInputEntity.isStorage();
        String newid = UUIDUtil.getNextId();
        System.out.println("======本次新建contentID："+newid);
        if(null == projectMain.getProcessKey() && null ==  projectMain.getFormId()){
            return null;
        }

        //对业务库进行修改
        if(projectMain.getId()!=null){
            ProjectMainEntity hasprojectMain = projectMainMapper.getObjectById(projectMain.getId());
            if(hasprojectMain!=null){
                projectMain.setModifiedAt(System.currentTimeMillis());
                try {
                    projectMainMapper.update(projectMain);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return hasprojectMain;
            }else{
                newid = projectMain.getId();
            }
        }

        // 1 表示于工作流进行绑定
        if(projectMain.getProjectType().equals("1") || projectMain.getProjectType().equals("3") ){
            if(null != projectMain.getProcessKey()){
                try {
                    TaskContentData contentData = new TaskContentData();
                    contentData.setContentId(newid);
                    contentData.setTaskNo(projectMain.getProcessKey());
                    contentData.setContentName(projectMain.getProjectName());

                    Map<String, Object> values =new HashMap<>();
                    values.put("starter",projectMain.getUserId());
                    values.put("form_form001","4ef4d90a1f3e43d3b30bf9a1cba6b10f");
                    ActiveTask activeTask = workflowService.startProcessInstanceByKey(projectMain.getUserId(),projectMain.getProcessKey(),values,null,contentData);
                    // 完成任务的参数
                    String prefixs = WorkflowUtil.TASK_JUDGE_HEAD;
                    List<String> taskParams = foreachJudgeParams(prefixs, activeTask.getJudgePropertyList());

                    UserDepartDto userinfo = authInfoUtil.getUserById(projectMain.getUserId()).get(0);
                    if(userinfo==null){
                        return null;
                    }
                    String userName = userinfo.getName();
                    String deptName = userinfo.getDepartName();
                    List<String> unitIds = new ArrayList<String>();
                    if(userinfo.getDepartId()==null || userinfo.getDepartId().length()==0){
                        unitIds=null;
                    }else{
                        unitIds.add(userinfo.getDepartId());
                    }
                    Map<String, String> actvalues = new HashMap<>();
                    // 设置参数
                    actvalues = setOperateParams("submit", taskParams, prefixs);
                    actvalues.put("taskuser_director",projectMain.getUserId());
                    values.put("form_smartforminput01","4ef4d90a1f3e43d3b30bf9a1cba6b10f");
                    try{
                        Boolean flag_com=workflowService.complateTask(projectMain.getUserId(),userName, deptName , "submit","提交申请",activeTask,"", unitIds , activeTask.getTaskId(), actvalues,null,null,null,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    projectMain.setProcessKey(activeTask.getProcessKey());
                    projectMain.setProcessDefinitionId(activeTask.getProcessDefinitionId());
                    projectMain.setProcessInstanceId(activeTask.getProcessInstanceId());
                } catch (WorkFlowException e) {
                    e.printStackTrace();
                    return null;
                }
            }else {
                return null;
            }
        }
        projectMain.setId(newid);
        projectMain.setModifiedAt(System.currentTimeMillis());
        projectMain.setCreatedAt(System.currentTimeMillis());
        projectMain.setAuditState("1");
        projectMain.setProjectStatus("1");
        projectMain.setState("1");
        try {
            projectMainMapper.insert(projectMain);
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
        try{
            if(data!=null && data.length()>0){
                FormPageContent content = JSON.parseObject(data, FormPageContent.class);
                content.setContentId(newid);
                JSONObject jsonss= JSON.parseObject(data);
                jsonss.put("contentId",newid);
                String newdata = jsonss.toString();
                FormPage formpage = smartFormService.saveFormPage(newdata,isStorage);
                ProjectPagesEntity projectMain_new = new ProjectPagesEntity();
                projectMain_new.setContentId(newid);
                projectMain_new.setFormId(content.getFormId());
                projectMain_new.setPageId(content.getPageId());
                projectPagesService.saveProjectPages(projectMain_new);
            }
        } catch (Exception e) {
            System.out.println("" + e);
            e.printStackTrace();
            return null;
        }
        return projectMain;
    }

    //获取判断结点名称
    public List<String> foreachJudgeParams(String prefix, List<JudgeProperty> properties) {
        if (properties == null || properties.size() < 1) {
            return null;
        }
        if (StringUtils.isEmpty(prefix)) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        for (JudgeProperty item : properties) {
            if (item.getId().startsWith(prefix)) {
                list.add(item.getId());
            }
        }
        return list;
    }

    /**
     * 设置工作流任务参数
     *
     * @param businessOperate
     * @param taskParams
     * @return
     */
    private Map<String, String> setOperateParams(String businessOperate, List<String> taskParams, String type) {
        if (taskParams == null || taskParams.size() < 1) {
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        // 设置参数 业务参数目前只有一个参数 0/1/2
        if (taskParams != null && taskParams.size() > 0) {
            params.put(taskParams.get(0), businessOperate);
        }
        return params;
    }

    @Override
    public ProjectMainEntity getProjectMainById(String id) {
        QueryWrapper query=new QueryWrapper();
        if(id!=null){
            query.eq("id",id);
        }
        ProjectMainEntity projectMain = projectMainMapper.selectOne(query);
        return projectMain;
    }

    @Override
    public int updateProjectMain(ProjectMainEntity projectMain) {
        int count = projectMainMapper.update(projectMain);
        return count;
    }

    @Override
    public int batchRemove(Long[] id) {
        int count = projectMainMapper.batchRemove(id);
        return count;
    }

    @Override
    public int removeProjectMain(Object id) {
        int count = projectMainMapper.remove(id);
        return count;
    }

    @Override
    public List<FormPage> selectSmartFormPageAll(String formId, String contentId) {
        QueryWrapper<SmartForm> query = new QueryWrapper<>();
        QueryWrapper<ProjectPagesEntity> queryWrapper = new QueryWrapper<>();
        if (contentId != null) {
            queryWrapper.eq("content_id", contentId);
        }
        if (formId != null) {
            query.eq("id", formId);
            queryWrapper.eq("form_id", formId);
        }
        DynamicDataSourceContextHolder.push(DBCutConstants.SMART_FORM);
        // 获取表单
        SmartForm smartForm = smartFormService.smartForm(formId, false);
        // 获取表单组件列表
        List<FormPage> smartFormList = smartForm.getPageList();
        DynamicDataSourceContextHolder.poll();
        // 获取状态已填写的表单信息
        List<ProjectPagesEntity> ProjectPagesEntityList = projectPagesMapper.selectList(queryWrapper);
        //拿到pageId
        List<String> pageIdList = ProjectPagesEntityList.stream().map(ProjectPagesEntity::getPageId).collect(Collectors.toList());
        //  保存返回的数据
        List<FormPage> formPageList = new ArrayList<>();
        for (int i = 0; i < smartFormList.size(); i++) {
            smartFormService.findPageContentById(smartForm,smartFormList.get(i),contentId);
        }
        for (FormPage formPage : smartFormList) {
            String pageId = formPage.getId();
            for (String pageIds : pageIdList) {
                if( pageId.equals(pageIds)) formPage.setFillState(ContentStateType.SAVE.value);
            }
            formPageList.add(formPage);

        }
        return formPageList;
    }

    @Override
    public Object getProjectActHisList(String contentId,String processInstanceId) throws WorkFlowException {
        System.out.println("=============传入参数contentId："+contentId);
        System.out.println("=============传入参数processInstanceId："+processInstanceId);
        CheckDataUtil.checkNull(contentId, "contentId");
        CheckDataUtil.checkNull(processInstanceId, "processInstanceId");
        //获取实例全部节点
        List<TaskDefData> taskdefList = workflowService.getProcessInfoByProcessInstId(processInstanceId);
        //创建新的list列表 用于存储返回内容
        List<TaskDefDataActHis> taskDefDataActHisList = new ArrayList<>();
        //获取log表中已办任务
        AuditLog auditLog = new AuditLog();
        auditLog.setContentId(contentId);
        auditLog.setProcessInstanceId(processInstanceId);
        List<AuditLog> auditLogList = auditLogService.listAll(auditLog);
        //获取active_task表中代办任务
        ActiveTask activeTask=new ActiveTask();
        activeTask.setProcessInstanceId(processInstanceId);
        activeTask.setContentId(contentId);
        List<ActiveTask> activeTaskList=activeTaskService.listAll(activeTask);
        if (auditLogList != null && auditLogList.size() > 0) {
            for (int i = 0; i < auditLogList.size(); i++) {
                AuditLog audit = auditLogList.get(i);
                TaskDefDataActHis taskDefDataActHis = new TaskDefDataActHis();
                taskDefDataActHis.setState("2");//已办任务
                taskDefDataActHis.setId(audit.getTaskDefId());
                taskDefDataActHis.setName(audit.getBusinessNode());
                String[] taskid_split = audit.getTaskDefId().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
                taskDefDataActHis.setSort(Integer.parseInt(taskid_split[1]));
                taskDefDataActHis.setMemo(audit.getMemo() == null ? "通过" : audit.getMemo());
                taskDefDataActHis.setOperate(audit.getBusinessOperate() == null ? "pass" : audit.getBusinessOperate());
                taskDefDataActHis.setUnitname(audit.getUnitName() == null ? "" : audit.getUnitName());
                taskDefDataActHis.setUsername(audit.getUserName() == null ? "" : audit.getUserName());
                taskDefDataActHis.setOperatetime(audit.getOperateTime() == null ? null : audit.getOperateTime());
                taskDefDataActHisList.add(taskDefDataActHis);
            }
        }
        if(activeTaskList!=null && activeTaskList.size()>0){
            for (ActiveTask activeTask1:activeTaskList) {
                TaskDefDataActHis taskDefDataActHis = new TaskDefDataActHis();
                taskDefDataActHis.setState("1");
                taskDefDataActHis.setId(activeTask1.getTaskDefId());
                taskDefDataActHis.setName(activeTask1.getTaskName());
                String[] taskid_split = activeTask1.getTaskDefId().split(ActivitiConstant.FORM_PROPERTY_KEY_SPAN);
                taskDefDataActHis.setSort(Integer.parseInt(taskid_split[1]));
                String username=activeTask1.getAssignee();
                List<UserDepartDto> userlist=authInfoUtil.getUserById(activeTask1.getAssignee());
                if(userlist!=null && userlist.size()>0){
                    UserDepartDto userinfo=userlist.get(0);
//                    System.out.println("=======userinfo.getName():"+userinfo.getName());
                    username=userinfo.getName()==null?activeTask1.getAssignee():userinfo.getName();
                }
                taskDefDataActHis.setUsername(username);
                taskDefDataActHisList.add(taskDefDataActHis);
            }
        }
        if(taskDefDataActHisList.size()>0){
            List<TaskDefData> taskdefListnew=taskdefList;
            for (int i=0; i<taskDefDataActHisList.size() ;i++) {
                TaskDefDataActHis taskDefDataActHis =taskDefDataActHisList.get(i);
                for (int j=0; j<taskdefList.size() ;j++) {
                    TaskDefData taskdefdata =taskdefList.get(j);
                    if(taskdefdata.getId().equals(taskDefDataActHis.getId())){
                        taskdefListnew.remove(taskdefdata);
                    }
                }
            }
            if(taskdefListnew.size()>0){
                for (int i = 0; i < taskdefListnew.size(); i++) {
                    TaskDefData taskdefdata =taskdefListnew.get(i);
                    TaskDefDataActHis taskDefDataActHis = new TaskDefDataActHis();
                    taskDefDataActHis.setId(taskdefdata.getId());
                    taskDefDataActHis.setName(taskdefdata.getName());
                    taskDefDataActHis.setSort(taskdefdata.getSort());
                    taskDefDataActHis.setState("0");
                    taskDefDataActHisList.add(taskDefDataActHis);
                }
            }
        }else{
            for (TaskDefData taskdefdata :taskdefList) {
                TaskDefDataActHis taskDefDataActHis = new TaskDefDataActHis();
                taskDefDataActHis.setId(taskdefdata.getId());
                taskDefDataActHis.setName(taskdefdata.getName());
                taskDefDataActHis.setSort(taskdefdata.getSort());
                taskDefDataActHis.setState("0");
                taskDefDataActHisList.add(taskDefDataActHis);
            }
        }
        return taskDefDataActHisList;
    }

}
