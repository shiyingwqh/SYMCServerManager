package com.wuqihang.symcservermanager.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Wuqihang
 */
//@Configuration
public class MCMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefaultHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login-check");
    }
}
