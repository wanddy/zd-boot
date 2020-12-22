package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 填值规则
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_fill_rule")
public class FillRule extends BaseAuditingEntity implements Serializable {

    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则Code
     */
    private String ruleCode;
    /**
     * 规则实现类
     */
    private String ruleClass;
    /**
     * 规则参数
     */
    private String ruleParams;
}
