package com.wuqihang.symcservermanager;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Wuqihang
 */
@Component
public class MCSServerLauncher {
    private final ProcessManager processManager;

    public MCSServerLauncher(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public long launch(File java, File serverJar) throws IOException {
        String javaPath = java.getAbsolutePath();
        String serverJarPath = serverJar.getAbsolutePath();
        return launch(javaPath, serverJarPath, null);
    }
    public long launch(String javaPath, String serverJarPath) throws IOException {
        return launch(javaPath, serverJarPath, null);
    }


    public long launch(String javaPath, String serverJarPath, String others) throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append(javaPath).append(" ");
        cmd.append("-jar").append(" ");
        cmd.append(serverJarPath).append(" ");
        if (others != null) {
            cmd.append(others);
        }
        long pid;
        Process process = Runtime.getRuntime().exec(cmd.toString());
        pid = process.pid();
        processManager.addProcess(process);
        return pid;
    }

    public void shutdown(long pid) {
        processManager.destroy(pid);
    }

    public Process getProcess(long pid) {
        return processManager.getProcess(pid);
    }
}
