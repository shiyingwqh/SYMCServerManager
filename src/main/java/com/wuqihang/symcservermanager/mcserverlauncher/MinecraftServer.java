package com.wuqihang.symcservermanager.mcserverlauncher;

import java.io.IOException;

/**
 * @author Wuqihang
 */
public interface MinecraftServer {

    String getName();

    Process getProcess();

    long pid();

    void start() throws IOException;

    void stop();

    void restart();

    String getMessage();

    void sendMessage(String msg);

    void destroy();

    boolean isRunning();

    void setListener(MinecraftServerMessageListener onMessage);

}
