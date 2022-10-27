package com.wuqihang.symcservermanager.controller;

import com.wuqihang.mcserverlauncher.config.ForgeMinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
import com.wuqihang.mcserverlauncher.server.MinecraftServerException;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManagerImpl;
import com.wuqihang.symcservermanager.services.MinecraftServerConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Wuqihang
 */
@Controller
@ConditionalOnMissingBean(MinecraftServer.class)
public class ServerController {
    private final MinecraftServerManagerImpl minecraftServerManager;

    private final MinecraftServerConfigService serverConfigService;

    private final Map<String, Future<MinecraftServerConfig>> tasks = new Hashtable<>();

    public ServerController(MinecraftServerManagerImpl minecraftServerManager, MinecraftServerConfigService serverConfigService) {
        this.minecraftServerManager = minecraftServerManager;
        this.serverConfigService = serverConfigService;
    }

    @RequestMapping("terminal/{pid}")
    public String terminal(@PathVariable("pid") String pid, Model model) {
        model.addAttribute("id", pid);
        return "terminal";
    }

    @RequestMapping({"/index", "/"})
    public String index(Model model) {
        List<MinecraftServerConfig> allConfigs = minecraftServerManager.getAllConfigs();
        model.addAttribute("configs", allConfigs);
        return "index";
    }

    @RequestMapping("/add_instance")
    public String addInstance(Model model) {
        model.addAttribute("versions", serverConfigService.getAllId());
        return "new_instance";
    }

    @PostMapping("/new")
    @ResponseBody
    public Res newInstance(@RequestParam String name,
                           @RequestParam String jvm,
                           @RequestParam String javaPath,
                           @RequestParam String version,
                           @RequestParam String comment,
                           @RequestParam("install_forge") @Nullable String installForge,
                           @RequestParam("forge_version") @Nullable String forgeVersion) throws ExecutionException, InterruptedException, IOException {
        MinecraftServerConfig config;
        File serverHome = new File("servers/" + name);
        boolean mkdirs = serverHome.mkdirs();
        if (!mkdirs) {
            return new Res(400, "Server Instance Exist");
        }
        if (Objects.equals(installForge, "on") && forgeVersion != null) {
            config = new ForgeMinecraftServerConfig();
            ((ForgeMinecraftServerConfig) config).setForgeVersion(forgeVersion);
        } else {
            config = new MinecraftServerConfig();
        }
        config.setName(name);
        config.setJavaPath(javaPath);
        config.setJvmParam(jvm);
        config.setComment(comment);
        config.setServerHomePath(serverHome.getAbsolutePath());
        config.setVersion(version);
        FutureTask<MinecraftServerConfig> init = serverConfigService.initConfig(config, version, serverHome.getAbsolutePath(), forgeVersion);
        tasks.put(name, init);
        File serverJar = new File(serverHome, "server.jar");
        config.setJarPath(serverJar.getAbsolutePath());
        minecraftServerManager.putConfig(config);
        return new Res(200, "Server Instance Creating");
    }

    @RequestMapping("/forge/{version}")
    @ResponseBody
    public String forge(@PathVariable String version, HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        URI uri = new URI("https://bmclapi2.bangbang93.com/forge/minecraft/" + version);
        HttpsURLConnection urlConnection = (HttpsURLConnection) uri.toURL().openConnection();
        urlConnection.connect();
        if (urlConnection.getResponseCode() != 200) {
            response.setStatus(urlConnection.getResponseCode());
            return null;
        }
        byte[] bytes = urlConnection.getInputStream().readAllBytes();
        urlConnection.disconnect();
        return new String(bytes);
    }

    @RequestMapping("/run")
    @ResponseBody
    public String run(@RequestParam String configName) throws MinecraftServerException {
        Future<MinecraftServerConfig> task = tasks.get(configName);
        if (task != null && !task.isDone()) {
            return "Config Initialization";
        }
        minecraftServerManager.launch(configName);
        return "OK";
    }

    @RequestMapping("/server_detail/{config_name}")
    public String serverDetail(@PathVariable("config_name") String configName, Model model, HttpServletResponse response) throws IOException, MinecraftServerException {
        MinecraftServerConfig config = minecraftServerManager.getConfig(configName);
        if (config == null) {
            response.sendError(404);
            return null;
        }
        model.addAttribute("config", config);
        MinecraftServer minecraftServer = minecraftServerManager.getServer(configName);
        if (minecraftServer == null) {
            minecraftServer = minecraftServerManager.create(configName);
        }
        model.addAttribute("server", minecraftServer);
        return "serverDetail";
    }

    @PostMapping("/start_server")
    @ResponseBody
    public Res startServer(@RequestParam("id") String configName) throws IOException {
        MinecraftServer server = minecraftServerManager.getServer(configName);
        if (server == null) {
            return new Res(400, "Server Not Exist");
        }
        server.start();
        return new Res(200, "OK");
    }
}
