//
// Source code recreated from GenUtils .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import smartcode.form.entity.OnlCgformIndex;

@Repository
public interface OnlCgformIndexMapper extends BaseMapper<OnlCgformIndex> {

    int queryIndexCount(@Param("sqlStr") String sqlStr);

    void updateByData(@Param("sqlStr") String sqlStr);

}
