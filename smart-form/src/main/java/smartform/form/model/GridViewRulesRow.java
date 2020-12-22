package smartform.form.model;

import smartform.common.model.BaseData;

/** 
* @ClassName: GridViewRulesRow 
* @Description: gridView表格显示内容（前端使用）
* @author chenx
* @date 2020年7月20日 下午2:43:24 
*  
*/
public class GridViewRulesRow extends BaseData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 
	* @Fields id : 对应gridViewRules中的id
	*/ 
	private String id;
	/** 
	* @Fields span : 表格列宽
	*/ 
	private Integer span;
	/** 
	* @Fields alignment : 表格显示内容对齐方式  默认  0 左对齐1、居中对齐2、右对齐3
	*/ 
	private Integer alignment;
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Integer getSpan() {
		return span;
	}


	public void setSpan(Integer span) {
		this.span = span;
	}


	public Integer getAlignment() {
		return alignment;
	}


	public void setAlignment(Integer alignment) {
		this.alignment = alignment;
	}


	@Override
	public String toString() {
		return "GroupLine [span=" + span + ", alignment=" + alignment + "]";
	}

}