package auth.entity;

import java.io.Serializable;
import java.util.Date;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 系统通告
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_announcement")
public class Announcement extends BaseAuditingEntity implements Serializable {

    /**
     * 标题
     */
    private String titile;
    /**
     * 内容
     */
    private String msgContent;

    /**
     * 开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 发布人
     */
    private String sender;
    /**
     * 优先级（L低，M中，H高）
     */
    @commons.annotation.Dict(dicCode = "priority")
    private String priority;

    /**
     * 消息类型1:通知公告2:系统消息
     */
    @commons.annotation.Dict(dicCode = "msg_category")
    private String msgCategory;
    /**
     * 通告对象类型（USER:指定用户，ALL:全体用户）
     */
    @commons.annotation.Dict(dicCode = "msg_type")
    private String msgType;
    /**
     * 发布状态（0未发布，1已发布，2已撤销）
     */
    @commons.annotation.Dict(dicCode = "send_status")
    private String sendStatus;
    /**
     * 发布时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    /**
     * 撤销时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /**
     * 删除状态（0，正常，1已删除）
     */
    private String delFlag;

    /**
     * 指定用户
     **/
    private String userIds;
    /**
     * 业务类型(email:邮件 bpm:流程)
     */
    private String busType;
    /**
     * 业务id
     */
    private String busId;
    /**
     * 打开方式 组件：component 路由：url
     */
    private String openType;
    /**
     * 组件/路由 地址
     */
    private String openPage;
    /**
     * 摘要
     */
    private String msgAbstract;
}
