package smartform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.SmartFormContent;
import smartform.query.Query;

import java.util.List;
import java.util.Map;

/**
 * @author quhanlin
 * @ClassName: FormContentMainMapper
 * @Description: 用于表单内容主表存储
 * @date 2018年11月1日 下午6:03:23
 */
@Mapper
public interface FormContentMainMapper extends BaseMapper<SmartFormContent> {

    /**
     * 保存表单内容主表数据
     *
     * @param t
     * @param dbTable
     * @param content
     * @return
     */
    int saveContent(@Param("data") SmartFormContent data, @Param("dbTable") String dbTable, @Param("content") Query content);

    /**
     * 查询表单内容主表简单数据
     *
     * @param dbTable
     * @param id
     * @param column
     * @return
     */
    Map<String, Object> getContent(@Param("dbTable") String dbTable, @Param("id") String id, @Param("columns") List<String> columns);

    /**
     * 查询表单内容主表简单数据
     *
     * @param dbTable
     * @param id
     * @param columns
     * @param extraData
     * @return
     */
    Map<String, Object> getContentQuery(@Param("dbTable") String dbTable, @Param("formId") String formId,
                                        @Param("userId") String userId, @Param("columns") List<String> columns,
                                        @Param("extraData") Map<String, Object> extraData);

    /**
     * 编辑表单内容主表数据
     *
     * @param t
     * @param dbTable
     * @param content
     * @return
     */
    int updateContent(@Param("data") SmartFormContent data, @Param("dbTable") String dbTable, @Param("content") Query content);


    /**
     * 自定义查询
     *
     * @param select
     * @param contentId
     * @return
     */
    List<Object> customSelect(@Param("select") List<String> select, @Param("contentId") String contentId);
}
