package auth.domain.notice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import auth.entity.Announcement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface SysAnnouncementMapper extends BaseMapper<Announcement> {


	List<Announcement> querySysCementListByUserId(Page<Announcement> page, @Param("userId")String userId, @Param("msgCategory")String msgCategory);

}
