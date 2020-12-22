package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 多数据源管理
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_data_source")
public class DataSource extends BaseAuditingEntity implements Serializable {

    /**
     * 数据源编码
     */
    private String code;
    /**
     * 数据源名称
     */
    private String name;
    /**
     * 描述
     */
    private String remark;
    /**
     * 数据库类型
     */
    @commons.annotation.Dict(dicCode = "database_type")
    private String dbType;
    /**
     * 驱动类
     */
    private String dbDriver;
    /**
     * 数据源地址
     */
    private String dbUrl;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 用户名
     */
    private String dbUsername;
    /**
     * 密码
     */
    private String dbPassword;
    /**
     * 所属部门
     */
    private String sysOrgCode;
}
