package smartform.widget.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WidgeCascader
 * @Description: 多级下拉菜单
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgeCascader extends WidgetBase  implements Serializable {


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
	/**
	 * 是否可以清空选项
	 */
	private Boolean clear;
	/**
	 * 是否可本地搜索
	 */
	private Boolean filterable;
	/**
	 * 是否启用远程搜索
	 */
	private Boolean remote;
	/**
	 * 是否允许选择任意一级
	 */
	private Boolean arbitrarily;

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

	public Boolean getRemote() {
		return remote;
	}

	public void setRemote(Boolean remote) {
		this.remote = remote;
	}

	public Boolean getArbitrarily() {
		return arbitrarily;
	}

	public void setArbitrarily(Boolean arbitrarily) {
		this.arbitrarily = arbitrarily;
	}

	@Override
	public String toString() {
		return "WidgeCascader [defValue=" + defValue + ", sourceID=" + sourceID + ", disables=" + disables
				+ ", minSelect=" + minSelect + ", maxSelect=" + maxSelect + ", clear=" + clear + ", filterable="
				+ filterable + ", remote=" + remote + ", arbitrarily=" + arbitrarily + "]";
	}

}