package com.example.project_f1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class KnowledgeLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        
        setContentView(R.layout.activity_knowledge_level);

        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        String email = getIntent().getStringExtra("email");
        int userId = getIntent().getIntExtra("user_id", -1);
        
        findViewById(R.id.cardRookie).setOnClickListener(v -> selectLevel(prefs, userId, "rookie"));
        findViewById(R.id.cardCasual).setOnClickListener(v -> selectLevel(prefs, userId, "casual"));
        findViewById(R.id.cardEnthusiast).setOnClickListener(v -> selectLevel(prefs, userId, "enthusiast"));
        findViewById(R.id.cardInsider).setOnClickListener(v -> selectLevel(prefs, userId, "insider"));
        findViewById(R.id.btnMenu).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_knowledge_level, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectLevel(SharedPreferences prefs, int userId, String level) {
        if (userId != -1) UserRepository.updateLevel(this, userId, level);
        prefs.edit()
            .putString("knowledge_level", level)
            .putBoolean("is_logged_in", true)
            .apply();

        boolean needsOnboarding = (level.equals("rookie") || level.equals("casual"))
            && !prefs.getBoolean("onboarding_done", false);

        if (needsOnboarding) {
            startActivity(new Intent(this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
