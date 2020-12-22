package smartform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.entity.FormContentTableEntity;
import smartform.query.Query;

import java.util.List;
import java.util.Map;

/**
 * @author quhanlin
 * @ClassName: FormContentTableMapper
 * @Description: 用于表单表格表存储
 * @date 2018年11月1日 下午6:03:23
 */
@Mapper
public interface FormContentTableMapper extends BaseMapper<FormContentTableEntity> {

    /**
     * 保存表单内容表格数据
     *
     * @param t
     * @param dbTable
     * @param content
     * @return
     */
    int saveContent(@Param("data") FormContentTableEntity data, @Param("dbTable") String dbTable,
                    @Param("content") Query content);

    /**
     * 编辑表单内容表格数据
     *
     * @param t
     * @param dbTable
     * @param content
     * @return
     */
    int updateContent(@Param("data") FormContentTableEntity data, @Param("dbTable") String dbTable,
                      @Param("content") Query content);

    /**
     * @param data
     * @param dbTable
     * @return int    返回类型
     * @Title: updateContentSort
     * @Description: 更新行排序值
     */
    int updateContentSort(@Param("data") FormContentTableEntity data, @Param("dbTable") String dbTable);

    /**
     * 查询表单内容表格简单数据
     *
     * @param dbTable
     * @param id
     * @param column
     * @return
     */
    Map<String, Object> getContent(@Param("dbTable") String dbTable, @Param("contentId") String contentId,
                                   @Param("workType") Integer workType, @Param("lineId") String lineId,
                                   @Param("columns") List<String> columns);

    /**
     * 列表获取内容表格
     *
     * @param dbTable
     * @param id
     * @param columns
     * @param query
     * @return
     */
    List<Map<String, Object>> getContentList(@Param("dbTable") String dbTable, @Param("contentId") String contentId,
                                             @Param("workType") Integer workType, @Param("columns") List<String> columns);

    /**
     * 分页查询列表
     *
     * @param page
     * @param query
     * @return
     */
    List<Map<String, Object>> getContentListPage(@Param("page") Page<Map<String, Object>> page,
                                                 @Param("dbTable") String dbTable, @Param("contentId") String contentId,
                                                 @Param("workType") Integer workType, @Param("columns") List<String> columns);

    /**
     * 批量添加表格数据
     *
     * @param data
     * @param dbTable
     * @return
     */
    int batchSave(@Param("data") List<FormContentTableEntity> data, @Param("dbTable") String dbTable);

    /**
     * 按表单内容批量删除表格数据
     *
     * @param dbTable
     * @param query
     * @return
     */
    int removeContent(@Param("dbTable") String dbTable, @Param("contentId") String contentId,
                      @Param("workType") Integer workType, @Param("lineId") String lineId);

    /**
     * 获取当前存在的固定行,目前只返回id列表
     *
     * @param dbTable
     * @param contentId
     * @param fixedId
     * @return
     */
    List<String> getFixedLine(@Param("dbTable") String dbTable, @Param("contentId") String contentId,
                              @Param("workType") Integer workType, @Param("fixedId") List<String> fixedId);

    /**
     * 统计行数，安装状态统计行数，查询固定行
     *
     * @param dbTable
     * @param contentId
     * @param fixedId
     * @param state
     * @return
     */
    List<String> countTotals(@Param("dbTable") String dbTable, @Param("contentId") String contentId,
                             @Param("workType") Integer workType, @Param("fixedId") List<String> fixedId,
                             @Param("state") Integer state);

    /**
     * 统计
     *
     * @param query
     * @return
     */
    int countTotal(Query query);
}
