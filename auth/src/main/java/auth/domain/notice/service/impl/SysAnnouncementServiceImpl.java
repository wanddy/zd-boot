package auth.domain.notice.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import commons.constant.CommonConstant;
import commons.util.oConvertUtils;
import auth.entity.Announcement;
import auth.entity.AnnouncementSend;
import auth.domain.notice.mapper.SysAnnouncementMapper;
import auth.domain.notice.mapper.SysAnnouncementSendMapper;
import auth.domain.notice.service.ISysAnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 系统通告
 */
@Slf4j
@Service
public class SysAnnouncementServiceImpl extends ServiceImpl<SysAnnouncementMapper, Announcement> implements ISysAnnouncementService {

    @Resource
    private SysAnnouncementMapper sysAnnouncementMapper;

    @Resource
    private SysAnnouncementSendMapper sysAnnouncementSendMapper;

    @Transactional
    @Override
    public void saveAnnouncement(Announcement announcement) {
        if (announcement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
            sysAnnouncementMapper.insert(announcement);
        } else {
            // 1.插入通告表记录
            sysAnnouncementMapper.insert(announcement);
            // 2.插入用户通告阅读标记表记录
            String userId = announcement.getUserIds();
            String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
            String anntId = announcement.getId();
            Date refDate = new Date();
            for (String id : userIds) {
                AnnouncementSend announcementSend = new AnnouncementSend();
                announcementSend.setId(anntId);
                announcementSend.setUserId(id);
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                announcementSend.setReadTime(refDate);
                sysAnnouncementSendMapper.insert(announcementSend);
            }
        }
    }

    /**
     * @功能：编辑消息信息
     */
    @Transactional
    @Override
    public boolean upDateAnnouncement(Announcement announcement) {
        // 1.更新系统信息表数据
        sysAnnouncementMapper.updateById(announcement);
        String userId = announcement.getUserIds();
        if (oConvertUtils.isNotEmpty(userId) && announcement.getMsgType().equals(CommonConstant.MSG_TYPE_UESR)) {
            // 2.补充新的通知用户数据
            String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
            String anntId = announcement.getId();
            Date refDate = new Date();
            for (int i = 0; i < userIds.length; i++) {
                LambdaQueryWrapper<AnnouncementSend> queryWrapper = new LambdaQueryWrapper<AnnouncementSend>();
                queryWrapper.eq(AnnouncementSend::getId, anntId);
                queryWrapper.eq(AnnouncementSend::getUserId, userIds[i]);
                List<AnnouncementSend> announcementSends = sysAnnouncementSendMapper.selectList(queryWrapper);
                if (announcementSends.size() <= 0) {
                    AnnouncementSend announcementSend = new AnnouncementSend();
                    announcementSend.setId(anntId);
                    announcementSend.setUserId(userIds[i]);
                    announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                    announcementSend.setReadTime(refDate);
                    sysAnnouncementSendMapper.insert(announcementSend);
                }
            }
            // 3. 删除多余通知用户数据
            Collection<String> delUserIds = Arrays.asList(userIds);
            LambdaQueryWrapper<AnnouncementSend> queryWrapper = new LambdaQueryWrapper<AnnouncementSend>();
            queryWrapper.notIn(AnnouncementSend::getUserId, delUserIds);
            queryWrapper.eq(AnnouncementSend::getId, anntId);
            sysAnnouncementSendMapper.delete(queryWrapper);
        }
        return true;
    }

    // @功能：流程执行完成保存消息通知
    @Override
    public void saveSysAnnouncement(String title, String msgContent) {
        Announcement announcement = new Announcement();
        announcement.setTitile(title);
        announcement.setMsgContent(msgContent);
        announcement.setSender("JEECG BOOT");
        announcement.setPriority(CommonConstant.PRIORITY_L);
        announcement.setMsgType(CommonConstant.MSG_TYPE_ALL);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
        sysAnnouncementMapper.insert(announcement);
    }

    @Override
    public Page<Announcement> querySysCementPageByUserId(Page<Announcement> page, String userId, String msgCategory) {
        return page.setRecords(sysAnnouncementMapper.querySysCementListByUserId(page, userId, msgCategory));
    }

}
