package com.wuqihang.symcservermanager.mc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Wuqihang
 */
@Component
public class MinecraftServerLauncher {
    private static String EULA = "eula=true";
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
        File file = new File(minecraftServerConfig.getServerHomePath(), "eula.txt");
        boolean created = false;
        if (!file.exists()) {
            created = file.createNewFile();
        }
        if (created)
            try (PrintWriter printWriter = new PrintWriter(file, StandardCharsets.UTF_8)) {
                printWriter.write(EULA);
            }
        return new MinecraftServerImpl(launch(minecraftServerConfig.getJavaPath() + " -jar", minecraftServerConfig.getJarPath(), minecraftServerConfig.getOtherParam()));
    }
}
