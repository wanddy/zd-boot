package auth.domain.dict.service.impl;

import auth.discard.model.TreeSelectModel;
import auth.domain.category.service.ISysCategoryService;
import auth.domain.dict.mapper.SysDictItemMapper;
import auth.domain.dict.mapper.SysDictMapper;
import auth.domain.dict.service.ISysDictService;
import auth.entity.Category;
import auth.entity.Dict;
import auth.entity.DictItem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.api.vo.Result;
import commons.auth.vo.DictModel;
import commons.auth.vo.DictQuery;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.constant.FillRuleConstant;
import commons.util.oConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典 服务实现类
 */
@Slf4j
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, Dict> implements ISysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private SysDictItemMapper sysDictItemMapper;
    @Autowired
    private ISysCategoryService sysCategoryService;

    /**
     * 通过查询指定code 获取字典
     *
     * @param code
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_CACHE, key = "#code")
    public List<DictModel> queryDictItemsByCode(String code) {
        log.info("无缓存dictCache的时候调用这里！");
        return sysDictMapper.queryDictItemsByCode(code);
    }

    @Override
    public Map<String, List<DictModel>> queryAllDictItems() {
        Map<String, List<DictModel>> res = new HashMap<String, List<DictModel>>();
        List<Dict> ls = sysDictMapper.selectList(null);
        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<DictItem>();
        queryWrapper.eq(DictItem::getStatus, 1);
        queryWrapper.orderByAsc(DictItem::getSortOrder);
        List<DictItem> dictItemList = sysDictItemMapper.selectList(queryWrapper);

        for (Dict d : ls) {
            List<DictModel> dictModelList = dictItemList.stream().filter(s -> d.getId().equals(s.getDictId())).map(item -> {
                DictModel dictModel = new DictModel();
                dictModel.setText(item.getItemText());
                dictModel.setValue(item.getItemValue());
                return dictModel;
            }).collect(Collectors.toList());
            res.put(d.getDictCode(), dictModelList);
        }
        log.info("-------登录加载系统字典-----" + res.toString());
        return res;
    }

    /**
     * 通过查询指定code 获取字典值text
     *
     * @param code
     * @param key
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_CACHE, key = "#code+':'+#key")
    public String queryDictTextByKey(String code, String key) {
        log.info("无缓存dictText的时候调用这里！");
        return sysDictMapper.queryDictTextByKey(code, key);
    }

    /**
     * 通过查询指定table的 text code 获取字典
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @return
     */
    @Override
    //@Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        log.info("无缓存dictTableList的时候调用这里！");
        return sysDictMapper.queryTableDictItemsByCode(table, text, code);
    }

    @Override
    public List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql) {
        log.info("无缓存dictTableList的时候调用这里！");
        return sysDictMapper.queryTableDictItemsByCodeAndFilter(table, text, code, filterSql);
    }

    /**
     * 通过查询指定table的 text code 获取字典值text
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
    public String queryTableDictTextByKey(String table, String text, String code, String key) {
        log.info("无缓存dictTable的时候调用这里！");
        return sysDictMapper.queryTableDictTextByKey(table, text, code, key);
    }

    /**
     * 通过查询指定table的 text code 获取字典，包含text和value
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @param keys  (逗号分隔)
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
    public List<String> queryTableDictByKeys(String table, String text, String code, String keys) {
        if (oConvertUtils.isEmpty(keys)) {
            return null;
        }
        String[] keyArray = keys.split(",");
        List<DictModel> dicts = sysDictMapper.queryTableDictByKeys(table, text, code, keyArray);
        List<String> texts = new ArrayList<>(dicts.size());
        // 查询出来的顺序可能是乱的，需要排个序
        for (String key : keyArray) {
            for (DictModel dict : dicts) {
                if (key.equals(dict.getValue())) {
                    texts.add(dict.getText());
                    break;
                }
            }
        }
        return texts;
    }

    /**
     * 根据字典类型id删除关联表中其对应的数据
     */
    @Override
    public boolean deleteByDictId(Dict dict) {
        dict.setDelFlag(CommonConstant.DEL_FLAG_1);
        return this.updateById(dict);
    }

    @Override
    @Transactional
    public Integer saveMain(Dict dict, List<DictItem> dictItemList) {
        int insert = 0;
        try {
            insert = sysDictMapper.insert(dict);
            if (dictItemList != null) {
                for (DictItem entity : dictItemList) {
                    entity.setId(dict.getId());
                    entity.setStatus(1);
                    sysDictItemMapper.insert(entity);
                }
            }
        } catch (Exception e) {
            return insert;
        }
        return insert;
    }

    @Override
    public List<DictModel> queryAllDepartBackDictModel() {
        return baseMapper.queryAllDepartBackDictModel();
    }

    @Override
    public List<DictModel> queryAllUserBackDictModel() {
        return baseMapper.queryAllUserBackDictModel();
    }

    @Override
    public List<DictModel> queryTableDictItems(String table, String text, String code, String keyword) {
        return baseMapper.queryTableDictItems(table, text, code, "%" + keyword + "%");
    }

    @Override
    public List<TreeSelectModel> queryTreeList(Map<String, String> query, String table, String text, String code, String pidField, String pid, String hasChildField) {
        return baseMapper.queryTreeList(query, table, text, code, pidField, pid, hasChildField);
    }

    @Override
    public void deleteOneDictPhysically(String id) {
        this.baseMapper.deleteOneById(id);
        this.sysDictItemMapper.delete(new LambdaQueryWrapper<DictItem>().eq(DictItem::getId, id));
    }

    @Override
    public void updateDictDelFlag(int delFlag, String id) {
        baseMapper.updateDictDelFlag(delFlag, id);
    }

    @Override
    public List<Dict> queryDeleteList() {
        return baseMapper.queryDeleteList();
    }

    @Override
    public List<DictModel> queryDictTablePageList(DictQuery query, int pageSize, int pageNo) {
        Page page = new Page(pageNo, pageSize, false);
        Page<DictModel> pageList = baseMapper.queryDictTablePageList(page, query);
        return pageList.getRecords();
    }

    @Override
    public Result<IPage<Category>> getDictList(Integer pageNo, Integer pageSize) {
        Result<IPage<Category>> result = new Result<>();
        Page<Category> page = new Page<>(pageNo, pageSize);
        IPage<Category> pageList = baseMapper.getDictList(page);
        if(pageList.getRecords()!=null && pageList.getRecords().size()>0){
            pageList.getRecords().forEach(category -> {
                category.setId(category.getId()+"_"+category.getType());
                category.setDictionary_name(category.getName());
                category.setDictionary_code(category.getCode());
            });
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @Override
    public Result<?> getCategory(Category category, HttpServletRequest req) {
        String[] split = category.getId().split("_");
        category.setId(split[0]);
        category.setType(Integer.parseInt(split[1]));
        Result result = new Result<>();
        result.setSuccess(true);
        //根据type判断属于分类字典或数据字典，hasChild判断是否有下级
        if (category.getType().equals(FillRuleConstant.CATEGORY_TYPE) && category.getHasChild().equals(FillRuleConstant.CATEGORY_TYPE.toString())) {
            List<Category> categoryList = sysCategoryService.getByPId(category.getId());
            if(categoryList.size()>0){
                categoryList.forEach(category1 -> {
                    category1.setDictionary_name(category1.getName());
                    category1.setDictionary_code(category1.getCode());
                    category1.setLabel(category1.getName());
                    category1.setValue(category1.getId());
                    category1.setType(FillRuleConstant.CATEGORY_TYPE);
                    List<Category> list = getId(category1);
                    category1.setCategoryList(list);
                });
            }
            result.setResult(categoryList);
            return result;
        }
        if (category.getType().equals(FillRuleConstant.DICT_TYPE)) {
            List<Category> dictItemList = sysDictItemMapper.getPid(category.getId());
            if(dictItemList!=null && dictItemList.size()>0){
                dictItemList.forEach(category1->{
                    category1.setDictionary_name(category1.getName());
                    category1.setDictionary_code(category1.getCode());
                    category1.setLabel(category1.getName());
                    category1.setValue(category1.getId());
                });
            }
            result.setResult(dictItemList);
            return result;
        }
        return Result.error("没有查询到信息！");
    }

    @Override
    public List<Category> getIds(List<String> ids) {
        List<Category> list = new ArrayList<>();
        ids.forEach(id->{
            Category category = new Category();
            String[] split = id.split("_");
            if(Integer.parseInt(split[1]) == FillRuleConstant.DICT_TYPE){
                Dict dict = super.getById(split[0]);
                category.setId(dict.getId()+"_"+FillRuleConstant.DICT_TYPE);
                category.setDictionary_name(dict.getDictName());
                category.setDictionary_code(dict.getDictCode());
                category.setType(FillRuleConstant.DICT_TYPE);
                category.setHasChild(FillRuleConstant.CATEGORY_TYPE.toString());
            }
            if(Integer.parseInt(split[1]) == FillRuleConstant.CATEGORY_TYPE){
                 category = sysCategoryService.getById(split[0]);
                 category.setDictionary_name(category.getName());
                 category.setDictionary_code(category.getCode());
                 category.setId(category.getId()+"_"+FillRuleConstant.CATEGORY_TYPE);
            }
            list.add(category);
        });
        return list;
    }

    /**
     * 递归查询子级
     * @param category
     * @return
     */
    private List<Category> getId(Category category) {
        List<Category> categoryList = sysCategoryService.getByPId(category.getId());
        if(categoryList!=null && categoryList.size()>0){
            categoryList.forEach(category1 -> {
                category1.setDictionary_name(category1.getName());
                category1.setDictionary_code(category1.getCode());
                if (category1.getHasChild()!=null && category1.getHasChild().equals(FillRuleConstant.CATEGORY_TYPE.toString())) {
                    List<Category> lists = getId(category1);
                    category1.setCategoryList(lists);
                }
            });
        }
        return categoryList;
    }
}
