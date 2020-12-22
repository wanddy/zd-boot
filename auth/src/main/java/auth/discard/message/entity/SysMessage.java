package auth.discard.message.entity;

import commons.annotation.Dict;
import commons.system.base.entity.JeecgEntity;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date: 2019-04-09
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_sms")
public class SysMessage extends JeecgEntity {
    /**
     * 推送内容
     */
    private String esContent;
    /**
     * 推送所需参数Json格式
     */
    private String esParam;
    /**
     * 接收人
     */
    private String esReceiver;
    /**
     * 推送失败原因
     */
    private String esResult;
    /**
     * 发送次数
     */
    private Integer esSendNum;
    /**
     * 推送状态 0未推送 1推送成功 2推送失败
     */
    @Dict(dicCode = "msgSendStatus")
    private String esSendStatus;
    /**
     * 推送时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date esSendTime;
    /**
     * 消息标题
     */
    private String esTitle;
    /**
     * 推送方式：1短信 2邮件 3微信
     */
    @Dict(dicCode = "msgType")
    private String esType;
    /**
     * 备注
     */
    private String remark;
}
