package minhduong.id.vn;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class LoginCommand {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("login")
                    .then(CommandManager.argument("password", StringArgumentType.string())
                            .executes(commandContext ->{
                                ServerCommandSource source = commandContext.getSource();
                                if (source.getPlayer() == null) return 0;
                                var player = source.getPlayer();
                                String pass = StringArgumentType.getString(commandContext, "password");
                                if (AuthManager.login(player, pass)){
                                    player.sendMessage(Text.of("Đăng nhập thành công!"), false);
                                }
                                return 1;
                            })
            ));
        });
    }
}
