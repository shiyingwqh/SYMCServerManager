package com.wuqihang.mcserverlauncher.server;

import com.jcraft.jsch.Session;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;

import java.io.IOException;

public class RemoteMinecraftServer implements MinecraftServer {

    public RemoteMinecraftServer(MinecraftServer server) {

    }


    @Override
    public String getName() {
        return null;
    }

    @Override
    public Process getProcess() {
        return null;
    }

    @Override
    public long pid() {
        return 0;
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void sendMessage(String msg) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void setListener(MinecraftServerMessageListener onMessage) {

    }
}
