package com.wuqihang.symcservermanager;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author Wuqihang
 */
@Component
public class MCServerLauncher {

    public Process launch(File java, File serverJar) throws IOException {
        String javaPath = java.getAbsolutePath();
        String serverJarPath = serverJar.getAbsolutePath();
        return launch(javaPath, serverJarPath, null);
    }
    public Process launch(String javaPath, String serverJarPath) throws IOException {
        return launch(javaPath, serverJarPath, null);
    }


    public Process launch(String javaPath, String serverJarPath, String others) throws IOException {
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
        return process;
    }
}
