package com.wuqihang.symcservermanager.mc;

/**
 * @author Wuqihang
 */
public interface MinecraftServerMessageListener {
    void message(String msg);
    default boolean isClosed() {
        return false;
    }
}
