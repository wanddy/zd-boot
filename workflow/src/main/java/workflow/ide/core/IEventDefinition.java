package workflow.ide.core;

import workflow.ide.core.ext.*;

public interface IEventDefinition {

    public TimerEventDefinition getTimerEventDefinition();

    public void setTimerEventDefinition(TimerEventDefinition timerEventDefinition);


    public MessageEventDefinition getMessageEventDefinition();

    public void setMessageEventDefinition(MessageEventDefinition messageEventDefinition);


    public SignalEventDefinition getSignalEventDefinition();

    public void setSignalEventDefinition(SignalEventDefinition signalEventDefinition);


    public ErrorEventDefinition getErrorEventDefinition();

    public void setErrorEventDefinition(ErrorEventDefinition errorEventDefinition);

    public CompensateEventDefinition getCompensateEventDefinition();

    public void setCompensateEventDefinition(CompensateEventDefinition compensateEventDefinition);
}
