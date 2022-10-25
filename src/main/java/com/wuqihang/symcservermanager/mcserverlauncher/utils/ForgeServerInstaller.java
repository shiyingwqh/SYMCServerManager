package com.wuqihang.symcservermanager.mcserverlauncher.utils;

import com.wuqihang.symcservermanager.mcserverlauncher.ForgeMinecraftServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgeServerInstaller {
    private static final String OS_TYPE;
    private static final String BASH_EX;

    private static final Logger logger = LoggerFactory.getLogger(ForgeServerInstaller.class);

    static {
        String osName = System.getProperty("os.name");
        if (osName.indexOf("Windows") > 0) {
            OS_TYPE = "WINDOWS";
            BASH_EX = "bat";
        } else {
            OS_TYPE = "UNIX";
            BASH_EX = "sh";
        }
    }

    public static Future<ForgeMinecraftServerConfig> install(String serverPath, String forgeInstallerJar) throws IOException {
        return install(serverPath, forgeInstallerJar, new ForgeMinecraftServerConfig());
    }

    public static Future<ForgeMinecraftServerConfig> install(String serverPath, String forgeInstallerJar, ForgeMinecraftServerConfig config) throws IOException {
        File server = new File(serverPath);
        File serverJar = check(server);
        config.setJarPath(serverJar.getAbsolutePath());
        File forge = new File(forgeInstallerJar);
        ProcessBuilder processBuilder = new ProcessBuilder();
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-jar");
        cmd.add(forge.getAbsolutePath());
        cmd.add("--installServer");
        cmd.add(server.getAbsolutePath());
        String[] strings = new String[cmd.size()];
        strings = cmd.toArray(strings);
        processBuilder.command(strings);
        FutureTask<ForgeMinecraftServerConfig> task = new FutureTask<>(() -> {
            Process start = processBuilder.start();
            new BufferedReader(new InputStreamReader(start.getInputStream())).lines().forEach(logger::debug);
            start.waitFor();
            if (start.exitValue() != 0) {
                return null;
            }
            String[] list = server.list((dir, name) -> ("run." + BASH_EX).equals(name));
            assert list != null;
            if (list.length > 0) {
                config.setNewly(true);
                StringBuilder cmd1 = new StringBuilder();
                try (FileReader fReader = new FileReader(new File(server, "run." + BASH_EX)); BufferedReader bufferedReader = new BufferedReader(fReader)) {
                    bufferedReader.lines().forEach(cmd1::append);
                }
                Pattern pattern = Pattern.compile("@lib.*args.txt");
                Matcher matcher = pattern.matcher(cmd1.toString());
                if (matcher.find()) {
                    String argsPath = matcher.group().replace("@", "");
                    StringBuilder args = new StringBuilder();
                    try (FileReader fReader = new FileReader(new File(server, argsPath)); BufferedReader bufferedReader = new BufferedReader(fReader)) {
                        bufferedReader.lines().forEach(s -> args.append(s).append(' '));
                    }
                    config.setForgeArgs(args.toString());
                }
            }
            return config;
        });
        new Thread(task).start();
        return task;
    }

    private static File check(File serverPath) throws IOException {
        File[] files = serverPath.listFiles(pathname -> pathname.getName().indexOf(".jar") > 0);
        assert files != null;
        if (files.length == 0) {
            throw new FileNotFoundException("Server Jar Not Found!");
        }
        boolean isPacked = false;
        File serverJar = null;
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try (JarFile jarFile = new JarFile(file)) {
                String mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
                if ("net.minecraft.server.Main".equals(mainClass)) {
                    serverJar = file;
                    break;
                }
                if ("net.minecraft.bundler.Main".equals(mainClass)) {
                    serverJar = file;
                    isPacked = true;
                    break;
                }
            }
        }
        if (serverJar == null) {
            throw new FileNotFoundException("Server Jar Not Found!");
        }
        if (isPacked) {
            unpackServer(serverJar.getAbsolutePath());
        }
        return serverJar;
    }

    public static void unpackServer(String serverJar) throws IOException {
        File file = new File(serverJar);
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.matches("META-INF/versions/.*/.*\\.jar")) {
                    File server = new File(file.getParentFile(), jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/")));
                    boolean newFile = server.createNewFile();
                    if (!newFile) {
                        server = new File(file.getParentFile(), jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/")) + ".jar");
                        boolean newFile1 = server.createNewFile();
                    }
                    writeFile(jarFile, jarEntry, server);
                }
                if (name.matches("META-INF/libraries/.*")) {
                    if (!jarEntry.isDirectory()) {
                        File lib = new File(file.getParentFile(), jarEntry.getName().substring(jarEntry.getName().indexOf("/")));
                        boolean mkdirs = lib.getParentFile().mkdirs();
                        boolean newFile = lib.createNewFile();
                        writeFile(jarFile, jarEntry, lib);
                    }
                }
            }
        }
    }

    private static void writeFile(JarFile jarFile, JarEntry jarEntry, File lib) throws IOException {
        InputStream inputStream = jarFile.getInputStream(jarEntry);
        try (FileOutputStream fileOutputStream = new FileOutputStream(lib)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }
        }
    }
}
