package minhduong.id.vn;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.io.File;
import java.nio.file.Path;


public class LoadDir {
    public static File configDir;
    public static void register (){
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path configPath = server.getRunDirectory().resolve("config/minhauthen");
            configDir = configPath.toFile();
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            AuthManager.load(configDir);
            TokenManager.loadTokens();
            System.out.println("[Minhauthen] AuthManager loaded from " + configDir.getAbsolutePath());
        });
    }
}
