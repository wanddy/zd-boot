package smartcode.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import smartcode.form.entity.OnlCgreportHead;
import smartcode.form.entity.OnlCgreportParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 15:41
 * @Description: zdit.zdboot.auth.online.mapper
 **/
@Repository
public interface OnlCgreportHeadMapper extends BaseMapper<OnlCgreportHead> {
    List<Map<?, ?>> executeSelete(@Param("sql") String var1);

    List<Map<String, Object>> executeSelect(@Param("sql") String var1);

    IPage<Map<String, Object>> selectPageBySql(Page<Map<String, Object>> var1, @Param("sqlStr") String var2);

    Map<String, Object> queryCgReportMainConfig(@Param("reportId") String reportId);

    List<Map<String, Object>> queryCgReportItems(@Param("cgrheadId") String cgrheadId);

    List<OnlCgreportParam> queryCgReportParams(@Param("cgrheadId") String cgrheadId);

}
