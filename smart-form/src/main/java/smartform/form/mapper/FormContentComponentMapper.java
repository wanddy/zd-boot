package smartform.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import smartform.form.model.entity.FormContentComponentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smartform.query.Query;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FormContentComponentMapper
 * @Description: 用于表单组件表存储
 * @author quhanlin
 * @date 2018年11月1日 下午6:03:23
 *
 */
@Mapper
public interface FormContentComponentMapper extends BaseMapper<FormContentComponentEntity> {

	/**
	 * 保存表单内容主表数据
	 *
	 * @param t
	 * @param dbTable
	 * @param content
	 * @return
	 */
	int saveContent(@Param("data") FormContentComponentEntity data,@Param("dbTable") String dbTable, @Param("content")Query content);

	/**
	 * 查询表单内容主表简单数据
	 * @param dbTable
	 * @param id
	 * @param column
	 * @return
	 */
	Map<String,Object> getContent(@Param("dbTable") String dbTable,@Param("contentId") String contentId,@Param("workType") Integer workType,@Param("columns") List<String> columns);

	/**
	 * 编辑表单内容主表数据
	 * @param t
	 * @param dbTable
	 * @param content
	 * @return
	 */
	int updateContent(FormContentComponentEntity data, String dbTable, Query content);
	//List<Map<String,Object>> getFormContent(@Param("filedNameList") List<String> filedNameList,@Param("tableName") String tableName);
}
