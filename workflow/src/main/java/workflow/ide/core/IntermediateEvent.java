package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.*;

@Data
public class IntermediateEvent extends BaseWorkflowObject implements IEventDefinition {

    // 定时事件
    private TimerEventDefinition timerEventDefinition;

    // 信号事件
    private SignalEventDefinition signalEventDefinition;

    // 消息事件
    private MessageEventDefinition messageEventDefinition;

    // 错误定义事件
    private ErrorEventDefinition errorEventDefinition;
    // 补偿事件
    private CompensateEventDefinition compensateEventDefinition;

}
