package workflow.olddata.core.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * 自定义GraphQLException
 */
public class GraphQLException extends RuntimeException implements GraphQLError{
	
	private String message;
	
	private String errorMsg;

	public GraphQLException(int code) {
		this.message = String.valueOf(code);
	}
	
	public GraphQLException(int code, String msg) {
		this.message = String.valueOf(code);
		this.errorMsg = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public List<SourceLocation> getLocations() {
		return null;
	}

	public ErrorType getErrorType() {
		return ErrorType.ValidationError;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
