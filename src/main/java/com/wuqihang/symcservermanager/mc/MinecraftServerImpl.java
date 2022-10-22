package com.wuqihang.symcservermanager.mc;

import com.wuqihang.symcservermanager.mc.utils.MinecraftServerLauncher;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Wuqihang
 */
public class MinecraftServerImpl implements MinecraftServer {
    private BufferedReader in;
    private BufferedWriter out;
    private Process process;

    private MinecraftServerMessageListener listener = null;

    protected MinecraftServerConfig config;

    private Thread listenerThread;

    private final BlockingQueue<String> msgQueue = new ArrayBlockingQueue<>(20);

    public MinecraftServerImpl(MinecraftServerConfig config) {
        this.config = config;
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
        if (process == null) {
            return -1;
        }
        return process.toHandle().pid();
    }

    @Override
    public void stop() {
        message("Stop Server");
        destroy();
    }

    @Override
    public void start() throws IOException {
        if (isRunning()) {
            return;
        }
        launch();
    }

    @Override
    public void restart() {
        try {
            if (process.isAlive()) {
                stop();
            }
            launch();
        } catch (IOException ignored) {

        }
    }

    private void launch() throws IOException {
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add(config.getJavaPath());
        cmd.add("-jar");
        cmd.add(config.getJarPath());
        cmd.addAll(Arrays.asList(config.getOtherParam().split("\\s")));
        ProcessBuilder processBuilder = new ProcessBuilder().command(cmd).directory(new File(config.getServerHomePath()).getAbsoluteFile());
        this.process = processBuilder.start();
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        new Thread(() -> {
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    msgQueue.put(s);
                }
            } catch (IOException | InterruptedException ignored) {

            }
        }).start();
    }

    private void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public String getMessage() {
        return msgQueue.poll();
    }

    @Override
    public void sendMessage(String msg) {
        if (!isRunning()) {
            return;
        }
        try {
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        if (process == null || !process.isAlive()) {
            return;
        }
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
    public synchronized void setListener(MinecraftServerMessageListener listener) {
        this.listener = listener;
        if (listener != null && listenerThread == null) {
            listenerThread = new Thread(() -> {
                while (true) {
                    String message = null;
                    try {
                        message = msgQueue.poll(200, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (message == null) {
                        continue;
                    }
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
                this.listener.message(msg);
            } catch (Exception ignored) {
            }
        }
    }

    public MinecraftServerConfig getConfig() {
        return config;
    }
}
