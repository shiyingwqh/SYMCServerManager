import com.wuqihang.mcserverlauncher.MinecraftServerLauncher;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManager;

import java.io.File;

/**
 * @author Wuqihang
 */
public class Test {

    public static void main(String[] args) throws Exception {
        MinecraftServerLauncher instance = MinecraftServerLauncher.getInstance();
        MinecraftServerManager manager = instance.getManager();
        System.out.println(manager);
        MinecraftServerDownloader downloader = instance.getDownloader();
        System.out.println(downloader);
        System.out.println("       ");
    }
}
