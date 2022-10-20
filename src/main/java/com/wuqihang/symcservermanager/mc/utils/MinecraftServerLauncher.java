package com.wuqihang.symcservermanager.mc.utils;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mc.MinecraftServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author Wuqihang
 */
public class MinecraftServerLauncher {
    private static final String EULA = "eula=true";
    private static final Logger logger = LoggerFactory.getLogger(MinecraftServerLauncher.class);

    public static Process launch(File cmd, File file) throws IOException {
        String javaPath = cmd.getAbsolutePath();
        String serverJarPath = file.getAbsolutePath();
        return launch(javaPath, serverJarPath, null);
    }

    public static Process launch(String cmd, String file) throws IOException {
        return launch(cmd, file, null);
    }


    public static Process launch(String cmd, String file, String others) throws IOException {
        StringBuilder cmds = new StringBuilder();
        cmds.append(cmd).append(' ');
        cmds.append(file).append(' ');
        if (others != null) {
            cmds.append(others).append(' ');
        }
        long pid;
        Process process = Runtime.getRuntime().exec(cmds.toString(), null, new File(file).getParentFile());
        pid = process.pid();
        logger.info(file + " run");
        return process;
    }

    public static MinecraftServer launchMinecraftServer(MinecraftServerConfig minecraftServerConfig) throws IOException {
        checkEula(minecraftServerConfig);
        Process launch = launch(minecraftServerConfig.getJavaPath() + " -jar", minecraftServerConfig.getJarPath(), minecraftServerConfig.getOtherParam());
        return new MinecraftServerImpl(launch, minecraftServerConfig);
    }

    public static MinecraftServer restartMinecraftServer(MinecraftServer minecraftServer, MinecraftServerConfig minecraftServerConfig) throws IOException {
        if (minecraftServer.isRunning()) {
            minecraftServer.stop();
        }
        Process process = launch(minecraftServerConfig.getJavaPath() + " -jar", minecraftServerConfig.getJarPath(), minecraftServerConfig.getOtherParam());
        checkEula(minecraftServerConfig);
        Class<?> minecraftServerClass = minecraftServer.getClass();
        try {
            Method setProcess = minecraftServerClass.getDeclaredMethod("setProcess", Process.class);
            setProcess.setAccessible(true);
            setProcess.invoke(minecraftServer, process);
            setProcess.setAccessible(false);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.warn("Can't Find MinecraftServer Method 'setProcess'");
            Field[] fields = minecraftServerClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().isInstance(Process.class)) {
                    try {
                        field.setAccessible(true);
                        field.set(Process.class, process);
                        field.setAccessible(false);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
        }
        return minecraftServer;
    }

    private static void checkEula(MinecraftServerConfig minecraftServerConfig) throws IOException {
        File file = new File(minecraftServerConfig.getServerHomePath(), "eula.txt");
        boolean created = false;
        if (!file.exists()) {
            created = file.createNewFile();
        }
        if (created)
            try (PrintWriter printWriter = new PrintWriter(file, StandardCharsets.UTF_8)) {
                printWriter.write(EULA);
            }
    }
}
