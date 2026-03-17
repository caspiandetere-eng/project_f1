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
        saveCache(context, key, data, CACHE_DURATION);
    }
    
    public static void saveCache(Context context, String key, String data, long ttlMs) {
        SharedPreferences prefs = getCachePrefs(context);
        prefs.edit()
            .putString(key, data)
            .putLong(key + "_time", System.currentTimeMillis())
            .putLong(key + "_ttl", ttlMs)
            .apply();
    }
    
    public static String getCache(Context context, String key) {
        SharedPreferences prefs = getCachePrefs(context);
        long cacheTime = prefs.getLong(key + "_time", 0);
        long ttl = prefs.getLong(key + "_ttl", CACHE_DURATION);
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - cacheTime > ttl) {
            clearCache(context, key);
            return null;
        }
        
        return prefs.getString(key, null);
    }
    
    public static void clearCache(Context context, String key) {
        SharedPreferences prefs = getCachePrefs(context);
        prefs.edit().remove(key).remove(key + "_time").remove(key + "_ttl").apply();
    }
    
    public static void clearAllCache(Context context) {
        SharedPreferences prefs = getCachePrefs(context);
        prefs.edit().clear().apply();
        
        File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        if (cacheDir.exists()) {
            deleteDir(cacheDir);
        }
    }
    
    public static long getCacheSize(Context context) {
        SharedPreferences prefs = getCachePrefs(context);
        long size = 0;
        for (String key : prefs.getAll().keySet()) {
            if (!key.endsWith("_time") && !key.endsWith("_ttl")) {
                String value = prefs.getString(key, "");
                size += value.length();
            }
        }
        return size;
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
