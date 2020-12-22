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
 * 用户通告阅读标记表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_announcement_send")
public class AnnouncementSend extends BaseAuditingEntity implements Serializable {

    /**
     * 通告id
     */
    private String anntId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 阅读状态（0未读，1已读）
     */
    private String readFlag;

    /**
     * 阅读时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
}
