package workflow.business.model;

import java.io.Serializable;
import java.util.List;

/** 
* @ClassName: TaskDefData 
* @Description: 任务定义数据
* @author KaminanGTO
* @date 2018年11月9日 下午6:19:48 
*  
*/
public class TaskDefDataActHis implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 
	* @Fields id : 任务定义ID
	*/ 
	private String id;
	
	/** 
	* @Fields name : 任务名称
	*/ 
	private String name;
	
	/** 
	* @Fields sort : 排序值
	*/ 
	private int sort;
	
	/** 
	* @Fields judgeList : 判断型变量列表
	*/ 
	private List<JudgeProperty> judgeList;
	/**
	当前状态  0：未执行过节点 1：当前正在执行节点 2：节点已执行过
	 */
	private String state;
	/**
	 用户名称
	 */
	private String username;
	/**
	 用户单位
	 */
	private String unitname;
	/**
	 用户操作
	 */
	private String operate;
	/**
	 意见/备注
	 */
	private String memo;

	/**
	 审核时间
	 */
	private Long operatetime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public List<JudgeProperty> getJudgeList() {
		return judgeList;
	}

	public void setJudgeList(List<JudgeProperty> judgeList) {
		this.judgeList = judgeList;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Long getOperatetime() {
		return operatetime;
	}

	public void setOperatetime(Long operatetime) {
		this.operatetime = operatetime;
	}
}
