package smartform.form.model;

import com.baomidou.mybatisplus.annotation.TableName;
import smartform.common.model.BaseData;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: FormCategory
 * @Description: 表单分类
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
@TableName("form_category")
public class FormCategory extends BaseData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分类名
	 */
	private String name;
	/**
	 * 排序
	 */
	private Integer sort;
	
	/**
	 * 业务编号
	 */
	private String code;

	private String parentId;
	
	/**
	 * 类型，1:表单,2:定制组件
	 */
	private Integer categoryType;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;

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

	@Override
	public String toString() {
		return "FormCategory [name=" + name + ", sort=" + sort + ", code=" + code + ", parentId=" + parentId
				+ ", categoryType=" + categoryType + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(Integer categoryType) {
		this.categoryType = categoryType;
	}
}