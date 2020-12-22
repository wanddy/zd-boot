package smartform.form.model;

import auth.entity.Category;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import smartform.form.model.deserializer.FormFieldBaseListDeserializer;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: Group
 * @Description: 表单组
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
@TableName("form_group")
public class Group extends FormFieldBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 数据库：超级组件内容的Id
	 */
	@TableField(exist = false)
	private String dataId;
	
	/**
	 * 分类ID
	 */
	private String categoryId;

	/**
	 * 分类名称
	 */
	@TableField(exist = false)
	private String categoryName;

	/**
	 * 组类型 1（超级组件），2（样式组/单行组），3（表格组/可复制组），4栅格组
	 */
	private Integer groupType;

	/**
	 * 是否显示标题
	 */
	private Boolean showName;

	/**
	 * 是否显示边框样式
	 */
	private Boolean showStyle;

	/**
	 * 数据库名
	 */
	private String dbName;

	/**
	 * 数据库表名
	 */
	//@TableField("table_name")
	private String tableName;

	/**
	 * 业务类型，用于识别一个表单中拖入两个相同的超级组件的情况
	 */
	private Integer workType;
	/**
	 * 组标题
	 */
	private String name;
	/**
	 * 组描述
	 */
	private String des;
	/**
	 * 是否隐藏
	 */
	@TableField(exist = false)
	private Boolean hide;

	/**
	 * 是否禁用
	 */
	@TableField(exist = false)
	private Boolean disable;

	/**
	 * 默认行数
	 */
	@TableField(exist = false)
	private Integer defLine;
	/**
	 * 列数
	 */
	@TableField(exist = false)
	private Integer columnCount;
	/**
	 * 是否限制行数
	 */
	@TableField(exist = false)
	private Boolean fillAdd;
	/**
	 * 可添加最小行数
	 */
	@TableField(exist = false)
	private Integer minAdd;
	/**
	 * 可添加最大行数
	 */
	@TableField(exist = false)
	private Integer maxAdd;
	/**
	 * 是否添加序号
	 */
	@TableField(exist = false)
	private Boolean linenum;
	/**
	 * 是否自定义序号
	 */
	@TableField(exist = false)
	private Boolean customLinenum;
	/**
	 * 序号列表
	 */
	@TableField(exist = false)
	private List<String> linenumList;
	/**
	 * 行字段列表
	 */
	@TableField(exist = false)
	private List<GroupLine> lineList;

	/** 
	* @Fields rowList : 列字段列表（栅格组使用）
	*/
	@TableField(exist = false)
	private List<GroupRow> rowList;
	
	/**
	 * 原始行，用于用户新加时拷贝
	 */
	@TableField(exist = false)
	private GroupLine originalLine;

	/**
	 * 固定行列表
	 */
	@TableField(exist = false)
	private List<GroupLine> fixedLineList;

	/**
	 * 公式列表
	 */
	@TableField(exist = false)
	private List<Formula> formulaRules;

	/**
	 * 合计行列表
	 */
	@TableField(exist = false)
	private List<GroupTotalLine> totalRules;

	/**
	 * gridView表格显示内容（前端使用）
	 */
	@TableField(exist = false)
	private List<String> gridViewRules;

	/**
	 * gridView表格显示内容（前端使用）
	 */
	@TableField(exist = false)
	private List<GridViewRulesRow> gridViewRulesRow;
	/**
	 * 是否是可控表格,其下每一行都是可以删除和修改字段属性
	 */
	@TableField(exist = false)
	private Boolean steerable;

	/**
	 * 表头规则设置
	 */
	@TableField(exist = false)
	private List<GroupTableHead> tableHeadRules;

	/**
	 * 字段列表（超级组件，样式组/单行组）
	 */
	@TableField(exist = false)
	@JSONField(deserializeUsing = FormFieldBaseListDeserializer.class)
	private List<FormFieldBase> fieldList;

	/**
	 * 字段json，即超级组件内容
	 */
	private String fieldsJson;

	/**
	 * 选项源列表
	 */
	@TableField(exist = false)
	private List<Category> optionsList;

	/**
	 * 组件复杂条件规则
	 */
	@TableField(exist = false)
	private List<ConditionalRule> conditionRules;

	/**
	 * 规则json，即组件复杂条件规则内容
	 */
	//@TableField(exist = false)
	private String conditionRulesJson;

	/**
	 * 回退状态的组Id列表，表单编辑时才进行赋值
	 */
	@TableField(exist = false)
	private List<String> stateBackGorups;

	/**
	 * 选填，选填的组件或表格可认为已提交状态
	 */
	@TableField(exist = false)
	private Boolean optional;

	/**
	 * 关联表设置
	 */
	@TableField(exist = false)
	private RelationTableSetting relationSet;

	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;

	/**
	 * 表单状态，10:全部; 1:未发布; 2:已发布
	 */
	private Integer state;
	
	/**
	 * 排序字段别名，不为空时，按字段值排序。目前只支持正序
	 */
	private String sortName;
	
	/** 
	* @Fields hideAddLineButton : 隐藏添加行按钮
	*/
	@TableField(exist = false)
	private Boolean hideAddLineButton;
	
	/** 
	* @Fields disableAddLineButton : 禁用添加行按钮
	*/
	@TableField(exist = false)
	private Boolean disableAddLineButton;
	
	/** 
	* @Fields addLineButtonPosition : 选择添加行按钮位置 
	*/
	@TableField(exist = false)
	private String addLineButtonPosition;
	
	/** 
	* @Fields hideOperationBlock : 隐藏操作栏
	*/
	@TableField(exist = false)
	private Boolean hideOperationBlock;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
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

	public Boolean getHide() {
		return hide;
	}

	public void setHide(Boolean hide) {
		this.hide = hide;
	}

	public Integer getDefLine() {
		return defLine;
	}

	public void setDefLine(Integer defLine) {
		this.defLine = defLine;
	}

	public Integer getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(Integer columnCount) {
		this.columnCount = columnCount;
	}

	public Boolean getFillAdd() {
		return fillAdd;
	}

	public void setFillAdd(Boolean fillAdd) {
		this.fillAdd = fillAdd;
	}

	public Integer getMinAdd() {
		return minAdd;
	}

	public void setMinAdd(Integer minAdd) {
		this.minAdd = minAdd;
	}

	public Integer getMaxAdd() {
		return maxAdd;
	}

	public void setMaxAdd(Integer maxAdd) {
		this.maxAdd = maxAdd;
	}

	public Boolean getLinenum() {
		return linenum;
	}

	public void setLinenum(Boolean linenum) {
		this.linenum = linenum;
	}

	public Boolean getCustomLinenum() {
		return customLinenum;
	}

	public void setCustomLinenum(Boolean customLinenum) {
		this.customLinenum = customLinenum;
	}

	public List<String> getLinenumList() {
		return linenumList;
	}

	public void setLinenumList(List<String> linenumList) {
		this.linenumList = linenumList;
	}

	public List<GroupLine> getLineList() {
		return lineList;
	}

	public void setLineList(List<GroupLine> lineList) {
		this.lineList = lineList;
	}

	public List<GroupRow> getRowList() {
		return rowList;
	}

	public void setRowList(List<GroupRow> rowList) {
		this.rowList = rowList;
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

	public List<Formula> getFormulaRules() {
		return formulaRules;
	}

	public void setFormulaRules(List<Formula> formulaRules) {
		this.formulaRules = formulaRules;
	}

	public List<GroupTotalLine> getTotalRules() {
		return totalRules;
	}

	public void setTotalRules(List<GroupTotalLine> totalRules) {
		this.totalRules = totalRules;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public Boolean getShowName() {
		return showName;
	}

	public void setShowName(Boolean showName) {
		this.showName = showName;
	}

	public Boolean getShowStyle() {
		return showStyle;
	}

	public void setShowStyle(Boolean showStyle) {
		this.showStyle = showStyle;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTable() {
		return tableName;
	}

	public void setTable(String tableName) {
		this.tableName = tableName;
	}

	public Integer getWorkType() {
		return workType;
	}

	public void setWorkType(Integer workType) {
		this.workType = workType;
	}

	public List<FormFieldBase> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<FormFieldBase> fieldList) {
		this.fieldList = fieldList;
	}

	public Boolean getDisable() {
		return disable;
	}

	public void setDisable(Boolean disable) {
		this.disable = disable;
	}

	public String getFieldsJson() {
		return fieldsJson;
	}

	public void setFieldsJson(String fieldsJson) {
		this.fieldsJson = fieldsJson;
	}

	public List<Category> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<Category> optionsList) {
		this.optionsList = optionsList;
	}

	public GroupLine getOriginalLine() {
		return originalLine;
	}

	public void setOriginalLine(GroupLine originalLine) {
		this.originalLine = originalLine;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public List<String> getGridViewRules() {
		return gridViewRules;
	}

	public void setGridViewRules(List<String> gridViewRules) {
		this.gridViewRules = gridViewRules;
	}

	public List<GridViewRulesRow> getGridViewRulesRow() {
		return gridViewRulesRow;
	}

	public void setGridViewRulesRow(List<GridViewRulesRow> gridViewRulesRow) {
		this.gridViewRulesRow = gridViewRulesRow;
	}

	public Boolean getSteerable() {
		return steerable;
	}

	public void setSteerable(Boolean steerable) {
		this.steerable = steerable;
	}

	public List<GroupTableHead> getTableHeadRules() {
		return tableHeadRules;
	}

	public void setTableHeadRules(List<GroupTableHead> tableHeadRules) {
		this.tableHeadRules = tableHeadRules;
	}

	/**
	 * 是否是GridView模式,存在GridView规则，并不为空即GridView模式
	 * 
	 * @return
	 */
	public boolean hasGridView() {
		if (this.getGridViewRules() != null && this.getGridViewRules().size() > 0) {
			return true;
		}
		return false;
	}

	public List<ConditionalRule> getConditionRules() {
		return conditionRules;
	}

	public void setConditionRules(List<ConditionalRule> conditionRules) {
		this.conditionRules = conditionRules;
	}

	public List<GroupLine> getFixedLineList() {
		return fixedLineList;
	}

	public void setFixedLineList(List<GroupLine> fixedLineList) {
		this.fixedLineList = fixedLineList;
	}

	public String getConditionRulesJson() {
		return conditionRulesJson;
	}

	public void setConditionRulesJson(String conditionRulesJson) {
		this.conditionRulesJson = conditionRulesJson;
	}

	public List<String> getStateBackGorups() {
		return stateBackGorups;
	}

	public void setStateBackGorups(List<String> stateBackGorups) {
		this.stateBackGorups = stateBackGorups;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean selectFill) {
		this.optional = selectFill;
	}

	public RelationTableSetting getRelationSet() {
		return relationSet;
	}

	public void setRelationSet(RelationTableSetting relationSet) {
		this.relationSet = relationSet;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public Boolean getHideAddLineButton() {
		return hideAddLineButton;
	}

	public void setHideAddLineButton(Boolean hideAddLineButton) {
		this.hideAddLineButton = hideAddLineButton;
	}

	public Boolean getDisableAddLineButton() {
		return disableAddLineButton;
	}

	public void setDisableAddLineButton(Boolean disableAddLineButton) {
		this.disableAddLineButton = disableAddLineButton;
	}

	public String getAddLineButtonPosition() {
		return addLineButtonPosition;
	}

	public void setAddLineButtonPosition(String addLineButtonPosition) {
		this.addLineButtonPosition = addLineButtonPosition;
	}

	public Boolean getHideOperationBlock() {
		return hideOperationBlock;
	}

	public void setHideOperationBlock(Boolean hideOperationBlock) {
		this.hideOperationBlock = hideOperationBlock;
	}
	
	@Override
	public String toString() {
		return "Group [dataId=" + dataId + ", categoryId=" + categoryId + ", groupType=" + groupType + ", showName="
				+ showName + ", showStyle=" + showStyle + ", dbName=" + dbName + ", table=" + tableName + ", workType="
				+ workType + ", name=" + name + ", des=" + des + ", hide=" + hide + ", disable=" + disable
				+ ", defLine=" + defLine + ", columnCount=" + columnCount + ", fillAdd=" + fillAdd + ", minAdd="
				+ minAdd + ", maxAdd=" + maxAdd + ", linenum=" + linenum + ", customLinenum=" + customLinenum
				+ ", linenumList=" + linenumList + ", lineList=" + lineList + ", rowList=" + rowList + ", originalLine=" + originalLine
				+ ", fixedLineList=" + fixedLineList + ", formulaRules=" + formulaRules + ", totalRules=" + totalRules
				+ ", gridViewRules=" + gridViewRules + ", steerable=" + steerable + ", tableHeadRules=" + tableHeadRules
				+ ", fieldList=" + fieldList + ", fieldsJson=" + fieldsJson + ", optionsList=" + optionsList
				+ ", conditionRules=" + conditionRules + ", conditionRulesJson=" + conditionRulesJson
				+ ", stateBackGorups=" + stateBackGorups + ", optional=" + optional + ", relationSet=" + relationSet
				+ ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", state=" + state + ", sortName=" + sortName
				+ ", hideAddLineButton=" + hideAddLineButton + ", disableAddLineButton=" + disableAddLineButton + ", addLineButtonPosition=" + addLineButtonPosition + ", hideOperationBlock=" + hideOperationBlock + "]";
	}
	
}