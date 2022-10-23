package com.wuqihang.symcservermanager.mcserverlauncher;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Wuqihang
 */
public class ForgeMinecraftServer extends MinecraftServerImpl {

    public ForgeMinecraftServer(ForgeMinecraftServerConfig config) {
        super(config);
    }

    @Override
    protected void launch() throws IOException {
        ForgeMinecraftServerConfig forgeConfig = (ForgeMinecraftServerConfig) config;
        ArrayList<String> cmd = new ArrayList<>();
        if (forgeConfig.isNewly()) {
            cmd.add(config.getJavaPath());
            cmd.addAll(Arrays.asList(config.getJvmParam().split("\\s")));
            cmd.addAll(Arrays.asList(forgeConfig.getForgeArgs().split("\\s")));
        }else  {
            cmd.add(config.getJavaPath());
            cmd.addAll(Arrays.asList(config.getJvmParam().split("\\s")));
            cmd.add("-jar");
            cmd.add(config.getJarPath());
        }
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command(cmd)
                .directory(new File(config.getServerHomePath()).getAbsoluteFile());
        this.process = processBuilder.start();
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        new Thread(() -> {
            try {
                String s;
                while (isRunning() && (s = in.readLine()) != null) {
                    msgQueue.put(s);
                }
            } catch (IOException | InterruptedException ignored) {

            }
        }).start();
    }
}
