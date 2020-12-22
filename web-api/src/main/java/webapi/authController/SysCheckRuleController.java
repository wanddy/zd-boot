package webapi.authController;

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
import auth.domain.check.service.ISysCheckRuleService;
import auth.entity.CheckRule;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.system.base.controller.JeecgController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * 编码校验规则
 */
@Slf4j
@Api(tags = "编码校验规则")
@RestController
@RequestMapping("/sys/checkRule")
public class SysCheckRuleController extends JeecgController<CheckRule, ISysCheckRuleService> {

    private final ISysCheckRuleService sysCheckRuleService;

    @Autowired
    public SysCheckRuleController(ISysCheckRuleService sysCheckRuleService) {
        this.sysCheckRuleService = sysCheckRuleService;
    }

    /**
     * 分页列表查询
     */
    @AutoLog(value = "编码校验规则-分页列表查询")
    @ApiOperation(value = "编码校验规则-分页列表查询", notes = "编码校验规则-分页列表查询")
    @GetMapping(value = "/list")
    public Result queryPageList(CheckRule checkRule,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest request) {
        QueryWrapper<CheckRule> queryWrapper = QueryGenerator.initQueryWrapper(checkRule, request.getParameterMap());
        Page<CheckRule> page = new Page<>(pageNo, pageSize);
        IPage<CheckRule> pageList = sysCheckRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 通过id查询
     */
    @AutoLog(value = "编码校验规则-通过Code校验传入的值")
    @ApiOperation(value = "编码校验规则-通过Code校验传入的值", notes = "编码校验规则-通过Code校验传入的值")
    @GetMapping(value = "/checkByCode")
    public Result checkByCode(@RequestParam(name = "ruleCode") String ruleCode,
                              @RequestParam(name = "value") String value) throws UnsupportedEncodingException {
        CheckRule checkRule = sysCheckRuleService.getByCode(ruleCode);
        if (checkRule == null) {
            return Result.error("该编码不存在");
        }
        JSONObject errorResult = sysCheckRuleService.checkValue(checkRule, URLDecoder.decode(value, "UTF-8"));
        if (errorResult == null) {
            return Result.ok();
        } else {
            Result<Object> r = Result.error(errorResult.getString("message"));
            r.setResult(errorResult);
            return r;
        }
    }

    /**
     * 添加
     */
    @AutoLog(value = "编码校验规则-添加")
    @ApiOperation(value = "编码校验规则-添加", notes = "编码校验规则-添加")
    @PostMapping(value = "/add")
    public Result add(@RequestBody CheckRule checkRule) {
        sysCheckRuleService.save(checkRule);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     */
    @AutoLog(value = "编码校验规则-编辑")
    @ApiOperation(value = "编码校验规则-编辑", notes = "编码校验规则-编辑")
    @PutMapping(value = "/edit")
    public Result edit(@RequestBody CheckRule checkRule) {
        sysCheckRuleService.updateById(checkRule);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "编码校验规则-通过id删除")
    @ApiOperation(value = "编码校验规则-通过id删除", notes = "编码校验规则-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestParam(name = "id") String id) {
        sysCheckRuleService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "编码校验规则-批量删除")
    @ApiOperation(value = "编码校验规则-批量删除", notes = "编码校验规则-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result deleteBatch(@RequestParam(name = "ids") String ids) {
        this.sysCheckRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "编码校验规则-通过id查询")
    @ApiOperation(value = "编码校验规则-通过id查询", notes = "编码校验规则-通过id查询")
    @GetMapping(value = "/queryById")
    public Result queryById(@RequestParam(name = "id") String id) {
        CheckRule checkRule = sysCheckRuleService.getById(id);
        return Result.ok(checkRule);
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CheckRule checkRule) {
        return super.exportXls(request, checkRule, CheckRule.class, "编码校验规则");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CheckRule.class);
    }

}
