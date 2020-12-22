package workflow.business.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import workflow.business.model.ActiveTask;
import workflow.business.model.TotalTasks;

import java.util.List;

/**
 *
 */
@Mapper
@Repository
public interface ActiveTaskMapper extends BaseMapper<ActiveTask> {
    /**
     * 条件查询
     *
     * @param query
     * @return
     */
    List<ActiveTask> listAll(Query query);

    /**
     * 分页查询
     *
     * @param page
     * @param query
     * @return
     */
    List<ActiveTask> listAll(Page<ActiveTask> page, Query query);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ActiveTask getActiveTaskById(String id);

    /**
     * 删除用户userId的某个待办任务
     * 删除taskId的所有任务
     *
     * @param userId
     * @param taskId
     * @return
     */
    int deleteActiveTask(@Param(value = "userId") String userId, @Param(value = "taskId") String taskId);

    /**
     * 获取待办任务总览
     *
     * @param query 查询条件
     * @return
     */
    List<TotalTasks> totalTasks(Query query);

    /**
     * 进行暂停、停止、恢复操作时，更新对应状态
     *
     * @param wrapper
     * @return
     */
    int updateStatus(@Param(Constants.WRAPPER) Wrapper<ActiveTask> wrapper, @Param("status") int status);

    /**
     * 进行终止流程操作时，删除对应任务
     *
     * @param wrapper
     * @return
     */
    int deleteByProcess(@Param(Constants.WRAPPER) Wrapper<ActiveTask> wrapper);

    /**
     * 批量删除
     *
     * @param id
     * @return
     */
    int batchRemove(String[] id);

    /**
     * 批量新增
     *
     * @param items
     * @return
     */
    int batchSave(List<ActiveTask> items);

    /**
     * 更新
     *
     * @param t
     * @return
     */
    int update(ActiveTask t);

    /**
     * 新增
     *
     * @param t
     * @return
     */
    int save(ActiveTask t);

}
