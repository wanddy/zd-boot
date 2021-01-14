package tech.signUp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.constant.Constant;
import tech.signUp.entity.SignUp;
import tech.signUp.mapper.SignUpMapper;
import tech.signUp.service.ISignUpService;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;
import tech.techActivity.service.ITechActivityService;
import tech.techActivity.vo.AccessToken;
import tech.utils.CommonUtil;
import tech.wxUser.entity.WxUser;
import tech.wxUser.service.WxUserService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 报名表
 * @Author: zd-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SignUpServiceImpl extends ServiceImpl<SignUpMapper, SignUp> implements ISignUpService, Job {

    @Autowired
    private ITechActivityService techActivityService;
    @Autowired
    private WxUserService wxUserService;

    /**
     * 审核通过发送消息
     *
     * @param signUp
     * @return
     */
    @Override
    public Result<?> edit(SignUp signUp) {
        if (this.updateById(signUp)) {
            this.getTemplate(signUp);
            return Result.ok("审核成功！");
        } else {
            return Result.error("审核失败！");
        }
    }

    /**
     * 新增报名
     * @param signUp
     * @return
     */
    @Override
    public Result<?> add(SignUp signUp) {
        WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>()
                .eq("open_id", signUp.getOpenId())
                .eq("status", 2));
        if(wxUser!=null){
            return Result.error("你的账号已被禁用，暂时不允许报名参加活动！");
        }
        this.save(signUp);
        TechActivity techActivity = techActivityService.getById(signUp.getTechName());
        AccessToken accessToken = CommonUtil.accessToken;
        JSONObject jsonObject = new JSONObject();
        //用户openid
        jsonObject.put("touser", signUp.getOpenId());
        jsonObject.put("template_id", Constant.templateId3);
        //跳转路径
        jsonObject.put("url", Constant.dist + "eventdetails?id=" + techActivity.getId() + "&openId=" + signUp.getOpenId());


        JSONObject data = new JSONObject();
        JSONObject first = new JSONObject();
        first.put("value", "您报名的活动已提交成功,等待审核中！");
        data.put("first", first);
        JSONObject keyword1 = new JSONObject();
        keyword1.put("value", signUp.getName());
        data.put("keyword1", keyword1);

        JSONObject keyword2 = new JSONObject();
        keyword2.put("value", techActivity.getHeadline());
        data.put("keyword2", keyword2);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject keyword3 = new JSONObject();
        keyword3.put("value", sf.format(techActivity.getStartTime()));
        data.put("keyword3", keyword3);

        JSONObject keyword4 = new JSONObject();
        keyword4.put("value", sf.format(techActivity.getEndTime()));
        data.put("keyword4", keyword4);

        JSONObject keyword5 = new JSONObject();
        keyword5.put("value", techActivity.getPlace());
        data.put("keyword5", keyword5);

        jsonObject.put("data", data);

        net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

        System.out.println(jsonObject1+"报名成功--------------------11111111111");

        return Result.ok("报名成功！");
    }

    /**
     * 查询表字段
     *
     * @return
     */
    @Override
    public Result<?> getFieldList() {
        List<TechField> list = baseMapper.getFieldList();
        return Result.ok(list);
    }

    /**
     * 批量审批
     *
     * @param list
     * @param audit
     * @return
     */
    @Override
    public Result<?> batchUpdate(List<String> list, String audit) {
//        String audit1;
//        if ("2".equals(audit)) {
//            audit1 = "3";
//        } else {
//            audit1 = "2";
//        }
        List<SignUp> signUpList = baseMapper.selectList(
                new QueryWrapper<SignUp>().in("id", list)
                        .and(qw->qw.in("audit", 2,3).or().isNull("audit")));
        if (signUpList != null && signUpList.size() > 0) {
            return Result.error("所选的数据中有不是审批中人员，请重新选择！");
        }
        List<SignUp> selectList = baseMapper.selectList(
                new QueryWrapper<SignUp>().in("id", list));
        for (SignUp sign : selectList) {
            TechActivity techActivity = techActivityService.getById(sign.getTechName());
            if (techActivity.getPeopleMax() != null && !techActivity.getPeopleMax().equals("0")) {
                int techName = this.count(new QueryWrapper<SignUp>()
                        .eq("tech_name", techActivity.getId()).eq("audit",2));
                if (techName >= Integer.parseInt(techActivity.getPeopleMax())) {
                    return Result.error("审批通过人数已上限，不允许审批通过！");
                }
            }
        }
        if (baseMapper.batchUpdate(list, audit)) {
            String template_id = Constant.templateId1;
            //TODO：此处消息发送
            List<SignUp> selectList1 = baseMapper.selectList(
                    new QueryWrapper<SignUp>().in("id", list));
            for (SignUp sign : selectList1) {

                TechActivity techActivity = techActivityService.getById(sign.getTechName());

                AccessToken accessToken = CommonUtil.accessToken;
                JSONObject jsonObject = new JSONObject();
                //用户openid
                jsonObject.put("touser", sign.getOpenId());
                jsonObject.put("template_id", template_id);

//不通过
                if (("3").equals(audit)) {
                    jsonObject.put("url", Constant.dist + "eventdetails?id=" + techActivity.getId() + "&openId=" + sign.getOpenId());
                }
                if (("2").equals(audit)) {
//            已通过
                    jsonObject.put("url", Constant.dist + "appointmentSuccess?id="+ techActivity.getId() + "&openId=" + sign.getOpenId());
                }
                JSONObject data = new JSONObject();
                JSONObject first = new JSONObject();

                if (("3").equals(audit)) {
                    first.put("value", "很遗憾，您报名的活动未通过审核！");
                }
                if (("2").equals(audit)) {
//            已通过
                    first.put("value", "恭喜您，您报名的活动已通过审核,点击详情可签到！");
                }

                data.put("first", first);
                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", techActivity.getHeadline());
                data.put("keyword1", keyword1);

                JSONObject keyword2 = new JSONObject();
                if (("3").equals(sign.getAudit())) {
                    keyword2.put("value", "未通过");
                }
                if (("2").equals(sign.getAudit())) {
//            已通过
                    keyword2.put("value", "已通过");
                }

                data.put("keyword2", keyword2);

                jsonObject.put("data", data);

                net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

            }
            return Result.ok("操作成功！");
        } else {
            return Result.error("操作失败！");
        }
    }

    /**
     * 导出
     * @param signUp
     * @return
     */
    @Override
    public List<SignUp> getList(SignUp signUp,LoginUser user) {
        return baseMapper.getList(signUp,user);
    }

    /**
     * 公众号登录权限查询
     * @param sign
     * @return
     */
    @Override
    public Result<?> queryList(SignUp sign) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SignUp> list = baseMapper.queryList(sign,user);
        if(list!=null && list.size()>0){
            return Result.ok(list);
        }
        return Result.error("没有查询到报名信息！");
    }

    /**
     * 发送公众号消息
     * @param signUp
     */
    private void getTemplate(SignUp signUp){
        //模板id
//        String template_id = "";
//        未通过
//        if (signUp.getAudit().equals("3")) {
         String template_id = Constant.templateId1;
//        } else if (signUp.getAudit().equals("2")) {
////            已通过
//            template_id = Constant.templateId2;
//        }
        TechActivity techActivity = techActivityService.getById(signUp.getTechName());

        AccessToken accessToken = CommonUtil.accessToken;
        JSONObject jsonObject = new JSONObject();
        //用户openid
        jsonObject.put("touser", signUp.getOpenId());
        jsonObject.put("template_id", template_id);

//不通过
        if (("3").equals(signUp.getAudit())) {
            jsonObject.put("url", Constant.dist + "eventdetails?id=" + techActivity.getId() + "&openId=" + signUp.getOpenId());
        }
        if (("2").equals(signUp.getAudit())) {
//            已通过
            jsonObject.put("url", Constant.dist + "appointmentSuccess?id="+ techActivity.getId() + "&openId=" + signUp.getOpenId());
        }
        JSONObject data = new JSONObject();
        JSONObject first = new JSONObject();

        if (("3").equals(signUp.getAudit())) {
            first.put("value", "很遗憾，您报名的活动未通过审核！");
        }
        if (("2").equals(signUp.getAudit())) {
//            已通过
            first.put("value", "恭喜您，您报名的活动已通过审核,点击详情可签到！");
        }

        data.put("first", first);
        JSONObject keyword1 = new JSONObject();
        keyword1.put("value", techActivity.getHeadline());
        data.put("keyword1", keyword1);

        JSONObject keyword2 = new JSONObject();
        if (("3").equals(signUp.getAudit())) {
            keyword2.put("value", "未通过");
        }
        if (("2").equals(signUp.getAudit())) {
//            已通过
            keyword2.put("value", "已通过");
        }

        data.put("keyword2", keyword2);

        jsonObject.put("data", data);

        net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

    }

    /**
     * 每天23:50执行：统计用户未签到次数
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //查询已审核、已开始的活动
        List<TechActivity> list = techActivityService.list(new QueryWrapper<TechActivity>()
                .eq("audit", 2).eq("status", 2));
        if(list!=null && list.size()>0){
            List<SignUp> signUpList = baseMapper.getByTech(list);
            if(signUpList!=null && signUpList.size()>0){
                signUpList.forEach(signUp -> {
                    WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>()
                            .eq("open_id", signUp.getOpenId()));
                    if(wxUser!=null){
                        if(wxUser.getNum()!=null){
                            wxUser.setNum(wxUser.getNum()+1);
                        }else{
                            wxUser.setNum(1L);
                        }
                        wxUserService.updateById(wxUser);
                        //已统计
                        signUp.setType("2");
                    }
                });
                this.updateBatchById(signUpList);
            }
        }

    }
}
