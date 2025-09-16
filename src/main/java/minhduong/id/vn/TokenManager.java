package minhduong.id.vn;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TokenManager {
    private static File tokenFile;
    private static Map<String, Boolean> tokens = new ConcurrentHashMap<>();
    public static void loadTokens() {
        tokenFile = new File(LoadDir.configDir, "tokens.json");
        if (!tokenFile.exists()) {
            try{
                tokenFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{

        }
    }
}
