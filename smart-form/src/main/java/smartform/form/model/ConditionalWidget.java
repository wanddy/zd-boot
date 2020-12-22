package smartform.form.model;

import java.io.Serializable;

/**
 * @ClassName: ConditionalWidget
 * @Description: 条件处理需要设置的组件实体
 * @author quhanlin
 * @date 2018年12月1日 下午6:05:13
 * 
 */
public class ConditionalWidget implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 组件/表格：2，字段：1
	 */
	private int type;

	/**
	 * 分页ID
	 */
	private String pageId;

	/**
	 * 组ID
	 */
	private String groupId;

	/**
	 * 是否隐藏
	 */
	private Boolean hide;

	/**
	 * 是否禁用
	 */
	private Boolean disable;

	/**
	 * 字段行号
	 */
	private Integer lineNum;

	/**
	 * 要处理字段
	 */
	private ConditionalField field;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public Boolean getHide() {
		return hide;
	}

	public void setHide(Boolean hide) {
		this.hide = hide;
	}

	public Boolean getDisable() {
		return disable;
	}

	public void setDisable(Boolean disable) {
		this.disable = disable;
	}

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

	public ConditionalField getField() {
		return field;
	}

	public void setField(ConditionalField field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "ConditionalWidget [type=" + type + ", pageId=" + pageId + ", groupId=" + groupId + ", hide=" + hide
				+ ", disable=" + disable + ", lineNum=" + lineNum + ", field=" + field + "]";
	}

}
