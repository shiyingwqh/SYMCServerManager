package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerManager;
import com.wuqihang.symcservermanager.mc.MinecraftServerMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint(value = "/socket/{id}")
@ConditionalOnMissingBean(MinecraftServer.class)
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static final AtomicInteger ids = new AtomicInteger(0);
    private static final ConcurrentHashMap<String, WebSocketServer> MAP = new ConcurrentHashMap<>();
    private Session session;
    private int sid;

    private boolean closed = false;
    private static MinecraftServerManager minecraftServerManager;
    private MinecraftServer minecraftServer;
    private static final Queue<WebSocketServer> m = new LinkedBlockingQueue<>();
    private static final MinecraftServerMessageListener listener = WebSocketServer::listener;

    private static synchronized void listener(String s) {
        ArrayList<WebSocketServer> servers = new ArrayList<>();
        while (!m.isEmpty()) {
            servers.add(m.poll());
        }
        servers.forEach(webSocketServer -> {
            webSocketServer.sendMessage(s);
        });
    }

    public WebSocketServer() {
    }

    @Autowired()
    public void setServerProcessManager(MinecraftServerManager minecraftServerManager) {
        WebSocketServer.minecraftServerManager = minecraftServerManager;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("pid") String id) {
        if (MAP.containsKey(session.getId())) {
            return;
        }
        this.session = session;
        minecraftServer = minecraftServerManager.getServer(Integer.parseInt(id));
        if (minecraftServer == null || !minecraftServer.isRunning()) {
            return;
        }
        minecraftServer.setListener(listener);
        m.add(this);
        sid = Integer.parseInt(id);
        MAP.put(session.getId(), this);
        logger.info(session.getId() + " connected " + sid);
        sendMessage("connected\n");
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        if (!StringUtils.isEmptyOrWhitespace(msg)) {
            minecraftServer.sendMessage(msg + "\r");
            logger.info(sid + " send: " + msg);
        }
    }

    @OnClose
    public void onClose() {
        MAP.remove(session.getId());
        closed = true;
        logger.info(session.getId() + " disconnected");
    }

    public boolean isClosed() {
        return closed;
    }

    public synchronized void sendMessage(String msg){
        if (closed) {
            return;
        }
        m.add(this);
        try {
            this.session.getBasicRemote().sendText(msg + "\n");
        } catch (IOException e) {
            logger.warn("Session " + session.getId() + "Send Message Failed ",e);
        }
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error("", throwable);
    }

}
