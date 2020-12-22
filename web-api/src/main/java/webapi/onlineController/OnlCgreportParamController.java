package webapi.onlineController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.api.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartcode.form.entity.OnlCgreportParam;
import smartcode.form.service.OnlCgreportParamService;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/9/17 13:10
 * @Description: smartcode.form.controller
 **/
@RestController("onlCgreportParamController")
@RequestMapping({"/online/cgreport/param"})
public class OnlCgreportParamController {

    private static final Logger logger = LoggerFactory.getLogger(OnlCgreportParamController.class);
    @Autowired
    private OnlCgreportParamService onlCgreportParamService;

    /**
     * 根据headId查询
     * @param headId
     * @return
     */
    @GetMapping({"/listByHeadId"})
    public Result<?> listByHeadId(@RequestParam("headId") String headId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("cgrhead_id", headId);
        queryWrapper.orderByAsc("order_num");
        List list = this.onlCgreportParamService.list(queryWrapper);
        Result result = new Result();
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 新增
     * @param onlCgreportParam
     * @return
     */
    @PostMapping({"/add"})
    public Result<?> add(@RequestBody OnlCgreportParam onlCgreportParam) {
        this.onlCgreportParamService.save(onlCgreportParam);
        return Result.ok("添加成功!");
    }

    /**
     * 修改
     * @param onlCgreportParam
     * @return
     */
    @PutMapping({"/edit"})
    public Result<?> edit(@RequestBody OnlCgreportParam onlCgreportParam) {
        this.onlCgreportParamService.updateById(onlCgreportParam);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping({"/delete"})
    public Result<?> delete(@RequestParam(name = "id",required = true) String id) {
        this.onlCgreportParamService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping({"/deleteBatch"})
    public Result<?> deleteBatch(@RequestParam(name = "ids",required = true) String ids) {
        this.onlCgreportParamService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping({"/queryById"})
    public Result<OnlCgreportParam> queryById(@RequestParam(name = "id",required = true) String id) {
        Result result = new Result();
        OnlCgreportParam onlCgreportParam = (OnlCgreportParam)this.onlCgreportParamService.getById(id);
        result.setResult(onlCgreportParam);
        return result;
    }
}
