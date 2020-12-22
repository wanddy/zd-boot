package workflow.business.model;

import java.io.Serializable;
import java.util.Date;

/** 
* @ClassName: ProcessDeploymentData 
* @Description: 流程部署信息
* @author KaminanGTO
* @date 2018年12月10日 上午11:25:12 
*  
*/
public class ProcessDeploymentData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String key;
	
	private String name;
	
	private Date time;

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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
}
