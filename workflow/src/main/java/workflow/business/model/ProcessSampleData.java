package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: ProcessSampleData 
* @Description: 流程简单数据实体
* @author KaminanGTO
* @date 2018年9月11日 上午11:57:14 
*  
*/
public class ProcessSampleData implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 3723424811906079214L;

	/** 
	* @Fields id : 流程id
	*/ 
	private String id;
	
	/** 
	* @Fields key : 流程key
	*/ 
	private String key;
	
	/** 
	* @Fields name : 流程名字
	*/ 
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
