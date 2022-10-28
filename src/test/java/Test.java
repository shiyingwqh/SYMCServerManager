import com.wuqihang.mcserverlauncher.MinecraftServerLauncher;
import com.wuqihang.mcserverlauncher.config.ForgeMinecraftServerConfig;
import com.wuqihang.mcserverlauncher.utils.FabricServerInstaller;
import com.wuqihang.mcserverlauncher.utils.ForgeServerInstaller;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.mcserverlauncher.utils.MinecraftServerManager;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Wuqihang
 */
public class Test {

    public static void main(String[] args) throws Exception {
        MinecraftServerLauncher instance = MinecraftServerLauncher.getInstance();
//        MinecraftServerManager manager = instance.getManager();
//        System.out.println(manager);
        MinecraftServerDownloader downloader = instance.getDownloader();
        System.out.println(downloader);
        System.out.println("       ");
        downloader.init();
//        FutureTask<Boolean> download = downloader.download("1.16.5", "/Users/wuqihang/minecraft-server-1.16.5");
//        System.out.println(download.get());
        FutureTask<Boolean> task = downloader.downloadForgeInstaller("1.16.5", "36.1.65", "installer", "jar", "/Users/wuqihang/minecraft-server-1.16.5");
        System.out.println(task.get());
        ForgeMinecraftServerConfig forgeMinecraftServerConfig = new ForgeMinecraftServerConfig();
        forgeMinecraftServerConfig.setJavaPath(System.getProperty("java.home") + "/bin/java");
        forgeMinecraftServerConfig.setServerHomePath("/Users/wuqihang/minecraft-server-1.16.5");
        Future<ForgeMinecraftServerConfig> install = ForgeServerInstaller.install("/Users/wuqihang/minecraft-server-1.16.5/forge_installer.jar", forgeMinecraftServerConfig);
        System.out.println(install.get());
        downloader.destroy();
    }
}
