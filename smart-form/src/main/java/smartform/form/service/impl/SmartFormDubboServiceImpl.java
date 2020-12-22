package smartform.form.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartform.form.mapper.SmartFormMapper;
import smartform.form.model.*;
import smartform.form.service.FormCategoryService;
import smartform.form.service.SmartFormContentService;
import smartform.form.service.SmartFormDubboService;
import smartform.form.service.SmartFormService;
import smartform.widget.model.WidgetBase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import smartform.form.service.dubbo.SmartFormDubboService;

@Service("SmartFormDubboService")
public class SmartFormDubboServiceImpl implements SmartFormDubboService {

	@Autowired
	FormCategoryService categoryService;

	@Autowired
	SmartFormService formService;

	@Autowired
	SmartFormContentService contentService;

	@Autowired
	private SmartFormMapper smartFormMapper;

	@Override
	public SmartFormPagination smartFormList(Integer nowPage, Integer pageSize, String name, String categoryId,
			Integer state) {
		SmartFormInput page = new SmartFormInput();
		page.setNowPage(nowPage);
		page.setPageSize(pageSize);
		page.setName(name);
		page.setCategoryId(categoryId);
		page.setState(state);
		return formService.smartFormList(page);
	}

	@Override
	public List<FormCategory> formCategoryList(List<String> codes) {
		return categoryService.formCategoryList(codes);
	}

	@Override
	public List<SmartForm> smartFormSimple(List<String> ids) {
		return smartFormMapper.getSimpleInfoByIds(ids);
	}

	@Override
	public SmartFormContent smartFormContentPageState(String formId, String id) throws ParseException {
		return contentService.smartFormContentPageState(formId, id);
	}

	/**
	 * 获取表单结构
	 *
	 * @param id
	 * @param pageId
	 *            按分页查询表单（目前只提出了分页外的选项源）， pageId = null 自动获取第一页结构； pageId = 0
	 *            只获取分页列表，不获取分页内字段结构；
	 * @param hasOptions
	 *            是否获取选项源信息
	 * @return
	 */
	@Override
	public SmartForm smartForm(String id, String pageId, boolean hasOptions) {
		return smartForm(id, pageId, hasOptions, false);
	}

	@Override
	public SmartForm smartForm(String id, String pageId, boolean hasOptions, boolean hasFieldMapperList) {
		//long start = System.currentTimeMillis();
		SmartForm smartform = formService.smartForm(id, pageId, hasOptions);
		//System.out.println("fill load over ----" + (System.currentTimeMillis() - start));
		if (smartform != null) {
			List<FormPage> pageList = smartform.getPageList();
			FormPage nowPage = null;
			// 默认第一页
			if (pageId == null || pageId.equals("")) {
				nowPage = pageList.get(0);
				pageId = nowPage.getId();
			}
			//System.out.println("fill load page ----" + (System.currentTimeMillis() - start));
			// 移除不需要的FieldList
			for (FormPage page : smartform.getPageList()) {
				if (pageId.equals("0") || !page.getId().equals(pageId)) {
					page.setFieldList(null);
				}
			}
			//System.out.println("fill del field ----" + (System.currentTimeMillis() - start));
			// 不获取复杂规则,FieldMapperList
			smartform.setConditionRules(null);
			if(!hasFieldMapperList)
			{
				smartform.setFieldMapperList(null);
			}
		}
		//System.out.println("fill use----" + (System.currentTimeMillis() - start));
		return smartform;
	}


	/**
	 *
  	 *  pageid
   	 *   tablename
     *    alias
   	 *	   ruleList
	 */
	/**获取表单验证规则
	 * @param
	 * @return Map<pageid, List<SuperGroupRule>>
	 */
	@Override
	public Map<String, List<SuperGroupRule>> smartFormRules(String id) {
		SmartForm smartform = formService.smartForm(id, false);
		Map<String, List<SuperGroupRule>> rules = new HashMap<String, List<SuperGroupRule>>();
		//表单
		if (smartform != null) {
			List<FormPage> pageList = smartform.getPageList();
			System.out.println("pageList："+pageList.size());
			//分页列表
			for (FormPage page : pageList) {
				List<SuperGroupRule> superGroupRules = new ArrayList<SuperGroupRule>();
				//组件列表
				for(FormFieldBase mainfiled : page.getFieldList())
				{
					if(mainfiled instanceof Group)
					{
						Group superGroup = (Group) mainfiled;
						// 超级组件查找
						if (superGroup.getFieldList() != null) {
							SuperGroupRule rule = new SuperGroupRule();
							rule.setId(superGroup.getId());
							rule.setTableName(superGroup.getTable());
							Map<String, FieldRules> fieldRuless = new HashMap<String, FieldRules>();
							Map<String, FieldRules> fieldIdRules = new HashMap<String, FieldRules>();
							for (FormFieldBase gfield : superGroup.getFieldList()) {
								if (gfield instanceof Group) {
									Group tableGroup = (Group) gfield;
									if (tableGroup.getFieldList() != null) {
										// 样式组
										for (FormFieldBase field : tableGroup.getFieldList()) {
											if (field instanceof WidgetBase) {
												WidgetBase widget = (WidgetBase)field;
												if(widget.getAlias() == null)
												{
													continue;
												}
												// 添加规则
												FieldRules fieldRules = new FieldRules();
												fieldRules.setId(widget.getId());
												fieldRules.setGroupId(tableGroup.getId());
												fieldRules.setAlias(widget.getAlias());
												fieldRules.setRules(widget.getRules());
												fieldRuless.put(widget.getAlias(), fieldRules);
												String idkey = widget.getId() + "_" + -1;
												fieldIdRules.put(idkey, fieldRules);
											}
										}
									} else if (tableGroup.getSteerable() != null && tableGroup.getSteerable()
											&& tableGroup.getLineList() != null) {
										// 汇总表格
										for (GroupLine line : tableGroup.getLineList()) {
											// 遍历行字段
											if (line.getFieldList() != null && line.getFieldList().size() > 0) {
												for (WidgetBase widget : line.getFieldList()) {
													if(widget.getAlias() == null)
													{
														continue;
													}
													// 添加规则
													FieldRules fieldRules = new FieldRules();
													fieldRules.setId(widget.getId());
													fieldRules.setGroupId(tableGroup.getId());
													fieldRules.setLineNum(line.getLineNum());
													fieldRules.setAlias(widget.getAlias());
													fieldRules.setRules(widget.getRules());
													fieldRuless.put(widget.getAlias(), fieldRules);
													String idkey = widget.getId() + "_" + (line.getLineNum() == null ? -1 : line.getLineNum());
													fieldIdRules.put(idkey, fieldRules);
												}
											}
										}
									}
								} else if (gfield instanceof WidgetBase) {
									WidgetBase widget = (WidgetBase) gfield;
									if(widget.getAlias() == null)
									{
										continue;
									}
									// 添加规则
									FieldRules fieldRules = new FieldRules();
									fieldRules.setId(widget.getId());
									fieldRules.setAlias(widget.getAlias());
									fieldRules.setRules(widget.getRules());
									fieldRuless.put(widget.getAlias(), fieldRules);
									String idkey = widget.getId() + "_" + -1;
									fieldIdRules.put(idkey, fieldRules);
								}
							}
							rule.setFieldRules(fieldRuless);
							rule.setFieldIdRules(fieldIdRules);
							//超级组件规则列表
							rule.setRules(superGroup.getConditionRules());
							rule.setFormulaRules(superGroup.getFormulaRules());
							superGroupRules.add(rule);
						}
					}
				}
				rules.put(page.getId(), superGroupRules);
			}

		}
		return rules;
	}

}
