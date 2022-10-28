package com.wuqihang.symcservermanager.controller;

import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.server.MinecraftServerMessageListener;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManagerImpl;
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
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint(value = "/socket/{id}")
@ConditionalOnMissingBean(MinecraftServer.class)
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private Session session;
    private MinecraftServer minecraftServer;
    private final static MinecraftServerMessageListener listener = WebSocketServer::listener;
    private static final CopyOnWriteArraySet<WebSocketServer> set = new CopyOnWriteArraySet<>();
    private static MinecraftServerManagerImpl minecraftServerManager;

    private static synchronized void listener(String s) {
        set.forEach(webSocketServer -> {
            webSocketServer.sendMessage(s);
        });
    }

    @Autowired
    public void setMinecraftServer(MinecraftServerManagerImpl minecraftServer) {
        WebSocketServer.minecraftServerManager = minecraftServer;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        this.session = session;
        set.add(this);
        this.minecraftServer = minecraftServerManager.getServer(id);
        if (this.minecraftServer == null) {
            return;
        }
        logger.info(session.getId() + " connected ");
        sendMessage("connected\n");
        minecraftServer.setListener(listener);
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        if (!StringUtils.isEmptyOrWhitespace(msg)) {
            minecraftServer.sendMessage(msg + 'r');
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
//            logger.info(msg);
        } catch (IOException e) {
            logger.warn("Session " + session.getId() + " Send Message Failed ", e);
        }
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error("", throwable);
    }
}
