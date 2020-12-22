package smartform.widget.model;

import smartform.common.model.BaseData;

import java.io.Serializable;

/** 
* @ClassName: UploadItem 
* @Description: 上传内容实体
* @author quhanlin
* @date 2018年10月6日 下午10:06:45 
*  
*/
public class UploadItem extends BaseData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 文件名
	 */
	private String name;

	/**
	 * 文件后缀名
	 */
	private String type;

	/**
	 * 文件大小(kb)
	 */
	private Integer size;

	/**
	 * url地址
	 */
	private String url;
	
	/** 
	* @Fields createdAt : 创建时间，排序用
	*/ 
	private Long createdAt;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	
}
