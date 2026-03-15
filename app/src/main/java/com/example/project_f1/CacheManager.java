package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.io.File;

public class CacheManager {
    private static final String CACHE_PREFS = "F1Cache";
    private static final String CACHE_DIR = "f1_cache";
    private static final long CACHE_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    
    private static SharedPreferences getCachePrefs(Context context) {
        return context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE);
    }
    
    public static void saveCache(Context context, String key, String data) {
        SharedPreferences prefs = getCachePrefs(context);
        prefs.edit()
            .putString(key, data)
            .putLong(key + "_time", System.currentTimeMillis())
            .apply();
    }
    
    public static String getCache(Context context, String key) {
        SharedPreferences prefs = getCachePrefs(context);
        long cacheTime = prefs.getLong(key + "_time", 0);
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - cacheTime > CACHE_DURATION) {
            prefs.edit().remove(key).remove(key + "_time").apply();
            return null;
        }
        
        return prefs.getString(key, null);
    }
    
    public static void clearAllCache(Context context) {
        SharedPreferences prefs = getCachePrefs(context);
        prefs.edit().clear().apply();
        
        File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        if (cacheDir.exists()) {
            deleteDir(cacheDir);
        }
    }
    
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
                }
            }
        }
        return dir.delete();
    }
}
