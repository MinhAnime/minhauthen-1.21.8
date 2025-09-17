package minhduong.id.vn;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;


public class DisconnectHandler {
    public static void register(){
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            AuthManager.logout(server, player);
        });
    }
}
