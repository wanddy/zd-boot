package smartform.widget.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartform.common.error.SmartFormError;
import smartform.common.util.CheckDataUtil;
import smartform.common.util.UUIDUtil;
import smartform.form.model.StateType;
import smartform.widget.mapper.OptionSourceMapper;
import smartform.widget.model.Option;
import smartform.widget.model.OptionSource;
import smartform.widget.model.OptionSourceInput;
import smartform.widget.model.OptionSourcePagination;
import smartform.widget.service.OptionSourceService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: OptionSourceServiceImpl
 * @Description: 选项源服务实现
 * @author hou
 * @date 2018年9月24日 下午3:50:57
 *
 */

/**
 * @author 13449
 *
 */
@Service("OptionSourceService")
@DS("smart-form")
public class OptionSourceServiceImpl implements OptionSourceService {
	// 实例化
	private static Logger LOG = LoggerFactory.getLogger(OptionSourceServiceImpl.class);

	// @Autowired
	// private OptionSourceService mapper;

	// @Autowired
	// private OptionMapper optionDao;

	@Autowired
	private OptionSourceMapper optionSourceMapper;

	/**
	 * 选项列表操作类
	 */
	// @Autowired
	// private OptionListService optionListDao;

	@Override
	////@DataSource(justClear = true)
	public OptionSource optionSourceById(String id) {
		OptionSource data = optionSourceMapper.getObjectById(id);
		return data;
	}

	@Override
	////@DataSource(justClear = true)
	public OptionSourcePagination optionSourceList(OptionSourceInput page) {
		// 设置分页数和页码
		Page<OptionSource> pageSql = new Page<>(page.getNowPage(), page.getPageSize());
		// 设置查询条件
		QueryWrapper<OptionSource> queryWrapper = new QueryWrapper<>();
		// 状态查询
		if (page.getState() != null && page.getState().intValue() != StateType.All.value) {
			queryWrapper.eq("state", page.getState());
		}
		// 名称模糊查询
		if (page.getName() != null && page.getName() != "") {
			queryWrapper.like("name", page.getName());
		}
		queryWrapper.orderByDesc("created_at");

		Page<OptionSource> widgetBasePage = optionSourceMapper.selectPage(pageSql, queryWrapper);
		List<OptionSource> list = widgetBasePage.getRecords();

		OptionSourcePagination pageList = new OptionSourcePagination();
		pageList.setTotal(Math.toIntExact(pageSql.getTotal()));
		pageList.setRows(list);
		return pageList;
	}

	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String createOptionSource(String source) {
		// 解析json
		OptionSource data = JSON.parseObject(source, OptionSource.class);
		if (data != null) {
			data.setId(UUIDUtil.getNextId());
			// String json = data.getOptionsJson();
			// if (json.isEmpty()) {
			// throw new SmartFormError("没有提交选项");
			// } else {
			// // 解析选项JSON
			// List<Option> options = JSON.parseArray(json, Option.class);
			// // 子选项处理
			// int depth = this.setChildOption(options, true);
			// // 子选项存在层级，设为非单级源
			// if (depth > 1)
			// data.setSingle(false);
			// else
			// data.setSingle(true);
			// data.setOptions(options);
			// }
			// 设置添加编辑时间
			data.setCreatedAt(new Date());
			data.setModifiedAt(new Date());
			data.setSort(0);
			// 设置新建状态
			data.setState(StateType.DEV.value);
			// SQL新增选项源
			//optionSourceMapper.save(data);
			optionSourceMapper.insert(data);
			return data.getId();
		}
		return "0";
	}

	@Override
	////@DataSource(justClear = true)
	public List<OptionSource> list(String[] id){
		return optionSourceMapper.list(id);
	}

	/**
	 * 检查列表中的每一个Value值是否是唯一的
	 *
	 * @param options
	 * @return
	 */
	@Deprecated
	private boolean checkValueIsUnique(List<Option> options) {
		Option[] optionArray = new Option[options.size()];
		options.toArray(optionArray);
		for (int i = 0; i < optionArray.length; i++) {
			Option cur = optionArray[i];
			for (int k = i + 1; k < optionArray.length; k++) {
				Option next = optionArray[k];
				if (cur.getValue() == next.getValue()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 检查字符串列表的唯一性
	 *
	 * @param list
	 * @param Value
	 * @return
	 */
	@Deprecated
	private boolean checkValueIsUnique(List<String> list, String Value) {
		boolean isFind = false;
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String cur = list.get(i);
				if (cur == Value) {
					isFind = true;
					break;
				}
			}
		}
		if (isFind)
			return false;
		else
			list.add(Value);
		return true;
	}

	/**
	 * 设置选项基本属性
	 *
	 * @param options
	 * @param hasUpdateId
	 * @return
	 */
	@Deprecated
	private int setChildOption(List<Option> options, boolean hasUpdateId) {
		// int[] depths = new int[options.size()];
		return setChildOption(options, 0, new ArrayList(), hasUpdateId, false, 0);
		// int maxDepth = 0;
		// for (int depth : depths) {
		// maxDepth = maxDepth > depth ? maxDepth : depth;
		// }
		// return maxDepth;
	}

	/**
	 * 设置options的ID和验证label，value长度
	 *
	 * @param options
	 */
	@Deprecated
	private int setChildOption(List<Option> options, int depths, List<String> list, boolean hasUpdateId,
			boolean hasChild, int pIndex) {
		// System.out.println("-------------------------" + depths);
		if (depths > 3)
			throw new SmartFormError("多级选项源不可超过4级");
		++depths;
		int d = depths;
		for (Option op : options) {
			CheckDataUtil.checkStringLenght(op.getValue(), 10, "选项value", false);
			CheckDataUtil.checkStringLenght(op.getLabel(), 30, "选项label", false);
			if (!checkValueIsUnique(list, op.getValue()))
				throw new SmartFormError("有重复的Value出现");
			// 如果存在UUID则不再赋值
			if (hasUpdateId || (op.getId() == null || op.getId().length() != 32))
				op.setId(UUIDUtil.getNextId());
			// 递归验证子选项
			if (op.getChildren() != null && op.getChildren().size() > 0) {

				d = setChildOption(op.getChildren(), depths, list, hasUpdateId, true, pIndex);
				// System.out.println("++++++++++++++++++" + d);
			}
		}
		// System.out.println("***************************" + d);
		return d;
	}

	@Override
	////@DataSource(justClear = true)
	@Transactional
	public String updateOptionSource(String source) {
		OptionSource data = JSON.parseObject(source, OptionSource.class);
		if (data != null) {
			// 获取添加时间并判定该ID的数据是否存在
			OptionSource dataOld = optionSourceMapper.getObjectById(data.getId());
			if (dataOld == null)
				throw new SmartFormError("要编辑的选项源已不存在");
			else if (data.getState().intValue() == StateType.RELEASE.value) {
				throw new SmartFormError("该选项已经发布");
			}
			// 不改变添加时间
			data.setCreatedAt(dataOld.getCreatedAt());
			// 修改编辑时间
			data.setModifiedAt(new Date());
			// SQL更新选项源
			optionSourceMapper.updateById(data);
			return "1";
			// String json = data.getOptionsJson();
			// if (json.isEmpty()) {
			// throw new SmartFormError("没有提交选项");
			// } else {
			// List<Option> options = JSON.parseArray(json, Option.class);
			// // 子选项处理
			// Integer depth = this.setChildOption(options, true);
			//
			// // 子选项存在层级，设为非单级源
			// if (depth > 1)
			// data.setSingle(false);
			// else
			// data.setSingle(true);
			// data.setOptions(options);
			// // 不改变添加时间
			// data.setCreatedAt(dataOld.getCreatedAt());
			// // 修改编辑时间
			// data.setModifiedAt(new Date());
			// // SQL更新选项源
			// optionSourceMapper.update(data);
			// // redis更新选项源
			// mapper.update(data);
			// return "1";
			// }
		}
		return "0";
	}

	@SuppressWarnings("unused")
	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String updateOptionSourceState(String id, Integer state) {
		// 获取state判定可发布
		OptionSource data = optionSourceMapper.selectById(id);
		if (data.getState().intValue() == StateType.DEV.value) {
			// 目前只设置发布状态
			data.setState(StateType.RELEASE.value);
			data.setModifiedAt(new Date());
			if (data != null) {
				// SQL更新选项源
				optionSourceMapper.updateById(data);

				// // 为options设置SourceId和ParentId
				// this.setOptionSqlData(data.getOptions(), data.getId(), null);
				//
				// // copy 选项源下的所有选项，拿到同一级中
				// List<Option> copyOptionList =
				// this.getOptionChildList(data.getOptions());
				//
				// // 发布选项源时，将所有选项发布到选项表中
				// optionDao.batchSave(copyOptionList);
				//
				// // LOG.debug("updateOptionSourceState size=" +
				// // copyOptionList.size());
				// // 发布选项源时，将所有选项发布到redis中
				// optionListDao.updateList(copyOptionList);
				//
				// // redis更新选项源state和编辑时间
				// mapper.update(data, "state", "modifiedAt");
				return "1";
			}
		} else
			throw new SmartFormError("该选项源已经发布");
		return "0";
	}

	/**
	 * 获取选项源的所有子选项列表
	 *
	 * @param options
	 * @return
	 */
	@Deprecated
	private List<Option> getOptionChildList(List<Option> options) {
		// copy 选项源下的所有选项
		List<Option> copyOptionList = new ArrayList();
		getOptionChildList(options, copyOptionList);
		return copyOptionList;
	}

	/**
	 * 负责把选项源中的所有子选项copy到一个列表中
	 *
	 * @param options
	 * @param copyList
	 */
	@Deprecated
	private void getOptionChildList(List<Option> options, List<Option> copyList) {
		// 把选项源的子集和父级的设置成为同一级别
		for (Option op : options) {
			// 把每一个选项都添加进一个列表中
			copyList.add(op);
			if (op.getChildren() != null && op.getChildren().size() > 0) {
				getOptionChildList(op.getChildren(), copyList);
			}
		}
	}

	/**
	 * 为options设置SourceId和ParentId
	 *
	 * @param options
	 * @param sourceId
	 * @param parentId
	 */
	@Deprecated
	private void setOptionSqlData(List<Option> options, String sourceId, String parentId) {
		for (Option op : options) {
			op.setSourceId(sourceId);
			op.setParentId(parentId);
			op.setCreatedAt(new Date());
			op.setModifiedAt(new Date());
			// 递归子选项
			if (op.getChildren() != null && op.getChildren().size() > 0) {
				setOptionSqlData(op.getChildren(), sourceId, op.getId());
			}
		}
	}

	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String copyOptionSource(String id) {
		OptionSource data = optionSourceMapper.getObjectById(id);
		if (data != null) {
			// 设置新ID
			data.setId(UUIDUtil.getNextId());
			// 设置添加编辑时间
			data.setCreatedAt(new Date());
			data.setModifiedAt(new Date());
			// 设置新建状态
			data.setState(StateType.DEV.value);
			// // 重新赋值子选项id
			// List<Option> options = data.getOptions();
			// if (options != null)
			// // 重新设置子选项的ID
			// setChildOption(options, true);
			// SQL新增选项源
			//optionSourceMapper.save(data);
			optionSourceMapper.insert(data);
			// redis新增选项源
			// mapper.update(data);
			return data.getId();
		} else
			throw new SmartFormError("要复制的选项源已不存在");
	}

	@Override
	@Transactional
	////@DataSource(justClear = true)
	public String deleteOptionSource(String id) {
		OptionSource data = optionSourceMapper.getObjectById(id);
		if (data != null) {
			if (data.getState().intValue() == StateType.RELEASE.value) {
				throw new SmartFormError("选项源发布后不可以编辑删除");
			}
			// SQL移除选项源
			optionSourceMapper.batchRemove(new String[] { id });
			// // Redis移除选项源
			// mapper.delete(id);
		}
		return "1";
	}
}
