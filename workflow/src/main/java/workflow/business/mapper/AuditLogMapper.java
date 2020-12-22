package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import workflow.business.model.AuditLog;

import java.util.List;

//import framework.orm.mapper.BaseMapper;
//import framework.entity.Page;
//import framework.entity.Query;

/**
 *
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
	/**
	 * 条件查询
	 * @param query
	 * @return
	 */
	List<AuditLog> listAll(Query query);

	/**
	 * 分页查询
	 * @param page
	 * @param query
	 * @return
	 */
	List<AuditLog> listAll(Page<AuditLog> page, Query query);

	/**
	 * 查询业务数据审核最后一次上传材料的日志
	 * @param query
	 * @return
	 */
	AuditLog queryLastFile(Query query);

	/**
	 * 新增
	 * @param t
	 * @return
	 */
	int save(AuditLog t);

	/**
	 * 根据id查询详情
	 * @param id
	 * @return
	 */
	AuditLog getObjectById(String id);

	/**
	 * 批量删除
	 * @param id
	 * @return
	 */
	int batchRemove(String[] id);

	/**
	 * 进行暂停、停止、恢复操作时，更新对应状态
	 * @param auditLog
	 * @return
	 */
	int update(AuditLog auditLog);

}
