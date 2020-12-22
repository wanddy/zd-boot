package smartform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.FormCategory;

import java.util.List;

/**
 * 表单分类表
 */
@Mapper
public interface FormCategoryMapper extends BaseMapper<FormCategory> {
    /**
     * 使用code查询分类信息
     *
     * @param codes
     * @return
     */
    List<FormCategory> categoryList(@Param("codes") List<String> codes);

    /**
     * 批量删除
     *
     * @param id
     * @return
     */
    int batchRemove(@Param("id") Object[] id);
}
