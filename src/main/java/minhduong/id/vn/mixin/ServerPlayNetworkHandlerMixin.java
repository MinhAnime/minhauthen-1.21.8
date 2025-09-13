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
            handler.player.sendMessage(Text.of("§cBạn cần /login trước khi " + action + "!"), false);
            ci.cancel();
            return false;
        }
        return true;
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "di chuyển");
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "tương tác block");
    }

    @Inject(method = "onPlayerInteractItem", at = @At("HEAD"), cancellable = true)
    private void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "sử dụng item");
    }

    @Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    private void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        checkLogin((ServerPlayNetworkHandler)(Object)this, ci, "tương tác entity");
    }

}
