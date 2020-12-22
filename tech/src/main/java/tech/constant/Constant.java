package tech.constant;

/**
 * 枚举类
 * @author liu
 */
public class Constant {

    /**
     * url
     */
    public static final String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";

    /**
     * tokenUrl
     */
    public static final String tokenUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";

    /**
     * 用户tokenUrl
     */
    public static final String userToken = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 用户信息URL
     */
    public static final String userInfo = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 二维码url
     */
    public static final String codeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";


    /**
     * 测试号：wx27fb38d49c8005c3
     * 上海服务号：wx299843a93da2344c
     * appid
     */
    public static final String appId = "wx27fb38d49c8005c3";

    /**
     * 测试号：e673879d697890764af176968e40c38b
     * 上海服务号：e5009be2e30b248b4c2995fb6afc976f
     * secrete
     */
    public static final String secret = "e673879d697890764af176968e40c38b";

    /**
     * GET请求
     */
    public static final String get = "GET";

    /**
     * POST请求
     */
    public static final String post = "POST";

    /**
     * 永久字符串二维码
     */
    public static final String QR_SCENE1 = "QR_LIMIT_STR_SCENE";

    /**
     * 临时30天字符串二维码
     */
    public static final String expire_seconds = "2592000";

    /**
     * 临时30天字符串二维码
     */
    public static final String QR_SCENE2 = "QR_STR_SCENE";

    /**
     * 微信token
     */
    public static final String token = "wsqasdfgyysdfgtezada";

    /**
     * 微信事件
     */
    public static final String event = "event";

    /**
     * 扫码
     */
    public static final String scan = "SCAN";

    /**
     * 关注
     */
    public static final String subscribe = "subscribe";

    /**
     * 取消关注
     */
    public static final String unsubscribe = "unsubscribe";

    /**
     * 前端域名
     */
    public static final String dist = "http://sbmht.natapp1.cc/";

    /**
     * 模板url
     */
    public static final String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    /**
     * 报名审批通过
     * 模板ID1
     */
    public static final String templateId1= "dL9CkMxyoqU61zdWEOx42guz8Wm6TLwSPhfH0rSMbOo";

    /**
     * 报名审批未通过
     * 模板ID2
     */
    public static final String templateId2= "UR-vYH1q8JgtS4_eDoK_51nETbSM5oQMqDH6UIj0x0k";

    /**
     * 报名成功通知
     * 模板ID3
     */
    public static final String templateId3= "PNZozOisfQaTV2oh5iSIcT-vBVL0Cm9DdT7hOx74HG0";

    /**
     * 账号启用通知
     * 模板ID4
     */
    public static final String templateId4= "PbPYkihpfH876vqhFE-FFJiAW7ibCEWQEC8MjlWcpLU";

    /**
     * 账号禁用通知
     * 模板ID5
     */
    public static final String templateId5= "yY8wIuDCsVcU695aQQ1Z3BlxXDcbXgITXrs3SvdEmZs";

    /**
     * 账号禁用通知
     * 模板ID6
     */
    public static final String templateId6= "TjVpxD2xHn4eZzx9q1NKeckCH7AQBhK1OMDaChptxLE";

}
