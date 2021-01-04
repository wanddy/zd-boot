package webapi.techController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import commons.annotation.AutoLog;
import commons.annotation.PermissionData;
import commons.api.vo.Result;
import commons.auth.query.QueryGenerator;
import commons.auth.vo.LoginUser;
import commons.poi.def.NormalExcelConstants;
import commons.poi.excel.ExcelImportUtil;
import commons.poi.excel.entity.ExportParams;
import commons.poi.excel.entity.ImportParams;
import commons.poi.view.JeecgEntityExcelView;
import commons.util.oConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import tech.constant.Constant;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;
import tech.techActivity.service.ITechActivityService;
import tech.techActivity.service.ITechFieldService;
import tech.techActivity.vo.AccessToken;
import tech.techActivity.vo.TechActivityPage;
import tech.utils.CommonUtil;
import webapi.weixinController.utils.SignUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date: 2020-12-02
 * @Version: V1.0
 */
@Api(tags = "活动表")
@RestController
@RequestMapping("/techActivity/techActivity")
@Slf4j
public class TechActivityController {
    @Autowired
    private ITechActivityService techActivityService;
    @Autowired
    private ITechFieldService techFieldService;
    @Autowired
    private ISignUpService iSignUpService;


    /**
     * 分页列表查询
     *
     * @param techActivity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "活动表-分页列表查询")
    @ApiOperation(value = "活动表-分页列表查询", notes = "活动表-分页列表查询")
    @PermissionData(pageComponent = "tech/techActivity/TechActivityList")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(TechActivity techActivity,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req,
                                   @RequestParam(name = "startTime", required = false) String startTime1) {
        String audit = "audit";
        if (techActivity != null) {
            if (oConvertUtils.isNotEmpty(techActivity.getHeadline())) {
                techActivity.setHeadline("*" + techActivity.getHeadline() + "*");
            }
            if (oConvertUtils.isNotEmpty(startTime1)) {
//                techActivity.setStartTime(startTime1 + "*");
            }
            if (oConvertUtils.isNotEmpty(techActivity.getAudit())) {
                audit = techActivity.getAudit();
                techActivity.setAudit(null);
            }
        }
        QueryWrapper<TechActivity> queryWrapper = QueryGenerator.initQueryWrapper(techActivity, req.getParameterMap());
//        if (!audit.equals("audit")) {
//            queryWrapper.notIn("audit", 1, 3);
//        }
        Page<TechActivity> page = new Page<TechActivity>(pageNo, pageSize);
        IPage<TechActivity> pageList = techActivityService.page(page, queryWrapper);
        if (pageList.getRecords() != null && pageList.getRecords().size() > 0) {
            pageList.getRecords().forEach(techActivity1 -> {
                int techName = iSignUpService.count(new QueryWrapper<SignUp>()
                        .eq("tech_name", techActivity1.getId())
                        .notIn("audit", 1, 3));
                if (oConvertUtils.isNotEmpty(techActivity1.getPeopleMax()) && Integer.parseInt(techActivity1.getPeopleMax()) > 0) {
                    techActivity1.setPeopleMaxText(techName + "/" + techActivity1.getPeopleMax());
                } else {
                    techActivity1.setPeopleMax(null);
                    techActivity1.setPeopleMaxText(String.valueOf(techName) + "/" + "∞");
                }
            });
        }
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param techActivityPage
     * @return
     */
    @AutoLog(value = "活动表-添加")
    @ApiOperation(value = "活动表-添加", notes = "活动表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody TechActivityPage techActivityPage) {
        TechActivity techActivity = new TechActivity();
        BeanUtils.copyProperties(techActivityPage, techActivity);
        if (oConvertUtils.isEmpty(techActivity.getPeopleMax())) {
            techActivity.setPeopleMax("0");
        }
        if(techActivity.getAuditTech().equals("2") && oConvertUtils.isNotEmpty(techActivity.getDepartCode())){
            return  Result.error("需要审批请选择审批人，如不需要审批请清空审批人！");
        }
        if(techActivity.getAuditType().equals("2") && oConvertUtils.isNotEmpty(techActivity.getDeptCode())){
            return  Result.error("需要审批请选择审批人，如不需要审批请清空审批人！");
        }
        if(techActivity.getAuditType().equals("1") && oConvertUtils.isEmpty(techActivity.getDeptCode())){
            return  Result.error("需要审批请选择审批人，如不需要审批请清空审批人！");
        }
        if(techActivity.getAuditTech().equals("1") && oConvertUtils.isEmpty(techActivity.getDepartCode())){
            return  Result.error("需要审批请选择审批人，如不需要审批请清空审批人！");
        }
        if ((("2").equals(techActivity.getAuditType()) && oConvertUtils.isEmpty(techActivity.getDeptCode())) || (("1").equals(techActivity.getAuditType()) && oConvertUtils.isNotEmpty(techActivity.getDeptCode()))) {
            if (("1").equals(techActivity.getAuditTech())) {
                techActivity.setAudit("1");
                String value = "管理端有您需要审批的活动，请您登录系统进行该！";
                techActivityService.getTemplate(techActivity, Constant.templateId7, value);
            }
            techActivityService.saveMain(techActivity, techActivityPage.getTechFieldList());
            return Result.ok("添加成功！");
        }
        return Result.error("需要审批请选择审批人，如不需要审批请清空审批人！");
    }

    /**
     * 编辑
     *
     * @param techActivityPage
     * @return
     */
    @AutoLog(value = "活动表-编辑")
    @ApiOperation(value = "活动表-编辑", notes = "活动表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody TechActivityPage techActivityPage) {
        TechActivity techActivity = new TechActivity();
        BeanUtils.copyProperties(techActivityPage, techActivity);
        if (oConvertUtils.isEmpty(techActivity.getPeopleMax())) {
            techActivity.setPeopleMax("0");
        }
        TechActivity techActivityEntity = techActivityService.getById(techActivity.getId());
        if (techActivityEntity == null) {
            return Result.error("未找到对应数据");
        }
        if ((("2").equals(techActivity.getAuditType()) && oConvertUtils.isEmpty(techActivity.getDeptCode())) || (("1").equals(techActivity.getAuditType()) && oConvertUtils.isNotEmpty(techActivity.getDeptCode()))) {
            if (("2").equals(techActivityEntity.getAuditType()) && ("1").equals(techActivity.getAuditTech())) {
                techActivity.setAudit("1");
            }
            techActivityService.updateMain(techActivity, techActivityPage.getTechFieldList());
            return Result.ok("编辑成功!");
        }
        return Result.error("需要审批请选择审批人，如不需要审批请清空审批人！");
    }

    /**
     * 审批
     *
     * @param
     * @return
     */
    @AutoLog(value = "活动表-审批")
    @RequiresPermissions("techActivity:updateAudit")
    @PutMapping(value = "/updateAudit")
    public Result<?> updateAudit(@RequestBody TechActivity techActivity) {
        techActivityService.updateById(techActivity);

        if (techActivity.getAudit() != null) {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//            审批通过发送微信模板消息
            if (techActivity.getAudit().equals("2")) {
                String value = "该活动已通过审批,审批人：" + user.getRealname();
                techActivityService.getTemplate1(techActivity, Constant.templateId1, value);
            }
            if (techActivity.getAudit().equals("3")) {
                String value = "该活动未通过审批,审批人：" + user.getRealname();
                techActivityService.getTemplate1(techActivity, Constant.templateId1, value);
            }
        }
        return Result.ok("审批成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "活动表-通过id删除")
    @ApiOperation(value = "活动表-通过id删除", notes = "活动表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        techActivityService.delMain(id);
        iSignUpService.remove(new QueryWrapper<SignUp>().eq("tech_name", id));
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "活动表-批量删除")
    @ApiOperation(value = "活动表-批量删除", notes = "活动表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        this.techActivityService.delBatchMain(list);
        list.forEach(id -> {
            iSignUpService.remove(new QueryWrapper<SignUp>().eq("tech_name", id));
        });
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "活动表-通过id查询")
    @ApiOperation(value = "活动表-通过id查询", notes = "活动表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id,
                               @RequestParam(name = "code", required = false) String openId) {
        return techActivityService.queryById(id, openId);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报名表单配置通过主表ID查询")
    @ApiOperation(value = "报名表单配置主表ID查询", notes = "报名表单配置-通主表ID查询")
    @GetMapping(value = "/queryTechFieldByMainId")
    public Result<?> queryTechFieldListByMainId(@RequestParam(name = "id", required = true) String id,
                                                @RequestParam(name = "type", required = false) String type) {
        List<TechField> techFieldList = new ArrayList<>();
        if(type == null){
            techFieldList = techFieldService.selectByMainId(id);
        }else{
            TechField techField1 = new TechField();
            techField1.setFieldName("姓名");
            techField1.setFieldType("1");
            techFieldList.add(techField1);
            TechField techField2 = new TechField();
            techField2.setFieldName("单位");
            techField2.setFieldType("1");
            techFieldList.add(techField2);
            TechField techField3 = new TechField();
            techField3.setFieldName("手机号");
            techField3.setFieldType("1");
            techFieldList.add(techField3);
        }
        return Result.ok(techFieldList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param techActivity
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TechActivity techActivity) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<TechActivity> queryWrapper = QueryGenerator.initQueryWrapper(techActivity, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<TechActivity> queryList = techActivityService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<TechActivity> techActivityList = new ArrayList<TechActivity>();
        if (oConvertUtils.isEmpty(selections)) {
            techActivityList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            techActivityList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<TechActivityPage> pageList = new ArrayList<TechActivityPage>();
        for (TechActivity main : techActivityList) {
            TechActivityPage vo = new TechActivityPage();
            BeanUtils.copyProperties(main, vo);
            List<TechField> techFieldList = techFieldService.selectByMainId(main.getId());
            vo.setTechFieldList(techFieldList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "活动表列表");
        mv.addObject(NormalExcelConstants.CLASS, TechActivityPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("活动表数据", "导出人:" + sysUser.getRealname(), "活动表"));
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<TechActivityPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TechActivityPage.class, params);
                for (TechActivityPage page : list) {
                    TechActivity po = new TechActivity();
                    BeanUtils.copyProperties(page, po);
                    techActivityService.saveMain(po, page.getTechFieldList());
                }
                return Result.ok("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    @AutoLog(value = "活动表-公众号列表")
    @ApiOperation(value = "活动表-公众号列表", notes = "活动表-公众号列表")
    @GetMapping(value = "/appList")
    public Result<?> appList(@RequestParam(name = "headline", required = false) String headline,
                             @RequestParam(name = "place", required = false) String place,
                             @RequestParam(name = "startTime", required = false) String startTime,
                             @RequestParam(name = "status", required = false) String status) {
        List<TechActivity> list = techActivityService.appList(headline, place, startTime,status);
        if (list != null && list.size() > 0) {
            list.forEach(techActivity -> {
                if (!techActivity.getStatus().equals(3L)) {
                    int techName = iSignUpService.count(new QueryWrapper<SignUp>()
                            .eq("tech_name", techActivity.getId()).eq("audit",2));
                    if (oConvertUtils.isNotEmpty(techActivity.getPeopleMax()) && Integer.parseInt(techActivity.getPeopleMax()) > 0) {
                        techActivity.setPeopleMaxText(techName + "/" + techActivity.getPeopleMax());
                    } else {
                        techActivity.setPeopleMaxText(String.valueOf(techName) + "/" + "∞");
                    }
                }
            });
        }
        return Result.ok(list);
    }

    /**
     * 生成永久二维码
     *
     * @param techActivity
     * @return
     */
    @PostMapping(value = "/saveCode")
    public Result<?> saveCode(@RequestBody TechActivity techActivity) {
        return techActivityService.saveCode(techActivity);
    }

    /**
     * 获取用户openid
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/getOpenId")
    public Result<?> getOpenId(@RequestParam String code) {
        String openId = techActivityService.getOpenId(code);
        return Result.ok(openId);
    }

    /**
     * 下载二维码
     *
     * @param id 微信用户唯一标识
     * @return 返回状态
     */
    @GetMapping(value = "/download")
    public void download(@RequestParam String id,
                         @RequestParam String type,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        techActivityService.download(id, type, request, response);
    }

    /**
     * 开发者通过检验signature对请求进行校验及微信动作拦截
     *
     * @param request  request
     * @param response response
     * @return String
     */
    @RequestMapping(value = "/signature")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//		AccessToken accessToken = CommonUtil.accessToken;
//		System.out.println(accessToken);
//		//服务器校验
//
//			try {
//				// 微信加密签名
//				String signature = request.getParameter("signature");
//				// 时间戳
//				String timestamp = request.getParameter("timestamp");
//				// 随机数
//				String nonce = request.getParameter("nonce");
//				// 随机字符串
//				String echostr = request.getParameter("echostr");
//				PrintWriter out = response.getWriter();
//				// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
//				if (SignUtil.checkSignature(signature, timestamp, nonce)) {
//					out.print(echostr);
//				} else {
//					//校验失败返回其他
//					out.print(timestamp);
//				}
//				out.close();
//			} catch (Exception e) {
//				log.error("微信服务器校验失败，失败信息：", e);
//			}

//		 获得微信端返回的xml数据
        techActivityService.doGet(request, response);
    }


    /**
     * js验证
     *
     * @return
     */
    @RequestMapping("MP_verify_HRls16b6K4qVKiZP.txt")
    public String wxPrivateKey() {
        return "HRls16b6K4qVKiZP";
    }
}
