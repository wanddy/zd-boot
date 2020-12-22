package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.MultiInstanceLoopCharacteristics;

@Data
public class SubProcess extends BaseProcess implements IMultiInstanceLoopCharacteristics {

    // 多实例类型
    private MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;

}
