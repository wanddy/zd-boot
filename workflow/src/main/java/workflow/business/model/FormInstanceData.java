package workflow.business.model;

import java.io.Serializable;
import java.util.Date;

/** 
* @ClassName: FormInstanceData 
* @Description: 表单实例数据
* @author KaminanGTO
* @date 2018年9月11日 下午12:47:27 
*  
*/
public class FormInstanceData implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 3790458878709075947L;

	/** 
	* @Fields id : 表单实例数据id，存放表单实例id
	*/ 
	private String id;
	
	/** 
	* @Fields userId : 表单填写或修改的人员id（只保留最新）
	*/ 
	private String userId;
	
	/** 
	* @Fields updateTime : 表单填写或修改的时间（最保留最新）
	*/ 
	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
