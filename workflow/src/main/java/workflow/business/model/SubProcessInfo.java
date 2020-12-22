package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: SubProcessInfo 
* @Description: 子流程信息（包含未发布的和已发布的） 
* @author KaminanGTO
* @date 2018年11月9日 下午2:10:47 
*  
*/
public class SubProcessInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 流程ID
	*/ 
	private String id;
	
	/** 
	* @Fields name : 流程名称
	*/ 
	private String name;
	
	/** 
	* @Fields key : 流程Key
	*/ 
	private String key;
	
	/** 
	* @Fields releaseVersion : 已发布流程版本号
	*/ 
	private int releaseVersion;
	
	/** 
	* @Fields released : 是否已发布
	*/ 
	private Boolean released;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(int releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public Boolean getReleased() {
		return released;
	}

	public void setReleased(Boolean released) {
		this.released = released;
	}
	
	
	
}
