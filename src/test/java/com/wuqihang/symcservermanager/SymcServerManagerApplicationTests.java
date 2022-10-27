package com.wuqihang.symcservermanager;

import com.wuqihang.symcservermanager.config.MCConfigurer;
import com.wuqihang.mcserverlauncher.server.MinecraftServer;
import com.wuqihang.mcserverlauncher.config.MinecraftServerConfig;
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
