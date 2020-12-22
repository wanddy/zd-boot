package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: ProcessDefData 
* @Description: 流程定义数据(已发布)
* @author KaminanGTO
* @date 2018年10月25日 下午3:27:51 
*  
*/
public class ProcessDefData implements Serializable{

	private static final long serialVersionUID = 1L;

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
	
	/** 
	* @Fields version : 流程版本号
	*/ 
	private int version;
	
	/** 
	* @Fields state : 流程状态1激活2中止
	*/ 
	private int state;
	
	/** 
	* @Fields deploymentID : 流程部署ID
	*/ 
	private String deploymentId;
	
	/** 
	* @Fields description : 流程描述
	*/ 
	private String description;
	
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
