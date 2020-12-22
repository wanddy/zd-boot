package smartcode.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import commons.api.vo.Result;
import smartcode.form.entity.OnlCgreportHead;
import smartcode.form.entity.OnlCgreportModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 15:39
 * @Description: zdit.zdboot.auth.online.service
 **/
public interface OnlCgreportHeadService extends IService<OnlCgreportHead> {

    Map<String, Object> executeSelectSqlDynamic(String dbKey, String sql, Map<String, Object> params, String onlCgreportHeadId);

    Map<String, Object> executeSelectSql(String sql, String onlCgreportHeadId, Map<String, Object> params) throws SQLException;

    List getSqlFields(String sql, String dbKey) throws SQLException;

    List getSqlParams(String sql);

    Result<?> editAll(OnlCgreportModel onlCgreportModel);

    Result<?> delete(String id);

    Result<?> bathDelete(String[] ids);

    Map queryCgReportConfig(String var1);

}
