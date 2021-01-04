package tech.techActivity.service.impl;

import auth.domain.dict.service.ISysDictItemService;
import auth.domain.dict.service.ISysDictService;
import auth.domain.user.service.ISysUserService;
import auth.entity.Dict;
import auth.entity.DictItem;
import auth.entity.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import commons.util.oConvertUtils;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartcode.config.exception.BusinessException;
import tech.constant.Constant;
import tech.signUp.entity.SignUp;
import tech.signUp.service.ISignUpService;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.entity.TechField;
import tech.techActivity.mapper.TechActivityMapper;
import tech.techActivity.mapper.TechFieldMapper;
import tech.techActivity.service.ITechActivityService;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 活动表
 * @Author: zd-boot
 * @Date: 2020-12-02
 * @Version: V1.0
 */
@Service
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
    @Autowired
    private ISysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(TechActivity techActivity, List<TechField> techFieldList) {
        techActivityMapper.insert(techActivity);
        if (techFieldList != null && techFieldList.size() > 0) {
            for (TechField entity : techFieldList) {
                //外键设置
                entity.setTechId(techActivity.getId());
                techFieldMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMain(TechActivity techActivity, List<TechField> techFieldList) {

        techActivityMapper.updateById(techActivity);
        if (("1").equals(techActivity.getAuditType()) && oConvertUtils.isNotEmpty(techActivity.getDeptCode())) {
            List<SignUp> signUpList = signUpService.list(new QueryWrapper<SignUp>().eq("tech_name", techActivity.getId()));

            signUpList.forEach(signUp -> {
                signUp.setCreateBy(techActivity.getDeptCode());
            });
            signUpService.updateBatchById(signUpList);
        }

        //1.先删除子表数据
        techFieldMapper.deleteByMainId(techActivity.getId());

        //2.子表数据重新插入
        if (techFieldList != null && techFieldList.size() > 0) {
            for (TechField entity : techFieldList) {
                //外键设置
                entity.setTechId(techActivity.getId());
                techFieldMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        techFieldMapper.deleteByMainId(id);
        techActivityMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            techFieldMapper.deleteByMainId(id.toString());
            techActivityMapper.deleteById(id);
        }
    }

    @Override
    public Result<?> saveCode(TechActivity techActivity) {
        if (oConvertUtils.isEmpty(techActivity.getAudit())) {
            String url = this.createForeverTicket(techActivity);
            techActivity.setUrl(url);
            baseMapper.updateById(techActivity);
            return Result.ok("生成成功");
        } else if (("2").equals(techActivity.getAudit())) {
            String url = this.createForeverTicket(techActivity);
//		报名二维码
            if (("1").equals(techActivity.getType())) {
                techActivity.setUrl(url);
            }
//		核销二维码
            if (("2").equals(techActivity.getType())) {
                techActivity.setCancelUrl(url);
            }
            baseMapper.updateById(techActivity);
            return Result.ok("生成成功");
        }
        return Result.error("该活动未审批或审批未通过，不允许生成活动二维码！");

    }

    /**
     * 获取用户信息
     *
     * @param code
     * @return
     */
    @Override
    public String getOpenId(String code) {

        String url = String.format(Constant.userToken, Constant.appId, Constant.secret, code);

        //获取access_token
//		String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
//				"?appid=" + Constant.appId +
//				"&secret=" + Constant.secret +
//				"&code=" + code +
//				"&grant_type=authorization_code";

        net.sf.json.JSONObject resultObject = CommonUtil.httpsRequest(url, Constant.get, null);

        String infoUrl = String.format(Constant.userInfo, resultObject.getString("access_token"), resultObject.getString("openid"));

//		//请求获取userInfo
//		String infoUrl = "https://api.weixin.qq.com/sns/userinfo" +
//				"" + resultObject.getString("access_token") +
//				"" + resultObject.getString("openid") +
//				"";

        net.sf.json.JSONObject resultInfo = CommonUtil.httpsRequest(infoUrl, Constant.get, null);

        return resultObject.getString("openid");
    }

    @Override
    public Result<?> queryById(String id, String openId) {
        TechActivity techActivity = this.getById(id);
        List<TechField> techFieldList = techFieldMapper.selectList(new QueryWrapper<TechField>().eq("tech_id", id));
        if (openId != null) {
            SignUp signUp = signUpService.getOne(new QueryWrapper<SignUp>().eq("open_id", openId).eq("tech_name", id));
            if (signUp == null) {
                WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>().eq("open_id", openId).eq("del_flag", 0));
                signUp = new SignUp();
                if (wxUser != null && wxUser.getName() != null) {
                    signUp.setName(wxUser.getName());
                    signUp.setPhoneNumber(wxUser.getPhoneNumber());
                    signUp.setUnitName(wxUser.getUnitName());
                }
                signUp.setId("");
                if (techFieldList != null && techFieldList.size() > 0) {
                    techFieldList.forEach(techField -> {
                        if (("3").equals(techField.getFieldType()) || ("4").equals(techField.getFieldType())) {
                            if (oConvertUtils.isNotEmpty(techField.getFieldDict())) {
                                Dict dict = sysDictService.getOne(new QueryWrapper<Dict>().eq("dict_code", techField.getFieldDict()));
                                List<DictItem> dictItemList = sysDictItemService.list(new QueryWrapper<DictItem>().eq("dict_id", dict.getId()));
                                if (oConvertUtils.isNotEmpty(techField.getTest())) {
                                    List<DictItem> itemList = dictItemList.stream().filter(dictItem -> dictItem.getItemValue().equals(techField.getTest())).collect(Collectors.toList());
                                    techField.setItemText(itemList.get(0).getItemText());
                                }
                                techField.setDictList(dictItemList);
                            }
                        }
                    });
                }
                signUp.setTechFieldList(techFieldList);
            } else {
                if (oConvertUtils.isNotEmpty(signUp.getFieldTest())) {
                    JSONArray jsonArray = JSONArray.fromObject(signUp.getFieldTest());
                    List<TechField> fields = JSONObject.parseArray(jsonArray.toString(), TechField.class);
                    fields.forEach(techField -> {
                        if (("3") .equals(techField.getFieldType())|| ("4").equals(techField.getFieldType())) {
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
            }
            techActivity.setSignUp(signUp);
        }
        return Result.ok(techActivity);
    }

    /**
     * 拦截微信动作
     *
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
            if ("管理员登录".equals(content)) {
                // 发送文本消息
                returnMap.put("ToUserName", fromUserName);
                returnMap.put("FromUserName", toUserName);
                returnMap.put("CreateTime", (new Date()).getTime() + "");
                returnMap.put("MsgType", "text");
                returnMap.put("Content", "http://register.stcsm.sh.gov.cn/login");
                encryptMsg = WxUtil.mapToXml(returnMap);
                printWriter.print(encryptMsg);
            }
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
                    if (split[1].equals("1")) {
                        returnMap.put("Content", "活动名称:" +techActivity.getHeadline() +"\n\r"+"点击链接报名:"+split[0]);
                    } else {
                        returnMap.put("Content", "活动名称:" + techActivity.getHeadline() + "\n\r" + "<a href='" + Constant.dist + "appointmentSuccess" + "&openId=" + fromUserName + "'>点击签到</a>");
                    }

                    //明文
                    encryptMsg = WxUtil.mapToXml(returnMap);
                    System.out.print(encryptMsg);
                    printWriter.print(encryptMsg);
                } else if (Constant.subscribe.equals(event)) {
                    // 订阅事件 或 未关注扫描二维码事件
                    // 返回消息时ToUserName的值与FromUserName的互换
                    WxUser wxUser = wxUserService.getOne(new QueryWrapper<WxUser>().eq("open_id", fromUserName));
                    //保存关注用户信息
                    if (wxUser == null) {
                        //获取用户详细信息
                        String infoUrl = String.format(Constant.userInfo, CommonUtil.accessToken.getAccessToken(), fromUserName);
                        net.sf.json.JSONObject resultInfo = CommonUtil.httpsRequest(infoUrl, Constant.get, null);
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
                    if (wxUser != null) {
                        wxUser.setDelFlag("1");
                        wxUserService.updateById(wxUser);
                    }
                } else if ("CLICK".equals(event)) {
                    // 点击菜单跳转链接时的事件推送
                    // 文本消息
                    String eventKey = map.get("EventKey");
                    if (eventKey != null ) {
                        returnMap.put("ToUserName", fromUserName);
                        returnMap.put("FromUserName", toUserName);
                        returnMap.put("CreateTime", (new Date()).getTime() + "");
                        returnMap.put("MsgType", "text");
                        if("waizhuan".equals(eventKey)){
                            returnMap.put("Content", "外专服务事项介绍：负责外国人来华工作许可、外国高端人才确认函、"+
                                    "外国专家短期来华邀请函、外籍高层次人才资格认定等市外专局授权行政审批事项的市级窗口受理审批以及各区受理点的审批工作。"+
                                    "开展外国专家服务体系、引进国外智力等研究与支撑工作，协助外专项目计划受理评审工作。");
                        }

                        if("zonghezixun".equals(eventKey)){
                            returnMap.put("Content", "综窗服务介绍：负责市科委政府信息现场查阅、政府信息公开申请受理；"+
                                    "市科委科技计划项目政策咨询、市科委行政事项以及本市科技创新政策对外咨询服务。");
                        }
                        if("zhongxinjieshao".equals(eventKey)){
                            returnMap.put("Content", "上海市科技政务服务中心介绍：上海市科技政务服务中心由上海市科学技术委员会办公室牵头负责，"+
                                    "依托上海市科技信息中心负责日常运行，并对参与政务服务的各个机构和部门进行管理。政务服务中心将实行科学的管理制度，"+
                                    "以严密的工作流程，严格的考核制度，严肃的工作纪律，严谨的工作作风，为社会公众提供优质高效的服务。");
                        }
                        if("fuwudidian".equals(eventKey)){
                            returnMap.put("Content", "服务地点：上海市徐汇区中山西路1525号技贸大厦一楼");
                        }
                        if("fuwushijian".equals(eventKey)){
                            returnMap.put("Content", "服务时间：周一至周五  上午09:00-11:30（最晚取号时间11:15），下午13:30-16:30（最晚取号时间16:15）");
                        }
                        encryptMsg = WxUtil.mapToXml(returnMap);
                        printWriter.print(encryptMsg);
                    }

//					System.out.println("aaa");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
        }

    }

    @Override
    public void download(String id, String type, HttpServletRequest request, HttpServletResponse response) throws MalformedURLException {
        TechActivity techActivity = baseMapper.selectById(id);
        String activityUrl = new String();
        if (type.equals("1")) {
            if (StringUtils.isNullOrEmpty(techActivity.getUrl())) {
                return;
            }
            activityUrl = techActivity.getUrl();
        }

        if (type.equals("2")) {
            if (StringUtils.isNullOrEmpty(techActivity.getCancelUrl())) {
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
                outputStream.write(buffer, 0, byteread);
            }
            outputStream.flush();
            outputStream.close();
            inStream.close();
        } catch (NoSuchProviderException | KeyManagementException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IPage<TechActivity> getPage(Page<TechActivity> page, TechActivity techActivity, LoginUser sysUser) {
        return baseMapper.getPage(page, techActivity, sysUser);
    }

    @Override
    public void getTemplate(TechActivity techActivity, String templateId, String value) {
        List<String> list = Arrays.asList(techActivity.getDeptCode().split(","));
        List<User> users = sysUserService.list(new QueryWrapper<User>().in("username", list));
        users.forEach(user -> {
            if (oConvertUtils.isNotEmpty(user.getThirdId())) {
                AccessToken accessToken = CommonUtil.accessToken;
                JSONObject jsonObject = new JSONObject();
                //用户openid
                jsonObject.put("touser", user.getThirdId());
                jsonObject.put("template_id", templateId);
//                        jsonObject.put("url", Constant.dist + "?/eventdetails?id=" + techActivity.getId() + "&openId=" + user.getThirdId());
                JSONObject data = new JSONObject();
                JSONObject first = new JSONObject();
                first.put("value", value);
                data.put("first", first);
                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", "活动审批");
                data.put("keyword1", keyword1);

                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                JSONObject keyword2 = new JSONObject();
                keyword2.put("value", techActivity.getHeadline());
                data.put("keyword2", keyword2);

                JSONObject keyword3 = new JSONObject();
                keyword3.put("value", sf.format(new Date()));
                data.put("keyword3", keyword3);

//                JSONObject keyword4 = new JSONObject();
//                keyword4.put("value", techActivity.getPlace());
//                data.put("keyword4", keyword4);

                jsonObject.put("data", data);

                net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

            }
        });
    }

    /**
     * 公众号微信列表
     *
     * @param headline
     * @param place
     * @param startTime
     * @return
     */
    @Override
    public List<TechActivity> appList(String headline, String place, String startTime, String status) {
        return baseMapper.appList(headline, place, startTime, status);
    }

    @Override
    public void getTemplate1(TechActivity techActivity, String templateId, String value) {
        List<String> list = Arrays.asList(techActivity.getDeptCode().split(","));
        List<User> users = sysUserService.list(new QueryWrapper<User>().in("username", list));
        users.forEach(user -> {
            if (oConvertUtils.isNotEmpty(user.getThirdId())) {
                AccessToken accessToken = CommonUtil.accessToken;
                JSONObject jsonObject = new JSONObject();
                //用户openid
                jsonObject.put("touser", user.getThirdId());
                jsonObject.put("template_id", templateId);
//                        jsonObject.put("url", Constant.dist + "?/eventdetails?id=" + techActivity.getId() + "&openId=" + user.getThirdId());
                JSONObject data = new JSONObject();
                JSONObject first = new JSONObject();
                first.put("value", value);
                data.put("first", first);

                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", techActivity.getHeadline());
                data.put("keyword1", keyword1);

                JSONObject keyword2 = new JSONObject();
                if (("2").equals(techActivity.getAudit())) {
                    keyword2.put("value", "活动审批已通过");
                }
                if (("3").equals(techActivity.getAudit())) {
                    keyword2.put("value", "活动审批未通过");
                }
                data.put("keyword2", keyword2);

//
//                JSONObject keyword3 = new JSONObject();
//                keyword3.put("value", new Date());
//                data.put("keyword3", keyword3);

//                JSONObject keyword4 = new JSONObject();
//                keyword4.put("value", techActivity.getPlace());
//                data.put("keyword4", keyword4);

                jsonObject.put("data", data);

                net.sf.json.JSONObject jsonObject1 = CommonUtil.httpsRequest(Constant.templateUrl + accessToken.getAccessToken(), "POST", JSONObject.toJSONString(jsonObject));

            }
        });
    }


    /**
     * 是否关注了微信公众号
     *
     * @param openId
     * @return Boolean ,true：已关注，false：未关注
     * @throws BusinessException
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
            if (("1").equals(techActivity.getType())) {
                scene.put("scene_str", Constant.dist + "eventdetails?id=" + techActivity.getId() + "&type=1");
            }
//			核销地址
            if (("2").equals(techActivity.getType())) {
                scene.put("scene_str", Constant.dist + "audit?id=" + techActivity.getId() + "&type=2");
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
