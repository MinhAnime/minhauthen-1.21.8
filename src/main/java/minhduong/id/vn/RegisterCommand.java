package minhduong.id.vn;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RegisterCommand {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(CommandManager.literal("register")
                            .then(CommandManager.argument("password", StringArgumentType.string())
                                    .executes(commandContext ->{
                                        ServerCommandSource source = commandContext.getSource();
                                        if (source.getPlayer() == null) return 0;
                                        var player = source.getPlayer();

                                        if (AuthManager.isRegistered(player)) {
                                            player.sendMessage(Text.of("\"§cBạn đã đăng ký rồi, hãy dùng /login.\""), false);
                                            return 0;
                                        }
                                        String pass = StringArgumentType.getString(commandContext, "password");
                                        if (AuthManager.register(player, pass)) {
                                            player.sendMessage(Text.of("§aĐăng ký thành công! Hãy dùng /login để vào game."), false);
                                        }else {
                                            player.sendMessage(Text.of("\"§cĐăng ký thất bại.\""), false);
                                        }
                                        return 1;
                                    })
                            ));
                });
    }
}
