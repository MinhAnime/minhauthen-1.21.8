package minhduong.id.vn;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.Set;

public class LoginCommand {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("login")
                    .then(CommandManager.argument("password", StringArgumentType.string())
                            .executes(commandContext ->{
                                ServerCommandSource source = commandContext.getSource();
                                if (source.getPlayer() == null) return 0;
                                var player = source.getPlayer();

                                if (!AuthManager.isRegistered(player)) {
                                    player.sendMessage(Text.of("Bạn chưa có tài khoản! Hãy dùng /register <password> <confirm> <token> để đăng ký."), false);
                                    return 0;
                                }

                                if (AuthManager.isLoggedIn(player)) {
                                    player.sendMessage(Text.of("Bạn đã đăng nhập rồi!"), false);
                                    return 0;
                                }

                                String pass = StringArgumentType.getString(commandContext, "password");

                                if (AuthManager.login(player, pass)){
                                    PlayerLocation lastloc = AuthManager.getLastLocation(player);
                                    if (lastloc != null){
                                        lastloc.loginTeleport(player);
                                    }
                                    else{
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
                                    }
                                    if (player.interactionManager.getGameMode() != GameMode.SURVIVAL){
                                        player.changeGameMode(GameMode.SURVIVAL);
                                    }
                                    player.sendMessage(Text.of("Đăng nhập thành công!"), false);

                                    player.sendMessage(Text.of("Chào mừng "+ player.getName() + " đã đến với server"), true);

                                }else{
                                    player.changeGameMode(GameMode.SPECTATOR);
                                }
                                return 1;
                            })
            ));
        });
    }
}
