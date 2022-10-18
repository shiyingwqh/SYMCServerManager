package com.wuqihang.symcservermanager.mc;

import java.io.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Wuqihang
 */
public class MinecraftServerImpl implements MinecraftServer {
    private BufferedReader in;
    private BufferedWriter out;
    private final Process process;

    private final Map<MinecraftServerMessageListener, Object> onMessageSet = new Hashtable<>();

    private Thread msgThread;
    private static final Object v = new Object();

    private boolean msgExit = true;

    private String msgCache;

    private MinecraftServerConfig config;

    protected MinecraftServerImpl(Process process) {
        this.process = process;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }
     protected MinecraftServerImpl(Process process,MinecraftServerConfig config) {
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
    public void addListener(MinecraftServerMessageListener listener) {
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
                            for (MinecraftServerMessageListener message : onMessageSet.keySet()) {
                                try {
                                    message.message(msgCache + "\n");
                                } catch (Exception e) {
                                    removeListener(message);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            msgThread.start();
        }
    }

    @Override
    public void removeListener(MinecraftServerMessageListener onMessage) {
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
