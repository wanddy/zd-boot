package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: TaskVariableData 
* @Description: 任务自定变量数据
* @author KaminanGTO
* @date 2018年10月30日 下午6:39:25 
*  
*/
public class TaskVariableData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 变量id
	*/ 
	private String id;
	
	/** 
	* @Fields name : 变量名称
	*/ 
	private String name;
	
	/** 
	* @Fields value : 变量值
	*/ 
	private Object value;
	
	/** 
	* @Fields isReadable : 是否可读
	*/ 
	private boolean readable;
	
	/** 
	* @Fields isWritable : 是否可写
	*/ 
	private boolean writable;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	
}
