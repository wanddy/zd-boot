package auth.entity;

import java.util.Date;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import lombok.experimental.Accessors;

/**
 * 系统日志
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_log")
public class Log extends BaseAuditingEntity implements Serializable {

    /**
     * 耗时
     */
    private Long costTime;

    /**
     * IP
     */
    private String ip;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 请求路径
     */
    private String requestUrl;
    /**
     * 请求方法
     */
    private String method;

    /**
     * 操作人用户名称
     */
    private String username;
    /**
     * 操作人用户账户
     */
    private String userid;
    /**
     * 操作详细日志
     */
    private String logContent;

    /**
     * 日志类型（1登录日志，2操作日志）
     */
    @commons.annotation.Dict(dicCode = "log_type")
    private Integer logType;

    /**
     * 操作类型（1查询，2添加，3修改，4删除,5导入，6导出）
     */
    @commons.annotation.Dict(dicCode = "operate_type")
    private Integer operateType;

}
