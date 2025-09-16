package minhduong.id.vn;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.Set;

public class PlayerJoinHandler {
    public void register(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            if (!AuthManager.isLoggedIn(player)) {
                ServerWorld overworld = player.getServer().getOverworld();
                BlockPos pos = overworld.getSpawnPos();

                player.teleport(
                        overworld,
                        pos.getX() +0.5f,
                        pos.getY(),
                        pos.getZ() + 0.5f,
                        Set.of(),
                        player.getYaw(),
                        player.getPitch(),
                        false
                );
                player.changeGameMode(GameMode.SPECTATOR);
            }
        });
    }
}
