package minhduong.id.vn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Minhauthen implements ModInitializer {
	public static final String MOD_ID = "minhauthen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Minhauthen] Mod initialized!");
		LoadDir.register();
		Handlelogin.register();
		RegisterCommand.register();
		LoginCommand.register();
		TokenCommand.register();
		DisconnectHandler.register();
	}
}
