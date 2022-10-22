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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Wuqihang
 */
public class MinecraftServerLauncher {
    private static final String EULA = "eula=true";
    private static final Logger logger = LoggerFactory.getLogger(MinecraftServerLauncher.class);

    protected static MinecraftServer launchMinecraftServer(MinecraftServerConfig minecraftServerConfig) throws IOException {
        MinecraftServerImpl minecraftServer = new MinecraftServerImpl(minecraftServerConfig);
        minecraftServer.start();
        return minecraftServer;
    }

    protected static MinecraftServer restartMinecraftServer(MinecraftServer minecraftServer, MinecraftServerConfig minecraftServerConfig) throws IOException {
        if (minecraftServer.isRunning()) {
            minecraftServer.stop();
        }
        Process process = launch(minecraftServerConfig.getJavaPath() + " -jar", minecraftServerConfig.getOtherParam(), minecraftServerConfig.getJarPath());
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

    protected static Process launch(String cmd, String workdir, String... otherCmd) throws IOException {
        ArrayList<String> cmds = new ArrayList<>();
        cmds.add(cmd);
        if (otherCmd != null) {
            cmds.addAll(Arrays.asList(otherCmd));
        }
        ProcessBuilder processBuilder = new ProcessBuilder().command(cmds).directory(new File(workdir).getAbsoluteFile());
        return processBuilder.start();
    }
}
