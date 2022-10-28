package com.wuqihang.mcserverlauncher.utils;

import com.wuqihang.mcserverlauncher.config.FabricMinecraftServerConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.jar.JarFile;

/**
 * @author Wuqihang
 */
public class FabricServerInstaller extends AbstractModServerInstaller {
    public Future<FabricMinecraftServerConfig> install(String fabricInstallerJar, boolean downloadMinecraft, FabricMinecraftServerConfig config) {
        FutureTask<FabricMinecraftServerConfig> task = new FutureTask<>(() -> {
            String serverPath = new File(config.getServerHomePath()).getAbsolutePath();
            List<String> cmd = new ArrayList<>();
            cmd.add(config.getJavaPath());
            cmd.add("-jar");
            cmd.add(fabricInstallerJar);
            cmd.add("server");
            cmd.add("-dir");
            cmd.add(serverPath);
            if (config.getVersion() != null && !config.getVersion().isBlank()) {
                cmd.add("-mcversion");
                cmd.add(config.getVersion());
            }
            if (config.getFabricVersion() != null && !config.getFabricVersion().isBlank()) {
                cmd.add("-loader");
                cmd.add(config.getFabricVersion());
            }
            if (downloadMinecraft) {
                cmd.add("-downloadMinecraft");
            }
            ProcessBuilder processBuilder = new ProcessBuilder();
            Process start = processBuilder.command(cmd).start();
            InputStream outputStream = start.getInputStream();
            try (InputStreamReader ir = new InputStreamReader(outputStream); BufferedReader br = new BufferedReader(ir)) {
                br.lines().forEach(logger::debug);
            }
            start.waitFor();
            if (start.exitValue() != 0) {
                return null;
            }
            for (File file : Objects.requireNonNull(new File(config.getServerHomePath()).listFiles())) {
                if (file.getName().matches(".*.jar")) {
                    try (JarFile jarFile = new JarFile(file.getName())) {
                        if (jarFile.getManifest().getMainAttributes().get("Main-Class").equals("net.fabricmc.loader.impl.launch.server.FabricServerLaunche\n" +
                                " r")) {
                            config.setJarPath(file.getAbsolutePath());
                        }
                    }
                }
            }
            return config;
        });
        executorService.submit(task);
        return task;
    }
}
