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
 * 用户代理人
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_user_agent")
public class UserAgent extends BaseAuditingEntity implements Serializable {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 代理人用户名
     */
    private String agentUserName;

    /**
     * 代理开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 代理结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 状态0无效1有效
     */
    private String status;

    /**
     * 所属部门
     */
    private String sysOrgCode;

    /**
     * 所属公司
     */
    private String sysCompanyCode;
}
