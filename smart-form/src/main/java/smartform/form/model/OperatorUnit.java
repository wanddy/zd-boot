package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/***
 * @ClassName: OperatorUnit
 * @Description: 表单公式的运算单元
 * @author hou
 * @date 2018年10月17日 上午11:34:55
 * 
 */
public class OperatorUnit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 类型:1,算数运算符;2,合计运算符;3,字段
	 */
	private Integer type;

	/**
	 * 合计类型:1,SUM;2,AVERGE;3,MIN;4,MAX;5,COUNT
	 */
	private Integer totalType;

	/**
	 * 字段id列表(合计)
	 */
	private List<String> fieldList;

	/**
	 * 算数运算符类型：1，=；2，+；3，-；4，*；5，/
	 */
	private Integer operatorType;
	
	/**
	 * 行号
	 */
	private Integer lineNum;
	
	/**
	 * 字段id(算数)
	 */
	private String fieldId;

	/**
	 * 1，常量；2，正则；3，跨表（日期Long）
	 */
	private Integer valueType;

	/**
	 * 运算值
	 */
	private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getTotalType() {
		return totalType;
	}

	public void setTotalType(Integer totalType) {
		this.totalType = totalType;
	}

	public Integer getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(Integer operatorType) {
		this.operatorType = operatorType;
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	@Override
	public String toString() {
		return "OperatorUnit [id=" + id + ", type=" + type + ", totalType=" + totalType + ", fieldList=" + fieldList
				+ ", operatorType=" + operatorType + ", lineNum=" + lineNum + ", fieldId=" + fieldId + ", valueType="
				+ valueType + ", value=" + value + "]";
	}

	public Integer getValueType() {
		return valueType;
	}

	public void setValueType(Integer valueType) {
		this.valueType = valueType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

}