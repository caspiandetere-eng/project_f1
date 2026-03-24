package com.example.project_f1;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.project_f1.models.Driver;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class DriverComparisonActivity extends BaseActivity {

    public static final String EXTRA_DRIVER_A = "driver_a_id";
    public static final String EXTRA_DRIVER_B = "driver_b_id";

    private ThemeManager.TeamTheme theme;

    // ── Stat definition ───────────────────────────────────────────────────────
    private static final String[] STAT_LABELS  = {"Championships", "Wins", "Podiums", "Poles", "Career Pts", "Debut"};
    // weights for "who is better" score (championships & wins count most)
    private static final float[]  STAT_WEIGHTS = {4f, 3f, 2f, 1.5f, 1f, 0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = ThemeManager.applyFullTheme(this);

        String idA = getIntent().getStringExtra(EXTRA_DRIVER_A);
        String idB = getIntent().getStringExtra(EXTRA_DRIVER_B);

        Driver driverA = Driver.getDriverById(idA != null ? idA : "verstappen");
        Driver driverB = Driver.getDriverById(idB != null ? idB : "hamilton");

        AssetDataLoader.DriverCareerStats statsA = AssetDataLoader.getDriverStats(this, driverA.id);
        AssetDataLoader.DriverCareerStats statsB = AssetDataLoader.getDriverStats(this, driverB.id);

        AssetDataLoader.DriverTelemetry teleA = AssetDataLoader.getDriverTelemetry(this, driverA.id);
        AssetDataLoader.DriverTelemetry teleB = AssetDataLoader.getDriverTelemetry(this, driverB.id);

        // Fallback empty stats so UI never crashes
        if (statsA == null) statsA = emptyStats(driverA);
        if (statsB == null) statsB = emptyStats(driverB);

        int colorA = ThemeManager.getTeamTheme(driverA.teamId).accent;
        int colorB = ThemeManager.getTeamTheme(driverB.teamId).accent;

        NestedScrollView scroll = new NestedScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, dp(32));
        scroll.addView(root);

        buildTopStripe(root);
        buildHeader(root, driverA, driverB, colorA, colorB);
        buildVsBar(root, colorA, colorB);
        buildStatRows(root, statsA, statsB, colorA, colorB);
        buildSummary(root, statsA, statsB, colorA, colorB, driverA, driverB);
        buildAiInsights(root, statsA, statsB, teleA, teleB, driverA, driverB, colorA, colorB);

        setContentView(scroll);
    }

    // ── Top stripe + back ─────────────────────────────────────────────────────

    private void buildTopStripe(LinearLayout root) {
        View stripe = new View(this);
        stripe.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(4)));
        stripe.setBackgroundColor(theme.accent);
        root.addView(stripe);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bp.setMargins(dp(16), dp(48), dp(16), dp(4));
        bar.setLayoutParams(bp);

        MaterialButton btnBack = new MaterialButton(this);
        btnBack.setText("←");
        btnBack.setTextSize(18);
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setBackgroundTintList(ColorStateList.valueOf(theme.buttonBg));
        LinearLayout.LayoutParams bbp = new LinearLayout.LayoutParams(dp(44), dp(44));
        bbp.setMargins(0, 0, dp(14), 0);
        btnBack.setLayoutParams(bbp);
        btnBack.setPadding(0, 0, 0, 0);
        btnBack.setCornerRadius(dp(22));
        btnBack.setOnClickListener(v -> finish());
        bar.addView(btnBack);

        TextView title = new TextView(this);
        title.setText("HEAD TO HEAD");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(22);
        title.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        title.setLetterSpacing(0.1f);
        bar.addView(title);

        root.addView(bar);
    }

    // ── Driver header cards ───────────────────────────────────────────────────

    private void buildHeader(LinearLayout root, Driver a, Driver b, int colorA, int colorB) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(240)); // Increased height for 9:16
        rp.setMargins(dp(16), dp(16), dp(16), 0);
        row.setLayoutParams(rp);

        row.addView(buildDriverCard(a, colorA, Gravity.START));

        // VS badge in the middle
        FrameLayout vsFrame = new FrameLayout(this);
        vsFrame.setLayoutParams(new LinearLayout.LayoutParams(dp(40), LinearLayout.LayoutParams.MATCH_PARENT));
        TextView vs = new TextView(this);
        vs.setText("VS");
        vs.setTextColor(0xFFFFFFFF);
        vs.setTextSize(14);
        vs.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        vs.setLetterSpacing(0.1f);
        vs.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams vslp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        vslp.gravity = Gravity.CENTER;
        vsFrame.addView(vs, vslp);
        row.addView(vsFrame);

        row.addView(buildDriverCard(b, colorB, Gravity.END));
        root.addView(row);
    }

    private View buildDriverCard(Driver d, int teamColor, int nameGravity) {
        // Outer card
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        card.setLayoutParams(cp);
        card.setRadius(dp(16));
        card.setCardElevation(dp(8));
        card.setCardBackgroundColor(theme.cardBg);
        card.setStrokeColor(teamColor);
        card.setStrokeWidth(dp(2));
        card.setClipToOutline(true);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(inner);

        // Driver photo container: 9:16 Aspect Ratio (90dp x 160dp)
        FrameLayout photoFrame = new FrameLayout(this);
        LinearLayout.LayoutParams pfp = new LinearLayout.LayoutParams(dp(90), dp(160));
        pfp.setMargins(0, dp(12), 0, dp(8));
        photoFrame.setLayoutParams(pfp);
        
        // Rounded corners for the image container
        GradientDrawable rounded = new GradientDrawable();
        rounded.setCornerRadius(dp(8));
        rounded.setColor(ThemeManager.blendColors(theme.cardBg, teamColor, 0.1f));
        photoFrame.setBackground(rounded);

        ImageView photo = new ImageView(this);
        photo.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photo.setClipToOutline(true);
        photo.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);

        // Load image using Glide
        Object imageSource = d.photoResId != 0 ? d.photoResId : d.getPhotoUrl();
        Glide.with(this)
                .load(imageSource)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(photo);

        photoFrame.addView(photo);
        inner.addView(photoFrame);

        // Last name
        TextView lastName = new TextView(this);
        lastName.setText(d.lastName.toUpperCase());
        lastName.setTextColor(0xFFFFFFFF);
        lastName.setTextSize(16);
        lastName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        lastName.setLetterSpacing(0.04f);
        lastName.setGravity(Gravity.CENTER_HORIZONTAL);
        inner.addView(lastName);

        // Team name
        TextView teamTv = new TextView(this);
        teamTv.setText(d.teamName.toUpperCase());
        teamTv.setTextColor(teamColor);
        teamTv.setTextSize(10);
        teamTv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        teamTv.setLetterSpacing(0.1f);
        teamTv.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams ttp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ttp.setMargins(dp(4), dp(2), dp(4), dp(8));
        teamTv.setLayoutParams(ttp);
        inner.addView(teamTv);

        return card;
    }

    // ── VS divider bar ────────────────────────────────────────────────────────

    private void buildVsBar(LinearLayout root, int colorA, int colorB) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3));
        bp.setMargins(dp(16), dp(20), dp(16), dp(4));
        bar.setLayoutParams(bp);

        View left = new View(this);
        left.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        left.setBackgroundColor(colorA);
        bar.addView(left);

        View right = new View(this);
        right.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        right.setBackgroundColor(colorB);
        bar.addView(right);

        root.addView(bar);
    }

    // ── Stat rows ─────────────────────────────────────────────────────────────

    private void buildStatRows(LinearLayout root, AssetDataLoader.DriverCareerStats a,
                                AssetDataLoader.DriverCareerStats b, int colorA, int colorB) {
        int[] valsA = {a.championships, a.wins, a.podiums, a.poles, a.careerPoints, a.debutYear};
        int[] valsB = {b.championships, b.wins, b.podiums, b.poles, b.careerPoints, b.debutYear};

        // Section header
        TextView header = new TextView(this);
        header.setText("CAREER STATS");
        header.setTextColor(0xFF555555);
        header.setTextSize(10);
        header.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        header.setLetterSpacing(0.15f);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hp.setMargins(dp(20), dp(16), dp(20), dp(8));
        header.setLayoutParams(hp);
        root.addView(header);

        for (int i = 0; i < STAT_LABELS.length; i++) {
            root.addView(buildStatRow(STAT_LABELS[i], valsA[i], valsB[i],
                    colorA, colorB, i, STAT_WEIGHTS[i] > 0));
        }
    }

    private View buildStatRow(String label, int valA, int valB,
                               int colorA, int colorB, int rowIndex, boolean higherIsBetter) {
        // For "Debut" lower year = more experienced = better
        boolean aWins = higherIsBetter ? valA > valB : valA < valB;
        boolean bWins = higherIsBetter ? valB > valA : valB < valA;

        int activeA  = aWins ? colorA : 0xFF333333;
        int activeB  = bWins ? colorB : 0xFF333333;
        int textA    = aWins ? 0xFFFFFFFF : 0xFF666666;
        int textB    = bWins ? 0xFFFFFFFF : 0xFF666666;

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setAlpha(0f);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(dp(16), 0, dp(16), dp(10));
        row.setLayoutParams(rp);

        // Card wrapper
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setRadius(dp(12));
        card.setCardElevation(dp(4));
        card.setCardBackgroundColor(theme.cardBg);
        card.setStrokeColor(ThemeManager.blendColors(theme.cardBg,
                aWins ? colorA : bWins ? colorB : 0xFF444444, 0.4f));
        card.setStrokeWidth(dp(1));

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(14), dp(12), dp(14), dp(10));
        card.addView(inner);

        // Values row
        LinearLayout valRow = new LinearLayout(this);
        valRow.setOrientation(LinearLayout.HORIZONTAL);
        valRow.setGravity(Gravity.CENTER_VERTICAL);
        valRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Value A
        TextView tvA = new TextView(this);
        tvA.setText(String.valueOf(valA));
        tvA.setTextColor(textA);
        tvA.setTextSize(aWins ? 26 : 20);
        tvA.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvA.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvA.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        valRow.addView(tvA);

        // Stat label (center)
        TextView tvLabel = new TextView(this);
        tvLabel.setText(label.toUpperCase());
        tvLabel.setTextColor(0xFF888888);
        tvLabel.setTextSize(9);
        tvLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvLabel.setLetterSpacing(0.1f);
        tvLabel.setGravity(Gravity.CENTER);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        valRow.addView(tvLabel);

        // Value B
        TextView tvB = new TextView(this);
        tvB.setText(String.valueOf(valB));
        tvB.setTextColor(textB);
        tvB.setTextSize(bWins ? 26 : 20);
        tvB.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvB.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvB.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        valRow.addView(tvB);

        inner.addView(valRow);

        // Progress bar (skip for debut year and when both are 0)
        if (STAT_WEIGHTS[indexOf(label)] > 0 && (valA > 0 || valB > 0)) {
            int total = valA + valB;
            float fracA = total > 0 ? (float) valA / total : 0.5f;

            LinearLayout barRow = new LinearLayout(this);
            barRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams brp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(4));
            brp.setMargins(0, dp(8), 0, 0);
            barRow.setLayoutParams(brp);
            barRow.setClipToOutline(true);
            GradientDrawable barBg = new GradientDrawable();
            barBg.setCornerRadius(dp(2));
            barBg.setColor(0xFF1A1A1A);
            barRow.setBackground(barBg);

            View segA = new View(this);
            LinearLayout.LayoutParams saP = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, fracA);
            segA.setLayoutParams(saP);
            segA.setBackgroundColor(aWins ? colorA : ThemeManager.blendColors(colorA, 0xFF000000, 0.6f));
            barRow.addView(segA);

            View segB = new View(this);
            LinearLayout.LayoutParams sbP = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f - fracA);
            segB.setLayoutParams(sbP);
            segB.setBackgroundColor(bWins ? colorB : ThemeManager.blendColors(colorB, 0xFF000000, 0.6f));
            barRow.addView(segB);

            inner.addView(barRow);
        }

        row.addView(card);

        // Count-up animation + fade-in
        long delay = 80L + rowIndex * 60L;
        row.postDelayed(() -> {
            row.animate().alpha(1f).setDuration(250)
                    .setInterpolator(new DecelerateInterpolator()).start();
            animateCount(tvA, valA, delay);
            animateCount(tvB, valB, delay);
        }, delay);

        return row;
    }

    // ── Summary card ──────────────────────────────────────────────────────────

    private void buildSummary(LinearLayout root,
                               AssetDataLoader.DriverCareerStats a,
                               AssetDataLoader.DriverCareerStats b,
                               int colorA, int colorB,
                               Driver driverA, Driver driverB) {
        float scoreA = weightedScore(a);
        float scoreB = weightedScore(b);

        boolean aLeads = scoreA > scoreB;
        boolean tied   = scoreA == scoreB;

        String winnerName = tied ? "TIED" :
                (aLeads ? driverA.lastName : driverB.lastName).toUpperCase();
        int winnerColor   = tied ? 0xFFFFFFFF : (aLeads ? colorA : colorB);

        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(dp(16), dp(8), dp(16), 0);
        card.setLayoutParams(cp);
        card.setRadius(dp(16));
        card.setCardElevation(dp(10));
        card.setCardBackgroundColor(ThemeManager.blendColors(theme.cardBg, winnerColor, 0.08f));
        card.setStrokeColor(winnerColor);
        card.setStrokeWidth(dp(2));
        card.setAlpha(0f);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setGravity(Gravity.CENTER_HORIZONTAL);
        inner.setPadding(dp(20), dp(18), dp(20), dp(18));
        card.addView(inner);

        TextView label = new TextView(this);
        label.setText("OVERALL EDGE");
        label.setTextColor(0xFF666666);
        label.setTextSize(10);
        label.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        label.setLetterSpacing(0.15f);
        label.setGravity(Gravity.CENTER);
        inner.addView(label);

        TextView winner = new TextView(this);
        winner.setText(winnerName);
        winner.setTextColor(winnerColor);
        winner.setTextSize(28);
        winner.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        winner.setLetterSpacing(0.06f);
        winner.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wp.setMargins(0, dp(4), 0, dp(6));
        winner.setLayoutParams(wp);
        inner.addView(winner);

        // Score breakdown
        TextView scores = new TextView(this);
        scores.setText(String.format(Locale.US, "Score  %.0f  —  %.0f", scoreA, scoreB));
        scores.setTextColor(0xFF888888);
        scores.setTextSize(12);
        scores.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono), Typeface.NORMAL);
        scores.setGravity(Gravity.CENTER);
        inner.addView(scores);

        root.addView(card);

        card.postDelayed(() ->
                card.animate().alpha(1f).setDuration(350)
                        .setInterpolator(new DecelerateInterpolator()).start(),
                STAT_LABELS.length * 60L + 200L);
    }

    // ── AI Insights Card ───────────────────────────────────────────────────────

    private void buildAiInsights(LinearLayout root,
                                 AssetDataLoader.DriverCareerStats a,
                                 AssetDataLoader.DriverCareerStats b,
                                 AssetDataLoader.DriverTelemetry ta,
                                 AssetDataLoader.DriverTelemetry tb,
                                 Driver driverA, Driver driverB,
                                 int colorA, int colorB) {
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(dp(16), dp(24), dp(16), 0);
        card.setLayoutParams(cp);
        card.setRadius(dp(16));
        card.setCardElevation(dp(8));
        card.setCardBackgroundColor(0xFF121212);
        card.setStrokeColor(0xFF333333);
        card.setStrokeWidth(dp(1));
        card.setAlpha(0f);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(20), dp(20), dp(20), dp(20));
        card.addView(inner);

        // AI Header Row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        inner.addView(headerRow);

        TextView aiLabel = new TextView(this);
        aiLabel.setText("AI ADVANCED COMPARISON");
        aiLabel.setTextColor(0xFF00FFCC); // Neon AI cyan
        aiLabel.setTextSize(12);
        aiLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        aiLabel.setLetterSpacing(0.2f);
        headerRow.addView(aiLabel);

        View glowDot = new View(this);
        LinearLayout.LayoutParams gdp = new LinearLayout.LayoutParams(dp(6), dp(6));
        gdp.setMargins(dp(8), 0, 0, 0);
        glowDot.setLayoutParams(gdp);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(0xFF00FFCC);
        glowDot.setBackground(dotBg);
        headerRow.addView(glowDot);

        // Generate Insights Text
        TextView insightText = new TextView(this);
        insightText.setText(generateAdvancedInsights(a, b, ta, tb, driverA, driverB));
        insightText.setTextColor(0xFFBBBBBB);
        insightText.setTextSize(14);
        insightText.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        insightText.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams itp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        itp.setMargins(0, dp(16), 0, 0);
        insightText.setLayoutParams(itp);
        inner.addView(insightText);

        root.addView(card);

        card.postDelayed(() ->
                card.animate().alpha(1f).setDuration(500)
                        .setInterpolator(new DecelerateInterpolator()).start(),
                STAT_LABELS.length * 60L + 600L);
    }

    private String generateAdvancedInsights(AssetDataLoader.DriverCareerStats a,
                                            AssetDataLoader.DriverCareerStats b,
                                            AssetDataLoader.DriverTelemetry ta,
                                            AssetDataLoader.DriverTelemetry tb,
                                            Driver da, Driver db) {
        StringBuilder sb = new StringBuilder();

        // 1. Performance Consistency (Telemetry based)
        if (ta != null && tb != null) {
            double vDiff = Math.abs(ta.avgSpeed - tb.avgSpeed);
            if (vDiff > 5) {
                String faster = ta.avgSpeed > tb.avgSpeed ? da.lastName : db.lastName;
                sb.append("• Race Pace: Telemetry shows ").append(faster).append(" maintains a higher average entry velocity, suggesting superior mid-corner rotation.\n\n");
            }
        }

        // 2. High-Pressure Conversion
        float winConvA = a.podiums > 0 ? (float) a.wins / a.podiums : 0;
        float winConvB = b.podiums > 0 ? (float) b.wins / b.podiums : 0;
        if (Math.abs(winConvA - winConvB) > 0.1f) {
            String closer = winConvA > winConvB ? da.lastName : db.lastName;
            sb.append("• Winning Mentality: When on the podium, ").append(closer).append(" has a higher probability of converting the result into a P1 finish.\n\n");
        }

        // 3. Technical Adaptability
        int ageA = 2026 - a.debutYear;
        int ageB = 2026 - b.debutYear;
        if (Math.abs(ageA - ageB) > 8) {
            String young = ageA < ageB ? da.lastName : db.lastName;
            String senior = ageA > ageB ? da.lastName : db.lastName;
            sb.append("• Regulation Shift: ").append(senior).append("'s vast experience across engine eras provides an advantage in troubleshooting, while ").append(young).append("'s data-driven approach favors modern sim-heavy setups.\n\n");
        }

        // 4. Team Synergy
        sb.append("• Prediction: AI projects a tight battle. ").append(da.lastName).append("'s style suits high-downforce circuits, whereas ").append(db.lastName).append(" excels in technical, low-speed sectors.");

        return sb.toString();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private float weightedScore(AssetDataLoader.DriverCareerStats s) {
        int[] vals = {s.championships, s.wins, s.podiums, s.poles, s.careerPoints, 0};
        float score = 0;
        for (int i = 0; i < STAT_WEIGHTS.length; i++) score += vals[i] * STAT_WEIGHTS[i];
        return score;
    }

    private int indexOf(String label) {
        for (int i = 0; i < STAT_LABELS.length; i++)
            if (STAT_LABELS[i].equals(label)) return i;
        return 0;
    }

    private void animateCount(TextView tv, int target, long delay) {
        if (target == 0) return;
        ValueAnimator anim = ValueAnimator.ofInt(0, target);
        anim.setDuration(600);
        anim.setStartDelay(delay);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> tv.setText(String.valueOf((int) a.getAnimatedValue())));
        anim.start();
    }

    private AssetDataLoader.DriverCareerStats emptyStats(Driver d) {
        AssetDataLoader.DriverCareerStats s = new AssetDataLoader.DriverCareerStats();
        s.name = d.getFullName();
        s.team = d.teamName;
        return s;
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
