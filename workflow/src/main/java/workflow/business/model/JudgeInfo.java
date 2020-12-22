package workflow.business.model;

import java.io.Serializable;

/** 
* @ClassName: JudgeInfo 
* @Description: 判断信息数据
* @author KaminanGTO
* @date 2019年1月11日 下午4:08:17 
*  
*/
public class JudgeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields value : 判断信息值
	*/ 
	private String value;
	
	/** 
	* @Fields name : 判断信息名称
	*/ 
	private String name;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
