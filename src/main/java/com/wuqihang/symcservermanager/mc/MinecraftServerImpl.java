package com.wuqihang.symcservermanager.mc;

import com.wuqihang.symcservermanager.mc.utils.MinecraftServerLauncher;

import java.io.*;

/**
 * @author Wuqihang
 */
public class MinecraftServerImpl implements MinecraftServer {
    private final BufferedReader in;
    private final BufferedWriter out;
    private Process process;

    private MinecraftServerMessageListener listener = null;

    private volatile boolean msgExist = false;

    private String msgCache;

    protected MinecraftServerConfig config;

    private Thread listenerThread;

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
        if (listener != null && msgCache != null) {
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
        msgExist = true;
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
    public synchronized void setListener(MinecraftServerMessageListener listener) {
        this.listener = listener;
        if (listener != null) {
            listenerThread = new Thread(() -> {
                while (process.isAlive()) {
                    String message = getMessage();
                    message(message);
                }
            });
            if (!listenerThread.isAlive()) {
                listenerThread.start();
            }
        }
    }

    private void message(String msg) {
        if (this.listener != null) {
            try {
                if ((msgCache = in.readLine()) != null){
                    this.listener.message(msgCache);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public MinecraftServerConfig getConfig() {
        return config;
    }
}
