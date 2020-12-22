package tech.techActivity.vo;

import lombok.Data;

/**
 * 微信Token
 * @author liu
 */
@Data
public class AccessToken {

    private String accessToken;

    /**
     * 凭证有效期，单位：秒
     */
    private int expiresIn;
}
