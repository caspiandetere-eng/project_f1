package com.example.project_f1;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.models.Driver;
import com.example.project_f1.models.JolpicaDriverResponse;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.Team;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Locale;

public class DriversActivity extends BaseActivity {

    private ThemeManager.TeamTheme theme;
    private static final int[] YEARS = {2025, 2024, 2023, 2022, 2021};
    private static final long TTL_LIVE = 60 * 60 * 1000L;
    private static final long TTL_HIST = 7 * 24 * 60 * 60 * 1000L;
    private boolean isEnthusiast, isInsider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = ThemeManager.applyFullTheme(this);
        isEnthusiast = KnowledgeLevelManager.isEnthusiast(this) || KnowledgeLevelManager.isInsider(this);
        isInsider    = KnowledgeLevelManager.isInsider(this);

        androidx.core.widget.NestedScrollView scroll = new androidx.core.widget.NestedScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), 0, dp(16), dp(32));
        scroll.addView(root);

        addHeader(root);
        buildDriverList(root);

        setContentView(scroll);
    }

    // ── Header ────────────────────────────────────────────────────────────────

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
        tvTitle.setText("2026 DRIVERS");
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(26);
        tvTitle.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvTitle.setLetterSpacing(0.08f);
        titleCol.addView(tvTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText("Tap a driver to load live stats");
        tvSub.setTextColor(0xFF999999);
        tvSub.setTextSize(13);
        tvSub.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        titleCol.addView(tvSub);

        header.addView(titleCol);
        root.addView(header);
    }

    // ── Driver list ───────────────────────────────────────────────────────────

    private void buildDriverList(LinearLayout root) {
        for (Team team : Team.getAllTeams()) {
            List<Driver> teamDrivers = Driver.getDriversByTeam(team.id);
            if (teamDrivers.isEmpty()) continue;
            addTeamSectionHeader(root, team);
            for (Driver driver : teamDrivers) addDriverCard(root, driver, team);
        }
    }

    private void addTeamSectionHeader(LinearLayout root, Team team) {
        int teamColor = safeColor(team.color);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(0, dp(20), 0, dp(8));
        row.setLayoutParams(rp);

        View dot = new View(this);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(teamColor);
        dot.setBackground(dotBg);
        LinearLayout.LayoutParams dotP = new LinearLayout.LayoutParams(dp(10), dp(10));
        dotP.setMargins(0, 0, dp(10), 0);
        dot.setLayoutParams(dotP);
        row.addView(dot);

        TextView tv = new TextView(this);
        tv.setText(team.name.toUpperCase());
        tv.setTextColor(teamColor);
        tv.setTextSize(12);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tv.setLetterSpacing(0.15f);
        row.addView(tv);
        root.addView(row);
    }

    private void addDriverCard(LinearLayout root, Driver driver, Team team) {
        int teamColor = safeColor(team.color);
        int teamDark  = ThemeManager.blendColors(teamColor, 0xFF000000, 0.72f);

        // ── Outer wrapper: MaterialCardView ──────────────────────────────────
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cp);
        card.setRadius(dp(20));
        card.setCardElevation(dp(10));
        card.setCardBackgroundColor(0xFF000000);
        card.setStrokeColor(ThemeManager.blendColors(teamColor, 0xFF000000, 0.55f));
        card.setStrokeWidth(dp(1));
        card.setClipToOutline(true);

        // ── Inner vertical container (summary + detail) ───────────────────────
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        card.addView(inner);

        // ── SUMMARY: ConstraintLayout with layered design ─────────────────────
        androidx.constraintlayout.widget.ConstraintLayout summary =
                new androidx.constraintlayout.widget.ConstraintLayout(this);
        summary.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(130)));
        summary.setClipChildren(false);
        summary.setClipToPadding(false);

        // LAYER 1+2+3 — single custom drawable: base gradient + dot mesh + shimmer
        View bgView = new View(this);
        bgView.setBackground(new DriverCardBgDrawable(teamColor, teamDark));
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams bgLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        bgLp.startToStart   = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        bgLp.endToEnd       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        bgLp.topToTop       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        bgLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        summary.addView(bgView, bgLp);

        // LAYER 2 — large faded driver number (behind everything)
        String driverNumber = driver.number > 0 ? String.valueOf(driver.number) : "";
        TextView tvNumber = new TextView(this);
        tvNumber.setId(View.generateViewId());
        tvNumber.setText(driverNumber);
        tvNumber.setTextColor(0xFFFFFFFF);
        tvNumber.setAlpha(0.12f);
        tvNumber.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 110);
        tvNumber.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvNumber.setIncludeFontPadding(false);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams numLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        numLp.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        numLp.topToTop     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        numLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        numLp.setMargins(dp(8), 0, 0, 0);
        summary.addView(tvNumber, numLp);

        // LAYER 3 — driver name + team name (top-left)
        TextView tvName = new TextView(this);
        tvName.setId(View.generateViewId());
        tvName.setText(driver.lastName.toUpperCase());
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 20);
        tvName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvName.setLetterSpacing(0.04f);
        tvName.setMaxLines(1);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams nameLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        0,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        nameLp.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        nameLp.topToTop     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        nameLp.endToStart   = tvNumber.getId(); // don't overlap number area too much
        nameLp.setMargins(dp(16), dp(22), dp(8), 0);
        summary.addView(tvName, nameLp);

        TextView tvFirstName = new TextView(this);
        tvFirstName.setId(View.generateViewId());
        tvFirstName.setText(driver.firstName);
        tvFirstName.setTextColor(0xCCFFFFFF);
        tvFirstName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
        tvFirstName.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        tvFirstName.setLetterSpacing(0.02f);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams fnLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        0,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        fnLp.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        fnLp.topToBottom  = tvName.getId();
        fnLp.endToStart   = tvNumber.getId();
        fnLp.setMargins(dp(16), dp(2), dp(8), 0);
        summary.addView(tvFirstName, fnLp);

        TextView tvTeam = new TextView(this);
        tvTeam.setId(View.generateViewId());
        tvTeam.setText(team.name.toUpperCase());
        tvTeam.setTextColor(teamColor);
        tvTeam.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
        tvTeam.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvTeam.setLetterSpacing(0.12f);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams teamLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        0,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        teamLp.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        teamLp.topToBottom  = tvFirstName.getId();
        teamLp.endToStart   = tvNumber.getId();
        teamLp.setMargins(dp(16), dp(5), dp(8), 0);
        summary.addView(tvTeam, teamLp);

        // LAYER 4 — nationality badge (bottom-left)
        TextView tvFlag = new TextView(this);
        tvFlag.setId(View.generateViewId());
        tvFlag.setText(nationalityCode(driver.nationality));
        tvFlag.setTextColor(0xFFFFFFFF);
        tvFlag.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 9);
        tvFlag.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvFlag.setLetterSpacing(0.08f);
        tvFlag.setGravity(Gravity.CENTER);
        tvFlag.setPadding(dp(6), dp(3), dp(6), dp(3));
        GradientDrawable flagBg = new GradientDrawable();
        flagBg.setShape(GradientDrawable.RECTANGLE);
        flagBg.setCornerRadius(dp(6));
        flagBg.setColor(ThemeManager.blendColors(teamColor, 0xFF000000, 0.6f));
        flagBg.setStroke(dp(1), ThemeManager.blendColors(teamColor, 0xFF000000, 0.3f));
        tvFlag.setBackground(flagBg);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams flagLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        flagLp.startToStart  = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        flagLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        flagLp.setMargins(dp(16), 0, 0, dp(14));
        summary.addView(tvFlag, flagLp);

        // LAYER 4b — accent left stripe
        View stripe = new View(this);
        stripe.setBackgroundColor(teamColor);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams stripeLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(dp(3), 0);
        stripeLp.startToStart  = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        stripeLp.topToTop      = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        stripeLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        summary.addView(stripe, stripeLp);

        // LAYER 5 — glow behind driver image
        View glowView = new View(this);
        glowView.setId(View.generateViewId());
        int glowColor = (teamColor & 0x00FFFFFF) | 0x30000000;
        GradientDrawable glowBg = new GradientDrawable();
        glowBg.setShape(GradientDrawable.OVAL);
        glowBg.setColor(glowColor);
        glowView.setBackground(glowBg);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams glowLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(dp(160), dp(160));
        glowLp.endToEnd     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        glowLp.topToTop     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        glowLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        glowLp.setMargins(0, 0, dp(-20), 0);
        summary.addView(glowView, glowLp);

        // LAYER 5 — driver image (overflows right edge)
        ImageView driverImg = new ImageView(this);
        driverImg.setId(View.generateViewId());
        driverImg.setScaleType(ImageView.ScaleType.FIT_END);
        driverImg.setAdjustViewBounds(true);
        if (driver.photoResId != 0) driverImg.setImageResource(driver.photoResId);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams imgLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(dp(170), dp(150));
        imgLp.endToEnd      = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        imgLp.topToTop      = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        imgLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        imgLp.setMargins(0, 0, dp(-8), 0);
        summary.addView(driverImg, imgLp);

        // LAYER 6 — chevron (bottom-right)
        TextView chevron = new TextView(this);
        chevron.setText("▼");
        chevron.setTextColor(ThemeManager.blendColors(teamColor, 0xFF000000, 0.3f));
        chevron.setTextSize(10);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams chevLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        chevLp.endToEnd     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        chevLp.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        chevLp.setMargins(0, 0, dp(12), dp(10));
        summary.addView(chevron, chevLp);

        inner.addView(summary);

        // ── Detail section (lazy-loaded, unchanged) ───────────────────────────
        LinearLayout detail = new LinearLayout(this);
        detail.setOrientation(LinearLayout.VERTICAL);
        detail.setVisibility(View.GONE);
        detail.setPadding(dp(16), dp(4), dp(16), dp(16));
        inner.addView(detail);

        boolean[] fetched = {false};
        summary.setOnClickListener(v -> {
            boolean expanding = detail.getVisibility() == View.GONE;
            if (expanding && !fetched[0]) {
                fetched[0] = true;
                fetchAndPopulateDetail(detail, driver, team, teamColor);
            }
            toggleExpand(detail, chevron, teamColor);
        });

        // ── Entry animation ───────────────────────────────────────────────────
        card.setAlpha(0f);
        card.setTranslationY(dp(16));
        card.setScaleX(0.97f);
        card.setScaleY(0.97f);
        card.animate()
                .alpha(1f).translationY(0).scaleX(1f).scaleY(1f)
                .setDuration(260)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        root.addView(card);
    }

private static String nationalityCode(String nationality) {
        if (nationality == null) return "";
        switch (nationality) {
            case "British":     return "GBR";
            case "Dutch":       return "NED";
            case "Monegasque":  return "MON";
            case "Spanish":     return "ESP";
            case "Australian":  return "AUS";
            case "Finnish":     return "FIN";
            case "German":      return "GER";
            case "French":      return "FRA";
            case "Brazilian":   return "BRA";
            case "Mexican":     return "MEX";
            case "Canadian":    return "CAN";
            case "Thai":        return "THA";
            case "Italian":     return "ITA";
            case "Argentine":   return "ARG";
            case "New Zealander": return "NZL";
            case "Swedish":     return "SWE";
            default:            return nationality.substring(0, Math.min(3, nationality.length())).toUpperCase();
        }
    }

    //Cache-first fetch: bio and 5 years of standings

    private void fetchAndPopulateDetail(LinearLayout detail, Driver driver, Team team, int teamColor) {
        ProgressBar spinner = new ProgressBar(this);
        spinner.setIndeterminateTintList(ColorStateList.valueOf(teamColor));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(dp(32), dp(32));
        sp.gravity = Gravity.CENTER_HORIZONTAL;
        sp.setMargins(0, dp(12), 0, dp(12));
        spinner.setLayoutParams(sp);
        detail.addView(spinner);

        Gson gson = new Gson();
        final JolpicaDriverResponse.DriverInfo[] driverInfo = {null};
        @SuppressWarnings("unchecked")
        final JolpicaStandingsResponse.DriverStanding[] standings =
                new JolpicaStandingsResponse.DriverStanding[YEARS.length];
        final int[] pending = {1 + YEARS.length};

        Runnable onAllDone = () -> runOnUiThread(() -> {
            detail.removeView(spinner);
            buildDetailContent(detail, driver, team, teamColor, driverInfo[0], standings);
        });

        // 1. Driver bio — cache-first
        String bioKey = DataSyncManager.driverInfoKey(driver.apiId);
        String cachedBio = CacheManager.getCache(this, bioKey);
        if (cachedBio != null) {
            try {
                JolpicaDriverResponse r = gson.fromJson(cachedBio, JolpicaDriverResponse.class);
                if (r.mrData != null && r.mrData.driverTable != null
                        && !r.mrData.driverTable.drivers.isEmpty())
                    driverInfo[0] = r.mrData.driverTable.drivers.get(0);
            } catch (Exception ignored) {}
            if (--pending[0] == 0) onAllDone.run();
        } else {
            JolpicaApiClient.getApiService().getDriverInfo(driver.apiId)
                    .enqueue(new Callback<JolpicaDriverResponse>() {
                        @Override
                        public void onResponse(Call<JolpicaDriverResponse> call,
                                               Response<JolpicaDriverResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                CacheManager.saveCache(DriversActivity.this, bioKey,
                                        gson.toJson(response.body()), TTL_HIST);
                                try { driverInfo[0] = response.body().mrData.driverTable.drivers.get(0); }
                                catch (Exception ignored) {}
                            }
                            if (--pending[0] == 0) onAllDone.run(); // bioKey already uses apiId
                        }
                        @Override
                        public void onFailure(Call<JolpicaDriverResponse> call, Throwable t) {
                            if (--pending[0] == 0) onAllDone.run();
                        }
                    });
        }

        // 2. Standings for each of the past 5 years — cache-first
        for (int i = 0; i < YEARS.length; i++) {
            final int idx = i;
            final int year = YEARS[i];
            String standKey = DataSyncManager.driverStandingsKey(year, driver.apiId);
            String cachedStand = CacheManager.getCache(this, standKey);
            if (cachedStand != null) {
                try { standings[idx] = extractStanding(gson.fromJson(cachedStand, JolpicaStandingsResponse.class)); }
                catch (Exception ignored) {}
                if (--pending[0] == 0) onAllDone.run();
            } else {
                long ttl = (year == 2025) ? TTL_LIVE : TTL_HIST;
                JolpicaApiClient.getApiService()
                        .getDriverSeasonStandings(year, driver.apiId)
                        .enqueue(new Callback<JolpicaStandingsResponse>() {
                            @Override
                            public void onResponse(Call<JolpicaStandingsResponse> call,
                                                   Response<JolpicaStandingsResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    CacheManager.saveCache(DriversActivity.this, standKey,
                                            gson.toJson(response.body()), ttl);
                                    standings[idx] = extractStanding(response.body());
                                }
                                if (--pending[0] == 0) onAllDone.run();
                            }
                            @Override
                            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {
                                if (--pending[0] == 0) onAllDone.run();
                            }
                        });
            }
        }
    }

    private static JolpicaStandingsResponse.DriverStanding extractStanding(JolpicaStandingsResponse r) {
        try {
            List<JolpicaStandingsResponse.DriverStanding> ds =
                    r.mrData.standingsTable.standingsLists.get(0).driverStandings;
            return (ds != null && !ds.isEmpty()) ? ds.get(0) : null;
        } catch (Exception e) { return null; }
    }

    // Detail content

    private void buildDetailContent(LinearLayout detail, Driver driver, Team team,
                                    int teamColor, JolpicaDriverResponse.DriverInfo info,
                                    JolpicaStandingsResponse.DriverStanding[] standings) {
        // Divider
        View div = new View(this);
        LinearLayout.LayoutParams divP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        divP.setMargins(0, 0, 0, dp(14));
        div.setLayoutParams(divP);
        div.setBackgroundColor(ThemeManager.blendColors(teamColor, 0xFF000000, 0.6f));
        detail.addView(div);

        // Car image
        int carRes = getCarRes(team.id);
        if (carRes != 0) {
            ImageView carImg = new ImageView(this);
            LinearLayout.LayoutParams cip = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(120));
            cip.setMargins(0, 0, 0, dp(14));
            carImg.setLayoutParams(cip);
            carImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            carImg.setAdjustViewBounds(true);
            carImg.setImageResource(carRes);
            detail.addView(carImg);
        }

        // Row 1: Number · Code · DOB  (from bio API)
        if (info != null) {
            LinearLayout row1 = chipRow();
            if (info.permanentNumber != null) addChip(row1, "NUMBER", "#" + info.permanentNumber, teamColor);
            if (info.code != null)            addChip(row1, "CODE", info.code, teamColor);
            if (info.dateOfBirth != null)     addChip(row1, "DATE OF BIRTH", formatDob(info.dateOfBirth), teamColor);
            if (row1.getChildCount() > 0) detail.addView(row1);
        } else {
            // Fallback: show number from local data when API unavailable
            LinearLayout row1 = chipRow();
            addChip(row1, "NUMBER", "#" + driver.number, teamColor);
            addChip(row1, "NATIONALITY", driver.nationality, teamColor);
            detail.addView(row1);
        }

        // Row 2: Nationality · Team
        LinearLayout row2 = chipRow();
        addChip(row2, "NATIONALITY", driver.nationality, teamColor);
        addChip(row2, "TEAM", team.name, teamColor);
        detail.addView(row2);

        // Season history header
        TextView histHeader = new TextView(this);
        histHeader.setText("SEASON HISTORY");
        histHeader.setTextColor(0xFF555555);
        histHeader.setTextSize(10);
        histHeader.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        histHeader.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams hhP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hhP.setMargins(0, dp(10), 0, dp(6));
        histHeader.setLayoutParams(hhP);
        detail.addView(histHeader);

        // One row per year — skip years with no data for rookies
        boolean anyStandingFound = false;
        for (int i = 0; i < YEARS.length; i++) {
            int year = YEARS[i];
            JolpicaStandingsResponse.DriverStanding s = standings[i];
            LinearLayout yearRow = chipRow();
            if (s != null) {
                anyStandingFound = true;
                addChip(yearRow, String.valueOf(year), "P" + s.position, teamColor);
                addChip(yearRow, "POINTS", s.points, teamColor);
                addChip(yearRow, "WINS", s.wins, teamColor);
                detail.addView(yearRow);
            }
        }
        if (!anyStandingFound) {
            LinearLayout noDataRow = chipRow();
            addChip(noDataRow, "HISTORY", "Rookie — no prior seasons", 0xFF444444);
            detail.addView(noDataRow);
        }

        // ALL USERS : points/wins history + top speed from telemetry
        addBasicTelemetry(detail, driver, teamColor);

        //ENTHUSIAST+ : career stats & records from CSV
        if (isEnthusiast) {
            addCsvDriverData(detail, driver, teamColor);
        }

        //INSIDER only : full telemetry analysis from JSON
        if (isInsider) {
            addTelemetryData(detail, driver, teamColor);
        }
    }

    // Basic telemetry: points/wins history + top speed (ALL users)

    private void addBasicTelemetry(LinearLayout detail, Driver driver, int teamColor) {
        AssetDataLoader.DriverTelemetry t = AssetDataLoader.getDriverTelemetry(this, driver.id);
        if (t == null || t.sampleCount == 0) return;

        // Points & wins from season history are already shown above;
        // here we show the top speed record from telemetry data.
        addSectionHeader(detail, "TOP SPEED RECORD");
        LinearLayout r1 = chipRow();
        addChip(r1, "TOP SPEED", String.format(Locale.US, "%.0f km/h", t.maxSpeed), teamColor);
        addChip(r1, "YEAR", t.maxSpeedYear > 0 ? String.valueOf(t.maxSpeedYear) : "—", teamColor);
        detail.addView(r1);
        if (t.maxSpeedRace != null) {
            LinearLayout r2 = chipRow();
            addChip(r2, "RACE", t.maxSpeedRace, teamColor);
            addChip(r2, "CAR", t.maxSpeedCar != null ? t.maxSpeedCar : "—", teamColor);
            detail.addView(r2);
        }
    }

    // CSV career stats + records (ENTHUSIAST+)

    private void addCsvDriverData(LinearLayout detail, Driver driver, int teamColor) {
        AssetDataLoader.DriverCareerStats stats = AssetDataLoader.getDriverStats(this, driver.id);
        if (stats != null) {
            addSectionHeader(detail, "CAREER STATS");
            LinearLayout r1 = chipRow();
            addChip(r1, "CHAMPIONSHIPS", String.valueOf(stats.championships), teamColor);
            addChip(r1, "WINS",          String.valueOf(stats.wins),          teamColor);
            addChip(r1, "PODIUMS",       String.valueOf(stats.podiums),       teamColor);
            detail.addView(r1);
            LinearLayout r2 = chipRow();
            addChip(r2, "POLES",      String.valueOf(stats.poles),        teamColor);
            addChip(r2, "DEBUT",      String.valueOf(stats.debutYear),    teamColor);
            addChip(r2, "CAREER PTS", String.valueOf(stats.careerPoints), teamColor);
            detail.addView(r2);
        }
        java.util.List<AssetDataLoader.DriverRecord> records =
                AssetDataLoader.getDriverRecords(this, driver.id);
        if (!records.isEmpty()) {
            addSectionHeader(detail, "NOTABLE RECORDS");
            for (AssetDataLoader.DriverRecord rec : records) {
                LinearLayout row = chipRow();
                addChip(row, rec.recordType.toUpperCase(), rec.recordTitle, teamColor);
                String yearStr = rec.year.equals("-") ? "" : "  " + rec.year;
                addChip(row, "VALUE", rec.value + yearStr, teamColor);
                detail.addView(row);
            }
        }
    }

    //  Telemetry analysis (INSIDER only)

    private void addTelemetryData(LinearLayout detail, Driver driver, int teamColor) {
        AssetDataLoader.DriverTelemetry t = AssetDataLoader.getDriverTelemetry(this, driver.id);
        if (t == null || t.sampleCount == 0) return;
        addSectionHeader(detail, "TELEMETRY  (" + t.sampleCount + " samples, 2021-2023)");
        LinearLayout r1 = chipRow();
        addChip(r1, "AVG SPEED", String.format(Locale.US, "%.1f km/h", t.avgSpeed), teamColor);
        addChip(r1, "MAX SPEED", String.format(Locale.US, "%.0f km/h", t.maxSpeed), teamColor);
        detail.addView(r1);
        LinearLayout r2 = chipRow();
        addChip(r2, "AVG THROTTLE", String.format(Locale.US, "%.1f%%", t.avgThrottle),       teamColor);
        addChip(r2, "AVG BRAKE",    String.format(Locale.US, "%.1f%%", t.avgBrake * 100.0),  teamColor);
        detail.addView(r2);
    }

    private void addSectionHeader(LinearLayout parent, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF555555);
        tv.setTextSize(10);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tv.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, dp(12), 0, dp(6));
        tv.setLayoutParams(p);
        parent.addView(tv);
    }

    // Chip helpers

    private LinearLayout chipRow() {
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
        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setShape(GradientDrawable.RECTANGLE);
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
        tvLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvLabel.setLetterSpacing(0.1f);
        chip.addView(tvLabel);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextColor(0xFFCCCCCC);
        tvValue.setTextSize(13);
        tvValue.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        chip.addView(tvValue);

        parent.addView(chip);
    }

    // Expand / collapse

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
            anim.setDuration(220);
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

    //  Utilities

    private static int getCarRes(String teamId) {
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

    /** "1999-01-07" -> "7 Jan 1999" */
    private static String formatDob(String dob) {
        try {
            String[] parts = dob.split("-");
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            int m = Integer.parseInt(parts[1]) - 1;
            return Integer.parseInt(parts[2]) + " " + months[m] + " " + parts[0];
        } catch (Exception e) { return dob; }
    }

    private int safeColor(String hex) {
        try { return Color.parseColor(hex); } catch (Exception e) { return theme.accent; }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    // ── Card background: gradient + dot mesh + shimmer ────────────────────────

    private static final class DriverCardBgDrawable extends Drawable {

        private final int colorStart;  // team color, slightly darkened
        private final int colorEnd;    // very dark team color
        private final Paint basePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint dotPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint shimPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint glowPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

        // dot grid config
        private static final int   DOT_SPACING_PX = 14;  // pixels between dot centres
        private static final float DOT_RADIUS_PX  = 1.4f;

        // cached dot-mesh bitmap (rebuilt only when bounds change)
        private Bitmap dotBitmap;
        private int    cachedW, cachedH;

        DriverCardBgDrawable(int teamColor, int teamDark) {
            // start: team color blended 45% toward black  (left side)
            this.colorStart = blendColor(teamColor, 0xFF000000, 0.45f);
            // end:   team color blended 72% toward black  (right side)
            this.colorEnd   = teamDark;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Rect b = getBounds();
            int w = b.width(), h = b.height();
            if (w == 0 || h == 0) return;

            // ── LAYER 1: base gradient LEFT → RIGHT ──────────────────────────
            basePaint.setShader(new LinearGradient(
                    0, 0, w, 0,
                    colorStart, colorEnd,
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, w, h, basePaint);

            // ── LAYER 2: dark depth shadow on left third ──────────────────────
            shimPaint.setShader(new LinearGradient(
                    0, 0, w * 0.45f, 0,
                    0x66000000, 0x00000000,
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, w, h, shimPaint);

            // ── LAYER 3: dot mesh — right 60%, faded in from centre ───────────
            canvas.drawBitmap(getDotBitmap(w, h), 0, 0, null);

            // ── LAYER 4: radial glow at right-centre (behind driver image) ────
            float gx = w * 0.82f, gy = h * 0.5f, gr = h * 0.75f;
            int accentAlpha = (colorStart & 0x00FFFFFF) | 0x28000000; // ~16% alpha
            glowPaint.setShader(new RadialGradient(
                    gx, gy, gr,
                    accentAlpha, 0x00000000,
                    Shader.TileMode.CLAMP));
            canvas.drawCircle(gx, gy, gr, glowPaint);

            // ── LAYER 5: subtle right-edge light shimmer ──────────────────────
            int shimmerColor = blendColor(colorStart, 0xFFFFFFFF, 0.06f) | 0xFF000000;
            shimPaint.setShader(new LinearGradient(
                    w * 0.55f, 0, w, 0,
                    0x00000000,
                    (shimmerColor & 0x00FFFFFF) | 0x18000000,
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, w, h, shimPaint);
        }

        /** Builds (and caches) the dot-mesh bitmap for the current card size. */
        private Bitmap getDotBitmap(int w, int h) {
            if (dotBitmap != null && cachedW == w && cachedH == h) return dotBitmap;
            cachedW = w; cachedH = h;

            dotBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(dotBitmap);

            // dot colour: light tint of the end (right) colour
            int dotBase = blendColor(colorEnd, 0xFFFFFFFF, 0.55f);

            // mesh starts at 40% from left, fully visible at 60%+
            float meshStartX = w * 0.40f;
            float meshFullX  = w * 0.62f;

            for (int row = 0; row * DOT_SPACING_PX < h + DOT_SPACING_PX; row++) {
                for (int col = 0; col * DOT_SPACING_PX < w + DOT_SPACING_PX; col++) {
                    float cx = col * DOT_SPACING_PX;
                    float cy = row * DOT_SPACING_PX;
                    if (cx < meshStartX) continue;

                    // fade alpha: 0 at meshStartX → max at meshFullX
                    float fade = Math.min(1f, (cx - meshStartX) / (meshFullX - meshStartX));
                    // also fade out near right edge so dots don't clash with image
                    float rightFade = 1f - Math.max(0f, (cx - w * 0.78f) / (w * 0.22f));
                    float alpha = fade * rightFade * 0.18f; // max 18% opacity

                    dotPaint.setColor(dotBase);
                    dotPaint.setAlpha((int)(alpha * 255));
                    c.drawCircle(cx, cy, DOT_RADIUS_PX, dotPaint);
                }
            }
            return dotBitmap;
        }

        @Override public void setAlpha(int alpha) {}
        @Override public void setColorFilter(@Nullable android.graphics.ColorFilter cf) {}
        @Override public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }

        private static int blendColor(int c1, int c2, float r) {
            float i = 1f - r;
            return Color.argb(
                    (int)(Color.alpha(c1)*i + Color.alpha(c2)*r),
                    (int)(Color.red(c1)*i   + Color.red(c2)*r),
                    (int)(Color.green(c1)*i + Color.green(c2)*r),
                    (int)(Color.blue(c1)*i  + Color.blue(c2)*r));
        }
    }
}
