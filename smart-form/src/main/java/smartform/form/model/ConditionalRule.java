package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ConditionalRule
 * @Description: 复杂条件规则
 * @author quhanlin
 * @date 2018年12月1日 下午6:05:13
 * 
 */
public class ConditionalRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;

	/**
	 * 条件字段所在分页
	 */
	private String pageId;

	/**
	 * 条件字段所在组件
	 */
	private String groupId;

	/**
	 * 条件字段所在行
	 */
	private Integer lineNum;

	/**
	 * 条件字段类型
	 */
	private Integer fieldType;

	/**
	 * 条件字段Id
	 */
	private String fieldId;

	/**
	 * 条件关系运算符
	 */
	private Integer relationOperator;

	/**
	 * 判定条件
	 */
	private String condition;

	/**
	 * 要处理的组件列表
	 */
	private List<ConditionalWidget> widgetList;

	/**
	 * 字段列表是否有组件
	 */
	private Boolean hasGroup;
	
	/** 
	* @Fields checkedNodes : 规则选中状态（临时用）
	*/ 
	private String checkedNodes;

	// /**
	// * 条件字段表名
	// */
	// private String table;
	//
	// /**
	// * 条件字段别名
	// */
	// private String alias;

	@Override
	public String toString() {
		return "ConditionalRule [pageId=" + pageId + ", groupId=" + groupId + ", lineNum=" + lineNum + ", fieldType="
				+ fieldType + ", fieldId=" + fieldId + ", relationOperator=" + relationOperator + ", condition="
				+ condition + ", widgetList=" + widgetList + ", hasGroup=" + hasGroup + ", checkedNodes=" + checkedNodes + "]";
	}

	public Integer getFieldType() {
		return fieldType;
	}

	public void setFieldType(Integer fieldType) {
		this.fieldType = fieldType;
	}

	public Integer getRelationOperator() {
		return relationOperator;
	}

	public void setRelationOperator(Integer relationOperator) {
		this.relationOperator = relationOperator;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<ConditionalWidget> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(List<ConditionalWidget> widgetList) {
		this.widgetList = widgetList;
	}

	public Boolean getHasGroup() {
		return hasGroup;
	}

	public void setHasGroup(Boolean hasGroup) {
		this.hasGroup = hasGroup;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCheckedNodes() {
		return checkedNodes;
	}

	public void setCheckedNodes(String checkedNodes) {
		this.checkedNodes = checkedNodes;
	}
	
	
}
