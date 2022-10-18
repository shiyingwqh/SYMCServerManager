package com.wuqihang.symcservermanager;

import com.wuqihang.symcservermanager.mc.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mc.MinecraftServerLauncher;
import com.wuqihang.symcservermanager.mc.MinecraftServerNoneProcessImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SymcServerManagerApplicationTests {

    @Autowired
    MinecraftServerLauncher mcsServerLauncher;
    @Test
    void contextLoads(){
    }

}
