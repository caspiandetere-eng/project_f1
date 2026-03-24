package com.example.project_f1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class QuizActivity extends BaseActivity {

    private static final int TOTAL = 15;

    private int currentIndex = 0;
    private int score = 0;
    private boolean answered = false;

    private ThemeManager.TeamTheme theme;
    private float dp;

    // Views rebuilt per question
    private TextView tvProgress;
    private TextView tvQuestion;
    private LinearLayout optionsContainer;
    private TextView tvExplanation;
    private TextView btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = ThemeManager.applyFullTheme(this);
        dp = getResources().getDisplayMetrics().density;
        buildLayout();
        showQuestion();
    }

    private void buildLayout() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(px(20), px(48), px(20), px(32));

        // Top row: back + progress
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        topParams.setMargins(0, 0, 0, px(28));
        topRow.setLayoutParams(topParams);

        TextView btnBack = new TextView(this);
        btnBack.setText("✕");
        btnBack.setTextColor(0xFF888888);
        btnBack.setTextSize(18);
        btnBack.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        btnBack.setOnClickListener(v -> finish());
        topRow.addView(btnBack);

        tvProgress = new TextView(this);
        tvProgress.setTextColor(0xFF888888);
        tvProgress.setTextSize(14);
        tvProgress.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed));
        tvProgress.setGravity(Gravity.END);
        LinearLayout.LayoutParams progParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        tvProgress.setLayoutParams(progParams);
        topRow.addView(tvProgress);
        root.addView(topRow);

        // Progress bar track
        LinearLayout progressTrack = new LinearLayout(this);
        progressTrack.setOrientation(LinearLayout.HORIZONTAL);
        GradientDrawable trackBg = new GradientDrawable();
        trackBg.setColor(0xFF1A1A1A);
        trackBg.setCornerRadius(px(3));
        progressTrack.setBackground(trackBg);
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, px(4));
        trackParams.setMargins(0, 0, 0, px(32));
        progressTrack.setLayoutParams(trackParams);

        View progressFill = new View(this);
        progressFill.setTag("progressFill");
        GradientDrawable fillBg = new GradientDrawable();
        fillBg.setColor(theme.accent);
        fillBg.setCornerRadius(px(3));
        progressFill.setBackground(fillBg);
        progressTrack.addView(progressFill);
        root.addView(progressTrack);

        // Question card
        LinearLayout questionCard = new LinearLayout(this);
        questionCard.setOrientation(LinearLayout.VERTICAL);
        questionCard.setPadding(px(20), px(24), px(20), px(24));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(ThemeManager.blendColors(0xFF0D0D0D, theme.accent, 0.07f));
        cardBg.setCornerRadius(px(16));
        cardBg.setStroke(px(1), ThemeManager.blendColors(0xFF222222, theme.accent, 0.28f));
        questionCard.setBackground(cardBg);
        LinearLayout.LayoutParams qCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qCardParams.setMargins(0, 0, 0, px(20));
        questionCard.setLayoutParams(qCardParams);

        tvQuestion = new TextView(this);
        tvQuestion.setTextColor(0xFFFFFFFF);
        tvQuestion.setTextSize(18);
        tvQuestion.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvQuestion.setLineSpacing(0, 1.3f);
        questionCard.addView(tvQuestion);
        root.addView(questionCard);

        // Options container
        optionsContainer = new LinearLayout(this);
        optionsContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams optParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        optParams.setMargins(0, 0, 0, px(20));
        optionsContainer.setLayoutParams(optParams);
        root.addView(optionsContainer);

        // Explanation
        tvExplanation = new TextView(this);
        tvExplanation.setTextColor(0xFFAAAAAA);
        tvExplanation.setTextSize(14);
        tvExplanation.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        tvExplanation.setLineSpacing(0, 1.4f);
        tvExplanation.setPadding(px(16), px(14), px(16), px(14));
        tvExplanation.setVisibility(View.GONE);
        GradientDrawable expBg = new GradientDrawable();
        expBg.setColor(0xFF111111);
        expBg.setCornerRadius(px(12));
        expBg.setStroke(px(1), 0xFF2A2A2A);
        tvExplanation.setBackground(expBg);
        LinearLayout.LayoutParams expParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        expParams.setMargins(0, 0, 0, px(24));
        tvExplanation.setLayoutParams(expParams);
        root.addView(tvExplanation);

        // Next button
        btnNext = new TextView(this);
        btnNext.setTextColor(0xFF000000);
        btnNext.setTextSize(15);
        btnNext.setGravity(Gravity.CENTER);
        btnNext.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        btnNext.setPadding(0, px(16), 0, px(16));
        btnNext.setVisibility(View.GONE);
        GradientDrawable nextBg = new GradientDrawable();
        nextBg.setColor(theme.accent);
        nextBg.setCornerRadius(px(12));
        btnNext.setBackground(nextBg);
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnNext.setLayoutParams(nextParams);
        btnNext.setOnClickListener(v -> advance());
        root.addView(btnNext);

        scroll.addView(root);
        setContentView(scroll);
    }

    private void showQuestion() {
        answered = false;
        QuizQuestion q = QuizData.QUESTIONS.get(currentIndex);

        tvProgress.setText((currentIndex + 1) + " / " + TOTAL);
        tvQuestion.setText(q.question);
        tvExplanation.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        // Update progress fill width
        updateProgressBar();

        // Build option buttons
        optionsContainer.removeAllViews();
        for (int i = 0; i < q.options.length; i++) {
            final int idx = i;
            TextView opt = new TextView(this);
            opt.setText(q.options[i]);
            opt.setTextColor(0xFFDDDDDD);
            opt.setTextSize(15);
            opt.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
            opt.setPadding(px(16), px(16), px(16), px(16));
            opt.setLineSpacing(0, 1.2f);
            opt.setTag(i);

            GradientDrawable optBg = new GradientDrawable();
            optBg.setColor(ThemeManager.blendColors(0xFF0D0D0D, theme.accent, 0.05f));
            optBg.setCornerRadius(px(12));
            optBg.setStroke(px(1), 0xFF2A2A2A);
            opt.setBackground(optBg);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, px(10));
            opt.setLayoutParams(lp);

            opt.setOnClickListener(v -> {
                if (!answered) selectAnswer(idx);
            });

            optionsContainer.addView(opt);

            // Entrance animation
            opt.setAlpha(0f);
            opt.setTranslationY(px(16));
            opt.animate().alpha(1f).translationY(0f).setDuration(250).setStartDelay(i * 50L).start();
        }
    }

    private void selectAnswer(int selectedIdx) {
        answered = true;
        QuizQuestion q = QuizData.QUESTIONS.get(currentIndex);
        boolean correct = selectedIdx == q.correctIndex;
        if (correct) score++;

        // Color all options
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            TextView opt = (TextView) optionsContainer.getChildAt(i);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(px(12));
            if (i == q.correctIndex) {
                bg.setColor(0xFF0A2A0A);
                bg.setStroke(px(2), 0xFF2ECC71);
                opt.setTextColor(0xFF2ECC71);
            } else if (i == selectedIdx) {
                bg.setColor(0xFF2A0A0A);
                bg.setStroke(px(2), 0xFFE74C3C);
                opt.setTextColor(0xFFE74C3C);
            } else {
                bg.setColor(0xFF0D0D0D);
                bg.setStroke(px(1), 0xFF1A1A1A);
                opt.setTextColor(0xFF555555);
            }
            opt.setBackground(bg);
            opt.setClickable(false);
        }

        // Show explanation
        tvExplanation.setText(q.explanation);
        tvExplanation.setVisibility(View.VISIBLE);
        tvExplanation.setAlpha(0f);
        tvExplanation.animate().alpha(1f).setDuration(200).start();

        // Next button label
        boolean isLast = currentIndex == TOTAL - 1;
        btnNext.setText(isLast ? "SEE RESULTS" : "NEXT");
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setAlpha(0f);
        btnNext.animate().alpha(1f).setDuration(200).start();
    }

    private void advance() {
        if (currentIndex < TOTAL - 1) {
            currentIndex++;
            showQuestion();
        } else {
            saveAndShowResult();
        }
    }

    private void saveAndShowResult() {
        String level = QuizData.levelForScore(score);
        getSharedPreferences("F1Prefs", MODE_PRIVATE)
                .edit()
                .putString("user_level", level)
                .putString("knowledge_level", level)
                .apply();
        StateManager.init(this);

        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("level", level);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        finish();
    }

    private void updateProgressBar() {
        View root = getWindow().getDecorView();
        View fill = root.findViewWithTag("progressFill");
        if (fill != null && fill.getParent() instanceof LinearLayout) {
            LinearLayout track = (LinearLayout) fill.getParent();
            track.post(() -> {
                int trackWidth = track.getWidth();
                float fraction = (float) (currentIndex + 1) / TOTAL;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        (int) (trackWidth * fraction), LinearLayout.LayoutParams.MATCH_PARENT);
                fill.setLayoutParams(lp);
            });
        }
    }

    private int px(int dp) { return (int) (dp * this.dp); }
}
