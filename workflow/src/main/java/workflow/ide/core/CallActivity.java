package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.CallParameter;
import workflow.ide.core.ext.MultiInstanceLoopCharacteristics;

import java.util.List;

/**
 * 调用活动
 */
@Data
public class CallActivity extends BaseWorkflowObject implements IMultiInstanceLoopCharacteristics {

    private String calledElement;

    // 输入参数
    List<CallParameter> inCallParameters;

    // 输出参数
    List<CallParameter> outCallParameters;

    // 多实例类型
    private MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;
}
