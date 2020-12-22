package webapi.authController;


import auth.domain.category.service.ISysCategoryService;
import auth.entity.Category;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.constant.FillRuleConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.model.SysDictTree;
import auth.discard.model.TreeSelectModel;
import auth.discard.vo.SysDictPage;
import auth.domain.dict.service.ISysDictItemService;
import auth.domain.dict.service.ISysDictService;
import auth.entity.Dict;
import auth.entity.DictItem;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.DictModel;
import commons.auth.vo.DictQuery;
import commons.auth.vo.LoginUser;
import commons.constant.CacheConstant;
import commons.constant.CommonConstant;
import commons.util.ImportExcelUtil;
import commons.util.SqlInjectionUtil;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 字典
 */
@RestController
@RequestMapping("/sys/dict")
@Slf4j
public class SysDictController {

    private final ISysDictService sysDictService;

    private final ISysDictItemService sysDictItemService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public SysDictController(ISysDictService sysDictService, ISysDictItemService sysDictItemService, RedisTemplate<String, Object> redisTemplate) {
        this.sysDictService = sysDictService;
        this.sysDictItemService = sysDictItemService;
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Dict>> queryPageList(Dict dict, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Dict>> result = new Result<>();
        QueryWrapper<Dict> queryWrapper = QueryGenerator.initQueryWrapper(dict, req.getParameterMap());
        Page<Dict> page = new Page<>(pageNo, pageSize);
        IPage<Dict> pageList = sysDictService.page(page, queryWrapper);
        log.debug("查询当前页：" + pageList.getCurrent());
        log.debug("查询当前页数量：" + pageList.getSize());
        log.debug("查询结果数量：" + pageList.getRecords().size());
        log.debug("数据总数：" + pageList.getTotal());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 获取树形字典数据
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/treeList", method = RequestMethod.GET)
    public Result<List<SysDictTree>> treeList(Dict dict) {
        Result<List<SysDictTree>> result = new Result<>();
        LambdaQueryWrapper<Dict> query = new LambdaQueryWrapper<>();
        // 构造查询条件
        String dictName = dict.getDictName();
        if (oConvertUtils.isNotEmpty(dictName)) {
            query.like(true, Dict::getDictName, dictName);
        }
        query.orderByDesc(true, Dict::getCreateTime);
        List<Dict> list = sysDictService.list(query);
        List<SysDictTree> treeList = new ArrayList<>();
        for (Dict node : list) {
            treeList.add(new SysDictTree(node));
        }
        result.setSuccess(true);
        result.setResult(treeList);
        return result;
    }

    /**
     * 获取字典数据
     *
     * @param dictCode 字典code
     */
    @RequestMapping(value = "/getDictItems/{dictCode}", method = RequestMethod.GET)
    public Result<List<DictModel>> getDictItems(@PathVariable String dictCode) {
        log.info(" dictCode : " + dictCode);
        Result<List<DictModel>> result = new Result<>();
        List<DictModel> ls;
        try {
            if (dictCode.contains(",")) {
                //关联表字典（举例：sys_user,realname,id）
                String[] params = dictCode.split(",");

                if (params.length < 3) {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                //SQL注入校验（只限制非法串改数据库）
                final String[] sqlInjCheck = {params[0], params[1], params[2]};
                SqlInjectionUtil.filterContent(sqlInjCheck);

                if (params.length == 4) {
                    //SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
                    SqlInjectionUtil.specialFilterContent(params[3]);
                    ls = sysDictService.queryTableDictItemsByCodeAndFilter(params[0], params[1], params[2], params[3]);
                } else if (params.length == 3) {
                    ls = sysDictService.queryTableDictItemsByCode(params[0], params[1], params[2]);
                } else {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
            } else {
                //字典表
                ls = sysDictService.queryDictItemsByCode(dictCode);
            }

            result.setSuccess(true);
            result.setResult(ls);
            log.info(result.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

    /**
     * 获取全部字典数据
     */
    @RequestMapping(value = "/queryAllDictItems", method = RequestMethod.GET)
    public Result<?> queryAllDictItems() {
        Map<String, List<DictModel>> res;
        res = sysDictService.queryAllDictItems();
        return Result.ok(res);
    }

    /**
     * 获取字典数据
     */
    @RequestMapping(value = "/getDictText/{dictCode}/{key}", method = RequestMethod.GET)
    public Result<String> getDictText(@PathVariable("dictCode") String dictCode, @PathVariable("key") String key) {
        log.info(" dictCode : " + dictCode);
        Result<String> result = new Result<>();
        String text;
        try {
            text = sysDictService.queryDictTextByKey(dictCode, key);
            result.setSuccess(true);
            result.setResult(text);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }
        return result;
    }

    /**
     * 大数据量的字典表 走异步加载  即前端输入内容过滤数据
     */
    @RequestMapping(value = "/loadDict/{dictCode}", method = RequestMethod.GET)
    public Result<List<DictModel>> loadDict(@PathVariable String dictCode,
                                            @RequestParam(name = "keyword") String keyword) {
        log.info(" 加载字典表数据,加载关键字: " + keyword);
        Result<List<DictModel>> result = new Result<>();
        List<DictModel> ls;
        try {
            if (dictCode.contains(",")) {
                String[] params = dictCode.split(",");
                if (params.length != 3) {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                ls = sysDictService.queryTableDictItems(params[0], params[1], params[2], keyword);
                result.setSuccess(true);
                result.setResult(ls);
                log.info(result.toString());
            } else {
                result.error500("字典Code格式不正确！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

    /**
     * 根据字典code加载字典text 返回
     */
    @RequestMapping(value = "/loadDictItem/{dictCode}", method = RequestMethod.GET)
    public Result<List<String>> loadDictItem(@PathVariable String dictCode,
                                             @RequestParam(name = "key") String keys) {
        Result<List<String>> result = new Result<>();
        try {
            if (dictCode.contains(",")) {
                String[] params = dictCode.split(",");
                if (params.length != 3) {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                List<String> texts = sysDictService.queryTableDictByKeys(params[0], params[1], params[2], keys);

                result.setSuccess(true);
                result.setResult(texts);
                log.info(result.toString());
            } else {
                result.error500("字典Code格式不正确！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

    /**
     * 根据表名——显示字段-存储字段 pid 加载树形数据
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeData(@RequestParam(name = "pid") String pid, @RequestParam(name = "pidField") String pidField,
                                                      @RequestParam(name = "tableName") String tbname,
                                                      @RequestParam(name = "text") String text,
                                                      @RequestParam(name = "code") String code,
                                                      @RequestParam(name = "hasChildField") String hasChildField,
                                                      @RequestParam(name = "condition") String condition) {
        Result<List<TreeSelectModel>> result = new Result<>();
        Map<String, String> query = null;
        if (oConvertUtils.isNotEmpty(condition)) {
            query = JSON.parseObject(condition, Map.class);
        }
        // SQL注入漏洞 sign签名校验(表名,label字段,val字段,条件)
        List<TreeSelectModel> ls = sysDictService.queryTreeList(query, tbname, text, code, pidField, pid, hasChildField);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 【APP接口】根据字典配置查询表字典数据
     */
    @GetMapping("/queryTableData")
    public Result<List<DictModel>> queryTableData(DictQuery query,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<List<DictModel>> res = new Result<>();
        // SQL注入漏洞 sign签名校验
        List<DictModel> ls = this.sysDictService.queryDictTablePageList(query, pageSize, pageNo);
        res.setResult(ls);
        res.setSuccess(true);
        return res;
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Dict> add(@RequestBody Dict dict) {
        Result<Dict> result = new Result<>();
        try {
            dict.setDelFlag(CommonConstant.DEL_FLAG_0);
            sysDictService.save(dict);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<Dict> edit(@RequestBody Dict dict) {
        Result<Dict> result = new Result<>();
        Dict sysdict = sysDictService.getById(dict.getId());
        if (sysdict == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDictService.updateById(dict);
            if (ok) {
                result.success("编辑成功!");
            }
        }
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<Dict> delete(@RequestParam(name = "id") String id) {
        Result<Dict> result = new Result<>();
        boolean ok = sysDictService.removeById(id);
        if (ok) {
            result.success("删除成功!");
        } else {
            result.error500("删除失败!");
        }
        return result;
    }

    /**
     * 批量删除
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<Dict> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<Dict> result = new Result<>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            sysDictService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 刷新缓存
     */
    @RequestMapping(value = "/refleshCache")
    public Result<?> refleshCache() {
        Result<?> result = new Result<Dict>();
        //清空字典缓存
        Set keys = redisTemplate.keys(CacheConstant.SYS_DICT_CACHE + "*");
        Set keys2 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_CACHE + "*");
        Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
        Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
        redisTemplate.delete(keys);
        redisTemplate.delete(keys2);
        redisTemplate.delete(keys3);
        redisTemplate.delete(keys4);
        return result;
    }


    /**
     * 查询被删除的列表
     */
    @RequestMapping(value = "/deleteList", method = RequestMethod.GET)
    public Result<List<Dict>> deleteList() {
        Result<List<Dict>> result = new Result<>();
        List<Dict> list = this.sysDictService.queryDeleteList();
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 物理删除
     */
    @RequestMapping(value = "/deletePhysic/{id}", method = RequestMethod.DELETE)
    public Result<?> deletePhysic(@PathVariable String id) {
        try {
            sysDictService.deleteOneDictPhysically(id);
            return Result.ok("删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败!");
        }
    }

    /**
     * 取回
     */
    @RequestMapping(value = "/back/{id}", method = RequestMethod.PUT)
    public Result<?> back(@PathVariable String id) {
        try {
            sysDictService.updateDictDelFlag(0, id);
            return Result.ok("操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败!");
        }
    }

    /**
     * 智能表单选项源
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getDictList", method = RequestMethod.GET)
    public Result<IPage<Category>> getDictList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return sysDictService.getDictList(pageNo, pageSize);
    }

    /**
     * 智能表单选项源
     * @param category
     * @return
     */
    @RequestMapping(value = "/getCategory", method = RequestMethod.GET)
    public Result<?> getCategory(Category category, HttpServletRequest req) {
        if(category!=null){
            return sysDictService.getCategory(category,req);
        }
        return Result.error("请选择父级选项源！");
    }

}
