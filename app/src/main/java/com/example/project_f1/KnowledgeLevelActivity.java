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
        
        findViewById(R.id.cardRookie).setOnClickListener(v -> selectLevel(prefs, email, "rookie"));
        findViewById(R.id.cardCasual).setOnClickListener(v -> selectLevel(prefs, email, "casual"));
        findViewById(R.id.cardEnthusiast).setOnClickListener(v -> selectLevel(prefs, email, "enthusiast"));
        findViewById(R.id.cardInsider).setOnClickListener(v -> selectLevel(prefs, email, "insider"));
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

    private void selectLevel(SharedPreferences prefs, String email, String level) {
        prefs.edit()
            .putString("knowledge_level", level)
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .apply();
        
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
