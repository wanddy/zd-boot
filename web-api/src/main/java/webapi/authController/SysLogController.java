package webapi.authController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import auth.domain.log.service.ISysLogService;
import auth.entity.Log;
import auth.entity.Role;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 系统日志
 */
@RestController
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController {

    private final ISysLogService sysLogService;

    @Autowired
    public SysLogController(ISysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    /**
     * 查询日志记录
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Log>> queryPageList(Log syslog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Log>> result = new Result<>();
        QueryWrapper<Log> queryWrapper = QueryGenerator.initQueryWrapper(syslog, req.getParameterMap());
        Page<Log> page = new Page<>(pageNo, pageSize);
        //日志关键词
        String keyWord = req.getParameter("keyWord");
        if (oConvertUtils.isNotEmpty(keyWord)) {
            queryWrapper.like("log_content", keyWord);
        }
        //TODO 过滤逻辑处理
        //TODO begin、end逻辑处理
        //TODO 一个强大的功能，前端传一个字段字符串，后台只返回这些字符串对应的字段
        //创建时间/创建人的赋值
        IPage<Log> pageList = sysLogService.page(page, queryWrapper);
        log.info("查询当前页：" + pageList.getCurrent());
        log.info("查询当前页数量：" + pageList.getSize());
        log.info("查询结果数量：" + pageList.getRecords().size());
        log.info("数据总数：" + pageList.getTotal());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 删除单个日志记录
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<Log> delete(@RequestParam(name = "id") String id) {
        Result<Log> result = new Result<>();
        Log log = sysLogService.getById(id);
        if (log == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysLogService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }
        return result;
    }

    /**
     * 批量，全部清空日志记录
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<Role> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<Role> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            if ("allclear".equals(ids)) {
                this.sysLogService.removeAll();
                result.success("清除成功!");
            }
            this.sysLogService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }


}
