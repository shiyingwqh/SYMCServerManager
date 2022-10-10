package com.wuqihang.symcservermanager.mc;

/**
 * @author Wuqihang
 */
public interface MinecraftServer {
    Process getProcess();
    String getMessage();

    void sendMessage(String msg);

    void destroy();

    boolean isRunning();

    interface OnMessage {
        void onMessage(String message) throws Exception;
    }
    void addOnMessage(OnMessage onMessage);
    void removeOnMessage(OnMessage onMessage);
}
