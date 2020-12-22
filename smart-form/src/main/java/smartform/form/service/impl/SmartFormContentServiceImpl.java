package smartform.form.service.impl;

//import com.alibaba.dubbo.config.annotation.Reference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartform.common.error.SmartFormError;
import smartform.common.util.CheckDataUtil;
import smartform.common.util.UUIDUtil;
import smartform.form.mapper.*;
import smartform.form.model.*;
import smartform.form.model.entity.ComponentStateEntity;
import smartform.form.model.entity.FormContentComponentEntity;
import smartform.form.model.entity.FormContentTableEntity;
import smartform.form.model.entity.FormContentUploadsEntity;
import smartform.form.redis.SmartFormContentService;
import smartform.form.service.SmartFormService;
import smartform.query.Query;
import smartform.widget.model.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import smartform.framework.annotation.DataSource;
//import smartform.framework.orm.db.DataSourceEnum;
//import smartform.framework.orm.db.DynamicDataSource;

//import stcsm.feasiblescheme.api.StcsmFeasibleschemeApi;

/**
 * @ClassName: SmartFormContentService
 * @Description: 表单内容服务
 * @author hou
 * @date 2018年10月5日 下午3:59:45
 */

/**
 * @ClassName: SmartFormContentServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Admin
 * @date 2018年11月7日 下午7:03:31
 *
 */
@Service("SmartFormContentService")
public class SmartFormContentServiceImpl implements smartform.form.service.SmartFormContentService {

	// 实例化
	private static Logger LOG = LoggerFactory.getLogger(SmartFormContentServiceImpl.class);

	/**
	 * 表单服务
	 */
	@Autowired
	SmartFormService smartFormService;

	/**
	 * SmartForm内容基本信息的redis操作类
	 */
	@Autowired
	private SmartFormContentService formContentDao;

	/**
	 * 内容主表操作
	 */
	@Autowired
	FormContentMainMapper contentMainDao;

	/**
	 * 组件表操作
	 */
	@Autowired
	FormContentComponentMapper componentDao;

	/**
	 * 表格表操作
	 */
	@Autowired
	FormContentTableMapper tableDao;

	/**
	 * 组件状态操作
	 */
	@Autowired
	ComponentStateMapper componentStateDao;

	/**
	 * 上传列表操作
	 */
	@Autowired
	FormContentUploadsMapper uploadsDao;

	/*@Reference
	StcsmFeasibleschemeApi stcsmFeasibleschemeApi;*/

	/**
	 * 检测关键信息存在
	 *
	 * @param data
	 */
	private void checkImportInfo(SmartFormContent data) {
		CheckDataUtil.checkNull(data.getFormId(), "缺少表单ID");
		// CheckDataUtil.checkNull(data.getUserId(), "缺少用户ID");
		// if (data.getWorkType() == null)
		// throw new SmartFormError("缺少业务类型!");
	}

	final Date minDate = new Date(0);

	/**
	 * 将时分秒格式转为时间戳
	 *
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	private int getTimeStamp(String time) throws ParseException {
		// 只有5位，则补全后面的秒：00:00:00
		if (time.length() == 5)
			time += ":00";
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		TreeMap t = new TreeMap();
		return (int) timeFormat.parse(time).getTime();
	}

	/**
	 * 将时间戳格式转为时分秒
	 *
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	private String getTimeStampStr(int time) throws ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		return timeFormat.format(new Date(time));
	}

	/*
	 * (non-Javadoc) 获取表单填报结构与内容，用于首次填报
	 *
	 * @see
	 * stcsm.smartform.form.service.SmartFormContentService#smartFormContent(
	 * int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unused")
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent smartFormContent(int type, String formId, String userId, String extraData)
			throws ParseException {
		// 先读取SmartForm结构
		SmartForm smartForm = smartFormService.smartForm(formId, true);
		if (smartForm == null || smartForm.getPageList() == null || smartForm.getPageList().size() == 0) {
			throw new SmartFormError("获取的表单已经不存在!");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		boolean hasExtraData = false;
		// 验证额外参数合法性
		if (extraData != null && !extraData.equals("")) {
			JSONObject jsonObject = JSONObject.parseObject(extraData);
			// json对象转Map
			Map<String, Object> extraMap = (Map<String, Object>) jsonObject;
			if (smartForm.getFieldMapperList() != null) {
				hasExtraData = true;
				for (DBFieldMapper fmap : smartForm.getFieldMapperList()) {
					// 将额外数据固定参数作为查询条件确定表单唯一性
					if (fmap.getMapperType() == DBFieldMapperType.PARAM.value) {
						if (extraMap.containsKey(fmap.getAlias())) {
							Object obj = extraMap.get(fmap.getAlias());
							if (obj != null) {
								// 1，字符串；2，Long
								if (fmap.getDataType() == 2) {
									try {
										map.put(fmap.getDbAlias(), Long.parseLong(obj.toString()));
									} catch (Exception ex) {
									}
								} else
									map.put(fmap.getDbAlias(), obj.toString());
							}
						}
					}
				}
			}
		}
		if (map.size() == 0)
			map = null;

		// 切换数据源
		//DynamicDataSource.setDataSource(smartForm.getDbName());

		SmartFormContent content = this.getMainContentBase(smartForm.getDbName(), smartForm.getTableName(), formId,
				userId, null, map);
		String uuid = null;
		if (content == null)
			uuid = UUIDUtil.getNextId();
		else
			uuid = content.getId();
		// 首次填报将额外数据存储起来
		if (hasExtraData && (content == null || formContentDao.getExtraData(content.getId()) == null)) {
			// 存储额外数据
			formContentDao.updateExtraData(uuid, extraData);
			// 设置额外数据的存储时间，48个小时，首次暂存/保存时即可删除
			formContentDao.expire(uuid, 48, TimeUnit.HOURS);
		}
		if (content == null) {
			content = new SmartFormContent();
			content.setId(uuid);
			// 将smartForm转换成content 直接返回
			content.setWorkType(type);
			content.setFormId(formId);
			// content.setUserId(userId);
			// 默认第一页
			int pageIndex = 1;
			// 从表格结构中获得原始数据当前分页
			List<FormPage> pageList = smartForm.getPageList();
			FormPage nowPage = null;
			if (pageList.size() > pageIndex - 1) {
				nowPage = pageList.get(pageIndex - 1);
			}
			// 状态表名
			String stateTable = this.getMapperTable(smartForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
			// 获取当前退回的组件状态
			List<ComponentStateEntity> componentStates = this.getComponentState(stateTable, content.getId());
			// 处理分页优先级
			this.handlePagePriority(pageList, componentStates, nowPage);
			// 处理固定行
			this.firstFixedLine(content.getId(), nowPage, stateTable);

			this.handleNowPage(pageList, nowPage, false);
			// 取自smartForm中的信息
			content.setPageList(smartForm.getPageList());
			content.setName(smartForm.getName());
			content.setOptionsList(smartForm.getOptionsList());
			content.setExtraSetting(smartForm.getExtraSetting());
			// content.setDes(smartForm.getDes());
		} else {
			// 使用ID和分页获取纯填报内容数据
			this.getPageContentById(content, null, smartForm, false);
			content.setName(smartForm.getName());
			// 取自smartForm中的信息
			content.setOptionsList(smartForm.getOptionsList());
			content.setExtraSetting(smartForm.getExtraSetting());
			// content.setDes(smartForm.getDes());
		}

		return content;
	}

	/*
	 * (non-Javadoc) 获取表单填报结构与内容,用于编辑填报内容
	 *
	 * @see
	 * stcsm.smartform.form.service.SmartFormContentService#smartFormContentById
	 * (java.lang.String, java.lang.Integer)
	 */
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent smartFormContentById(String formId, String id, String pageId, boolean justPageState)
			throws ParseException {
		// 先读取SmartForm结构
		SmartForm smartForm = smartFormService.smartForm(formId, pageId, justPageState ? false : true);
		if (smartForm == null || smartForm.getPageList() == null || smartForm.getPageList().size() == 0) {
			throw new SmartFormError("获取的表单已经不存在!");
		}

		// 切换数据源
		//DynamicDataSource.setDataSource(smartForm.getDbName());
		// 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
		SmartFormContent data = this.getMainContentBase(smartForm.getDbName(), smartForm.getTableName(), id);
		// 首次填报将额外数据存储起来
		if (data == null) {
			throw new SmartFormError("获取的表单内容已经不存在!");
		} else {
			// 使用ID和分页获取纯填报内容数据
			SmartFormContent content = this.getPageContentById(data, pageId, smartForm, justPageState);
			if (content != null) {
				// 取自smartForm中的信息
				content.setOptionsList(smartForm.getOptionsList());
				content.setName(smartForm.getName());
				content.setExtraSetting(smartForm.getExtraSetting());
				// content.setDes(smartForm.getDes());
			}
			return content;
		}
	}

	/*
	 * (non-Javadoc) 获取表单填报结构与内容,用于编辑填报内容
	 *
	 * @see
	 * stcsm.smartform.form.service.SmartFormContentService#smartFormContentById
	 * (java.lang.String, java.lang.Integer)
	 */
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent smartFormContentPageState(String formId, String id) throws ParseException {
		// 先读取SmartForm结构
		SmartForm smartForm = smartFormService.smartForm(formId, null, false);
		if (smartForm == null || smartForm.getPageList() == null || smartForm.getPageList().size() == 0) {
			throw new SmartFormError("获取的表单已经不存在!");
		}

		// 切换数据源
		//DynamicDataSource.setDataSource(smartForm.getDbName());
		// 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
		SmartFormContent data = this.getMainContentBase(smartForm.getDbName(), smartForm.getTableName(), id);
		// 首次填报将额外数据存储起来
		if (data == null) {
			data = new SmartFormContent();
			data.setId(id);
			data.setFormId(formId);
		}
		// data.setUserId(userId);
		// 从表格结构中获得原始数据当前分页
		List<FormPage> pageList = smartForm.getPageList();

		// 状态表名
		String stateTable = this.getMapperTable(smartForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
		// 获取当前退回的组件状态
		List<ComponentStateEntity> componentStates = this.getComponentState(stateTable, data.getId());
		// 设置分页状态
		this.setPageState(pageList, componentStates);
		// 清除分页字段
		this.handleNowPage(pageList, null, true);
		// 取自smartForm中的信息
		data.setPageList(smartForm.getPageList());
		data.setName(smartForm.getName());
		data.setOptionsList(smartForm.getOptionsList());
		// content.setDes(smartForm.getDes());
		return data;
	}

	/**
	 * 根据内容ID获取分页详情数据
	 *
	 * @param id
	 * @param pageIndex
	 * @param smartForm
	 * @return
	 * @throws ParseException
	 */
	private SmartFormContent getPageContentById(SmartFormContent data, String pageId, SmartForm orgForm,
			boolean justPageState) throws ParseException {
		if (data != null) {
			FormPage nowPage = null;
			// 从表格结构中获得原始数据当前分页
			List<FormPage> pageList = orgForm.getPageList();
			// 默认第一页
			if (pageId == null || pageId.equals("")) {
				nowPage = pageList.get(0);
				pageId = nowPage.getId();
			} else {
				for (FormPage page : pageList) {
					if (page.getId().equals(pageId))
						nowPage = page;
				}
			}

			// 优先执行表单复杂规则
			this.executeFormRule(data.getId(), orgForm, pageId, 0);
			if (nowPage.getHide() != null && nowPage.getHide()) {
				throw new SmartFormError("此分页数据已不需要填写!");
			}

			// 状态表名
			String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
			// 上传表名
			String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

			// 获取分页中要处理的组件
			List<FormContentBaseDTO<Group>> components = this.getPageComponents(nowPage, true);
			if (components == null || components.size() == 0) {
				data.setPageList(pageList);
				return data;
			}
			// 处理组件状态
			this.handleGroupState(data, stateTable, components, pageList, nowPage);
			this.handleNowPage(pageList, nowPage, justPageState);
			if (justPageState) {
				// 将表单分页交给内容实体
				data.setPageList(pageList);
				return data;
			}
			// 处理固定行
			this.firstFixedLine(data.getId(), nowPage, stateTable);

			// 要查询的主表字段
			List<String> mainColumns = new ArrayList<String>();
			// 要查询的组件表
			List<FormContentBaseDTO<List<String>>> componentList = new ArrayList<FormContentBaseDTO<List<String>>>();
			// 要查询的表格表
			List<FormContentBaseDTO<List<String>>> tableList = new ArrayList<FormContentBaseDTO<List<String>>>();
			// 要查询的上传列表
			List<FormContentBaseDTO<String>> uploadList = new ArrayList<FormContentBaseDTO<String>>();
			// 要执行的数字框SQL查询
			List<WidgetNumber> sqlList = new ArrayList<WidgetNumber>();

			// 遍历要存储的组件
			this.handlQueryPageDTO(components, orgForm, data.getId(), mainColumns, componentList, tableList, uploadList,
					sqlList);

			// 查询组件表
			if (componentList.size() > 0) {
				for (FormContentBaseDTO<List<String>> componentData : componentList) {
					Map<String, Object> dataMap = componentDao.getContent(componentData.getDbTable(), data.getId(),
							componentData.getWorkType(), componentData.getData());
					componentData.setQueryData(dataMap);
				}
			}
			// 查询表格表
			if (tableList.size() > 0) {
				for (FormContentBaseDTO<List<String>> tableData : tableList) {
					List<Map<String, Object>> dataMaps = tableDao.getContentList(tableData.getDbTable(), data.getId(),
							tableData.getWorkType(), tableData.getData());
					tableData.setQueryData(dataMaps);
				}
			}
			// 查询上传表
			if (uploadList.size() > 0) {
				for (FormContentBaseDTO<String> uploadData : uploadList) {
					List<FormContentUploadsEntity> dataMaps = uploadsDao.getContentList(uploadTable, data.getId(),
							uploadData.getDbTable(), uploadData.getWorkType(), null, null);
					uploadData.setQueryData(dataMaps);
				}
			}

			// 要执行的数字框SQL查询
			if (sqlList.size() > 0) {
				List<String> array = new ArrayList<String>();
				for (WidgetNumber sql : sqlList) {
					array.add(sql.getSqlStr());
				}
				List<Object> numberList = contentMainDao.customSelect(array, data.getId());
				int i = 0;
				// 将查询到的值赋值给表单原始字段
				for (Object value : numberList) {
					WidgetNumber number = sqlList.get(i);
					number.setDefValue(this.handleBigDecimal(value));
					i++;
				}
			}
			Map<String, Object> mainDataMap = null;
			// 查询主表
			if (mainColumns.size() > 0) {
				mainDataMap = contentMainDao.getContent(orgForm.getTableName(), data.getId(), mainColumns);
			}
			// 填充查询数据
			this.fillQueryPage(orgForm, nowPage, data.getId(), mainDataMap, componentList, tableList, uploadList);
			// 将表单分页交给内容实体
			data.setPageList(pageList);
		}
		return data;
	}

	/**
	 * 读取当前分页状态
	 *
	 * @param pageList
	 * @param componentStates
	 */
	private void setPageState(List<FormPage> pageList, List<ComponentStateEntity> componentStates) {
		for (FormPage page : pageList) {
			// 存储组件列表状态
			Set<Integer> stateSet = new HashSet<Integer>();
			if (page.getFieldList() != null) {
				for (FormFieldBase field : page.getFieldList()) {
					if (field instanceof Group) {
						Group group = (Group) field;
						if (group.getGroupType() == GroupType.SUPER.value) {
							// 遍历超级组件
							List<FormFieldBase> groupFields = group.getFieldList();
							boolean hasField = false;
							if (groupFields != null) {
								for (FormFieldBase gfield : groupFields) {
									if (gfield instanceof Group) {
										Group tableGroup = (Group) gfield;
										// 遍历非样式组状态
										if (tableGroup.getGroupType() != GroupType.STYLE.value) {
											// 如果组件隐藏，则认为已经填报
											if (tableGroup.getHide() != null && tableGroup.getHide()) {
												stateSet.add(ContentStateType.SAVE.value);
												tableGroup.setState(ContentStateType.SAVE.value);
											} else {
												// 查找组件状态
												ComponentStateEntity state = this.getGroupState(tableGroup,
														componentStates);
												if (state != null) {
													stateSet.add(state.getState());
													tableGroup.setState(state.getState());
												} else {
													stateSet.add(ContentStateType.UNFILL.value);
													tableGroup.setState(ContentStateType.UNFILL.value);
												}
											}
										} else
											hasField = true;
									} else
										hasField = true;
								}
							}
							if (hasField) {
								ComponentStateEntity state = this.getGroupState(group, componentStates);
								if (state != null) {
									stateSet.add(state.getState());
								} else
									stateSet.add(ContentStateType.UNFILL.value);
							}
						}
					}
				}
			}
			// 额外判断一下状态表中page字段
			for(ComponentStateEntity state : componentStates)
			{
				if(page.getId().equals(state.getPageId()))
				{
					stateSet.add(state.getState());
					break;
				}
			}
			page.setFillState(ContentStateType.UNFILL.value);
			// 判定当前分页状态
			for (Integer state : stateSet) {
				if (state == ContentStateType.SAVE.value) {// || state == ContentStateType.REFUSESUBMIT.value) {
					page.setFillState(state);
				} else {
					page.setFillState(state);
					break;
				}
			}
		}
	}

	/**
	 * 查找组的状态
	 *
	 * @param group
	 * @param componentStates
	 * @return
	 */
	private ComponentStateEntity getGroupState(Group group, List<ComponentStateEntity> componentStates) {
		if (componentStates != null && componentStates.size() > 0) {
			for (ComponentStateEntity state : componentStates) {
				if (state.getTableName().equals(group.getTable()) && state.getWorkType() == group.getWorkType()) {
					return state;
				}
			}
		}
		return null;
	}

	/**
	 * 判定组件填写优先级,同时设置组件状态
	 *
	 * @param pageList
	 * @param componentStates
	 */
	private void handlePagePriority(List<FormPage> pageList, List<ComponentStateEntity> componentStates,
			FormPage nowPage) {
		this.setPageState(pageList, componentStates);
		// 按优先级倒叙
		List<FormPage> copyPages = new ArrayList<FormPage>();
		copyPages.addAll(pageList);
		copyPages.sort((FormPage p1, FormPage p2) -> p1.getPriority() < p2.getPriority() ? 1 : -1);

		boolean noFill = false;
		FormPage noFillPage = null;
		for (FormPage page : copyPages) {
			// 判定之前优先级是否有未填页面
			if (nowPage == page) {
				if (noFill && noFillPage.getPriority() != nowPage.getPriority())
					throw new SmartFormError("请先填写 '" + noFillPage.getName() + "'");
				else
					return;
			}
			if (!noFill) {
				Integer state = page.getFillState();
				if (state != ContentStateType.SAVE.value) {// && state != ContentStateType.REFUSESUBMIT.value) {
					noFill = true;
					noFillPage = page;
				}
			}
		}
	}

	/**
	 * 根据当前表单状态处理分页中组件的禁用状态
	 *
	 * @param content
	 * @param stateTable
	 * @param components
	 * @param pageList
	 * @param pageIndex
	 */
	private void handleGroupState(SmartFormContent content, String stateTable,
			List<FormContentBaseDTO<Group>> components, List<FormPage> pageList, FormPage nowPage) {
		// 获取当前退回的组件状态
		List<ComponentStateEntity> componentStates = this.getComponentState(stateTable, content.getId());
		// 处理分页优先级
		this.handlePagePriority(pageList, componentStates, nowPage);

		// 进行状态判定，锁定状态无法提交
		if (content.getState().intValue() == ContentStateType.LOCK.value) {
			for (FormContentBaseDTO<Group> component : components) {
				// 设置组件禁用
				component.getData().setDisable(true);
			}
		}
		// 如果是退回状态，验证当前具体退回的超级组件
//		if (content.getState().intValue() == ContentStateType.REFUSE.value) {
//			if (components.size() > 0) {
//				// 处理可以回退的表单
//				if (components != null && componentStates != null)
//					for (int i = components.size() - 1; i >= 0; i--) {
//						FormContentBaseDTO<Group> component = components.get(i);
//						// 遍历组件状态
//						for (ComponentStateEntity componentState : componentStates) {
//							if (componentState.getTableName().equals(component.getDbTable())
//									&& componentState.getWorkType() == component.getWorkType()) {
//								// 判定当前组件状态不可编辑
//								if (componentState.getState() != ContentStateType.REFUSE.value
//										&& componentState.getState() != ContentStateType.REFUSESUBMIT.value
//										&& componentState.getState() != ContentStateType.STORAGE.value) {
//									// 设置组件禁用
//									component.getData().setDisable(true);
//								}
//								break;
//							}
//						}
//					}
//			}
//		}
	}

	/**
	 * 处理当前分页，非当前分页的字段全部剔除掉，节省发往前端的空间
	 *
	 * @param pageList
	 * @param nowPage
	 */
	private void handleNowPage(List<FormPage> pageList, FormPage nowPage, boolean justPageState) {
		for (FormPage page : pageList) {
			if (justPageState || page != nowPage) {
				page.setFieldList(null);
			}
		}
	}

	/**
	 * 处理查询分页所需要的DTO
	 *
	 * @param components
	 * @param orgForm
	 * @param contentId
	 * @param mainColumns
	 * @param componentList
	 * @param tableList
	 * @param uploadList
	 * @throws ParseException
	 */
	private void handlQueryPageDTO(List<FormContentBaseDTO<Group>> components, SmartForm orgForm, String contentId,
			List<String> mainColumns, List<FormContentBaseDTO<List<String>>> componentList,
			List<FormContentBaseDTO<List<String>>> tableList, List<FormContentBaseDTO<String>> uploadList,
			List<WidgetNumber> sqlList) throws ParseException {
		for (FormContentBaseDTO<Group> component : components) {
			// 获取组件内容
			Group group = component.getData();
			if (group.getGroupType() == GroupType.SUPER.value) {
				// 组件表是否跟主表公用
				boolean inMainTable = group.getDbName().equals(orgForm.getDbName())
						&& group.getTable().equals(orgForm.getTableName());
				// 处理超级组件
				for (FormFieldBase widget : group.getFieldList()) {
					if (widget instanceof Group) {
						// 处理超级组件旗下的样式组
						Group styleGroup = (Group) widget;
						if (styleGroup.getGroupType() == GroupType.STYLE.value) {
							// 遍历样式组字段
							for (FormFieldBase styleWidget : styleGroup.getFieldList()) {
								if (styleWidget instanceof WidgetBase) {
									this.handleQueryFieldDTO(inMainTable, false, contentId, group,
											(WidgetBase) styleWidget, mainColumns, componentList, tableList, uploadList,
											sqlList);
								}
							}
						}
					} else if (widget instanceof WidgetBase) {
						this.handleQueryFieldDTO(inMainTable, false, contentId, group, (WidgetBase) widget, mainColumns,
								componentList, tableList, uploadList, sqlList);
					}
				}
			} else if (group.getGroupType() == GroupType.TABLE.value) {
				// 处理表格
				if (group.getSteerable() == null || !group.getSteerable()) {
					// 遍历表格原始字段
					if (group.getOriginalLine() != null) {
						GroupLine line = group.getOriginalLine();
						if (line.getFieldList() != null && line.getFieldList().size() > 0) {
							for (WidgetBase widget : line.getFieldList()) {
								if (widget instanceof WidgetBase) {
									// 目前查询所有Grid数据
									// // 如果是GridView模式，则只获取Grid需要的字段
									// if (group.hasGridView()) {
									// for (String widgetId :
									// group.getGridViewRules()) {
									// if (widgetId != null &&
									// widgetId.equals(widget.getId())) {
									// this.handleQueryFieldDTO(false, true,
									// contentId, group,
									// (WidgetBase) widget, mainColumns,
									// componentList, tableList,
									// uploadList);
									// break;
									// }
									// }
									// } else
									this.handleQueryFieldDTO(false, true, contentId, group, (WidgetBase) widget,
											mainColumns, componentList, tableList, uploadList, sqlList);
								}
							}
						}
					}
				} else {
					// 处理汇总表格
					// 组件表是否跟主表公用
					boolean inMainTable = group.getDbName().equals(orgForm.getDbName())
							&& group.getTable().equals(orgForm.getTableName());
					if (group.getLineList() != null) {
						for (GroupLine line : group.getLineList()) {
							// 遍历组字段
							if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
									&& line.getFieldList().size() > 0) {
								for (WidgetBase widget : line.getFieldList()) {
									if (widget instanceof WidgetBase) {
										this.handleQueryFieldDTO(inMainTable, false, contentId, group,
												(WidgetBase) widget, mainColumns, componentList, tableList, uploadList,
												sqlList);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 将field填充到存储实体中
	 *
	 * @param useMainTable
	 *            是否存到主表
	 * @param hasTable
	 *            是否是存到表格中
	 * @param contentId
	 *            表单内容ID
	 * @param component
	 * @param widget
	 * @param mainContentMap
	 * @param componentList
	 * @param tableList
	 * @param uploadList
	 * @param addToMainTable
	 *            是否追加至主表中
	 * @param hasStorage
	 * @throws ParseException
	 */
	private void handleQueryFieldDTO(boolean inMainTable, boolean hasTable, String contentId, Group group,
			WidgetBase widget, List<String> mainColumns, List<FormContentBaseDTO<List<String>>> componentList,
			List<FormContentBaseDTO<List<String>>> tableList, List<FormContentBaseDTO<String>> uploadList,
			List<WidgetNumber> sqlList) throws ParseException {
		// 越过空别名字段
		if (widget.getFieldType() != WidgetType.UPLOAD.value
				&& (widget.getAlias() == null || widget.getAlias().equals("")))
			return;

		String endAlias = null;
		if (widget.getFieldType() == WidgetType.UPLOAD.value) {
			// 上传
			FormContentBaseDTO<String> uploadDTO = null;
			for (FormContentBaseDTO<String> item : uploadList) {
				if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
						&& item.getWorkType().equals(group.getWorkType())) {
					uploadDTO = item;
				}
			}
			if (uploadDTO == null) {
				uploadDTO = new FormContentBaseDTO<String>();
				uploadDTO.setDbName(group.getDbName());
				uploadDTO.setDbTable(group.getTable());
				uploadDTO.setWorkType(group.getWorkType());
				uploadList.add(uploadDTO);
			}
			return;
		} else if (widget.getFieldType() == WidgetType.DATESPAN.value) {
			// 日期
			WidgetDate vData = (WidgetDate) widget;
			endAlias = vData.getEndAlias();
		} else if (widget.getFieldType() == WidgetType.TIMESPAN.value) {
			// 时间
			WidgetTime vData = (WidgetTime) widget;
			endAlias = vData.getEndAlias();
		} else if (widget.getFieldType() == WidgetType.NUMBER.value) {
			// 数字框
			WidgetNumber vData = (WidgetNumber) widget;
			// 如果数字框启用了Sql查询
			if (vData.getUseSql() != null && vData.getUseSql() && vData.getSqlStr() != null
					&& !vData.getSqlStr().equals("")) {
				sqlList.add(vData);
				return;
			}
		}
		// 是否从主表查询
		if (inMainTable)
			mainColumns.add(widget.getAlias());
		else if (!hasTable) {
			// 查找是否已经存在组件存储DTO
			FormContentBaseDTO<List<String>> componentDTO = null;
			for (FormContentBaseDTO<List<String>> item : componentList) {
				if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
						&& item.getWorkType().equals(group.getWorkType())) {
					componentDTO = item;
				}
			}
			// 初始化组件存储对象
			if (componentDTO == null) {
				componentDTO = new FormContentBaseDTO<List<String>>();
				componentDTO.setData(new ArrayList<String>());
				componentDTO.setDbName(group.getDbName());
				componentDTO.setDbTable(group.getTable());
				componentDTO.setWorkType(group.getWorkType());
				componentList.add(componentDTO);
			}
			// 添加查询列
			componentDTO.getData().add(widget.getAlias());
			if (endAlias != null && !endAlias.equals(""))
				componentDTO.getData().add(endAlias);
		} else {
			// 查找是否已经存在组件存储DTO
			FormContentBaseDTO<List<String>> tableDTO = null;
			for (FormContentBaseDTO<List<String>> item : tableList) {
				if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
						&& item.getWorkType().equals(group.getWorkType())) {
					tableDTO = item;
				}
			}
			// 初始化组件存储对象
			if (tableDTO == null) {
				tableDTO = new FormContentBaseDTO<List<String>>();
				tableDTO.setData(new ArrayList<String>());
				tableDTO.setDbName(group.getDbName());
				tableDTO.setDbTable(group.getTable());
				tableDTO.setWorkType(group.getWorkType());
				tableList.add(tableDTO);
			}
			// 添加查询列
			tableDTO.getData().add(widget.getAlias());
			if (endAlias != null && !endAlias.equals(""))
				tableDTO.getData().add(endAlias);
		}
	}

	/**
	 * 向表单分页中填充查询到的数据
	 *
	 * @param nowPage
	 * @param mainDataMap
	 * @param componentList
	 * @param tableList
	 * @param uploadList
	 * @throws ParseException
	 */
	private void fillQueryPage(SmartForm orgForm, FormPage nowPage, String contentId, Map<String, Object> mainDataMap,
			List<FormContentBaseDTO<List<String>>> componentList, List<FormContentBaseDTO<List<String>>> tableList,
			List<FormContentBaseDTO<String>> uploadList) throws ParseException {
		// 获取当前分页内的组件列表
		List<FormFieldBase> pageFields = nowPage.getFieldList();
		// 遍历分页旗下的超级组件
		if (pageFields != null) {
			for (FormFieldBase field : pageFields) {
				if (field instanceof Group) {
					Group group = (Group) field;
					// 组件表是否跟主表公用
					boolean inMainTable = group.getDbName().equals(orgForm.getDbName())
							&& group.getTable().equals(orgForm.getTableName());
					if (group.getGroupType() != GroupType.SUPER.value) {
						continue;
					}
					// 遍历超级组件旗下是否还有表格组
					List<FormFieldBase> groupFields = group.getFieldList();
					if (groupFields == null) {
						continue;
					}
					for (FormFieldBase gfield : groupFields) {
						if (gfield instanceof Group) {
							Group tableGroup = (Group) gfield;
							if (tableGroup.getGroupType() == GroupType.TABLE.value) {
								// 是否是可控表格
								if (tableGroup.getSteerable() == null || !tableGroup.getSteerable()) {
									// 遍历表格原始字段
									if (tableGroup.getOriginalLine() == null) {
										continue;
									}
									GroupLine origline = tableGroup.getOriginalLine();
									if (origline.getFieldList() != null && origline.getFieldList().size() > 0) {
										// 填充表格数据
										this.fillQueryTable(tableGroup, tableList, origline, uploadList);
									}
								} else {
									// 处理汇总表格
									boolean inMainTableGroup = tableGroup.getDbName().equals(orgForm.getDbName())
											&& tableGroup.getTable().equals(orgForm.getTableName());
									if (tableGroup.getLineList() != null) {
										for (GroupLine line : tableGroup.getLineList()) {
											// 遍历组字段
											if (line.getFieldList() != null && line.getFieldList().size() > 0) {
												for (WidgetBase widget : line.getFieldList()) {
													if (widget instanceof WidgetBase) {
														this.fillQueryField(inMainTableGroup, contentId, tableGroup,
																(WidgetBase) widget, null, mainDataMap, componentList,
																uploadList);
													}
												}
											}
										}
									}
								}
							} else if (tableGroup.getGroupType() == GroupType.STYLE.value) {
								if (tableGroup.getFieldList() == null) {
									continue;
								}
								// 遍历样式组字段
								for (FormFieldBase styleWidget : tableGroup.getFieldList()) {
									if (styleWidget instanceof WidgetBase) {
										this.fillQueryField(inMainTable, contentId, group, (WidgetBase) styleWidget,
												null, mainDataMap, componentList, uploadList);
									}
								}
							}
						} else if (gfield instanceof WidgetBase) {
							this.fillQueryField(inMainTable, contentId, group, (WidgetBase) gfield, null, mainDataMap,
									componentList, uploadList);
						}
					}
				}
			}
		}
	}

	/**
	 * 将查询到的数据填充至表格
	 *
	 * @param tableGroup
	 * @param tableList
	 * @param origline
	 * @param uploadList
	 * @throws ParseException
	 */
	private void fillQueryTable(Group tableGroup, List<FormContentBaseDTO<List<String>>> tableList, GroupLine origline,
			List<FormContentBaseDTO<String>> uploadList) throws ParseException {
		// 查找查询到的表格数据
		FormContentBaseDTO<List<String>> tableData = null;
		for (FormContentBaseDTO<List<String>> item : tableList) {
			if (item.getDbName().equals(tableGroup.getDbName()) && item.getDbTable().equals(tableGroup.getTable())
					&& item.getWorkType().equals(tableGroup.getWorkType())) {
				tableData = item;
			}
		}
		if (tableData != null && tableData.getQueryData() != null) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> dataMaps = (List<Map<String, Object>>) tableData.getQueryData();
			List<GroupLine> lineList = new ArrayList<GroupLine>();
			for (Map<String, Object> line : dataMaps) {
				GroupLine lineData = new GroupLine();
				lineData.setId((String) line.get("id"));
				lineData.setLineNum(handleInteger(line.get("line_num")));
				// 增加固定行ID和状态信息
				if (line.get("fixed_id") != null)
					lineData.setDataId((String) line.get("fixed_id"));
				lineData.setState(handleInteger(line.get("state")));

				lineData.setLineType(GroupLineType.COMMON.value);
				List<WidgetBase> fieldList = new ArrayList<WidgetBase>();
				lineData.setFieldList(fieldList);
				for (WidgetBase origWidget : origline.getFieldList()) {
					WidgetBase newWidget = null;
					if (origWidget.getFieldType() == WidgetType.UPLOAD.value) {
						newWidget = this.getQueryUploadWidget(uploadList, tableGroup, lineData.getId(), origWidget,
								true);
					} else {
						newWidget = this.getQueryWidget(line, origWidget, true);
					}
					if (newWidget != null)
						lineData.getFieldList().add(newWidget);
				}
				lineList.add(lineData);
			}
			tableGroup.setLineList(lineList);
		}
	}

	/**
	 * 将field填充到存储实体中
	 *
	 * @param useMainTable
	 *            是否存到主表
	 * @param contentId
	 *            表单内容ID
	 * @param component
	 * @param widget
	 * @param mainContentMap
	 * @param componentList
	 * @param uploadList
	 * @param addToMainTable
	 *            是否追加至主表中
	 * @param hasStorage
	 * @throws ParseException
	 */
	private void fillQueryField(boolean inMainTable, String contentId, Group group, WidgetBase widget, String lineId,
			Map<String, Object> mainDataMap, List<FormContentBaseDTO<List<String>>> componentList,
			List<FormContentBaseDTO<String>> uploadList) throws ParseException {
		if (widget.getFieldType() == WidgetType.UPLOAD.value) {
			this.getQueryUploadWidget(uploadList, group, lineId, widget, false);
			return;
		}
		// 是否从主表查询
		if (inMainTable) {
			this.getQueryWidget(mainDataMap, widget, false);
		} else {
			// 查找是否已经存在组件存储DTO
			FormContentBaseDTO<List<String>> componentDTO = null;
			for (FormContentBaseDTO<List<String>> item : componentList) {
				if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
						&& item.getWorkType().equals(group.getWorkType())) {
					componentDTO = item;
				}
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) componentDTO.getQueryData();
			if (dataMap != null)
				this.getQueryWidget(dataMap, widget, false);
			else {
				int dataType = widget.getFieldType();
				if (dataType == WidgetType.NUMBER.value) {
					// 数字
					WidgetBase simWidget = widget;
					WidgetNumber orgWidget = (WidgetNumber) widget;
					// 如果数字框启用了Sql查询,则不再应用数据库中的值
					if (orgWidget.getUseSql() != null && orgWidget.getUseSql() && orgWidget.getSqlStr() != null
							&& !orgWidget.getSqlStr().equals("")) {
						((WidgetNumber) simWidget).setDefValue(orgWidget.getDefValue());
					}
				}
			}
		}
	}

	/**
	 * 获取查询数据
	 *
	 * @param dataMap
	 * @param widget
	 * @param useCopy
	 * @return
	 * @throws ParseException
	 */
	private WidgetBase getQueryWidget(Map<String, Object> dataMap, WidgetBase widget, boolean useCopy)
			throws ParseException {
		int dataType = widget.getFieldType();
		WidgetBase simWidget = null;
		if (dataType == WidgetType.NUMBER.value) {
			// 数字
			simWidget = useCopy ? new WidgetNumber() : widget;
			WidgetNumber orgWidget = (WidgetNumber) widget;
			// 如果数字框启用了Sql查询,则不再应用数据库中的值
			if (orgWidget.getUseSql() != null && orgWidget.getUseSql() && orgWidget.getSqlStr() != null
					&& !orgWidget.getSqlStr().equals("")) {
				((WidgetNumber) simWidget).setDefValue(orgWidget.getDefValue());
			} else {
				if (widget.getAlias() != null
						|| !widget.getAlias().equals("") && dataMap != null && dataMap.containsKey(widget.getAlias())) {
					Object obj = dataMap.get(widget.getAlias());
					((WidgetNumber) simWidget).setDefValue(handleBigDecimal(obj));
				}
			}
		} else if (dataType == WidgetType.INPUT.value || dataType == WidgetType.TEXTAREA.value) {
			// 文本
			simWidget = useCopy ? new WidgetInput() : widget;
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
					&& dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());
				((WidgetInput) simWidget).setDefValue(obj.toString());
			}
		} else if (dataType == WidgetType.EDITOR.value) {
			// 富文本
			simWidget = useCopy ? new WidgetEditor() : widget;
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
					&& dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());
				((WidgetEditor) simWidget).setDefValue(obj.toString());
			}
		} else if (dataType == WidgetType.RADIO.value) {
			// 单选
			simWidget = useCopy ? new WidgeRadio() : widget;
			WidgeRadio vData = (WidgeRadio) widget;
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
					&& dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());
				((WidgeRadio) simWidget).setDefValue(obj.toString());
			}
			((WidgeRadio) simWidget).setSourceID(vData.getSourceID());

		} else if (dataType == WidgetType.CHECKBOX.value || dataType == WidgetType.SELECT.value
				|| dataType == WidgetType.CASCADER.value) {
			// 多选
			simWidget = useCopy ? new WidgeSelect() : widget;
			WidgeSelect vData = (WidgeSelect) widget;
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
					&& dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());

				if (obj.toString() != null && !obj.toString().equals("")) {
					String[] listValue = obj.toString().split(",");
					// 拆分Sql中逗号分割的ID
					List<String> defs = new ArrayList<String>();
					if (listValue != null && listValue.length > 0) {
						for (String value : listValue) {
							defs.add(value);
						}
					}
					((WidgeSelect) simWidget).setDefValue(defs);
				} else
					((WidgeSelect) simWidget).setDefValue(null);
			}
			((WidgeSelect) simWidget).setMulti(vData.getMulti());
			((WidgeSelect) simWidget).setSourceID(vData.getSourceID());

		} else if (dataType == WidgetType.DATE.value || dataType == WidgetType.DATESPAN.value) {
			// 日期
			simWidget = useCopy ? new WidgetDate() : widget;
			WidgetDate vData = (WidgetDate) widget;

			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null) {
				if (dataMap.containsKey(widget.getAlias())) {
					Long time = handleLong(dataMap.get(widget.getAlias()));
					if (time != null && time != 0)
						((WidgetDate) simWidget).setDefStartDate(new Date(time));
					else
						((WidgetDate) simWidget).setDefStartDate(null);
				}
				if (dataType == WidgetType.DATESPAN.value && dataMap.containsKey(vData.getEndAlias())) {
					Long time = handleLong(dataMap.get(vData.getEndAlias()));
					if (time != null && time != 0)
						((WidgetDate) simWidget).setDefEndDate(new Date(time));
					else
						((WidgetDate) simWidget).setDefEndDate(null);
				}
			}
			((WidgetDate) simWidget).setDateType(vData.getDateType());

		} else if (dataType == WidgetType.TIME.value || dataType == WidgetType.TIMESPAN.value) {
			// 时间简化
			simWidget = useCopy ? new WidgetTime() : widget;
			WidgetTime vData = (WidgetTime) widget;
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null) {
				if (dataMap.containsKey(widget.getAlias())) {
					Integer time = handleInteger(dataMap.get(widget.getAlias()));
					if (time != null && time != 0)
						((WidgetTime) simWidget).setDefStartTime(this.getTimeStampStr(time));
					else
						((WidgetTime) simWidget).setDefStartTime(null);
				}
				if (dataType == WidgetType.TIMESPAN.value && dataMap.containsKey(vData.getEndAlias())) {
					Integer time = handleInteger(dataMap.get(vData.getEndAlias()));
					if (time != null && time != 0)
						((WidgetTime) simWidget).setDefEndTime(this.getTimeStampStr(time));
					else
						((WidgetTime) simWidget).setDefEndTime(null);
				}
				((WidgetTime) simWidget).setTimeType(vData.getTimeType());
			}
		}
		if (useCopy && simWidget != null) {
			simWidget.setId(widget.getId());
			simWidget.setType(widget.getType());
			simWidget.setFieldType(dataType);
			simWidget.setName(widget.getName());
		}
		return simWidget;
	}

	/**
	 * @param uploadList
	 * @param group
	 * @param widget
	 * @param useCopy
	 * @return
	 */
	private WidgetUpload getQueryUploadWidget(List<FormContentBaseDTO<String>> uploadList, Group group, String lineId,
			WidgetBase widget, boolean useCopy) {
		WidgetUpload simWidget = useCopy ? new WidgetUpload() : (WidgetUpload) widget;
		// 找到上传查询数据
		FormContentBaseDTO<String> uploadDTO = null;
		for (FormContentBaseDTO<String> item : uploadList) {
			if (item.getDbName().equals(group.getDbName()) && item.getDbTable().equals(group.getTable())
					&& item.getWorkType().equals(group.getWorkType())) {
				uploadDTO = item;
				break;
			}
		}
		// 将数据填充到WidgetUpload
		if (uploadDTO != null && uploadDTO.getQueryData() != null) {
			@SuppressWarnings("unchecked")
			List<FormContentUploadsEntity> dataMaps = (List<FormContentUploadsEntity>) uploadDTO.getQueryData();
			// 上传
			WidgetUpload vData = (WidgetUpload) widget;
			List<UploadItem> defValue = new ArrayList<UploadItem>();
			for (FormContentUploadsEntity uploadData : dataMaps) {
				boolean ckLineId = lineId == null ? true : uploadData.getLineId().equals(lineId);
				if (uploadData.getUploadType() == vData.getUploadType() && ckLineId) {
					UploadItem item = new UploadItem();
					item.setId(uploadData.getId());
					item.setUrl(uploadData.getUrl());
					item.setSize(uploadData.getSize());
					item.setName(uploadData.getFileName());
					item.setType(uploadData.getSuffix());
					defValue.add(item);
				}
			}
			if (defValue.size() > 0)
				simWidget.setDefValue(defValue);
		}
		if (useCopy && simWidget != null) {
			simWidget.setId(widget.getId());
			simWidget.setType(widget.getType());
			simWidget.setFieldType(widget.getFieldType());
			simWidget.setName(widget.getName());
		}
		return simWidget;
	}

	/*
	 * (non-Javadoc) 暂存表单内容
	 *
	 * @see
	 * stcsm.smartform.form.service.SmartFormContentService#storageFormContent(
	 * java.lang.String, java.lang.Integer)
	 */
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent storageFormContent(String form, String pageId) throws ParseException {
		SmartFormContent data = JSON.parseObject(form, SmartFormContent.class);
		if (data != null) {
			// 检测data中 formId，userId，等重要信息存在
			this.checkImportInfo(data);
			CheckDataUtil.checkNull(data.getId(), "缺少表单ID");

			String formId = data.getFormId();

			// 读取表单结构数据，检查合法性，识别具体存储分页以及组织
			SmartForm orgForm = smartFormService.smartForm(formId, pageId, false);
			if (orgForm == null)
				throw new SmartFormError("当前表单已经不存在");

			// 切换数据源
			//DynamicDataSource.setDataSource(orgForm.getDbName());

			this.saveFormContentPage(data, orgForm, pageId, true);
		}
		return data;
	}

	/*
	 * (non-Javadoc) 提交表单内容
	 *
	 * @see
	 * stcsm.smartform.form.service.SmartFormContentService#submitFormContent(
	 * java.lang.String, java.lang.Integer)
	 */
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent submitFormContentPage(String form, String pageId) throws ParseException {
		SmartFormContent data = JSON.parseObject(form, SmartFormContent.class);
		if (data != null) {
			// 检测data中 formId，userId，等重要信息存在
			this.checkImportInfo(data);
			CheckDataUtil.checkNull(data.getId(), "缺少表单ID");

			String formId = data.getFormId();
			// 读取表单结构数据，检查合法性，识别具体存储分页以及组织
			SmartForm orgForm = smartFormService.smartForm(formId, pageId, false);
			if (orgForm == null)
				throw new SmartFormError("当前表单已经不存在");

			// 切换数据源
			//DynamicDataSource.setDataSource(orgForm.getDbName());

			this.saveFormContentPage(data, orgForm, pageId, false);
		}
		return data;
	}

	/**
	 * 分页保存表单内容
	 *
	 * @param data
	 * @param orgForm
	 * @param pageIndex
	 * @param hasStorage
	 * @throws ParseException
	 */
	@Transactional
	private void saveFormContentPage(SmartFormContent data, SmartForm orgForm, String pageId, boolean hasStorage)
			throws ParseException {
		// 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
		SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), data.getId(),
				orgForm.getFieldMapperList());

		// 状态表名
		String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
		// 上传表名
		String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

		FormPage nowPage = null;
		// 从表格结构中获得原始数据当前分页
		List<FormPage> pageList = orgForm.getPageList();
		// 默认第一页
		if (pageId == null || pageId.equals("")) {
			nowPage = pageList.get(0);
			pageId = nowPage.getId();
		} else {
			for (FormPage page : pageList) {
				if (page.getId().equals(pageId))
				{
					nowPage = page;
					break;
				}
			}
		}

		if (nowPage == null) {
			throw new SmartFormError("当前分页已不存在");
		}

		// 执行表单复杂规则
		this.executeFormRule(data.getId(), orgForm, pageId, 1);

		// 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
		List<FormContentBaseDTO<Group>> components = this.getPageComponents(nowPage, false);

		// 当前已存的组件状态
		List<ComponentStateEntity> componentStates = null;

		// 判定状态数据
		boolean isCreated = false;
		boolean isRefuse = false;
		if (oldData != null) {
			if(orgForm.getSkipendtimevalidation()==null || orgForm.getSkipendtimevalidation().length()<1 || !orgForm.getSkipendtimevalidation().equals("1")) {
				// 判定填报截止日期
				this.ckFillEndDate(oldData);
			}
			// 编辑模式，进行状态判定，锁定状态无法提交
			if (oldData.getState().intValue() == ContentStateType.LOCK.value)
				throw new SmartFormError("该表单已提交，无法修改");

			// 获取当前退回的组件状态
			componentStates = this.getComponentState(stateTable, oldData.getId());

//			// 如果是退回状态，验证当前具体退回的超级组件
//			if (oldData.getState().intValue() == ContentStateType.REFUSE.value) {
//				if (components.size() > 0) {
//					// 处理可以回退的表单
//					handleRefuseComponent(components, componentStates);
//				}
//				if (components.size() == 0 || data.getPageList() == null || data.getPageList().size() == 0) {
//					throw new SmartFormError("该表单内容并未退回，无法再次提交");
//				}
//				isRefuse = true;
//			}
			data.setName(orgForm.getName());
			data.setCreatedAt(oldData.getCreatedAt());
			data.setModifiedAt(new Date().getTime());
		} else {
			// 添加模式
			isCreated = true;
			data.setName(orgForm.getName());
			data.setCreatedAt(System.currentTimeMillis());
			data.setModifiedAt(System.currentTimeMillis());
			data.setState(ContentStateType.STORAGE.value);
		}

		// 该分页没有需要存储的组件，直接返回
		if (components.size() == 0 || data.getPageList() == null || data.getPageList().size() == 0) {
			return;
		}

		// 要发布到主表的额外字段
		Map<String, Object> mainContentMap = new HashMap<String, Object>();
		// 要存储的组件表
		List<FormContentBaseDTO<FormContentComponentEntity>> componentList = new ArrayList<FormContentBaseDTO<FormContentComponentEntity>>();
		// 要存储的表格表
		List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList = new ArrayList<FormContentBaseDTO<List<FormContentTableEntity>>>();
		// 要存储的上传列表
		List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList = new ArrayList<FormContentBaseDTO<List<FormContentUploadsEntity>>>();

		// 遍历内容中的字段数据，合计mysql需要存储的表与字段
		// 并根据是否暂存检测必填字段存储
		handleDBData(nowPage.getId(), orgForm, data, components, mainContentMap, componentList, tableList, uploadList,
				hasStorage);

		// 存储组件表
		if (componentList.size() > 0) {
			for (FormContentBaseDTO<FormContentComponentEntity> componentData : componentList) {
				if (componentData.getContent() != null && componentData.getContent().size() > 0) {
					componentDao.saveContent(componentData.getData(), componentData.getDbTable(),
							componentData.getContent());
				}
			}
		}
		// 存储表格表
		if (tableList.size() > 0) {
			for (FormContentBaseDTO<List<FormContentTableEntity>> tableData : tableList) {
				// 删除已添加的数据
				if (!isCreated)
					tableDao.removeContent(tableData.getDbTable(), data.getId(), tableData.getWorkType(), null);
				// 批量添加表格数据
				tableDao.batchSave(tableData.getData(), tableData.getDbTable());
			}
		}
		// 存储上传表
		if (uploadList.size() > 0) {
			for (FormContentBaseDTO<List<FormContentUploadsEntity>> uploadData : uploadList) {
				// 删除已添加的数据
				if (!isCreated)
					uploadsDao.removeContent(uploadTable, data.getId(), uploadData.getDbTable(),
							uploadData.getWorkType(), null, null);
				// 批量一个表的上传数据
				uploadsDao.batchSave(uploadData.getData(), uploadTable);
			}
		}
		// 存储组件状态
		if (components.size() > 0) {
			for (FormContentBaseDTO<Group> component : components) {
				// 处理数据库
				// String dbName = component.getDbName();
				ComponentStateEntity comState = new ComponentStateEntity();
				comState.setId(UUIDUtil.getNextId());
				comState.setDbTable(stateTable);
				comState.setContentId(data.getId());
				comState.setTableName(component.getDbTable());
				comState.setWorkType(component.getWorkType());
				comState.setCreatedAt(System.currentTimeMillis());
				comState.setModifiedAt(System.currentTimeMillis());
				if (hasStorage) {
					comState.setState(ContentStateType.STORAGE.value);
				} else {
					comState.setState(ContentStateType.SAVE.value);
				}
//				} else {
//					if (!isRefuse)
//						comState.setState(ContentStateType.SUBMIT.value);
//					else
//						comState.setState(ContentStateType.REFUSESUBMIT.value);
//				}

				this.componentStateDao.saveContent(comState, stateTable);
			}
			// 处理表单回退
			List<FormContentBaseDTO<Group>> stateBackGroups = this.getStateBack(components, orgForm, componentStates);
			// 执行表单回退
			for (FormContentBaseDTO<Group> dto : stateBackGroups) {
				ComponentStateEntity comState = new ComponentStateEntity();
				comState.setId(UUIDUtil.getNextId());
				comState.setDbTable(stateTable);
				comState.setContentId(data.getId());
				comState.setTableName(dto.getDbTable());
				comState.setWorkType(dto.getWorkType());
				comState.setCreatedAt(System.currentTimeMillis());
				comState.setModifiedAt(System.currentTimeMillis());
				comState.setState(ContentStateType.STORAGE.value);
				this.componentStateDao.saveContent(comState, stateTable);
			}
		}

		data.setState(ContentStateType.STORAGE.value);
		// 是否是暂存
		if (!hasStorage) {
			// 只有一页，则直接提交表单
			if (pageList.size() == 1) {
				data.setState(ContentStateType.SAVE.value);
				//data.setSubmitTime(System.currentTimeMillis());
			}
		}

		Query mainContent = null;
		// 存储主表mysql数据
		if (isCreated) {
			// 处理固定表单固定额外参数
			handleParamExtraData(data.getId(), orgForm.getFieldMapperList(), mainContentMap);
			if (mainContentMap.size() > 0)
				mainContent = new Query(mainContentMap);
			contentMainDao.saveContent(data, orgForm.getTableName(), mainContent);
		} else {
			if (mainContentMap.size() > 0)
				mainContent = new Query(mainContentMap);
			contentMainDao.updateContent(data, orgForm.getTableName(), mainContent);
		}

		// 执行保存后的表单复杂规则，用于刷新表单分页状态
		this.executeFormRule(data.getId(), orgForm, pageId, 2);

		// 重新获取状态进行设置
		componentStates = this.getComponentState(stateTable, data.getId());
		this.setPageState(pageList, componentStates);
		this.handleNowPage(pageList, null, true);
		// 将最新的分页状态返回给前端
		data.setPageList(pageList);
	}

	/**
	 * 提交表单，多页时使用
	 *
	 * @param formId
	 * @param contentId
	 * @return
	 * @throws ParseException
	 */
	@Override
	//@DataSource(justClear = true)
	public String submitFormContent(String formId, String contentId) throws ParseException {
		// 读取表单结构数据，检查合法性，识别具体存储分页以及组织
		SmartForm orgForm = smartFormService.smartForm(formId, false);
		if (orgForm == null)
			throw new SmartFormError("当前表单已经不存在");

		// 切换数据源
		//DynamicDataSource.setDataSource(orgForm.getDbName());
		this.executeSubmitFormContent(orgForm, contentId);

		// 通知业务表单提交成功
		/*if (orgForm.getDbName().equals(DataSourceEnum.PROJECT.getName())) {
			int result = 200;
			try {
				result = 1;
				LOG.debug("通知业务表单提交结果:" + result);
			} catch (Exception ex) {
				LOG.error("通知业务表单提交异常:", ex);
			}
			if (result != 200) {
				throw new ServerCodeError(String.valueOf(result));
			}
			// throw new ServerCodeError(String.valueOf(60203));
		}*/
		return "1";
	}

	/**
	 * 提交表单，多页时使用
	 *
	 * @param orgForm
	 * @param contentId
	 * @throws ParseException
	 */
	@Transactional
	private void executeSubmitFormContent(SmartForm orgForm, String contentId) throws ParseException {
		SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
				orgForm.getFieldMapperList());
		if (oldData == null) {
			throw new SmartFormError("目前还没有提交任何数据");
		}
		// 编辑模式，进行状态判定，锁定状态无法提交
		if (oldData.getState().intValue() == ContentStateType.LOCK.value)
			throw new SmartFormError("该内容已经提交，无法编辑");
		if(orgForm.getSkipendtimevalidation()==null || orgForm.getSkipendtimevalidation().length()<1 || !orgForm.getSkipendtimevalidation().equals("1")) {
			// 判定填报截止日期
			this.ckFillEndDate(oldData);
		}
		// 状态表名
		String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);

		// 获取最新的组件状态
		List<ComponentStateEntity> componentStates = this.getComponentState(stateTable, oldData.getId());

		// 执行表单复杂规则
		this.executeFormRule(contentId, orgForm, null, 2);

		// 验证所有组件是否有已经提交
		List<FormContentBaseDTO<Group>> allComponents = new ArrayList<FormContentBaseDTO<Group>>();
		// 获取所有分页需要存储的组件
		for (FormPage page : orgForm.getPageList()) {
			List<FormContentBaseDTO<Group>> components = this.getPageComponents(page, true);
			allComponents.addAll(components);
		}
		// 验证grid模式数据是否已经满足

		// 判定所有组件是否已经提交
		if (componentStates != null) {
			// 移除已经提交的组件
			for (int i = allComponents.size() - 1; i >= 0; i--) {
				FormContentBaseDTO<Group> component = allComponents.get(i);
				Group group = component.getData();
				// 如果是表格，则额外验证行状态，固定行数量，行数量
				if (group.getGroupType() == GroupType.TABLE.value
						&& (group.getSteerable() == null || !group.getSteerable())) {
					List<String> fixedId = null;
					if (group.getFixedLineList() != null && group.getFixedLineList().size() > 0) {
						fixedId = new ArrayList<String>();
						// 遍历固定行
						for (GroupLine line : group.getFixedLineList()) {
							if (line.getId() != null) {
								fixedId.add(line.getId());
							}
						}
						if (fixedId.size() == 0)
							fixedId = null;
					}
					List<String> data = tableDao.countTotals(component.getDbTable(), contentId, component.getWorkType(),
							fixedId, ContentStateType.SAVE.value);
					int total = Integer.parseInt(data.get(0));
					int submitCount = Integer.parseInt(data.get(1));
					if (total != submitCount) {
						throw new SmartFormError("'" + group.getName() + "' 目前还有未提交的行，请检查");
					}
					int min = group.getMinAdd() == null ? 0 : group.getMinAdd();
					if (total < min) {
						throw new SmartFormError("'" + group.getName() + "' 最少需要填 " + min + " 行，请检查");
					}
					// 最大1000行
					int max = group.getMaxAdd() == null ? 1000 : group.getMaxAdd();
					if (total > max) {
						throw new SmartFormError("'" + group.getName() + "' 最多只能填 " + max + " 行，请检查");
					}

					if (fixedId != null) {
						boolean tips = false;
						HashMap<String, Boolean> hasIds = null;
						if (data.size() > 2) {
							hasIds = new HashMap<String, Boolean>();
							for (int j = 2; j < data.size(); j++) {
								hasIds.put(data.get(j), true);
							}
							// 已有的固定行数量少于固定行数
							if (hasIds.size() < fixedId.size()) {
								tips = true;
							}
						} else
							tips = true;
						if (tips) {
							// 执行固定行加入
							this.addFixedLine(contentId, group, hasIds);
							throw new SmartFormError("'" + group.getName() + "' 目前还有未提交的行，请检查");
						}
					}
				}
				for (ComponentStateEntity state : componentStates) {
					if (state.getTableName().equals(component.getDbTable())
							&& state.getWorkType() == component.getWorkType()
							&& (state.getState() == ContentStateType.SAVE.value)) {
						allComponents.remove(i);
					}
				}
			}
			// 所有组件都已经提交
			if (allComponents.size() == 0) {
				// 设置提交
				oldData.setState(ContentStateType.SAVE.value);
				//oldData.setSubmitTime(System.currentTimeMillis());
				oldData.setModifiedAt(System.currentTimeMillis());
				contentMainDao.updateContent(oldData, orgForm.getTableName(), null);
			} else {
				throw new SmartFormError("目前还表单数据未提交，请检查");
			}
		} else {
			throw new SmartFormError("目前还没有提交任何数据");
		}
	}

	/**
	 * 根据内容ID获取主表基本数据，获取状态，修改/添加时间等
	 *
	 * @param id
	 * @return
	 */
	private SmartFormContent getMainContentBase(String dbName, String dbTable, String formId, String userId,
			List<String> columns, Map<String, Object> extraData) {
		Map<String, Object> dataMap = contentMainDao.getContentQuery(dbTable, formId, userId, columns, extraData);
		if (dataMap != null) {
			SmartFormContent data = new SmartFormContent();
			data.setId((String) dataMap.get("id"));
			data.setFormId((String) dataMap.get("form_id"));
			// data.setUserId((String) dataMap.get("user_id"));
			data.setCreatedAt((Long) dataMap.get("created_at"));
			data.setModifiedAt((Long) dataMap.get("modified_at"));
			data.setState(handleInteger(dataMap.get("state")));
			data.setReasonInfo((String) dataMap.get("refuse_info"));
			return data;
		}
		return null;
	}

	/**
	 * 根据内容ID获取主表基本数据，获取状态，修改/添加时间等
	 *
	 * @param id
	 * @return
	 */
	private SmartFormContent getMainContentBase(String dbName, String dbTable, String id) {
		return this.getMainContentBase(dbName, dbTable, id, null);
	}

	/**
	 * 根据内容ID获取主表基本数据，获取状态，修改/添加时间等
	 *
	 * @param id
	 * @return
	 */
	private SmartFormContent getMainContentBase(String dbName, String dbTable, String id, List<DBFieldMapper> mapper) {
		List<String> columns = null;
		String endDate = null;
		// 判定是否有从主表读取结束时间
		if (mapper != null) {
			endDate = this.getMapperTable(mapper, DBFieldMapperType.ENDDATE);
			if (endDate != null && !endDate.equals("")) {
				columns = new ArrayList<String>();
				columns.add(endDate);
			}
		}

		Map<String, Object> dataMap = contentMainDao.getContent(dbTable, id, columns);
		if (dataMap != null) {
			SmartFormContent data = new SmartFormContent();
			data.setId((String) dataMap.get("id"));
			data.setFormId((String) dataMap.get("form_id"));
			// data.setUserId((String) dataMap.get("user_id"));
			data.setCreatedAt((Long) dataMap.get("created_at"));
			data.setModifiedAt((Long) dataMap.get("modified_at"));
			data.setState(handleInteger(dataMap.get("state")));
			data.setReasonInfo((String) dataMap.get("refuse_info"));
			// 设置填报截止日期
			if (endDate != null && dataMap.containsKey(endDate)) {
				Long time = handleLong(dataMap.get(endDate));
				data.setEndDate(time);
			}
			return data;
		}
		return null;
	}

	/**
	 * 获取当前分页中的超级组件和表格组
	 *
	 * @param nowPage
	 * @param hasGridView
	 *            是否包含GridView
	 * @return
	 */
	private List<FormContentBaseDTO<Group>> getPageComponents(FormPage nowPage, boolean hasGridView) {
		// 获取当前分页内的组件列表
		List<FormContentBaseDTO<Group>> components = new ArrayList<FormContentBaseDTO<Group>>();
		List<FormFieldBase> pageFields = nowPage.getFieldList();
		// 遍历分页旗下的超级组件
		if (pageFields != null) {
			for (FormFieldBase field : pageFields) {
				if (field instanceof Group) {
					Group group = (Group) field;
					// 组件隐藏，则无需处理
					if (group.getHide() != null && group.getHide())
						continue;
					if (group.getGroupType() == GroupType.SUPER.value) {
						// 遍历超级组件旗下是否还有表格组
						List<FormFieldBase> groupFields = group.getFieldList();
						if (groupFields != null) {
							boolean onlyGroup = true;
							for (FormFieldBase gfield : groupFields) {
								if (gfield instanceof Group) {
									Group tableGroup = (Group) gfield;
									// 组件隐藏，则无需处理
									if (tableGroup.getHide() != null && tableGroup.getHide())
										continue;
									if (tableGroup.getGroupType() == GroupType.TABLE.value) {
										// 排除GridView模式表格，此模式有单独的存取接口
										if (hasGridView || !tableGroup.hasGridView()) {
											FormContentBaseDTO<Group> component = new FormContentBaseDTO<Group>();
											component.setDbName(tableGroup.getDbName());
											component.setDbTable(tableGroup.getTable());
											int workType = tableGroup.getWorkType() == null ? 0
													: tableGroup.getWorkType();
											tableGroup.setWorkType(workType);
											component.setWorkType(workType);
											component.setData(tableGroup);
											components.add(component);
										}
									} else if (tableGroup.getGroupType() == GroupType.STYLE.value) {
										onlyGroup = false;
									}
								} else {
									onlyGroup = false;
								}
							}
							// 超级组件中不只有表格组,否则不对超级组件进行单独存储
							if (!onlyGroup) {
								FormContentBaseDTO<Group> component = new FormContentBaseDTO<Group>();
								component.setDbName(group.getDbName());
								component.setDbTable(group.getTable());
								int workType = group.getWorkType() == null ? 0 : group.getWorkType();
								group.setWorkType(workType);
								component.setWorkType(workType);
								component.setData(group);
								components.add(component);
							}
						}
					}
				}
			}
		}
		return components;
	}

	/**
	 * 查询表单中的GridView组
	 *
	 * @param origForm
	 * @param groupId
	 * @return
	 */
	private Group getGridGroup(SmartForm origForm, String groupId) {
		if (origForm != null && origForm.getPageList() != null) {
			for (FormPage page : origForm.getPageList()) {
				if (page.getFieldList() != null && page.getFieldList().size() > 0) {
					for (FormFieldBase pageField : page.getFieldList()) {
						if (pageField instanceof Group) {
							Group group = (Group) pageField;
							if (group.getGroupType() == GroupType.SUPER.value) {
								// 遍历超级组件旗下是否还有表格组
								List<FormFieldBase> groupFields = group.getFieldList();
								if (groupFields != null) {
									for (FormFieldBase gfield : groupFields) {
										if (gfield instanceof Group) {
											group = (Group) gfield;
											if (group.getGroupType() == GroupType.TABLE.value) {
												// GridView模式表格
												if (group.hasGridView() && group.getId().equals(groupId)) {
													return group;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取所有组件状态
	 *
	 * @param contentId
	 * @return
	 */
	private List<ComponentStateEntity> getComponentState(String dbTable, String contentId) {
		// 设置查询条件
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dbTable", dbTable);
		params.put("contentId", contentId);
		Query query = new Query(params);
		List<ComponentStateEntity> componentStates = this.componentStateDao.list(query);
		return componentStates;
	}

	/**
	 * 处理退回的组件，没退回的从components中移除
	 *
	 * @param components
	 * @param componentStates
	 */
	private void handleRefuseComponent(List<FormContentBaseDTO<Group>> components,
			List<ComponentStateEntity> componentStates) {
		if (components != null && componentStates != null)
			for (int i = components.size() - 1; i >= 0; i--) {
				FormContentBaseDTO<Group> component = components.get(i);
				// 遍历组件状态
				for (ComponentStateEntity componentState : componentStates) {
					if (componentState.getTableName().equals(component.getDbTable())
							&& componentState.getWorkType() == component.getWorkType()) {
						// 判定当前组件状态不可编辑
						if (componentState.getState() == ContentStateType.LOCK.value) {
							// 移除不可编辑的组件
							components.remove(component);
						}
						break;
					}
				}
			}
	}

	/**
	 * 获取表单配置中的表名
	 *
	 * @param mapper
	 * @param type
	 * @return
	 */
	private String getMapperTable(List<DBFieldMapper> mapper, DBFieldMapperType type) {
		String table = null;
		if (mapper != null) {
			for (DBFieldMapper fmap : mapper) {
				if (fmap.getMapperType() == type.value) {
					table = fmap.getDbAlias();
				}
			}
		}
		return table;
	}

	/**
	 * 处理固定的额外参数
	 *
	 * @param contentId
	 * @param mappers
	 * @param mainContentMap
	 */
	private void handleParamExtraData(String contentId, List<DBFieldMapper> mappers,
			Map<String, Object> mainContentMap) {
		// 处理固定表单固定额外参数
		String extraData = formContentDao.getExtraData(contentId);
		if (mappers != null && mappers.size() > 0 && extraData != null && !extraData.equals("")) {
			JSONObject jsonObject = JSONObject.parseObject(extraData);
			// json对象转Map
			Map<String, Object> map = (Map<String, Object>) jsonObject;
			for (DBFieldMapper fmap : mappers) {
				// 将额外数据固定参数填入mainContentMap
				if (fmap.getMapperType() == DBFieldMapperType.PARAM.value) {
					if (map.containsKey(fmap.getAlias())) {
						Object obj = map.get(fmap.getAlias());
						// 1，字符串；2，Long
						if (fmap.getDataType() == 2) {
							try {
								mainContentMap.put(fmap.getDbAlias(), Long.parseLong(obj.toString()));
							} catch (Exception ex) {
							}
						} else
							mainContentMap.put(fmap.getDbAlias(), obj.toString());
					}
				}
			}
		}
	}

	/**
	 * 筛选要存入数据库的数据
	 *
	 * @param pageId
	 *            要处理的分页ID
	 * @param orgForm
	 *            表单结构
	 * @param data
	 *            表单内容
	 * @param components
	 *            当前页要存储的组件列表
	 * @param mainContentMap
	 *            主表内容数据
	 * @param componentList
	 *            组件表数据
	 * @param tableList
	 *            表格表列表数据
	 * @param uploadList
	 * @param hasStorage
	 *            是否暂存，暂存不做验证
	 * @throws ParseException
	 */
	private void handleDBData(String pageId, SmartForm orgForm, SmartFormContent data,
			List<FormContentBaseDTO<Group>> components, Map<String, Object> mainContentMap,
			List<FormContentBaseDTO<FormContentComponentEntity>> componentList,
			List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList,
			List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, boolean hasStorage)
			throws ParseException {
		// 确定提交的分页数据
		List<FormPage> pages = data.getPageList();
		FormPage nowPage = null;
		for (FormPage page : pages) {
			if (page.getId().equals(pageId)) {
				nowPage = page;
				break;
			}
		}

		// 遍历分页提取组件数据
		if (nowPage != null && nowPage.getFieldList() != null && nowPage.getFieldList().size() > 0) {
			// 遍历要存储的组件
			for (FormContentBaseDTO<Group> component : components) {
				// 获取组件内容
				Group contentGroup = getContentGroup(component, nowPage);
				Group group = component.getData();
				if (group.getGroupType() == GroupType.SUPER.value) {
					boolean useMainTable = false;
					// 判定组件的库名与表名是否与表单主表相同
					if (orgForm.getDbName().equals(contentGroup.getDbName())
							&& orgForm.getTableName().equals(contentGroup.getTable())) {
						useMainTable = true;
					}
					// 处理超级组件
					for (FormFieldBase widget : contentGroup.getFieldList()) {
						if (widget instanceof Group) {
							// 处理超级组件旗下的样式组
							Group styleGroup = (Group) widget;
							if (styleGroup.getGroupType() == GroupType.STYLE.value) {
								// 遍历样式组字段
								for (FormFieldBase styleWidget : styleGroup.getFieldList()) {
									if (styleWidget instanceof WidgetBase) {
										this.handleFieldDTO(useMainTable, false, data.getId(), null, component,
												(WidgetBase) styleWidget, mainContentMap, componentList, tableList,
												uploadList, orgForm.getFieldMapperList(), null, hasStorage);
									}
								}
							}
						} else if (widget instanceof WidgetBase) {
							this.handleFieldDTO(useMainTable, false, data.getId(), null, component, (WidgetBase) widget,
									mainContentMap, componentList, tableList, uploadList, orgForm.getFieldMapperList(),
									null, hasStorage);
						}
					}
				} else if (group.getGroupType() == GroupType.TABLE.value) {
					// 处理表格
					if (group.getSteerable() == null || !group.getSteerable()) {
						// 遍历表格组行
						if (contentGroup.getLineList() != null && contentGroup.getLineList().size() > 0) {
							// 查找table存储对象
							FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = this.getTableDTO(component,
									tableList);
							int lineNum = 0;
							for (GroupLine line : contentGroup.getLineList()) {
								// 遍历组字段
								if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
										&& line.getFieldList().size() > 0) {
									lineNum++;
									// 创建行
									FormContentTableEntity lineDTO = new FormContentTableEntity();
									String uuid = UUIDUtil.getNextId();
									lineDTO.setId(uuid);
									lineDTO.setContentId(data.getId());
									lineDTO.setLineNum(lineNum);
									lineDTO.setWorkType(component.getWorkType());
									lineDTO.setCreatedAt(System.currentTimeMillis());
									lineDTO.setModifiedAt(System.currentTimeMillis());
									if (hasStorage) {
										lineDTO.setState(ContentStateType.STORAGE.value);
									} else {
										lineDTO.setState(ContentStateType.SAVE.value);
									}
									// map添加
									Query query = new Query();
									lineDTO.setContent(query);
									tableDTO.getData().add(lineDTO);
									for (WidgetBase widget : line.getFieldList()) {
										if (widget instanceof WidgetBase) {
											this.handleFieldDTO(false, true, data.getId(), lineDTO, component,
													(WidgetBase) widget, mainContentMap, componentList, tableList,
													uploadList, null, null, hasStorage);
										}
									}
								}
							}
						}
					} else {
						// 可控表格，做为超级组件处理
						boolean useMainTable = false;
						// 判定组件的库名与表名是否与表单主表相同
						if (orgForm.getDbName().equals(contentGroup.getDbName())
								&& orgForm.getTableName().equals(contentGroup.getTable())) {
							useMainTable = true;
						}
						// 遍历行
						for (GroupLine line : contentGroup.getLineList()) {
							// 遍历组字段
							if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
									&& line.getFieldList().size() > 0) {
								for (WidgetBase widget : line.getFieldList()) {
									if (widget instanceof WidgetBase) {
										this.handleFieldDTO(useMainTable, false, data.getId(), null, component,
												(WidgetBase) widget, mainContentMap, componentList, tableList,
												uploadList, null, line.getLineNum(), hasStorage);
									}
								}
							}
						}
					}
				}
			}

		}
	}

	/**
	 * 获取内容组件
	 *
	 * @param component
	 *            查询的组件
	 * @param nowPage
	 *            内容分页
	 */
	private Group getContentGroup(FormContentBaseDTO<Group> component, FormPage nowPage) {
		// 遍历分页旗下超级组件
		for (FormFieldBase pageField : nowPage.getFieldList()) {
			if (pageField instanceof Group) {
				Group group = (Group) pageField;
				// 是否是该组
				if (component.getData().getId().equals(group.getId())) {
					int workType = group.getWorkType() == null ? 0 : group.getWorkType();
					group.setWorkType(workType);
					return group;
				}
				if (group.getGroupType() == GroupType.SUPER.value && group.getFieldList() != null) {
					// 遍历超级组件字段
					for (FormFieldBase widget : group.getFieldList()) {
						if (widget instanceof Group) {
							// 处理超级组件旗下的组
							Group tableGroup = (Group) widget;
							if (tableGroup.getGroupType() == GroupType.TABLE.value) {
								// 是否是该组
								if (component.getData().getId().equals(tableGroup.getId())) {
									int workType = tableGroup.getWorkType() == null ? 0 : tableGroup.getWorkType();
									tableGroup.setWorkType(workType);
									return tableGroup;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取结构字段实体
	 *
	 * @param group
	 * @param fieldId
	 * @return
	 */
	private WidgetBase getOriginalWidget(Group group, Integer lineNum, String fieldId) {
		if (group.getGroupType() == GroupType.SUPER.value) {
			if (group.getFieldList() != null) {
				// 遍历超级组件字段
				for (FormFieldBase widget : group.getFieldList()) {
					if (widget instanceof Group) {
						// 处理超级组件旗下的组
						Group tableGroup = (Group) widget;
						if (tableGroup.getGroupType() == GroupType.STYLE.value) {
							// 遍历样式组字段
							for (FormFieldBase styleWidget : tableGroup.getFieldList()) {
								if (styleWidget instanceof WidgetBase
										&& ((WidgetBase) styleWidget).getId().equals(fieldId))
									return (WidgetBase) styleWidget;
							}
						}
					} else {
						// 处理超级组件旗下字段
						if (widget instanceof WidgetBase && ((WidgetBase) widget).getId().equals(fieldId))
							return (WidgetBase) widget;
					}
				}
			}
		} else if (group.getGroupType() == GroupType.TABLE.value) {
			// 处理表格
			if (group.getSteerable() == null || !group.getSteerable()) {
				// 遍历表格原始字段
				if (group.getOriginalLine() != null) {
					GroupLine line = group.getOriginalLine();
					// 遍历组字段
					if (line.getFieldList() != null && line.getFieldList().size() > 0) {
						for (WidgetBase widget : line.getFieldList()) {
							// 处理超级组件旗下字段
							if (widget instanceof WidgetBase && ((WidgetBase) widget).getId().equals(fieldId))
								return (WidgetBase) widget;
						}
					}
				}
			} else {
				// 可控表格，做为超级组件处理
				if (group.getLineList() != null && group.getLineList().size() > 0) {
					for (GroupLine line : group.getLineList()) {
						// 遍历组字段
						if (line.getLineType() == GroupLineType.COMMON.value && line.getFieldList() != null
								&& line.getFieldList().size() > 0 && lineNum == line.getLineNum()) {
							for (WidgetBase widget : line.getFieldList()) {
								// 处理超级组件旗下字段
								if (widget instanceof WidgetBase && ((WidgetBase) widget).getId().equals(fieldId))
									return (WidgetBase) widget;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 查找table存储对象,如果没有则初始化
	 *
	 * @param component
	 * @param tableList
	 * @return
	 */
	private FormContentBaseDTO<List<FormContentTableEntity>> getTableDTO(FormContentBaseDTO<Group> component,
			List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList) {
		// 查找table存储对象
		FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = null;
		for (FormContentBaseDTO<List<FormContentTableEntity>> tableItem : tableList) {
			if (tableItem.getDbName().equals(component.getDbName())
					&& tableItem.getDbTable().equals(component.getDbTable())
					&& tableItem.getWorkType().equals(component.getWorkType())) {
				tableDTO = tableItem;
				break;
			}
		}
		// 初始化组件存储对象
		if (tableDTO == null) {
			tableDTO = new FormContentBaseDTO<List<FormContentTableEntity>>();
			List<FormContentTableEntity> entity = new ArrayList<FormContentTableEntity>();

			tableDTO.setData(entity);
			tableDTO.setDbName(component.getDbName());
			tableDTO.setDbTable(component.getDbTable());
			tableDTO.setWorkType(component.getWorkType());
			// map添加
			tableDTO.setContent(new Query());
			tableList.add(tableDTO);
		}
		return tableDTO;
	}

	/**
	 * 查找upload存储对象,如果没有则初始化
	 *
	 * @param component
	 * @param tableList
	 * @return
	 */
	private FormContentBaseDTO<List<FormContentUploadsEntity>> getUploadData(WidgetUpload widget,
			List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, Group component) {
		// 查找uploadData存储对象
		FormContentBaseDTO<List<FormContentUploadsEntity>> uploadData = null;
		for (FormContentBaseDTO<List<FormContentUploadsEntity>> uploadItem : uploadList) {
			if (uploadItem.getDbTable().equals(component.getTable())
					&& uploadItem.getWorkType().equals(component.getWorkType())) {
				uploadData = uploadItem;
			}
		}
		// 初始化组件存储对象
		if (uploadData == null) {
			uploadData = new FormContentBaseDTO<List<FormContentUploadsEntity>>();
			List<FormContentUploadsEntity> entity = new ArrayList<FormContentUploadsEntity>();
			uploadData.setData(entity);
			uploadData.setDbTable(component.getTable());
			uploadData.setWorkType(component.getWorkType());
			uploadList.add(uploadData);
		}
		return uploadData;
	}

	/**
	 * 将field填充到存储实体中
	 *
	 * @param useMainTable
	 *            是否存到主表
	 * @param hasTable
	 *            是否是存到表格中
	 * @param contentId
	 *            表单内容ID
	 * @param component
	 * @param widget
	 * @param mainContentMap
	 * @param componentList
	 * @param tableList
	 * @param uploadList
	 * @param addToMainTable
	 *            是否追加至主表中
	 * @param hasStorage
	 * @throws ParseException
	 */
	private void handleFieldDTO(boolean useMainTable, boolean hasTable, String contentId, FormContentTableEntity line,
			FormContentBaseDTO<Group> component, WidgetBase widget, Map<String, Object> mainContentMap,
			List<FormContentBaseDTO<FormContentComponentEntity>> componentList,
			List<FormContentBaseDTO<List<FormContentTableEntity>>> tableList,
			List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, List<DBFieldMapper> mainMappers,
			Integer lineNum, boolean hasStorage) throws ParseException {
		// 根据字段ID查询结构数据
		WidgetBase orgWidget = getOriginalWidget(component.getData(), lineNum, widget.getId());

		// 是否追加至主表中
		if (mainMappers != null && mainMappers.size() > 0) {
			for (DBFieldMapper fmap : mainMappers) {
				if (fmap.getMapperType() == DBFieldMapperType.ALIAS.value && fmap.getAlias() != null) {
					String[] aliasConfig = fmap.getAlias().split("\\.");
					// 分割表名与字段名
					if (aliasConfig.length == 2) {
						WidgetBase base = (WidgetBase) orgWidget;
						// 是否要存储改字段
						if (aliasConfig[0].equals(component.getDbTable()) && aliasConfig[1].equals(base.getAlias())) {
							handleField((WidgetBase) widget, orgWidget, mainContentMap, uploadList, null,
									component.getData(), contentId, fmap.getDbAlias(), hasStorage);
						}
					}
				}
			}
		}

		// 处理超级组件旗下字段
		if (useMainTable) {
			handleField((WidgetBase) widget, orgWidget, mainContentMap, uploadList, null, component.getData(),
					contentId, null, hasStorage);
		} else if (!hasTable) {
			// 查找是否已经存在组件存储DTO
			FormContentBaseDTO<FormContentComponentEntity> componentDTO = null;
			for (FormContentBaseDTO<FormContentComponentEntity> componentItem : componentList) {
				if (componentItem.getDbName().equals(component.getDbName())
						&& componentItem.getDbTable().equals(component.getDbTable())
						&& componentItem.getWorkType().equals(component.getWorkType())) {
					componentDTO = componentItem;
					break;
				}
			}
			// 初始化组件存储对象
			if (componentDTO == null) {
				componentDTO = new FormContentBaseDTO<FormContentComponentEntity>();
				FormContentComponentEntity entity = new FormContentComponentEntity();
				String uuid = UUIDUtil.getNextId();
				entity.setId(uuid);
				entity.setContentId(contentId);
				entity.setWorkType(component.getWorkType());
				entity.setCreatedAt(System.currentTimeMillis());
				entity.setModifiedAt(System.currentTimeMillis());
				componentDTO.setData(entity);
				componentDTO.setDbName(component.getDbName());
				componentDTO.setDbTable(component.getDbTable());
				componentDTO.setWorkType(component.getWorkType());
				// map添加
				componentDTO.setContent(new Query());
				componentList.add(componentDTO);
			}
			// 添加组件存储数据
			handleField((WidgetBase) widget, orgWidget, componentDTO.getContent(), uploadList, null,
					component.getData(), contentId, null, hasStorage);
		} else {
			// 添加组件存储数据
			handleField((WidgetBase) widget, orgWidget, line.getContent(), uploadList, line.getId(),
					component.getData(), contentId, null, hasStorage);
		}
	}

	/**
	 * 简化字段值，只保留关键type，id，value，name等 验证必填项和简单规则 转换为Mysql存储实体
	 *
	 * @param widget
	 *            字段值
	 * @param orgWidget
	 *            原始数据，用于检测规则和赋值基本属性
	 * @param mainAlias
	 *            存到主表的字段名，存在则优先使用
	 * @param hasStorage
	 *            是否暂存，暂存不做复杂与必填验证
	 * @throws ParseException
	 */
	private void handleField(WidgetBase widget, WidgetBase orgWidget, Map<String, Object> contentMap,
			List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList, String lineId, Group component,
			String contentId, String mainAlias, boolean hasStorage) throws ParseException {
		int dataType = widget.getFieldType();
		// 数据容错
		if (orgWidget == null)
			return;
		String alias = mainAlias != null ? mainAlias : orgWidget.getAlias();
		// 空别名字段不处理
		if (dataType != WidgetType.UPLOAD.value && (alias == null || alias.equals("")))
			return;
		// 简化字段
		if (dataType == WidgetType.NUMBER.value) {
			// 数字框
			WidgetNumber vData = ((WidgetNumber) widget);
			if (vData.getDefValue() == null || vData.getDefValue().equals(BigDecimal.ZERO))
				contentMap.put(alias, 0);
			else
				contentMap.put(alias, vData.getDefValue());
		} else if (dataType == WidgetType.INPUT.value || dataType == WidgetType.TEXTAREA.value) {
			// 文本框
			WidgetInput vData = (WidgetInput) widget;
			if (CheckDataUtil.isNull(vData.getDefValue()))
				contentMap.put(alias, "");
			else
				contentMap.put(alias, vData.getDefValue());
		} else if (dataType == WidgetType.EDITOR.value) {
			// 富文本
			WidgetEditor vData = (WidgetEditor) widget;
			if (CheckDataUtil.isNull(vData.getDefValue()))
				contentMap.put(alias, "");
			else
				contentMap.put(alias, vData.getDefValue());
		} else if (dataType == WidgetType.RADIO.value) {
			// 单选
			WidgeRadio vData = (WidgeRadio) widget;
			if (CheckDataUtil.isNull(vData.getDefValue()))
				contentMap.put(alias, "");
			else
				contentMap.put(alias, vData.getDefValue());
		} else if (dataType == WidgetType.CHECKBOX.value || dataType == WidgetType.SELECT.value
				|| dataType == WidgetType.CASCADER.value) {
			// 多选
			WidgeSelect vData = (WidgeSelect) widget;
			if (CheckDataUtil.isNull(vData.getDefValue()))
				contentMap.put(alias, "");
			else {
				// 使用逗号拼接选项
				if (vData.getDefValue().size() == 1)
					contentMap.put(alias, vData.getDefValue().get(0));
				else {
					StringBuffer sb = new StringBuffer();
					for (String value : vData.getDefValue()) {
						sb.append(value + ",");
					}
					sb.deleteCharAt(sb.length() - 1);
					contentMap.put(alias, sb.toString());
				}
			}
		} else if (dataType == WidgetType.DATE.value || dataType == WidgetType.DATESPAN.value) {
			// 日期
			WidgetDate vData = (WidgetDate) widget;
			if (vData.getDefStartDate() == null)
				contentMap.put(alias, 0);
			else
				contentMap.put(alias, vData.getDefStartDate().getTime());
			if (dataType == WidgetType.DATESPAN.value) {
				Long endDate = vData.getDefEndDate() == null ? 0 : vData.getDefEndDate().getTime();
				contentMap.put(((WidgetDate) orgWidget).getEndAlias(), endDate);
			}

		} else if (dataType == WidgetType.TIME.value || dataType == WidgetType.TIMESPAN.value) {
			// 时间
			WidgetTime vData = (WidgetTime) widget;
			if (CheckDataUtil.isNull(vData.getDefStartTime()))
				contentMap.put(alias, 0);
			else {
				int timeStamp = this.getTimeStamp(vData.getDefStartTime());
				contentMap.put(alias, timeStamp);
			}
			if (dataType == WidgetType.TIMESPAN.value) {
				int timeStamp = CheckDataUtil.isNull(vData.getDefEndTime()) ? 0
						: this.getTimeStamp(vData.getDefEndTime());
				contentMap.put(((WidgetTime) orgWidget).getEndAlias(), timeStamp);
			}
		} else if (dataType == WidgetType.UPLOAD.value && uploadList != null) {
			// 上传
			WidgetUpload vData = (WidgetUpload) widget;
			if (vData.getDefValue() != null && vData.getDefValue().size() != 0) {
				FormContentBaseDTO<List<FormContentUploadsEntity>> uploadDTO = this.getUploadData(vData, uploadList,
						component);
				for (UploadItem value : vData.getDefValue()) {
					// 创建sql数据实体
					FormContentUploadsEntity data = new FormContentUploadsEntity();
					String uuid = UUIDUtil.getNextId();
					data.setId(uuid);
					data.setContentId(contentId);
					data.setTableName(component.getTable());
					data.setWorkType(component.getWorkType());
					data.setLineId(lineId);
					data.setUploadType(((WidgetUpload) orgWidget).getUploadType());
					data.setCreatedAt(System.currentTimeMillis());
					data.setModifiedAt(System.currentTimeMillis());

					data.setUrl(value.getUrl());
					data.setSize(value.getSize());
					data.setFileName(value.getName());
					data.setSuffix(value.getType());
					uploadDTO.getData().add(data);
				}
			}
		}
	}

	/**
	 * 处理短int
	 *
	 * @param date
	 * @return
	 */
	private Integer handleInteger(Object date) {
		if (date == null)
			return null;
		if (date instanceof Boolean) {
			return (Boolean) date ? 1 : 0;
		} else if (date instanceof Long) {
			return ((Long) date).intValue();
		} else if (date instanceof String) {
			return Integer.parseInt((String) date);
		}
		return (Integer) date;
	}

	/**
	 * 处理Long
	 *
	 * @param date
	 * @return
	 */
	private Long handleLong(Object date) {
		if (date == null)
			return null;
		if (date instanceof Boolean) {
			return (Boolean) date ? 1L : 0L;
		} else if (date instanceof Integer) {
			return (Long) date;
		} else if (date instanceof String) {
			return Long.parseLong((String) date);
		}
		return (Long) date;
	}

	/**
	 * 处理BigDecimal
	 *
	 * @param date
	 * @return
	 */
	private BigDecimal handleBigDecimal(Object date) {
		if (date == null)
			return null;
		if (date instanceof Boolean) {
			return new BigDecimal((Boolean) date ? 1 : 0);
		} else if (date instanceof Long) {
			return new BigDecimal(((Long) date));
		} else if (date instanceof Float) {
			return new BigDecimal((Float.toString((Float) date)));
		} else if (date instanceof Double) {
			return new BigDecimal((Double.toString((Double) date)));
		} else if (date instanceof BigDecimal) {
			return (BigDecimal) date;
		} else if (date instanceof String) {
			return new BigDecimal((String) date);
		}
		return new BigDecimal((Integer) date);
	}

	@Override
	//@DataSource(justClear = true)
	public FormFieldBase getFormFieldByForm(SmartForm smartForm, String fieldId) {
		return null;
	}

	/**
	 * 提交gridView行
	 *
	 * @param form
	 * @return
	 * @throws ParseException
	 */
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent submitGridLine(String formId, String contentId, String groupId, String line,
			Boolean storage) throws ParseException {
		// 读取表单结构数据，检查合法性，识别具体存储分页以及组织
		SmartForm orgForm = smartFormService.smartForm(formId, false);
		if (orgForm == null)
			throw new SmartFormError("当前表单已经不存在");

		GroupLine lineData = JSON.parseObject(line, GroupLine.class);
		if (lineData == null || lineData.getFieldList() == null || lineData.getFieldList().size() == 0
				|| lineData.getLineNum() == null) {
			throw new SmartFormError("数据提交失败");
		}

		// 切换数据源
		//DynamicDataSource.setDataSource(orgForm.getDbName());

		SmartFormContent oldData = this.executeSubmitGridLine(orgForm, lineData, contentId, groupId, line, storage);

		return oldData;
	}

	/**
	 * 执行提交gridView行
	 *
	 * @param orgForm
	 * @param lineData
	 * @param contentId
	 * @param groupId
	 * @param line
	 * @throws ParseException
	 */
	@Transactional
	private SmartFormContent executeSubmitGridLine(SmartForm orgForm, GroupLine lineData, String contentId,
			String groupId, String line, Boolean storage) throws ParseException {
		// 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
		SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
				orgForm.getFieldMapperList());

		// 状态表名
		String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
		// 上传表名
		String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

		// 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
		Group group = this.getGridGroup(orgForm, groupId);

		// 表格行保存暂不运行复杂规则
		// this.executeFormRule(contentId, orgForm, pageId, 1);

		// 判定状态数据
		boolean isCreated = false;
		boolean isRefuse = false;
		// 获取当前退回的组件状态
		List<ComponentStateEntity> componentStates = null;
		if (oldData != null) {
			if(orgForm.getSkipendtimevalidation()==null || orgForm.getSkipendtimevalidation().length()<1 || !orgForm.getSkipendtimevalidation().equals("1")) {
				// 判定填报截止日期
				this.ckFillEndDate(oldData);
			}
			// 获取当前退回的组件状态
			componentStates = this.getComponentState(stateTable, oldData.getId());
			// 编辑模式，进行状态判定，锁定状态无法编辑
			if (oldData.getState().intValue() == ContentStateType.LOCK.value)
				throw new SmartFormError("该内容已提交，无法编辑");

			// 如果是退回状态，验证当前具体退回的超级组件
//			if (oldData.getState().intValue() == ContentStateType.REFUSE.value) {
//				if (componentStates != null)
//					for (ComponentStateEntity state : componentStates)
//						if (state.getTableName().equals(group.getTable())
//								&& state.getWorkType() == group.getWorkType()) {
//							// 判定当前组件状态不可编辑
//							if (state.getState() != ContentStateType.REFUSE.value
//									&& state.getState() != ContentStateType.STORAGE.value) {
//								throw new SmartFormError("该表单内容并未退回，无法再次提交");
//							}
//						}
//
//				isRefuse = true;
//			}
			oldData.setName(orgForm.getName());
			oldData.setCreatedAt(oldData.getCreatedAt());
			oldData.setModifiedAt(new Date().getTime());
		} else {
			// 添加模式
			isCreated = true;
			oldData = new SmartFormContent();
			oldData.setId(contentId);
			// 获取用户ID
			// oldData.setUserId("");
			oldData.setName(orgForm.getName());
			oldData.setFormId(orgForm.getId());
			oldData.setCreatedAt(System.currentTimeMillis());
			oldData.setModifiedAt(System.currentTimeMillis());
			oldData.setState(ContentStateType.STORAGE.value);
		}

		// 要发布到主表的额外字段
		Map<String, Object> mainContentMap = new HashMap<String, Object>();
		// 要存储的表格表
		FormContentTableEntity table = new FormContentTableEntity();
		table.setId(lineData.getId());
		table.setContentId(contentId);
		table.setWorkType(group.getWorkType());
		table.setLineNum(lineData.getLineNum());
		table.setCreatedAt(System.currentTimeMillis());
		table.setModifiedAt(System.currentTimeMillis());
		if(storage != null && storage){
			table.setState(ContentStateType.STORAGE.value);
		} else {
			table.setState(ContentStateType.SAVE.value);
		}

		// 要存储的上传列表
		List<FormContentBaseDTO<List<FormContentUploadsEntity>>> uploadList = new ArrayList<FormContentBaseDTO<List<FormContentUploadsEntity>>>();

		// 检测必填字段是否存储

		Query content = new Query();
		// 遍历内容中的字段数据，合计mysql需要存储的表与字段
		for (WidgetBase widget : lineData.getFieldList()) {
			if (widget instanceof WidgetBase) {
				// 根据字段ID查询结构数据
				WidgetBase orgWidget = getOriginalWidget(group, null, widget.getId());
				this.handleField((WidgetBase) widget, orgWidget, content, uploadList, lineData.getId(), group,
						contentId, null, false);
			}
		}

		// 存储表格表
		tableDao.saveContent(table, group.getTable(), content);
		// 存储上传表
		if (uploadList.size() > 0) {
			for (FormContentBaseDTO<List<FormContentUploadsEntity>> uploadData : uploadList) {
				// 删除已添加的数据
				if (!isCreated)
					uploadsDao.removeContent(uploadTable, oldData.getId(), uploadData.getDbTable(),
							uploadData.getWorkType(), lineData.getId(), null);
				// 批量一个表的上传数据
				uploadsDao.batchSave(uploadData.getData(), uploadTable);
			}
		}
		// 存储组件状态
		ComponentStateEntity comState = new ComponentStateEntity();
		comState.setId(UUIDUtil.getNextId());
		comState.setDbTable(stateTable);
		comState.setContentId(oldData.getId());
		comState.setTableName(group.getTable());
		comState.setWorkType(group.getWorkType());
		comState.setCreatedAt(System.currentTimeMillis());
		comState.setModifiedAt(System.currentTimeMillis());
		if (!isRefuse)
			comState.setState(ContentStateType.SAVE.value);
//		else
//			comState.setState(ContentStateType.REFUSESUBMIT.value);
		this.componentStateDao.saveContent(comState, stateTable);

		// 处理表单回退
		List<FormContentBaseDTO<Group>> stateBackGroups = this.getStateBack(group, orgForm, componentStates);
		// 执行表单回退
		for (FormContentBaseDTO<Group> dto : stateBackGroups) {
			ComponentStateEntity comStateBack = new ComponentStateEntity();
			comStateBack.setId(UUIDUtil.getNextId());
			comStateBack.setDbTable(stateTable);
			comStateBack.setContentId(oldData.getId());
			comStateBack.setTableName(dto.getDbTable());
			comStateBack.setWorkType(dto.getWorkType());
			comStateBack.setCreatedAt(System.currentTimeMillis());
			comStateBack.setModifiedAt(System.currentTimeMillis());
			comStateBack.setState(ContentStateType.STORAGE.value);
			this.componentStateDao.saveContent(comStateBack, stateTable);
		}

		Query mainContent = null;
		// 存储主表mysql数据
		if (isCreated) {
			// 处理固定表单固定额外参数
			handleParamExtraData(oldData.getId(), orgForm.getFieldMapperList(), mainContentMap);
			if (mainContentMap.size() > 0)
				mainContent = new Query(mainContentMap);
			contentMainDao.saveContent(oldData, orgForm.getTableName(), mainContent);
		}

		this.executeFormRule(contentId, orgForm, null, 2);

		// 重新获取状态进行设置
		componentStates = this.getComponentState(stateTable, oldData.getId());
		this.setPageState(orgForm.getPageList(), componentStates);
		this.handleNowPage(orgForm.getPageList(), null, true);
		// 将最新的分页状态返回给前端
		oldData.setPageList(orgForm.getPageList());

		return oldData;
	}

	/**
	 * 删除gridView行
	 *
	 * @param form
	 * @return
	 * @throws ParseException
	 */
	@Override
	//@DataSource(justClear = true)
	public SmartFormContent deleteGridLine(String formId, String contentId, String groupId, String lineId) {
		// 读取表单结构数据，检查合法性，识别具体存储分页以及组织
		SmartForm orgForm = smartFormService.smartForm(formId, false);
		if (orgForm == null)
			throw new SmartFormError("当前表单已经不存在");

		// 切换数据源
		//DynamicDataSource.setDataSource(orgForm.getDbName());

		SmartFormContent oldData = this.executeDeleteGridLine(orgForm, contentId, groupId, lineId);
		return oldData;
	}

	/**
	 * 执行删除gridView行
	 *
	 * @param orgForm
	 * @param contentId
	 * @param groupId
	 * @param lineId
	 * @throws ParseException
	 */
	@Transactional
	private SmartFormContent executeDeleteGridLine(SmartForm orgForm, String contentId, String groupId, String lineId) {
		boolean isRefuse = false;
		// 从SQL中查询该用户是否填过此表单，并获取表单内容基本信息
		SmartFormContent oldData = this.getMainContentBase(orgForm.getDbName(), orgForm.getTableName(), contentId,
				orgForm.getFieldMapperList());

		// 状态表名
		String stateTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.STATETABLE);
		// 上传表名
		String uploadTable = this.getMapperTable(orgForm.getFieldMapperList(), DBFieldMapperType.UPLOADTABLE);

		// 获取当前退回的组件状态
		List<ComponentStateEntity> componentStates = null;
		if (oldData != null) {
			if(orgForm.getSkipendtimevalidation()==null || orgForm.getSkipendtimevalidation().length()<1 || !orgForm.getSkipendtimevalidation().equals("1")) {
				// 判定填报截止日期
				this.ckFillEndDate(oldData);
			}
			// 获取当前退回的组件状态
			componentStates = this.getComponentState(stateTable, oldData.getId());
			// 编辑模式，进行状态判定，锁定状态无法提交
			if (oldData.getState().intValue() == ContentStateType.LOCK.value)
				throw new SmartFormError("该内容已提及哦啊，无法编辑");

			// 获取当页中的超级组件与表格组（超级组件中的表格组也在其中）
			Group group = this.getGridGroup(orgForm, groupId);
			// 如果是退回状态，验证当前具体退回的超级组件
//			if (oldData.getState().intValue() == ContentStateType.REFUSE.value) {
//				if (componentStates != null)
//					for (ComponentStateEntity state : componentStates)
//						if (state.getTableName().equals(group.getTable())
//								&& state.getWorkType() == group.getWorkType()) {
//							// 判定当前组件状态不可编辑
//							if (state.getState() != ContentStateType.REFUSE.value
//									&& state.getState() != ContentStateType.STORAGE.value) {
//								throw new SmartFormError("该表单内容并未退回，无法再次提交");
//							}
//						}
//
//				isRefuse = true;
//			} else {
			{
				oldData = new SmartFormContent();
				oldData.setId(contentId);
				// 获取用户ID
				// oldData.setUserId("");
				oldData.setName(orgForm.getName());
				oldData.setFormId(orgForm.getId());
				oldData.setCreatedAt(System.currentTimeMillis());
				oldData.setModifiedAt(System.currentTimeMillis());
				oldData.setState(ContentStateType.STORAGE.value);
			}
			// 验证用户ID

			// 查询行
			Map<String, Object> dataMaps = tableDao.getContent(group.getTable(), oldData.getId(), group.getWorkType(),
					lineId, null);
			if (dataMaps.containsKey("fixed_id")) {
				if (!dataMaps.get("fixed_id").toString().equals("")) {
					throw new SmartFormError("此行为固定行无法删除");
				}
			}
			// 删除行
			tableDao.removeContent(group.getTable(), oldData.getId(), group.getWorkType(), lineId);
			Query query = new Query();
			query.put("dbTable", group.getTable());
			query.put("contentId", oldData.getId());
			query.put("workType", group.getWorkType());
			int count = tableDao.countTotal(query);
			// 存储组件状态
			ComponentStateEntity comState = new ComponentStateEntity();
			comState.setId(UUIDUtil.getNextId());
			comState.setDbTable(stateTable);
			comState.setContentId(oldData.getId());
			comState.setTableName(group.getTable());
			comState.setWorkType(group.getWorkType());
			comState.setCreatedAt(System.currentTimeMillis());
			comState.setModifiedAt(System.currentTimeMillis());

			// 删除已添加的上传表数据
			uploadsDao.removeContent(uploadTable, oldData.getId(), group.getTable(), group.getWorkType(), lineId, null);

			// 删光数据改为暂存状态
			if (count == 0)
				comState.setState(ContentStateType.STORAGE.value);
			else {
				comState.setState(ContentStateType.SAVE.value);
//				if (!isRefuse)
//					comState.setState(ContentStateType.SUBMIT.value);
//				else
//					comState.setState(ContentStateType.REFUSESUBMIT.value);
			}
			this.componentStateDao.saveContent(comState, stateTable);
			// 处理表单回退
			List<FormContentBaseDTO<Group>> stateBackGroups = this.getStateBack(group, orgForm, componentStates);
			// 执行表单回退
			for (FormContentBaseDTO<Group> dto : stateBackGroups) {
				ComponentStateEntity comStateBack = new ComponentStateEntity();
				comStateBack.setId(UUIDUtil.getNextId());
				comStateBack.setDbTable(stateTable);
				comStateBack.setContentId(oldData.getId());
				comStateBack.setTableName(dto.getDbTable());
				comStateBack.setWorkType(dto.getWorkType());
				comStateBack.setCreatedAt(System.currentTimeMillis());
				comStateBack.setModifiedAt(System.currentTimeMillis());
				comStateBack.setState(ContentStateType.STORAGE.value);
				this.componentStateDao.saveContent(comStateBack, stateTable);
			}
			// 重新获取状态进行设置
			componentStates = this.getComponentState(stateTable, oldData.getId());
			this.setPageState(orgForm.getPageList(), componentStates);
			this.handleNowPage(orgForm.getPageList(), null, true);
			// 将最新的分页状态返回给前端
			oldData.setPageList(orgForm.getPageList());
			oldData.setName(orgForm.getName());
		}
		return oldData;
	}

	/**
	 * 获取回退状态组
	 *
	 * @param components
	 * @param origForm
	 * @return
	 */
	private List<FormContentBaseDTO<Group>> getStateBack(List<FormContentBaseDTO<Group>> components, SmartForm origForm,
			List<ComponentStateEntity> componentStates) {
		List<FormContentBaseDTO<Group>> stateBackComponent = new ArrayList<FormContentBaseDTO<Group>>();
		// 没有状态则跳过
		if (componentStates == null || componentStates.size() == 0)
			return stateBackComponent;
		List<String> groupIDList = new ArrayList<String>();
		// 收集退状态的组ID
		for (FormContentBaseDTO<Group> data : components) {
			if (data.getData().getStateBackGorups() != null) {
				groupIDList.addAll(data.getData().getStateBackGorups());
			}
		}
		this.handleStateBack(stateBackComponent, groupIDList, origForm, componentStates);
		return stateBackComponent;
	}

	/**
	 * 获取回退状态组,单个组的情况
	 *
	 * @param component
	 * @param origForm
	 * @param componentStates
	 * @return
	 */
	private List<FormContentBaseDTO<Group>> getStateBack(Group component, SmartForm origForm,
			List<ComponentStateEntity> componentStates) {
		List<FormContentBaseDTO<Group>> stateBackComponent = new ArrayList<FormContentBaseDTO<Group>>();
		// 没有状态则跳过
		if (componentStates == null || componentStates.size() == 0)
			return stateBackComponent;
		if (component.getStateBackGorups() != null) {
			// 收集退状态的组ID
			List<String> groupIDList = component.getStateBackGorups();
			this.handleStateBack(stateBackComponent, groupIDList, origForm, componentStates);
		}
		return stateBackComponent;
	}

	private void handleStateBack(List<FormContentBaseDTO<Group>> stateBackComponent, List<String> groupIDList,
			SmartForm origForm, List<ComponentStateEntity> componentStates) {
		// 查找回退的组信息并组织List<FormContentBaseDTO<Group>>
		for (String groupId : groupIDList) {
			Group group = this.getGroup(origForm, groupId);
			if (group != null && group.getTable() != null && !group.getTable().equals("")) {
				// 遍历组件状态
				for (ComponentStateEntity componentState : componentStates) {
					if (componentState.getTableName().equals(group.getTable())
							&& componentState.getWorkType() == group.getWorkType()) {
//						// 判定组件状态是提交，则加入回退
//						if (componentState.getState() == ContentStateType.SUBMIT.value
//								|| componentState.getState() == ContentStateType.REFUSESUBMIT.value) {
//							FormContentBaseDTO<Group> dto = new FormContentBaseDTO<Group>();
//							dto.setData(group);
//							// dto.setDbName(group.getDbName());
//							dto.setDbTable(group.getTable());
//							int workType = group.getWorkType() == null ? 0 : group.getWorkType();
//							dto.setWorkType(workType);
//							stateBackComponent.add(dto);
//						}
					}
				}
			}
		}
	}

	/**
	 * 根据组件ID获取组
	 *
	 * @param origForm
	 * @param groupId
	 * @return
	 */
	private Group getGroup(SmartForm origForm, String groupId) {
		if (origForm != null && origForm.getPageList() != null) {
			for (FormPage page : origForm.getPageList()) {
				if (page.getFieldList() != null && page.getFieldList().size() > 0) {
					for (FormFieldBase pageField : page.getFieldList()) {
						if (pageField instanceof Group) {
							Group group = (Group) pageField;
							if (group.getGroupType() == GroupType.SUPER.value) {
								if (group.getId().equals(groupId)) {
									return group;
								}
								List<FormFieldBase> groupFields = group.getFieldList();
								if (groupFields != null) {
									for (FormFieldBase gfield : groupFields) {
										if (gfield instanceof Group) {
											group = (Group) gfield;
											if (group.getId().equals(groupId)) {
												return group;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 判定是否首次填写固定行
	 *
	 * @param contentId
	 * @param nowPage
	 * @throws ParseException
	 */
	@Transactional
	private void firstFixedLine(String contentId, FormPage nowPage, String stateTable) throws ParseException {
		if (nowPage.getFieldList() != null) {
			for (FormFieldBase field : nowPage.getFieldList()) {
				if (field instanceof Group) {
					Group group = (Group) field;
					if (group.getGroupType() == GroupType.SUPER.value) {
						// 遍历超级组件
						List<FormFieldBase> groupFields = group.getFieldList();
						if (groupFields != null) {
							for (FormFieldBase gfield : groupFields) {
								if (gfield instanceof Group) {
									Group tableGroup = (Group) gfield;
									// 表格存在固定行，并且没有填，则自动添加固定行
									if (tableGroup.getGroupType() == GroupType.TABLE.value
											&& tableGroup.getFixedLineList() != null
											&& tableGroup.getFixedLineList().size() > 0
											&& tableGroup.getState() == ContentStateType.UNFILL.value) {
										// 执行固定行加入
										this.addFixedLine(contentId, tableGroup, null);
										// 存储组件状态
										ComponentStateEntity comState = new ComponentStateEntity();
										comState.setId(UUIDUtil.getNextId());
										comState.setDbTable(stateTable);
										comState.setContentId(contentId);
										comState.setTableName(tableGroup.getTable());
										comState.setWorkType(tableGroup.getWorkType());
										comState.setCreatedAt(System.currentTimeMillis());
										comState.setModifiedAt(System.currentTimeMillis());
										comState.setState(ContentStateType.STORAGE.value);
										this.componentStateDao.saveContent(comState, stateTable);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 添加固定行,加入表格固定行
	 *
	 * @param group
	 *            要加的组
	 * @param hasIds
	 *            已经有的固定行
	 * @throws ParseException
	 */
	private void addFixedLine(String contentId, Group group, HashMap<String, Boolean> hasIds) throws ParseException {
		// 处理表格
		if (group.getFixedLineList() != null && group.getFixedLineList().size() > 0) {
			// 拼接handleFieldDTO方法需要的格式
			FormContentBaseDTO<Group> component = new FormContentBaseDTO<Group>();
			component.setDbName(group.getDbName());
			component.setDbTable(group.getTable());
			int workType = group.getWorkType() == null ? 0 : group.getWorkType();
			group.setWorkType(workType);
			component.setWorkType(workType);
			component.setData(group);

			// 拼接handleFieldDTO方法需要的table存储对象
			FormContentBaseDTO<List<FormContentTableEntity>> tableDTO = new FormContentBaseDTO<List<FormContentTableEntity>>();
			List<FormContentTableEntity> entity = new ArrayList<FormContentTableEntity>();
			tableDTO.setData(entity);
			tableDTO.setDbName(component.getDbName());
			tableDTO.setDbTable(component.getDbTable());
			tableDTO.setWorkType(component.getWorkType());
			// map添加
			tableDTO.setContent(new Query());

			int lineNum = 0;
			for (GroupLine line : group.getFixedLineList()) {
				// 确定固定行有内容，并且没有存在数据库
				if (line.getId() != null && line.getFieldList() != null && line.getFieldList().size() > 0
						&& (hasIds == null || !hasIds.containsKey(line.getId()))) {
					lineNum++;
					// 创建存储行
					FormContentTableEntity lineDTO = new FormContentTableEntity();
					String uuid = UUIDUtil.getNextId();
					lineDTO.setId(uuid);
					lineDTO.setContentId(contentId);
					lineDTO.setLineNum(lineNum);
					lineDTO.setFixedId(line.getId());
					lineDTO.setWorkType(component.getWorkType());
					lineDTO.setCreatedAt(System.currentTimeMillis());
					lineDTO.setModifiedAt(System.currentTimeMillis());
					lineDTO.setState(ContentStateType.STORAGE.value);
					Query query = new Query();
					// map添加
					lineDTO.setContent(query);

					tableDTO.getData().add(lineDTO);

					for (WidgetBase widget : line.getFieldList()) {
						if (widget instanceof WidgetBase) {
							if (widget.getHide() != null && !widget.getHide()) {
								this.handleFieldDTO(false, true, contentId, lineDTO, component, (WidgetBase) widget,
										null, null, null, null, null, null, true);
							}
						}
					}
				}
			}
			if (lineNum != 0) {
				// 批量添加表格数据
				tableDao.batchSave(tableDTO.getData(), tableDTO.getDbTable());
			}
		}
	}

	/**
	 * 检查填报是否已经结束
	 *
	 * @param oldData
	 */
	private void ckFillEndDate(SmartFormContent oldData) {
		if (oldData.getEndDate() != null && oldData.getEndDate() > 0
				&& oldData.getEndDate() < System.currentTimeMillis()) {
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String tips = format1.format(new Date(oldData.getEndDate()));
			throw new SmartFormError("填报时间已截止于 " + tips + "，无法再提交");
		}
	}

	/**
	 * 执行表单复杂规则
	 *
	 * @param contentId
	 * @param origForm
	 * @param pageId
	 * @param timeSave
	 *            保存时机，0.非保存，1.保存前，2.保存后
	 */
	private void executeFormRule(String contentId, SmartForm origForm, String pageId, int timeSave) {
		// 判定存在复杂规则
		if (origForm.getConditionRules() != null && origForm.getConditionRules().size() > 0) {
			// 要查询的组件表
			List<FormContentBaseDTO<List<String>>> componentList = new ArrayList<FormContentBaseDTO<List<String>>>();

			this.handleConditionWidget(origForm, componentList, pageId, timeSave);

			// 查询组件表
			if (componentList.size() > 0) {
				for (FormContentBaseDTO<List<String>> componentData : componentList) {
					Map<String, Object> dataMap = componentDao.getContent(componentData.getDbTable(), contentId,
							componentData.getWorkType(), componentData.getData());
					// componentData.setQueryData(dataMap);
					// 如果有查询结果
					if (dataMap != null && dataMap.size() > 0) {
						List<Object> objs = componentData.getContexts();
						// 遍历规则
						for (Object item : objs) {
							if (item instanceof ConditionRuleContext) {
								ConditionRuleContext context = (ConditionRuleContext) item;
								// 是否条件满足
								if (this.ckConditionSuccess(context, dataMap)) {
									// 应用规则
									this.handleConditionSuccess(context.getRule(), origForm, pageId, timeSave);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 处理条件字段，筛选出要查询的条件字段
	 *
	 * @param origForm
	 * @param componentList
	 * @param pageId
	 * @param timeSave
	 *            保存时机，0.非保存，1.保存前，2.保存后
	 */
	private void handleConditionWidget(SmartForm origForm, List<FormContentBaseDTO<List<String>>> componentList,
			String pageId, int timeSave) {
		for (ConditionalRule rule : origForm.getConditionRules()) {
			// 保存分页前，只应用非本分页条件字段的规则
			if (timeSave == 1 && rule.getPageId().equals(pageId)) {
				continue;
			}
			if (origForm.getPageList() != null) {
				for (FormPage page : origForm.getPageList()) {
					// 分页查找
					if (page.getId().equals(rule.getPageId()) && page.getFieldList() != null) {
						for (FormFieldBase group : page.getFieldList()) {
							if (group instanceof Group) {
								Group superGroup = (Group) group;
								// 超级组件查找
								if (superGroup.getId().equals(rule.getGroupId()) && superGroup.getFieldList() != null) {
									for (FormFieldBase gfield : superGroup.getFieldList()) {
										if (gfield instanceof Group) {
											Group tableGroup = (Group) gfield;
											if (tableGroup.getFieldList() != null) {
												// 样式组
												for (FormFieldBase field : tableGroup.getFieldList()) {
													if (field instanceof WidgetBase) {
														if (((WidgetBase) field).getId().equals(rule.getFieldId())) {
															this.setConditionWidgetDto(rule, (WidgetBase) field,
																	superGroup, componentList);
														}
													}
												}
											} else if (tableGroup.getSteerable() != null && tableGroup.getSteerable()
													&& tableGroup.getLineList() != null) {
												// 汇总表格
												for (GroupLine line : tableGroup.getLineList()) {
													// 遍历行字段
													if (line.getFieldList() != null && line.getFieldList().size() > 0
															&& line.getLineNum() == rule.getLineNum()) {
														for (WidgetBase widget : line.getFieldList()) {
															// 处理超级组件旗下字段
															if (widget instanceof WidgetBase && ((WidgetBase) widget)
																	.getId().equals(rule.getFieldId())) {
																this.setConditionWidgetDto(rule, (WidgetBase) widget,
																		tableGroup, componentList);
															}
														}
													}
												}
											}
										} else if (gfield instanceof WidgetBase) {
											WidgetBase field = (WidgetBase) gfield;
											if (field.getId().equals(rule.getFieldId())) {
												this.setConditionWidgetDto(rule, field, superGroup, componentList);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 设置条件字段查询列
	 *
	 * @param widget
	 * @param group
	 * @param componentList
	 */
	private void setConditionWidgetDto(ConditionalRule rule, WidgetBase widget, Group group,
			List<FormContentBaseDTO<List<String>>> componentList) {
		FormContentBaseDTO<List<String>> componentDTO = null;
		for (FormContentBaseDTO<List<String>> item : componentList) {
			if (item.getDbTable().equals(group.getTable()) && item.getWorkType().equals(group.getWorkType())) {
				componentDTO = item;
			}
		}
		// 初始化组件存储对象
		if (componentDTO == null) {
			componentDTO = new FormContentBaseDTO<List<String>>();
			componentDTO.setData(new ArrayList<String>());
			componentDTO.setDbTable(group.getTable());
			componentDTO.setWorkType(group.getWorkType());
			componentDTO.setContexts(new ArrayList<Object>());
			componentList.add(componentDTO);
		}
		// 将规则加入上下文
		ConditionRuleContext context = new ConditionRuleContext();
		context.setRule(rule);
		context.setWidget(widget);
		componentDTO.getContexts().add(context);
		// 添加查询列
		componentDTO.getData().add(widget.getAlias());
	}

	/**
	 * 检查上下文判定的复杂规则是否成立
	 *
	 * @param context
	 * @param dataMap
	 * @param pageId
	 * @return
	 */
	private boolean ckConditionSuccess(ConditionRuleContext context, Map<String, Object> dataMap) {
		WidgetBase widget = context.getWidget();
		Integer dataType = widget.getFieldType();
		// 判定规则条件是否满足
		ConditionalRule rule = context.getRule();

		if (dataType == WidgetType.NUMBER.value) {
			if (widget.getAlias() != null
					|| !widget.getAlias().equals("") && dataMap != null && dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());
				BigDecimal decimal = handleBigDecimal(obj);
				try {
					BigDecimal condition = new BigDecimal(rule.getCondition());
					int result = decimal.compareTo(condition);
					// 数字判定==，!=，<,>,>=,<=
					if (rule.getRelationOperator() == OperatorType.EQUAL.value) {
						return result == 0;
					} else if (rule.getRelationOperator() == OperatorType.NOTEQUAL.value) {
						return result != 0;
					} else if (rule.getRelationOperator() == OperatorType.GREATERTHAN.value) {
						return result == 1;
					} else if (rule.getRelationOperator() == OperatorType.LESSTHAN.value) {
						return result == -1;
					} else if (rule.getRelationOperator() == OperatorType.GREATERTHANANDEQUAL.value) {
						return result == 1 && result == 0;
					} else if (rule.getRelationOperator() == OperatorType.LESSTHANANDEQUAL.value) {
						return result == -1 && result == 0;
					}
				} catch (Exception ex) {
				}
			}
		} else if (dataType == WidgetType.RADIO.value) {
			// 单选
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
					&& dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());
				// 选择只判定==和!=
				if (rule.getRelationOperator() == OperatorType.EQUAL.value) {
					return obj.toString().equals(rule.getCondition());
				} else if (rule.getRelationOperator() == OperatorType.NOTEQUAL.value) {
					return !obj.toString().equals(rule.getCondition());
				}
			}
		} else if (dataType == WidgetType.CHECKBOX.value || dataType == WidgetType.SELECT.value) {
			if (widget.getAlias() != null && !widget.getAlias().equals("") && dataMap != null
					&& dataMap.containsKey(widget.getAlias())) {
				Object obj = dataMap.get(widget.getAlias());
				if (obj.toString() != null && !obj.toString().equals("")) {
					String[] listValue = obj.toString().split(",");
					// 拆分Sql中逗号分割的ID
					// List<String> defs = new ArrayList<String>();
					boolean hasValue = false;
					if (listValue != null && listValue.length > 0) {
						for (String value : listValue) {
							// defs.add(value);
							hasValue = value.toString().equals(rule.getCondition());
						}
					}
					if (rule.getRelationOperator() == OperatorType.EQUAL.value) {
						return hasValue;
					} else if (rule.getRelationOperator() == OperatorType.NOTEQUAL.value) {
						return !hasValue;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 处理条件成功
	 *
	 * @param rule
	 * @param origForm
	 * @param pageId
	 * @param timeSave
	 *            保存时机，0.非保存，1.保存前，2.保存后
	 */
	private void handleConditionSuccess(ConditionalRule rule, SmartForm origForm, String pageId, int timeSave) {
		if (rule.getWidgetList() != null && rule.getWidgetList().size() > 0) {
			for (ConditionalWidget conWidget : rule.getWidgetList()) {
				// 应用widget，保存后刷新分页状态时不对字段进行规则应用
				if (conWidget.getType() == FormFieldType.WIDGET.value) {
					// 先判定是否是当前页
					if (timeSave != 2 && pageId.equals(conWidget.getPageId())) {
						this.useConditionWidget(conWidget, origForm);
					}
				}
				// 应用组，分页设置
				else {
					this.useConditionWidget(conWidget, origForm);
				}
			}
			// 移除不填的分页
			if (origForm.getPageList() != null) {
				for (int i = origForm.getPageList().size() - 1; i > 0; i--) {
					FormPage page = origForm.getPageList().get(i);
					if (page.getHide() != null && page.getHide()) {
						origForm.getPageList().remove(i);
					}
				}
			}
		}
	}

	/**
	 * 应用条件规则
	 *
	 * @param origForm
	 * @param timeSave
	 */
	private void useConditionWidget(ConditionalWidget conWidget, SmartForm origForm) {
		if (origForm.getPageList() != null) {
			for (FormPage page : origForm.getPageList()) {
				// 应用分页
				if (conWidget.getType() == FormFieldType.PAGE.value) {
					// 分页查找
					if (page.getId().equals(conWidget.getPageId())) {
						page.setHide(conWidget.getHide());
						// 禁用旗下超级组件
						if (conWidget.getDisable() != null && conWidget.getDisable()) {
							if (page.getFieldList() != null) {
								for (FormFieldBase group : page.getFieldList()) {
									Group superGroup = (Group) group;
									superGroup.setDisable(true);
								}
							}
						}
						return;
					}
					continue;
				}
				if (page.getHide() != null && page.getHide()) {
					continue;
				}
				if (page.getFieldList() != null) {
					for (FormFieldBase group : page.getFieldList()) {
						if (group instanceof Group) {
							Group superGroup = (Group) group;
							// 应用超级组件设置
							if (conWidget.getType() == FormFieldType.GROUP.value) {
								if (superGroup.getId().equals(conWidget.getGroupId())) {
									superGroup.setHide(conWidget.getHide());
									superGroup.setDisable(conWidget.getDisable());
									return;
								}
							}
							if (superGroup.getHide() != null && superGroup.getHide()) {
								continue;
							}
							if (superGroup.getFieldList() != null) {
								for (FormFieldBase gfield : superGroup.getFieldList()) {
									if (gfield instanceof Group) {
										Group tableGroup = (Group) gfield;
										// 应用组件设置
										if (conWidget.getType() == FormFieldType.GROUP.value) {
											if (tableGroup.getId().equals(conWidget.getGroupId())) {
												tableGroup.setHide(conWidget.getHide());
												tableGroup.setDisable(conWidget.getDisable());
												return;
											}
										}
										// 应用字段设置
										else if (conWidget.getType() == FormFieldType.WIDGET.value) {
											if (tableGroup.getFieldList() != null) {
												// 样式组
												for (FormFieldBase field : tableGroup.getFieldList()) {
													if (field instanceof WidgetBase) {
														if (((WidgetBase) field).getId()
																.equals(conWidget.getField().getId())) {
															this.useConditionWidgetRule(conWidget, (WidgetBase) field);
														}
													}
												}
											} else if (tableGroup.getSteerable() != null && tableGroup.getSteerable()
													&& tableGroup.getLineList() != null) {
												// 汇总表格
												for (GroupLine line : tableGroup.getLineList()) {
													// 遍历行字段
													if (line.getFieldList() != null && line.getFieldList().size() > 0
															&& line.getLineNum() == conWidget.getLineNum()) {
														for (WidgetBase widget : line.getFieldList()) {
															if (widget.getId().equals(conWidget.getField().getId())) {
																this.useConditionWidgetRule(conWidget, widget);
															}
														}
													}
												}
											} else if (tableGroup.getOriginalLine() != null) {
												GroupLine line = tableGroup.getOriginalLine();
												// 普通表格
												if (line.getFieldList() != null && line.getFieldList().size() > 0) {
													for (WidgetBase widget : line.getFieldList()) {
														if (widget.getId().equals(conWidget.getField().getId())) {
															this.useConditionWidgetRule(conWidget, widget);
														}
													}
												}
											}
										}
									} else if (gfield instanceof WidgetBase) {
										WidgetBase field = (WidgetBase) gfield;
										// 应用字段设置
										if (conWidget.getType() == FormFieldType.WIDGET.value) {
											if (field.getId().equals(conWidget.getField().getId())) {
												this.useConditionWidgetRule(conWidget, field);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 应用字段级别的规则
	 *
	 * @param conWidget
	 * @param field
	 */
	private void useConditionWidgetRule(ConditionalWidget conWidget, WidgetBase field) {
		field.setDisable(conWidget.getDisable());
		field.setHide(conWidget.getHide());
		Integer dataType = field.getFieldType();
		// 应用规则
		if (conWidget.getField().getDefValue() != null) {
			if (dataType == WidgetType.NUMBER.value) {
				try {
					BigDecimal defValue = new BigDecimal(conWidget.getField().getDefValue());
					WidgetNumber number = (WidgetNumber) field;
					number.setDefValue(defValue);
				} catch (Exception ex) {
				}
			} else if (dataType == WidgetType.INPUT.value || dataType == WidgetType.TEXTAREA.value) {
				WidgetInput input = (WidgetInput) field;
				input.setDefValue(conWidget.getField().getDefValue());
			}
		}
		// 设置SQL语句规则
		if (conWidget.getField().getSqlStr() != null) {
			if (dataType == WidgetType.NUMBER.value) {
				WidgetNumber number = (WidgetNumber) field;
				number.setUseSql(true);
				number.setSqlStr(conWidget.getField().getSqlStr());
			}
		}
		// 设置字段规则
		if (conWidget.getField().getRules() != null && conWidget.getField().getRules().size() > 0) {
			List<RuleBase> conditionRules = conWidget.getField().getRules();
			// List<RuleBase> rules = field.getRules();
			// 直接启用条件中的规则
			field.setRules(conditionRules);
			// for (RuleBase conRule : conditionRules) {
			// for (int i = 0; i < rules.size(); i ++) {
			// RuleBase rule = rules.get(i);
			//
			// // 非自定义规则
			// if (conRule.getType() != RuleType.CUSTOM.value &&
			// conRule.getType().equals(rule.getType()) {
			// rules.set(i, conRule);
			// }
			// // 自定义规则
			// if(conRule.getType() == RuleType.CUSTOM.value){
			// if(conRule.getId().equals(rule.getId())
			// rules.set(i, conRule);
			// }
			// }
			// }
		}
	}
}
