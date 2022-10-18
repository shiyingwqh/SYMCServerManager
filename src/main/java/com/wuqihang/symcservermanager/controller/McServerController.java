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
    private final MinecraftServerLauncher launcher;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MinecraftServerManager minecraftServerManager;
    public boolean singleMode;

    public McServerController(MinecraftServerLauncher launcher, MinecraftServerManager minecraftServerManager) {
        this.launcher = launcher;
        this.minecraftServerManager = minecraftServerManager;
    }

    @RequestMapping("launch-server")
    public String launch(@RequestParam("jpath") String javaPath, @RequestParam("spath") String serverPath, Session session) {
//        User user = (User) session.getAttribute("user");
//        String[] envs = null;
//        if (user ==null || !user.isAdmin()) {
//            return "Failed";
//        }
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
