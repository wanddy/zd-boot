package workflow.business.service.impl;

import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workflow.business.mapper.ProcessEntryCategoryMapper;
import workflow.business.model.entity.ProcessEntryCategory;
import workflow.business.service.ProcessEntryCategoryService;
import workflow.common.utils.UUIDUtil;

import java.util.Map;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月15日 11:05
 *
 * 业务主体
 */
@Service("ProcessEntryCategoryService")
@DS("master")
public class ProcessEntryCategoryServiceImpl implements ProcessEntryCategoryService {
    @Autowired
    private ProcessEntryCategoryMapper processEntryCategoryMapper;
    @Autowired
    private AuthInfo authInfoUtil;


    @Override
    public Page<ProcessEntryCategory> listProcessEntryCategory(Map<String, Object> params) {
        QueryWrapper<ProcessEntryCategory> query = new QueryWrapper<ProcessEntryCategory>();
        if(params.get("state")!=null){
            query.eq("state",params.get("state"));
        }
        query.orderByAsc("sort");
        Page<ProcessEntryCategory> page = new Page<ProcessEntryCategory>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
        // 设置分页数和页码
        Page<ProcessEntryCategory> list= processEntryCategoryMapper.selectPage(page,query);
        return page;
    }

    @Override
    public int saveProcessEntryCategory(ProcessEntryCategory processEntryCategory) {
        String newid = UUIDUtil.getNextId();
        //已存在 则修改
        if(processEntryCategory.getId()!=null){
            ProcessEntryCategory oldprocessEntryCategory=processEntryCategoryMapper.getObjectById(processEntryCategory.getId());
            if(oldprocessEntryCategory!=null){
                processEntryCategory.setModifiedAt(System.currentTimeMillis());
                try {
                    processEntryCategoryMapper.update(processEntryCategory);
                }catch (Exception e) {
                    e.printStackTrace();
                    return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
                }
                return HttpResponseStatus.OK.getCode();
            }else{
                newid = processEntryCategory.getId();
            }
        }
        processEntryCategory.setId(newid);
        processEntryCategory.setModifiedAt(System.currentTimeMillis());
        processEntryCategory.setCreatedAt(System.currentTimeMillis());
        processEntryCategory.setState(1);
        try {
            processEntryCategoryMapper.insert(processEntryCategory);
        } catch (Exception e) {
            e.printStackTrace();
            return  HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
        }
        return HttpResponseStatus.OK.getCode();
    }

    @Override
    public ProcessEntryCategory getProcessEntryCategoryById(String id) {
        QueryWrapper query=new QueryWrapper();
        if(id!=null){
            query.eq("id",id);
        }
        ProcessEntryCategory processEntryCategory = processEntryCategoryMapper.selectOne(query);
        return processEntryCategory;
    }

    @Override
    public int updateProcessEntryCategory(ProcessEntryCategory projectMain) {
        return processEntryCategoryMapper.update(projectMain);
    }

    @Override
    public int batchRemove(Long[] id) {
        return processEntryCategoryMapper.batchRemove(id);
    }

    @Override
    public int removeProcessEntryCategory(Object id) {
        return processEntryCategoryMapper.remove(id);
    }
}
