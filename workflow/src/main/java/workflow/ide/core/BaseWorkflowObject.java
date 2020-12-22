package workflow.ide.core;

import lombok.Data;
import workflow.ide.core.ext.ExtensionElements;

@Data
public class BaseWorkflowObject {

    private String id;

    private String name;

    // 异步
    private String async;

    // 排他
    private String exclusive;

    // 文档
    private String documentation;

    private ExtensionElements extensionElements;
}
