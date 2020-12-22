package workflow.business.model.entity;

import java.util.Date;

public class EditModelInput {
	private String processId;
    private String processKey;
    private String processName;
    private String description;
    private String creater;
    private Date createTime;
    private String updater;
    private Date updateTime;  
    private String info; 
    private String thumbnail;
    private Integer processType;
    private Integer releaseVersion;   
    private int pageNum;
	private int pageSize;
	private int total;
	private String orderInfo;
    
    public EditModelInput() {}

	public EditModelInput(String processId, String processKey, String processName, String description,
			String creater, Date createTime, String updater, Date updateTime, String info, String thumbnail,
			Integer processType, Integer releaseVersion, String orderInfo) {
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
		this.orderInfo = orderInfo;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Integer getProcessType() {
		return processType;
	}

	public void setProcessType(Integer processType) {
		this.processType = processType;
	}

	public Integer getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(Integer releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
	
}
