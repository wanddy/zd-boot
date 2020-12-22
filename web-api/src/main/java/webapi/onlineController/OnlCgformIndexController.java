package webapi.onlineController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.api.vo.Result;
import commons.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smartcode.form.service.OnlCgformIndexService;

import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/26 13:09
 * @Description: zdit.zdboot.smartcode.online.controller
 **/
@RestController("onlCgformIndexController")
@RequestMapping({"/online/cgform/index"})
public class OnlCgformIndexController {
    private static final Logger logger = LoggerFactory.getLogger(OnlCgformIndexController.class);
    @Autowired
    private OnlCgformIndexService onlCgformIndexService;

    @GetMapping({"/listByHeadId"})
    public Result<?> a(@RequestParam("headId") String headId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("cgform_head_id", headId);
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByDesc("create_time");
        List list = this.onlCgformIndexService.list(queryWrapper);
        return Result.ok(list);
    }

}
