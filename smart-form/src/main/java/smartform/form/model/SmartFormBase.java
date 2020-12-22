package smartform.form.model;

import auth.entity.Category;
import com.baomidou.mybatisplus.annotation.TableField;
import smartform.common.model.BaseData;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: SmartFormBase
 * @Description: 表单通用字段
 * @author 表单基类
 * @date 2018年10月4日 下午10:56:15
 * 
 */
public class SmartFormBase extends BaseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 分类Id
	 */
	private String categoryId;
	/**
	 * 分类
	 */
	@TableField(exist = false)
	private FormCategory category;
	/**
	 * 表单名
	 */
	private String name;
	/**
	 * 表单备注
	 */
	private String remarks;
	/**
	 * 表单描述
	 */
	private String des;
	
	/** 
	* @Fields formCode : 表单编码
	*/ 
	private String formCode;
	/**
	 * 选项源列表
	 */
	@TableField(exist = false)
	private List<Category> optionsList;
	/**
	 * 分页列表
	 */
	@TableField(exist = false)
	private List<FormPage> pageList;

	
	/**
	 * 表单额外设置  
	 */
	@TableField(exist = false)
	private FormSettings extraSetting;

	public FormSettings getExtraSetting() {
		return extraSetting;
	}

	public void setExtraSetting(FormSettings extraSetting) {
		this.extraSetting = extraSetting;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public FormCategory getCategory() {
		return category;
	}

	public void setCategory(FormCategory category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public List<Category> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<Category> optionsList) {
		this.optionsList = optionsList;
	}

	public List<FormPage> getPageList() {
		return pageList;
	}

	public void setPageList(List<FormPage> pageList) {
		this.pageList = pageList;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	@Override
	public String toString() {
		return "SmartFormBase [categoryId=" + categoryId + ", category=" + category + ", name=" + name + ", remarks="
				+ remarks + ", des=" + des + ", optionsList=" + optionsList + ", pageList=" + pageList + ", formCode=" + formCode
				+ ", extraSetting=" + extraSetting + "]";
	}
}
