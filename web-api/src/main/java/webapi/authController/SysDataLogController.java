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
import auth.domain.data.service.ISysDataLogService;
import auth.entity.DataLog;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sys/dataLog")
@Slf4j
public class SysDataLogController {

    private final ISysDataLogService service;

    @Autowired
    public SysDataLogController(ISysDataLogService service) {
        this.service = service;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<DataLog>> queryPageList(DataLog dataLog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<DataLog>> result = new Result<>();
        QueryWrapper<DataLog> queryWrapper = QueryGenerator.initQueryWrapper(dataLog, req.getParameterMap());
        Page<DataLog> page = new Page<>(pageNo, pageSize);
        IPage<DataLog> pageList = service.page(page, queryWrapper);
        log.info("查询当前页：" + pageList.getCurrent());
        log.info("查询当前页数量：" + pageList.getSize());
        log.info("查询结果数量：" + pageList.getRecords().size());
        log.info("数据总数：" + pageList.getTotal());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 查询对比数据
     */
    @RequestMapping(value = "/queryCompareList", method = RequestMethod.GET)
    public Result<List<DataLog>> queryCompareList(HttpServletRequest req) {
        Result<List<DataLog>> result = new Result<>();
        String dataId1 = req.getParameter("dataId1");
        String dataId2 = req.getParameter("dataId2");
        List<String> idList = new ArrayList<>();
        idList.add(dataId1);
        idList.add(dataId2);
        try {
            List<DataLog> list = service.listByIds(idList);
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询版本信息
     */
    @RequestMapping(value = "/queryDataVerList", method = RequestMethod.GET)
    public Result<List<DataLog>> queryDataVerList(HttpServletRequest req) {
        Result<List<DataLog>> result = new Result<>();
        String dataTable = req.getParameter("dataTable");
        String dataId = req.getParameter("dataId");
        QueryWrapper<DataLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("data_table", dataTable);
        queryWrapper.eq("data_id", dataId);
        List<DataLog> list = service.list(queryWrapper);
        if (list == null || list.size() <= 0) {
            result.error500("未找到版本信息");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

}
