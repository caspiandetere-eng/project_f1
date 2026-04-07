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
        ThemeManager.applyStatusBar(this);

        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        String email = getIntent().getStringExtra("email");
        int userId = getIntent().getIntExtra("user_id", -1);
        
        int[] cardIds = {R.id.cardRookie, R.id.cardCasual, R.id.cardEnthusiast, R.id.cardInsider};
        String[] levels = {"rookie", "casual", "enthusiast", "insider"};
        long delayMs = 0;
        for (int i = 0; i < cardIds.length; i++) {
            android.view.View card = findViewById(cardIds[i]);
            String level = levels[i];
            card.postDelayed(() -> SpringAnimationHelper.popIn(card), delayMs);
            card.setOnClickListener(v -> selectLevel(prefs, userId, level));
            SpringAnimationHelper.attachPressSpring(card, 0.96);
            delayMs += 80;
        }
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
        } else if (!FavoriteRepository.hasBothFavorites(this)) {
            Intent intent = new Intent(this, FavoriteSelectionActivity.class);
            intent.putExtra("from_onboarding", true);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
