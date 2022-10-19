package com.wuqihang.symcservermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.mc.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.Session;
import java.io.IOException;

/**
 * @author Wuqihang
 */
@RestController
@ConditionalOnProperty(prefix = "mc",name = "single-mode", havingValue = "false")
public class McServerController {
    private final MinecraftServerManager minecraftServerManager;

    public McServerController(MinecraftServerManager minecraftServerManager) {
        this.minecraftServerManager = minecraftServerManager;
    }

    @RequestMapping("launch-server")
    public String launch(@RequestParam("jpath") String javaPath, @RequestParam("spath") String serverPath, Session session) {

        MinecraftServer minecraftServer;
        MinecraftServerConfig minecraftServerConfig = new MinecraftServerConfig();
        minecraftServerConfig.setJarPath(javaPath);
        minecraftServerConfig.setServerHomePath(javaPath);
        try {
            minecraftServer =  minecraftServerManager.launch(minecraftServerConfig);
        } catch (MinecraftServerException e) {
            return e.getMessage();
        }
        return minecraftServer.toString();
    }


}
