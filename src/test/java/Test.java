import com.wuqihang.symcservermanager.mc.utils.MinecraftServerDownloader;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Wuqihang
 */
public class Test {

    public static void main(String[] args) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        ProcessBuilder notepad = processBuilder.command("notepad");
        Process start = notepad.start();
        Process notepad1 = Runtime.getRuntime().exec("notepad");
    }
}
