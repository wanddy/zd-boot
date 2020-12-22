package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.UnreleaseProcessEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//import framework.orm.mapper.BaseMapper;
//import framework.entity.Query;

/**
 * 未发布流程关联数据
 */
@Mapper
public interface UnreleaseProcessMapper extends BaseMapper<UnreleaseProcessEntity> {
    /**
     * 分页查询列表
     * @param page
     * @param query
     * @return
     */
    List<UnreleaseProcessEntity> listForPage(Page<UnreleaseProcessEntity> page, Query query);

    /**
     * 新增
     * @param t
     * @return
     */
    int save(UnreleaseProcessEntity t);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    UnreleaseProcessEntity getObjectById(Long id);

    /**
     * 批量删除
     * @param id
     * @return
     */
    int batchRemove(Object[] id);

    /**
     * 删除
     * @param id
     * @return
     */
    int remove(Object id);

    /**
     * 更新
     * @param t
     * @return
     */
    int update(UnreleaseProcessEntity t);
}
