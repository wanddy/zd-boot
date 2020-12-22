package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.*;

/**
 * 边界事件
 */
@Data
public class BoundaryEvent extends BaseWorkflowObject implements IEventDefinition {

    private String attachedToRef;

    // 取消活动
    private String cancelActivity;

    // 定时边界事件
    private TimerEventDefinition timerEventDefinition;

    // 信号边界事件
    private SignalEventDefinition signalEventDefinition;

    // 消息边界事件
    private MessageEventDefinition messageEventDefinition;

    // 错误定义事件
    private ErrorEventDefinition errorEventDefinition;

    // 补偿事件
    private CompensateEventDefinition compensateEventDefinition;

}
