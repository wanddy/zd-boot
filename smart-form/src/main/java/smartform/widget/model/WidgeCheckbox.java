package smartform.widget.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WidgeCheckbox
 * @Description: 多选框
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgeCheckbox extends WidgetBase  implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 默认值
	 */
	private List<String> defValue;
	/**
	 * 选项源_ID
	 */
	private String sourceID;
	/**
	 * 禁用选项列表
	 */
	private List<String> disables;
	/**
	 * 最小可选数量
	 */
	private Integer minSelect;
	/**
	 * 最大可选数量
	 */
	private Integer maxSelect;

    public List<String> getDefValue() {
		return defValue;
	}

	public void setDefValue(List<String> defValue) {
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

	public Integer getMinSelect() {
		return minSelect;
	}

	public void setMinSelect(Integer minSelect) {
		this.minSelect = minSelect;
	}

	public Integer getMaxSelect() {
		return maxSelect;
	}

	public void setMaxSelect(Integer maxSelect) {
		this.maxSelect = maxSelect;
	}

	@Override
	public String toString() {
		return "WidgeCheckbox [defValue=" + defValue + ", sourceID=" + sourceID + ", disables=" + disables
				+ ", minSelect=" + minSelect + ", maxSelect=" + maxSelect + "]";
	}

}