//
// Source code recreated from TableTypeEntity .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.config.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component("dataBaseConfig")
@ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.smart-core")
public class DataBaseConfig {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
