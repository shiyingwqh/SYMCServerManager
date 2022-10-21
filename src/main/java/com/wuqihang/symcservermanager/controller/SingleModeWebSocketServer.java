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
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint(value = "/socket/{id}")
@ConditionalOnBean(MinecraftServer.class)
public class SingleModeWebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SingleModeWebSocketServer.class);
    private static final ConcurrentHashMap<String, SingleModeWebSocketServer> MAP = new ConcurrentHashMap<>();
    private boolean closed;
    private Session session;
    private static MinecraftServer minecraftServer;
    private final static MinecraftServerMessageListener listener = SingleModeWebSocketServer::listener;
    private static final Queue<SingleModeWebSocketServer> m = new LinkedBlockingQueue<>();

    private static synchronized void listener(String s)  {
        ArrayList<SingleModeWebSocketServer> servers = new ArrayList<>();
        while (!m.isEmpty()) {
            servers.add(m.poll());
        }
        servers.forEach(webSocketServer -> {
            webSocketServer.sendMessage(s);
        });
    }

    public SingleModeWebSocketServer() {
    }

    @Autowired
    public void setServerProcessManager(MinecraftServer minecraftServer) {
        SingleModeWebSocketServer.minecraftServer = minecraftServer;
    }

    @OnOpen
    public void onOpen(Session session) {
        if (MAP.containsKey(session.getId())) {
            return;
        }
        this.session = session;
        minecraftServer.setListener(listener);
        m.add(this);
        MAP.put(session.getId(), this);
        logger.info(session.getId() + " connected ");
        sendMessage("connected\n");
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
        MAP.remove(session.getId());
        closed = true;
        logger.info(session.getId() + " disconnected");
    }

    public synchronized void sendMessage(String msg) {
        if (closed) {
            return;
        }
        m.add(this);
        try {
            this.session.getBasicRemote().sendText(msg + "\n");
        } catch (IOException e) {
            logger.warn("Session " + session.getId() + " Send Message Failed ",e);
        }
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error("", throwable);
    }
}
