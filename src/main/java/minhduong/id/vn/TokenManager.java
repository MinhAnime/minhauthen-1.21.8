package minhduong.id.vn;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;

import static com.mojang.text2speech.Narrator.LOGGER;

public class TokenManager {
    private static final Gson gson = new Gson();
    private static File tokenFile;
    private static Map<String, Boolean> tokens = new ConcurrentHashMap<>();

    public static void loadTokens() {
        tokenFile = new File(LoadDir.configDir, "tokens.json");
        if (!tokenFile.exists()) {
            try {
                tokenFile.createNewFile();
                try (Writer writer = new FileWriter(tokenFile)) {
                    gson.toJson(new HashMap<String, Boolean>(), writer);
                }
                LOGGER.info("Tokens file created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Reader reader = new FileReader(tokenFile)) {
                Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
                Map<String, Boolean> loaded = gson.fromJson(reader, type);
                if (loaded != null) tokens.putAll(loaded);
                LOGGER.info("Tokens loaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveTokens() throws IOException {
        try (Writer writer = new FileWriter(tokenFile)) {
            gson.toJson(tokens, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scheduleSaveTokens(MinecraftServer server) {
        server.execute(() -> {
            try {
                saveTokens();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Kiểm tra token hợp lệ và chưa được dùng
    public static boolean isTokenValid(String token) {
        return tokens.containsKey(token) && !tokens.get(token);
    }

    // Đánh dấu token là đã dùng
    public static void markUsedToken(String token, MinecraftServer server) {
        if (tokens.containsKey(token)) {
            tokens.put(token, true);
            scheduleSaveTokens(server);
        }
    }

    // Thêm token mới, trả về true nếu thành công, false nếu đã tồn tại
    public static boolean addToken(String token, MinecraftServer server) {
        if (tokens.containsKey(token)) return false;
        tokens.put(token, false);
        scheduleSaveTokens(server);
        return true;
    }
}
