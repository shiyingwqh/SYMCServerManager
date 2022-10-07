package com.wuqihang.symcservermanager;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Wuqihang
 */
@Component
public class ProcessManager {
    private final Map<Long, Process> map;


    public ProcessManager() {
        this.map = new Hashtable<>();
    }

    public void addProcess(Process process) {
        map.put(process.pid(), process);
    }

    public Process getProcess(long pid) {
        return map.getOrDefault(pid, null);
    }

    public void destroy(long pid) {
        Process process = map.get(pid);
        if (process != null) {
            process.destroy();
            map.remove(pid);
        }
    }

    public List<Long> getAllPid() {
        return new ArrayList<>(map.keySet());
    }
}
