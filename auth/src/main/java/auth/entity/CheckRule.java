package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * 编码校验规则
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_check_rule")
public class CheckRule extends BaseAuditingEntity implements Serializable {

    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则Code
     */
    private String ruleCode;
    /**
     * 规则JSON
     */
    private String ruleJson;
    /**
     * 规则描述
     */
    private String ruleDescription;
}
