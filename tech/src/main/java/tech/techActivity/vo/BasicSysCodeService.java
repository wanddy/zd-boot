package tech.techActivity.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liu
 */
@Data
@Component("basicSysCodeService")
@ConfigurationProperties(prefix = "wx")
public class BasicSysCodeService {
    /**
     * 获取access_token请求的url
     */
    public String accessTokenUrl;

    public String appId;

    public String secret;
}