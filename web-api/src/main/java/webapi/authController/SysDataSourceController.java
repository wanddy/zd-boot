package webapi.authController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import auth.domain.data.service.ISysDataSourceService;
import auth.entity.DataSource;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.system.base.controller.JeecgController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 多数据源管理
 */
@Slf4j
@Api(tags = "多数据源管理")
@RestController
@RequestMapping("/sys/dataSource")
public class SysDataSourceController extends JeecgController<DataSource, ISysDataSourceService> {

    private final ISysDataSourceService sysDataSourceService;

    @Autowired
    public SysDataSourceController(ISysDataSourceService sysDataSourceService) {
        this.sysDataSourceService = sysDataSourceService;
    }

    /**
     * 分页列表查询
     */
    @AutoLog(value = "多数据源管理-分页列表查询")
    @ApiOperation(value = "多数据源管理-分页列表查询", notes = "多数据源管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(DataSource dataSource,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<DataSource> queryWrapper = QueryGenerator.initQueryWrapper(dataSource, req.getParameterMap());
        Page<DataSource> page = new Page<>(pageNo, pageSize);
        IPage<DataSource> pageList = sysDataSourceService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/options")
    public Result<?> queryOptions(DataSource dataSource, HttpServletRequest req) {
        QueryWrapper<DataSource> queryWrapper = QueryGenerator.initQueryWrapper(dataSource, req.getParameterMap());
        List<DataSource> pageList = sysDataSourceService.list(queryWrapper);
        JSONArray array = new JSONArray(pageList.size());
        for (DataSource item : pageList) {
            JSONObject option = new JSONObject(3);
            option.put("value", item.getCode());
            option.put("label", item.getName());
            option.put("text", item.getName());
            array.add(option);
        }
        return Result.ok(array);
    }

    /**
     * 添加
     */
    @AutoLog(value = "多数据源管理-添加")
    @ApiOperation(value = "多数据源管理-添加", notes = "多数据源管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DataSource dataSource) {
        sysDataSourceService.save(dataSource);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     */
    @AutoLog(value = "多数据源管理-编辑")
    @ApiOperation(value = "多数据源管理-编辑", notes = "多数据源管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DataSource dataSource) {
        DataSource d = sysDataSourceService.getById(dataSource.getId());
        sysDataSourceService.updateById(dataSource);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "多数据源管理-通过id删除")
    @ApiOperation(value = "多数据源管理-通过id删除", notes = "多数据源管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        DataSource dataSource = sysDataSourceService.getById(id);
        sysDataSourceService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "多数据源管理-批量删除")
    @ApiOperation(value = "多数据源管理-批量删除", notes = "多数据源管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(item -> {
            DataSource dataSource = sysDataSourceService.getById(item);
        });
        this.sysDataSourceService.removeByIds(idList);
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "多数据源管理-通过id查询")
    @ApiOperation(value = "多数据源管理-通过id查询", notes = "多数据源管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        DataSource dataSource = sysDataSourceService.getById(id);
        return Result.ok(dataSource);
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DataSource dataSource) {
        return super.exportXls(request, dataSource, DataSource.class, "多数据源管理");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DataSource.class);
    }

}
