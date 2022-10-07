package com.wuqihang.symcservermanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Wuqihang
 */
//@Configuration
public class WMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefaultHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login-check");
    }
}
