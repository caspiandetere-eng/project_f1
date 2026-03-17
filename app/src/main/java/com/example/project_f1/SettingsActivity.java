package com.example.project_f1;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileObserver;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvCacheSize;
    private ProgressBar progressCache;
    private List<FileObserver> fileObservers = new ArrayList<>();
    private static final long MAX_CACHE_SIZE = 500 * 1024 * 1024; // 500 MB max for progress calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        
        setContentView(R.layout.activity_settings);
        ThemeManager.applyStatusBar(this);
        
        setTitle("Settings");
        
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnClearCache = findViewById(R.id.btnClearCache);
        SwitchCompat switchTheme = findViewById(R.id.switchTheme);
        tvCacheSize = findViewById(R.id.tvCacheSize);
        progressCache = findViewById(R.id.progressCache);
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        
        boolean isDarkTheme = prefs.getBoolean("dark_theme", true);
        switchTheme.setChecked(isDarkTheme);
        
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_theme", isChecked).apply();
            recreate();
        });
        
        btnLogout.setOnClickListener(v -> logout());
        btnClearCache.setOnClickListener(v -> clearCache());
        btnBack.setOnClickListener(v -> finish());
        
        updateCacheSize();
        startCacheMonitoring();
    }
    
    private void startCacheMonitoring() {
        File cacheDir = getCacheDir();
        if (cacheDir.exists()) {
            FileObserver observer = new FileObserver(cacheDir.getAbsolutePath()) {
                @Override
                public void onEvent(int event, String path) {
                    runOnUiThread(() -> updateCacheSize());
                }
            };
            observer.startWatching();
            fileObservers.add(observer);
        }
    }
    
    private void updateCacheSize() {
        long cacheSize = getCacheSize();
        String cacheSizeStr = formatBytes(cacheSize);
        tvCacheSize.setText(cacheSizeStr);
        
        // Update progress bar with animation
        int progress = (int) ((cacheSize * 100) / MAX_CACHE_SIZE);
        progress = Math.min(progress, 100);
        animateProgress(progress);
    }
    
    private void animateProgress(int targetProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(progressCache, "progress", progressCache.getProgress(), targetProgress);
        animator.setDuration(500);
        animator.start();
    }
    
    private long getCacheSize() {
        long size = 0;
        File cacheDir = getCacheDir();
        if (cacheDir.exists()) {
            size += getDirSize(cacheDir);
        }
        return size;
    }
    
    private long getDirSize(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        size += getDirSize(file);
                    } else {
                        size += file.length();
                    }
                }
            }
        } else {
            size = dir.length();
        }
        return size;
    }
    
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
    
    private void clearCache() {
        CacheManager.clearAllCache(this);
        updateCacheSize();
        android.widget.Toast.makeText(this, "Cache cleared", android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (FileObserver observer : fileObservers) {
            observer.stopWatching();
        }
        fileObservers.clear();
    }
}
