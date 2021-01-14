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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;
import tech.techActivity.entity.TechFieldCope;
import tech.techActivity.service.ITechActivityService;
import tech.techActivity.service.ITechFieldService;
import tech.wxUser.entity.WxUser;
import tech.wxUser.service.WxUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
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
//        if (signUp != null) {
//            if (oConvertUtils.isNotEmpty(signUp.getName())) {
//                signUp.setName("*" + signUp.getName() + "*");
//            }
//            if (oConvertUtils.isNotEmpty(signUp.getUnitName())) {
//                signUp.setUnitName("*" + signUp.getUnitName() + "*");
//            }
//        }
        String sql = QueryGenerator.installAuthJdbc(SignUp.class);
//        QueryGenerator.initQueryWrapper(signUp, req.getParameterMap());
        QueryWrapper<SignUp> queryWrapper = new QueryWrapper<>();
        if(signUp!=null){
            if (oConvertUtils.isNotEmpty(signUp.getName())) {
                queryWrapper.like("name",signUp.getName());
            }
            if (oConvertUtils.isNotEmpty(signUp.getUnitName())) {
                queryWrapper.like("unit_name",signUp.getUnitName());
            }
            if(oConvertUtils.isEmpty(signUp.getAudit()) || (("1".equals(signUp.getAudit())))){
                queryWrapper.eq("audit",1);
            }else if(("3").equals(signUp.getAudit())){
                queryWrapper.in("audit",2,3);
            }else if(("4").equals(signUp.getAudit())){
                queryWrapper.isNull("audit");
            }
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        queryWrapper.and(qw->qw.like("create_by", user.getUsername()).or().isNull("create_by"));
        Page<SignUp> page = new Page<SignUp>(pageNo, pageSize);
        IPage<SignUp> pageList = signUpService.page(page, queryWrapper);
        if (pageList.getRecords() != null && pageList.getRecords().size() > 0) {
            pageList.getRecords().forEach(signUp1 -> {
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
    public Result<?> add(@RequestBody SignUp signUp) {
//        统计标记
        signUp.setType("1");
//        签到状态
        signUp.setStatus("2");
        WxUser open_id = wxUserService.getOne(new QueryWrapper<WxUser>()
                .eq("open_id", signUp.getOpenId())
                .eq("del_flag",0));
        if(oConvertUtils.isEmpty(open_id)){
            return Result.error("您未关注该服务号号，请先关注才能报名！");
        }
        WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>()
                .eq("open_id", signUp.getOpenId()).eq("status", 2));
        if (wxUser != null) {
            return Result.error("您的账号已被禁用，暂时不允许报名活动！");
        }
        TechActivity techActivity = techActivityService.getById(signUp.getTechName());
        if(techActivity.getTime()!=null && techActivity.getTime().compareTo(new Date())<=0){
            return Result.error("该活动报名时间已过，不允许报名该活动");
        }
//        需要审批
        if("1".equals(techActivity.getAuditType())){
            signUp.setAudit("1");
//            设置审批负责人
            signUp.setCreateBy(techActivity.getDeptCode());
        }else{
            signUp.setCreateBy(null);
        }
        if (techActivity.getStatus() != null && !(("1").equals(techActivity.getStatus().toString()))) {
            if (techActivity.getPeopleMax() != null && !(("0").equals(techActivity.getPeopleMax()))) {
                int techName = signUpService.count(new QueryWrapper<SignUp>()
                        .eq("tech_name", techActivity.getId())
                        .and(q->q.eq("audit",2).or().isNull("audit")));
                if (techName >= Integer.parseInt(techActivity.getPeopleMax())) {
                    return Result.error("报名人数已上限，不允许报名！");
                }
            }
            signUp.setSysOrgCode(techActivity.getSysOrgCode());

            if (signUp.getTechFieldList() != null && signUp.getTechFieldList().size() > 0) {
                signUp.getTechFieldList().sort(Comparator.comparing(TechField::getSort));
                ArrayList<TechFieldCope> techFieldCopes = new ArrayList<>();
                signUp.getTechFieldList().forEach(techField -> {
                    TechFieldCope techFieldCope = new TechFieldCope();
                    BeanUtils.copyProperties(techField,techFieldCope);
                    techFieldCopes.add(techFieldCope);
                });
                JSONArray jsonArray = JSONArray.fromObject(techFieldCopes);
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
//            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

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
        TechActivity techActivity = techActivityService.getById(sign.getTechName());
        if (techActivity.getPeopleMax() != null && !techActivity.getPeopleMax().equals("0")) {
            int techName = signUpService.count(new QueryWrapper<SignUp>()
                    .eq("tech_name", techActivity.getId()).eq("audit",2));
            if (techName >= Integer.parseInt(techActivity.getPeopleMax())) {
                return Result.error("审批通过人数已上限，不允许审批通过！");
            }
        }
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
                                techField.setItemText(itemList.get(0).getItemText());
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
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SignUp> pageList = signUpService.getList(signUp,user);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "报名表");
        mv.addObject(NormalExcelConstants.CLASS, SignUp.class);
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
    public Result<?> getList(SignUp sign) {
        return signUpService.queryList(sign);
    }

    @AutoLog(value = "报名表-分页列表查询")
    @ApiOperation(value = "报名表-分页列表查询", notes = "报名表-分页列表查询")
    @GetMapping(value = "/queryList")
    public Result<?> queryList(SignUp signUp,
                                   HttpServletRequest req) {
        if (signUp != null) {
            if (oConvertUtils.isNotEmpty(signUp.getName())) {
                signUp.setName("*" + signUp.getName() + "*");
            }
            if (oConvertUtils.isNotEmpty(signUp.getUnitName())) {
                signUp.setUnitName("*" + signUp.getUnitName() + "*");
            }
        }
        List<TechActivity> list = techActivityService.list(new QueryWrapper<TechActivity>()
                .select("id")
                .gt("end_time", new Date()));
        if(list!=null && list.size()>0){
            List<String> list1 = list.stream().map(l -> l.getId()).collect(Collectors.toList());
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            QueryWrapper<SignUp> queryWrapper = QueryGenerator.initQueryWrapper(signUp, req.getParameterMap());
            queryWrapper.in("tech_name",list1);
            queryWrapper.and(qw->qw.like("create_by", user.getUsername()).or().isNull("create_by"));
            List<SignUp> pageList = signUpService.list(queryWrapper);
            if(pageList!=null && pageList.size()>0){
                pageList.forEach(signUp1 -> {
                    signUp1.setChecked(false);
                });
                return Result.ok(pageList);
            }
        }
       return Result.error("没有需要审批的数据");
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
        TechActivity techActivity = techActivityService.getById(sign.getTechName());
        if(techActivity.getSignTime().compareTo(new Date())<=0 && techActivity.getSignEndTime().compareTo(new Date())>=0){
            if (("1").equals(sign.getStatus())) {
                return Result.error("您已签到，不能重复签到！");
            }
            signUpService.updateById(signUp);
            return Result.ok("签到成功！");
        }
        return Result.error("签到失败,不在签到时间内！");
    }

}
