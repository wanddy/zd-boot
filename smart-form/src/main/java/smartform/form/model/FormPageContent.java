package smartform.form.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** 
* @ClassName: FormPageContent 
* @Description: 表单分页填报提交数据
* @author KaminanGTO
* @date 2019年3月22日 下午2:45:22 
*  
*/
public class FormPageContent implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	* @Fields formId : 表单id
	*/ 
	private String formId;
	
	/** 
	* @Fields pageId : 分页id 
	*/ 
	private String pageId;
	
	/** 
	* @Fields contentId : 填报id
	*/ 
	private String contentId;
	
	/** 
	* @Fields widgetList : 填报字段列表
	*/ 
	private List<FormWidgetContent> widgetList;
	
	/** 
	* @Fields widgets : 填报字段字典缓存，key=字段id+行id
	*/ 
	private Map<String, FormWidgetContent> widgets;

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
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
	
	
}
