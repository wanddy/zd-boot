package smartform.form.model;

import smartform.form.model.FormWidgetContent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** 
* @ClassName: FormGroupLineContent 
* @Description: 表格行内容
* @author KaminanGTO
* @date 2019年3月27日 上午11:40:14 
*  
*/
public class FormGroupLineContent implements Serializable {

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String formId;

	/** 
	* @Fields contentId : 填报id
	*/ 
	private String contentId;
	
	/** 
	* @Fields pageId : 分页id
	*/ 
	private String pageId;
	
	/** 
	* @Fields workType : 业务类型
	*/ 
	private String workType;
	
	/** 
	* @Fields groupId : 组id 
	*/ 
	private String groupId;
	
	/** 
	* @Fields lineId : 行id
	*/ 
	private String lineId;
	
	/** 
	* @Fields lineNum : 行号
	*/ 
	private int lineNum;
	
	/** 
	* @Fields sort : 排序值 
	*/ 
	private Integer sort;
	
	/** 
	* @Fields widgetList : 填报字段列表，行保存只需要字段id和数据值
	*/ 
	private List<FormWidgetContent> widgetList;

	/** 
	* @Fields widgets : 填报字段字典缓存，key=字段id
	*/ 
	private Map<String, FormWidgetContent> widgets;
	
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public List<FormWidgetContent> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(List<FormWidgetContent> widgetList) {
		this.widgetList = widgetList;
	}

	public Map<String, FormWidgetContent> getWidgets() {
		return widgets;
	}

	public void setWidgets(Map<String, FormWidgetContent> widgets) {
		this.widgets = widgets;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
}
