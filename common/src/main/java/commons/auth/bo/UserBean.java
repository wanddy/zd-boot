package commons.auth.bo;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserBean {
    private String username;
    private String password;
    private Set<String> roles = new HashSet<>();
    private Set<String> perms = new HashSet<>();
}
