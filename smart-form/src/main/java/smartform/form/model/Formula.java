package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/*** 
* @ClassName: Formula 
* @Description: 表单公式
* @author hou
* @date 2018年10月17日 上午11:39:37 
*  
*/
public class Formula implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 应用的组ID
	 */
	private String groupId;

	/**
	 * 运算单元列表
	 */
	private List<OperatorUnit> operatorList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<OperatorUnit> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(List<OperatorUnit> operatorList) {
		this.operatorList = operatorList;
	}

	@Override
	public String toString() {
		return "Formula [id=" + id + ", groupId=" + groupId + ", operatorList=" + operatorList + "]";
	}
}
