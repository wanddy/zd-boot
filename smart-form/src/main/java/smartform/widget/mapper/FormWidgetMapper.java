package smartform.widget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import smartform.query.Query;
import smartform.widget.model.WidgetBase;

import java.util.List;

/**
 * 预设控件表
 */
@Mapper
public interface FormWidgetMapper extends BaseMapper<WidgetBase> {

    /**
     * 分页查询列表
     * @param page
     * @param query
     * @return
     */
    List<WidgetBase> listForPage(Page<WidgetBase> page, Query query);

    /**
     * 批量删除
     * @param id
     * @return
     */
    int batchRemove(Object[] id);

}
