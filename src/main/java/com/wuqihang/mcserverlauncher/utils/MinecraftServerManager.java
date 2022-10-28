package com.wuqihang.mcserverlauncher.utils;

import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.MinecraftServerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wuqihang
 */
public interface MinecraftServerManager {
    MinecraftServer launch(MinecraftServerConfig config) throws MinecraftServerException;
    MinecraftServer launch(String configName) throws MinecraftServerException;

    MinecraftServer create(MinecraftServerConfig config) throws MinecraftServerException;
    MinecraftServer create(String configName) throws MinecraftServerException;

    MinecraftServer getServer(String configName);
    Set<MinecraftServer> getAllServer();
    MinecraftServerConfig getConfig(String configName);
    MinecraftServerConfig getConfig(MinecraftServer server);

    void putConfig(MinecraftServerConfig config);

    void removeConfig(String configName) throws MinecraftServerException;
    List<MinecraftServerConfig> getAllConfigs();

    Map<MinecraftServer, MinecraftServerConfig> getServerConfigMap();

    void startServer(String configName) throws IOException;

    void stopServer(String configName);

    void restartServer(String configName);

    ServerCommandProxy getCommandProxy(String configName);
}
