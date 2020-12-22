package smartform.form.model;

import java.io.Serializable;
import java.util.List;

/***
 * @ClassName: GroupTotalLine
 * @Description: 组合计行
 * @author hou
 * @date 2018年10月17日 上午11:29:47
 * 
 */
public class GroupTotalLine implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * id 合计行Id
	 */
	private String id;
	/**
	 * 行标题
	 */
	private String name;
	
	/**
	 * 合计的组ID
	 */
	private String groupId;

	/**
	 * 合计的起始行
	 */
	private List<Integer> operationList;

	/**
	 * 合计的结束行，默认0合计所有行
	 */
	private Integer resultLine;

	/**
	 * 合计排序，用于执行顺序
	 */
	private Integer sort;

	/**
	 * 合计的首行字段id
	 */
	private List<String> fieldList;

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

	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getOperationList() {
		return operationList;
	}

	public void setOperationList(List<Integer> operationList) {
		this.operationList = operationList;
	}

	public Integer getResultLine() {
		return resultLine;
	}

	public void setResultLine(Integer resultLine) {
		this.resultLine = resultLine;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "GroupTotalLine [id=" + id + ", name=" + name + ", groupId=" + groupId + ", operationList="
				+ operationList + ", resultLine=" + resultLine + ", sort=" + sort + ", fieldList=" + fieldList + "]";
	}

}
