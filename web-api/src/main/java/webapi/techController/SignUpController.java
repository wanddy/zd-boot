package webapi.techController;

import auth.domain.dict.service.ISysDictItemService;
import auth.domain.dict.service.ISysDictService;
import auth.entity.Dict;
import auth.entity.DictItem;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.AutoLog;
import commons.annotation.PermissionData;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;
import commons.poi.def.NormalExcelConstants;
import commons.poi.excel.entity.ExportParams;
import commons.poi.view.JeecgEntityExcelView;
import commons.system.base.controller.JeecgController;
import commons.util.oConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;
import tech.techActivity.service.ITechActivityService;
import tech.techActivity.service.ITechFieldService;
import tech.wxUser.entity.WxUser;
import tech.wxUser.service.WxUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 报名表
 * @Author: zd-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
@Api(tags = "报名表")
@RestController
@RequestMapping("/signUp/signUp")
@Slf4j
public class SignUpController extends JeecgController<SignUp, ISignUpService> {
    @Autowired
    private ISignUpService signUpService;
    @Autowired
    private ITechActivityService techActivityService;
    @Autowired
    private ITechFieldService techFieldService;
    @Autowired
    private TechActivityController techActivityController;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    private ISysDictItemService sysDictItemService;


    /**
     * 分页列表查询
     *
     * @param signUp
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "报名表-分页列表查询")
    @ApiOperation(value = "报名表-分页列表查询", notes = "报名表-分页列表查询")
    @PermissionData(pageComponent = "tech/sginUp/SignUpList")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SignUp signUp,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(name = "techName", required = false) String techName,
                                   HttpServletRequest req) {
        if (signUp != null) {
            if (oConvertUtils.isNotEmpty(signUp.getName())) {
                signUp.setName("*" + signUp.getName() + "*");
            }
            if (oConvertUtils.isNotEmpty(signUp.getUnitName())) {
                signUp.setUnitName("*" + signUp.getUnitName() + "*");
            }
        }
        QueryWrapper<SignUp> queryWrapper = QueryGenerator.initQueryWrapper(signUp, req.getParameterMap());
        Page<SignUp> page = new Page<SignUp>(pageNo, pageSize);
        IPage<SignUp> pageList = signUpService.page(page, queryWrapper);
        if (pageList.getRecords() != null && pageList.getRecords().size() > 0) {
            pageList.getRecords().forEach(signUp1 -> {
//				List<TechField> techFields = techFieldService.list(new QueryWrapper<TechField>().eq("tech_id", signUp1.getTechName()));
                if (oConvertUtils.isNotEmpty(signUp1.getFieldTest())) {
                    JSONArray jsonArray = JSONArray.fromObject(signUp1.getFieldTest());
                    List<TechField> fields = JSONObject.parseArray(jsonArray.toString(), TechField.class);
                    signUp1.setTechFieldList(fields);
                }
            });
        }
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param signUp
     * @return
     */
    @AutoLog(value = "报名表-添加")
    @ApiOperation(value = "报名表-添加", notes = "报名表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(SignUp signUp) {
        signUp.setType("1");
        signUp.setStatus("2");
        WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>()
                .eq("open_id", signUp.getOpenId()).eq("status", 2));
        if (wxUser != null) {
            return Result.error("您的账号已被禁用，暂时不允许报名活动！");
        }
        TechActivity techActivity = techActivityService.getById(signUp.getTechName());
        if (techActivity != null && techActivity.getStatus() != null && !techActivity.getStatus().equals("1")) {
            if (techActivity.getPeopleMax() != null && !techActivity.getPeopleMax().equals("0")) {
                int techName = signUpService.count(new QueryWrapper<SignUp>()
                        .eq("tech_name", techActivity.getId()));
                if (techName >= Integer.parseInt(techActivity.getPeopleMax())) {
                    return Result.error("报名人数已上限，不允许报名！");
                }
            }
            signUp.setSysOrgCode(techActivity.getSysOrgCode());
            if (signUp.getTechFieldList() != null && signUp.getTechFieldList().size() > 0) {
                signUp.getTechFieldList().sort(Comparator.comparing(TechField::getSort).reversed());
                JSONArray jsonArray = JSONArray.fromObject(signUp.getTechFieldList());
                signUp.setFieldTest(jsonArray.toString());
            }
            WxUser wxUser1 = wxUserService.getOne(new QueryWrapper<WxUser>()
                    .eq("open_id", signUp.getOpenId()));
//            保存用户上次填写的报名信息
            if (oConvertUtils.isNotEmpty(wxUser1)) {
                wxUser1.setName(signUp.getName());
                wxUser1.setUnitName(signUp.getUnitName());
                wxUser1.setPhoneNumber(signUp.getPhoneNumber());
                wxUserService.updateById(wxUser1);
            }
            return signUpService.add(signUp);
        }
        return Result.error("该活动不是未开始活动，不允许报名");
    }

    /**
     * 编辑
     *
     * @param signUp
     * @return
     */
    @AutoLog(value = "报名表-编辑")
    @ApiOperation(value = "报名表-编辑", notes = "报名表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SignUp signUp) {
        SignUp sign = signUpService.getById(signUp.getId());
        if (Integer.parseInt(sign.getAudit()) > 1) {
            return Result.error("该用户已审批过，不能重复审批！");
        }
        return signUpService.edit(signUp);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报名表-通过id删除")
    @ApiOperation(value = "报名表-通过id删除", notes = "报名表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        signUpService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "报名表-批量删除")
    @ApiOperation(value = "报名表-批量删除", notes = "报名表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.signUpService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 批量审批
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "报名表-批量审批")
    @ApiOperation(value = "报名表-批量审批", notes = "报名表-批量审批")
    @GetMapping(value = "/batchUpdate")
    public Result<?> batchUpdate(@RequestParam(name = "ids") String ids,
                                 @RequestParam(name = "audit") String audit) {
        List<String> list = Arrays.asList(ids.split(","));
        return this.signUpService.batchUpdate(list, audit);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报名表-通过id查询")
    @ApiOperation(value = "报名表-通过id查询", notes = "报名表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id,
                               @RequestParam(name = "openId", required = false) String openId) {
        if (oConvertUtils.isEmpty(openId)) {
            SignUp signUp = signUpService.getById(id);
            if (signUp == null) {
                return Result.error("未找到对应数据");
            }
            if (oConvertUtils.isNotEmpty(signUp.getFieldTest())) {
                JSONArray jsonArray = JSONArray.fromObject(signUp.getFieldTest());
                List<TechField> fields = JSONObject.parseArray(jsonArray.toString(), TechField.class);
                fields.forEach(techField -> {
                    if (techField.getFieldType().equals("3") || techField.getFieldType().equals("4")) {
                        if (oConvertUtils.isNotEmpty(techField.getFieldDict())) {
                            Dict dict = sysDictService.getOne(new QueryWrapper<Dict>().eq("dict_code", techField.getFieldDict()));
                            List<DictItem> dictItemList = sysDictItemService.list(new QueryWrapper<DictItem>().eq("dict_id", dict.getId()));
                            if (oConvertUtils.isNotEmpty(techField.getTest()) && techField.getFieldType().equals("3")) {
                                List<DictItem> itemList = dictItemList.stream().filter(dictItem -> dictItem.getItemValue().equals(techField.getTest())).collect(Collectors.toList());
                                techField.setTest(itemList.get(0).getItemText());
                            }
                            techField.setDictList(dictItemList);
                        }
                    }
                });
                signUp.setTechFieldList(fields);
            }
            return Result.ok(signUp);
        } else {
            SignUp signUp = signUpService.getOne(
                    new QueryWrapper<SignUp>().eq("tech_name", id).eq("open_id", openId));
            if (signUp == null) {
                return Result.error("未找到对应数据");
            }
            TechActivity techActivity = techActivityService.getById(id);
            techActivity.setSignUp(signUp);
            return Result.ok(techActivity);
        }

    }

    /**
     * 导出excel
     *
     * @param request
     * @param signUp
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SignUp signUp) {

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SignUp> pageList = signUpService.getList(signUp);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "报名表");
        mv.addObject(NormalExcelConstants.CLASS, SignUp.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("报名表报表", "导出人:" + user.getRealname(), "报名表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
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
        return super.importExcel(request, response, SignUp.class);
    }

    /**
     * 查询表字段
     *
     * @return
     */
    @GetMapping("/getFieldList")
    public Result<?> getFieldList() {
        return signUpService.getFieldList();
    }

    /**
     * 查询表字段
     *
     * @return
     */
    @GetMapping("/getField")
    public Result<?> getField(@RequestParam String techId) {
        List<TechField> techFields = techFieldService.list(new QueryWrapper<TechField>().eq("tech_id", techId));
        return Result.ok(techFields);
    }

    /**
     * 公众号查询未开始、已审核的列表数据
     *
     * @return
     */
    @GetMapping("/getList")
    public Result<?> getList(TechActivity techActivity, HttpServletRequest req) {
        techActivity.setStatus(1L);
        techActivity.setAudit("2");
        Result<?> result = techActivityController.queryPageList(techActivity, 1, 100, req);
        return result;
    }

    /**
     * 根据活动id查询报名用户信息
     *
     * @return
     */
    @GetMapping("/getByTechId")
    public Result<?> getByTechId(@RequestParam String id,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "unitName", required = false) String unitName,
                                 @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
        if (oConvertUtils.isNotEmpty(id)) {
            QueryWrapper<SignUp> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tech_name", id);
            if (oConvertUtils.isNotEmpty(name)) {
                queryWrapper.like("name", name);
            }
            if (oConvertUtils.isNotEmpty(name)) {
                queryWrapper.or().like("unit_name", unitName);
            }
            if (oConvertUtils.isNotEmpty(name)) {
                queryWrapper.or().like("phone_number", phoneNumber);
            }
            List<SignUp> list = signUpService.list(queryWrapper.orderByDesc("create_time"));
            return Result.ok(list);
        }
        return Result.error("请传入活动id");
    }

    /**
     * 公众号查询未开始、已审核的列表数据
     *
     * @return
     */
    @GetMapping("/getDictItem")
    public Result<?> getDictItem(@RequestParam String code) {
        if (oConvertUtils.isNotEmpty(code)) {
            Dict dict = sysDictService.getOne(new QueryWrapper<Dict>().eq("dict_code", code));
            List<DictItem> dictItemList = sysDictItemService.list(new QueryWrapper<DictItem>().eq("dict_id", dict.getId()));
            return Result.ok(dictItemList);
        }
        return Result.error("获取失败");
    }

    /**
     * 编辑
     *
     * @param signUp
     * @return
     */
    @AutoLog(value = "报名表-签到")
    @ApiOperation(value = "报名表-签到", notes = "报名表-签到")
    @PutMapping(value = "/updateByStatus")
    public Result<?> updateByStatus(@RequestBody SignUp signUp) {
        SignUp sign = signUpService.getById(signUp.getId());
        if (sign.getStatus().equals("2")) {
            return Result.error("您已签到，不能重复签到！");
        }
        signUpService.updateById(signUp);
        return Result.ok("签到成功！");
    }

}
