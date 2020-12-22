package smartform.form.model;

import java.io.Serializable;

/** 
* @ClassName: FormWidgetContent 
* @Description: 表单字段填报提交数据
* @author KaminanGTO
* @date 2019年3月22日 下午2:47:02 
*  
*/
public class FormWidgetContent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 
	* @Fields id : 字段id
	*/ 
	private String fieldId;
	
	/** 
	* @Fields lineId : 行id 
	*/ 
	private String lineId;
	
	/** 
	* @Fields lineNum : 行号
	*/ 
	private Integer lineNum;
	
	/** 
	* @Fields workType : 业务类型
	*/ 
	private int workType;
	
	/** 
	* @Fields value : 数据值
	*/ 
	private Object value;



	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

	public int getWorkType() {
		return workType;
	}

	public void setWorkType(int workType) {
		this.workType = workType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
