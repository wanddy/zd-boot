package commons.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ZdException extends RuntimeException {

    private int code;

    private String message;

    private String filterFailureReason;

    public ZdException(FilterFailureReason filterFailureReason) {
        super(buildMessage(filterFailureReason.toString(), filterFailureReason.getCode()));
        this.code = filterFailureReason.getCode();
        this.message = filterFailureReason.toString();
        this.filterFailureReason = filterFailureReason.toString();
    }

    public ZdException(FilterFailureReason filterFailureReason, String message) {
        super(buildMessage(message, filterFailureReason.getCode()));
        this.code = filterFailureReason.getCode();
        this.message = message;
        this.filterFailureReason = filterFailureReason.toString();
    }

    private static String buildMessage(String message, int code) {
        return message + "(" + code + ")";
    }

    public ZdException(String message) {
        super(message);
    }

    public ZdException(Throwable cause) {
        super(cause);
    }

    public ZdException(String message, Throwable cause) {
        super(message, cause);
    }

}
