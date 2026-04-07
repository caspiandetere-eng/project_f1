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

        int score         = getIntent().getIntExtra("score", 0);
        int total         = getIntent().getIntExtra("total", 30);
        int xpEarned      = getIntent().getIntExtra("xp_earned", 0);
        int oldLevel      = getIntent().getIntExtra("old_level", 1);
        int newLevel      = getIntent().getIntExtra("new_level", 1);
        int xpAfter       = getIntent().getIntExtra("xp_after", 0);
        boolean leveledUp = getIntent().getBooleanExtra("leveled_up", false);
        boolean demoted   = getIntent().getBooleanExtra("demoted", false);
        boolean[] correct = getIntent().getBooleanArrayExtra("correct_answers");

        String newTier = KnowledgeLevelManager.tierForLevel(newLevel);
        int pct = (int) Math.round(score * 100.0 / total);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(px(24, dp), px(56, dp), px(24, dp), px(48, dp));
        scroll.addView(root);

        // ── Score circle ──────────────────────────────────────────────────────
        TextView tvScore = new TextView(this);
        tvScore.setText(score + "\n/ " + total);
        tvScore.setTextColor(getResources().getColor(R.color.quiz_text_primary, null));
        tvScore.setTextSize(30);
        tvScore.setGravity(Gravity.CENTER);
        tvScore.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvScore.setLineSpacing(0, 1.1f);
        int circleSize = px(120, dp);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleSize, circleSize);
        circleParams.gravity = Gravity.CENTER_HORIZONTAL;
        circleParams.setMargins(0, 0, 0, px(12, dp));
        tvScore.setLayoutParams(circleParams);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        int circleColor = pct >= 70 ? getResources().getColor(R.color.quiz_correct_bg, null)
                : pct >= 40 ? ThemeManager.blendColors(getResources().getColor(R.color.quiz_bg, null), theme.accent, 0.15f)
                : getResources().getColor(R.color.quiz_wrong_bg, null);
        int strokeColor = pct >= 70 ? getResources().getColor(R.color.quiz_correct_stroke, null)
                : pct >= 40 ? theme.accent
                : getResources().getColor(R.color.quiz_wrong_stroke, null);
        circleBg.setColor(circleColor);
        circleBg.setStroke(px(3, dp), strokeColor);
        tvScore.setBackground(circleBg);
        root.addView(tvScore);

        // Percentage label
        TextView tvPct = new TextView(this);
        tvPct.setText(pct + "%  ·  " + (pct >= 70 ? "Full XP" : pct >= 40 ? "Half XP" : "No XP"));
        tvPct.setTextColor(pct >= 70 ? getResources().getColor(R.color.quiz_correct_text, null)
                : pct >= 40 ? getResources().getColor(R.color.quiz_score_mid_text, null)
                : getResources().getColor(R.color.quiz_wrong_text, null));
        tvPct.setTextSize(13);
        tvPct.setGravity(Gravity.CENTER);
        tvPct.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        LinearLayout.LayoutParams pctParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pctParams.setMargins(0, 0, 0, px(20, dp));
        tvPct.setLayoutParams(pctParams);
        root.addView(tvPct);

        // ── Level change banner (only if no LevelChangeActivity was shown, i.e. no change) ──
        if (leveledUp || demoted) {
            TextView tvChange = new TextView(this);
            tvChange.setText(leveledUp
                    ? "⬆  LEVEL UP!  L" + oldLevel + " → L" + newLevel
                    : "⬇  DEMOTED  L" + oldLevel + " → L" + newLevel);
            tvChange.setTextColor(leveledUp ? getResources().getColor(R.color.quiz_correct_text, null) : getResources().getColor(R.color.quiz_wrong_text, null));
            tvChange.setTextSize(14);
            tvChange.setGravity(Gravity.CENTER);
            tvChange.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            tvChange.setLetterSpacing(0.05f);
            tvChange.setPadding(px(20, dp), px(10, dp), px(20, dp), px(10, dp));
            GradientDrawable changeBg = new GradientDrawable();
            changeBg.setColor(leveledUp ? getResources().getColor(R.color.quiz_level_up_bg, null) : getResources().getColor(R.color.quiz_demote_bg, null));
            changeBg.setCornerRadius(px(10, dp));
            changeBg.setStroke(px(1, dp), leveledUp ? getResources().getColor(R.color.quiz_level_up_stroke, null) : getResources().getColor(R.color.quiz_demote_stroke, null));
            tvChange.setBackground(changeBg);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cp.setMargins(0, 0, 0, px(16, dp));
            tvChange.setLayoutParams(cp);
            root.addView(tvChange);
        }

        // ── Tier + level badge ────────────────────────────────────────────────
        TextView tvLevel = new TextView(this);
        tvLevel.setText(newTier.toUpperCase() + "  ·  L" + newLevel);
        tvLevel.setTextColor(getResources().getColor(R.color.quiz_btn_text, null));
        tvLevel.setTextSize(14);
        tvLevel.setGravity(Gravity.CENTER);
        tvLevel.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvLevel.setPadding(px(28, dp), px(10, dp), px(28, dp), px(10, dp));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(theme.accent);
        badgeBg.setCornerRadius(px(20, dp));
        tvLevel.setBackground(badgeBg);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgeParams.setMargins(0, 0, 0, px(24, dp));
        tvLevel.setLayoutParams(badgeParams);
        root.addView(tvLevel);

        // ── XP card ───────────────────────────────────────────────────────────
        LinearLayout xpCard = makeCard(theme, dp);
        LinearLayout.LayoutParams xpCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        xpCardParams.setMargins(0, 0, 0, px(16, dp));
        xpCard.setLayoutParams(xpCardParams);
        root.addView(xpCard);

        LinearLayout xpRow = new LinearLayout(this);
        xpRow.setOrientation(LinearLayout.HORIZONTAL);
        xpRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams xpRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        xpRowParams.setMargins(0, 0, 0, px(10, dp));
        xpRow.setLayoutParams(xpRowParams);

        TextView tvXpLabel = new TextView(this);
        tvXpLabel.setText("XP PROGRESS");
        tvXpLabel.setTextColor(theme.accent);
        tvXpLabel.setTextSize(11);
        tvXpLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvXpLabel.setLetterSpacing(0.1f);
        tvXpLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        xpRow.addView(tvXpLabel);

        TextView tvXpEarned = new TextView(this);
        tvXpEarned.setText(xpEarned > 0 ? "+" + xpEarned + " XP" : "0 XP");
        tvXpEarned.setTextColor(xpEarned > 0 ? getResources().getColor(R.color.quiz_correct_text, null) : getResources().getColor(R.color.quiz_text_secondary, null));
        tvXpEarned.setTextSize(13);
        tvXpEarned.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        xpRow.addView(tvXpEarned);
        xpCard.addView(xpRow);

        LinearLayout xpTrack = new LinearLayout(this);
        xpTrack.setOrientation(LinearLayout.HORIZONTAL);
        GradientDrawable trackBg = new GradientDrawable();
        trackBg.setColor(getResources().getColor(R.color.quiz_xp_track, null));
        trackBg.setCornerRadius(px(4, dp));
        xpTrack.setBackground(trackBg);
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, px(8, dp));
        trackParams.setMargins(0, 0, 0, px(8, dp));
        xpTrack.setLayoutParams(trackParams);

        float fillFraction = Math.min((float) xpAfter / KnowledgeLevelManager.XP_PER_LEVEL, 1f);
        View xpFill = new View(this);
        GradientDrawable fillBg = new GradientDrawable();
        fillBg.setColor(theme.accent);
        fillBg.setCornerRadius(px(4, dp));
        xpFill.setBackground(fillBg);
        xpFill.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, fillFraction));
        xpTrack.addView(xpFill);
        View xpRem = new View(this);
        xpRem.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f - fillFraction));
        xpTrack.addView(xpRem);
        xpCard.addView(xpTrack);

        TextView tvXpNumbers = new TextView(this);
        tvXpNumbers.setText(xpAfter + " / " + KnowledgeLevelManager.XP_PER_LEVEL
                + " XP  ·  L" + newLevel + " → L" + Math.min(newLevel + 1, KnowledgeLevelManager.MAX_LEVEL));
        tvXpNumbers.setTextColor(getResources().getColor(R.color.quiz_xp_numbers, null));
        tvXpNumbers.setTextSize(12);
        tvXpNumbers.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        xpCard.addView(tvXpNumbers);

        // ── Per-question breakdown ────────────────────────────────────────────
        if (correct != null && correct.length == total) {
            LinearLayout breakdownCard = makeCard(theme, dp);
            LinearLayout.LayoutParams bcParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bcParams.setMargins(0, 0, 0, px(16, dp));
            breakdownCard.setLayoutParams(bcParams);
            root.addView(breakdownCard);

            TextView tvBdTitle = new TextView(this);
            tvBdTitle.setText("QUESTION BREAKDOWN");
            tvBdTitle.setTextColor(theme.accent);
            tvBdTitle.setTextSize(11);
            tvBdTitle.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            tvBdTitle.setLetterSpacing(0.1f);
            LinearLayout.LayoutParams bdtp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bdtp.setMargins(0, 0, 0, px(12, dp));
            tvBdTitle.setLayoutParams(bdtp);
            breakdownCard.addView(tvBdTitle);

            // Grid of dots: 10 per row
            int cols = 10;
            for (int row = 0; row < Math.ceil((double) total / cols); row++) {
                LinearLayout dotRow = new LinearLayout(this);
                dotRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams drp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                drp.setMargins(0, 0, 0, px(6, dp));
                dotRow.setLayoutParams(drp);

                for (int col = 0; col < cols; col++) {
                    int idx = row * cols + col;
                    if (idx >= total) break;

                    LinearLayout cell = new LinearLayout(this);
                    cell.setOrientation(LinearLayout.VERTICAL);
                    cell.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams cp2 = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    cp2.setMargins(px(2, dp), 0, px(2, dp), 0);
                    cell.setLayoutParams(cp2);

                    // Dot
                    View dot = new View(this);
                    int dotSize = px(18, dp);
                    LinearLayout.LayoutParams dotp = new LinearLayout.LayoutParams(dotSize, dotSize);
                    dotp.gravity = Gravity.CENTER_HORIZONTAL;
                    dotp.setMargins(0, 0, 0, px(3, dp));
                    dot.setLayoutParams(dotp);
                    GradientDrawable dotBg = new GradientDrawable();
                    dotBg.setShape(GradientDrawable.OVAL);
                    dotBg.setColor(correct[idx] ? getResources().getColor(R.color.quiz_correct_stroke, null) : getResources().getColor(R.color.quiz_wrong_stroke, null));
                    dot.setBackground(dotBg);
                    cell.addView(dot);

                    // Q number
                    TextView tvNum = new TextView(this);
                    tvNum.setText(String.valueOf(idx + 1));
                    tvNum.setTextColor(getResources().getColor(R.color.quiz_text_faint, null));
                    tvNum.setTextSize(9);
                    tvNum.setGravity(Gravity.CENTER);
                    tvNum.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed));
                    cell.addView(tvNum);

                    dotRow.addView(cell);
                }
                breakdownCard.addView(dotRow);
            }

            // Legend
            LinearLayout legend = new LinearLayout(this);
            legend.setOrientation(LinearLayout.HORIZONTAL);
            legend.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, px(10, dp), 0, 0);
            legend.setLayoutParams(lp);

            legend.addView(makeLegendDot(getResources().getColor(R.color.quiz_correct_stroke, null), dp));
            legend.addView(makeLegendLabel("Correct  ", dp));
            legend.addView(makeLegendDot(getResources().getColor(R.color.quiz_wrong_stroke, null), dp));
            legend.addView(makeLegendLabel("Wrong", dp));
            breakdownCard.addView(legend);
        }

        // ── Content mode card ─────────────────────────────────────────────────
        LinearLayout infoCard = makeCard(theme, dp);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoParams.setMargins(0, 0, 0, px(32, dp));
        infoCard.setLayoutParams(infoParams);
        root.addView(infoCard);

        TextView tvInfoTitle = new TextView(this);
        tvInfoTitle.setText("CONTENT MODE");
        tvInfoTitle.setTextColor(theme.accent);
        tvInfoTitle.setTextSize(11);
        tvInfoTitle.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvInfoTitle.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams itp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        itp.setMargins(0, 0, 0, px(8, dp));
        tvInfoTitle.setLayoutParams(itp);
        infoCard.addView(tvInfoTitle);

        TextView tvInfoBody = new TextView(this);
        tvInfoBody.setText(contentModeDescription(newTier));
        tvInfoBody.setTextColor(getResources().getColor(R.color.quiz_option_text, null));
        tvInfoBody.setTextSize(14);
        tvInfoBody.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        tvInfoBody.setLineSpacing(0, 1.4f);
        infoCard.addView(tvInfoBody);

        if (demoted) {
            TextView tvTip = new TextView(this);
            tvTip.setText("Score ≥ 70% to level up  ·  Score < 40% causes demotion");
            tvTip.setTextColor(getResources().getColor(R.color.quiz_legend_text, null));
            tvTip.setTextSize(12);
            tvTip.setGravity(Gravity.CENTER);
            tvTip.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
            LinearLayout.LayoutParams tipParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tipParams.setMargins(0, px(8, dp), 0, px(24, dp));
            tvTip.setLayoutParams(tipParams);
            root.addView(tvTip);
        }

        // ── Continue button ───────────────────────────────────────────────────
        TextView btnContinue = new TextView(this);
        btnContinue.setText("CONTINUE TO APP");
        btnContinue.setTextColor(getResources().getColor(R.color.quiz_btn_text, null));
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

        TextView btnRetake = new TextView(this);
        btnRetake.setText("Retake quiz");
        btnRetake.setTextColor(getResources().getColor(R.color.quiz_legend_text, null));
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
        root.setAlpha(0f);
        root.animate().alpha(1f).setDuration(400).start();
    }

    private View makeLegendDot(int color, float dp) {
        View dot = new View(this);
        int size = px(10, dp);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(size, size);
        p.gravity = Gravity.CENTER_VERTICAL;
        p.setMargins(0, 0, px(4, dp), 0);
        dot.setLayoutParams(p);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(color);
        dot.setBackground(bg);
        return dot;
    }

    private TextView makeLegendLabel(String text, float dp) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.quiz_legend_text, null));
        tv.setTextSize(12);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        return tv;
    }

    private LinearLayout makeCard(ThemeManager.TeamTheme theme, float dp) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(px(20, dp), px(18, dp), px(20, dp), px(18, dp));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ThemeManager.blendColors(getResources().getColor(R.color.quiz_bg, null), theme.accent, 0.07f));
        bg.setCornerRadius(px(16, dp));
        bg.setStroke(px(1, dp), ThemeManager.blendColors(getResources().getColor(R.color.quiz_card_stroke, null), theme.accent, 0.28f));
        card.setBackground(bg);
        return card;
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        finish();
    }

    private String contentModeDescription(String tier) {
        switch (tier) {
            case "casual":      return "You'll see mostly general content with some technical details.";
            case "enthusiast":  return "You'll see a mix of general and detailed technical content.";
            case "insider":     return "You'll see full detailed content across all sections.";
            default:            return "You'll see general content to help you learn F1.";
        }
    }

    private int px(int dp, float density) { return (int) (dp * density); }
}
