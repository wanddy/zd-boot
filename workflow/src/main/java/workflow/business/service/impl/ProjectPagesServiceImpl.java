package workflow.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartform.form.model.ContentStateType;
import smartform.form.model.FormPage;
import smartform.form.model.SmartForm;
import smartform.form.service.SmartFormService;
import workflow.business.mapper.ProjectPagesMapper;
import workflow.business.model.entity.ProjectPagesEntity;
import workflow.business.service.ProjectPagesService;
import workflow.common.utils.UUIDUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月15日 11:05
 * <p>
 * 业务主体
 */
@Service("ProjectPagesService")
@DS("master")
public class ProjectPagesServiceImpl implements ProjectPagesService {
    @Autowired
    private ProjectPagesMapper projectPagesMapper;
    @Autowired
    private SmartFormService smartFormService;

    @Override
    public Page<FormPage> listProjectPages(Map<String, Object> params) {
        QueryWrapper<ProjectPagesEntity> query = new QueryWrapper<ProjectPagesEntity>();
        if (params.get("contentId") != null) {
            query.eq("content_id", params.get("contentId"));
        }
        String formId = MapUtils.getString(params, "formId");
        if (formId != null) {
            query.eq("form_id", formId);
        }
        Integer pageNumber = MapUtils.getInteger(params, "pageNumber");
        pageNumber = null ==  pageNumber ?1:pageNumber;
        Integer pageSize = MapUtils.getInteger(params, "pageSize");
        pageSize = null ==  pageSize ?10:pageSize;
        Page<ProjectPagesEntity> page = new Page<ProjectPagesEntity>(pageNumber, pageSize);
        // 设置分页数和页码
        Page<ProjectPagesEntity> list = projectPagesMapper.selectPage(page, query);
        List<ProjectPagesEntity> ProjectPagesEntityList = list.getRecords();
       List<String> pageIdList = ProjectPagesEntityList.stream().map(ProjectPagesEntity::getPageId).collect(Collectors.toList());

        SmartForm smartForm = smartFormService.smartForm(formId, true);
        Page<FormPage> foprmPage = new Page();
        if (null != smartForm) {
            List<FormPage> pageList = smartForm.getPageList();
            // 使用Map存储对应pageCode-FormPage
            // 使用Map存储对应pageId-FormPage
            Map<String, FormPage> pageCodeFromEntity = pageList.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
            for (String pageId : pageIdList) {
                if (pageCodeFromEntity.get(pageId) != null) {
                    FormPage formPage = pageCodeFromEntity.get(pageId);
                    formPage.setFillState(ContentStateType.SAVE.value);
                }
            }
            List<FormPage> formPages = pageCodeFromEntity.values().stream().sorted(Comparator.comparing(FormPage::getSort)).collect(Collectors.toList());
            formPages.forEach(v->v.setFormID(formId));
            foprmPage.setRecords(formPages);
            foprmPage.setCurrent(pageNumber);
            foprmPage.setSize(pageSize);
            foprmPage.setTotal(formPages.size());
        }


        return foprmPage;
    }


    @Override
    public int saveProjectPages(ProjectPagesEntity projectPages) {
        QueryWrapper<ProjectPagesEntity> query = new QueryWrapper<>();
        if(null != projectPages.getFormId()&&null != projectPages.getPageId()&&null != projectPages.getContentId()){
            query.eq("form_id",projectPages.getFormId());
            query.eq("page_id",projectPages.getPageId());
            query.eq("content_id",projectPages.getContentId());
        }
        ProjectPagesEntity entity = projectPagesMapper.selectOne(query);
        projectPages.setModifiedAt(System.currentTimeMillis());
        if(null != entity){
            return projectPagesMapper.updateById(entity);
        }else {
            String newid = UUIDUtil.getNextId();
            projectPages.setId(newid);
            projectPages.setModifiedAt(System.currentTimeMillis());
            projectPages.setCreatedAt(System.currentTimeMillis());
            projectPages.setPageStatus("1");
            return projectPagesMapper.insert(projectPages);
        }
    }

    @Override
    public ProjectPagesEntity getProjectPagesById(String id) {
        QueryWrapper query = new QueryWrapper();
        if (id != null) {
            query.eq("id", id);
        }
        ProjectPagesEntity projectMain = projectPagesMapper.selectOne(query);
        return projectMain;
    }

    @Override
    public int batchRemove(Long[] id) {
        int count = projectPagesMapper.batchRemove(id);
        return count;
    }

    @Override
    public int removeProjectPagesbyContentId(Object contentid) {
        int count = projectPagesMapper.removebycontentid(contentid);
        return count;
    }

    @Override
    public int removeProjectPages(Object id) {
        int count = projectPagesMapper.remove(id);
        return count;
    }
}
