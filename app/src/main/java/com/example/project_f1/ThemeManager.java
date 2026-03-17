package com.example.project_f1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class ThemeManager {

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("F1Prefs", Context.MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", true);
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void applyStatusBar(Activity activity) {
        // Opt out of forced edge-to-edge (SDK 35+)
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), true);
        activity.getWindow().setStatusBarColor(Color.parseColor("#0D0D0D"));
        activity.getWindow().setNavigationBarColor(Color.parseColor("#0D0D0D"));
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }
}
