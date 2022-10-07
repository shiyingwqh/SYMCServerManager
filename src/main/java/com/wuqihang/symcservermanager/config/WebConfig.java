package com.wuqihang.symcservermanager.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * @author Wuqihang
 */
@Configuration
public class WebConfig implements DisposableBean {

    @Override
    public void destroy() throws Exception {

    }
}
