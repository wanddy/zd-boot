package workflow.business.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ComplateTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2810004754227658283L;
	
	/**
	 * 操作人
	 */
	private String userId;
	
	/**
	 * 所属区县
	 */
	private String region;
	
	/**
	 * 部门id
	 */
	private List<String> unitIds; 
	
	/**
	 * 参数<字段名,字段值>
	 */
	private Map<String, String> values;

	/**
	 * 自定义参数<字段名,字段值>
	 */
	private Map<String, String> exValues;
	/**
	 * 会签用户信息<会签用户信息ID，<会签用户id列表>>
	 */
	private Map<String, List<String>> signUsers;
	
	/**
	 * 认领用户信息<认领用户信息ID，<认领用户id列表>>
	 */
	private Map<String, List<String>> claimUsers; 

	/**
	 * 认领用户组<认领用户组信息ID，<认领用户组id列表>>
	 */
	private Map<String, List<String>> claimGroups;
	
	/**
	 * 操作人
	 */
	private String userName;
	
	/**
	 * 操作人所属部门
	 */
	private String deptName; 

	/**
	 * 操作人所属部门
	 */
	private String opResult;

	/**
	 * 操作备注
	 */
	private String memo;
	
	/**
	 * 当前任务
	 */
	private ActiveTask activeTask;
	/**
	 * 当前任务Id
	 */
	private String activeTaskId;
	/**
	 * 错误信息code
	 */
	private String errorCode;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	/**
	 * 操作时间
	 */
	private Date createdAt;
	/**
	 * 任务id
	 */
	private String taskId;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<String> getUnitIds() {
		return unitIds;
	}

	public void setUnitIds(List<String> unitIds) {
		this.unitIds = unitIds;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public Map<String, List<String>> getSignUsers() {
		return signUsers;
	}

	public void setSignUsers(Map<String, List<String>> signUsers) {
		this.signUsers = signUsers;
	}

	public Map<String, List<String>> getClaimUsers() {
		return claimUsers;
	}

	public void setClaimUsers(Map<String, List<String>> claimUsers) {
		this.claimUsers = claimUsers;
	}

	public Map<String, List<String>> getClaimGroups() {
		return claimGroups;
	}

	public void setClaimGroups(Map<String, List<String>> claimGroups) {
		this.claimGroups = claimGroups;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getOpResult() {
		return opResult;
	}

	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public ActiveTask getActiveTask() {
		return activeTask;
	}

	public void setActiveTask(ActiveTask activeTask) {
		this.activeTask = activeTask;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Map<String, String> getExValues() {
		return exValues;
	}

	public void setExValues(Map<String, String> exValues) {
		this.exValues = exValues;
	}

	public String getActiveTaskId() {
		return activeTaskId;
	}

	public void setActiveTaskId(String activeTaskId) {
		this.activeTaskId = activeTaskId;
	}
}
