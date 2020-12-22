package smartform.widget.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WidgeSelect
 * @Description: 下拉菜单
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgeSelect extends WidgetBase implements Serializable {


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
	 * 是否多选
	 */
	private Boolean multi;
	/**
	 * 最小可选数量
	 */
	private Integer minSelect;
	/**
	 * 最大可选数量
	 */
	private Integer maxSelect;
	/**
	 * 是否可以清空选项
	 */
	private Boolean clear;
	/**
	 * 是否可本地搜索
	 */
	private Boolean filterable;

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

	public Boolean getMulti() {
		return multi;
	}

	public void setMulti(Boolean multi) {
		this.multi = multi;
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

	public Boolean getClear() {
		return clear;
	}

	public void setClear(Boolean clear) {
		this.clear = clear;
	}

	public Boolean getFilterable() {
		return filterable;
	}

	public void setFilterable(Boolean filterable) {
		this.filterable = filterable;
	}

	@Override
	public String toString() {
		return "WidgeSelect [defValue=" + defValue + ", sourceID=" + sourceID + ", disables=" + disables + ", multi="
				+ multi + ", minSelect=" + minSelect + ", maxSelect=" + maxSelect + ", clear=" + clear + ", filterable="
				+ filterable + "]";
	}

}