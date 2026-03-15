package com.example.project_f1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        
        setContentView(R.layout.activity_settings);
        
        setTitle("Settings");
        
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnBack = findViewById(R.id.btnBack);
        SwitchCompat switchTheme = findViewById(R.id.switchTheme);
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        
        boolean isDarkTheme = prefs.getBoolean("dark_theme", true);
        switchTheme.setChecked(isDarkTheme);
        
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_theme", isChecked).apply();
            recreate();
        });
        
        btnLogout.setOnClickListener(v -> logout());
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
