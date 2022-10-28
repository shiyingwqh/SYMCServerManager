package com.wuqihang.mcserverlauncher.server;

import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuqihang
 */
public class BaseModMinecraftServer extends MinecraftServerImpl implements MinecraftServerMod {
    private final File MOD_DIR;

    public BaseModMinecraftServer(MinecraftServerConfig config) {
        super(config);
        MOD_DIR = new File(config.getServerHomePath(), "mods");
    }

    @Override
    public String modPath() {
        return MOD_DIR.getAbsolutePath();
    }

    @Override
    public void addMod(File mod) throws IOException {
        Files.copy(mod.toPath(), MOD_DIR.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void removeMod(String modFileName) {
        File mod = new File(MOD_DIR, modFileName);
        if (mod.exists()) {
            try {
                Files.delete(mod.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<String> getAllModName() {
        List<String> mods = new ArrayList<>();
        File[] files = MOD_DIR.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) continue;
            String name = file.getName();
            if (name.matches(".*.jar") || file.getName().matches(".*.zip")) {
                mods.add(file.getName());
            }
        }
        return mods;
    }
}
