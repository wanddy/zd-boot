package smartform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.entity.ComponentStateEntity;
import smartform.query.Query;

import java.util.List;


/**
 * @author quhanlin
 * @ClassName: ComponentStateMapper
 * @Description: 用于超级组件状态存储
 * @date 2018年11月1日 下午6:03:23
 */
@Mapper
public interface ComponentStateMapper extends BaseMapper<ComponentStateEntity> {

    int saveContent(@Param("t") ComponentStateEntity t, @Param("dbTable") String dbTable);

    /**
     * 查询列表
     *
     * @param query
     * @return
     */
    List<ComponentStateEntity> list(Query query);
}
