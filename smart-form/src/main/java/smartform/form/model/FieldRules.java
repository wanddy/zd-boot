package smartform.form.model;

import smartform.widget.model.RuleBase;

import java.io.Serializable;
import java.util.List;

/**
 * 控件规则数据
 * @author KaminanGTO
 *
 */
public class FieldRules implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 控件id
	 */
	private String id;
	
	/**
	 * 所属组id
	 */
	private String groupId;
	
	/**
	 * 所在行号
	 */
	private Integer lineNum;
	
	/**
	 * 字段名
	 */
	private String alias;
	
	/**
	 * 规则列表
	 */
	private List<RuleBase> rules;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<RuleBase> getRules() {
		return rules;
	}

	public void setRules(List<RuleBase> rules) {
		this.rules = rules;
	}
	
	
}
