package smartcode.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import smartcode.form.entity.OnlCgformField;

import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:36
 * @Description: zdit.zdboot.auth.online.mapper
 **/
@Repository
public interface OnlCgformFieldMapper extends BaseMapper<OnlCgformField> {
    List<String> selectOnlineHideColumns(@Param("user_id") String userId, @Param("online_tbname") String onlineTbname);

    List<String> selectFlowAuthColumns(@Param("table_name") String tableName, @Param("task_id") String taskId, @Param("rule_type") String ruleType);

    List<String> selectOnlineDisabledColumns(@Param("user_id") String userId, @Param("online_tbname") String onlineTbname);

    List<Map<String, Object>> queryListBySql(@Param("sqlStr") String sqlStr);

    IPage<Map<String, Object>> selectPageBySql(Page<Map<String, Object>> var1, @Param("sqlStr") String sqlStr);

    void deleteAutoList(@Param("sqlStr") String sqlStr);

    Map<String, Object> queryFormData(@Param("sqlStr") String sqlStr);

    void executeUpdatetSQL(Map<String, Object> execute_sql_string);

    void executeInsertSQL(Map<String, Object> execute_sql_string);

    void editFormData(@Param("sqlStr") String sqlStr);

    Integer queryCountBySql(@Param("sqlStr") String sqlStr);

    List<Map<String, Object>> queryListData(@Param("sqlStr") String var1);
}
