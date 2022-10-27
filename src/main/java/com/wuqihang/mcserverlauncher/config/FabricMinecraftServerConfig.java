package com.wuqihang.mcserverlauncher.config;

/**
 * @author Wuqihang
 */
public class FabricMinecraftServerConfig extends MinecraftServerConfig{
    private String fabricVersion;

    public FabricMinecraftServerConfig(String fabricVersion) {
        this.fabricVersion = fabricVersion;
    }

    public FabricMinecraftServerConfig(String name, String javaPath, String jarPath, String jvmParam, String comment, String serverHomePath, String version, String fabricVersion) {
        super(name, javaPath, jarPath, jvmParam, comment, serverHomePath, version);
        this.fabricVersion = fabricVersion;
    }

    public String getFabricVersion() {
        return fabricVersion;
    }

    public void setFabricVersion(String fabricVersion) {
        this.fabricVersion = fabricVersion;
    }
}
