package com.wuqihang.mcserverlauncher;

import com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Wuqihang
 */
public class MinecraftServerLauncher {
    private final Logger logger = LoggerFactory.getLogger(MinecraftServerLauncher.class);
    private MinecraftServerManager manager;
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
        if (manager == null) {
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass("com.wuqihang.mcserverlauncher.utils.MinecraftServerManagerImpl");
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                manager = (MinecraftServerManager) constructor.newInstance();
                constructor.setAccessible(false);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return manager;
    }

    public MinecraftServerDownloader getDownloader(){
        if (downloader == null) {
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass("com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader");
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                downloader = (MinecraftServerDownloader) constructor.newInstance();
                constructor.setAccessible(false);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return downloader;
    }
}
