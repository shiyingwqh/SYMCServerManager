package com.wuqihang.symcservermanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SymcServerManagerApplicationTests {

    @Autowired
    MCServerLauncher mcsServerLauncher;
    @Test
    void contextLoads() {
    }

}
