package smartform.widget.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartform.common.util.UUIDUtil;
import smartform.widget.redis.FormWidgetService;
import smartform.widget.mapper.FormWidgetMapper;
import smartform.widget.model.FormWidgetInput;
import smartform.widget.model.FormWidgetPagination;
import smartform.widget.model.WidgetBase;
import smartform.widget.model.WidgetState;
import smartform.widget.model.deserializer.WidgetBaseDeserializer;

import java.util.Date;
import java.util.List;


/**
 * @ClassName: FormWidgetServiceImpl
 * @Description: 表单字段服务
 * @author hou
 * @date 2018年9月24日 上午11:20:45
 */
@Service("FormWidgetService")
@DS("smart-form")
public class FormWidgetServiceImpl implements smartform.widget.service.FormWidgetService {
	@Autowired
	FormWidgetService dao;

	@Autowired
	FormWidgetMapper mapper;

	@Override
	////@DataSource(justClear = true)
	public WidgetBase formWidget(String id) {
		return dao.get(id);
	}

	@Override
	////@DataSource(justClear = true)
	public FormWidgetPagination formWidgetList(FormWidgetInput page) {
		// 设置分页数和页码
		//Page<WidgetBase> pageSql = new Page<WidgetBase>(page.getNowPage(), page.getPageSize());
		Page<WidgetBase> pageSql = new Page<>(page.getNowPage(),page.getPageSize());
		// 设置查询条件
		QueryWrapper<WidgetBase> queryWrapper = new QueryWrapper<>();
		// 名称模糊查询
		if (page.getName() != null && page.getName() != "") {
			queryWrapper.like("name",page.getName());
		}
		Page<WidgetBase> widgetBasePage = mapper.selectPage(pageSql, queryWrapper);
		List<WidgetBase> list = widgetBasePage.getRecords();
		FormWidgetPagination pageList = new FormWidgetPagination();
		pageList.setTotal(Math.toIntExact(pageSql.getTotal()));
		pageList.setRows(list);
		return pageList;
	}

	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String createFormWidget(String widget) {
		JSONObject object = JSON.parseObject(widget);
		// 使用WidgetBaseDeserializer解析子类
		WidgetBase data = WidgetBaseDeserializer.deserialzeWidgetBase(object);
		if (data != null) {
			data.setId(UUIDUtil.getNextId());
			// 创建时间
			data.setCreatedAt(new Date());
			// 修改时间
			data.setModifiedAt(new Date());
			// 状态
			data.setState(WidgetState.PREFAB.value);
			// 先执行SQL，再存Redis，SQL使用事务提交
			//mapper.save(data);
			mapper.insert(data);
			dao.update(data);
			return data.getId();
		}
		return "0";
	}

	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String updateFormWidget(String widget) {
		JSONObject object = JSON.parseObject(widget);
		// 使用WidgetBaseDeserializer解析子类
		WidgetBase data = WidgetBaseDeserializer.deserialzeWidgetBase(object);
		String id = data.getId();
		if (dao.hasKey(id)) {
			WidgetBase oldData = dao.get(id, "id", "createdAt");
			if (data != null && oldData != null) {
				// 设置上次的创建时间
				data.setCreatedAt(oldData.getCreatedAt());
				// 修改时间
				data.setModifiedAt(new Date());
				// 先执行SQL，再存Redis，SQL使用事务提交
				mapper.updateById(data);
				dao.update(data);
				return "1";
			}
		}
		return "0";
	}

	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String deleteFormWidget(String id) {
		if (dao.hasKey(id)) {
			// SQL移除
			mapper.batchRemove(new String[] { id });
			dao.delete(id);
		}
		return "1";
	}
}
