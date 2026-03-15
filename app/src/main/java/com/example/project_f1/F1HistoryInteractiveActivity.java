package com.example.project_f1;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class F1HistoryInteractiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_f1_history_interactive);

        setupExpandableCards();
    }

    private void setupExpandableCards() {
        setupCard(R.id.cardEra1950, R.id.contentEra1950, R.id.tvArrowEra1950);
        setupCard(R.id.cardEra1970, R.id.contentEra1970, R.id.tvArrowEra1970);
        setupCard(R.id.cardEra2000, R.id.contentEra2000, R.id.tvArrowEra2000);
        setupCard(R.id.cardLegendSenna, R.id.contentLegendSenna, R.id.tvArrowSenna);
        setupCard(R.id.cardLegendSchumacher, R.id.contentLegendSchumacher, R.id.tvArrowSchumacher);
        setupCard(R.id.cardLegendHamilton, R.id.contentLegendHamilton, R.id.tvArrowHamilton);
    }

    private void setupCard(int cardId, int contentId, int arrowId) {
        MaterialCardView card = findViewById(cardId);
        LinearLayout content = findViewById(contentId);
        TextView arrow = findViewById(arrowId);

        card.setOnClickListener(v -> {
            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                arrow.setText("▲");
            } else {
                content.setVisibility(View.GONE);
                arrow.setText("▼");
            }
        });
    }
}
