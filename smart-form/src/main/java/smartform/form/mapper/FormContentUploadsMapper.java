package smartform.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.entity.FormContentUploadsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author quhanlin
 * @ClassName: FormContentUploadsMapper
 * @Description: 文件上传表
 * @date 2018年11月1日 下午6:30:52
 */
@Mapper
public interface FormContentUploadsMapper extends BaseMapper<FormContentUploadsEntity> {
    /**
     * 删除表单内容的所有上传数据
     */
    int removeContent(@Param("dbTable") String dbTable, @Param("contentId") String contentId, @Param("tableName") String tableName,
                      @Param("workType") Integer workType, @Param("lineId") String lineId, @Param("uploadType") Integer uploadType);

    /**
     * 批量新增
     * @return
     */
    int batchSave(@Param("items") List<FormContentUploadsEntity> items, @Param("dbTable") String dbTable);

    /**
     * 列表获取上传内容
     * @return
     */
    List<FormContentUploadsEntity> getContentList(@Param("dbTable") String dbTable, @Param("contentId") String contentId,
                                                  @Param("tableName") String tableName, @Param("workType") Integer workType,
                                                  @Param("uploadType") Integer uploadType, @Param("lineId") String lineId);

}
