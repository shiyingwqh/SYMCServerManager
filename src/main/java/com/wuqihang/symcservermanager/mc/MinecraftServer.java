package com.wuqihang.symcservermanager.mc;

/**
 * @author Wuqihang
 */
public interface MinecraftServer {

    String getName();
    Process getProcess();
    String getMessage();

    void sendMessage(String msg);

    void destroy();

    boolean isRunning();

    boolean start();

    void addListener(MinecraftServerMessageListener onMessage);
    void removeListener(MinecraftServerMessageListener onMessage);
}
