package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.ReleaseProcessEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//import framework.orm.mapper.BaseMapper;

/**
 * 已发布流程关联数据
 */
@Mapper
public interface ReleaseProcessMapper extends BaseMapper<ReleaseProcessEntity> {
    /**
     * 分页查询列表
     * @param page
     * @param query
     * @return
     */
    List<ReleaseProcessEntity> listForPage(Page<ReleaseProcessEntity> page, Query query);

    /**
     * 新增
     * @param t
     * @return
     */
    int save(ReleaseProcessEntity t);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    ReleaseProcessEntity getObjectById(Long id);
    /**
     * 批量删除
     * @param id
     * @return
     */
    int batchRemove(Long[] id);

    /**
     * 更新
     * @param t
     * @return
     */
    int update(ReleaseProcessEntity t);


}
