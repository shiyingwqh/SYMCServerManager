package com.wuqihang.symcservermanager.mcserverlauncher.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerException;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
public class MinecraftServerManagerImpl implements MinecraftServerManager {
    private final Map<Integer, MinecraftServer> servers = new Hashtable<>();

    private final Map<Integer, MinecraftServerConfig> configs = new Hashtable<>();
    private final Map<MinecraftServer, MinecraftServerConfig> serverConfigMap = new Hashtable<>();
    private final AtomicInteger ids = new AtomicInteger(0);
    private final static ObjectMapper mapper = new ObjectMapper();

    protected MinecraftServerManagerImpl() {
    }

    public void init() {
        try {
            File file = new File("configs.json");
            if (file.exists()) {
                Set<MinecraftServerConfig> minecraftServerConfigs = mapper.readValue(file, new TypeReference<Set<MinecraftServerConfig>>() {
                });
                for (MinecraftServerConfig c : minecraftServerConfigs) {
                    int id = ids.get();
                    c.setId(id);
                    configs.put(id, c);
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
                            config.setId(ids.get());
                            configs.put(config.getId(), config);
                        }
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }

    @Override
    public MinecraftServer launch(MinecraftServerConfig config) throws MinecraftServerException {
        config.setId(ids.get());
        configs.put(config.getId(), config);
        return launch(config.getId());
    }

    @Override
    public MinecraftServer launch(int configId) throws MinecraftServerException {
        MinecraftServerConfig config = configs.get(configId);
        if (config == null) {
            throw new MinecraftServerException("Config Not Found");
        }
        if (servers.containsKey(configId)) {
            throw new MinecraftServerException("Server Instance Exist");
        }
        MinecraftServerImpl minecraftServer = new MinecraftServerImpl(config);
        try {
            minecraftServer.start();
            servers.put(configId, minecraftServer);
            serverConfigMap.put(minecraftServer, config);
        } catch (IOException e) {
            configs.remove(configId);
            throw new MinecraftServerException("Server Launch Failed");
        }
        return minecraftServer;
    }

    @Override
    public MinecraftServer create(MinecraftServerConfig config) throws MinecraftServerException {
        config.setId(ids.get());
        return create(config.getId());
    }

    @Override
    public MinecraftServer create(int configId) throws MinecraftServerException {
        MinecraftServerConfig config = configs.get(configId);
        if (config == null) {
            throw new MinecraftServerException("Config Not Found");
        }
        if (servers.containsKey(configId)) {
            throw new MinecraftServerException("Server Instance Exist");
        }
        MinecraftServerImpl minecraftServer = new MinecraftServerImpl(config);
        serverConfigMap.put(minecraftServer, config);
        return minecraftServer;
    }

    @Override
    public MinecraftServer getServer(int configId) {
        return servers.get(configId);
    }

    @Override
    public List<MinecraftServer> getAllServer() {
        return servers.values().stream().toList();
    }

    @Override
    public MinecraftServerConfig getConfig(int configId) {
        return configs.get(configId);
    }

    @Override
    public MinecraftServerConfig getConfig(MinecraftServer server) {
        return serverConfigMap.get(server);
    }

    @Override
    public void putConfig(MinecraftServerConfig config) {
        config.setId(ids.get());
        configs.put(config.getId(), config);
    }

    @Override
    public void removeConfig(int configId) throws MinecraftServerException {
        if (servers.containsKey(configId) && servers.get(configId).isRunning()) {
            throw new MinecraftServerException("Can't Remove Config, Server Instance Still Running");
        }
        servers.remove(configId);
        serverConfigMap.remove(servers.getOrDefault(configId, null));
        configs.remove(configId);
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
    public void startServer(int id) throws IOException {
        MinecraftServer minecraftServer = servers.get(id);
        if (minecraftServer != null) {
            minecraftServer.start();
        }
    }

    @Override
    public void stopServer(int id) {
        MinecraftServer minecraftServer = servers.get(id);
        if (minecraftServer != null) {
            minecraftServer.stop();
        }
    }

    @Override
    public void restartServer(int id) {
        MinecraftServer minecraftServer = servers.get(id);
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
