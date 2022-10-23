package com.wuqihang.symcservermanager.mcserverlauncher.utils;

import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Wuqihang
 */
public class MinecraftServerLauncher {
    private final Logger logger = LoggerFactory.getLogger(MinecraftServerLauncher.class);
    private final MinecraftServerManager manager = new MinecraftServerManagerImpl();
    private MinecraftServerDownloader downloader;

    private static MinecraftServerLauncher INSTANCE;

    private MinecraftServerLauncher() {

    }

    public static MinecraftServerLauncher getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MinecraftServerLauncher();
        }
        return INSTANCE;
    }
    public MinecraftServerManager getManager(){
        return manager;
    }

    public MinecraftServerDownloader getDownloader() throws IOException{
        if (downloader == null) {
            downloader = new MinecraftServerDownloader();
        }
        return downloader;
    }
}
