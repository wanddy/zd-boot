package smartform.form.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 超级组件规则数据
 * @author KaminanGTO
 *
 */
public class SuperGroupRule implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 控件id
	 */
	private String id;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 超级组件规则列表（跨字段规则）
	 */
	private List<ConditionalRule> rules;
	
	/**
	 * 公式列表
	 */
	private List<Formula> formulaRules;
	
	/**
	 * 字段规则列表 map（别名，字段规则
	 */
	private Map<String, FieldRules> fieldRules;
	
	/**
	 * 字段规则列表 map（id_line，字段规则）
	 */
	private Map<String, FieldRules> fieldIdRules;
	
	/**
	 * 根据别名取字段规则
	 * @param alias
	 * @return
	 */
	public FieldRules getRulesByAlias(String alias)
	{
		return fieldRules.get(alias);
	}
	
	/**
	 * 根据控件id取字段规则
	 * @param id
	 * @return
	 */
	public FieldRules getRulesById(String id)
	{
		return fieldIdRules.get(id + "_" + -1);
	}
	
	/**
	 * 根据控件id和所在行号取字段规则
	 * @param id
	 * @param lineNum
	 * @return
	 */
	public FieldRules getRulesByIdLine(String id, int lineNum)
	{
		return fieldIdRules.get(id + "_" + lineNum);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<ConditionalRule> getRules() {
		return rules;
	}

	public void setRules(List<ConditionalRule> rules) {
		this.rules = rules;
	}

	public List<Formula> getFormulaRules() {
		return formulaRules;
	}

	public void setFormulaRules(List<Formula> formulaRules) {
		this.formulaRules = formulaRules;
	}

	public Map<String, FieldRules> getFieldRules() {
		return fieldRules;
	}

	public void setFieldRules(Map<String, FieldRules> fieldRules) {
		this.fieldRules = fieldRules;
	}

	public Map<String, FieldRules> getFieldIdRules() {
		return fieldIdRules;
	}

	public void setFieldIdRules(Map<String, FieldRules> fieldIdRules) {
		this.fieldIdRules = fieldIdRules;
	}
	
}
