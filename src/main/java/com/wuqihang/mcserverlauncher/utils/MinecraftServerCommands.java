package com.wuqihang.mcserverlauncher.utils;

public enum MinecraftServerCommands {

    HELP("help"),
    BAN("ban"),
    BAN_IP("ban-ip"),
    PARDON("pardon"),
    PARDON_IP("pardon-ip");
    private final String string;

    MinecraftServerCommands(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
