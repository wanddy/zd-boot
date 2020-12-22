package smartform.widget.model;

import java.io.Serializable;

/**
 * @ClassName: RuleSymbol
 * @Description: 正负,0.正负,1.正,2.负 符号
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleSymbol extends RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 符号
	 */
	private int plusMinus;
	
	public int getPlusMinus() {
		return plusMinus;
	}
	public void setPlusMinus(int plusMinus) {
		this.plusMinus = plusMinus;
	}

}