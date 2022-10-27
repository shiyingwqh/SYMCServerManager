package com.wuqihang.symcservermanager.services;

import com.wuqihang.mcserverlauncher.config.ForgeMinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
import com.wuqihang.mcserverlauncher.utils.ForgeServerInstaller;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Wuqihang
 */
@Component
@ConditionalOnMissingBean(MinecraftServer.class)
public class MinecraftServerConfigService implements DisposableBean {
    private final MinecraftServerDownloader downloader;
    private final ExecutorService pool;


    public MinecraftServerConfigService(MinecraftServerDownloader downloader) {
        this.downloader = downloader;
        pool = Executors.newCachedThreadPool();
    }

    public FutureTask<MinecraftServerConfig> initConfig(MinecraftServerConfig config, String version, String serverHome, @Nullable String forgeVersion) {
        FutureTask<MinecraftServerConfig> target = new FutureTask<>(() -> {
            FutureTask<Boolean> download = downloader.download(version, serverHome);
            if (config instanceof ForgeMinecraftServerConfig) {
                FutureTask<Boolean> downloadForgeInstaller = downloader.downloadForgeInstaller(version, forgeVersion, "installer", "jar", serverHome);
                if (downloadForgeInstaller.get() && download.get()) {
                    Future<ForgeMinecraftServerConfig> install = ForgeServerInstaller.install(new File(serverHome, "forge_installer.jar").getAbsolutePath(), (ForgeMinecraftServerConfig) config);
                    return install.get();
                }
            } else {
                download.get();
            }
            return config;
        });
        pool.submit(target);
        return target;
    }



    @Override
    public void destroy() throws Exception {
        pool.shutdown();
    }

    public List<String> getAllId() {
        return downloader.getAllId();
    }
}
