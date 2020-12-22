package smartform.form.model.entity;

import java.io.Serializable;

/** 
* @ClassName: FormContentUploadsEntity 
* @Description: 上传列表实体
* @author quhanlin
* @date 2018年11月1日 下午6:29:16 
*  
*/
public class FormContentUploadsEntity implements Serializable {

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
	 * 超级组件表名
	 */
	private String tableName;

	/**
	 * 业务类型，当表名相同时用于区分识别组件
	 */
	private Integer workType;

	/**
	 * 上传附件的业务类型
	 */
	private Integer uploadType;

	/**
	 * 行号ID，当为表格组时使用
	 */
	private String lineId;

	/**
	 * 文件地址
	 */
	private String url;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件后缀名
	 */
	private String suffix;

	/**
	 * 文件大小(kb)
	 */
	private Integer size;

	/**
	 * 创建时间
	 */
	private Long createdAt;

	/**
	 * 修改时间
	 */
	private Long modifiedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getWorkType() {
		return workType;
	}

	public void setWorkType(Integer workType) {
		this.workType = workType;
	}

	public Integer getUploadType() {
		return uploadType;
	}

	public void setUploadType(Integer uploadType) {
		this.uploadType = uploadType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	@Override
	public String toString() {
		return "FormContentUploadsEntity [id=" + id + ", contentId=" + contentId + ", tableName=" + tableName
				+ ", workType=" + workType + ", uploadType=" + uploadType + ", lineId=" + lineId + ", url=" + url
				+ ", fileName=" + fileName + ", suffix=" + suffix + ", size=" + size + ", createdAt=" + createdAt
				+ ", modifiedAt=" + modifiedAt + "]";
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

}
