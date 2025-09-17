package minhduong.id.vn;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TokenCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("addToken")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(
                                    CommandManager.argument("token", StringArgumentType.string())
                                            .executes(context -> {
                                                ServerCommandSource source = context.getSource();
                                                String token = StringArgumentType.getString(context, "token");

                                                if (TokenManager.addToken(token, source.getServer())) {
                                                    TokenManager.scheduleSaveTokens(source.getServer());
                                                    source.sendFeedback(() -> Text.of("Token '"+token+"' đã thêm thành công!"), false);
                                                } else {
                                                    source.sendFeedback(() -> Text.of("Token đã tồn tại!"), false);
                                                }
                                                return 1;
                                            })
                            )
            );
        });
    }
}
