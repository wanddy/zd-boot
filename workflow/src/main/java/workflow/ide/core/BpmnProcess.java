package workflow.ide.core;

import lombok.Data;

@Data
public class BpmnProcess extends BaseProcess {

    // 流程类型（1主、2子）
    private int processType;

    private String subActivityStr;

    private String isNew;

    private String candidateStarterUsers; // 候选用户

    private String candidateStarterGroups; // 候选组
}
