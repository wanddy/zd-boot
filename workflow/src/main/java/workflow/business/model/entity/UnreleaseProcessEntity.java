package workflow.business.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;


/**
 * 未发布流程关联数据
 */
@TableName("wf_unrelease_process")
public class UnreleaseProcessEntity implements Serializable {
	
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
	 * 子流程id（未发布子流程id）
	 */
	private String subId;
	
	/**
	 * 子流程key
	 */
	private String subKey;
	

	public UnreleaseProcessEntity() {
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
     * setter for subId
     * @param subId
     */
	public void setSubId(String subId) {
		this.subId = subId;
	}

    /**
     * getter for subId
     */
	public String getSubId() {
		return subId;
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