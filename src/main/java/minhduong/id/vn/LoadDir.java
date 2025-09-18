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
            TokenManager.startAutoSave(server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            System.out.println("[MinhAuthen] Saving data before shutdown...");
           AuthManager.stopAndSave(server);
           TokenManager.stopAndSave(server);
           System.out.println("[MinhAuthen] Data saved, shutdown complete.");
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            System.out.println("[MinhAuthen] Đã dừng hoàn toàn (STOPPED).");
        });

    }
}
