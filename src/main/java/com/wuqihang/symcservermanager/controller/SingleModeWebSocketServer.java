package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint(value = "/socket/{id}")
@ConditionalOnBean(MinecraftServer.class)
public class SingleModeWebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SingleModeWebSocketServer.class);
    private Session session;
    private static MinecraftServer minecraftServer;
    private final static MinecraftServerMessageListener listener = SingleModeWebSocketServer::listener;
    private static final CopyOnWriteArraySet<SingleModeWebSocketServer> set = new CopyOnWriteArraySet<>();

    private static synchronized void listener(String s)  {
        set.forEach(webSocketServer -> {
            webSocketServer.sendMessage(s);
        });
    }

    public SingleModeWebSocketServer() {
    }

    @Autowired
    public void setMinecraftServer(MinecraftServer minecraftServer) {
        SingleModeWebSocketServer.minecraftServer = minecraftServer;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        set.add(this);
        logger.info(session.getId() + " connected ");
        sendMessage("connected\n");
        minecraftServer.setListener(listener);
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        if (!StringUtils.isEmptyOrWhitespace(msg)) {
            minecraftServer.sendMessage(msg + "\r");
            logger.info("server send: " + msg);
        }
    }

    @OnClose
    public void onClose() {
        set.remove(this);
        logger.info(session.getId() + " disconnected");
    }

    public synchronized void sendMessage(String msg) {
        try {
            this.session.getBasicRemote().sendText(msg + "\n");
            logger.info(msg);
        } catch (IOException e) {
            logger.warn("Session " + session.getId() + " Send Message Failed ",e);
        }
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error("", throwable);
    }
}
