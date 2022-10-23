package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerException;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;

/**
 * @author Wuqihang
 */
@Controller
@ConditionalOnMissingBean(MinecraftServer.class)
public class ServerController {
    private final MinecraftServerManagerImpl minecraftServerManager;

    public ServerController(@Autowired(required = false) MinecraftServerManagerImpl minecraftServerManager) {
        this.minecraftServerManager = minecraftServerManager;
    }

    @RequestMapping("launch-server")
    @ResponseBody
    public String launch(@RequestParam("jpath") String javaPath, @RequestParam("spath") String serverPath, Session session) {

        MinecraftServer minecraftServer;
        MinecraftServerConfig minecraftServerConfig = new MinecraftServerConfig();
        minecraftServerConfig.setJarPath(javaPath);
        minecraftServerConfig.setServerHomePath(javaPath);
        try {
            minecraftServer = minecraftServerManager.launch(minecraftServerConfig);
        } catch (MinecraftServerException e) {
            return e.getMessage();
        }
        return minecraftServer.toString();
    }

    @RequestMapping("terminal/{pid}")
    public String terminal(@PathVariable("pid") String pid, Model model) {
        model.addAttribute("id", pid);
        return "terminal";
    }

    @RequestMapping({"/index", "/"})
    public String index(Model model) {
        List<MinecraftServer> servers = minecraftServerManager.getAllServer();
        Map<MinecraftServer, MinecraftServerConfig> map = minecraftServerManager.getServerConfigMap();
        model.addAttribute("servers", servers);
        return "index";
    }

    @RequestMapping("/add_instance")
    public String addInstance() {
        return "new_instance";
    }
}
