package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: TaskExVariableData 
* @Description: 任务扩展自定义数据（用于存放业务自定义数据）
* @author KaminanGTO
* @date 2018年12月12日 下午3:51:54 
*  
*/
public class TaskExVariableData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 变量id
	*/ 
	private String id;
	
	/** 
	* @Fields value : 变量值
	*/ 
	private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
