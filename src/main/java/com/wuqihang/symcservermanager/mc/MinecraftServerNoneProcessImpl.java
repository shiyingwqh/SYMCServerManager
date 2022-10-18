package com.wuqihang.symcservermanager.mc;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.action.LoggerAction;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LoggingSystem;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.LoggingUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author Wuqihang
 * Some Problem, Runable but Can't Interact
 */
@Deprecated
public class MinecraftServerNoneProcessImpl implements MinecraftServer {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftServerNoneProcessImpl.class);
    private static final ThreadGroup threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Minecraft Server");
    private final Thread serverThread;
    private final Object o = new Object();
    private static final Map<MinecraftServerMessageListener, Object> map = new HashMap<>();

    public MinecraftServerNoneProcessImpl(MinecraftServerConfig config) throws Exception {
        JarFile jar = new JarFile(new File(config.getJarPath()));
        Manifest manifest = jar.getManifest();
        String mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        jar.close();
        mainClassName = mainClassName.replaceAll("/", ".");
        File file = new File(config.getJarPath());

        ArrayList<URL> classPath = new ArrayList<>();

        classPath.add(file.toPath().toUri().toURL());

        String finalMainClassName = mainClassName;
        ClassLoader parent = getClass().getClassLoader().getParent();
        URLClassLoader classLoader = new URLClassLoader(classPath.toArray(new URL[0]));
        serverThread = new Thread(threadGroup, () -> {
            try {
                Class<?> mainClass = Class.forName(finalMainClassName, true, classLoader);
                Method main = mainClass.getMethod("main", String[].class);
                main.invoke(null, (Object) new String[]{});
                logger.info("Server Started");
            } catch (Exception e) {
                logger.warn("Server Start Failed", e);
            }
        }, "Mc Server Thread");
        serverThread.setContextClassLoader(classLoader);
        serverThread.start();

    }

    private void unJar(File jarFile, File toDir) throws IOException {
        JarFile jar = new JarFile(jarFile);

        try {
            Enumeration entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                if (!entry.isDirectory()) {
                    InputStream in = jar.getInputStream(entry);
                    try {
                        File file = new File(toDir, entry.getName());
                        if (!file.getParentFile().mkdirs()) {
                            if (!file.getParentFile().isDirectory()) {
                                throw new IOException("Mkdirs failed to create " + file.getParentFile().toString());

                            }

                        }

                        OutputStream out = new FileOutputStream(file);

                        try {

                            byte[] buffer = new byte[8192];

                            int i;
                            while ((i = in.read(buffer)) != -1) {
                                out.write(buffer, 0, i);
                            }

                        } finally {
                            out.close();
                        }
                    } finally {
                        in.close();
                    }
                }
            }
        } finally {
            jar.close();
        }

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Process getProcess() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void sendMessage(String msg) {

    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean isRunning() {
        int noThreads = threadGroup.activeCount();
        System.out.println(threadGroup.activeGroupCount());
        Thread[] lstThreads = new Thread[noThreads];
        int enumerate = threadGroup.enumerate(lstThreads);
        for (Thread thread : lstThreads) {
            if (thread.isAlive() && (thread.getName().equals("Server thread") || thread.getName().equals("ServerMain"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void addListener(MinecraftServerMessageListener onMessage) {

    }

    @Override
    public void removeListener(MinecraftServerMessageListener onMessage) {

    }
}
