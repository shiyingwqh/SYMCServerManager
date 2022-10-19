package com.wuqihang.symcservermanager.config;

import com.wuqihang.symcservermanager.mc.*;
import com.wuqihang.symcservermanager.mc.utils.MinecraftServerDownloader;
import com.wuqihang.symcservermanager.mc.utils.MinecraftServerLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author Wuqihang
 */
@Configuration
@ConfigurationProperties(prefix = "mc")
public class MCConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(MCConfigurer.class);
    @Value("${mc.single-mode:false}")
    public boolean singleMode;

    @Bean(name = "mc-server", destroyMethod = "destroy")
    @ConditionalOnProperty(prefix = "mc", name = "single-mode", havingValue = "true")
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

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean(MinecraftServer.class)
    public MinecraftServerManager minecraftServerManager() {
        return new MinecraftServerManager();
    }

    @Bean
    @ConditionalOnMissingBean(MinecraftServer.class)
    public MinecraftServerDownloader minecraftServerDownloader() throws IOException {
        return new MinecraftServerDownloader();
    }


}
