package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.List;

/**
 * @author Wuqihang
 */
@ConditionalOnBean(MinecraftServer.class)
@Controller
public class McServerController2 extends McServerController{
    private final MinecraftServer minecraftServer;

    public McServerController2(MinecraftServer minecraftServer) {
        super(null);
        this.minecraftServer = minecraftServer;
    }

    @Override
    public String launch(String javaPath, String serverPath, Session session) {
        return "";
    }

    @Override
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("servers", List.of(new MinecraftServer[]{minecraftServer}));
        model.addAttribute("singleMode", true);
        return "index";
    }
}
