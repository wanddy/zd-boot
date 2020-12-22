package smartform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.Group;

import java.util.HashMap;
import java.util.List;

/**
 * 组
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 根据id查询超级组件简单信息
     *
     * @param id
     * @return
     */
    Group getSimpleInfoById(@Param("id") String id);

    /**
     * 批量删除
     *
     * @param id
     * @return
     */
    int batchRemove(Object[] id);

    /**
     * 分页查询列表
     *
     * @return
     */
    List<Group> listForPage(@Param("page") Page<Group> page, @Param("query") HashMap query);
}
