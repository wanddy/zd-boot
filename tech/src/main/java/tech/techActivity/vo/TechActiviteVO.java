package tech.techActivity.vo;

import lombok.Data;
import tech.signUp.entity.SignUp;
import tech.techActivity.entity.TechActivity;

/**
 * @author liu
 */
@Data
public class TechActiviteVO {
    private SignUp signUp;
    private TechActivity techActivity;
}
