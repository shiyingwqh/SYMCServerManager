package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerManager;
import com.wuqihang.symcservermanager.mc.MinecraftServerMessageListener;
import com.wuqihang.symcservermanager.mc.MinecraftServerNoneProcessImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "mc",name = "single-mode", havingValue = "true")
public class SingleModeWebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SingleModeWebSocketServer.class);
    private static ConcurrentHashMap<Integer,SingleModeWebSocketServer> MAP =new ConcurrentHashMap<>();
    private Session session;
    private final AtomicInteger ids = new AtomicInteger(0);
    private int id;
    private static MinecraftServer minecraftServer;
    private MinecraftServerMessageListener onMessage;

    public SingleModeWebSocketServer() {
    }

    @Autowired
    public void setServerProcessManager(MinecraftServer minecraftServer) {
        SingleModeWebSocketServer.minecraftServer = minecraftServer;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("pid") long pid) {
        this.session = session;
        onMessage = this::sendMessage;
        minecraftServer.addListener(onMessage);
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
            minecraftServer.sendMessage(msg + "\r");
            logger.info("server send: " + msg);
        }
    }

    @OnClose
    public void onClose() {
        MAP.remove(id);
        minecraftServer.removeListener(onMessage);
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
