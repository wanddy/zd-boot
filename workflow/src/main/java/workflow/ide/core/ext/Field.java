package workflow.ide.core.ext;

import lombok.Data;

@Data
public class Field {

    private String name;

    // 表达式
    private String expression;

    // 字符串
    private String string;
}
