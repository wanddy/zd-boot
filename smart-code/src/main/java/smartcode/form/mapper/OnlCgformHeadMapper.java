package smartcode.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import smartcode.form.entity.OnlCgformHead;

import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 16:08
 * @Description: zdit.zdboot.auth.online.mapper
 **/
@Repository
public interface OnlCgformHeadMapper extends BaseMapper<OnlCgformHead> {

    @Select({"select physic_id from onl_cgform_head GROUP BY physic_id"})
    List<String> queryCopyPhysicId();

    void executeDDL(@Param("sqlStr") String var1);

    List<String> queryOnlinetables();

    String queryCategoryIdByCode(@Param("code") String code);

    Map<String, Object> queryOneByTableNameAndId(@Param("tbname") String tbname, @Param("dataId") String dataId);

    @Select({"select count(*) from ${tableName} where ${pidField} = #{pidValue}"})
    Integer queryChildNode(@Param("tableName") String var1, @Param("pidField") String var2, @Param("pidValue") String var3);

    void deleteOne(@Param("sqlStr") String var1);
}
