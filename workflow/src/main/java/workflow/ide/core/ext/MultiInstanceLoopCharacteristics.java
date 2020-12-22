package workflow.ide.core.ext;

import lombok.Data;

@Data
public class MultiInstanceLoopCharacteristics {

    // 多实例类型
    private String isSequential;

    // 基数（多实例）
    private String collection;

    // 元素变量（多实例）
    private String elementVariable;

    // 集合（多实例）> 子节点
    private String loopCardinality;

    // 完成条件（多实例）> 子节点
    private String completionCondition;
}
