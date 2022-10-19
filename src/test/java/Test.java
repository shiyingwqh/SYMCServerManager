import com.wuqihang.symcservermanager.mc.utils.MinecraftServerDownloader;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Wuqihang
 */
public class Test {

    public static void main(String[] args) throws Exception {
        MinecraftServerDownloader minecraftServerDownloader = new MinecraftServerDownloader();
        List<String> allId = minecraftServerDownloader.getAllId();
        System.out.println(allId);
        System.out.println(minecraftServerDownloader.getUrl(allId.get(0)));
        Future<Boolean> download = minecraftServerDownloader.download(allId.get(0), "D:\\Desktop");
        System.out.println(download.get());
    }
}
