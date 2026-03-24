package com.example.project_f1;

import android.view.View;
import android.widget.LinearLayout;
import android.os.Bundle;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class F1HistoryInteractiveActivity extends BaseActivity {

    // Tracks the currently expanded content view (null = all collapsed)
    private View currentlyExpandedContent = null;
    private View currentlyExpandedArrow = null;
    private MaterialCardView currentlyExpandedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_f1_history_interactive);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        applyHistoryTheme(theme);
        setupExpandableCards();
    }

    private void applyHistoryTheme(ThemeManager.TeamTheme theme) {
        View root = ((android.view.ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{theme.bgTop, theme.bgBottom});
        root.setBackground(bg);
        View stripe = root.findViewById(R.id.topStripe);
        if (stripe != null) stripe.setBackgroundColor(theme.accent);
        View headerBar = root.findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        ThemeManager.tintViewTree((android.view.ViewGroup) root, theme);
    }

    private void setupExpandableCards() {
        // Era cards
        setupCard(R.id.cardEra1950, R.id.contentEra1950, R.id.tvArrowEra1950);
        setupCard(R.id.cardEra1970, R.id.contentEra1970, R.id.tvArrowEra1970);
        setupCard(R.id.cardEra2000, R.id.contentEra2000, R.id.tvArrowEra2000);
        // Legend cards
        setupCard(R.id.cardLegendLauda, R.id.contentLegendLauda, R.id.tvArrowLauda);
        setupCard(R.id.cardLegendSenna, R.id.contentLegendSenna, R.id.tvArrowSenna);
        setupCard(R.id.cardLegendSchumacher, R.id.contentLegendSchumacher, R.id.tvArrowSchumacher);
        setupCard(R.id.cardLegendHamilton, R.id.contentLegendHamilton, R.id.tvArrowHamilton);
        setupCard(R.id.cardLegendMax, R.id.contentLegendMax, R.id.tvArrowMax);
    }

    private void setupCard(int cardId, int contentId, int arrowId) {
        MaterialCardView card = findViewById(cardId);
        LinearLayout content = findViewById(contentId);
        View arrow = findViewById(arrowId);

        card.setOnClickListener(v -> {
            boolean isExpanded = content.getVisibility() == View.VISIBLE;

            // Collapse the currently open card first
            if (currentlyExpandedContent != null && currentlyExpandedContent != content) {
                CardAnimationHelper.collapse(currentlyExpandedContent);
                CardAnimationHelper.rotateArrow(currentlyExpandedArrow, false);
                CardAnimationHelper.animateElevation(currentlyExpandedCard, false);
                currentlyExpandedContent = null;
                currentlyExpandedArrow = null;
                currentlyExpandedCard = null;
            }

            if (isExpanded) {
                // Collapse this card
                CardAnimationHelper.collapse(content);
                CardAnimationHelper.rotateArrow(arrow, false);
                CardAnimationHelper.animateElevation(card, false);
                currentlyExpandedContent = null;
                currentlyExpandedArrow = null;
                currentlyExpandedCard = null;
            } else {
                // Expand this card
                CardAnimationHelper.expand(content);
                CardAnimationHelper.rotateArrow(arrow, true);
                CardAnimationHelper.animateElevation(card, true);
                currentlyExpandedContent = content;
                currentlyExpandedArrow = arrow;
                currentlyExpandedCard = card;
            }
        });
    }
}
