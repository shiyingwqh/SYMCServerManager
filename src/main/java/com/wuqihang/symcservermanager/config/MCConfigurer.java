package com.wuqihang.symcservermanager.config;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mc.MinecraftServerException;
import com.wuqihang.symcservermanager.mc.MinecraftServerManager;
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
    private boolean singleMode;
    @Value("${mc.auto-start:false}")
    private boolean autoStart;

    @Bean
    public MinecraftServerConfig minecraftServerConfig(@Value("${mc.jar-path}") String jarPath) throws MinecraftServerException {
        MinecraftServerConfig config = new MinecraftServerConfig();
        File jar = new File(jarPath);
        if (!jar.exists() || jar.isDirectory()) {
            throw new MinecraftServerException("Server Jar Not Found");
        }
        config.setId(0);
        config.setName("default");
        config.setJarPath(jar.getAbsolutePath());
        config.setJavaPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        config.setComment("default");
        config.setServerHomePath(jar.getParentFile().getAbsolutePath());
        return config;
    }

    @Bean(name = "mc-server", destroyMethod = "destroy")
    @ConditionalOnProperty(prefix = "mc", name = "auto-start", havingValue = "true")
    public MinecraftServer minecraftServerNoneProcess(MinecraftServerConfig minecraftServerConfig) throws Exception {
        return MinecraftServerLauncher.launchMinecraftServer(minecraftServerConfig);
    }

    @Bean(destroyMethod = "destroy", initMethod = "init")
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
