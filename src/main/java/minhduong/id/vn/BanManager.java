package minhduong.id.vn;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Date;

public class BanManager {
    public static void banPlayer(ServerCommandSource source, GameProfile profile, String ip, String reason) {
        var server = source.getServer();
        assert server != null;
        var banList = server.getPlayerManager().getUserBanList();
        var ipBanList = server.getPlayerManager().getIpBanList();

        BannedPlayerEntry playerEntry = new BannedPlayerEntry(
                profile,
                new Date(),
                "Minh đẹp trai",
                null,
                reason
        );
        banList.add(playerEntry);
        BannedIpEntry ipEntry = new BannedIpEntry(
                ip,
                new Date(),
                "Minhauthen",
                null,
                reason
        );
        ipBanList.add(ipEntry);
        var player = server.getPlayerManager().getPlayer(profile.getId());
        if (player != null) {
            player.networkHandler.disconnect(Text.of("Mày đã bị ban vĩnh viễn " + reason));
        }
    }
}

