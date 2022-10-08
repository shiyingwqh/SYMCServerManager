package com.wuqihang.symcservermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuqihang.symcservermanager.MCServerLauncher;
import com.wuqihang.symcservermanager.ServerProcessManager;
import com.wuqihang.symcservermanager.pojo.User;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Wuqihang
 */
@RestController
public class McServerController {
    private final MCServerLauncher launcher;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ServerProcessManager serverProcessManager;

    public McServerController(MCServerLauncher launcher, ServerProcessManager serverProcessManager) {
        this.launcher = launcher;
        this.serverProcessManager = serverProcessManager;
    }

    @RequestMapping("launch-server")
    public String launch(@RequestParam("jpath") String javaPath,@RequestParam("spath") String serverPath, @Nullable @RequestParam("env") String other, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String[] envs = null;
        if (!user.isAdmin()) {
            return "Failed";
        }
        Process process;
        try {
            process = launcher.launch(javaPath, serverPath, other);
             serverProcessManager.addProcess(process);
        } catch (IOException e) {
            return e.getMessage();
        }
        return process.toString();
    }
}
