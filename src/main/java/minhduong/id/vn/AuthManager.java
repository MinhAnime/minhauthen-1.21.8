package minhduong.id.vn;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.util.*;
import java.lang.reflect.Type;
import java.util.concurrent.*;

public class AuthManager {
    private static final Map<String, String> accounts = new ConcurrentHashMap<>();
    private static final Set<String> loggedIn = Collections.synchronizedSet(new HashSet<>());
    private static final Gson gson = new Gson();
    private static File dataFile;

    public static void load(File dir) {
        if (!dir.exists()) {
            dir.mkdirs(); // tạo thư mục cha nếu chưa có
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

    public static void save(){
        try(Writer writer = new FileWriter(dataFile)){
            gson.toJson(accounts, writer);
            System.out.println("[Minhauthen] Saved " + accounts.size() + " accounts.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean register(ServerPlayerEntity player, String password){
        String name = player.getName().getString();
        if (accounts.containsKey(name)) return false;
        accounts.put(name, password);
        save();
        return true;
    }

    public static boolean login(ServerPlayerEntity player, String password){
        String name = player.getName().getString();
        if (!accounts.containsKey(name)) return false;
        if (!accounts.get(name).equals(password)) return false;
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
