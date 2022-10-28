package com.wuqihang.mcserverlauncher.server;

import com.wuqihang.mcserverlauncher.config.ForgeMinecraftServerConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Wuqihang
 */
public class ForgeMinecraftServer extends BaseModMinecraftServer {



    public ForgeMinecraftServer(ForgeMinecraftServerConfig config) {
        super(config);
    }

    @Override
    protected void launch() throws IOException {
        ForgeMinecraftServerConfig forgeConfig = (ForgeMinecraftServerConfig) config;
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add(config.getJavaPath());
        if (config.getJvmParam() != null && !config.getJvmParam().isBlank()) {
            cmd.addAll(Arrays.asList(config.getJvmParam().split("\\s+")));
        }
        if (forgeConfig.isNewly()) {
            cmd.addAll(Arrays.asList(forgeConfig.getForgeArgs().split("\\s+")));
        } else {
            cmd.add("-jar");
            cmd.add(config.getJarPath());
        }
        cmd.add("nogui");
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command(cmd)
                .directory(new File(config.getServerHomePath()).getAbsoluteFile());
        this.process = processBuilder.start();
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        new Thread(() -> in.lines().forEach(s -> {
            try {
                msgQueue.put(s);
            } catch (InterruptedException ignored) {
            }
        })).start();
    }


}
