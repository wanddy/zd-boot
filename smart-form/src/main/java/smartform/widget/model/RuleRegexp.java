package smartform.widget.model;

import java.io.Serializable;

/**
 * @ClassName: RuleRegexp
 * @Description: 正则规则
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleRegexp extends RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 正则类型
	 */
	private String regexpID;
	/**
	 * 正则内容
	 */
	private String regexp;

	public String getRegexpID() {
		return regexpID;
	}

	public void setRegexpID(String regexpID) {
		this.regexpID = regexpID;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	@Override
	public String toString() {
		return "RuleRegexp [regexpID=" + regexpID + ", regexp=" + regexp + "]";
	}

}