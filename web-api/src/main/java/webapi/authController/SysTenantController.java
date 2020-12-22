package webapi.authController;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.PermissionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import auth.domain.tenant.service.ISysTenantService;
import auth.entity.Tenant;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 租户配置信息
 */
@Slf4j
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantController {

    private final ISysTenantService sysTenantService;

    @Autowired
    public SysTenantController(ISysTenantService sysTenantService) {
        this.sysTenantService = sysTenantService;
    }

    /**
     * 获取列表数据
     */
    @PermissionData(pageComponent = "system/TenantList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Tenant>> queryPageList(Tenant tenant, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Tenant>> result = new Result<>();
        QueryWrapper<Tenant> queryWrapper = QueryGenerator.initQueryWrapper(tenant, req.getParameterMap());
        Page<Tenant> page = new Page<>(pageNo, pageSize);
        IPage<Tenant> pageList = sysTenantService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Tenant> add(@RequestBody Tenant tenant) {
        Result<Tenant> result = new Result<>();
        if (sysTenantService.getById(tenant.getId()) != null) {
            return result.error500("该编号已存在!");
        }
        try {
            sysTenantService.save(tenant);
            result.success("添加成功！");
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
    public Result<Tenant> edit(@RequestBody Tenant tenant) {
        Result<Tenant> result = new Result<>();
        Tenant sysTenant = sysTenantService.getById(tenant.getId());
        if (sysTenant == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysTenantService.updateById(tenant);
            if (ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }

    /**
     * 通过id删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysTenantService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<?> result = new Result<>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("未选中租户！");
        } else {
            List<String> ls = Arrays.asList(ids.split(","));
            sysTenantService.removeByIds(ls);
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<Tenant> queryById(@RequestParam(name = "id") String id) {
        Result<Tenant> result = new Result<>();
        Tenant tenant = sysTenantService.getById(id);
        if (tenant == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(tenant);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 查询有效的 租户数据
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public Result<List<Tenant>> queryList(@RequestParam(name = "ids", required = false) String ids) {
        Result<List<Tenant>> result = new Result<>();
        LambdaQueryWrapper<Tenant> query = new LambdaQueryWrapper<>();
        query.eq(Tenant::getStatus, 1);
        if (oConvertUtils.isNotEmpty(ids)) {
            query.in(Tenant::getId, ids.split(","));
        }
        Date now = new Date();
        query.ge(Tenant::getEndDate, now);
        query.le(Tenant::getBeginDate, now);
        List<Tenant> ls = sysTenantService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }
}
