package com.wuqihang.symcservermanager.config;

import com.wuqihang.symcservermanager.mc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author Wuqihang
 */
@Configuration
public class MCConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(MCConfigurer.class);
    @Value("${mc.single-mode:false}")
    public boolean singleMode;

    @Bean(name = "mc-server")
    @ConditionalOnProperty(prefix = "mc",name = "single-mode", havingValue = "true")
    public MinecraftServer minecraftServerNoneProcess(@Value("${mc.jar-path}") String jarPath) throws Exception {
        logger.debug("Bean mc-server Loading");
        File jar = new File(jarPath);
        if (!jar.exists() || jar.isDirectory()) {
            throw new MinecraftServerException("Server Jar Not Found");
        }
        MinecraftServerConfig config = new MinecraftServerConfig();
        config.setId(0);
        config.setName("default");
        config.setJarPath(jar.getAbsolutePath());
        config.setJavaPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        config.setComment("default");
        config.setServerHomePath(jar.getParentFile().getAbsolutePath());
        return MinecraftServerLauncher.launchMinecraftServer(config);
    }
}