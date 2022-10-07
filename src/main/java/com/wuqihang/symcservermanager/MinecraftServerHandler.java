package com.wuqihang.symcservermanager;

import org.springframework.web.method.HandlerMethod;

/**
 * @author Wuqihang
 */
public interface MinecraftServerHandler {
    String getMessage();
    void sendMessage(String msg);
    void destroy();
    void putProcess(Process process);

    boolean isRunning();

    interface OnMessage {
        void onMessage(String msg);
    }

    void setOnMessage(OnMessage onMessage);

}
