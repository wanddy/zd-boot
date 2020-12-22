package webapi.authController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import auth.domain.relation.user.agent.service.ISysUserAgentService;
import auth.entity.UserAgent;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户代理人设置
 */
@RestController
@RequestMapping("/sys/sysUserAgent")
@Slf4j
public class SysUserAgentController {

    private final ISysUserAgentService sysUserAgentService;

    @Autowired
    public SysUserAgentController(ISysUserAgentService sysUserAgentService) {
        this.sysUserAgentService = sysUserAgentService;
    }

    /**
     * 分页列表查询
     */
    @GetMapping(value = "/list")
    public Result<IPage<UserAgent>> queryPageList(UserAgent userAgent,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        Result<IPage<UserAgent>> result = new Result<>();
        QueryWrapper<UserAgent> queryWrapper = QueryGenerator.initQueryWrapper(userAgent, req.getParameterMap());
        Page<UserAgent> page = new Page<>(pageNo, pageSize);
        IPage<UserAgent> pageList = sysUserAgentService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     */
    @PostMapping(value = "/add")
    public Result<UserAgent> add(@RequestBody UserAgent userAgent) {
        Result<UserAgent> result = new Result<>();
        try {
            sysUserAgentService.save(userAgent);
            result.success("代理人设置成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     */
    @PutMapping(value = "/edit")
    public Result<UserAgent> edit(@RequestBody UserAgent userAgent) {
        Result<UserAgent> result = new Result<>();
        UserAgent userAgentEntity = sysUserAgentService.getById(userAgent.getId());
        if (userAgentEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysUserAgentService.updateById(userAgent);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("代理人设置成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     */
    @DeleteMapping(value = "/delete")
    public Result<UserAgent> delete(@RequestParam(name = "id") String id) {
        Result<UserAgent> result = new Result<>();
        UserAgent userAgent = sysUserAgentService.getById(id);
        if (userAgent == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysUserAgentService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<UserAgent> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<UserAgent> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysUserAgentService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     */
    @GetMapping(value = "/queryById")
    public Result<UserAgent> queryById(@RequestParam(name = "id") String id) {
        Result<UserAgent> result = new Result<>();
        UserAgent userAgent = sysUserAgentService.getById(id);
        if (userAgent == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(userAgent);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 通过userName查询
     */
    @GetMapping(value = "/queryByUserName")
    public Result<UserAgent> queryByUserName(@RequestParam(name = "userName") String userName) {
        Result<UserAgent> result = new Result<>();
        LambdaQueryWrapper<UserAgent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgent::getUserName, userName);
        UserAgent userAgent = sysUserAgentService.getOne(queryWrapper);
        if (userAgent == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(userAgent);
            result.setSuccess(true);
        }
        return result;
    }


}
