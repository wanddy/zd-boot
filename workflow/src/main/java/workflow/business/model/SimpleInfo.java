package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: SimpleInfo 
* @Description: 简单数据信息，包含ID和name
* @author KaminanGTO
* @date 2018年12月10日 上午11:22:57 
*  
*/
public class SimpleInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String code;
	
	private String label;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
