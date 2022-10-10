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

    private final Map<OnMessage, Object> onMessageSet = new Hashtable<>();

    private Thread msgThread;
    private static final Object v = new Object();

    private boolean msgExit = true;

    private MinecraftServerConfig config;

    public MinecraftServerImpl(Process process) {
        this.process = process;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }
    public MinecraftServerImpl(Process process,MinecraftServerConfig config) {
        this.process = process;
        this.config = config;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    @Override
    public Process getProcess() {
        return process;
    }

    @Override
    public String getMessage() {
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
    public void addOnMessage(OnMessage onMessage) {
        if (onMessage == null || !isRunning()) {
            return;
        }
        onMessageSet.put(onMessage, v);
        if (msgExit) {
            msgExit = false;
            if (msgThread != null) {
                msgThread.stop();
            }
            msgThread = new Thread(() -> {
                while (!msgExit && isRunning()) {
                    String msg;
                    try {
                        if ((msg = in.readLine()) != null) {
                            for (OnMessage message : onMessageSet.keySet()) {
                                try {
                                    message.onMessage(msg + "\n");
                                } catch (Exception e) {
                                    removeOnMessage(message);
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
    public void removeOnMessage(OnMessage onMessage) {
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
