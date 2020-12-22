package smartform.widget.model;

import java.io.Serializable;

/**
 * @ClassName: WidgetInput
 * @Description: 文本类控件
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgetInput extends WidgetBase implements Serializable {


	private static final long serialVersionUID = 1L;
	
	/**
	 * 文本默认值
	 */
	private String defValue;
	/**
	 * 后缀类型,1.文本，2.URL
	 */
	private Integer suffixType;
	/**
	 * 后缀文本
	 */
	private String suffix;
	/**
	 * 最大文本长度
	 */
	private String txtmaxLength;

	
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

	public Integer getSuffixType() {
		return suffixType;
	}

	public void setSuffixType(Integer suffixType) {
		this.suffixType = suffixType;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTxtmaxLength() {
		return txtmaxLength;
	}

	public void setTxtmaxLength(String txtmaxLength) {
		this.txtmaxLength = txtmaxLength;
	}

	@Override
	public String toString() {
		return "WidgetInput [defValue=" + defValue + ", suffixType=" + suffixType + ", suffix=" + suffix + "]";
	}

}