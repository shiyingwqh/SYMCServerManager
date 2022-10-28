package com.wuqihang.mcserverlauncher.config;

/**
 * @author Wuqihang
 */
public class ForgeMinecraftServerConfig extends MinecraftServerConfig {
    private String forgeArgs;
    private boolean newly;
    private String forgeVersion;

    public ForgeMinecraftServerConfig() {
    }

    public ForgeMinecraftServerConfig(String name, String javaPath, String jarPath, String jvmParam, String comment, String serverHomePath, String version, String forgeArgs, boolean newly, String forgeVersion) {
        super(name, javaPath, jarPath, jvmParam, comment, serverHomePath, version);
        this.forgeArgs = forgeArgs;
        this.newly = newly;
        this.forgeVersion = forgeVersion;
    }

    public String getForgeArgs() {
        return forgeArgs;
    }

    public void setForgeArgs(String forgeArgs) {
        this.forgeArgs = forgeArgs;
    }

    public boolean isNewly() {
        return newly;
    }

    public void setNewly(boolean newly) {
        this.newly = newly;
    }

    public String getForgeVersion() {
        return forgeVersion;
    }

    public void setForgeVersion(String forgeVersion) {
        this.forgeVersion = forgeVersion;
    }

    @Override
    public String toString() {
        return "ForgeMinecraftServerConfig{" +
                "forgeArgs='" + forgeArgs + '\'' +
                ", newly=" + newly +
                "} " + super.toString();
    }
}
