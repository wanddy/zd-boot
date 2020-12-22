package tech.wxUser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.constant.Constant;
import tech.utils.CommonUtil;
import tech.wxUser.entity.WxUser;
import tech.wxUser.mapper.WxUserMapper;
import tech.wxUser.service.WxUserService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * @Description: 微信用户表
 * @Author: zd-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService, Job {

    /**
     * 定时任务修改用户禁用状态(先启用，再禁用)
     *
     * @param context
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, -1);
        String endTime = format.format(ca.getTime());
//        启用
        List<WxUser> wxUsers = baseMapper.selectList(
                new QueryWrapper<WxUser>()
                        .eq("status", "2")
                        .eq("end_time", endTime).or());
        if (wxUsers != null && wxUsers.size() > 0) {
            wxUsers.forEach(wxUser -> {
                wxUser.setStatus("1");
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
                jsonObject.put("data", data);
                net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + CommonUtil.accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));
            });
            this.updateBatchById(wxUsers);
        }
//        连续未签到三次禁用一周
        List<WxUser> wxUserList = baseMapper.selectList(
                new QueryWrapper<WxUser>()
                        .eq("status", "1")
                        .eq("num", 3));
        ca.add(Calendar.DATE, 8);
        String time = format.format(ca.getTime());
        if (wxUserList != null && wxUserList.size() > 0) {
            wxUserList.forEach(wxUser -> {
                wxUser.setStatus("2");
                wxUser.setEndDay("7");
                wxUser.setEndTime(time);

                JSONObject jsonObject = new JSONObject();
                //用户openid
                jsonObject.put("touser", wxUser.getOpenId());
                jsonObject.put("template_id", Constant.templateId5);
                JSONObject data = new JSONObject();
                JSONObject first = new JSONObject();
                first.put("value", "您的账号已被禁用，禁用期间不允许报名活动，如有问题请联系管理员！");
                data.put("first", first);
                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", wxUser.getEndDay()+"天");
                data.put("keyword1", keyword1);

                JSONObject keyword2 = new JSONObject();
                keyword2.put("value", "连续违约"+wxUser.getNum()+"次未签到活动,系统自动禁用！");
                data.put("keyword2", keyword2);
                jsonObject.put("data", data);
                net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + CommonUtil.accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

            });
            this.updateBatchById(wxUserList);
        }

    }
}
