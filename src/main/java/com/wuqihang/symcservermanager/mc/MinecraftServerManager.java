package com.wuqihang.symcservermanager.mc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.mc.utils.MinecraftServerLauncher;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
public class MinecraftServerManager {
    private final Map<Integer, MinecraftServer> servers = new Hashtable<>();

    private final Map<Integer, MinecraftServerConfig> configs = new Hashtable<>();
    private final Map<MinecraftServer, MinecraftServerConfig> serverConfigMap = new Hashtable<>();
    private final AtomicInteger ids = new AtomicInteger(0);
    private final static ObjectMapper mapper = new ObjectMapper();

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
                    server.mkdirs();
                }
                if (server.isDirectory()) {
                    MinecraftServerConfig.MinecraftServerConfigBuilder builder = MinecraftServerConfig.builder();
                    for (File listFile : server.listFiles()) {
                        if (listFile.isDirectory()) {
                            File serverJar = new File(listFile, "server.jar");
                            if (!serverJar.exists()) {
                                continue;
                            }

                            builder.javaPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java")
                                    .jarPath(new File(listFile, "server.jar").getAbsolutePath())
                                    .serverHomePath(listFile.getAbsolutePath())
                                    .name(listFile.getName())
                                    .otherParam("")
                                    .comment("")
                                    .id(ids.get());
                            MinecraftServerConfig config = builder.build();
                            configs.put(config.getId(), config);
                        }
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }


    public MinecraftServer getServer(int id) {
        return servers.get(id);
    }

    public List<MinecraftServer> getAllServer() {
        return new ArrayList<>(servers.values());
    }

    public void removeServer(int serverId) {
        servers.remove(serverId);
    }

    public MinecraftServer launch(MinecraftServerConfig minecraftServerConfig) throws MinecraftServerException {
        if (configs.containsKey(minecraftServerConfig.getId())) {
            if (servers.containsKey(minecraftServerConfig.getId())) {
                return null;
            }
        }
        MinecraftServer server;
        try {
            server = MinecraftServerLauncher.launchMinecraftServer(minecraftServerConfig);
        } catch (IOException e) {
            throw new MinecraftServerException("Server Launch Failed");
        }
        servers.put(ids.get(), server);
        return server;
    }

    public MinecraftServer launch(int configId) throws MinecraftServerException {
        MinecraftServerConfig minecraftServerConfig = configs.get(configId);
        if (servers.containsKey(configId)) {
            throw new MinecraftServerException("Config Already Launched");
        }
        if (minecraftServerConfig == null) {
            throw new MinecraftServerException("Config Not Found!");
        }
        MinecraftServer server;
        try {
            server = MinecraftServerLauncher.launchMinecraftServer(minecraftServerConfig);
        } catch (IOException e) {
            throw new MinecraftServerException("Server Launch Failed");
        }
        servers.put(configId, server);
        return server;
    }

    public void restartMinecraftServer(int serverId) throws MinecraftServerException {
        MinecraftServer minecraftServer = servers.get(serverId);
        if (minecraftServer == null) {
            throw new MinecraftServerException("Minecraft Server Instance Not Found");
        }
        minecraftServer.restart();
    }

    public List<MinecraftServerConfig> getAllConfig() {
        return new ArrayList<>(configs.values());
    }

    public void putConfig(MinecraftServerConfig config) throws MinecraftServerException {
        if (!config.isLegal()) {
            throw new MinecraftServerException("Config Illegal");
        }
        config.setId(ids.get());
        configs.put(config.getId(), config);
    }

    public void put(MinecraftServer minecraftServer, MinecraftServerConfig config) throws MinecraftServerException {
        if (configs.containsKey(config.getId())) {
            if (servers.containsKey(config.getId())) {
                throw new MinecraftServerException("Server Already In Manager");
            }
        }
    }

    public void destroy() throws Exception {
        for (MinecraftServer ms : servers.values()) {
            ms.destroy();
        }
        File file = new File("configs.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (PrintWriter printWriter = new PrintWriter(file, StandardCharsets.UTF_8)) {
            printWriter.write(mapper.writeValueAsString(configs.values().toArray()));
        }
    }

    public Map<MinecraftServer, MinecraftServerConfig> serverConfigMap() {
        return null;
    }
}
