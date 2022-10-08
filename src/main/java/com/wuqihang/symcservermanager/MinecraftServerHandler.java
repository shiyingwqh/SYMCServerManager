package com.wuqihang.symcservermanager;

/**
 * @author Wuqihang
 */
public interface MinecraftServerHandler {
    String getMessage();
    void sendMessage(String msg);
    void destroy();

    boolean isRunning();

     interface OnMessage {
        void message(String msg);
    }

    void setOnMessage(OnMessage onMessage);

}
