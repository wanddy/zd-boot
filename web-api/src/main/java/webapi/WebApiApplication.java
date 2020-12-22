package webapi;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroBeanAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@MapperScan("**.mapper")
@SpringBootApplication(scanBasePackages = {"auth.*", "webapi.*", "smartform.*","tech.*", "workflow.*", "commons.*", "smartcode.*"},
        exclude = {SecurityAutoConfiguration.class, ShiroAnnotationProcessorAutoConfiguration.class,
                ShiroAutoConfiguration.class, ShiroBeanAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class WebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApiApplication.class, args);
    }

}
