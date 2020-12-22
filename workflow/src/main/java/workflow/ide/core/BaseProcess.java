package workflow.ide.core;

import lombok.Data;

import java.util.List;

@Data
public class BaseProcess extends BaseWorkflowObject {

    private String isExecutable;

    // 开始事件
    private List<StartEvent> startEvents;

    // 结束事件
    private List<EndEvent> endEvents;

    // 用户任务
    private List<UserTask> userTasks;

    // 用户任务
    private List<Task> tasks;

    // 顺序流
    private List<SequenceFlow> sequenceFlows;

    // 排他网关
    private List<Gateway> exclusiveGateways;

    // 并行网关
    private List<Gateway> parallelGateways;

    // 兼容网关
    private List<Gateway> inclusiveGateways;

    // 事件网关
    private List<Gateway> eventBasedGateways;

    // 边界事件
    private List<BoundaryEvent> boundaryEvents;

    // 捕获事件
    private List<IntermediateEvent> intermediateCatchEvents;

    // 抛出事件
    private List<IntermediateEvent> intermediateThrowEvent;

    // 调用活动
    private List<CallActivity> callActivitys;

    private List<SubProcess> subProcessList; // 子流程对象
}
