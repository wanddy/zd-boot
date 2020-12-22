package workflow.ide.core.ext;

import lombok.Data;

import java.util.List;

@Data
public class ExtensionElements {

    private Properties properties; // process

    private FormData formData; // userTask

    private List<ExecutionListener> executionListeners; // common
}
