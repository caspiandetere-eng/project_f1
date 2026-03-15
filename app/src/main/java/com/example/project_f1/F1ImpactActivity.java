package com.example.project_f1;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class F1ImpactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_f1_impact);
        animateCards();
    }

    private void animateCards() {
        View root = findViewById(android.R.id.content);
        if (root == null) return;
        
        int[] cardIds = {
            R.id.cardAutomotive,
            R.id.cardMedical,
            R.id.cardSustainability,
            R.id.cardEconomic,
            R.id.cardMaterials
        };
        
        for (int i = 0; i < cardIds.length; i++) {
            View card = root.findViewById(cardIds[i]);
            if (card != null) {
                card.setAlpha(0f);
                card.setScaleX(0.9f);
                card.setScaleY(0.9f);
                card.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setStartDelay(100 + (i * 100));
            }
        }
    }
}
