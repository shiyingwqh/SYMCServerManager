package com.wuqihang.symcservermanager.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.websocket.server.ServerEndpoint;

/**
 * @author Wuqihang
 */
@Component
@ServerEndpoint("/socket/{}")
public class WebSocketServer {
}
