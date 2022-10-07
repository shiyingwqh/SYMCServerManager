package com.wuqihang.symcservermanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.MCSServerLauncher;
import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuqihang
 */
@RestController
public class McServerController {
    private final MCSServerLauncher launcher;
    private final ObjectMapper mapper = new ObjectMapper();

    public McServerController(MCSServerLauncher launcher) {
        this.launcher = launcher;
    }

    @RequestMapping("launch-server")
    public String launch(@RequestParam("jpath") String javaPath,@RequestParam("spath") String serverPath, @Nullable @RequestParam("env") String other, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String[] envs = null;
        if (!user.isAdmin()) {
            return "Failed";
        }
        long launch = 0;
        try {
            launch = launcher.launch(javaPath, serverPath, other);
        } catch (IOException e) {
            return e.getMessage();
        }
        return String.valueOf(launch);
    }
}
