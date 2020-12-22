package webapi.onlineController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import commons.api.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smartcode.form.service.OnlCgformFieldService;

import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/26 11:27
 * @Description: zdit.zdboot.smartcode.online.controller
 **/
@RestController("onlCgformFieldController")
@RequestMapping({"/online/cgform/field"})
public class OnlCgformFieldController {

    private static final Logger logger = LoggerFactory.getLogger(OnlCgformFieldController.class);

    @Autowired
    private OnlCgformFieldService onlCgformFieldService;

    @GetMapping({"/listByHeadId"})
    public Result<?> b(@RequestParam("headId") String headId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("cgform_head_id", headId);
        queryWrapper.orderByAsc("order_num");
        List list = this.onlCgformFieldService.list(queryWrapper);
        return Result.ok(list);
    }
}
