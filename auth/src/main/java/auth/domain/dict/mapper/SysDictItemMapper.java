package auth.domain.dict.mapper;

import auth.entity.Category;
import org.apache.ibatis.annotations.Select;
import auth.entity.DictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface SysDictItemMapper extends BaseMapper<DictItem> {
    @Select("SELECT * FROM sys_dict_item WHERE DICT_ID = #{mainId} order by sort_order asc, item_value asc")
    public List<DictItem> selectItemsByMainId(String mainId);

    List<Category> getPid(String id);
}
