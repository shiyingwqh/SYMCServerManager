package com.wuqihang.symcservermanager.controller;

import com.wuqihang.symcservermanager.mcserverlauncher.ForgeMinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.MinecraftServerException;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Wuqihang
 */
@Controller
@ConditionalOnMissingBean(MinecraftServer.class)
public class ServerController {
    private final MinecraftServerManagerImpl minecraftServerManager;
    private final MinecraftServerDownloader downloader;

    public ServerController(@Autowired(required = false) MinecraftServerManagerImpl minecraftServerManager, MinecraftServerDownloader downloader) {
        this.minecraftServerManager = minecraftServerManager;
        this.downloader = downloader;
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
    public String addInstance(Model model) {
        model.addAttribute("versions", downloader.getAllId());
        return "new_instance";
    }

    @PostMapping("/new")
    @ResponseBody
    public MinecraftServerConfig newInstance(@RequestParam String name, @RequestParam String jvm, @RequestParam String javaPath, @RequestParam String version, @RequestParam String comment) throws ExecutionException, InterruptedException {
        MinecraftServerConfig config = new ForgeMinecraftServerConfig();
        config.setName(name);
        config.setJavaPath(javaPath);
        config.setComment(comment);
        File serverHome = new File("servers/" + name);
        serverHome.mkdirs();
        config.setServerHomePath(serverHome.getAbsolutePath());
        Boolean aBoolean = downloader.download(version, serverHome.getAbsolutePath(), null).get();
        config.setJarPath(new File(serverHome, "server.jar").getAbsolutePath());
        minecraftServerManager.putConfig(config);
        return config;
    }

    @RequestMapping("/forge/{version}")
    @ResponseBody
    public String forge(@PathVariable String version, HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        URI uri = new URI("https://bmclapi2.bangbang93.com/forge/minecraft/" + version);
        HttpsURLConnection urlConnection = (HttpsURLConnection) uri.toURL().openConnection();
        if (urlConnection.getResponseCode() != 200) {
            response.setStatus(urlConnection.getResponseCode());
            return null;
        }
        byte[] bytes = urlConnection.getInputStream().readAllBytes();
        return new String(bytes);
    }
}
