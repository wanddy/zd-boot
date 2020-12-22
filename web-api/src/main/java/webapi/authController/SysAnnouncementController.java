package webapi.authController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import auth.discard.message.websocket.WebSocket;
import auth.domain.notice.service.ISysAnnouncementSendService;
import auth.domain.notice.service.ISysAnnouncementService;
import auth.entity.Announcement;
import auth.entity.AnnouncementSend;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import commons.constant.CommonConstant;
import commons.constant.CommonSendStatus;
import commons.constant.WebsocketConst;
import commons.auth.utils.JwtUtil;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Title: Controller
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/annountCement")
@Slf4j
public class SysAnnouncementController {

    private final ISysAnnouncementService sysAnnouncementService;

    private final ISysAnnouncementSendService sysAnnouncementSendService;

    private final WebSocket webSocket;

    @Autowired
    public SysAnnouncementController(ISysAnnouncementService sysAnnouncementService, ISysAnnouncementSendService sysAnnouncementSendService, WebSocket webSocket) {
        this.sysAnnouncementService = sysAnnouncementService;
        this.sysAnnouncementSendService = sysAnnouncementSendService;
        this.webSocket = webSocket;
    }

    /**
     * 分页列表查询
     *
     * @param announcement
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Announcement>> queryPageList(Announcement announcement,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        Result<IPage<Announcement>> result = new Result<IPage<Announcement>>();
        announcement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<Announcement>(announcement);
        Page<Announcement> page = new Page<Announcement>(pageNo, pageSize);
        //排序逻辑 处理
        String column = req.getParameter("column");
        String order = req.getParameter("order");
        if (oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
            if ("asc".equals(order)) {
                queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
            } else {
                queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
            }
        }
        IPage<Announcement> pageList = sysAnnouncementService.page(page, queryWrapper);
        log.info("查询当前页：" + pageList.getCurrent());
        log.info("查询当前页数量：" + pageList.getSize());
        log.info("查询结果数量：" + pageList.getRecords().size());
        log.info("数据总数：" + pageList.getTotal());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param announcement
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Announcement> add(@RequestBody Announcement announcement) {
        Result<Announcement> result = new Result<Announcement>();
        try {
            announcement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            announcement.setSendStatus(CommonSendStatus.UNPUBLISHED_STATUS_0);//未发布
            sysAnnouncementService.saveAnnouncement(announcement);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param announcement
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<Announcement> eidt(@RequestBody Announcement announcement) {
        Result<Announcement> result = new Result<Announcement>();
        Announcement announcementEntity = sysAnnouncementService.getById(announcement.getId());
        if (announcementEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysAnnouncementService.upDateAnnouncement(announcement);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<Announcement> delete(@RequestParam(name = "id", required = true) String id) {
        Result<Announcement> result = new Result<Announcement>();
        Announcement announcement = sysAnnouncementService.getById(id);
        if (announcement == null) {
            result.error500("未找到对应实体");
        } else {
            announcement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
            boolean ok = sysAnnouncementService.updateById(announcement);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<Announcement> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Announcement> result = new Result<Announcement>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            String[] id = ids.split(",");
            for (String s : id) {
                Announcement announcement = sysAnnouncementService.getById(s);
                announcement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
                sysAnnouncementService.updateById(announcement);
            }
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<Announcement> queryById(@RequestParam(name = "id") String id) {
        Result<Announcement> result = new Result<Announcement>();
        Announcement announcement = sysAnnouncementService.getById(id);
        if (announcement == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(announcement);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 更新发布操作
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/doReleaseData", method = RequestMethod.GET)
    public Result<Announcement> doReleaseData(@RequestParam(name = "id") String id, HttpServletRequest request) {
        Result<Announcement> result = new Result<Announcement>();
        Announcement announcement = sysAnnouncementService.getById(id);
        if (announcement == null) {
            result.error500("未找到对应实体");
        } else {
            announcement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);//发布中
            announcement.setSendTime(new Date());
            String currentUserName = JwtUtil.getUserNameByToken(request);
            announcement.setSender(currentUserName);
            boolean ok = sysAnnouncementService.updateById(announcement);
            if (ok) {
                result.success("该系统通知发布成功");
                if (announcement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
                    JSONObject obj = new JSONObject();
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
                    obj.put(WebsocketConst.MSG_ID, announcement.getId());
                    obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                    webSocket.sendAllMessage(obj.toJSONString());
                } else {
                    // 2.插入用户通告阅读标记表记录
                    String userId = announcement.getUserIds();
                    String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
                    String anntId = announcement.getId();
                    Date refDate = new Date();
                    JSONObject obj = new JSONObject();
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                    obj.put(WebsocketConst.MSG_ID, announcement.getId());
                    obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                    webSocket.sendMoreMessage(userIds, obj.toJSONString());
                }
            }
        }

        return result;
    }

    /**
     * 更新撤销操作
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/doReovkeData", method = RequestMethod.GET)
    public Result<Announcement> doReovkeData(@RequestParam(name = "id") String id) {
        Result<Announcement> result = new Result<Announcement>();
        Announcement announcement = sysAnnouncementService.getById(id);
        if (announcement == null) {
            result.error500("未找到对应实体");
        } else {
            announcement.setSendStatus(CommonSendStatus.REVOKE_STATUS_2);//撤销发布
            announcement.setCancelTime(new Date());
            boolean ok = sysAnnouncementService.updateById(announcement);
            if (ok) {
                result.success("该系统通知撤销成功");
            }
        }

        return result;
    }

    /**
     * @return
     * @功能：补充用户数据，并返回系统消息
     */
    @RequestMapping(value = "/listByUser", method = RequestMethod.GET)
    public Result<Map<String, Object>> listByUser() {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        // 1.将系统消息补充到用户通告阅读标记表中
        Collection<String> anntIds = sysAnnouncementSendService.queryByUserId(userId);
        LambdaQueryWrapper<Announcement> querySaWrapper = new LambdaQueryWrapper<Announcement>();
        querySaWrapper.eq(Announcement::getMsgType, CommonConstant.MSG_TYPE_ALL); // 全部人员
        querySaWrapper.eq(Announcement::getDelFlag, CommonConstant.DEL_FLAG_0.toString());  // 未删除
        querySaWrapper.eq(Announcement::getSendStatus, CommonConstant.HAS_SEND); //已发布
        querySaWrapper.ge(Announcement::getEndTime, sysUser.getCreateTime()); //新注册用户不看结束通知
        if (anntIds != null && anntIds.size() > 0) {
            querySaWrapper.notIn(Announcement::getId, anntIds);
        }
        List<Announcement> announcements = sysAnnouncementService.list(querySaWrapper);
        if (announcements.size() > 0) {
            for (int i = 0; i < announcements.size(); i++) {
                AnnouncementSend announcementSend = new AnnouncementSend();
                announcementSend.setId(announcements.get(i).getId());
                announcementSend.setUserId(userId);
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendService.save(announcementSend);
            }
        }
        // 2.查询用户未读的系统消息
        Page<Announcement> anntMsgList = new Page<Announcement>(0, 5);
        anntMsgList = sysAnnouncementService.querySysCementPageByUserId(anntMsgList, userId, "1");//通知公告消息
        Page<Announcement> sysMsgList = new Page<Announcement>(0, 5);
        sysMsgList = sysAnnouncementService.querySysCementPageByUserId(sysMsgList, userId, "2");//系统消息
        Map<String, Object> sysMsgMap = new HashMap<String, Object>();
        sysMsgMap.put("sysMsgList", sysMsgList.getRecords());
        sysMsgMap.put("sysMsgTotal", sysMsgList.getTotal());
        sysMsgMap.put("anntMsgList", anntMsgList.getRecords());
        sysMsgMap.put("anntMsgTotal", anntMsgList.getTotal());
        result.setSuccess(true);
        result.setResult(sysMsgMap);
        return result;
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(Announcement announcement, HttpServletRequest request) {
//        // Step.1 组装查询条件
//        QueryWrapper<Announcement> queryWrapper = QueryGenerator.initQueryWrapper(announcement, request.getParameterMap());
//        //Step.2 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//        List<Announcement> pageList = sysAnnouncementService.list(queryWrapper);
//        //导出文件名称
//        mv.addObject(NormalExcelConstants.FILE_NAME, "系统通告列表");
//        mv.addObject(NormalExcelConstants.CLASS, Announcement.class);
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("系统通告列表数据", "导出人:" + user.getRealname(), "导出信息"));
//        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//        return mv;
        return null;
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
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
//        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
//            MultipartFile file = entity.getValue();// 获取上传文件对象
//            ImportParams params = new ImportParams();
//            params.setTitleRows(2);
//            params.setHeadRows(1);
//            params.setNeedSave(true);
//            try {
//                List<Announcement> listAnnouncements = ExcelImportUtil.importExcel(file.getInputStream(), Announcement.class, params);
//                for (Announcement announcementExcel : listAnnouncements) {
//                    if (announcementExcel.getDelFlag() == null) {
//                        announcementExcel.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
//                    }
//                    sysAnnouncementService.save(announcementExcel);
//                }
//                return Result.ok("文件导入成功！数据行数：" + listAnnouncements.size());
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                return Result.error("文件导入失败！");
//            } finally {
//                try {
//                    file.getInputStream().close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return Result.error("文件导入失败！");
    }

    /**
     * 同步消息
     *
     * @param anntId
     * @return
     */
    @RequestMapping(value = "/syncNotic", method = RequestMethod.GET)
    public Result<Announcement> syncNotic(@RequestParam(name = "anntId", required = false) String anntId, HttpServletRequest request) {
        Result<Announcement> result = new Result<Announcement>();
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(anntId)) {
            Announcement announcement = sysAnnouncementService.getById(anntId);
            if (announcement == null) {
                result.error500("未找到对应实体");
            } else {
                if (announcement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
                    obj.put(WebsocketConst.MSG_ID, announcement.getId());
                    obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                    webSocket.sendAllMessage(obj.toJSONString());
                } else {
                    // 2.插入用户通告阅读标记表记录
                    String userId = announcement.getUserIds();
                    if (oConvertUtils.isNotEmpty(userId)) {
                        String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
                        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                        obj.put(WebsocketConst.MSG_ID, announcement.getId());
                        obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                        webSocket.sendMoreMessage(userIds, obj.toJSONString());
                    }
                }
            }
        } else {
            obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
            obj.put(WebsocketConst.MSG_TXT, "批量设置已读");
            webSocket.sendAllMessage(obj.toJSONString());
        }
        return result;
    }
}
