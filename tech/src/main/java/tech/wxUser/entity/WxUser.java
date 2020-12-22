package tech.wxUser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import commons.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 微信用户表
 * @Author: zd-boot
 * @Date:   2020-11-24
 * @Version: V1.0
 */
@Data
@TableName("wx_user")
public class WxUser implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**创建人*/
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**更新人*/
    private java.lang.String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;

    /**微信用户ID*/
    private String openId;

    /**用户昵称*/
    private String nickname;

    /**用户性别*/
    @Dict(dicCode = "sex")
    private String sex;

    /**用户城市*/
    private String city;

    /**用户所在国家*/
    private String country;

    /**用户所在省份*/
    private String province;

    /**用户头像*/
    private String headimgurl;

    /**用户唯一识别id*/
    private String unionid;

    /**状态*/
    @Dict(dicCode = "wxuser_status")
    private String status;
    /**禁用天数*/
    private String endDay;
    /**禁用到期时间*/
    private String endTime;
    /**连续违约次数（未签到）*/
    private Long num;

    /**真实姓名*/
    private String name;
    /**单位名称*/
    private String unitName;
    /**手机号码*/
    private String phoneNumber;


    @Dict(dicCode = "del_flag")
    private String delFlag;
}
