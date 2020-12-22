package workflow.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import workflow.business.model.entity.ProjectMainEntity;

import java.util.List;
import java.util.Map;

/**
 * 未发布流程数据
 */
@Mapper
public interface ProjectMainMapper extends BaseMapper<ProjectMainEntity> {

    List<ProjectMainEntity> listForPage(Page<ProjectMainEntity> page,@Param("map") Map<String, Object> map);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    ProjectMainEntity getObjectById(String id);

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
    int update(ProjectMainEntity t);

}
