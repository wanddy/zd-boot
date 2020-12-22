package smartform.widget.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.NoArgsConstructor;
import smartform.form.model.FormFieldBase;
import smartform.widget.model.deserializer.RuleBaseListDeserializer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: WidgetBase
 * @Description: 控件基类
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
@TableName("form_field")
@NoArgsConstructor
public class WidgetBase extends FormFieldBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 控件类型
	 */
	/**
	 * 控件类型,1.number;2.input3.textarea4.editor5.radio6.checkbox7.select8.
	 * cascader9.date10.dateSpan11.time12.timeSpan13.upload14.lable
	 */
	private Integer fieldType;
	/**
	 * 权限级别
	 */
	private Integer authLevel;
	/**
	 * 控件分类
	 */
	private String categoryId;
	/**
	 * 是否隐藏
	 */
	private Boolean hide;
	/**
	 * 标题
	 */
	private String name;
	/**
	 * 描述
	 */
	private String des;
	/**
	 * 警告
	 */
	private String warning;
	/**
	 * ICO图标
	 */
	private String ico;
	/**
	 * 数据库别名
	 */
	private String alias;
	/**
	 * 数据库表名
	 */
	// private String table;
	/**
	 * 文本提示
	 */
	private String placeholder;
	/**
	 * 是否禁用
	 */
	private Boolean disable;

	/**
	 * 是否可以设置，如果false则该控件的设置不可改动
	 */
	// private Boolean setting;

	/**
	 * 标题宽
	 */
	private String labWidth;

	/**
	 * 总宽度
	 */
	private Integer colSpan;

	/**
	 * 是否显示标题
	 */
	private Boolean showTitle;

	/**
	 * 规则列表
	 */
	@JSONField(deserializeUsing = RuleBaseListDeserializer.class)
	private List<RuleBase> rules;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;
	/**
	 * 状态,0.删除;1.基本;2.预设;3.引用;4.规则;
	 */
	private Integer state;
	
	/** 
	* @Fields tableFixedColumnsDisplay : 允许在表格固定列操作控件（gridview表格组专用）
	*/ 
	private Boolean tableFixedColumnsDisplay;
	/** 
	 * @Fields columnsDisplay : 表格栅格占位比例（gridview表格组专用）
	 */ 
	private Integer tableColSpan;
	
	

	public Integer getFieldType() {
		return fieldType;
	}

	public void setFieldType(Integer fieldType) {
		this.fieldType = fieldType;
	}

	public Integer getAuthLevel() {
		return authLevel;
	}

	public void setAuthLevel(Integer authLevel) {
		this.authLevel = authLevel;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Boolean getHide() {
		return hide;
	}

	public void setHide(Boolean hide) {
		this.hide = hide;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		this.ico = ico;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

//	public String getTable() {
//		return table;
//	}
//
//	public void setTable(String table) {
//		this.table = table;
//	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public Boolean getDisable() {
		return disable;
	}

	public void setDisable(Boolean disable) {
		this.disable = disable;
	}

//	public Boolean getSetting() {
//		return setting;
//	}
//
//	public void setSetting(Boolean setting) {
//		this.setting = setting;
//	}

	public List<RuleBase> getRules() {
		return rules;
	}

	public void setRules(List<RuleBase> rules) {
		this.rules = rules;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "WidgetBase [fieldType=" + fieldType + ", authLevel=" + authLevel + ", categoryId=" + categoryId
				+ ", hide=" + hide + ", name=" + name + ", des=" + des + ", warning=" + warning + ", ico=" + ico
				+ ", alias=" + alias + ", placeholder=" + placeholder + ", disable=" + disable
				+ ", labelWidth=" + labWidth + ", colSpan=" + colSpan + ", showTitle=" + showTitle + ", rules="
				+ rules + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", state=" + state
				+ ", tableFixedColumnsDisplay=" + tableFixedColumnsDisplay + "]";
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getLabWidth() {
		return labWidth;
	}

	public void setLabWidth(String labelWidth) {
		this.labWidth = labelWidth;
	}

	public Integer getColSpan() {
		return colSpan;
	}

	public void setColSpan(Integer colSpan) {
		this.colSpan = colSpan;
	}

	public Boolean getShowTitle() {
		return showTitle;
	}

	public void setShowTitle(Boolean showTitle) {
		this.showTitle = showTitle;
	}

	public Boolean getTableFixedColumnsDisplay() {
		return tableFixedColumnsDisplay;
	}

	public void setTableFixedColumnsDisplay(Boolean tableFixedColumnsDisplay) {
		this.tableFixedColumnsDisplay = tableFixedColumnsDisplay;
	}

	public Integer getTableColSpan() {
		return tableColSpan;
	}

	public void setTableColSpan(Integer tableColSpan) {
		this.tableColSpan = tableColSpan;
	}

	
}