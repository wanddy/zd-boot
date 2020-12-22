package workflow.business.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: FormData 
* @Description: 表单数据
* @author KaminanGTO
* @date 2018年9月11日 下午12:41:00 
*  
*/
public class FormData implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = -567035143489906406L;

	/** 
	* @Fields id : 表单数据id
	*/ 
	private String id;
	
	/** 
	* @Fields title : 表单标题
	*/ 
	private String title;
	
	/** 
	* @Fields dataList : 已填写表单实例列表
	*/ 
	private List<FormInstanceData> dataList;
	
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<FormInstanceData> getDataList() {
		return dataList;
	}

	public void setDataList(List<FormInstanceData> dataList) {
		this.dataList = dataList;
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
