package org.project.backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许凭证
        config.setAllowCredentials(true);
        // 2. 明确指定允许的来源
        config.setAllowedOrigins(Collections.singletonList("http://localhost:5555"));
        // 3. 允许所有请求头
        config.setAllowedHeaders(Collections.singletonList("*"));
        // 4. 允许所有方法
        config.setAllowedMethods(Collections.singletonList("*"));
        // 5. 设置预检请求的有效期
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));

        // 设置过滤器的顺序为最高优先级
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}