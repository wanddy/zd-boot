package workflow.common.error;

/** 
* @ClassName: WorkFlowException 
* @Description: 工作流错误
* @author KaminanGTO
* @date 2018年10月8日 上午9:53:13 
*  
*/
public class WorkFlowException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3647148792165352312L;

	private String message;
	
	private String errCode = "60501";
	
	public WorkFlowException(String message) {
		super(message);
		this.message = message;
	}
	
	public WorkFlowException(String errCode, String message) {
		super(message);
		this.errCode = errCode;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getErrCode() {
		return errCode;
	}

	@Override
	public String toString() {
		return this.errCode + ":" + message;
	}
}
