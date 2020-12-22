package smartform.widget.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: RuleSize
 * @Description: 数字大小规则
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleSize extends RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 数字最小值
	 */
	private BigDecimal minValue;
	/**
	 * 数字最小值
	 */
	private BigDecimal maxValue;
	
	public BigDecimal getMinValue() {
		return minValue;
	}
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}
	public BigDecimal getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

}