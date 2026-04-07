package com.example.project_f1;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Button;
import android.widget.ProgressBar;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity {

    private TextView tvCacheSize;
    private ProgressBar progressCache;
    private List<FileObserver> fileObservers = new ArrayList<>();
    private static final long MAX_CACHE_SIZE = 500 * 1024 * 1024; // 500 MB max for progress calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_settings);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        applySettingsAccents(theme);
        setTitle("Settings");
        
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnClearCache = findViewById(R.id.btnClearCache);
        Button btnChangeFavorites = findViewById(R.id.btnChangeFavorites);
        Button btnTakeQuiz = findViewById(R.id.btnTakeQuiz);
        TextView tvCurrentLevel = findViewById(R.id.tvCurrentLevel);
        SwitchCompat switchTeamTheme = findViewById(R.id.switchTeamTheme);
        tvCacheSize = findViewById(R.id.tvCacheSize);
        progressCache = findViewById(R.id.progressCache);
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        
        if (switchTeamTheme != null)
            switchTeamTheme.setChecked(ThemeManager.isTeamTheme(this));
        
        if (switchTeamTheme != null) {
            switchTeamTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String mode = isChecked ? ThemeManager.THEME_TEAM : ThemeManager.THEME_DEFAULT;
                StateManager.get().setTheme(this, mode);
            });
        }
        
        btnLogout.setOnClickListener(v -> logout());
        btnClearCache.setOnClickListener(v -> clearCache());
        btnChangeFavorites.setOnClickListener(v -> changeFavorites());
        btnBack.setOnClickListener(v -> finish());
        if (btnTakeQuiz != null) btnTakeQuiz.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizIntroActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });
        if (tvCurrentLevel != null) {
            tvCurrentLevel.setText("Current: " + KnowledgeLevelManager.displayLabel(this));
        }

        // Notification toggle
        androidx.appcompat.widget.SwitchCompat switchNotifications = findViewById(R.id.switchNotifications);
        androidx.appcompat.widget.SwitchCompat switch3Days  = findViewById(R.id.switch3Days);
        androidx.appcompat.widget.SwitchCompat switch1Day   = findViewById(R.id.switch1Day);
        androidx.appcompat.widget.SwitchCompat switch1Hour  = findViewById(R.id.switch1Hour);
        android.view.View rowNotif3Days = findViewById(R.id.rowNotif3Days);
        android.view.View rowNotif1Day  = findViewById(R.id.rowNotif1Day);
        android.view.View rowNotif1Hour = findViewById(R.id.rowNotif1Hour);
        android.view.View notifDivider  = findViewById(R.id.notifDivider);

        if (switchNotifications != null) {
            boolean masterOn = prefs.getBoolean("notifications_enabled", true);
            switchNotifications.setChecked(masterOn);

            // Restore individual toggle states
            if (switch3Days != null)  switch3Days.setChecked(prefs.getBoolean("notif_3days", true));
            if (switch1Day  != null)  switch1Day.setChecked(prefs.getBoolean("notif_1day",  true));
            if (switch1Hour != null)  switch1Hour.setChecked(prefs.getBoolean("notif_1hour", true));

            // Show/hide sub-rows based on master state
            setNotifSubRowsVisible(masterOn, rowNotif3Days, rowNotif1Day, rowNotif1Hour, notifDivider);

            switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
                setNotifSubRowsVisible(isChecked, rowNotif3Days, rowNotif1Day, rowNotif1Hour, notifDivider);
                rescheduleNotifications(prefs, isChecked);
            });

            // Individual timing toggles — only meaningful when master is ON
            if (switch3Days != null) switch3Days.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean("notif_3days", isChecked).apply();
                rescheduleNotifications(prefs, true);
            });
            if (switch1Day != null) switch1Day.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean("notif_1day", isChecked).apply();
                rescheduleNotifications(prefs, true);
            });
            if (switch1Hour != null) switch1Hour.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean("notif_1hour", isChecked).apply();
                rescheduleNotifications(prefs, true);
            });
        }
        
        updateCacheSize();
        startCacheMonitoring();
    }
    
    private void setNotifSubRowsVisible(boolean visible,
                                         android.view.View row3Days,
                                         android.view.View row1Day,
                                         android.view.View row1Hour,
                                         android.view.View divider) {
        int vis = visible ? android.view.View.VISIBLE : android.view.View.GONE;
        if (row3Days != null) row3Days.setVisibility(vis);
        if (row1Day  != null) row1Day.setVisibility(vis);
        if (row1Hour != null) row1Hour.setVisibility(vis);
        if (divider  != null) divider.setVisibility(vis);
    }

    private void rescheduleNotifications(android.content.SharedPreferences prefs, boolean masterOn) {
        String cached = CacheManager.getCache(this, "schedule_2026");
        if (cached == null) return;
        try {
            com.example.project_f1.models.JolpicaScheduleResponse resp =
                    new com.google.gson.Gson().fromJson(
                            cached, com.example.project_f1.models.JolpicaScheduleResponse.class);
            // Always cancel first so stale alarms are cleared
            RaceNotificationScheduler.cancelAll(this, resp);
            if (masterOn) {
                // Reset version key so scheduleAll re-runs with new toggle states
                getSharedPreferences("F1NotifPrefs", MODE_PRIVATE)
                        .edit().remove("scheduled_version").apply();
                RaceNotificationScheduler.scheduleAll(this, resp);
            }
        } catch (Exception ignored) {}
    }

    private void applySettingsAccents(ThemeManager.TeamTheme theme) {
        // Background
        View root = findViewById(android.R.id.content);
        if (root instanceof ViewGroup) {
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
                    android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{theme.bgTop, theme.bgBottom});
            ((ViewGroup) root).getChildAt(0).setBackground(bg);
        }
        // Top stripe
        View topStripe = findViewById(R.id.topStripe);
        if (topStripe != null) topStripe.setBackgroundColor(theme.accent);
        // Header accent bar
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        // All cards
        int[] cardIds = {R.id.cardAccount, R.id.cardFavorites, R.id.cardQuiz, R.id.cardDisplay, R.id.cardNotifications, R.id.cardStorage};
        for (int id : cardIds) {
            com.google.android.material.card.MaterialCardView card = findViewById(id);
            if (card != null) {
                card.setCardBackgroundColor(theme.cardBg);
                card.setStrokeColor(theme.cardStroke);
            }
        }
        // All buttons
        int[] btnIds = {R.id.btnLogout, R.id.btnChangeFavorites, R.id.btnClearCache, R.id.btnBack};
        for (int id : btnIds) {
            com.google.android.material.button.MaterialButton btn = findViewById(id);
            if (btn != null) btn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(theme.buttonBg));
        }
        // Cache size text color
        TextView tvCache = findViewById(R.id.tvCacheSize);
        if (tvCache != null) tvCache.setTextColor(theme.accent);
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }
    
    private void changeFavorites() {
        Intent intent = new Intent(this, FavoriteSelectionActivity.class);
        intent.putExtra("from_onboarding", false);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
