package smartform.form.service;

import smartform.form.model.FormFieldBase;
import smartform.form.model.SmartForm;
import smartform.form.model.SmartFormContent;

import java.text.ParseException;

/**
 * @ClassName: SmartFormContentService
 * @Description: 表单内容服务
 * @author hou
 * @date 2018年10月5日 下午3:06:45
 */
public interface SmartFormContentService {

	/**
	 * 获取表单填报结构与内容，用于首次填报
	 * @param type （1.指南）
	 * @param formId 表单ID
	 * @param userId
	 * @param extraData 额外参数（json字符串）
	 * @return
	 * @throws ParseException 
	 */
	SmartFormContent smartFormContent(int type, String formId, String userId, String extraData) throws ParseException;

	/**
	 * 暂存表单内容
	 * 
	 * @param form
	 * @param pageId 页码，默认表单第一页
	 * @return
	 * @throws ParseException 
	 */
	SmartFormContent storageFormContent(String form, String pageId) throws ParseException;

	/**
	 * 查询分页字段
	 * 
	 * @param smartForm
	 * @param fieldId
	 * @return
	 */
	FormFieldBase getFormFieldByForm(SmartForm smartForm, String fieldId);

	/**
	 * 提交表单分页内容
	 * @param form
	 * @param pageId
	 * @return
	 * @throws ParseException
	 */
	SmartFormContent submitFormContentPage(String form, String pageId) throws ParseException;

	/**
	 * 提交整套表单
	 * @param formId
	 * @param contentId
	 * @return
	 * @throws ParseException 
	 */
	String submitFormContent(String formId, String contentId) throws ParseException;
	
	/**
	 * 提交gridView行
	 * @param formId
	 * @param contentId
	 * @param groupId
	 * @param line
	 * @return
	 * @throws ParseException 
	 */
	SmartFormContent submitGridLine(String formId, String contentId, String groupId, String line, Boolean storage) throws ParseException;
	
	/**
	 * 删除gridView行
	 * @param formId
	 * @param contentId
	 * @param groupId
	 * @param lineId
	 * @return
	 */
	SmartFormContent deleteGridLine(String formId, String contentId, String groupId, String lineId);
	
	/**
	 * 获取表单填报结构与内容,用于编辑填报内容
	 * 
	 * @param id
	 * @param pageId 页码，默认表单第一页，表单结构一次性获取，内容按分页加载
	 * @return
	 * @throws ParseException 
	 */
	SmartFormContent smartFormContentById(String formId, String id, String pageId, boolean justPageState)
			throws ParseException;
	
	/**
	 * 只获取当前表单分页状态
	 * @param formId
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	SmartFormContent smartFormContentPageState(String formId, String id)
			throws ParseException;
}
