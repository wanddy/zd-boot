package workflow.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.EditModelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 未发布流程数据
 */
@Mapper
public interface EditModelMapper extends BaseMapper<EditModelEntity> {

    // 根据Key检索对象
    EditModelEntity getObjectByKey(Object id);

//    List<EditModelEntity> listForPage(Page page, Query query);

    List<EditModelEntity> listForPage(Page<EditModelEntity> page,@Param("map") Map<String, Object> map);
    /**
     * 新增
     * @param t
     * @return
     */
    int save(EditModelEntity t);
    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    EditModelEntity getObjectById(String id);

    /**
     * 批量删除
     * @param id
     * @return
     */
    int batchRemove(Long[] id);

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
    int update(EditModelEntity t);

}
