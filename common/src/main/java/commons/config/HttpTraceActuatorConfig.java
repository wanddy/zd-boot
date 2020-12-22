package commons.config;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName HttpTraceActuatorConfiguration
 * @Description 开启HttpTrace
 * @Author wangchen
 * @Date 2020/9/28 3:19 下午
 **/
@Configuration
public class HttpTraceActuatorConfig {

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}
