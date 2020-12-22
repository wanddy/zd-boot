package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: TaskFormData 
* @Description: 任务表单数据
* @author KaminanGTO
* @date 2018年10月30日 下午6:39:53 
*  
*/
public class TaskFormData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 表单id
	*/ 
	private String id;
	
	/** 
	* @Fields name : 表单标题
	*/ 
	private String title;
	
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
