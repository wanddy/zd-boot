package smartform.widget.model;

import java.io.Serializable;
import java.util.Date;


/**
 * @ClassName: RuleBase
 * @Description: 规则基类
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class RuleBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 规则ID
	 */
	private String id;
	/**
	 * 是否启用
	 */
	private Boolean enable;
	/**
	 * 规则类型,1.必填，2.长度，3.正则，4.符号，5.小数，6.数字大小，7.预设规则
	 */
	private Integer type;
	/**
	 * 验证失败提示
	 */
	private String message;
	/**
	 * 触发条件
	 */
	private String trigger;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 修改时间
	 */
	private Date modifiedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
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
		return "RuleBase [id=" + id + ", enable=" + enable + ", type=" + type + ", message=" + message + ", trigger="
				+ trigger + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + "]";
	}

}