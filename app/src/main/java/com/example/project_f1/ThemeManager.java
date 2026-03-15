package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("F1Prefs", Context.MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", true);
        
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
