package smartform.form.service.impl;

import auth.domain.dict.service.ISysDictService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartform.common.error.SmartFormError;
import smartform.common.util.CheckDataUtil;
import smartform.common.util.DBCutConstants;
import smartform.common.util.UUIDUtil;
import smartform.form.mapper.FormCategoryMapper;
import smartform.form.mapper.GroupMapper;
import smartform.form.model.*;
import smartform.form.redis.GroupService;
import smartform.widget.model.WidgeRadio;
import smartform.widget.model.WidgeSelect;
import smartform.widget.model.WidgetBase;

import java.util.*;

/**
 * @author hou
 * @ClassName: GroupServiceImpl
 * @Description: 预设组服务实现
 * @date 2018年9月23日 下午3:40:25
 */
@Service("GroupService")
@DS("smart-form")
public class GroupServiceImpl implements smartform.form.service.GroupService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    GroupService dao;

    @Autowired
    GroupMapper mapper;

    @Autowired
    private FormCategoryMapper formCategoryMapper;

    @Autowired
    private ISysDictService iSysDictService;

    /**
     * fastJson 序列化时写入类型信息
     */
    SerializerFeature[] features = new SerializerFeature[]{SerializerFeature.WriteClassName,
            // SerializerFeature.SkipTransientField,
            // SerializerFeature.DisableCircularReferenceDetect
    };

    @Override
    public Group groupById(String id) {
        Group data = null;
        // 先从redis中获取
        if (dao.hasKey(id)) {
            logger.info("从redis取:" + id);
            data = dao.get(id);
        } else {
            logger.info("从数据库取:" + id);
            data = mapper.selectById(id);
            if (data != null) {
                String json = data.getFieldsJson();
                if (json != null && !json.equals("")) {
                    List<FormFieldBase> list = JSON.parseArray(json, FormFieldBase.class);
                    data.setFieldList(list);
                }
                data.setFieldsJson(null);
                String jsonRules = data.getConditionRulesJson();
                if (jsonRules != null && !jsonRules.equals("")) {
                    List<ConditionalRule> list = JSON.parseArray(jsonRules, ConditionalRule.class);
                    data.setConditionRules(list);
                }
                data.setConditionRulesJson(null);
                data.setType(FormFieldType.GROUP.value);
                // 继续缓存入redis中
                dao.update(data);
            }
        }
        if (data != null) {
            // 使用唯一Set进行选项源ID存储
            Set<String> optionsIDs = new HashSet<String>();
            if (data.getFieldList() != null) {
                for (FormFieldBase field : data.getFieldList()) {
                    if (field instanceof Group) {
                        Group group = (Group) field;
                        this.getGroupSelectOptions(group, optionsIDs);
                    } else {
                        this.getSelectOptions((WidgetBase) field, optionsIDs);
                    }
                }
                // 遍历选项源ID读取选项
                if (optionsIDs.size() > 0) {
                    ArrayList<String> list = new ArrayList<>(optionsIDs);
                    // 赋值选项源
                    try {
                        //切换数据源（选项源以及数据字典在master）
                        DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
                        data.setOptionsList(iSysDictService.getIds(list));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        DynamicDataSourceContextHolder.poll();
                    }
                }
            }
        }
        return data;
    }

    /**
     * 获取组中选择控件的选项源
     *
     * @param group
     */
    private void getGroupSelectOptions(Group group, Set<String> optionsIDs) {
        if (group.getGroupType() == null || group.getGroupType() == GroupType.TABLE.value) {
            if (group.getLineList() != null && group.getLineList().size() > 0) {
                for (GroupLine line : group.getLineList()) {
                    // 遍历组字段
                    if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                        for (WidgetBase widget : line.getFieldList()) {
                            this.getSelectOptions(widget, optionsIDs);
                        }
                    }
                }
            } else if (group.getOriginalLine() != null) {
                GroupLine line = group.getOriginalLine();
                // 遍历组字段
                if (line.getFieldList() != null && line.getFieldList().size() > 0) {
                    for (WidgetBase widget : line.getFieldList()) {
                        this.getSelectOptions(widget, optionsIDs);
                    }
                }
            }
        } else {
            // 遍历超级组件/样式组字段
            if (CheckDataUtil.isNotNull(group.getFieldList())) {
                for (FormFieldBase widget : group.getFieldList()) {
                    if (group.getGroupType() == GroupType.SUPER.value) {
                        if (widget instanceof Group) {
                            Group nowGroup = (Group) widget;
                            this.getGroupSelectOptions(nowGroup, optionsIDs);
                        } else {
                            this.getSelectOptions((WidgetBase) widget, optionsIDs);
                        }
                    } else {
                        this.getSelectOptions((WidgetBase) widget, optionsIDs);
                    }
                }
            }
            if (CheckDataUtil.isNotNull(group.getRowList())) {
                // 遍历栅格组
                for (GroupRow row : group.getRowList()) {
                    if (CheckDataUtil.isNotNull(row.getFieldList())) {
                        for (FormFieldBase widget : row.getFieldList()) {
                            this.getSelectOptions((WidgetBase) widget, optionsIDs);
                        }
                    }

                }
            }

        }
    }

    /**
     * 获取选择控件的选项源
     *
     * @param widget
     */
    private void getSelectOptions(WidgetBase widget, Set<String> optionsIDs) {
        if (widget instanceof WidgeRadio) {
            if(StringUtils.isNotEmpty(((WidgeRadio) widget).getSourceID())){
                optionsIDs.add(((WidgeRadio) widget).getSourceID());
            }
        } else if (widget instanceof WidgeSelect) {
            if(StringUtils.isNotEmpty(((WidgeSelect) widget).getSourceID())){
                optionsIDs.add(((WidgeSelect) widget).getSourceID());
            }
        }
    }

    @Override
    public GroupPagination groupList(GroupInput page, boolean hasFieldList) {
        // 设置分页数和页码
        Page<Group> pageSql = new Page<>(page.getNowPage(), page.getPageSize());
        // 设置查询条件
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        // 状态查询
        if (page.getState().intValue() != StateType.All.value) {
            queryWrapper.eq("state", page.getState());
        }
        // 分类查询，""用于查询无分类表单,all代表查询所有，不筛选分类
        if (page.getCategoryId() != null && !"all".equals(page.getCategoryId())) {
            queryWrapper.eq("category_id", page.getCategoryId());
        }
        // 名称模糊查询
        if (page.getName() != null && page.getName() != "") {
            queryWrapper.like("name", page.getName());
        }
        queryWrapper.orderByDesc("created_at");
        //查询结果排除指定字段
        if (!hasFieldList) {
            queryWrapper.select(Group.class, i -> !i.getColumn().equals("fields_json")
                    && !i.getColumn().equals("condition_rules_json"));
        }
        Page<Group> page1 = mapper.selectPage(pageSql, queryWrapper);
        List<Group> list = page1.getRecords();

        //查询所有组件分类（category_type  1:表单分类 2:组件分类）
        QueryWrapper<FormCategory> categoryTypeQueryWrapper = new QueryWrapper<>();
        categoryTypeQueryWrapper.eq("category_type",2);
        List<FormCategory> formCategoryList = formCategoryMapper.selectList(categoryTypeQueryWrapper);

        //遍历所有组件，分别设置CategoryName
        B:for (Group group : list) {
            String categoryId = group.getCategoryId();
            for (FormCategory formCategory : formCategoryList) {
                //设置完跳过本次循环
                if (StringUtils.isEmpty(categoryId)){
                    group.setCategoryName("未分类");
                    continue B;
                }else if(formCategory.getId().equals(categoryId)){
                    group.setCategoryName(formCategory.getName());
                    continue B;
                }

            }
        }

        GroupPagination pageList = new GroupPagination();

        if (hasFieldList && list != null && list.size() > 0) {
            // 使用唯一Set进行选项源ID存储
            Set<String> optionsIDs = new HashSet<String>();
            // 处理字段详情数据
            for (Group data : list) {
                String json = data.getFieldsJson();
                if (json != null && !json.equals("")) {
                    List<FormFieldBase> fieldList = JSON.parseArray(json, FormFieldBase.class);
                    data.setFieldList(fieldList);
                }
                data.setFieldsJson(null);
                String jsonRules = data.getConditionRulesJson();
                if (jsonRules != null && !jsonRules.equals("")) {
                    List<ConditionalRule> ruleList = JSON.parseArray(jsonRules, ConditionalRule.class);
                    data.setConditionRules(ruleList);
                }
                data.setConditionRulesJson(null);
                data.setType(FormFieldType.GROUP.value);
                // 遍历字段，拉取旗下选项
                if (data.getFieldList() != null) {
                    for (FormFieldBase field : data.getFieldList()) {
                        if (field instanceof Group) {
                            Group group = (Group) field;
                            this.getGroupSelectOptions(group, optionsIDs);
                        } else {
                            this.getSelectOptions((WidgetBase) field, optionsIDs);
                        }
                    }
                }
            }
            // 遍历选项源ID读取选项
            if (optionsIDs.size() > 0) {
                ArrayList<String> optionsIDList = new ArrayList<>(optionsIDs);
                // 赋值选项源
                try {
                    //切换数据源（选项源以及数据字典在master）
                    DynamicDataSourceContextHolder.push(DBCutConstants.MASTER);
                    pageList.setOptionsList(iSysDictService.getIds(optionsIDList));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    DynamicDataSourceContextHolder.poll();
                }
            }
        }
        pageList.setTotal(Math.toIntExact(pageSql.getTotal()));
        pageList.setRows(list);
        return pageList;
    }

    @Override
    @Transactional
    public String createGroup(String group) {
        Group data = JSON.parseObject(group, Group.class);
        if (data != null) {
            return addGroup(data, false);
        }
        return "0";
    }

    /**
     * 添加一个group 到数据库中
     *
     * @param data
     * @return
     */
    private String addGroup(Group data, boolean isCopy) {
        data.setId(UUIDUtil.getNextId());
        // 创建时间
        data.setCreatedAt(new Date());
        data.setModifiedAt(new Date());
        data.setGroupType(GroupType.SUPER.value);
        data.setType(FormFieldType.GROUP.value);
        // 设置状态
        data.setState(StateType.DEV.value);
        // 设置描述
        if (data.getDes() == null) {
            data.setDes("");
        }
        // 设置排序字段
        if (data.getSortName() == null) {
            data.setSortName("");
        }
        if (isCopy) {
            String json = data.getFieldsJson();
            if (json != null && !json.equals("")) {
                List<FormFieldBase> list = JSON.parseArray(json, FormFieldBase.class);
                data.setFieldList(list);
            } else {
                data.setFieldsJson("");
            }
            String jsonRules = data.getConditionRulesJson();
            if (jsonRules != null && !jsonRules.equals("")) {
                List<ConditionalRule> list = JSON.parseArray(jsonRules, ConditionalRule.class);
                data.setConditionRules(list);
            }
        } else {
            String fieldsJson = "";
            if (data.getFieldList() != null)
                // 将字段列表系列化后存储到mysql中
                fieldsJson = JSON.toJSONString(data.getFieldList(), features);
            data.setFieldsJson(fieldsJson);

            String conditionJson = "";
            // 将字段列表系列化后存储到mysql中
            if (data.getConditionRules() != null)
                conditionJson = JSON.toJSONString(data.getConditionRules(), features);
            data.setConditionRulesJson(conditionJson);
        }
        // 先执行SQL，再存Redis，SQL使用事务提交
        mapper.insert(data);
        data.setFieldsJson(null);
        data.setConditionRulesJson(null);
        dao.update(data);
        return data.getId();
    }

    @Override
    @Transactional
    public String updateGroup(String group) {
        Group data = JSON.parseObject(group, Group.class);
        if (data != null) {
            Group oldData = mapper.getSimpleInfoById(data.getId());
            if (oldData != null) {
                if (oldData.getState().intValue() == StateType.RELEASE.value) {
                    throw new SmartFormError("超级组件发布后不可以编辑和删除");
                }
                data.setCreatedAt(oldData.getCreatedAt());
                data.setModifiedAt(new Date());
                data.setGroupType(GroupType.SUPER.value);
                data.setType(FormFieldType.GROUP.value);
                // 设置描述
                if (data.getDes() == null) {
                    data.setDes("");
                }
                // 设置排序字段
                if (data.getSortName() == null) {
                    data.setSortName("");
                }

                String fieldsJson = "";
                if (data.getFieldList() != null)
                    // 将字段列表系列化后存储到mysql中
                    fieldsJson = JSON.toJSONString(data.getFieldList(), features);
                data.setFieldsJson(fieldsJson);

                String conditionJson = "";
                // 将字段列表系列化后存储到mysql中
                if (data.getConditionRules() != null)
                    conditionJson = JSON.toJSONString(data.getConditionRules(), features);
                data.setConditionRulesJson(conditionJson);
                // 先执行SQL，再存Redis，SQL使用事务提交
                mapper.updateById(data);
                data.setFieldsJson(null);
                data.setConditionRulesJson(null);
                dao.update(data);
            } else {
                throw new SmartFormError("编辑的超级组件不存在");
            }
            return "1";
        }
        return "0";
    }

    @Override
    public String updateGroupState(String id, boolean isRelease) {
        Group data = mapper.getSimpleInfoById(id);
        if (data != null) {
            if (isRelease && data.getState() == StateType.DEV.value) {
                // 目前只设置发布状态
                data.setState(StateType.RELEASE.value);
                data.setModifiedAt(new Date());
                // 先执行SQL，再存Redis，SQL使用事务提交
                mapper.updateById(data);
                if (dao.hasKey(id))
                    dao.update(data, "state", "modifiedAt");
                return "1";
            } else if (!isRelease && data.getState() == StateType.RELEASE.value) {
                // 目前只设置发布状态
                data.setState(StateType.DEV.value);
                data.setModifiedAt(new Date());
                // 先执行SQL，再存Redis，SQL使用事务提交
                mapper.updateById(data);
                if (dao.hasKey(id))
                    dao.update(data, "state", "modifiedAt");
                return "1";
            }
        }
        return "0";
    }

    @Override
    public String copyGroup(String id) {
        Group data = mapper.selectById(id);
        if (data != null) {
            return addGroup(data, true);
        }
        return "0";
    }

    @Override
    @Transactional
    public String deleteGroup(String id) {
        Group data = mapper.getSimpleInfoById(id);
        if (data != null) {
            if (data.getState().intValue() == StateType.RELEASE.value) {
                throw new SmartFormError("超级组件发布后不可以编辑和删除");
            }
            // SQL移除
            mapper.batchRemove(new String[]{id});
            dao.delete(id);
            return "1";
        }
        return "1";
    }
}
