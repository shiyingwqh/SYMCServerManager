package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerConfig;
import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author Wuqihang
 */
@Controller
@ConditionalOnBean(MinecraftServer.class)
public class SingleModeServerController {
    private final MinecraftServer minecraftServer;
    private final MinecraftServerConfig config;

    public SingleModeServerController(MinecraftServer minecraftServer, MinecraftServerConfig config) {
        this.minecraftServer = minecraftServer;
        this.config = config;
    }

    @RequestMapping("/index")
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("server", minecraftServer);
        model.addAttribute("config", config);
        return "singleIndex";
    }
}
