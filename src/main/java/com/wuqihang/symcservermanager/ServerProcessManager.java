package com.wuqihang.symcservermanager;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Wuqihang
 */
@Component
public class ServerProcessManager {
    private final Map<Long, Process> processMap;
    private final Map<Long, MinecraftServerHandler> serverHandlerMap;



    public ServerProcessManager() {
        this.processMap = new Hashtable<>();
        serverHandlerMap = new Hashtable<>();
    }

    public void addProcess(Process process) {
        processMap.put(process.pid(), process);
        serverHandlerMap.put(process.pid(), new MinecraftServerHandlerImpl(process));
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

    public MinecraftServerHandler getServerHandler(long pid) {
        return serverHandlerMap.get(pid);
    }
}
