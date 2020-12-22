package auth.entity;

import java.io.Serializable;
import java.util.Date;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import lombok.experimental.Accessors;

/**
 * 数据日志
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_data_log")
public class DataLog extends BaseAuditingEntity implements Serializable {

    /**
     * 表名
     */
    private String dataTable;

    /**
     * 数据 id
     */
    private String dataId;

    /**
     * 数据内容
     */
    private String dataContent;

    /**
     * 版本号
     */
    private String dataVersion;
}
