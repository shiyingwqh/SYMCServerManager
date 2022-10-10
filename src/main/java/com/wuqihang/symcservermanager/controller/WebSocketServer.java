package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint(value = "/socket/{pid}")
public class WebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static AtomicInteger ids = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer,WebSocketServer> MAP =new ConcurrentHashMap<>();
    private Session session;
    private int id;
    private long pid;
    private static MinecraftServerManager minecraftServerManager;
    private MinecraftServer processHandle;
    private MinecraftServer.OnMessage onMessage;
    public WebSocketServer() {
    }
    @Autowired
    public void setServerProcessManager(MinecraftServerManager minecraftServerManager) {
        WebSocketServer.minecraftServerManager = minecraftServerManager;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("pid") long pid) {
        this.session = session;
        this.pid = pid;
        processHandle = minecraftServerManager.getServerHandler(pid);
        if (processHandle == null || !processHandle.isRunning()) {
            return;
        }
        onMessage = this::sendMessage;
        processHandle.addOnMessage(onMessage);
        id = ids.get();
        MAP.put(id, this);
        logger.info(session.getId() + " connected " + pid);
        try {
            sendMessage("connected");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        if (!StringUtils.isEmptyOrWhitespace(msg)) {
            processHandle.sendMessage(msg + "\r");
            logger.info(pid + " send: " + msg);
        }
    }

    @OnClose
    public void onClose() {
        MAP.remove(id);
        processHandle.removeOnMessage(onMessage);
        logger.info(session.getId() + " disconnected");
    }

    public void sendMessage(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }
    @OnError
    public void onError(Throwable throwable){
        logger.error("",throwable);
    }

}
