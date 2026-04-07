package com.example.project_f1;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class QuizIntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        float dp = getResources().getDisplayMetrics().density;

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(px(24, dp), px(48, dp), px(24, dp), px(48, dp));
        scroll.addView(root);

        // Back button row
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        topParams.setMargins(0, 0, 0, px(32, dp));
        topRow.setLayoutParams(topParams);

        TextView btnBack = new TextView(this);
        btnBack.setText("← Back");
        btnBack.setTextColor(theme.accent);
        btnBack.setTextSize(15);
        btnBack.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        btnBack.setOnClickListener(v -> finish());
        topRow.addView(btnBack);
        root.addView(topRow);

        // Accent bar
        View bar = new View(this);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(px(48, dp), px(4, dp));
        barParams.gravity = Gravity.CENTER_HORIZONTAL;
        barParams.setMargins(0, 0, 0, px(24, dp));
        bar.setLayoutParams(barParams);
        GradientDrawable barBg = new GradientDrawable();
        barBg.setColor(theme.accent);
        barBg.setCornerRadius(px(2, dp));
        bar.setBackground(barBg);
        root.addView(bar);

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText("F1 KNOWLEDGE QUIZ");
        tvTitle.setTextColor(getResources().getColor(R.color.quiz_text_primary, null));
        tvTitle.setTextSize(28);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(0, 0, 0, px(12, dp));
        tvTitle.setLayoutParams(titleParams);
        root.addView(tvTitle);

        // Subtitle
        TextView tvSub = new TextView(this);
        String currentLabel = KnowledgeLevelManager.displayLabel(this);
        int currentXp = KnowledgeLevelManager.getXp(this);
        tvSub.setText("30 questions · " + currentLabel + "  " + currentXp + "/100 XP");
        tvSub.setTextColor(getResources().getColor(R.color.quiz_text_secondary, null));
        tvSub.setTextSize(15);
        tvSub.setGravity(Gravity.CENTER);
        tvSub.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.setMargins(0, 0, 0, px(40, dp));
        tvSub.setLayoutParams(subParams);
        root.addView(tvSub);

        // Info card
        LinearLayout card = makeCard(theme, dp);
        root.addView(card);

        String[] infoLines = {
            "🏎  Answer 30 F1 questions",
            "⬆  Earn XP and level up (20 levels)",
            "⬇  Score < 40% causes demotion",
            "📊  Content adapts to your level"
        };
        for (String line : infoLines) {
            TextView tv = new TextView(this);
            tv.setText(line);
            tv.setTextColor(getResources().getColor(R.color.quiz_option_text, null));
            tv.setTextSize(15);
            tv.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, px(14, dp));
            tv.setLayoutParams(lp);
            card.addView(tv);
        }

        // Level descriptions card
        LinearLayout levelsCard = makeCard(theme, dp);
        LinearLayout.LayoutParams levelsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        levelsParams.setMargins(0, px(16, dp), 0, px(40, dp));
        levelsCard.setLayoutParams(levelsParams);
        root.addView(levelsCard);

        addLevelRow(levelsCard, "L1–L5",   "Rookie",      theme.accent, dp);
        addLevelRow(levelsCard, "L6–L10",  "Casual",      theme.accent, dp);
        addLevelRow(levelsCard, "L11–L15", "Enthusiast",  theme.accent, dp);
        addLevelRow(levelsCard, "L16–L20", "Insider",     theme.accent, dp);

        // Start button
        TextView btnStart = new TextView(this);
        btnStart.setText("START QUIZ");
        btnStart.setTextColor(getResources().getColor(R.color.quiz_btn_text, null));
        btnStart.setTextSize(16);
        btnStart.setGravity(Gravity.CENTER);
        btnStart.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        btnStart.setPadding(0, px(18, dp), 0, px(18, dp));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(theme.accent);
        btnBg.setCornerRadius(px(12, dp));
        btnStart.setBackground(btnBg);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnStart.setLayoutParams(btnParams);
        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        });
        root.addView(btnStart);

        setContentView(scroll);
    }

    private LinearLayout makeCard(ThemeManager.TeamTheme theme, float dp) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(px(20, dp), px(20, dp), px(20, dp), px(6, dp));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ThemeManager.blendColors(getResources().getColor(R.color.quiz_bg, null), theme.accent, 0.07f));
        bg.setCornerRadius(px(16, dp));
        bg.setStroke(px(1, dp), ThemeManager.blendColors(getResources().getColor(R.color.quiz_card_stroke, null), theme.accent, 0.28f));
        card.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        card.setLayoutParams(lp);
        return card;
    }

    private void addLevelRow(LinearLayout parent, String range, String label, int accent, float dp) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(0, 0, 0, px(12, dp));
        row.setLayoutParams(rp);

        TextView tvRange = new TextView(this);
        tvRange.setText(range);
        tvRange.setTextColor(accent);
        tvRange.setTextSize(13);
        tvRange.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvRange.setWidth(px(52, dp));
        row.addView(tvRange);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label.toUpperCase());
        tvLabel.setTextColor(getResources().getColor(R.color.quiz_option_text, null));
        tvLabel.setTextSize(13);
        tvLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed));
        row.addView(tvLabel);

        parent.addView(row);
    }

    private int px(int dp, float density) { return (int) (dp * density); }
}
