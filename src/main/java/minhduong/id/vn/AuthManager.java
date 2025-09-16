package minhduong.id.vn;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.*;
import java.util.*;
import java.lang.reflect.Type;
import java.util.concurrent.*;

public class AuthManager {
    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final Map<String, String> accounts = new ConcurrentHashMap<>();
    private static final Set<String> loggedIn = ConcurrentHashMap.newKeySet();
    private static final Gson gson = new Gson();
    private static File dataFile;

    public static void load(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        dataFile = new File(dir, "users.json");
        if (!dataFile.exists()) {
            try {
                // tạo file rỗng
                dataFile.createNewFile();
                try (Writer writer = new FileWriter(dataFile)) {
                    gson.toJson(new HashMap<String, String>(), writer);
                }
                System.out.println("[Minhauthen] Created new users.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Nếu file đã có thì đọc
        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> loaded = gson.fromJson(reader, type);
            if (loaded != null) accounts.putAll(loaded);
            System.out.println("[Minhauthen] Loaded " + accounts.size() + " accounts.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() throws IOException {
        try(Writer writer = new FileWriter(dataFile)){
            gson.toJson(accounts, writer);
            System.out.println("[Minhauthen] Saved " + accounts.size() + " accounts.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void scheduleSave(MinecraftServer server){
        server.execute(()->{
            try{
                save();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    public static boolean register(MinecraftServer server, ServerPlayerEntity player, String password){
        String name = player.getName().getString();
        if (accounts.containsKey(name)) return false;
        accounts.put(name, password);
        scheduleSave(server);
        return true;
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
                        "Trình gà"
                );
                failedAttempts.remove(name);
                return false;
            }else{
                try{
                    player.sendMessage(Text.of("Sai mật khẩu! Còn "+(5 - attempts) + " lần thử"), false);
                }
                catch(Exception ignored) {}
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
        loggedIn.remove(player.getName().getString());
    }

}
