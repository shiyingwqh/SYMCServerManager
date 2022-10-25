package com.wuqihang.symcservermanager.mcserverlauncher;

/**
 * @author Wuqihang
 */
public class ForgeMinecraftServerConfig extends MinecraftServerConfig {
    private String forgeArgs;
    private boolean newly;

    public ForgeMinecraftServerConfig() {
    }

    public ForgeMinecraftServerConfig(int id, String name, String javaPath, String jarPath, String otherParam, String comment, String serverHomePath, String forgeArgs) {
        super(id, name, javaPath, jarPath, otherParam, comment, serverHomePath);
        this.forgeArgs = forgeArgs;
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

    @Override
    public String toString() {
        return "ForgeMinecraftServerConfig{" +
                "forgeArgs='" + forgeArgs + '\'' +
                ", newly=" + newly +
                "} " + super.toString();
    }
}
