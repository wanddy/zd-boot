package commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebMvc
public class MyMvcConfig implements WebMvcConfigurer {

    @Bean

    public HttpMessageConverter<String> responseBodyStringConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<StringHttpMessageConverter> stringHttpMessageConverters = converters.stream()
                .filter(converter -> converter.getClass().equals(StringHttpMessageConverter.class))
                .map(converter -> (StringHttpMessageConverter) converter)
                .collect(Collectors.toList());

        if (stringHttpMessageConverters.isEmpty()) {
            converters.add(responseBodyStringConverter());
        } else {
            stringHttpMessageConverters.forEach(converter -> converter.setDefaultCharset(StandardCharsets.UTF_8));
        }
    }
}
