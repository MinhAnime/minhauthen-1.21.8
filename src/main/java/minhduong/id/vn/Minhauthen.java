package minhduong.id.vn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class Minhauthen implements ModInitializer {
	public static final String MOD_ID = "minhauthen";
	public static File configDir;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Minhauthen] Mod initialized!");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Path configPath = server.getRunDirectory().resolve("config/minhauthen");
			configDir = configPath.toFile();
			if (!configDir.exists()) {
				configDir.mkdirs();
			}
			AuthManager.load(configDir);
			LOGGER.info("[Minhauthen] AuthManager loaded from " + configDir.getAbsolutePath());
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.player;
			AuthManager.logout(player);
			LOGGER.info("[Minhauthen] Player {} logged out.", player.getName().getString());
		});

		RegisterCommand.register();
		LoginCommand.register();
	}
}
