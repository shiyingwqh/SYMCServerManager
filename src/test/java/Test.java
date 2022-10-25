import com.wuqihang.symcservermanager.mcserverlauncher.ForgeMinecraftServer;
import com.wuqihang.symcservermanager.mcserverlauncher.ForgeMinecraftServerConfig;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.ForgeServerInstaller;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerDownloader;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerLauncher;
import com.wuqihang.symcservermanager.mcserverlauncher.utils.MinecraftServerManager;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;

/**
 * @author Wuqihang
 */
public class Test {

    public static void main(String[] args) throws Exception {
        MinecraftServerDownloader downloader = MinecraftServerLauncher.getInstance().getDownloader();
        downloader.init();
        downloader.download("1.19.2", "./", new BiFunction<Long, Long, Void>() {
            @Override
            public Void apply(Long aLong, Long aLong2) {
                System.out.println(aLong / (double) aLong2);
                return null;
            }
        });
    }
}
