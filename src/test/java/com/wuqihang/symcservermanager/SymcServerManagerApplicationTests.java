package com.wuqihang.symcservermanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SymcServerManagerApplicationTests {

    @Autowired
    MCSServerLauncher mcsServerLauncher;
    @Test
    void contextLoads() {
        try {
            long java = mcsServerLauncher.launch("java", "D:\\Development\\IdeaProjects\\SYBlog\\target\\SYBlog-0.1.2.jar");
//            Process process = Runtime.getRuntime().exec("java -jar D:\\Development\\IdeaProjects\\SYBlog\\target\\SYBlog-0.1.2.jar");
            Process process = mcsServerLauncher.getProcess(java);
            MinecraftServerHandlerImpl minecraft = new MinecraftServerHandlerImpl();
            minecraft.putProcess(process);
            String s;
            while (true) {
                s = minecraft.getMessage();
                if (s != null) {
                    System.out.println(s);
                }
            }
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
