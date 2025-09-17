package minhduong.id.vn;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Set;

public class PlayerLocation {
    public String world;
    public double x, y, z;
    public float yaw, pitch;
    public PlayerLocation(ServerPlayerEntity player) {
        this.world = player.getWorld().getRegistryKey().getValue().toString();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
    }
    public void loginTeleport(ServerPlayerEntity player) {
        try {
            Identifier worldId = Identifier.of(world);
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, worldId);
            ServerWorld targetWorld = player.getServer().getWorld(worldKey);

            if (targetWorld == null) {
                targetWorld = player.getServer().getOverworld(); // fallback
            }
            player.teleport(targetWorld, x, y, z, Set.of(), yaw, pitch, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
