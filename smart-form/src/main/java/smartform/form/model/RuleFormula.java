package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/*** 
* @ClassName: RuleFormula 
* @Description: 规则公式
* @author hou
* @date 2018年12月3日 上午11:39:37 
*  
*/
public class RuleFormula implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 类型 1：公式，2，逻辑运算符
	 */
	private Integer type;

	/**
	 * 运算单元列表
	 */
	private List<OperatorUnit> operatorList;

	/**
	 * 逻辑运算符
	 */
	private Integer logicOperator;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<OperatorUnit> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(List<OperatorUnit> operatorList) {
		this.operatorList = operatorList;
	}

	public Integer getLogicOperator() {
		return logicOperator;
	}

	public void setLogicOperator(Integer logicOperator) {
		this.logicOperator = logicOperator;
	}

	@Override
	public String toString() {
		return "RuleFormula [type=" + type + ", operatorList=" + operatorList + ", logicOperator=" + logicOperator
				+ "]";
	}
}
