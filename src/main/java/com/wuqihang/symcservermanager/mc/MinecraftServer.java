package com.wuqihang.symcservermanager.mc;

/**
 * @author Wuqihang
 */
public interface MinecraftServer {

    String getName();
    Process getProcess();

    long pid();

    void stop();

    void restart();

    String getMessage();

    void sendMessage(String msg);

    void destroy();

    boolean isRunning();

    boolean start();

    void setListener(MinecraftServerMessageListener onMessage);

}
