package com.wuqihang.symcservermanager.mcserverlauncher.utils;

import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author wuqihang
 */
public interface MinecraftServerManager {
    MinecraftServer launch(MinecraftServerConfig config) throws MinecraftServerException;
    MinecraftServer launch(int configId) throws MinecraftServerException;

    MinecraftServer create(MinecraftServerConfig config) throws MinecraftServerException;
    MinecraftServer create(int configId) throws MinecraftServerException;

    MinecraftServer getServer(int configId);
    List<MinecraftServer> getAllServer();
    MinecraftServerConfig getConfig(int configId);
    MinecraftServerConfig getConfig(MinecraftServer server);

    void putConfig(MinecraftServerConfig config);

    void removeConfig(int configId) throws MinecraftServerException;
    List<MinecraftServerConfig> getAllConfigs();

    Map<MinecraftServer, MinecraftServerConfig> getServerConfigMap();

    void startServer(int id) throws IOException;

    void stopServer(int id);

    void restartServer(int id);
}
