package webapi.techController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.AutoLog;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.system.base.controller.JeecgController;
import commons.util.oConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import tech.constant.Constant;
import tech.utils.CommonUtil;
import tech.wxUser.entity.WxUser;
import tech.wxUser.service.WxUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: wx_user
 * @Author: zd-boot
 * @Date: 2020-12-11
 * @Version: V1.0
 */
@Api(tags = "wx_user")
@RestController
@RequestMapping("/wxUser/wxUser")
@Slf4j
public class WxUserController extends JeecgController<WxUser, WxUserService> {
    @Autowired
    private WxUserService wxUserService;

    /**
     * 分页列表查询
     *
     * @param wxUser
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "wx_user-分页列表查询")
    @ApiOperation(value = "wx_user-分页列表查询", notes = "wx_user-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(WxUser wxUser,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        wxUser.setDelFlag("0");
        if (oConvertUtils.isNotEmpty(wxUser.getNickname())) {
            wxUser.setNickname("*" + wxUser.getNickname() + "*");
        }
        if (oConvertUtils.isNotEmpty(wxUser.getCity())) {
            wxUser.setCity("*" + wxUser.getCity() + "*");
        }
        QueryWrapper<WxUser> queryWrapper = QueryGenerator.initQueryWrapper(wxUser, req.getParameterMap());
        Page<WxUser> page = new Page<WxUser>(pageNo, pageSize);
        IPage<WxUser> pageList = wxUserService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param wxUser
     * @return
     */
    @AutoLog(value = "wx_user-添加")
    @ApiOperation(value = "wx_user-添加", notes = "wx_user-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody WxUser wxUser) {
        wxUserService.save(wxUser);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param wxUser
     * @return
     */
    @AutoLog(value = "wx_user-编辑")
    @ApiOperation(value = "wx_user-编辑", notes = "wx_user-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody WxUser wxUser) {
        // 启用
        if (wxUser.getStatus().equals("1")) {
            wxUser.setEndDay("0");
            wxUser.setEndTime("0");
            wxUser.setNum(0L);

            JSONObject jsonObject = new JSONObject();
            //用户openid
            jsonObject.put("touser", wxUser.getOpenId());
            jsonObject.put("template_id", Constant.templateId4);
            JSONObject data = new JSONObject();
            JSONObject first = new JSONObject();
            first.put("value", "您的账号已被启用，可以报名参加活动！");
            data.put("first", first);
            JSONObject keyword1 = new JSONObject();
            keyword1.put("value", "已启用");
            data.put("keyword1", keyword1);

            JSONObject keyword2 = new JSONObject();
            keyword2.put("value", new Date());
            data.put("keyword2", keyword2);
            jsonObject.put("data", data);
            net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + CommonUtil.accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

        } else if (wxUser.getStatus().equals("2") && wxUser.getEndDay() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, Integer.parseInt(wxUser.getEndDay()));
            String endTime = format.format(ca.getTime());
            wxUser.setEndTime(endTime);

            JSONObject jsonObject = new JSONObject();
            //用户openid
            jsonObject.put("touser", wxUser.getOpenId());
            jsonObject.put("template_id", Constant.templateId4);
            JSONObject data = new JSONObject();
            JSONObject first = new JSONObject();
            first.put("value", "您的账号已被禁用" + wxUser.getEndDay() + "天" + "，禁用期间不允许报名活动，如有问题请联系管理员！");
            data.put("first", first);
            JSONObject keyword1 = new JSONObject();
            keyword1.put("value", "已禁用");
            data.put("keyword1", keyword1);

            JSONObject keyword2 = new JSONObject();
            keyword2.put("value", new Date());
            data.put("keyword2", keyword2);
            JSONObject remark = new JSONObject();
            remark.put("value", "管理员禁用！");
            data.put("remark", remark);
            jsonObject.put("data", data);
            net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + CommonUtil.accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));


        }
        wxUserService.updateById(wxUser);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "wx_user-通过id删除")
    @ApiOperation(value = "wx_user-通过id删除", notes = "wx_user-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        wxUserService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "wx_user-批量删除")
    @ApiOperation(value = "wx_user-批量删除", notes = "wx_user-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.wxUserService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "wx_user-通过id查询")
    @ApiOperation(value = "wx_user-通过id查询", notes = "wx_user-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        WxUser wxUser = wxUserService.getById(id);
        if (wxUser == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(wxUser);
    }

    /**
     * 通过openid查询
     *
     * @param openId
     * @return
     */
    @AutoLog(value = "wx_user-通过openid查询")
    @ApiOperation(value = "wx_user-通过openid查询", notes = "wx_user-通过openid查询")
    @GetMapping(value = "/queryByOpenId")
    public Result<?> queryByOpenId(@RequestParam(name = "openId", required = false) String openId) {
        WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>().eq("open_id", openId));
        if (wxUser == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(wxUser);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param wxUser
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WxUser wxUser) {
        return super.exportXls(request, wxUser, WxUser.class, "wx_user");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WxUser.class);
    }

    /**
     * 创建微信菜单
     *
     * @return
     */
    @RequestMapping(value = "/saveMenu", method = RequestMethod.POST)
    public Result<?> saveMenu(@RequestBody JSONObject menu) {
        Object menu1 = menu.get("menu");
        JSONObject jsonObject1 = JSONObject.parseObject(menu1.toString());
        String s = JSONObject.toJSONString(jsonObject1);
        System.out.println(s);
        net.sf.json.JSONObject jsonObject = CommonUtil.httpsRequest("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" +
                CommonUtil.accessToken.getAccessToken(), Constant.post, JSONObject.toJSONString(jsonObject1));
        if (("ok").equals(jsonObject.get("errmsg"))) {
            return Result.ok("修改成功！");
        }
        return Result.error("修改失败！");
    }

    /**
     * 查询微信菜单
     *
     * @return
     */
    @RequestMapping(value = "/getMenu", method = RequestMethod.GET)
    public Result<?> getMenu() {
        net.sf.json.JSONObject jsonObject = CommonUtil.httpsRequest("https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=" +
                CommonUtil.accessToken.getAccessToken(), Constant.get, null);
        String replace = jsonObject.get("selfmenu_info").toString().replace("{\"list\":", "");
        return Result.ok(replace.replace("]}}", "]}"));
    }
}
