package auth.domain.notice.service;

import auth.entity.Announcement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface ISysAnnouncementService extends IService<Announcement> {

	public void saveAnnouncement(Announcement announcement);

	public boolean upDateAnnouncement(Announcement announcement);

	public void saveSysAnnouncement(String title, String msgContent);

	public Page<Announcement> querySysCementPageByUserId(Page<Announcement> page, String userId, String msgCategory);


}
