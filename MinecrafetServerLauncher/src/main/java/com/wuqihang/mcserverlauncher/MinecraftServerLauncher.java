package com.wuqihang.mcserverlauncher;

import com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Wuqihang
 */
public class MinecraftServerLauncher {
    private final static Logger logger = LoggerFactory.getLogger(MinecraftServerLauncher.class);
    private static MinecraftServerManager manager;
    private static MinecraftServerDownloader downloader;

    private MinecraftServerLauncher() {

    }

    public static MinecraftServerManager getManager() {
        if (manager == null) {
            try {
                Class<?> clazz = MinecraftServerLauncher.class.getClassLoader().loadClass("com.wuqihang.mcserverlauncher.utils.MinecraftServerManagerImpl");
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                manager = (MinecraftServerManager) constructor.newInstance();
                constructor.setAccessible(false);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                logger.error("", e);
                throw new RuntimeException(e);
            }
        }
        return manager;
    }

    public static MinecraftServerDownloader getDownloader() {
        if (downloader == null) {
            try {
                Class<?> clazz = MinecraftServerLauncher.class.getClassLoader().loadClass("com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader");
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                downloader = (MinecraftServerDownloader) constructor.newInstance();
                constructor.setAccessible(false);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                logger.error("", e);
                throw new RuntimeException(e);
            }
        }
        return downloader;
    }
}
