package smartform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.SmartForm;

import java.util.List;

/**
 * 表单
 */
@Mapper
public interface SmartFormMapper extends BaseMapper<SmartForm> {

    /**
     * 根据id查询表单简单信息
     *
     * @return
     */
    SmartForm getSimpleInfoById(@Param("id") String id);

    /**
     * 根据id列表查询表单简单信息
     *
     * @return
     */
    List<SmartForm> getSimpleInfoByIds(@Param("ids") List<String> ids);

    /**
     * 批量删除
     *
     * @return
     */
    int batchRemove(Object[] id);

    /**
     * 新增
     *
     * @return
     */
    int save(SmartForm smartForm);

    /**
     * 根据id查询详情
     *
     * @return
     */
    SmartForm getObjectById(@Param("id") String id);
}
