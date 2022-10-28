package com.wuqihang.mcserverlauncher.utils;

import com.wuqihang.mcserverlauncher.server.MinecraftServer;

public class ServerCommandProxyImpl extends ServerCommandProxy {

    public ServerCommandProxyImpl(MinecraftServer server) {
        super(server);
    }

    @Override
    public void sendCommand(String command, String... args) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("/");
        cmd.append(command).append(" ");
        if (args != null) {
            for (String arg : args) {
                cmd.append(arg).append(" ");
            }
        }
        sendCommand(cmd.toString());
    }

    @Override
    public void sendCommand(String commandInline) {
        server.sendMessage(commandInline + "/r");
    }
}
