package minhduong.id.vn.mixin;

import minhduong.id.vn.AuthManager;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Unique
    private static final Map<String, Long> lastWarn = new ConcurrentHashMap<>();

    @Unique
    private void checkLogin(ServerPlayNetworkHandler handler, CallbackInfo ci, String action) {
        String name = handler.player.getName().getString();
        if (!AuthManager.isLoggedIn(handler.player)) {
            if(handler.player.interactionManager.getGameMode() != GameMode.SPECTATOR){
                handler.player.changeGameMode(GameMode.SPECTATOR);
            }

            // Teleport về spawn để tránh di chuyển tự do
            var overworld = handler.player.getServer().getOverworld();
            var pos = overworld.getSpawnPos();
            handler.player.teleport(
                    overworld,
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    Set.of(),
                    0.0f,
                    0.0f,
                    true);

            long now = System.currentTimeMillis();
            long last = lastWarn.getOrDefault(name, 0L);
            if (now - last > 3000) {
                handler.player.sendMessage(Text.of("Bạn cần /login trước khi " + action + "!"), false);
                lastWarn.put(name, now);
            }
            ci.cancel();
            return;
        }
        lastWarn.remove(name);
    }

    // Chặn di chuyển
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
            checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "di chuyển");
    }

    // Chặn phá block, drop item
    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "phá block hoặc hành động");
    }

    // Chặn đặt block
    @Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
    private void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "đặt block");
    }

    // Chặn dùng item trên không
    @Inject(method = "onPlayerInteractItem", at = @At("HEAD"), cancellable = true)
    private void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "sử dụng item");
    }

    // Chặn tương tác entity
    @Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    private void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "tương tác entity");
    }

    // Chặn chat khi chưa login
    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "chat");
    }

    // Chặn command ngoại trừ /login và /register
    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    private void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        String command = packet.command();
        String cmd = command.split(" ")[0];
        if (!(cmd.startsWith("login") || cmd.startsWith("register"))) {
            checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "dùng lệnh");
        }
    }
}
