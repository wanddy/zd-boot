package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;


/**
 * 未发布流程数据
 */
@TableName("wf_edit_model")
public class EditModelEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程id
	 */
	private String processId;
	
	/**
	 * 流程key
	 */
	private String processKey;
	
	/**
	 * 流程名称
	 */
	private String processName;
	
	/**
	 * 流程描述
	 */
	private String description;
	
	/**
	 * 创建者
	 */
	private String creater;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新者
	 */
	private String updater;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 明细（json）
	 */
	private String info;
	
	/**
	 * 缩略图
	 */
	private String thumbnail;
	
	/**
	 * 流程类型（主、子）
	 */
	private Integer processType;
	
	/**
	 * 是否有已发布版本
	 */
	private Integer releaseVersion;
	

	public EditModelEntity() {
		super();
	}
	
    public EditModelEntity(String processId, String processKey, String processName, String description, String creater,
			Date createTime, String updater, Date updateTime, String info, String thumbnail, Integer processType,
			Integer releaseVersion) {
		super();
		this.processId = processId;
		this.processKey = processKey;
		this.processName = processName;
		this.description = description;
		this.creater = creater;
		this.createTime = createTime;
		this.updater = updater;
		this.updateTime = updateTime;
		this.info = info;
		this.thumbnail = thumbnail;
		this.processType = processType;
		this.releaseVersion = releaseVersion;
	}



	/**
     * setter for processId
     * @param processId
     */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

    /**
     * getter for processId
     */
	public String getProcessId() {
		return this.processId;
	}
	
    /**
     * setter for processKey
     * @param processKey
     */
	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

    /**
     * getter for processKey
     */
	public String getProcessKey() {
		return processKey;
	}
	
    /**
     * setter for processName
     * @param processName
     */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

    /**
     * getter for processName
     */
	public String getProcessName() {
		return processName;
	}
	
    /**
     * setter for description
     * @param description
     */
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * getter for description
     */
	public String getDescription() {
		return description;
	}
	
    /**
     * setter for creater
     * @param creater
     */
	public void setCreater(String creater) {
		this.creater = creater;
	}

    /**
     * getter for creater
     */
	public String getCreater() {
		return creater;
	}
	
    /**
     * setter for createTime
     * @param createTime
     */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

    /**
     * getter for createTime
     */
	public Date getCreateTime() {
		return createTime;
	}
	
    /**
     * setter for updater
     * @param updater
     */
	public void setUpdater(String updater) {
		this.updater = updater;
	}

    /**
     * getter for updater
     */
	public String getUpdater() {
		return updater;
	}
	
    /**
     * setter for updateTime
     * @param updateTime
     */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

    /**
     * getter for updateTime
     */
	public Date getUpdateTime() {
		return updateTime;
	}
	
    /**
     * setter for thumbnail
     * @param thumbnail
     */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

    /**
     * getter for thumbnail
     */
	public String getThumbnail() {
		return thumbnail;
	}
	
    /**
     * setter for processType
     * @param processType
     */
	public void setProcessType(Integer processType) {
		this.processType = processType;
	}

    /**
     * getter for processType
     */
	public Integer getProcessType() {
		return processType;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(Integer releaseVersion) {
		this.releaseVersion = releaseVersion;
	}
	

}
