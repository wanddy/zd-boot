package smartform.widget.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WidgeRadio
 * @Description: 单选框
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgeRadio extends WidgetBase  implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 默认值
	 */
	private String defValue;
	/**
	 * 选项源_ID
	 */
	private String sourceID;
	/**
	 * 禁用选项列表
	 */
	private List<String> disables;

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

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public List<String> getDisables() {
		return disables;
	}

	public void setDisables(List<String> disables) {
		this.disables = disables;
	}

	@Override
	public String toString() {
		return "WidgeRadio [defValue=" + defValue + ", sourceID=" + sourceID + ", disables=" + disables + "]";
	}

}