package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.ConditionExpression;

@Data
public class SequenceFlow extends BaseWorkflowObject {

    private String sourceRef;

    private String targetRef;

    // 跳出表达式
    private String skipExpression;

    // 跳转条件
    private ConditionExpression conditionExpression;
}
