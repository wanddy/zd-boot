package smartform.widget.model;

import smartform.form.model.RuleFormula;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: RuleCustom
 * @Description: 预设规则（自定义规则）
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleCustom extends RuleBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 自定义规则Id
	 */
	private String id;

	/**
	 * 自定义规则名称
	 */
	private String ruleName;

	/**
	 * 条件判定规则列表，条件满足才进行结果判定，空则直接运行结果
	 */
	private List<RuleFormula> conditionList;

	/**
	 * 结果判定规则列表，结果为false，进行错误提示
	 */
	private List<RuleFormula> resultList;

	@Override
	public String toString() {
		return "RuleCustom [id=" + id + ", ruleName=" + ruleName + ", conditionList=" + conditionList + ", resultList="
				+ resultList + "]";
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public List<RuleFormula> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<RuleFormula> conditionList) {
		this.conditionList = conditionList;
	}

	public List<RuleFormula> getResultList() {
		return resultList;
	}

	public void setResultList(List<RuleFormula> resultList) {
		this.resultList = resultList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
