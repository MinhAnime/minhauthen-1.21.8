package minhduong.id.vn;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class Handlelogin {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static void register(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.player;
            scheduler.schedule(() -> {
                server.execute(() -> {
                    if (!AuthManager.isLoggedIn(player)) {
                        player.networkHandler.disconnect(Text.of("Vào treo máy thì cút đi"));
                    }
                });
            }, 30, TimeUnit.SECONDS);
        });
    }
}
