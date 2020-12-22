package workflow.ide.core.ext;

import lombok.Data;

/**
 * 调用活动
 */
@Data
public class CallParameter {

    private String source;

    private String sourceExpression;

    private String target;

    private String targetExpression;
}
