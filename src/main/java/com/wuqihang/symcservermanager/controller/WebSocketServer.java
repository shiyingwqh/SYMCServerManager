package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.MinecraftServerHandler;
import com.wuqihang.symcservermanager.ServerProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint("/socket/{pid}")
public class WebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static AtomicInteger ids = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer,WebSocketServer> map =new ConcurrentHashMap<>();
    private Session session;
    private int id;
    private long pid;
    private final ServerProcessManager serverProcessManager;
    private MinecraftServerHandler processHandle;

    public WebSocketServer(ServerProcessManager serverProcessManager) {
        this.serverProcessManager = serverProcessManager;
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("pid") long pid) {
        this.session = session;
        this.pid = pid;
        this.processHandle = serverProcessManager.getServerHandler(id);
        if (!processHandle.isRunning()) {

        }
        processHandle.setOnMessage(this::sendMessage);
        id = ids.get();
        map.put(id, this);
        logger.info(session.getId() + " connected " + id);
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        if (!StringUtils.isEmptyOrWhitespace(msg)) {
            processHandle.sendMessage(msg);
            logger.info(pid + " send: " + msg);
        }
    }

    @OnClose
    public void onClose() {
        map.remove(id);
        logger.info(session.getId() + " disconnected");
    }

    public void sendMessage(String msg) {
        try {
            this.session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
