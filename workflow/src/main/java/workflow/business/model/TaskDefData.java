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
public class TaskDefData implements Serializable {
	
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
	 * @Fields formDataList : 表单列表
	 */
	private List<FormData> formDataList;


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

	public List<FormData> getFormDataList() {
		return formDataList;
	}

	public void setFormDataList(List<FormData> formDataList) {
		this.formDataList = formDataList;
	}
}
