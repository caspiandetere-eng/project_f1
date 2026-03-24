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

public class QuizResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        float dp = getResources().getDisplayMetrics().density;

        int score = getIntent().getIntExtra("score", 0);
        String level = getIntent().getStringExtra("level");
        if (level == null) level = "rookie";
        String message = QuizData.messageForLevel(level);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(px(24, dp), px(64, dp), px(24, dp), px(48, dp));
        scroll.addView(root);

        // Score circle
        TextView tvScore = new TextView(this);
        tvScore.setText(score + "\n/ 15");
        tvScore.setTextColor(0xFFFFFFFF);
        tvScore.setTextSize(32);
        tvScore.setGravity(Gravity.CENTER);
        tvScore.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvScore.setLineSpacing(0, 1.1f);
        int circleSize = px(120, dp);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleSize, circleSize);
        circleParams.gravity = Gravity.CENTER_HORIZONTAL;
        circleParams.setMargins(0, 0, 0, px(32, dp));
        tvScore.setLayoutParams(circleParams);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(ThemeManager.blendColors(0xFF0D0D0D, theme.accent, 0.15f));
        circleBg.setStroke(px(3, dp), theme.accent);
        tvScore.setBackground(circleBg);
        root.addView(tvScore);

        // Level badge
        TextView tvLevel = new TextView(this);
        tvLevel.setText(level.toUpperCase());
        tvLevel.setTextColor(0xFF000000);
        tvLevel.setTextSize(14);
        tvLevel.setGravity(Gravity.CENTER);
        tvLevel.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvLevel.setPadding(px(24, dp), px(8, dp), px(24, dp), px(8, dp));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(theme.accent);
        badgeBg.setCornerRadius(px(20, dp));
        tvLevel.setBackground(badgeBg);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgeParams.setMargins(0, 0, 0, px(16, dp));
        tvLevel.setLayoutParams(badgeParams);
        root.addView(tvLevel);

        // Message
        TextView tvMessage = new TextView(this);
        tvMessage.setText(message);
        tvMessage.setTextColor(0xFFAAAAAA);
        tvMessage.setTextSize(16);
        tvMessage.setGravity(Gravity.CENTER);
        tvMessage.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        msgParams.setMargins(0, 0, 0, px(48, dp));
        tvMessage.setLayoutParams(msgParams);
        root.addView(tvMessage);

        // Info card
        LinearLayout infoCard = new LinearLayout(this);
        infoCard.setOrientation(LinearLayout.VERTICAL);
        infoCard.setPadding(px(20, dp), px(20, dp), px(20, dp), px(20, dp));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(ThemeManager.blendColors(0xFF0D0D0D, theme.accent, 0.07f));
        cardBg.setCornerRadius(px(16, dp));
        cardBg.setStroke(px(1, dp), ThemeManager.blendColors(0xFF222222, theme.accent, 0.28f));
        infoCard.setBackground(cardBg);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, px(32, dp));
        infoCard.setLayoutParams(cardParams);
        root.addView(infoCard);

        TextView tvInfoTitle = new TextView(this);
        tvInfoTitle.setText("CONTENT MODE");
        tvInfoTitle.setTextColor(theme.accent);
        tvInfoTitle.setTextSize(11);
        tvInfoTitle.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvInfoTitle.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams infoTitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoTitleParams.setMargins(0, 0, 0, px(8, dp));
        tvInfoTitle.setLayoutParams(infoTitleParams);
        infoCard.addView(tvInfoTitle);

        TextView tvInfoBody = new TextView(this);
        tvInfoBody.setText(contentModeDescription(level));
        tvInfoBody.setTextColor(0xFFCCCCCC);
        tvInfoBody.setTextSize(14);
        tvInfoBody.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        tvInfoBody.setLineSpacing(0, 1.4f);
        infoCard.addView(tvInfoBody);

        // Continue button
        TextView btnContinue = new TextView(this);
        btnContinue.setText("CONTINUE TO APP");
        btnContinue.setTextColor(0xFF000000);
        btnContinue.setTextSize(15);
        btnContinue.setGravity(Gravity.CENTER);
        btnContinue.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        btnContinue.setPadding(0, px(18, dp), 0, px(18, dp));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(theme.accent);
        btnBg.setCornerRadius(px(12, dp));
        btnContinue.setBackground(btnBg);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 0, 0, px(12, dp));
        btnContinue.setLayoutParams(btnParams);
        btnContinue.setOnClickListener(v -> goToMain());
        root.addView(btnContinue);

        // Retake link
        TextView btnRetake = new TextView(this);
        btnRetake.setText("Retake quiz");
        btnRetake.setTextColor(0xFF666666);
        btnRetake.setTextSize(14);
        btnRetake.setGravity(Gravity.CENTER);
        btnRetake.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        btnRetake.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
            finish();
        });
        root.addView(btnRetake);

        setContentView(scroll);

        // Entrance animation
        root.setAlpha(0f);
        root.animate().alpha(1f).setDuration(400).start();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        finish();
    }

    private String contentModeDescription(String level) {
        switch (level) {
            case "casual":     return "You'll see mostly general content with some technical details.";
            case "enthusiast": return "You'll see a mix of general and detailed technical content.";
            case "insider":    return "You'll see full detailed content across all sections.";
            default:           return "You'll see general content to help you learn F1.";
        }
    }

    private int px(int dp, float density) { return (int) (dp * density); }
}
