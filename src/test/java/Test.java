import com.wuqihang.symcservermanager.mc.MinecraftServerConfig;
import com.wuqihang.symcservermanager.mc.MinecraftServerNoneProcessImpl;

import java.util.Scanner;

/**
 * @author Wuqihang
 */
public class Test {
    public static void main(String[] args) throws Exception {
        MinecraftServerConfig config = new MinecraftServerConfig();
        config.setJarPath("D:\\Desktop\\test\\server.jar");
        config.setOtherParam("");
        MinecraftServerNoneProcessImpl server = new MinecraftServerNoneProcessImpl(config);
        while (true) {
            System.out.println("::::::" + server.getMessage());
            Thread.sleep(100);
        }
    }
}
