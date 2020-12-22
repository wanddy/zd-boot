package auth.discard.message.entity;

import commons.system.base.entity.JeecgEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date: 2019-04-09
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_sms_template")
public class SysMessageTemplate extends JeecgEntity {
    /**
     * 模板CODE
     */
    private String templateCode;
    /**
     * 模板标题
     */
    private String templateName;
    /**
     * 模板内容
     */
    private String templateContent;
    /**
     * 模板测试json
     */
    private String templateTestJson;
    /**
     * 模板类型
     */
    private String templateType;
}
