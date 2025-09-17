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
    private static final Map<String, PlayerLocation> lastLocations = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static File dataFile;
    private static File locFile;

    public static void load(File dir) {
        if (!dir.exists()) dir.mkdirs();

        dataFile = new File(dir, "users.json");
        locFile = new File(dir, "last_locations.json");

        if (!dataFile.exists()) {
            try {
                // tạo file rỗng
                dataFile.createNewFile();
                try (Writer writer = new FileWriter(dataFile)) {
                    gson.toJson(new HashMap<String, String>(), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> loaded = gson.fromJson(reader, type);
            if (loaded != null) accounts.putAll(loaded);
        }
        catch (Exception e) {
            e.printStackTrace();
            }
        }

        if (!locFile.exists()) {
            try{
                locFile.createNewFile();
                try (Writer writer = new FileWriter(locFile)) {
                    gson.toJson(new HashMap<String, double[]>(), writer);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try (Reader reader = new FileReader(locFile)){
                Type type = new TypeToken<Map<String, PlayerLocation>>() {}.getType();
                Map<String, PlayerLocation> loaded = gson.fromJson(reader, type);
                if (loaded != null) lastLocations.putAll(loaded);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveAccounts() throws IOException {
        try(Writer writer = new FileWriter(dataFile)){
            gson.toJson(accounts, writer);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void scheduleSaveAccounts(MinecraftServer server){
        server.execute(()->{
            try{
                saveAccounts();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    public static void saveLocation() throws IOException {
        try(Writer writer = new FileWriter(locFile)){
            gson.toJson(lastLocations, writer);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void scheduleSaveLocation(MinecraftServer server){
        server.execute(() -> {
            try{
                saveLocation();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public static boolean register(MinecraftServer server, ServerPlayerEntity player, String password){
        String name = player.getName().getString();
        if (accounts.containsKey(name)) return false;
        accounts.put(name, password);
        scheduleSaveAccounts(server);
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
    public static void logout(MinecraftServer server, ServerPlayerEntity player){
        saveLastLocation(server, player);
        loggedIn.remove(player.getName().getString());
    }
    public static void saveLastLocation(MinecraftServer server, ServerPlayerEntity player){
        lastLocations.put(player.getName().getString(), new PlayerLocation(player));
        scheduleSaveLocation(server);
    }
    public static PlayerLocation getLastLocation(ServerPlayerEntity player){
        return lastLocations.get(player.getName().getString());
    }

}
