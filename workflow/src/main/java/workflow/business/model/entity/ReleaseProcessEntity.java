package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;


/**
 * 已发布流程关联数据
 */
@TableName("wf_release_process")
public class ReleaseProcessEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主流程id
	 */
	private String processId;
	
	/**
	 * 主流程key
	 */
	private String processKey;
	
	/**
	 * 主流程版本号
	 */
	private Integer processVersion;
	
	/**
	 * 子流程key
	 */
	private String subKey;
	

	public ReleaseProcessEntity() {
		super();
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
		return processId;
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
     * setter for processVersion
     * @param processVersion
     */
	public void setProcessVersion(Integer processVersion) {
		this.processVersion = processVersion;
	}

    /**
     * getter for processVersion
     */
	public Integer getProcessVersion() {
		return processVersion;
	}
	
    /**
     * setter for subKey
     * @param subKey
     */
	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}

    /**
     * getter for subKey
     */
	public String getSubKey() {
		return subKey;
	}
	
}
