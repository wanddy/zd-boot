package smartform.exception;

/**
 * 自定义GlobalException
 */
public class GlobalException extends RuntimeException {
	
	private String message;
	
	private String errorMsg;

	public GlobalException(int code) {
		this.message = String.valueOf(code);
	}
	
	public GlobalException(int code, String msg) {
		this.message = String.valueOf(code);
		this.errorMsg = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
