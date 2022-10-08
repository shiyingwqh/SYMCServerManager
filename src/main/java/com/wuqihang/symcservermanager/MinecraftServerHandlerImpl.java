package com.wuqihang.symcservermanager;

import java.io.*;

/**
 * @author Wuqihang
 */
public class MinecraftServerHandlerImpl implements MinecraftServerHandler{
    private BufferedReader in;
    private OutputStreamWriter out;
    private Process process = null;

    private Thread msgThread;

    private boolean msgExit = true;

    private OnMessage onMessage;

    public MinecraftServerHandlerImpl(Process process) {
        putProcess(process);
    }
    @Override
    public String getMessage() {
        try {
            if (in.read() != -1){
                return in.readLine();
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @Override
    public void sendMessage(String msg) {
        try {
            out.write(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        msgExit = true;
        process.destroy();
    }

    private void putProcess(Process process) {
        if (this.process == null && process.isAlive()) {
            this.process = process;
            this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.out = new OutputStreamWriter(process.getOutputStream());
            this.msgThread = new Thread(() -> {
                while (!msgExit) {
                    if (onMessage != null) {
                        try {
                            if(in.read() != -1) {
                                onMessage.message(in.readLine());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean isRunning() {
        if (process == null) {
            return false;
        }
        return process.isAlive();
    }

    public void setOnMessage(OnMessage onMessage) {
        this.onMessage = onMessage;
        msgThread.start();
    }
}
