package smartform.form.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartform.common.error.SmartFormError;
import smartform.common.util.UUIDUtil;
import smartform.form.mapper.FormCategoryMapper;
import smartform.form.mapper.GroupMapper;
import smartform.form.mapper.SmartFormMapper;
import smartform.form.model.CategoryType;
import smartform.form.model.FormCategory;
import smartform.form.model.Group;
import smartform.form.model.SmartForm;
import smartform.form.service.FormCategoryService;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: FormCategoryServiceImpl
 * @Description: 表单分类服务实现
 * @author hou
 * @date 2018年9月23日 下午2:50:40
 */
@Service("FormCategoryService")
@DS("smart-form")
public class FormCategoryServiceImpl implements FormCategoryService {

	// @Autowired
	// FormCategoryService mapper;

	@Autowired
	FormCategoryMapper mapper;

	@Autowired
	private SmartFormMapper smartFormMapper;
	@Autowired
	private GroupMapper groupMapper;

	@Override
	public List<FormCategory> formCategoryList(Integer type) {
		//设置分页数和页码,默认读取100个
		Page<FormCategory> page = new Page<>(1,200);

		//设置查询条件
		QueryWrapper<FormCategory> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("category_type",type);

		//按创建时间升序
		queryWrapper.orderByAsc("sort","created_at");
		Page<FormCategory> selectPage = mapper.selectPage(page, queryWrapper);
		List<FormCategory> categoryList = selectPage.getRecords();
		return categoryList;
	}

	// 实例化
	// private static Logger LOG =
	// LoggerFactory.getLogger(FormCategoryServiceImpl.class);

	@Override
	public List<FormCategory> formCategoryList(List<String> codes) {
		if (codes == null || codes.size() == 0)
			return formCategoryList(CategoryType.SMARTFORM.value);
		List<FormCategory> list = mapper.categoryList(codes);
		return list;
	}

	@Override
	@Transactional
	public String createFormCategory(String category) {
		FormCategory data = JSON.parseObject(category, FormCategory.class);
		if (data != null) {
			data.setId(UUIDUtil.getNextId());
			// 设置添加编辑时间
			data.setCreatedAt(new Date());
			data.setModifiedAt(new Date());
			// SQL新增选项源
			mapper.insert(data);
			// mapper.update(data);
			return data.getId();
		}
		return "0";
	}

	@Override
	@Transactional
	public String updateFormCategory(String category) {
		FormCategory data = JSON.parseObject(category, FormCategory.class);
		if (data != null) {
			// 获取添加时间并判定该ID的数据是否存在
			//FormCategory dataOld = mapper.getObjectById(data.getId());
			QueryWrapper<FormCategory> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("id",data.getId());
			FormCategory dataOld = mapper.selectOne(queryWrapper);

			if (dataOld == null)
				throw new SmartFormError("要编辑的分类已不存在");
			// 不改变添加时间
			data.setCreatedAt(dataOld.getCreatedAt());
			// 修改编辑时间
			data.setModifiedAt(new Date());
			// SQL更新选项源
			mapper.updateById(data);
			// mapper.update(data);
			return "1";
		}
		return "0";
	}

	@Override
	@Transactional
	public String deleteFormCategory(String id) {
		QueryWrapper<FormCategory> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",id);
		FormCategory data = mapper.selectOne(queryWrapper);

		// 设置查询条件
		QueryWrapper<Group> queryWrapper1 = new QueryWrapper<>();
		QueryWrapper<SmartForm> queryWrapper2 = new QueryWrapper<>();
		if(id!=null){
			queryWrapper1.eq("category_Id",id);
			queryWrapper2.eq("category_Id",id);
		}

		int count = 0;
		if (data.getCategoryType() == CategoryType.GROUP.value)
			count = groupMapper.selectCount(queryWrapper1);
		else
			count = smartFormMapper.selectCount(queryWrapper2);
		if (count > 0) {
			if (data.getCategoryType() == CategoryType.GROUP.value)
				throw new SmartFormError("分类旗下存在定制组件，无法删除");
			else
				throw new SmartFormError("分类旗下存在表单，无法删除");
		}
		// SQL移除选项源
		mapper.batchRemove(new String[] { id });
		return "1";
	}

}
