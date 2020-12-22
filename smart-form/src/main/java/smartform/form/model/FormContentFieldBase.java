package smartform.form.model;

import java.io.Serializable;
import java.util.Date;

/** 
* @ClassName: FormContentFieldBase 
* @Description: 内容字段基类，目前只用于上下文
* @author quhanlin
* @date 2018年10月7日 上午11:06:49 
*  
*/
public class FormContentFieldBase implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 内容id
	 */
	private String contentId;
	
	/**
	 * 分页ID
	 */
	private String pageId;
	
	/**
	 * 组ID
	 */
	private String groupId;
	
	/**
	 * 行号
	 */
	private Integer linenum;
	
	/**
	 * 字段ID
	 */
	private String fieldId;
	
	/**
	 * 创建时间
	 */
	private Date createdAt;
	
	/**
	 * 修改时间
	 */
	private Date modifiedAt;
	

	public FormContentFieldBase() {
		super();
	}

    /**
     * setter for id
     * @param id
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * getter for id
     */
	public String getId() {
		return id;
	}
	
    /**
     * setter for contentId
     * @param contentId
     */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

    /**
     * getter for contentId
     */
	public String getContentId() {
		return contentId;
	}
	
    /**
     * setter for pageId
     * @param pageId
     */
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

    /**
     * getter for pageId
     */
	public String getPageId() {
		return pageId;
	}
	
    /**
     * setter for groupId
     * @param groupId
     */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

    /**
     * getter for groupId
     */
	public String getGroupId() {
		return groupId;
	}
	
    /**
     * setter for linenum
     * @param linenum
     */
	public void setLinenum(Integer linenum) {
		this.linenum = linenum;
	}

    /**
     * getter for linenum
     */
	public Integer getLinenum() {
		return linenum;
	}
	
    /**
     * setter for fieldId
     * @param fieldId
     */
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

    /**
     * getter for fieldId
     */
	public String getFieldId() {
		return fieldId;
	}
	
    /**
     * setter for createdAt
     * @param createdAt
     */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

    /**
     * getter for createdAt
     */
	public Date getCreatedAt() {
		return createdAt;
	}
	
    /**
     * setter for modifiedAt
     * @param modifiedAt
     */
	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

    /**
     * getter for modifiedAt
     */
	public Date getModifiedAt() {
		return modifiedAt;
	}
	
}
