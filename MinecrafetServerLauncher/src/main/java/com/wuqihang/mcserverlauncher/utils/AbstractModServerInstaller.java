package com.wuqihang.mcserverlauncher.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wuqihang
 */
public class AbstractModServerInstaller {
    protected static final String OS_TYPE;
    protected static final String BASH_EX;
    protected static final ExecutorService executorService;
    protected static Logger logger = LoggerFactory.getLogger(ForgeServerInstaller.class);

    static {
        String osName = System.getProperty("os.name");
        int windows = osName.indexOf("Windows");
        if (windows >= 0) {
            OS_TYPE = "WINDOWS";
            BASH_EX = "bat";
        } else {
            OS_TYPE = "UNIX";
            BASH_EX = "sh";
        }
        executorService = Executors.newCachedThreadPool();
    }

    public static void initLogger() {
        logger = LoggerFactory.getLogger(ForgeServerInstaller.class);
    }

    protected AbstractModServerInstaller() {
    }
}
