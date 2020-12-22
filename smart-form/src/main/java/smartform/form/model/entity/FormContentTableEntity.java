package smartform.form.model.entity;


import smartform.query.Query;

import java.io.Serializable;

/** 
* @ClassName: FormContentTableEntity 
* @Description: 用于表单内容表格表
* @author quhanlin
* @date 2018年11月1日 下午5:59:44
*/
public class FormContentTableEntity extends FormContentComponentBase implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 固定行ID
	 */
	private String fixedId;
	
	/**
	 * 表格的行号，也可用于排序
	 */
	private Integer lineNum;
	
	/**
	 * 表格的行状态
	 */
	private Integer state;
	
	/**
	 * 用于存储批量添加时表格中的其他字段
	 */
	private Query content;
	
	/** 
	* @Fields sort : 排序
	*/ 
	private Integer sort;

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

	@Override
	public String toString() {
		return "FormContentTableEntity [fixedId=" + fixedId + ", lineNum=" + lineNum + ", state=" + state + ", content="
				+ content + "]";
	}

	public Query getContent() {
		return content;
	}

	public void setContent(Query content) {
		this.content = content;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getFixedId() {
		return fixedId;
	}

	public void setFixedId(String fixedId) {
		this.fixedId = fixedId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	
}
