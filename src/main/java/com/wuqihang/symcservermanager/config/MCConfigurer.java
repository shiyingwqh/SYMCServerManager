package com.wuqihang.symcservermanager.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.MinecraftServerException;
import com.wuqihang.mcserverlauncher.server.MinecraftServerImpl;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.mcserverlauncher.MinecraftServerLauncher;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManagerImpl;
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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    public MinecraftServerConfig minecraftServerConfig(@Value("${mc.jar-path}") String jarPath) throws MinecraftServerException, IOException {
        MinecraftServerConfig config = new MinecraftServerConfig();
        File jar = new File(jarPath);
        if (!jar.exists() || jar.isDirectory()) {
            throw new MinecraftServerException("Server Jar Not Found");
        }
        config.setName("default");
        config.setJarPath(jar.getAbsolutePath());
        config.setJavaPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        config.setComment("default");
        config.setJvmParam("");
        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().matches("version.json")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(jarFile.getInputStream(entry));
                String id = node.get("id").asText();
                config.setVersion(id);
                break;
            }
        }
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

    @Bean(initMethod = "init")
    @ConditionalOnBean(MinecraftServerLauncher.class)
    public MinecraftServerDownloader minecraftServerDownloader(@Autowired MinecraftServerLauncher launcher) throws IOException{
        return launcher.getDownloader();
    }


}
