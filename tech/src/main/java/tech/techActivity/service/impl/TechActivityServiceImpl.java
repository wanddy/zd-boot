package tech.techActivity.service.impl;

import auth.domain.dict.service.ISysDictItemService;
import auth.domain.dict.service.ISysDictService;
import auth.domain.dict.service.impl.SysDictServiceImpl;
import auth.entity.Dict;
import auth.entity.DictItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import commons.api.vo.Result;
import commons.util.oConvertUtils;
import net.sf.json.JSONArray;
import org.apache.poi.util.StringUtil;
import smartcode.config.exception.BusinessException;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;
import tech.techActivity.mapper.TechFieldMapper;
import tech.techActivity.mapper.TechActivityMapper;
import tech.techActivity.service.ITechActivityService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tech.constant.Constant;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.techActivity.vo.AccessToken;
import tech.utils.CommonUtil;
import tech.utils.HttpClientUtil;
import tech.utils.MyX509TrustManager;
import tech.utils.WxUtil;
import tech.wxUser.entity.WxUser;
import tech.wxUser.service.WxUserService;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date:   2020-12-02
 * @Version: V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TechActivityServiceImpl extends ServiceImpl<TechActivityMapper, TechActivity> implements ITechActivityService {

	@Autowired
	private TechActivityMapper techActivityMapper;
	@Autowired
	private TechFieldMapper techFieldMapper;
	@Autowired
	private WxUserService wxUserService;
	@Autowired
	private ISignUpService signUpService;
	@Autowired
	private ISysDictService sysDictService;
	@Autowired
	private ISysDictItemService sysDictItemService;

	@Override
	@Transactional
	public void saveMain(TechActivity techActivity, List<TechField> techFieldList) {
		techActivityMapper.insert(techActivity);
		if(techFieldList!=null && techFieldList.size()>0) {
			for(TechField entity:techFieldList) {
				//外键设置
				entity.setTechId(techActivity.getId());
				techFieldMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void updateMain(TechActivity techActivity,List<TechField> techFieldList) {
		techActivityMapper.updateById(techActivity);

		//1.先删除子表数据
		techFieldMapper.deleteByMainId(techActivity.getId());

		//2.子表数据重新插入
		if(techFieldList!=null && techFieldList.size()>0) {
			for(TechField entity:techFieldList) {
				//外键设置
				entity.setTechId(techActivity.getId());
				techFieldMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		techFieldMapper.deleteByMainId(id);
		techActivityMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			techFieldMapper.deleteByMainId(id.toString());
			techActivityMapper.deleteById(id);
		}
	}

	@Override
	public Result<?> saveCode(TechActivity techActivity) {
		if(techActivity.getAudit().equals("2")){
			String url = this.createForeverTicket(techActivity);
//		报名二维码
			if(techActivity.getType().equals("1")){
				techActivity.setUrl(url);
			}
//		核销二维码
			if(techActivity.getType().equals("2")){
				techActivity.setCancelUrl(url);
			}
			baseMapper.updateById(techActivity);
			return Result.ok("生成成功");
		}
		return Result.error("该活动未审批或审批未通过，不允许生成活动二维码！");

	}

	/**
	 * 获取用户信息
	 * @param code
	 * @return
	 */
	@Override
	public String getOpenId(String code) {

		String url = String.format(Constant.userToken,Constant.appId, Constant.secret,code);

		//获取access_token
//		String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
//				"?appid=" + Constant.appId +
//				"&secret=" + Constant.secret +
//				"&code=" + code +
//				"&grant_type=authorization_code";

		net.sf.json.JSONObject resultObject = CommonUtil.httpsRequest(url,Constant.get,null);

		String infoUrl = String.format(Constant.userInfo,resultObject.getString("access_token"), resultObject.getString("openid"));

//		//请求获取userInfo
//		String infoUrl = "https://api.weixin.qq.com/sns/userinfo" +
//				"" + resultObject.getString("access_token") +
//				"" + resultObject.getString("openid") +
//				"";

		net.sf.json.JSONObject resultInfo = CommonUtil.httpsRequest(infoUrl,Constant.get,null);

		return resultObject.getString("openid");
	}

	@Override
	public Result<?> queryById(String id, String openId) {
		TechActivity techActivity = this.getById(id);
		List<TechField> techFieldList = techFieldMapper.selectList(new QueryWrapper<TechField>().eq("tech_id", id));
		if(openId!=null){
			SignUp signUp = signUpService.getOne(new QueryWrapper<SignUp>().eq("open_id", openId).eq("tech_name", id));
			if(signUp == null){
				WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>().eq("open_id", openId).eq("del_flag", 0));
				signUp = new SignUp();
				signUp.setName(wxUser.getName());
				signUp.setPhoneNumber(wxUser.getPhoneNumber());
				signUp.setUnitName(wxUser.getUnitName());
				signUp.setId("");
				if(techFieldList!=null && techFieldList.size()>0){
					techFieldList.forEach(techField -> {
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
				}
				signUp.setTechFieldList(techFieldList);
			}else {
				JSONArray jsonArray = JSONArray.fromObject(signUp.getFieldTest());
				List<TechField> fields = JSONObject.parseArray(jsonArray.toString(), TechField.class);
				if(fields!=null && fields.size()>0){
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
			}
			techActivity.setSignUp(signUp);
		}
		return Result.ok(techActivity);
	}

	/**
	 * 拦截微信动作
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		Map<String, String> returnMap = new HashMap<>();
		try {
			Map<String, String> map = WxUtil.xmlToMap(request);
			String toUserName = map.get("ToUserName");
			String fromUserName = map.get("FromUserName");
			String msgType = map.get("MsgType");
			String content = map.get("Content");
			String encryptMsg = null;
			if (Constant.event.equals(msgType)) {
				String event = map.get("Event");
//                二维码扫描事件
				if (Constant.scan.equals(event)) {
					String eventKey = map.get("EventKey");
					String[] split = eventKey.split("&type=");
					String id = split[0].split("id=")[1];
					TechActivity techActivity = this.getById(id);
					returnMap.put("ToUserName", fromUserName);
					returnMap.put("FromUserName", toUserName);
					returnMap.put("CreateTime", (new Date()).getTime() + "");
					returnMap.put("MsgType", "text");
					if(split[1].equals("1")){
						returnMap.put("Content", "活动名称:"+techActivity.getHeadline()+"\n\r"+"<a href='"+split[0]+"&openId="+fromUserName+"'>点击报名</a>");
					}else {
						returnMap.put("Content", "活动名称:"+techActivity.getHeadline()+"\n\r"+"<a href='"+split[0]+"&openId="+fromUserName+"'>点击签到</a>");
					}

					//明文
					encryptMsg =WxUtil.mapToXml(returnMap);
					System.out.print(encryptMsg);
					printWriter.print(encryptMsg);
				} else if (Constant.subscribe.equals(event)) {
					// 订阅事件 或 未关注扫描二维码事件
					// 返回消息时ToUserName的值与FromUserName的互换
					WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>().eq("open_id", fromUserName));
					//保存关注用户信息
					if(wxUser == null){
						//获取用户详细信息
						String infoUrl = String.format(Constant.userInfo,CommonUtil.accessToken.getAccessToken(), fromUserName);
						net.sf.json.JSONObject resultInfo = CommonUtil.httpsRequest(infoUrl,Constant.get,null);
						wxUser = JSONObject.parseObject(String.valueOf(resultInfo), WxUser.class);
						wxUser.setOpenId(fromUserName);
						wxUser.setStatus("1");
					}
					wxUser.setDelFlag("0");
					wxUserService.saveOrUpdate(wxUser);

					returnMap.put("ToUserName", fromUserName);
					returnMap.put("FromUserName", toUserName);
					returnMap.put("CreateTime", (new Date()).getTime() + "");
					returnMap.put("MsgType", "text");
					returnMap.put("Content", "欢迎进入上海科技报名服务公众号");
					//明文
					encryptMsg = WxUtil.mapToXml(returnMap);
					printWriter.print(encryptMsg);

				} else if (Constant.unsubscribe.equals(event)) {
					// 取消订阅事件
					WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>().eq("open_id", fromUserName));
					if(wxUser!=null){
						wxUser.setDelFlag("1");
						wxUserService.updateById(wxUser);
					}
				}
//				else if ("VIEW".equals(event)) {
//					// 点击菜单跳转链接时的事件推送
//					System.out.println("aaa");
//				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			printWriter.close();
		}

	}

	@Override
	public void download(String id,String type, HttpServletRequest request, HttpServletResponse response) throws MalformedURLException {
		TechActivity techActivity = baseMapper.selectById(id);
		String activityUrl = new String();
		if(type.equals("1")){
			if(StringUtils.isNullOrEmpty(techActivity.getUrl())){
				return;
			}
			activityUrl = techActivity.getUrl();
		}

		if(type.equals("2")){
			if(StringUtils.isNullOrEmpty(techActivity.getCancelUrl())){
				return;
			}
			activityUrl = techActivity.getCancelUrl();
		}
		int bytesum = 0;
		int byteread = 0;
		URL url = new URL(activityUrl);
		try {
			//跳过证书验证
			TrustManager[] tm = {new MyX509TrustManager()};
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(Constant.get);
			InputStream inStream = conn.getInputStream();
			OutputStream outputStream = response.getOutputStream();
			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				outputStream.write(buffer,0,byteread);
			}
			outputStream.flush();
			outputStream.close();
			inStream.close();
		} catch (NoSuchProviderException | KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 是否关注了微信公众号
	 * @param openId
	 * @return
	 * @throws BusinessException
	 * @return Boolean ,true：已关注，false：未关注
	 */
	public Boolean isXSNSubscribe(String openId) throws BusinessException {
		// 微信的access_token
		String access_token = CommonUtil.accessToken.getAccessToken();
		// 请求地址
		String url = String.format("https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN", access_token, openId);
		String result = HttpClientUtil.doGet(url, null);
		JSONObject json = JSON.parseObject(result);
		return "1".equals(json.getString("subscribe"));
	}

	/**
	 * 创建永久二维码(字符串)
	 *
	 * @param techActivity
	 * @return
	 */
	private String createForeverTicket(TechActivity techActivity) {
		String qrcodeUrl = "";
		try {
			AccessToken accessToken = CommonUtil.accessToken;
			JSONObject jsonObject = new JSONObject();
			JSONObject actionInfo = new JSONObject();
			JSONObject scene = new JSONObject();
			jsonObject.put("expire_seconds", Constant.expire_seconds);
			jsonObject.put("action_name", Constant.QR_SCENE2);
			//			报名地址
			if(techActivity.getType().equals("1")){
				scene.put("scene_str", "http://sbmht.natapp1.cc/#/eventdetails?id="+techActivity.getId()+"&type=1");
			}
//			核销地址
			if(techActivity.getType().equals("2")){
				scene.put("scene_str", "http://sbmht.natapp1.cc/#/audit?id="+techActivity.getId()+"&type=2");
			}
			actionInfo.put("scene", scene);
			jsonObject.put("action_info", actionInfo);
			String url = Constant.tokenUrl + accessToken.getAccessToken();
			net.sf.json.JSONObject resultJson = CommonUtil.httpsRequest(url, "POST", jsonObject.toJSONString());
			String ticket = resultJson.getString("ticket");
			qrcodeUrl = Constant.codeUrl + URLEncoder.encode(ticket, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(qrcodeUrl);
		return qrcodeUrl;
	}

}
