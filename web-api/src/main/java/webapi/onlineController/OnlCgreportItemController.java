package webapi.onlineController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smartcode.form.entity.OnlCgreportItem;
import smartcode.form.service.OnlCgreportItemService;

/**
 * @Author: LiuHongYan
 * @Date: 2020/9/17 13:10
 * @Description: smartcode.form.controller
 **/
@RestController("onlCgreportItemController")
@RequestMapping({"/online/cgreport/item"})
public class OnlCgreportItemController {
    private static final Logger logger = LoggerFactory.getLogger(OnlCgreportItemController.class);
    @Autowired
    private OnlCgreportItemService onlCgreportItemService;

    /**
     * 根据headId查找
     * @param headId
     * @return
     */
    @GetMapping({"/listByHeadId"})
    public Result<?> listByHeadId(@RequestParam("headId") String headId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("cgrhead_id", headId);
        queryWrapper.orderByAsc("order_num");
        List list = this.onlCgreportItemService.list(queryWrapper);
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 列表查询
     * @param onlCgreportItem
     * @param pageNo
     * @param pageSize
     * @param request
     * @return
     */
    @GetMapping({"/list"})
    public Result<IPage<OnlCgreportItem>> list(OnlCgreportItem onlCgreportItem,
                                               @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                               HttpServletRequest request) {
        Result result = new Result();
        QueryWrapper queryWrapper = QueryGenerator.initQueryWrapper(onlCgreportItem, request.getParameterMap());
        Page page = new Page(pageNo, pageSize);
        IPage iPage = this.onlCgreportItemService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(iPage);
        return result;
    }

    /**
     * 新增
     * @param var1
     * @return
     */
    @PostMapping({"/add"})
    public Result<?> add(@RequestBody OnlCgreportItem var1) {
        this.onlCgreportItemService.save(var1);
        return Result.ok("添加成功!");
    }

    /**
     * 修改
     * @param onlCgreportItem
     * @return
     */
    @PutMapping({"/edit"})
    public Result<?> b(@RequestBody OnlCgreportItem onlCgreportItem) {
        this.onlCgreportItemService.updateById(onlCgreportItem);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping({"/delete"})
    public Result<?> delete(@RequestParam(name = "id",required = true) String id) {
        this.onlCgreportItemService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping({"/deleteBatch"})
    public Result<?> deleteBatch(@RequestParam(name = "ids",required = true) String ids) {
        this.onlCgreportItemService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping({"/queryById"})
    public Result<OnlCgreportItem> queryById(@RequestParam(name = "id",required = true) String id) {
        Result result = new Result();
        OnlCgreportItem onlCgreportItem = this.onlCgreportItemService.getById(id);
        result.setResult(onlCgreportItem);
        result.setSuccess(true);
        return result;
    }
}
