package auth.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchModel {

    private String username;

    private Integer sex;

    private String realname;

    private String phone;

    private Integer status;
}
