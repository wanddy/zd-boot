package workflow.business.service.impl;
import auth.domain.common.service.AuthInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workflow.business.mapper.ProcessEntryMainMapper;
import workflow.business.model.entity.ProcessEntryMainEntity;
import workflow.business.service.*;
import workflow.common.utils.UUIDUtil;


import java.util.Map;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年09月15日 11:05
 *
 * 业务主体
 */
@Service("ProcessEntryMainService")
@DS("master")
public class ProcessEntryMainServiceImpl implements ProcessEntryMainService {
    @Autowired
    private ProcessEntryMainMapper processEntryMainMapper;
    @Autowired
    private AuthInfo authInfoUtil;


    @Override
    public Page<ProcessEntryMainEntity> listProcessEntryMain(Map<String, Object> params) {
        QueryWrapper<ProcessEntryMainEntity> query = new QueryWrapper<ProcessEntryMainEntity>();
        if(params.get("categoryId")!=null){
            query.eq("category_id",params.get("categoryId"));
        }
        if(params.get("state")!=null){
            query.eq("state",params.get("state"));
        }
        if(params.get("commonly")!=null){
            query.eq("commonly",params.get("commonly"));
        }
        query.orderByDesc("created_at");
        Page<ProcessEntryMainEntity> page = new Page<ProcessEntryMainEntity>(Integer.parseInt(params.get("pageNumber").toString()) ,Integer.parseInt(params.get("pageSize").toString()));
        // 设置分页数和页码
        Page<ProcessEntryMainEntity> list= processEntryMainMapper.selectPage(page,query);
        return page;
    }

    @Override
    public int saveProcessEntryMain(ProcessEntryMainEntity processEntryMainEntity) {
       String newid = UUIDUtil.getNextId();
       //已存在 则修改
       if(processEntryMainEntity.getId()!=null){
           ProcessEntryMainEntity oldprocessEntryMainEntity=processEntryMainMapper.getObjectById(processEntryMainEntity.getId());
           if(oldprocessEntryMainEntity!=null){
               processEntryMainEntity.setModifiedAt(System.currentTimeMillis());
               try {
                   processEntryMainMapper.update(processEntryMainEntity);
               }catch (Exception e) {
                   e.printStackTrace();
                   return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
               }
               return HttpResponseStatus.OK.getCode();
           }else{
               newid = processEntryMainEntity.getId();
           }
       }

        processEntryMainEntity.setId(newid);
        processEntryMainEntity.setModifiedAt(System.currentTimeMillis());
        processEntryMainEntity.setCreatedAt(System.currentTimeMillis());
        processEntryMainEntity.setState(1);
        try {
            processEntryMainMapper.insert(processEntryMainEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return  HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
        }
        return HttpResponseStatus.OK.getCode();
    }


    @Override
    public ProcessEntryMainEntity getProcessEntryMainById(String id) {
        QueryWrapper query=new QueryWrapper();
        if(id!=null){
            query.eq("id",id);
        }
        ProcessEntryMainEntity processEntryMainEntity = processEntryMainMapper.selectOne(query);
        return processEntryMainEntity;
    }

    @Override
    public int updateProcessEntryMain(ProcessEntryMainEntity projectMain) {
        int count = processEntryMainMapper.update(projectMain);
        return count;
    }

    @Override
    public int batchRemove(Long[] id) {
        int count = processEntryMainMapper.batchRemove(id);
        return count;
    }

    @Override
    public int removeProcessEntryMain(Object id) {
        int count = processEntryMainMapper.remove(id);
        return count;
    }
}
