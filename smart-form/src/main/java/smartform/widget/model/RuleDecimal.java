package smartform.widget.model;

import java.io.Serializable;

/**
 * @ClassName: RuleDecimal
 * @Description: 小数
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleDecimal extends RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 小数
	 */
	private Boolean decimal;
	/**
	 * 保留的小数位
	 */
	private Integer decimalLenght;

	public Boolean getDecimal() {
		return decimal;
	}

	public void setDecimal(Boolean decimal) {
		this.decimal = decimal;
	}

	public Integer getDecimalLenght() {
		return decimalLenght;
	}

	public void setDecimalLenght(Integer decimalLenght) {
		this.decimalLenght = decimalLenght;
	}

	@Override
	public String toString() {
		return "RuleDecimal [decimal=" + decimal + ", decimalLenght=" + decimalLenght + "]";
	}

}