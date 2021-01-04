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
     * 上海服务号：wx299843a93da2344c
     * appid
     *
     */
    public static final String appId = "wx299843a93da2344c";

    /**
     * 上海服务号：e5009be2e30b248b4c2995fb6afc976f
     * secrete
     *
     */
    public static final String secret = "e5009be2e30b248b4c2995fb6afc976f";

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
     * 文本消息
     */
    public static final String text = "text";

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
     * register.stcsm.sh.gov.cn
     */
    public static final String dist = "register.stcsm.sh.gov.cn/";

    /**
     * 模板url
     */
    public static final String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    /**
     * 报名审批、活动审批
     * 模板ID1
     *已换
     * dL9CkMxyoqU61zdWEOx42guz8Wm6TLwSPhfH0rSMbOo
     */
    public static final String templateId1= "mGRUYnopQhYEBgOk5EhPMVO15K4XL7zg91db7xqsayc";



    /**
     * 报名成功通知
     * 模板ID3
     * 已换
     * PNZozOisfQaTV2oh5iSIcT-vBVL0Cm9DdT7hOx74HG0
     */
    public static final String templateId3= "hnfUqKfm1Bh_3VjVeVtHHI5ldAJZz9rrg5UyyNfUtfQ";

    /**
     * 账号启用通知
     * 模板ID4
     * 已换
     * PbPYkihpfH876vqhFE-FFJiAW7ibCEWQEC8MjlWcpLU
     */
    public static final String templateId4= "Z2UQn7s39fiW5W6x35z-zHjVWma6HuTeXxnr2rR1NGM";

    /**
     * 活动开始通知
     * 模板ID6
     * TjVpxD2xHn4eZzx9q1NKeckCH7AQBhK1OMDaChptxLE
     *
     */
    public static final String templateId6= "g4VakjCcFGssoavEqanio3e6QQ5QyQrXA-KG67fOIgQ";

    /**
     * 活动审批通知
     * 模板ID7
     * 已换
     * tUR31ohWEtc3Yb2oeB9WHbqJyyBDL33TGpgarDA5CCU
     */
    public static final String templateId7= "c30KOS19WDyrt7yv1Wb2djLA_-hTFzFecQIP5vcP7Is";


    /**
     * 活动审批通过通知
     * 模板ID8
     */
    public static final String templateId8= "Gq3wn4tkisFDiHxSDquVLFSIypdenyD59P1KV5GTjCY";


    /**
     * 活动审批未通过通知
     * 模板ID9
     */
    public static final String templateId9= "sGEz_1as_a5UpdToFaUxmuXVl51WFt5NppkW2RumIQ0";

    /**
     * 报名审批未通过
     * 模板ID2
     * 已换
     */
    public static final String templateId2= "UR-vYH1q8JgtS4_eDoK_51nETbSM5oQMqDH6UIj0x0k";

    /**
     * 账号禁用通知
     * 模板ID5
     * 已换
     * yY8wIuDCsVcU695aQQ1Z3BlxXDcbXgITXrs3SvdEmZs
     */
    public static final String templateId5= "yY8wIuDCsVcU695aQQ1Z3BlxXDcbXgITXrs3SvdEmZs";

}
