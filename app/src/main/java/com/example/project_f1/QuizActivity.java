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

    private static final int TOTAL = 30;

    private int currentIndex = 0;
    private int score = 0;
    private boolean answered = false;
    private final boolean[] answerResults = new boolean[TOTAL];

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
        btnBack.setTextColor(getResources().getColor(R.color.quiz_text_secondary, null));
        btnBack.setTextSize(18);
        btnBack.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        btnBack.setOnClickListener(v -> finish());
        topRow.addView(btnBack);

        tvProgress = new TextView(this);
        tvProgress.setTextColor(getResources().getColor(R.color.quiz_text_secondary, null));
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
        trackBg.setColor(getResources().getColor(R.color.quiz_progress_track, null));
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
        cardBg.setColor(ThemeManager.blendColors(getResources().getColor(R.color.quiz_bg, null), theme.accent, 0.07f));
        cardBg.setCornerRadius(px(16));
        cardBg.setStroke(px(1), ThemeManager.blendColors(getResources().getColor(R.color.quiz_card_stroke, null), theme.accent, 0.28f));
        questionCard.setBackground(cardBg);
        LinearLayout.LayoutParams qCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qCardParams.setMargins(0, 0, 0, px(20));
        questionCard.setLayoutParams(qCardParams);

        tvQuestion = new TextView(this);
        tvQuestion.setTextColor(getResources().getColor(R.color.quiz_text_primary, null));
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
        tvExplanation.setTextColor(getResources().getColor(R.color.quiz_text_muted, null));
        tvExplanation.setTextSize(14);
        tvExplanation.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        tvExplanation.setLineSpacing(0, 1.4f);
        tvExplanation.setPadding(px(16), px(14), px(16), px(14));
        tvExplanation.setVisibility(View.GONE);
        GradientDrawable expBg = new GradientDrawable();
        expBg.setColor(getResources().getColor(R.color.quiz_card_bg, null));
        expBg.setCornerRadius(px(12));
        expBg.setStroke(px(1), getResources().getColor(R.color.quiz_card_stroke, null));
        tvExplanation.setBackground(expBg);
        LinearLayout.LayoutParams expParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        expParams.setMargins(0, 0, 0, px(24));
        tvExplanation.setLayoutParams(expParams);
        root.addView(tvExplanation);

        // Next button
        btnNext = new TextView(this);
        btnNext.setTextColor(getResources().getColor(R.color.quiz_btn_text, null));
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
            opt.setTextColor(getResources().getColor(R.color.quiz_option_text, null));
            opt.setTextSize(15);
            opt.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
            opt.setPadding(px(16), px(16), px(16), px(16));
            opt.setLineSpacing(0, 1.2f);
            opt.setTag(i);

            GradientDrawable optBg = new GradientDrawable();
            optBg.setColor(ThemeManager.blendColors(getResources().getColor(R.color.quiz_option_bg, null), theme.accent, 0.05f));
            optBg.setCornerRadius(px(12));
            optBg.setStroke(px(1), getResources().getColor(R.color.quiz_option_stroke, null));
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
        answerResults[currentIndex] = correct;

        // Color all options
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            TextView opt = (TextView) optionsContainer.getChildAt(i);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(px(12));
            if (i == q.correctIndex) {
                bg.setColor(getResources().getColor(R.color.quiz_correct_bg, null));
                bg.setStroke(px(2), getResources().getColor(R.color.quiz_correct_stroke, null));
                opt.setTextColor(getResources().getColor(R.color.quiz_correct_text, null));
            } else if (i == selectedIdx) {
                bg.setColor(getResources().getColor(R.color.quiz_wrong_bg, null));
                bg.setStroke(px(2), getResources().getColor(R.color.quiz_wrong_stroke, null));
                opt.setTextColor(getResources().getColor(R.color.quiz_wrong_text, null));
            } else {
                bg.setColor(getResources().getColor(R.color.quiz_unselected_bg, null));
                bg.setStroke(px(1), getResources().getColor(R.color.quiz_unselected_stroke, null));
                opt.setTextColor(getResources().getColor(R.color.quiz_unselected_text, null));
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
        KnowledgeLevelManager.QuizResult result =
                KnowledgeLevelManager.applyQuizResult(this, score, TOTAL);
        StateManager.init(this);

        // Build boolean array of correct/wrong per question
        boolean[] correct = new boolean[TOTAL];
        for (int i = 0; i < TOTAL; i++) correct[i] = answerResults[i];

        Intent resultIntent = new Intent(this, QuizResultActivity.class);
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("total", TOTAL);
        resultIntent.putExtra("xp_earned", result.xpEarned);
        resultIntent.putExtra("old_level", result.oldLevel);
        resultIntent.putExtra("new_level", result.newLevel);
        resultIntent.putExtra("xp_after", result.xpAfter);
        resultIntent.putExtra("leveled_up", result.leveledUp);
        resultIntent.putExtra("demoted", result.demoted);
        resultIntent.putExtra("correct_answers", correct);

        if (result.leveledUp || result.demoted) {
            Intent lcIntent = new Intent(this, LevelChangeActivity.class);
            lcIntent.putExtra("leveled_up", result.leveledUp);
            lcIntent.putExtra("old_level", result.oldLevel);
            lcIntent.putExtra("new_level", result.newLevel);
            lcIntent.putExtra("old_tier", KnowledgeLevelManager.tierForLevel(result.oldLevel));
            lcIntent.putExtra("new_tier", KnowledgeLevelManager.tierForLevel(result.newLevel));
            // Pass result intent so LevelChangeActivity can forward to it
            lcIntent.putExtra("score", score);
            lcIntent.putExtra("total", TOTAL);
            lcIntent.putExtra("xp_earned", result.xpEarned);
            lcIntent.putExtra("new_level_final", result.newLevel);
            lcIntent.putExtra("xp_after", result.xpAfter);
            lcIntent.putExtra("correct_answers", correct);
            startActivity(lcIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        } else {
            startActivity(resultIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        }
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
