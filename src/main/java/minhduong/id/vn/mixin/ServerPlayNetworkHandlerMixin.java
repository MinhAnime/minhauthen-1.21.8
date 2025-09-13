package minhduong.id.vn.mixin;


import minhduong.id.vn.AuthManager;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler a = (ServerPlayNetworkHandler)(Object)this;
        if (!AuthManager.isLoggedIn(a.player)){
            a.player.sendMessage(net.minecraft.text.Text.of("§cBạn cần /login trước khi di chuyển!"), false);
            ci.cancel();
        }
    }
}
