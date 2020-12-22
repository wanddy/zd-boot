package auth.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;

    private String username;

    private String realname;

    private String password;

    private String salt;

    private String avatar;

    private Date birthday;

    private Integer sex;

    private String email;

    private String phone;

    private String orgCode;

    private transient String orgCodeTxt;

    private Integer status;

    private Integer delFlag;

    private String workNo;

    private String post;

    private String telephone;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Integer activitiSync;

    private Integer userIdentity;

    private String departIds;

    private String thirdId;

    private String thirdType;

    private String relTenantIds;

}
