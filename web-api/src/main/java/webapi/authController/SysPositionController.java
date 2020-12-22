package webapi.authController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.AutoLog;
import commons.poi.def.NormalExcelConstants;
import commons.poi.excel.entity.ExportParams;
import commons.poi.view.JeecgEntityExcelView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import auth.domain.position.service.ISysPositionService;
import auth.entity.Position;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.constant.CommonConstant;
import commons.util.ImportExcelUtil;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 职务
 */
@Slf4j
@Api(tags = "职务表")
@RestController
@RequestMapping("/sys/position")
public class SysPositionController {

    private final ISysPositionService sysPositionService;

    @Autowired
    public SysPositionController(ISysPositionService sysPositionService) {
        this.sysPositionService = sysPositionService;
    }

    /**
     * 分页列表查询
     */
    @ApiOperation(value = "职务表-分页列表查询", notes = "职务表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Position>> queryPageList(Position position,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        Result<IPage<Position>> result = new Result<>();
        QueryWrapper<Position> queryWrapper = QueryGenerator.initQueryWrapper(position, req.getParameterMap());
        Page<Position> page = new Page<>(pageNo, pageSize);
        IPage<Position> pageList = sysPositionService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     */
    @AutoLog(value = "职务表-添加")
    @ApiOperation(value = "职务表-添加", notes = "职务表-添加")
    @PostMapping(value = "/add")
    public Result<Position> add(@RequestBody Position position) {
        Result<Position> result = new Result<>();
        try {
            sysPositionService.save(position);
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
    @AutoLog(value = "职务表-编辑")
    @ApiOperation(value = "职务表-编辑", notes = "职务表-编辑")
    @PutMapping(value = "/edit")
    public Result<Position> edit(@RequestBody Position position) {
        Result<Position> result = new Result<>();
        Position positionEntity = sysPositionService.getById(position.getId());
        if (positionEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysPositionService.updateById(position);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "职务表-通过id删除")
    @ApiOperation(value = "职务表-通过id删除", notes = "职务表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        try {
            sysPositionService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "职务表-批量删除")
    @ApiOperation(value = "职务表-批量删除", notes = "职务表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Position> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<Position> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysPositionService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "职务表-通过id查询")
    @ApiOperation(value = "职务表-通过id查询", notes = "职务表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Position> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Position> result = new Result<>();
        Position position = sysPositionService.getById(id);
        if (position == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(position);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<Position> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                Position sysPosition = JSON.parseObject(deString, Position.class);
                queryWrapper = QueryGenerator.initQueryWrapper(sysPosition, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Position> pageList = sysPositionService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "职务表列表");
        mv.addObject(NormalExcelConstants.CLASS, Position.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("职务表列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

}
