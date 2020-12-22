package workflow.business.model.entity;

import workflow.ide.core.Definitions;

import java.io.Serializable;


/**
 * 未发布流程数据
 */

public class SaveUndeployedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 流程id
	 */
	private String processId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 流程对象
	 */
	private Definitions definitions;

	/**
	 * mode 1: 新增 2：更新
	 */
	private Integer mode;
	/**
	 * 流程脚本
	 */
	private String xmlText;
	/**
	 * 流程key
	 */
	private String processKey;


	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Definitions getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Definitions definitions) {
		this.definitions = definitions;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public SaveUndeployedEntity() {
		super();
	}

    public SaveUndeployedEntity(String processId, String userId, int mode, Definitions definitions,String xmlText,String processKey) {
		super();
		this.processId = processId;
		this.userId = userId;
		this.mode = mode;
		this.definitions = definitions;
		this.xmlText = xmlText;
		this.processKey = processKey;
	}



}
