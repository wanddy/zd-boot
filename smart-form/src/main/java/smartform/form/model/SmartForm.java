package smartform.form.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: SmartForm
 * @Description: 表单
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
@TableName("form")
public class SmartForm extends SmartFormBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 发布者
	 */
	private String userId;

	/**
	 * 内容主表库名
	 */
	private String dbName;

	/**
	 * 内容主表名
	 */
	private String tableName;

	/**
	 * 分页json，即表单结构内容
	 */
	private String pagesJson;

	/**
	 * 内容主表字段映射
	 */
	@TableField(exist = false)
	private List<DBFieldMapper> fieldMapperList;

	/**
	 * 内容主表字段映射json，用于mysql持久
	 */
	private String fieldMapperJson;

	/**
	 * 跨分页复杂条件规则
	 */
	@TableField(exist = false)
	private List<ConditionalRule> conditionRules;
	
	/**
	 * 规则json，即组件复杂条件规则内容
	 */
	private String conditionRulesJson;

	/**
	 * 表单额外设置json  
	 */
	private String extraSettingJson;

	/**
	 * 表单状态，0,删除; 1,新建; 2,发布
	 */
	private Integer state;

	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;
	
	/*
	 * 是否跳过时效验证 1:跳过验证 0：不跳过  默认0
	 * */
	@TableField("skip_endtime_validation")
	private String skipendtimevalidation;
	
	public String getSkipendtimevalidation() {
		return skipendtimevalidation;
	}

	public void setSkipendtimevalidation(String skipendtimevalidation) {
		this.skipendtimevalidation = skipendtimevalidation;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getPagesJson() {
		return pagesJson;
	}

	public void setPagesJson(String pagesJson) {
		this.pagesJson = pagesJson;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<DBFieldMapper> getFieldMapperList() {
		return fieldMapperList;
	}

	public void setFieldMapperList(List<DBFieldMapper> fieldMapperList) {
		this.fieldMapperList = fieldMapperList;
	}

	public String getFieldMapperJson() {
		return fieldMapperJson;
	}

	public void setFieldMapperJson(String fieldMapperJson) {
		this.fieldMapperJson = fieldMapperJson;
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

	public List<ConditionalRule> getConditionRules() {
		return conditionRules;
	}

	public void setConditionRules(List<ConditionalRule> conditionRules) {
		this.conditionRules = conditionRules;
	}

	public String getConditionRulesJson() {
		return conditionRulesJson;
	}

	public void setConditionRulesJson(String conditionRulesJson) {
		this.conditionRulesJson = conditionRulesJson;
	}


	public String getExtraSettingJson() {
		return extraSettingJson;
	}

	public void setExtraSettingJson(String extraSettingJson) {
		this.extraSettingJson = extraSettingJson;
	}

	@Override
	public String toString() {
		return "SmartForm [userId=" + userId + ", dbName=" + dbName + ", tableName=" + tableName + ", pagesJson="
				+ pagesJson + ", fieldMapperList=" + fieldMapperList + ", fieldMapperJson=" + fieldMapperJson
				+ ", conditionRules=" + conditionRules + ", conditionRulesJson=" + conditionRulesJson
				+ ", extraSettingJson=" + extraSettingJson + ", state=" + state + ", createdAt=" + createdAt
				+ ", modifiedAt=" + modifiedAt+ ", skipendtimevalidation=" + skipendtimevalidation + "]";
	}
}