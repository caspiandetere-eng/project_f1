package com.example.project_f1;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.project_f1.models.Driver;
import com.example.project_f1.models.Team;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class TeamsActivity extends BaseActivity {

    private ThemeManager.TeamTheme theme;
    private boolean isEnthusiast;

    private static int getCar2026Res(String teamId) {
        if (teamId == null) return 0;
        switch (teamId) {
            case "mercedes":     return R.drawable.car_mercedes_2026;
            case "ferrari":      return R.drawable.car_ferrari_2026;
            case "mclaren":      return R.drawable.car_mclaren_2026;
            case "red_bull":     return R.drawable.car_red_bull_2026;
            case "aston_martin": return R.drawable.car_aston_martin_2026;
            case "audi":         return R.drawable.car_audi_2026;
            case "cadillac":     return R.drawable.car_cadillac_2026;
            case "alpine":       return R.drawable.car_alpine_2026;
            case "williams":     return R.drawable.car_williams_2026;
            case "rb":           return R.drawable.car_red_bull_2026;
            case "haas":         return R.drawable.car_haas_2026;
            default:             return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = ThemeManager.applyFullTheme(this);
        isEnthusiast = KnowledgeLevelManager.getKnowledgeLevel(this).level
                >= KnowledgeLevelManager.KnowledgeLevel.ENTHUSIAST.level;

        androidx.core.widget.NestedScrollView scroll = new androidx.core.widget.NestedScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), 0, dp(16), dp(32));
        scroll.addView(root);

        addHeader(root);
        addSectionLabel(root, "2026 CONSTRUCTORS");

        List<Team> teams = Team.getAllTeams();
        for (int i = 0; i < teams.size(); i++) {
            View card = buildTeamCard(teams.get(i));
            card.setAlpha(0f);
            root.addView(card);
            card.animate().alpha(1f).setDuration(350).setStartDelay(i * 60L).start();
        }

        setContentView(scroll);
    }

    private void addHeader(LinearLayout root) {
        View stripe = new View(this);
        stripe.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(4)));
        stripe.setBackgroundColor(theme.accent);
        root.addView(stripe);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hp.setMargins(0, dp(16), 0, dp(20));
        header.setLayoutParams(hp);

        MaterialButton btnBack = new MaterialButton(this);
        btnBack.setText("←");
        btnBack.setTextSize(18);
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setBackgroundTintList(ColorStateList.valueOf(theme.buttonBg));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(dp(44), dp(44));
        bp.setMargins(0, 0, dp(14), 0);
        btnBack.setLayoutParams(bp);
        btnBack.setPadding(0, 0, 0, 0);
        btnBack.setCornerRadius(dp(22));
        btnBack.setOnClickListener(v -> finish());
        header.addView(btnBack);

        View bar = new View(this);
        LinearLayout.LayoutParams barP = new LinearLayout.LayoutParams(dp(4), dp(52));
        barP.setMargins(0, 0, dp(12), 0);
        bar.setLayoutParams(barP);
        bar.setBackgroundColor(theme.accent);
        header.addView(bar);

        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("2026 TEAMS");
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(26);
        tvTitle.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvTitle.setLetterSpacing(0.08f);
        titleCol.addView(tvTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText("All 11 constructors on the 2026 grid");
        tvSub.setTextColor(0xFF999999);
        tvSub.setTextSize(13);
        tvSub.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        titleCol.addView(tvSub);

        header.addView(titleCol);
        root.addView(header);
    }

    private View buildTeamCard(Team team) {
        int teamColor;
        try { teamColor = Color.parseColor(team.color); } catch (Exception e) { teamColor = theme.accent; }
        final int finalTeamColor = teamColor;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{theme.cardBg, ThemeManager.blendColors(theme.cardBg, teamColor, 0.12f)});
        cardBg.setCornerRadius(dp(18));
        cardBg.setStroke(dp(1), ThemeManager.blendColors(finalTeamColor, 0xFF000000, 0.4f));
        card.setBackground(cardBg);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cp);

        // ── Summary row ───
        LinearLayout summary = new LinearLayout(this);
        summary.setOrientation(LinearLayout.HORIZONTAL);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        summary.setPadding(0, 0, dp(16), 0);

        // Left color bar
        View colorBar = new View(this);
        colorBar.setBackgroundColor(finalTeamColor);
        colorBar.setLayoutParams(new LinearLayout.LayoutParams(dp(5), LinearLayout.LayoutParams.MATCH_PARENT));
        summary.addView(colorBar);

        // Team logo
        ImageView logo = new ImageView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(72), dp(72));
        lp.setMargins(dp(12), dp(14), dp(12), dp(14));
        logo.setLayoutParams(lp);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setAdjustViewBounds(true);
        if (team.logoResId != 0) logo.setImageResource(team.logoResId);
        summary.addView(logo);

        // Team name + drivers
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setGravity(Gravity.CENTER_VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView tvName = new TextView(this);
        tvName.setText(team.name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(17);
        tvName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        info.addView(tvName);

        // Driver names
        List<Driver> drivers = Driver.getDriversByTeam(team.id);
        StringBuilder driverNames = new StringBuilder();
        for (int i = 0; i < drivers.size(); i++) {
            if (i > 0) driverNames.append("  •  ");
            driverNames.append(drivers.get(i).lastName);
        }
        TextView tvDrivers = new TextView(this);
        tvDrivers.setText(driverNames.toString());
        tvDrivers.setTextColor(0xFF888888);
        tvDrivers.setTextSize(12);
        tvDrivers.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        LinearLayout.LayoutParams dnp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dnp.setMargins(0, dp(3), 0, 0);
        tvDrivers.setLayoutParams(dnp);
        info.addView(tvDrivers);

        summary.addView(info);

        // Chevron
        TextView chevron = new TextView(this);
        chevron.setText("▼");
        chevron.setTextColor(0xFF444444);
        chevron.setTextSize(12);
        summary.addView(chevron);

        card.addView(summary);

        // ── Expandable detail ─────────────────────────────────────────────────
        LinearLayout detail = new LinearLayout(this);
        detail.setOrientation(LinearLayout.VERTICAL);
        detail.setVisibility(View.GONE);
        detail.setPadding(dp(16), dp(4), dp(16), dp(16));
        buildTeamDetail(detail, team, drivers, finalTeamColor);
        card.addView(detail);

        summary.setOnClickListener(v -> toggleExpand(detail, chevron, finalTeamColor));
        return card;
    }

    private void buildTeamDetail(LinearLayout detail, Team team, List<Driver> drivers, int teamColor) {
        // Divider
        View div = new View(this);
        LinearLayout.LayoutParams divP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        divP.setMargins(0, 0, 0, dp(14));
        div.setLayoutParams(divP);
        div.setBackgroundColor(ThemeManager.blendColors(teamColor, 0xFF000000, 0.55f));
        detail.addView(div);

        // Car image (2026 livery)
        int carRes = getCar2026Res(team.id);
        if (carRes != 0) {
            ImageView carImg = new ImageView(this);
            LinearLayout.LayoutParams cip = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(130));
            cip.setMargins(0, 0, 0, dp(14));
            carImg.setLayoutParams(cip);
            carImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            carImg.setAdjustViewBounds(true);
            carImg.setImageResource(carRes);
            detail.addView(carImg);
        }

        // Driver photos row
        if (!drivers.isEmpty()) {
            TextView driversLabel = new TextView(this);
            driversLabel.setText("DRIVERS");
            driversLabel.setTextColor(0xFF666666);
            driversLabel.setTextSize(10);
            driversLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            driversLabel.setLetterSpacing(0.12f);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dlp.setMargins(0, 0, 0, dp(8));
            driversLabel.setLayoutParams(dlp);
            detail.addView(driversLabel);

            LinearLayout driverRow = new LinearLayout(this);
            driverRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams drp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            drp.setMargins(0, 0, 0, dp(14));
            driverRow.setLayoutParams(drp);

            for (Driver d : drivers) {
                LinearLayout driverChip = new LinearLayout(this);
                driverChip.setOrientation(LinearLayout.HORIZONTAL);
                driverChip.setGravity(Gravity.CENTER_VERTICAL);
                GradientDrawable chipBg = new GradientDrawable();
                chipBg.setShape(GradientDrawable.RECTANGLE);
                chipBg.setCornerRadius(dp(10));
                chipBg.setColor(ThemeManager.blendColors(theme.cardBg, teamColor, 0.1f));
                chipBg.setStroke(dp(1), ThemeManager.blendColors(teamColor, 0xFF000000, 0.5f));
                driverChip.setBackground(chipBg);
                LinearLayout.LayoutParams dcp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                dcp.setMargins(0, 0, dp(8), 0);
                driverChip.setLayoutParams(dcp);
                driverChip.setPadding(dp(8), dp(8), dp(8), dp(8));

                // 9:16 Aspect Ratio Container
                FrameLayout photoFrame = new FrameLayout(this);
                photoFrame.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(78))); // ~9:16 ratio for small chips
                
                ImageView dPhoto = new ImageView(this);
                dPhoto.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                dPhoto.setScaleType(ImageView.ScaleType.MATRIX);
                
                final float currentDp = getResources().getDisplayMetrics().density;
                
                Glide.with(this)
                        .load(d.photoResId != 0 ? d.photoResId : d.getPhotoUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                dPhoto.post(() -> {
                                    Matrix matrix = new Matrix();
                                    float viewWidth = dPhoto.getWidth();
                                    float viewHeight = dPhoto.getHeight();
                                    float drawableWidth = resource.getIntrinsicWidth();
                                    float drawableHeight = resource.getIntrinsicHeight();

                                    float scale = viewWidth / drawableWidth;
                                    if (drawableHeight * scale < viewHeight) {
                                        scale = viewHeight / drawableHeight;
                                    }
                                    
                                    matrix.setScale(scale, scale);
                                    
                                    // Apply the same horizontal logic for specific drivers
                                    float dx = 0;
                                    if (d.id.equals("russell") || d.id.equals("albon") || d.id.equals("antonelli")) {
                                        dx = 8 * currentDp; // Proportional offset for smaller chip
                                    }
                                    
                                    matrix.postTranslate(dx, 0);
                                    dPhoto.setImageMatrix(matrix);
                                });
                                return false;
                            }
                        })
                        .into(dPhoto);

                photoFrame.addView(dPhoto);
                driverChip.addView(photoFrame);

                LinearLayout dInfo = new LinearLayout(this);
                dInfo.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams dip = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                dip.setMargins(dp(8), 0, 0, 0);
                dInfo.setLayoutParams(dip);

                TextView dName = new TextView(this);
                dName.setText(d.lastName);
                dName.setTextColor(0xFFFFFFFF);
                dName.setTextSize(13);
                dName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
                dInfo.addView(dName);

                TextView dNat = new TextView(this);
                dNat.setText(d.nationality);
                dNat.setTextColor(0xFF888888);
                dNat.setTextSize(11);
                dNat.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
                dInfo.addView(dNat);

                driverChip.addView(dInfo);
                driverRow.addView(driverChip);
            }
            detail.addView(driverRow);
        }

        // Team color swatch
        if (isEnthusiast) {
            addTeamCsvStats(detail, team, teamColor);
        }

        LinearLayout colorRow = new LinearLayout(this);
        colorRow.setOrientation(LinearLayout.HORIZONTAL);
        colorRow.setGravity(Gravity.CENTER_VERTICAL);

        View swatch = new View(this);
        GradientDrawable swatchBg = new GradientDrawable();
        swatchBg.setShape(GradientDrawable.OVAL);
        swatchBg.setColor(teamColor);
        swatch.setBackground(swatchBg);
        swatch.setLayoutParams(new LinearLayout.LayoutParams(dp(20), dp(20)));
        colorRow.addView(swatch);

        TextView tvColor = new TextView(this);
        tvColor.setText("  " + team.color.toUpperCase());
        tvColor.setTextColor(0xFF666666);
        tvColor.setTextSize(12);
        tvColor.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono), Typeface.NORMAL);
        colorRow.addView(tvColor);

        detail.addView(colorRow);
    }

    private void addTeamCsvStats(LinearLayout detail, Team team, int teamColor) {
        AssetDataLoader.TeamStats stats = AssetDataLoader.getTeamStats(this, team.id);
        if (stats == null) return;

        // Section header
        TextView header = new TextView(this);
        header.setText("CONSTRUCTOR STATS");
        header.setTextColor(0xFF555555);
        header.setTextSize(10);
        header.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        header.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hp.setMargins(0, dp(12), 0, dp(6));
        header.setLayoutParams(hp);
        detail.addView(header);

        // Row 1: Championships · Wins · Podiums
        LinearLayout r1 = chipRow(teamColor);
        addChip(r1, "TITLES",  String.valueOf(stats.championships), teamColor);
        addChip(r1, "WINS",    String.valueOf(stats.wins),          teamColor);
        addChip(r1, "PODIUMS", String.valueOf(stats.podiums),       teamColor);
        detail.addView(r1);

        // Row 2: Base · Years active
        LinearLayout r2 = chipRow(teamColor);
        addChip(r2, "BASE",         stats.base,          teamColor);
        addChip(r2, "YEARS ACTIVE", stats.yearsActive,   teamColor);
        detail.addView(r2);
    }

    private LinearLayout chipRow(int accentColor) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(0, 0, 0, dp(6));
        row.setLayoutParams(rp);
        return row;
    }

    private void addChip(LinearLayout parent, String label, String value, int accentColor) {
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.VERTICAL);
        android.graphics.drawable.GradientDrawable chipBg = new android.graphics.drawable.GradientDrawable();
        chipBg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        chipBg.setCornerRadius(dp(10));
        chipBg.setColor(ThemeManager.blendColors(theme.cardBg, accentColor, 0.08f));
        chipBg.setStroke(dp(1), ThemeManager.blendColors(accentColor, 0xFF000000, 0.5f));
        chip.setBackground(chipBg);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        cp.setMargins(0, 0, dp(6), 0);
        chip.setLayoutParams(cp);
        chip.setPadding(dp(10), dp(8), dp(10), dp(8));

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(0xFF666666);
        tvLabel.setTextSize(9);
        tvLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        tvLabel.setLetterSpacing(0.1f);
        chip.addView(tvLabel);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextColor(0xFFCCCCCC);
        tvValue.setTextSize(13);
        tvValue.setTypeface(ResourcesCompat.getFont(this, R.font.inter), android.graphics.Typeface.NORMAL);
        chip.addView(tvValue);

        parent.addView(chip);
    }

    private void addSectionLabel(LinearLayout root, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(theme.accent);
        tv.setTextSize(12);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tv.setLetterSpacing(0.15f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, dp(4), 0, dp(12));
        tv.setLayoutParams(p);
        root.addView(tv);
    }

    private void toggleExpand(LinearLayout detail, TextView chevron, int accentColor) {
        boolean expanding = detail.getVisibility() == View.GONE;
        if (expanding) {
            detail.setVisibility(View.VISIBLE);
            detail.measure(
                    View.MeasureSpec.makeMeasureSpec(detail.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int target = detail.getMeasuredHeight();
            detail.getLayoutParams().height = 0;
            detail.requestLayout();
            ValueAnimator anim = ValueAnimator.ofInt(0, target);
            anim.setDuration(240);
            anim.addUpdateListener(a -> { detail.getLayoutParams().height = (int) a.getAnimatedValue(); detail.requestLayout(); });
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                public void onAnimationEnd(android.animation.Animator a) {
                    detail.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }
            });
            anim.start();
            chevron.setText("▲");
            chevron.setTextColor(accentColor);
        } else {
            int initial = detail.getMeasuredHeight();
            ValueAnimator anim = ValueAnimator.ofInt(initial, 0);
            anim.setDuration(200);
            anim.addUpdateListener(a -> { detail.getLayoutParams().height = (int) a.getAnimatedValue(); detail.requestLayout(); });
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                public void onAnimationEnd(android.animation.Animator a) { detail.setVisibility(View.GONE); }
            });
            anim.start();
            chevron.setText("▼");
            chevron.setTextColor(0xFF444444);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }
}
