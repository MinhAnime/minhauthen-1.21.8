package minhduong.id.vn;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

public class AuthManager {
    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final Map<String, String> accounts = new ConcurrentHashMap<>();
    private static final Set<String> loggedIn = ConcurrentHashMap.newKeySet();
    private static final Map<String, PlayerLocation> lastLocations = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static File dataFile;
    private static File locFile;

    private static ScheduledExecutorService scheduler;

    public static void load(File dir) {
        if (!dir.exists()) dir.mkdirs();

        dataFile = new File(dir, "users.json");
        locFile = new File(dir, "last_locations.json");

        loadFile(dataFile, new TypeToken<Map<String, String>>() {}.getType(), accounts);
        loadFile(locFile, new TypeToken<Map<String, PlayerLocation>>() {}.getType(), lastLocations);

        // Khởi tạo scheduler save định kỳ 30s
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                saveAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private static <T> void loadFile(File file, Type type, Map<String, T> map) {
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (Writer writer = new FileWriter(file)) {
                    gson.toJson(new HashMap<String, T>(), writer);
                }
            } else {
                try (Reader reader = new FileReader(file)) {
                    Map<String, T> loaded = gson.fromJson(reader, type);
                    if (loaded != null) map.putAll(loaded);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() throws IOException {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(accounts, writer);
        }
        try (Writer writer = new FileWriter(locFile)) {
            gson.toJson(lastLocations, writer);
        }
    }

    public static boolean register(ServerPlayerEntity player, String password){
        String name = player.getName().getString();
        if (accounts.containsKey(name)) return false;
        accounts.put(name, password);
        return true; // lưu định kỳ, không cần save ngay
    }

    public static boolean login(ServerPlayerEntity player, String password){
        String name = player.getName().getString();
        if (!accounts.containsKey(name)) return false;

        if (!accounts.get(name).equals(password)){
            int attempts = failedAttempts.getOrDefault(name, 0) +1;
            failedAttempts.put(name, attempts);
            if (attempts >= 5) {
                BanManager.banPlayer(
                        player.getCommandSource(),
                        player.getGameProfile(),
                        player.getIp(),
                        "Nhập sai mật khẩu quá nhiều lần"
                );
                failedAttempts.remove(name);
                return false;
            } else {
                player.sendMessage(Text.of("Sai mật khẩu! Còn "+(5 - attempts)+" lần thử"), false);
                return false;
            }
        }
        failedAttempts.remove(name);
        loggedIn.add(name);
        return true;
    }

    public static boolean isRegistered(ServerPlayerEntity player){
        return accounts.containsKey(player.getName().getString());
    }

    public static boolean isLoggedIn(ServerPlayerEntity player){
        return loggedIn.contains(player.getName().getString());
    }

    public static void logout(ServerPlayerEntity player){
        saveLastLocation(player);
        loggedIn.remove(player.getName().getString());
    }

    public static void saveLastLocation(ServerPlayerEntity player){
        lastLocations.put(player.getName().getString(), new PlayerLocation(player));
    }

    public static PlayerLocation getLastLocation(ServerPlayerEntity player){
        return lastLocations.get(player.getName().getString());
    }

    // Stop scheduler và save ngay khi server tắt
    public static void stopAndSave(MinecraftServer server){
        if (scheduler != null) scheduler.shutdownNow();
        server.execute(() -> {
            try {
                saveAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
