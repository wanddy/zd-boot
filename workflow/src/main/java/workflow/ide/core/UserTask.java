package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.MultiInstanceLoopCharacteristics;

@Data
public class UserTask extends BaseWorkflowObject implements IMultiInstanceLoopCharacteristics  {

    // 代理人
    private String assignee;

    // 候选用户
    private String candidateUsers;

    // 候选组
    private String candidateGroups;

    // 多实例类型
    private MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;

}
