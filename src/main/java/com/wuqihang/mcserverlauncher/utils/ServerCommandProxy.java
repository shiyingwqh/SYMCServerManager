package com.wuqihang.mcserverlauncher.utils;

import com.wuqihang.mcserverlauncher.server.MinecraftServer;

public abstract class ServerCommandProxy {
    protected MinecraftServer server;

    protected ServerCommandProxy(MinecraftServer server) {
        this.server = server;
    }

    public abstract void sendCommand(String command, String... args);

    public abstract void sendCommand(String commandInline);

    public void sendCommand(MinecraftServerCommands command, String... args) {
        sendCommand(command.toString(), args);
    }
}
