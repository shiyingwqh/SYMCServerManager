package com.wuqihang.mcserverlauncher.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Wuqihang
 */
public interface MinecraftServerMod{
    String modPath();

    void addMod(File mod) throws IOException;

    void removeMod(String modFileName);

    List<String> getAllModName();
}
