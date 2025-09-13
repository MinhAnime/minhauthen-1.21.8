package minhduong.id.vn.mixin;

import minhduong.id.vn.AuthManager;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    private boolean checkLogin(ServerPlayNetworkHandler handler, CallbackInfo ci, String action) {
        if (!AuthManager.isLoggedIn(handler.player)) {
            handler.player.sendMessage(Text.of("Bạn cần /login trước khi " + action + "!"), false);
            ci.cancel();
            return false;
        }
        return true;
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
        if (!(command.startsWith("login") || command.startsWith("register"))) {
            checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "dùng lệnh");
        }
    }
}
