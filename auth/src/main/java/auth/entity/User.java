package auth.entity;

import auth.entity.base.BaseAuditingEntity;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import commons.annotation.Dict;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_user")
public class User extends BaseAuditingEntity implements Serializable {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * md5密码盐
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    //@commons.annotation.Dict(dicCode = "sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 部门code(当前选择登录部门)
     */
    private String orgCode;

    /**
     * 部门名称
     */
    private transient String orgCodeTxt;

    /**
     * 状态(1：正常  2：冻结 ）
     */
    @commons.annotation.Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 第三方登录的唯一标识
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String thirdId;

    /**
     * 第三方类型 <br>
     * （github/github，wechat_enterprise/企业微信，dingtalk/钉钉）
     */
    private String thirdType;

    /**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

    /**
     * 工号，唯一键
     */
    private String workNo;

    /**
     * 职务，关联职务表
     */
    private String post;

    /**
     * 座机号
     */
    private String telephone;


    /**
     * 身份（0 普通成员 1 上级）
     */
    private Integer userIdentity;

    /**
     * 负责部门
     */
    @commons.annotation.Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    private String departIds;

    /**
     * 多租户id配置，编辑用户的时候设置
     */
    private String relTenantIds;
}
