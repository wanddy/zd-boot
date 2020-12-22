package webapi.authController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import auth.domain.dict.service.ISysDictItemService;
import auth.entity.DictItem;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.constant.CacheConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 前端控制器
 */
@RestController
@RequestMapping("/sys/dictItem")
@Slf4j
public class SysDictItemController {

    private final ISysDictItemService sysDictItemService;

    @Autowired
    public SysDictItemController(ISysDictItemService sysDictItemService) {
        this.sysDictItemService = sysDictItemService;
    }

    /**
     * 查询字典数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<DictItem>> queryPageList(DictItem dictItem, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<DictItem>> result = new Result<>();
        QueryWrapper<DictItem> queryWrapper = QueryGenerator.initQueryWrapper(dictItem, req.getParameterMap());
        queryWrapper.orderByAsc("sort_order");
        Page<DictItem> page = new Page<>(pageNo, pageSize);
        IPage<DictItem> pageList = sysDictItemService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<DictItem> add(@RequestBody DictItem dictItem) {
        Result<DictItem> result = new Result<>();
        try {
            sysDictItemService.save(dictItem);
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
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<DictItem> edit(@RequestBody DictItem dictItem) {
        Result<DictItem> result = new Result<>();
        DictItem sysdict = sysDictItemService.getById(dictItem.getId());
        if (sysdict == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDictItemService.updateById(dictItem);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("编辑成功!");
            }
        }
        return result;
    }

    /**
     * 删除字典数据
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<DictItem> delete(@RequestParam(name = "id") String id) {
        Result<DictItem> result = new Result<>();
        DictItem joinSystem = sysDictItemService.getById(id);
        if (joinSystem == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDictItemService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }
        return result;
    }

    /**
     * 批量删除字典数据
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<DictItem> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<DictItem> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysDictItemService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

}
