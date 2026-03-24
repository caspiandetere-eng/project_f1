package com.example.project_f1;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.card.MaterialCardView;

public class F1ImpactActivity extends BaseActivity {

    private View currentlyExpandedContent = null;
    private View currentlyExpandedArrow = null;
    private MaterialCardView currentlyExpandedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_f1_impact);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);

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

        setupExpandableCards();
        animateCardsEntrance();
    }

    private void setupExpandableCards() {
        setupCard(R.id.cardAutomotive,    R.id.contentAutomotive,    R.id.arrowAutomotive);
        setupCard(R.id.cardMedical,       R.id.contentMedical,       R.id.arrowMedical);
        setupCard(R.id.cardSustainability,R.id.contentSustainability,R.id.arrowSustainability);
        setupCard(R.id.cardEconomic,      R.id.contentEconomic,      R.id.arrowEconomic);
        setupCard(R.id.cardMaterials,     R.id.contentMaterials,     R.id.arrowMaterials);
    }

    private void setupCard(int cardId, int contentId, int arrowId) {
        MaterialCardView card = findViewById(cardId);
        LinearLayout content = findViewById(contentId);
        View arrow = findViewById(arrowId);

        card.setOnClickListener(v -> {
            boolean isExpanded = content.getVisibility() == View.VISIBLE;

            if (currentlyExpandedContent != null && currentlyExpandedContent != content) {
                CardAnimationHelper.collapse(currentlyExpandedContent);
                CardAnimationHelper.rotateArrow(currentlyExpandedArrow, false);
                CardAnimationHelper.animateElevation(currentlyExpandedCard, false);
                currentlyExpandedContent = null;
                currentlyExpandedArrow = null;
                currentlyExpandedCard = null;
            }

            if (isExpanded) {
                CardAnimationHelper.collapse(content);
                CardAnimationHelper.rotateArrow(arrow, false);
                CardAnimationHelper.animateElevation(card, false);
                currentlyExpandedContent = null;
                currentlyExpandedArrow = null;
                currentlyExpandedCard = null;
            } else {
                CardAnimationHelper.expand(content);
                CardAnimationHelper.rotateArrow(arrow, true);
                CardAnimationHelper.animateElevation(card, true);
                currentlyExpandedContent = content;
                currentlyExpandedArrow = arrow;
                currentlyExpandedCard = card;
            }
        });
    }

    private void animateCardsEntrance() {
        int[] cardIds = {
            R.id.cardAutomotive, R.id.cardMedical,
            R.id.cardSustainability, R.id.cardEconomic, R.id.cardMaterials
        };
        for (int i = 0; i < cardIds.length; i++) {
            View card = findViewById(cardIds[i]);
            if (card == null) continue;
            card.setAlpha(0f);
            card.setTranslationY(30f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(350)
                    .setStartDelay(80 + (i * 80L))
                    .setInterpolator(new androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                    .start();
        }
    }
}
