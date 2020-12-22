package smartform.widget.model;

import java.io.Serializable;

/**
 * @ClassName: RuleRequired
 * @Description: 必填规则
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleRequired extends RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 是否必填
	 */
	private Boolean required;
	/**
	 * 是否剔除空格
	 */
	private Boolean whitespace;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getWhitespace() {
		return whitespace;
	}

	public void setWhitespace(Boolean whitespace) {
		this.whitespace = whitespace;
	}

	@Override
	public String toString() {
		return "RuleRequired [required=" + required + ", whitespace=" + whitespace + "]";
	}

}