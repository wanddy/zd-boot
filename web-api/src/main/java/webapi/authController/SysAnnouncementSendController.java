package webapi.authController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import auth.discard.model.AnnouncementSendModel;
import auth.domain.notice.service.ISysAnnouncementSendService;
import auth.entity.AnnouncementSend;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import commons.constant.CommonConstant;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
* @Title: Controller
* @Description: 用户通告阅读标记表
* @Author: jeecg-boot
* @Date:  2019-02-21
* @Version: V1.0
*/
@RestController
@RequestMapping("/sys/sysAnnouncementSend")
@Slf4j
public class SysAnnouncementSendController {

   private final ISysAnnouncementSendService sysAnnouncementSendService;

   @Autowired
    public SysAnnouncementSendController(ISysAnnouncementSendService sysAnnouncementSendService) {
        this.sysAnnouncementSendService = sysAnnouncementSendService;
    }

    /**
     * 分页列表查询
    * @param announcementSend
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @GetMapping(value = "/list")
   public Result<IPage<AnnouncementSend>> queryPageList(AnnouncementSend announcementSend,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                        HttpServletRequest req) {
       Result<IPage<AnnouncementSend>> result = new Result<IPage<AnnouncementSend>>();
       QueryWrapper<AnnouncementSend> queryWrapper = new QueryWrapper<AnnouncementSend>(announcementSend);
       Page<AnnouncementSend> page = new Page<AnnouncementSend>(pageNo,pageSize);
       //排序逻辑 处理
       String column = req.getParameter("column");
       String order = req.getParameter("order");
       if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
           if("asc".equals(order)) {
               queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
           }else {
               queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
           }
       }
       IPage<AnnouncementSend> pageList = sysAnnouncementSendService.page(page, queryWrapper);
       //log.info("查询当前页："+pageList.getCurrent());
       //log.info("查询当前页数量："+pageList.getSize());
       //log.info("查询结果数量："+pageList.getRecords().size());
       //log.info("数据总数："+pageList.getTotal());
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param announcementSend
    * @return
    */
   @PostMapping(value = "/add")
   public Result<AnnouncementSend> add(@RequestBody AnnouncementSend announcementSend) {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       try {
           sysAnnouncementSendService.save(announcementSend);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param announcementSend
    * @return
    */
   @PutMapping(value = "/edit")
   public Result<AnnouncementSend> eidt(@RequestBody AnnouncementSend announcementSend) {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       AnnouncementSend announcementSendEntity = sysAnnouncementSendService.getById(announcementSend.getId());
       if(announcementSendEntity ==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = sysAnnouncementSendService.updateById(announcementSend);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id删除
    * @param id
    * @return
    */
   @DeleteMapping(value = "/delete")
   public Result<AnnouncementSend> delete(@RequestParam(name="id",required=true) String id) {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       AnnouncementSend announcementSend = sysAnnouncementSendService.getById(id);
       if(announcementSend ==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = sysAnnouncementSendService.removeById(id);
           if(ok) {
               result.success("删除成功!");
           }
       }

       return result;
   }

   /**
     *  批量删除
    * @param ids
    * @return
    */
   @DeleteMapping(value = "/deleteBatch")
   public Result<AnnouncementSend> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.sysAnnouncementSendService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @GetMapping(value = "/queryById")
   public Result<AnnouncementSend> queryById(@RequestParam(name="id",required=true) String id) {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       AnnouncementSend announcementSend = sysAnnouncementSendService.getById(id);
       if(announcementSend ==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(announcementSend);
           result.setSuccess(true);
       }
       return result;
   }

   /**
    * @功能：更新用户系统消息阅读状态
    * @param json
    * @return
    */
   @PutMapping(value = "/editByAnntIdAndUserId")
   public Result<AnnouncementSend> editById(@RequestBody JSONObject json) {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       String anntId = json.getString("anntId");
       LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
       String userId = sysUser.getId();
       LambdaUpdateWrapper<AnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
       updateWrapper.set(AnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
       updateWrapper.set(AnnouncementSend::getReadTime, new Date());
       updateWrapper.last("where annt_id ='"+anntId+"' and user_id ='"+userId+"'");
       AnnouncementSend announcementSend = new AnnouncementSend();
       sysAnnouncementSendService.update(announcementSend, updateWrapper);
       result.setSuccess(true);
       return result;
   }

   /**
    * @功能：获取我的消息
    * @return
    */
   @GetMapping(value = "/getMyAnnouncementSend")
   public Result<IPage<AnnouncementSendModel>> getMyAnnouncementSend(AnnouncementSendModel announcementSendModel,
                                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       Result<IPage<AnnouncementSendModel>> result = new Result<IPage<AnnouncementSendModel>>();
       LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
       String userId = sysUser.getId();
       announcementSendModel.setUserId(userId);
       announcementSendModel.setPageNo((pageNo-1)*pageSize);
       announcementSendModel.setPageSize(pageSize);
       Page<AnnouncementSendModel> pageList = new Page<AnnouncementSendModel>(pageNo,pageSize);
       pageList = sysAnnouncementSendService.getMyAnnouncementSendPage(pageList, announcementSendModel);
       result.setResult(pageList);
       result.setSuccess(true);
       return result;
   }

   /**
    * @功能：一键已读
    * @return
    */
   @PutMapping(value = "/readAll")
   public Result<AnnouncementSend> readAll() {
       Result<AnnouncementSend> result = new Result<AnnouncementSend>();
       LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
       String userId = sysUser.getId();
       LambdaUpdateWrapper<AnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
       updateWrapper.set(AnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
       updateWrapper.set(AnnouncementSend::getReadTime, new Date());
       updateWrapper.last("where user_id ='"+userId+"'");
       AnnouncementSend announcementSend = new AnnouncementSend();
       sysAnnouncementSendService.update(announcementSend, updateWrapper);
       result.setSuccess(true);
       result.setMessage("全部已读");
       return result;
   }
}
