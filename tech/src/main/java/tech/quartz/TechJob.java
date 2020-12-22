package tech.quartz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import groovy.util.logging.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import tech.constant.Constant;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.service.ITechActivityService;
import tech.techActivity.vo.AccessToken;
import tech.utils.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 定时任务--活动开始
 *
 * @Author Scott
 */
@Slf4j
public class TechJob implements Job {

	@Autowired
	private ITechActivityService techActivityService;

	@Autowired
	private ISignUpService signUpService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<TechActivity> techActivityList = techActivityService.list(
				new QueryWrapper<TechActivity>()
						.gt("start_time", simpleDateFormat.format(new Date()))
						.eq("status",1L).eq("audit",2L));
		if(techActivityList!=null && techActivityList.size()>0){
			techActivityList.forEach(techActivity -> {
				techActivity.setStatus(2L);
			});
			techActivityService.updateBatchById(techActivityList);
//			活动开始推送消息
			techActivityList.forEach(techActivity -> {
				List<SignUp> signUpList = signUpService.list(new QueryWrapper<SignUp>().eq("tech_name", techActivity.getId()).eq("audit",2));
				String templateId = Constant.templateId6;
				signUpList.forEach(signUp -> {
					AccessToken accessToken = CommonUtil.accessToken;
					JSONObject jsonObject = new JSONObject();
					//用户openid
					jsonObject.put("touser", signUp.getOpenId());
					jsonObject.put("template_id", templateId);
					jsonObject.put("url", Constant.dist + "?#/eventdetails?id=" + techActivity.getId() + "&openId=" + signUp.getOpenId());
					JSONObject data = new JSONObject();
					JSONObject first = new JSONObject();
					first.put("value", "您报名的活动即将开始，请准时入场");
					data.put("first", first);
					JSONObject keyword1 = new JSONObject();
					keyword1.put("value", signUp.getName());
					data.put("keyword1", keyword1);

					JSONObject keyword2 = new JSONObject();
					keyword2.put("value", signUp.getPhoneNumber());
					data.put("keyword2", keyword2);

					JSONObject keyword3 = new JSONObject();
					keyword3.put("value", techActivity.getHeadline());
					data.put("keyword3", keyword3);

					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					JSONObject keyword4 = new JSONObject();
					keyword4.put("value", sf.format(techActivity.getStartTime()));
					data.put("keyword4", keyword4);

					jsonObject.put("data", data);

					net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

				});
			});

		}
	}

}
