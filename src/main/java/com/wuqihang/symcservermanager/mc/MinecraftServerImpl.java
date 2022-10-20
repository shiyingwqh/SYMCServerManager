package com.wuqihang.symcservermanager.mc;

import com.wuqihang.symcservermanager.mc.utils.MinecraftServerLauncher;

import java.io.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Wuqihang
 */
public class MinecraftServerImpl implements MinecraftServer {
    private BufferedReader in;
    private BufferedWriter out;
    private Process process;

    private final Map<MinecraftServerMessageListener, Object> onMessageSet = new Hashtable<>();

    private Thread msgThread;
    private static final Object v = new Object();

    private volatile boolean msgExit = true;

    private String msgCache;

    protected MinecraftServerConfig config;

    protected MinecraftServerImpl(Process process) {
        this.process = process;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    public MinecraftServerImpl(Process process, MinecraftServerConfig config) {
        this.process = process;
        this.config = config;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    @Override
    public String getName() {
        return config.getName();
    }

    @Override
    public Process getProcess() {
        return process;
    }

    @Override
    public long pid() {
        return process.toHandle().pid();
    }

    @Override
    public void stop() {
        message("Stop Server");
        destroy();
    }

    @Override
    public void restart() {
        try {
            if (process.isAlive()) {
                stop();
            }
            MinecraftServerLauncher.restartMinecraftServer(this, config);
        } catch (IOException ignored) {

        }
    }

    private void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public String getMessage() {
        if (!msgExit) {
            return msgCache;
        }
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void sendMessage(String msg) {
        try {
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        msgExit = true;
        sendMessage("stop");
        process.destroy();
    }

    @Override
    public boolean isRunning() {
        if (process == null) {
            return false;
        }
        return process.isAlive();
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public synchronized void addListener(MinecraftServerMessageListener listener) {
        if (listener == null || !isRunning()) {
            return;
        }
        onMessageSet.put(listener, v);
        if (msgExit) {
            msgExit = false;
            if (msgThread != null) {
                msgThread.stop();
            }
            msgThread = new Thread(() -> {
                while (!msgExit && isRunning()) {
                    try {
                        if ((msgCache = in.readLine()) != null) {
                            message(msgCache);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            msgThread.start();
        }
    }

    private void message(String msg) {
        for (MinecraftServerMessageListener message : onMessageSet.keySet()) {
            try {
                message.message(msg + "\n");
            } catch (SecurityException e) {
                break;
            } catch (Exception e) {
                removeListener(message);
            }
        }
    }

    @Override
    public synchronized void removeListener(MinecraftServerMessageListener onMessage) {
        if (onMessage == null) {
            return;
        }
        onMessageSet.remove(onMessage);
        if (onMessageSet.isEmpty()) {
            msgExit = true;
        }
    }

    public MinecraftServerConfig getConfig() {
        return config;
    }
}
