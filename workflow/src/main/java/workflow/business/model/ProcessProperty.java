package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: ProcessProperty 
* @Description: 流程变量数据
* @author KaminanGTO
* @date 2018年9月11日 下午12:33:50 
*  
*/
public class ProcessProperty implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 3542272288687212768L;

	/** 
	* @Fields id : 变量id
	*/ 
	private String id;
	
	/** 
	* @Fields name : 变量名称
	*/ 
	private String name;
	
	/** 
	* @Fields type : 变量类型名称(string,long,boolean,date) 
	*/ 
	private String type;
	
	/** 
	* @Fields value : 变量值
	*/ 
	private String value;
	
	/** 
	* @Fields isReadable : 是否可读
	*/ 
	private boolean readable;
	
	/** 
	* @Fields isWritable : 是否可写
	*/ 
	private boolean writable;
	
	/** 
	* @Fields isRequired : 是否必填 
	*/ 
	private boolean required;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	
	
}
