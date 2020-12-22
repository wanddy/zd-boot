package workflow.business.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: JudgeProperty 
* @Description: 判断型变量
* @author KaminanGTO
* @date 2019年1月11日 下午4:04:18 
*  
*/
public class JudgeProperty implements Serializable {

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
	private String value;
	
	/** 
	* @Fields infoList : 选项列表
	*/ 
	private List<JudgeInfo> infoList;
	
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<JudgeInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<JudgeInfo> infoList) {
		this.infoList = infoList;
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
