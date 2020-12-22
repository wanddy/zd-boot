package workflow.ide.core.ext;

import lombok.Data;

@Data
public class FormField {

    private String id;

    private String name;

    private String label;

    private String value;

    private String defaultValue;

    private String type;

    private String expression;

    private String variable;

    private String required;

    private String datePattern;

    private String readable;

    private String writable;

    private Properties properties;
}
