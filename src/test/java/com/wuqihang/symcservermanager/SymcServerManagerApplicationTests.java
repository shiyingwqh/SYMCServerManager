package com.wuqihang.symcservermanager;

import com.wuqihang.symcservermanager.config.MCConfigurer;
import com.wuqihang.symcservermanager.mc.utils.MinecraftServerDownloader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SymcServerManagerApplicationTests {
    @Autowired
    MCConfigurer mcConfigurer;
    @Autowired
    MinecraftServerDownloader downloader;
    @Test
    void contextLoads(){
        System.out.println("aaa");
        System.out.println(mcConfigurer.singleMode);
    }

}
