package com.wuqihang.mcserverlauncher.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.config.ForgeMinecraftServerConfig;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.ForgeMinecraftServer;
import com.wuqihang.mcserverlauncher.server.MinecraftServerException;
import com.wuqihang.mcserverlauncher.server.MinecraftServerImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Wuqihang
 */
public class MinecraftServerManagerImpl implements MinecraftServerManager {
    private final Map<String, MinecraftServer> servers = new Hashtable<>();

    private final Map<String, MinecraftServerConfig> configs = new Hashtable<>();
    private final Map<MinecraftServer, MinecraftServerConfig> serverConfigMap = new Hashtable<>();
    private final static ObjectMapper mapper = new ObjectMapper();

    private MinecraftServerManagerImpl() {
    }

    public void init() {
        try {
            File file = new File("configs.json");
            if (file.exists()) {
                JsonNode minecraftServerConfigs = mapper.readTree(file);
                for (JsonNode node : minecraftServerConfigs) {
                    MinecraftServerConfig config;
                    if (node.has("forgeVersion")) {
                        config = new ForgeMinecraftServerConfig();
                        ((ForgeMinecraftServerConfig) config).setForgeArgs(node.get("forgeArgs").asText());
                        ((ForgeMinecraftServerConfig) config).setNewly(node.get("newly").asBoolean(false));
                        ((ForgeMinecraftServerConfig) config).setForgeVersion(node.get("forgeVersion").asText());
                    }else {
                        config = new MinecraftServerConfig();
                    }
                    config.setName(node.get("name").asText());
                    config.setJarPath(node.get("jarPath").asText());
                    config.setJavaPath(node.get("javaPath").asText());
                    config.setComment(node.get("comment").asText());
                    config.setServerHomePath(node.get("serverHomePath").asText());
                    config.setJvmParam(node.get("jvmParam").asText());
                    config.setVersion(node.get("version").asText());
                    configs.put(config.getName(), config);
                }
            } else {
                File server = new File("servers");
                if (!server.exists()) {
                    boolean mkdirs = server.mkdirs();
                }
                if (server.isDirectory()) {
                    MinecraftServerConfig config = new MinecraftServerConfig();
                    for (File listFile : Objects.requireNonNull(server.listFiles())) {
                        if (listFile.isDirectory()) {
                            File serverJar = new File(listFile, "server.jar");
                            if (!serverJar.exists()) {
                                continue;
                            }
                            config.setJavaPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
                            config.setJavaPath(new File(listFile, "server.jar").getAbsolutePath());
                            config.setServerHomePath(listFile.getAbsolutePath());
                            config.setName(listFile.getName());
                            config.setJvmParam("");
                            config.setComment("");
                            configs.put(config.getName(), config);
                        }
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }

    @Override
    public MinecraftServer launch(MinecraftServerConfig config) throws MinecraftServerException {
        configs.put(config.getName(), config);
        return launch(config.getName());
    }

    @Override
    public MinecraftServer launch(String configName) throws MinecraftServerException {
        MinecraftServer minecraftServer = create(configName);
        try {
            minecraftServer.start();
            servers.put(configName, minecraftServer);
            serverConfigMap.put(minecraftServer, configs.get(configName));
        } catch (IOException e) {
            configs.remove(configName);
            throw new MinecraftServerException("Server Launch Failed");
        }
        return minecraftServer;
    }

    @Override
    public MinecraftServer create(MinecraftServerConfig config) throws MinecraftServerException {
        return create(config.getName());
    }

    @Override
    public MinecraftServer create(String configName) throws MinecraftServerException {
        MinecraftServerConfig config = configs.get(configName);
        if (config == null) {
            throw new MinecraftServerException("Config Not Found");
        }
        if (servers.containsKey(configName)) {
            return servers.get(configName);
        }
        MinecraftServer minecraftServer;
        if (config instanceof ForgeMinecraftServerConfig) {
            minecraftServer = new ForgeMinecraftServer((ForgeMinecraftServerConfig) config);
        } else {
            minecraftServer = new MinecraftServerImpl(config);
        }
        servers.put(config.getName(), minecraftServer);
        serverConfigMap.put(minecraftServer, config);
        return minecraftServer;
    }

    @Override
    public MinecraftServer getServer(String configName) {
        return servers.get(configName);
    }

    @Override
    public Set<MinecraftServer> getAllServer() {
        return new HashSet<>(servers.values());
    }

    @Override
    public MinecraftServerConfig getConfig(String configName) {
        return configs.get(configName);
    }

    @Override
    public MinecraftServerConfig getConfig(MinecraftServer server) {
        return serverConfigMap.get(server);
    }

    @Override
    public void putConfig(MinecraftServerConfig config) {
        configs.put(config.getName(), config);
    }

    @Override
    public void removeConfig(String configName) throws MinecraftServerException {
        if (servers.containsKey(configName) && servers.get(configName).isRunning()) {
            throw new MinecraftServerException("Can't Remove Config, Server Instance Still Running");
        }
        servers.remove(configName);
        serverConfigMap.remove(servers.getOrDefault(configName, null));
        configs.remove(configName);
    }

    @Override
    public List<MinecraftServerConfig> getAllConfigs() {
        return configs.values().stream().toList();
    }

    @Override
    public Map<MinecraftServer, MinecraftServerConfig> getServerConfigMap() {
        return serverConfigMap;
    }

    @Override
    public void startServer(String configName) throws IOException {
        MinecraftServer minecraftServer = servers.get(configName);
        if (minecraftServer != null) {
            minecraftServer.start();
        }
    }

    @Override
    public void stopServer(String configName) {
        MinecraftServer minecraftServer = servers.get(configName);
        if (minecraftServer != null) {
            minecraftServer.stop();
        }
    }

    @Override
    public void restartServer(String configName) {
        MinecraftServer minecraftServer = servers.get(configName);
        if (minecraftServer != null) {
            minecraftServer.restart();
        }
    }

    public void destroy() throws IOException {
        servers.values().forEach(MinecraftServer::destroy);
        File file = new File("configs.json");
        boolean newFile = true;
        if (!file.exists()) {
            newFile = file.createNewFile();
        }
        if (newFile) {
            mapper.writeValue(file, configs.values());
        }
    }
}
