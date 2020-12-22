package smartform.widget.model;

import java.io.Serializable;

/**
 * @ClassName: RuleLenght
 * @Description: 长度规则
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleLenght extends RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 最小输入长度
	 */
	private Integer minLenght;
	/**
	 * 最大输入长度
	 */
	private Integer maxLenght;

	public Integer getMinLenght() {
		return minLenght;
	}

	public void setMinLenght(Integer minLenght) {
		this.minLenght = minLenght;
	}

	public Integer getMaxLenght() {
		return maxLenght;
	}

	public void setMaxLenght(Integer maxLenght) {
		this.maxLenght = maxLenght;
	}

	@Override
	public String toString() {
		return "RuleLenght [minLenght=" + minLenght + ", maxLenght=" + maxLenght + "]";
	}

}