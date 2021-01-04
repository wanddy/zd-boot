package tech.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sf.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import tech.constant.Constant;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.wxUser.entity.WxUser;
import tech.wxUser.service.WxUserService;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取已关注微信用户信息并保存
 */
@Component
public class QuartzWxUserUtils implements Job {

    @Autowired
    private ISignUpService signUpService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        List<SignUp> list = signUpService.list(new QueryWrapper<SignUp>().eq("audit",2));
//        list.forEach(signUp -> {
//            signUp.setAudit("1");
//        });
//        signUpService.updateBatchById(list);
    }

//    private void updateQuartz(){
//        List<SignUp> list = signUpService.list();
//        list.forEach(signUp -> {
//            signUp.setAudit("1");
//        });
//        signUpService.updateBatchById(list);
//        String accessToken1 = CommonUtil.accessToken.getAccessToken();
//        JSONObject jsonObject2 = CommonUtil.httpsRequest("https://api.weixin.qq.com/cgi-bin/user/get?access_token="
//                + accessToken1, Constant.get, null);
//        Map<String, List<String>> data = (Map<String, List<String>>) jsonObject2.get("data");
//        List<String> list1 = data.get("openid");
//        ArrayList<WxUser> wxUsers = new ArrayList<>();
//        list1.forEach(str -> {
//            WxUser wxUser = new WxUser();
//            List<WxUser> openId = wxUserService.list(new QueryWrapper<WxUser>().eq("open_id", str));
//            if (openId == null || openId.size() <= 0) {
//                JSONObject jsonObject1 = CommonUtil.httpsRequest("https://api.weixin.qq.com/cgi-bin/user/info?access_token="
//                        + accessToken1 + "&openid=" + str + "&lang=zh_CN", Constant.get, null);
//                wxUser = com.alibaba.fastjson.JSONObject.parseObject(String.valueOf(jsonObject1), WxUser.class);
//                wxUser.setOpenId(str);
//                wxUser.setStatus("1");
//
//                wxUser.setDelFlag("0");
//                wxUsers.add(wxUser);
//            }
//        });
//        if(wxUsers.size()>0){
//            wxUserService.saveOrUpdateBatch(wxUsers);
//        }
//    }


}
