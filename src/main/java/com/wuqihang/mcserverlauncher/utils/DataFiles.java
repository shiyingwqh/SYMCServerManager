package com.wuqihang.mcserverlauncher.utils;

import java.io.File;

public class DataFiles {
    public static final File DATA_DIR = new File("mc_server_launcher_data");
    public static final File VERSIONS_JSON = new File(DATA_DIR,"versions.json");
    public static final File SERVER_CONFIGS_JSON = new File(DATA_DIR, "configs.json");

    static {
        if (!DATA_DIR.exists()) {
            boolean mkdirs = DATA_DIR.mkdirs();
        }
    }
}
