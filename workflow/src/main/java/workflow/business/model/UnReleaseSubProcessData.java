package workflow.business.model;

import java.io.Serializable;
import java.util.Date;

/** 
* @ClassName: UnReleaseSubProcessData 
* @Description: 未发布子流程数据
* @author KaminanGTO
* @date 2018年11月2日 上午11:07:19 
*  
*/
public class UnReleaseSubProcessData implements Serializable {

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
	* @Fields releasVersion : 已发布流程版本号
	*/ 
	private int releaseVersion;

	/** 
	* @Fields createTime : 创建时间
	*/ 
	private Date createTime;
	
	/** 
	* @Fields updateTime : 更新时间
	*/ 
	private Date updateTime;
	
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

	public int getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(int releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
