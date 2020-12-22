package auth.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private String id;

    private String username;

    private String realname;

    private String password;

    private String avatar;

    private String birthday;

    private Integer sex;

    private String email;

    private String phone;

    private String orgCode;

    private transient String orgCodeTxt;

    private String workNo;

    private String post;

    private String telephone;

    private Integer activitiSync;

    private Integer userIdentity;

    private String departIds;

    private String thirdId;

    private String thirdType;

    private String relTenantIds;

    private String selectedroles;

    private String selecteddeparts;

}
