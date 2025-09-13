package minhduong.id.vn;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.C2SConfigurationChannelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class Minhauthen implements ModInitializer {
	public static final String MOD_ID = "minhanime/authenmod";
	public static File configDir;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Minhauthen initialized");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Path configPath = server.getRunDirectory().resolve("config/minhauthen");
			File configDir = configPath.toFile();
			if (!configDir.exists()) {
				configDir.getParentFile().mkdirs();
			}
			AuthManager.load(configDir);
		});
		RegisterCommand.register();
		LoginCommand.register();
	}
}