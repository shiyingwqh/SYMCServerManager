package com.wuqihang.symcservermanager;

import com.wuqihang.symcservermanager.config.MCConfigurer;
import com.wuqihang.symcservermanager.mc.MinecraftServer;
import com.wuqihang.symcservermanager.mc.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mc.utils.MinecraftServerDownloader;
import com.wuqihang.symcservermanager.mc.utils.MinecraftServerLauncher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SymcServerManagerApplicationTests {
    @Autowired
    MCConfigurer mcConfigurer;
//    @Autowired
//    MinecraftServerDownloader downloader;
    @Autowired
    MinecraftServer minecraftServer;
    @Autowired
    MinecraftServerConfig config;
    @Test
    void contextLoads() throws IOException, InterruptedException {
    }

}
