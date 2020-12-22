package smartform.form.model;

import auth.entity.Category;
import com.alibaba.fastjson.annotation.JSONField;
import smartform.common.model.BaseData;
import smartform.form.model.deserializer.FormFieldBaseListDeserializer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: FormPage
 * @Description: 表单分页
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class FormPage extends BaseData implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 表单ID
	 */
	private String formID;
	/**
	 * 标题
	 */
	private String name;
	
	/**
	 * 是否隐藏分页
	 */
	private Boolean hide;
	
	/**
	 * 填报状态，0.未填，1.暂存，2.完成
	 */
	private Integer fillState;

	/**
	 * 优先级
	 */
	private Integer priority;
	
	/**
	 * 排序
	 */
	private Integer sort;
	
	/**
	 * 自定义表单标示，用于使用外部表单
	 */
	private String customFormKey;
	
	/** 
	* @Fields parentId : 父页id，为空时是一级目录。（前端展示用）
	*/ 
	private String parentId;
	
	/** 
	* @Fields pageCode : 分页表单编码-业务用
	*/ 
	private String pageCode;
	
	/** 
	* @Fields ignoreState : 是否忽略状态--业务用
	*/ 
	private Boolean ignoreState;
	
	/**
	 * 字段列表
	 */
	@JSONField(deserializeUsing = FormFieldBaseListDeserializer.class)
	private List<FormFieldBase> fieldList;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;
	
	/**
	 * 选项源列表
	 */
	private List<Category> optionsList;

	public String getFormID() {
		return formID;
	}

	public void setFormID(String formID) {
		this.formID = formID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getCustomFormKey() {
		return customFormKey;
	}

	public void setCustomFormKey(String customFormKey) {
		this.customFormKey = customFormKey;
	}

	public List<FormFieldBase> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<FormFieldBase> fieldList) {
		this.fieldList = fieldList;
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

	public Integer getFillState() {
		return fillState;
	}

	public void setFillState(Integer fillState) {
		this.fillState = fillState;
	}
	
	@Override
	public String toString() {
		return "FormPage [id=" + this.getId() + ", formID=" + formID + ", name=" + name + ", hide=" + hide + ", fillState="
				+ fillState + ", priority=" + priority + ", sort=" + sort + ", parentId=" + parentId + ", pageCode=" + pageCode + ", customFormKey=" + customFormKey + ", fieldList=" + fieldList + ", createdAt="
				+ createdAt + ", modifiedAt=" + modifiedAt + ", ignoreState=" + ignoreState + "]";
	}

	public Boolean getHide() {
		return hide;
	}

	public void setHide(Boolean hide) {
		this.hide = hide;
	}

	public Integer getPriority() {
		if(priority == null)
			priority = 0;
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public List<Category> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<Category> optionsList) {
		this.optionsList = optionsList;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPageCode() {
		return pageCode;
	}

	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}

	public Boolean getIgnoreState() {
		return ignoreState;
	}

	public void setIgnoreState(Boolean ignoreState) {
		this.ignoreState = ignoreState;
	}
	
	
}