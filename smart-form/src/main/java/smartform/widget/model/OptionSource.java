package smartform.widget.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import smartform.common.model.BaseData;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: OptionSource
 * @Description: 选项源
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
@TableName("form_optionsource")
public class OptionSource extends BaseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 分类ID
	 */
	private String categoryId;
	/**
	 * 是否是单级
	 */
	@TableField("is_single")
	private Boolean single;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 源名称
	 */
	private String name;
	
	/**
	 * 源的type
	 */
	private String optionsType;
	
	/**
	 * 源的code
	 */
	private String optionsCode;

	/**
	 * 选项列表
	 */
	@TableField(exist = false)
	private List<Option> options;
	/**
	 * Json字符串
	 */
	@TableField(exist = false)
	private String optionsJson;
	
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;
	/**
	 * 状态,0,删除;1,发布;2.新建
	 */
	private Integer state;


	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Boolean getSingle() {
		return single;
	}

	public void setSingle(Boolean single) {
		this.single = single;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "OptionSource [categoryId=" + categoryId + ", single=" + single + ", sort=" + sort + ", name=" + name
				+ ", optionsType=" + optionsType + ", optionsCode=" + optionsCode + ", options=" + options
				+ ", optionsJson=" + optionsJson + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt
				+ ", state=" + state + "]";
	}

	public String getOptionsJson() {
		return optionsJson;
	}

	public void setOptionsJson(String optionsJson) {
		this.optionsJson = optionsJson;
	}

	public String getOptionsType() {
		return optionsType;
	}

	public void setOptionsType(String optionsType) {
		this.optionsType = optionsType;
	}

	public String getOptionsCode() {
		return optionsCode;
	}

	public void setOptionsCode(String optionsCode) {
		this.optionsCode = optionsCode;
	}

}