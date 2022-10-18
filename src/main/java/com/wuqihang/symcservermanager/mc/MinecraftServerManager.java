package com.wuqihang.symcservermanager.mc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
@Component
@ConditionalOnProperty(prefix = "mc",name = "single-mode", havingValue = "false")
public class MinecraftServerManager implements DisposableBean {
    private final Map<Long, Process> processMap;
    private final Map<Long, MinecraftServer> minecraftServerMap;

    private final Map<Integer, MinecraftServerConfig> configs = new Hashtable<>();
    private final AtomicInteger configId = new AtomicInteger(0);
    private final static ObjectMapper mapper = new ObjectMapper();


    public MinecraftServerManager() {
        this.processMap = new Hashtable<>();
        minecraftServerMap = new Hashtable<>();
        try {
            File file = new File("configs.json");
            if (file.exists()) {
                Set<MinecraftServerConfig> minecraftServerConfigs = mapper.readValue(file, new TypeReference<Set<MinecraftServerConfig>>() {
                });
                for (MinecraftServerConfig c : minecraftServerConfigs) {
                    int id = configId.get();
                    c.setId(id);
                    configs.put(id, c);
                }
            } else {
                File server = new File("servers");
                if (!server.exists()) {
                    server.mkdirs();
                }
                if (server.isDirectory()) {
                    for (File listFile : server.listFiles()) {
                        if (listFile.isDirectory()) {
                            File serverJar = new File(listFile, "server.jar");
                            if (!serverJar.exists()) {
                                continue;
                            }
                            MinecraftServerConfig config = new MinecraftServerConfig();
                            config.setJavaPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
                            config.setJarPath(new File(listFile, "server.jar").getAbsolutePath());
                            config.setServerHomePath(listFile.getAbsolutePath());
                            config.setName(listFile.getName());
                            config.setOtherParam("");
                            config.setComment("");
                            config.setId(configId.get());
                            configs.put(config.getId(), config);
                        }
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }

    public void addProcess(Process process) {
        processMap.put(process.pid(), process);
        minecraftServerMap.put(process.pid(), new MinecraftServerImpl(process));
    }

    public Process getProcess(long pid) {
        return processMap.getOrDefault(pid, null);
    }

    public void destroy(long pid) {
        Process process = processMap.get(pid);
        if (process != null) {
            process.destroy();
            processMap.remove(pid);
        }
    }

    public List<Long> getAllPid() {
        return new ArrayList<>(processMap.keySet());
    }

    public MinecraftServer getServer(long pid) {
        return minecraftServerMap.get(pid);
    }

    public void removeProcess(Process process) {
        long pid = process.pid();
        processMap.remove(pid);
        minecraftServerMap.remove(pid);
    }

    public void flush() {
        for (Process p : processMap.values()) {
            if (!p.isAlive()) {
                minecraftServerMap.remove(p.pid());
                processMap.remove(p.pid());
            }
        }
    }

    public List<Process> getAllProcess() {
        flush();
        return processMap.values().stream().toList();
    }

    public MinecraftServer launch(MinecraftServerConfig minecraftServerConfig) throws MinecraftServerException {
        MinecraftServer server = null;
        try {
            server = MinecraftServerLauncher.launchMinecraftServer(minecraftServerConfig);
        } catch (IOException e) {
            throw new MinecraftServerException("Server Launch Failed");
        }
        processMap.put(server.getProcess().pid(), server.getProcess());
        minecraftServerMap.put(server.getProcess().pid(), server);
        return server;
    }

    public MinecraftServer launch(int configId) throws MinecraftServerException {
        MinecraftServerConfig minecraftServerConfig = configs.get(configId);
        if (minecraftServerConfig == null) {
            throw new MinecraftServerException("Config Not Found!");
        }
        return launch(minecraftServerConfig);
    }

    public List<MinecraftServerConfig> getAllConfig() {
        return configs.values().stream().toList();
    }

    @Override
    public void destroy() throws Exception {
        for (MinecraftServer ms : minecraftServerMap.values()) {
            if (ms.isRunning()) {
                ms.destroy();
            }
        }
        File file = new File("configs.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (PrintWriter printWriter = new PrintWriter(file, StandardCharsets.UTF_8)) {
            printWriter.write(mapper.writeValueAsString(configs.values().toArray()));
        }
    }

}
