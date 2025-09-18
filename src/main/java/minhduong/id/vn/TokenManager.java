package minhduong.id.vn;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;



public class TokenManager {
    private static ScheduledExecutorService scheduler;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Reader reader = new FileReader(tokenFile)) {
                Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
                Map<String, Boolean> loaded = gson.fromJson(reader, type);
                if (loaded != null) tokens.putAll(loaded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveTokens() throws IOException {
        try (Writer writer = new FileWriter(tokenFile)) {
            gson.toJson(tokens, writer);
        }
    }

    // Kiểm tra token hợp lệ và chưa được dùng
    public static boolean isTokenValid(String token) {
        return tokens.containsKey(token) && !tokens.get(token);
    }

    // Đánh dấu token là đã dùng
    public static boolean markUsedToken(String token) {
        return tokens.computeIfPresent(token, (k, v) -> true) != null;
    }

    public static boolean addToken(String token) {
         return tokens.putIfAbsent(token, false) == null;
    }
    public static boolean tokenExists(String token) {
        return tokens.containsKey(token);
    }

    public static void startAutoSave(MinecraftServer server) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            server.execute(() -> { // chạy trên server thread
                try {
                    saveTokens();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }, 30, 30, TimeUnit.SECONDS);
    }

    // Stop scheduler và save khi server tắt
    public static void stopAndSave(MinecraftServer server) {
        if (scheduler != null) {
            scheduler.shutdownNow();
            try {
                // Chờ tối đa 5 giây cho các task kết thúc
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Scheduler không tắt kịp, sẽ ép buộc dừng.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        server.execute(() -> {
            try {
                saveTokens();
                System.out.println("[TokenManager] Tokens saved on shutdown.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
