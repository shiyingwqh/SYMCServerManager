package com.wuqihang.symcservermanager.mc;

/**
 * @author Wuqihang
 */
public class MinecraftServerException extends Exception{

    private int code = 0;
    public MinecraftServerException(int code) {
        super();
        this.code = code;
    }

    public MinecraftServerException(String message) {
        super(message);
    }

    public MinecraftServerException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
