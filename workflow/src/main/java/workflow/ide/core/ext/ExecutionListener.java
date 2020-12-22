package workflow.ide.core.ext;

import lombok.Data;

import java.util.List;

@Data
public class ExecutionListener {

    // ******************************************
    // 监听器类型
    // 类
    private String listenerClass;

    // 表达式
    private String expression;

    // 委托表达式
    private String delegateExpression;
    // ******************************************

    // 事件
    private String event;

    private List<Field> fields;
}
