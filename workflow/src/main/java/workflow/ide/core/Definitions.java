package workflow.ide.core;

import lombok.Data;
import org.activiti.bpmn.model.BpmnModel;

import java.util.List;

@Data
public class Definitions {

    private String id;

    // 流程
    private BpmnProcess process;

    // BPMNDiagram
    private String bPMNDiagram;

    // 信号
    private List<Signal> signals;

    // 信号
    private List<Message> messages;

    // Activiti XML
    private String activitiXml;

    // 缩略图
    private String thumbnail;

    // BpmnModel
    private BpmnModel bpmnModel;
}
