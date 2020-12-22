package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import workflow.business.model.entity.UserTaskFinishedEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//import framework.orm.mapper.BaseMapper;
//import framework.entity.Query;

/**
 * 用户完成记录表
 */
@Mapper
public interface UserTaskFinishedMapper extends BaseMapper<UserTaskFinishedEntity> {

	List<UserTaskFinishedEntity> simpleList();
	/**
	 * 分页查询列表
	 * @param page
	 * @param query
	 * @return
	 */
	List<UserTaskFinishedEntity> listForPage(Page<UserTaskFinishedEntity> page, Query query);

	/**
	 * 新增
	 * @param t
	 * @return
	 */
	int save(UserTaskFinishedEntity t);

	/**
	 * 根据id查询详情
	 * @param id
	 * @return
	 */
	UserTaskFinishedEntity getObjectById(Object id);

	/**
	 * 根据id查询详情
	 * @param id
	 * @return
	 */
	UserTaskFinishedEntity getObjectBytaskId(Object id);
	/**
	 * 批量删除
	 * @param id
	 * @return
	 */
	int batchRemove(Long[] id);

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	int remove(String id);
	/**
	 * 更新
	 * @param t
	 * @return
	 */
	int update(UserTaskFinishedEntity t);
}
