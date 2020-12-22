package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.*;

@Data
public class EndEvent extends BaseWorkflowObject implements IEventDefinition {

    // 定时定义事件
    private TimerEventDefinition timerEventDefinition;

    // 信号定义事件
    private SignalEventDefinition signalEventDefinition;

    // 消息定义事件
    private MessageEventDefinition messageEventDefinition;

    // 错误定义事件
    private ErrorEventDefinition errorEventDefinition;

    // 补偿事件
    private CompensateEventDefinition compensateEventDefinition;

}
