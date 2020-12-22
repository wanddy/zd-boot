package smartform.widget.service;

import smartform.widget.model.FormWidgetInput;
import smartform.widget.model.FormWidgetPagination;
import smartform.widget.model.WidgetBase;

/**
 * @ClassName: FormWidgetService
 * @Description: 表单字段服务
 * @author hou
 * @date 2018年9月19日 上午10:24:57
 */
public interface FormWidgetService {
	/**
	 * 根据id获取一个FormWidget实体，并转化成JSON
	 * 
	 * @param id
	 */
	WidgetBase formWidget(String id);

	/**
	 * 根据输入信息获取一个FormWidget的列表
	 * 
	 * @param page
	 */
	FormWidgetPagination formWidgetList(FormWidgetInput page);

	/**
	 * 创建预设控件
	 * 
	 * @param widget
	 */
	String createFormWidget(String widget);

	/**
	 * 更新预设控件
	 * 
	 * @param widget
	 * 
	 */
	String updateFormWidget(String widget);

	/**
	 * 删除预设控件
	 * 
	 * @param widget
	 */
	String deleteFormWidget(String id);
}
