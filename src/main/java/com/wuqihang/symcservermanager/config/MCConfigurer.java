package com.wuqihang.symcservermanager.config;

import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerException;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerImpl;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerLauncher;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
    private boolean singleMode = false;
    @Value("${mc.auto-start:false}")
    private boolean autoStart = false;

    @Bean
    @ConditionalOnProperty(prefix = "mc", name = "single-mode", havingValue = "true")
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
        config.setJvmParam("");
        config.setServerHomePath(jar.getParentFile().getAbsolutePath());
        return config;
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnBean(MinecraftServerConfig.class)
    @ConditionalOnProperty(prefix = "mc", name = "auto-start", havingValue = "true", matchIfMissing = true)
    public MinecraftServer minecraftServerAutoStart(MinecraftServerConfig minecraftServerConfig) throws Exception {
        MinecraftServerImpl minecraftServer = new MinecraftServerImpl(minecraftServerConfig);
        minecraftServer.start();
        return minecraftServer;
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnBean(MinecraftServerConfig.class)
    @ConditionalOnProperty(prefix = "mc", name = "auto-start", havingValue = "false", matchIfMissing = true)
    public MinecraftServer minecraftServer(MinecraftServerConfig minecraftServerConfig) throws Exception {
        return new MinecraftServerImpl(minecraftServerConfig);
    }

    @Bean
    @ConditionalOnMissingBean(MinecraftServer.class)
    public MinecraftServerLauncher launcher() {
        return MinecraftServerLauncher.getInstance();
    }

    @Bean(destroyMethod = "destroy", initMethod = "init")
    @ConditionalOnBean(MinecraftServerLauncher.class)
    public MinecraftServerManagerImpl minecraftServerManager(@Autowired MinecraftServerLauncher launcher) {
        return (MinecraftServerManagerImpl) launcher.getManager();
    }

    @Bean
    @ConditionalOnBean(MinecraftServerLauncher.class)
    public MinecraftServerDownloader minecraftServerDownloader(@Autowired MinecraftServerLauncher launcher) throws IOException{
        return launcher.getDownloader();
    }


}
