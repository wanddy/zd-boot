package auth.domain.depart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import auth.entity.Depart;

import java.util.List;

@Repository
public interface SysDepartMapper extends BaseMapper<Depart> {

    /**
     * 根据用户ID查询部门集合
     */
    List<Depart> queryUserDeparts(@Param("userId") String userId);

    /**
     * 根据用户名查询部门
     *
     * @param username
     * @return
     */
    List<Depart> queryDepartsByUsername(@Param("username") String username);

    @Select("select id from sys_depart where org_code=#{orgCode}")
    String queryDepartIdByOrgCode(@Param("orgCode") String orgCode);

    @Select("select * from sys_depart where org_code=#{orgCode}")
    Depart queryDepartByOrgCode(@Param("orgCode") String orgCode);

    @Select("select id,parent_id from sys_depart where id=#{departId}")
    Depart getParentDepartId(@Param("departId") String departId);

    /**
     * 根据部门Id查询,当前和下级所有部门IDS
     *
     * @param departId
     * @return
     */
    List<String> getSubDepIdsByDepId(@Param("departId") String departId);

    /**
     * 根据部门编码获取部门下所有IDS
     *
     * @param orgCodes
     * @return
     */
    List<String> getSubDepIdsByOrgCodes(@org.apache.ibatis.annotations.Param("orgCodes") String[] orgCodes);

}
