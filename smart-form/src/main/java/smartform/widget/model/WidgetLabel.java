package smartform.widget.model;

import lombok.NoArgsConstructor;

/**
* @ClassName: WidgetLabel 
* @Description: 文本描述
* @author quhanlin
* @date 2018年11月5日 上午10:50:27 
*
*/
@NoArgsConstructor
public class WidgetLabel extends WidgetBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文本描述
	 */
	private String defValue;

	/**
	 * css样式
	 */
	private String css;

	/**
	 * 分割线：1，上分割线；2，下分割线；3，上下都有
	 */
	private Integer splitLine;
	
	/** 
	* @Fields tipIcon : 描述icon
	*/ 
	private String tipIcon;

    public String getDefValue() {
		/*if(null!=super.getDefValue()){
			defValue = (String) super.getDefValue();
			super.setDefValue(null);
		}*/
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public Integer getSplitLine() {
		return splitLine;
	}

	public void setSplitLine(Integer splitLine) {
		this.splitLine = splitLine;
	}

	public String getTipIcon() {
		return tipIcon;
	}

	public void setTipIcon(String tipIcon) {
		this.tipIcon = tipIcon;
	}

	@Override
	public String toString() {
		return "WidgetLabel [defValue=" + defValue + ", css=" + css + ", tipIcon=" + tipIcon + ", splitLine=" + splitLine + "]";
	}

}