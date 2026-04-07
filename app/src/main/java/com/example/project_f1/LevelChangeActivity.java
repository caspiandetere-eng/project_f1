package com.example.project_f1;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

/**
 * Full-screen dramatic page shown when the user levels up or gets demoted.
 * Auto-advances to QuizResultActivity after 4 seconds, or immediately on tap.
 */
public class LevelChangeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        float dp = getResources().getDisplayMetrics().density;

        boolean leveledUp = getIntent().getBooleanExtra("leveled_up", true);
        int oldLevel  = getIntent().getIntExtra("old_level", 1);
        int newLevel  = getIntent().getIntExtra("new_level", 1);
        String oldTier = getIntent().getStringExtra("old_tier");
        String newTier = getIntent().getStringExtra("new_tier");
        if (oldTier == null) oldTier = KnowledgeLevelManager.tierForLevel(oldLevel);
        if (newTier == null) newTier = KnowledgeLevelManager.tierForLevel(newLevel);

        boolean tierChanged = !oldTier.equals(newTier);

        // Root
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(px(32, dp), px(64, dp), px(32, dp), px(48, dp));

        // Gradient background
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                leveledUp
                    ? new int[]{theme.bgTop, ThemeManager.blendColors(theme.bgBottom, 0xFF0A2A0A, 0.4f)}
                    : new int[]{theme.bgTop, ThemeManager.blendColors(theme.bgBottom, 0xFF2A0A0A, 0.4f)});
        root.setBackground(bg);

        // Big emoji
        TextView tvEmoji = new TextView(this);
        tvEmoji.setText(leveledUp ? (tierChanged ? "🏆" : "⬆") : (tierChanged ? "💔" : "⬇"));
        tvEmoji.setTextSize(80);
        tvEmoji.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ep.setMargins(0, 0, 0, px(24, dp));
        tvEmoji.setLayoutParams(ep);
        root.addView(tvEmoji);

        // Main headline
        TextView tvHeadline = new TextView(this);
        if (leveledUp) {
            tvHeadline.setText(tierChanged ? "TIER UP!" : "LEVEL UP!");
        } else {
            tvHeadline.setText(tierChanged ? "TIER DOWN" : "DEMOTED");
        }
        tvHeadline.setTextColor(leveledUp ? 0xFF2ECC71 : 0xFFE74C3C);
        tvHeadline.setTextSize(48);
        tvHeadline.setGravity(Gravity.CENTER);
        tvHeadline.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvHeadline.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hp.setMargins(0, 0, 0, px(16, dp));
        tvHeadline.setLayoutParams(hp);
        root.addView(tvHeadline);

        // Level transition row  e.g.  "L3  →  L4"  or  "ROOKIE · L5  →  CASUAL · L6"
        LinearLayout transRow = new LinearLayout(this);
        transRow.setOrientation(LinearLayout.HORIZONTAL);
        transRow.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams trp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        trp.setMargins(0, 0, 0, px(32, dp));
        transRow.setLayoutParams(trp);

        transRow.addView(makeLevelBadge(
                oldTier.toUpperCase() + " · L" + oldLevel,
                leveledUp ? 0xFF444444 : theme.accent, dp));

        TextView tvArrow = new TextView(this);
        tvArrow.setText("  →  ");
        tvArrow.setTextColor(0xFF888888);
        tvArrow.setTextSize(22);
        tvArrow.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed));
        transRow.addView(tvArrow);

        transRow.addView(makeLevelBadge(
                newTier.toUpperCase() + " · L" + newLevel,
                leveledUp ? theme.accent : 0xFF444444, dp));

        root.addView(transRow);

        // Flavour text
        TextView tvFlavour = new TextView(this);
        tvFlavour.setText(flavourText(leveledUp, tierChanged, newTier));
        tvFlavour.setTextColor(0xFFAAAAAA);
        tvFlavour.setTextSize(16);
        tvFlavour.setGravity(Gravity.CENTER);
        tvFlavour.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        tvFlavour.setLineSpacing(0, 1.5f);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fp.setMargins(0, 0, 0, px(48, dp));
        tvFlavour.setLayoutParams(fp);
        root.addView(tvFlavour);

        // Accent divider
        View divider = new View(this);
        GradientDrawable divBg = new GradientDrawable();
        divBg.setColor(leveledUp ? 0xFF2ECC71 : 0xFFE74C3C);
        divBg.setCornerRadius(px(2, dp));
        divider.setBackground(divBg);
        LinearLayout.LayoutParams divp = new LinearLayout.LayoutParams(px(60, dp), px(3, dp));
        divp.gravity = Gravity.CENTER_HORIZONTAL;
        divp.setMargins(0, 0, 0, px(32, dp));
        divider.setLayoutParams(divp);
        root.addView(divider);

        // Continue button
        TextView btnContinue = new TextView(this);
        btnContinue.setText("SEE FULL RESULTS");
        btnContinue.setTextColor(0xFF000000);
        btnContinue.setTextSize(15);
        btnContinue.setGravity(Gravity.CENTER);
        btnContinue.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        btnContinue.setPadding(0, px(18, dp), 0, px(18, dp));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(theme.accent);
        btnBg.setCornerRadius(px(12, dp));
        btnContinue.setBackground(btnBg);
        LinearLayout.LayoutParams btnp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnp.setMargins(0, 0, 0, px(12, dp));
        btnContinue.setLayoutParams(btnp);
        btnContinue.setOnClickListener(v -> goToResult());
        root.addView(btnContinue);

        // Auto-hint
        TextView tvHint = new TextView(this);
        tvHint.setText("Tap anywhere or wait to continue");
        tvHint.setTextColor(0xFF444444);
        tvHint.setTextSize(12);
        tvHint.setGravity(Gravity.CENTER);
        tvHint.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        root.addView(tvHint);

        setContentView(root);

        // Entrance animation
        root.setAlpha(0f);
        root.setScaleX(0.92f);
        root.setScaleY(0.92f);
        root.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500)
                .setInterpolator(new android.view.animation.DecelerateInterpolator()).start();

        // Tap anywhere to skip
        root.setOnClickListener(v -> goToResult());

        // Auto-advance after 4 seconds
        new Handler(Looper.getMainLooper()).postDelayed(this::goToResult, 4000);
    }

    private void goToResult() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("score",           getIntent().getIntExtra("score", 0));
        intent.putExtra("total",           getIntent().getIntExtra("total", 30));
        intent.putExtra("xp_earned",       getIntent().getIntExtra("xp_earned", 0));
        intent.putExtra("old_level",       getIntent().getIntExtra("old_level", 1));
        intent.putExtra("new_level",       getIntent().getIntExtra("new_level_final", 1));
        intent.putExtra("xp_after",        getIntent().getIntExtra("xp_after", 0));
        intent.putExtra("leveled_up",      getIntent().getBooleanExtra("leveled_up", false));
        intent.putExtra("demoted",         !getIntent().getBooleanExtra("leveled_up", true));
        intent.putExtra("correct_answers", getIntent().getBooleanArrayExtra("correct_answers"));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        finish();
    }

    private TextView makeLevelBadge(String text, int color, float dp) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(13);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tv.setPadding(px(14, dp), px(8, dp), px(14, dp), px(8, dp));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(px(8, dp));
        tv.setBackground(bg);
        return tv;
    }

    private String flavourText(boolean leveledUp, boolean tierChanged, String newTier) {
        if (leveledUp && tierChanged) {
            switch (newTier) {
                case "casual":      return "You've moved beyond the basics.\nThe paddock is starting to notice you.";
                case "enthusiast":  return "You're thinking like a race engineer.\nStrategy, data, and speed — you get it.";
                case "insider":     return "You've reached the top tier.\nYou know F1 like a team principal.";
                default:            return "A new chapter begins.";
            }
        } else if (leveledUp) {
            return "Your knowledge is growing.\nKeep pushing — the next level awaits.";
        } else if (tierChanged) {
            return "A tough session. Every champion\nhas bad races — come back stronger.";
        } else {
            return "Score ≥ 70% to level up.\nScore < 40% causes demotion.";
        }
    }

    private int px(int dp, float density) { return (int) (dp * density); }
}
