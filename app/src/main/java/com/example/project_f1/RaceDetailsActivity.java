package com.example.project_f1;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.card.MaterialCardView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RaceDetailsActivity extends BaseActivity {

    private LinearLayout detailsContainer;
    private ThemeManager.TeamTheme currentTheme;
    private static final DateTimeFormatter IN_FMT  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_details);

        currentTheme = ThemeManager.applyFullTheme(this);
        detailsContainer = findViewById(R.id.detailsContainer);

        View stripe = findViewById(R.id.topStripe);
        if (stripe != null) stripe.setBackgroundColor(currentTheme.accent);

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        setupSwipeRefresh(swipeRefresh, currentTheme.accent, () -> {
            detailsContainer.removeAllViews();
            buildContent();
        });

        com.google.android.material.button.MaterialButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(currentTheme.buttonBg));
            btnBack.setOnClickListener(v -> finish());
        }

        buildContent();
    }

    private void buildContent() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        String raceName    = extras.getString("raceName", "Race");
        String raceDate    = extras.getString("raceDate", "");
        String location    = extras.getString("location", "");
        String circuitName = extras.getString("circuitName", "");
        String locality    = extras.getString("locality", "");
        String country     = extras.getString("country", "");

        displayRaceDetails(raceName, raceDate, location, circuitName, locality, country);
    }

    private void displayRaceDetails(String raceName, String raceDate,
                                    String location, String circuitName,
                                    String locality, String country) {
        if (detailsContainer == null) return;
        detailsContainer.removeAllViews();

        // ── Race name ─────────────────────────────────────────────────────
        TextView tvRaceName = new TextView(this);
        tvRaceName.setText(raceName);
        tvRaceName.setTextColor(0xFFFFFFFF);
        tvRaceName.setTextSize(24);
        tvRaceName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web),
                android.graphics.Typeface.BOLD);
        tvRaceName.setPadding(0, 0, 0, dp(4));
        detailsContainer.addView(tvRaceName);

        // ── Location & circuit name ───────────────────────────────────────
        if (!location.isEmpty()) {
            TextView tvLocation = new TextView(this);
            tvLocation.setText("📍 " + location);
            tvLocation.setTextColor(0xFF999999);
            tvLocation.setTextSize(15);
            tvLocation.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
            tvLocation.setPadding(0, dp(4), 0, dp(2));
            detailsContainer.addView(tvLocation);
        }

        if (!circuitName.isEmpty()) {
            TextView tvCircuit = new TextView(this);
            tvCircuit.setText("🏁 " + circuitName);
            tvCircuit.setTextColor(0xFF999999);
            tvCircuit.setTextSize(15);
            tvCircuit.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
            tvCircuit.setPadding(0, dp(2), 0, dp(16));
            detailsContainer.addView(tvCircuit);
        }

        // ── Date card ─────────────────────────────────────────────────────
        if (!raceDate.isEmpty()) {
            MaterialCardView dateCard = new MaterialCardView(this);
            dateCard.setCardBackgroundColor(
                    ThemeManager.blendColors(0xFF0D0D0D, currentTheme.accent, 0.07f));
            dateCard.setRadius(dp(12));
            dateCard.setStrokeColor(ThemeManager.blendColors(0xFF222222, currentTheme.accent, 0.28f));
            dateCard.setStrokeWidth(dp(1));
            LinearLayout.LayoutParams dateCardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dateCardParams.setMargins(0, 0, 0, dp(20));
            dateCard.setLayoutParams(dateCardParams);

            TextView tvDate = new TextView(this);
            try {
                tvDate.setText("📅  " + LocalDate.parse(raceDate, IN_FMT).format(OUT_FMT));
            } catch (Exception e) {
                tvDate.setText("📅  " + raceDate);
            }
            tvDate.setTextColor(currentTheme.accent);
            tvDate.setTextSize(17);
            tvDate.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono),
                    android.graphics.Typeface.BOLD);
            tvDate.setPadding(dp(16), dp(14), dp(16), dp(14));
            dateCard.addView(tvDate);
            detailsContainer.addView(dateCard);
        }

        // ── Circuit map card ──────────────────────────────────────────────
        Integer circuitRes = CircuitAssets.getCircuitDrawable(locality);
        if (circuitRes == null) circuitRes = CircuitAssets.getCircuitDrawable(country);
        if (circuitRes == null) circuitRes = CircuitAssets.getCircuitDrawable(circuitName);

        if (circuitRes != null) {
            // Section label
            TextView tvMapLabel = new TextView(this);
            tvMapLabel.setText("CIRCUIT MAP");
            tvMapLabel.setTextColor(currentTheme.accent);
            tvMapLabel.setTextSize(11);
            tvMapLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed),
                    android.graphics.Typeface.BOLD);
            tvMapLabel.setLetterSpacing(0.12f);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            labelParams.setMargins(0, 0, 0, dp(8));
            tvMapLabel.setLayoutParams(labelParams);
            detailsContainer.addView(tvMapLabel);

            // Map card
            MaterialCardView mapCard = new MaterialCardView(this);
            mapCard.setCardBackgroundColor(
                    ThemeManager.blendColors(0xFF0D0D0D, currentTheme.accent, 0.07f));
            mapCard.setRadius(dp(20));
            mapCard.setCardElevation(dp(6));
            mapCard.setStrokeColor(ThemeManager.blendColors(0xFF222222, currentTheme.accent, 0.35f));
            mapCard.setStrokeWidth(dp(1));
            LinearLayout.LayoutParams mapCardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mapCardParams.setMargins(0, 0, 0, dp(24));
            mapCard.setLayoutParams(mapCardParams);

            LinearLayout mapInner = new LinearLayout(this);
            mapInner.setOrientation(LinearLayout.VERTICAL);
            mapInner.setPadding(dp(24), dp(28), dp(24), dp(28));
            mapCard.addView(mapInner);

            // FrameLayout stacks three ImageViews for a lean sketch-outline look:
            //   1. Glow layer  — accent at 18% alpha, scaled up 1.04x (soft halo behind the line)
            //   2. Outline layer — accent at 70% alpha (the lean circuit line)
            //   3. Centre line  — white at 35% alpha (thin inner detail)
            android.widget.FrameLayout mapFrame = new android.widget.FrameLayout(this);
            LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(320));
            mapFrame.setLayoutParams(frameParams);

            // Layer 1 — soft glow behind the line
            ImageView ivGlow = new ImageView(this);
            android.widget.FrameLayout.LayoutParams glowParams =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
            ivGlow.setLayoutParams(glowParams);
            ivGlow.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivGlow.setAdjustViewBounds(true);
            ivGlow.setImageResource(circuitRes);
            ivGlow.setColorFilter(currentTheme.accent, android.graphics.PorterDuff.Mode.SRC_ATOP);
            ivGlow.setAlpha(0.18f);
            ivGlow.setScaleX(1.06f);
            ivGlow.setScaleY(1.06f);
            mapFrame.addView(ivGlow);

            // Layer 2 — lean accent outline
            ImageView ivAccent = new ImageView(this);
            android.widget.FrameLayout.LayoutParams ivFull =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
            ivAccent.setLayoutParams(ivFull);
            ivAccent.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivAccent.setAdjustViewBounds(true);
            ivAccent.setImageResource(circuitRes);
            ivAccent.setColorFilter(currentTheme.accent, android.graphics.PorterDuff.Mode.SRC_ATOP);
            ivAccent.setAlpha(0.72f);
            mapFrame.addView(ivAccent);

            // Layer 3 — thin white centre line detail
            ImageView ivWhite = new ImageView(this);
            ivWhite.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
            ivWhite.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivWhite.setAdjustViewBounds(true);
            ivWhite.setImageResource(circuitRes);
            ivWhite.setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.SRC_ATOP);
            ivWhite.setAlpha(0.30f);
            mapFrame.addView(ivWhite);

            mapInner.addView(mapFrame);

            // Stats row below the map
            addCircuitStatsRow(mapInner, locality, country, circuitName);

            detailsContainer.addView(mapCard);
        }
    }

    private void addCircuitStatsRow(LinearLayout parent, String locality, String country, String circuitName) {
        // Look up static circuit facts
        CircuitInfo info = get(locality);
        if (info == null) info = get(country);
        if (info == null) info = get(circuitName);

        LinearLayout divider = new LinearLayout(this);
        divider.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        divParams.setMargins(0, dp(20), 0, dp(20));
        divider.setLayoutParams(divParams);
        divider.setBackgroundColor(ThemeManager.blendColors(0xFF1A1A1A, currentTheme.accent, 0.2f));
        parent.addView(divider);

        // Circuit name centred
        if (!circuitName.isEmpty()) {
            TextView tvName = new TextView(this);
            tvName.setText(circuitName.toUpperCase());
            tvName.setTextColor(0xFFFFFFFF);
            tvName.setTextSize(13);
            tvName.setGravity(android.view.Gravity.CENTER);
            tvName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web),
                    android.graphics.Typeface.BOLD);
            tvName.setLetterSpacing(0.06f);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            nameParams.setMargins(0, 0, 0, dp(16));
            tvName.setLayoutParams(nameParams);
            parent.addView(tvName);
        }

        if (info == null) return;

        // Stats grid — 3 columns
        LinearLayout row1 = makeStatsRow();
        addStat(row1, "LENGTH",   info.length);
        addStat(row1, "LAPS",     info.laps);
        addStat(row1, "LAP RECORD", info.lapRecord);
        parent.addView(row1);

        LinearLayout row2 = makeStatsRow();
        LinearLayout.LayoutParams r2p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        r2p.setMargins(0, dp(12), 0, 0);
        row2.setLayoutParams(r2p);
        addStat(row2, "CORNERS",  info.corners);
        addStat(row2, "DRS ZONES", info.drsZones);
        addStat(row2, "DIRECTION", info.direction);
        parent.addView(row2);
    }

    private LinearLayout makeStatsRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return row;
    }

    private void addStat(LinearLayout row, String label, String value) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(android.view.Gravity.CENTER);
        android.graphics.drawable.GradientDrawable cellBg = new android.graphics.drawable.GradientDrawable();
        cellBg.setColor(ThemeManager.blendColors(0xFF111111, currentTheme.accent, 0.06f));
        cellBg.setCornerRadius(dp(10));
        cellBg.setStroke(dp(1), ThemeManager.blendColors(0xFF1A1A1A, currentTheme.accent, 0.18f));
        cell.setBackground(cellBg);
        cell.setPadding(dp(8), dp(12), dp(8), dp(12));
        LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        cellParams.setMargins(0, 0, dp(8), 0);
        cell.setLayoutParams(cellParams);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextColor(currentTheme.accent);
        tvValue.setTextSize(15);
        tvValue.setGravity(android.view.Gravity.CENTER);
        tvValue.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed),
                android.graphics.Typeface.BOLD);
        cell.addView(tvValue);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(0xFF666666);
        tvLabel.setTextSize(10);
        tvLabel.setGravity(android.view.Gravity.CENTER);
        tvLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed));
        tvLabel.setLetterSpacing(0.08f);
        cell.addView(tvLabel);

        row.addView(cell);
    }

    // ── Static circuit facts ──────────────────────────────────────────────────

    private static class CircuitInfo {
        final String length, laps, lapRecord, corners, drsZones, direction;
        CircuitInfo(String length, String laps, String lapRecord,
                    String corners, String drsZones, String direction) {
            this.length = length; this.laps = laps; this.lapRecord = lapRecord;
            this.corners = corners; this.drsZones = drsZones; this.direction = direction;
        }
        static CircuitInfo of(String length, String laps, String lapRecord,
                              String corners, String drsZones, String direction) {
            return new CircuitInfo(length, laps, lapRecord, corners, drsZones, direction);
        }
    }

    private static final java.util.Map<String, CircuitInfo> CIRCUIT_INFO = new java.util.HashMap<>();
    static {
        CIRCUIT_INFO.put("melbourne",    CircuitInfo.of("5.278 km", "58", "1:20.235", "16", "4", "Clockwise"));
        CIRCUIT_INFO.put("australia",    CircuitInfo.of("5.278 km", "58", "1:20.235", "16", "4", "Clockwise"));
        CIRCUIT_INFO.put("albert park",  CircuitInfo.of("5.278 km", "58", "1:20.235", "16", "4", "Clockwise"));
        CIRCUIT_INFO.put("shanghai",     CircuitInfo.of("5.451 km", "56", "1:32.238", "16", "2", "Clockwise"));
        CIRCUIT_INFO.put("china",        CircuitInfo.of("5.451 km", "56", "1:32.238", "16", "2", "Clockwise"));
        CIRCUIT_INFO.put("suzuka",       CircuitInfo.of("5.807 km", "53", "1:30.983", "18", "1", "Clockwise"));
        CIRCUIT_INFO.put("japan",        CircuitInfo.of("5.807 km", "53", "1:30.983", "18", "1", "Clockwise"));
        CIRCUIT_INFO.put("bahrain",      CircuitInfo.of("5.412 km", "57", "1:31.447", "15", "3", "Clockwise"));
        CIRCUIT_INFO.put("jeddah",       CircuitInfo.of("6.174 km", "50", "1:30.734", "27", "3", "Clockwise"));
        CIRCUIT_INFO.put("saudi arabia", CircuitInfo.of("6.174 km", "50", "1:30.734", "27", "3", "Clockwise"));
        CIRCUIT_INFO.put("miami",        CircuitInfo.of("5.412 km", "57", "1:29.708", "19", "3", "Clockwise"));
        CIRCUIT_INFO.put("monaco",       CircuitInfo.of("3.337 km", "78", "1:12.909", "19", "1", "Clockwise"));
        CIRCUIT_INFO.put("montreal",     CircuitInfo.of("4.361 km", "70", "1:13.078", "14", "2", "Clockwise"));
        CIRCUIT_INFO.put("canada",       CircuitInfo.of("4.361 km", "70", "1:13.078", "14", "2", "Clockwise"));
        CIRCUIT_INFO.put("barcelona",    CircuitInfo.of("4.657 km", "66", "1:16.330", "16", "2", "Clockwise"));
        CIRCUIT_INFO.put("spain",        CircuitInfo.of("4.657 km", "66", "1:16.330", "16", "2", "Clockwise"));
        CIRCUIT_INFO.put("madrid",       CircuitInfo.of("5.538 km", "55", "TBA",      "20", "3", "Clockwise"));
        CIRCUIT_INFO.put("austria",      CircuitInfo.of("4.318 km", "71", "1:05.619", "10", "3", "Clockwise"));
        CIRCUIT_INFO.put("red bull ring",CircuitInfo.of("4.318 km", "71", "1:05.619", "10", "3", "Clockwise"));
        CIRCUIT_INFO.put("silverstone",  CircuitInfo.of("5.891 km", "52", "1:27.097", "18", "2", "Clockwise"));
        CIRCUIT_INFO.put("britain",      CircuitInfo.of("5.891 km", "52", "1:27.097", "18", "2", "Clockwise"));
        CIRCUIT_INFO.put("hungaroring",  CircuitInfo.of("4.381 km", "70", "1:16.627", "14", "1", "Clockwise"));
        CIRCUIT_INFO.put("hungary",      CircuitInfo.of("4.381 km", "70", "1:16.627", "14", "1", "Clockwise"));
        CIRCUIT_INFO.put("spa",          CircuitInfo.of("7.004 km", "44", "1:46.286", "19", "2", "Clockwise"));
        CIRCUIT_INFO.put("belgium",      CircuitInfo.of("7.004 km", "44", "1:46.286", "19", "2", "Clockwise"));
        CIRCUIT_INFO.put("zandvoort",    CircuitInfo.of("4.259 km", "72", "1:11.097", "14", "2", "Clockwise"));
        CIRCUIT_INFO.put("netherlands",  CircuitInfo.of("4.259 km", "72", "1:11.097", "14", "2", "Clockwise"));
        CIRCUIT_INFO.put("monza",        CircuitInfo.of("5.793 km", "53", "1:21.046", "11", "2", "Clockwise"));
        CIRCUIT_INFO.put("italy",        CircuitInfo.of("5.793 km", "53", "1:21.046", "11", "2", "Clockwise"));
        CIRCUIT_INFO.put("baku",         CircuitInfo.of("6.003 km", "51", "1:43.009", "20", "2", "Clockwise"));
        CIRCUIT_INFO.put("azerbaijan",   CircuitInfo.of("6.003 km", "51", "1:43.009", "20", "2", "Clockwise"));
        CIRCUIT_INFO.put("singapore",    CircuitInfo.of("4.940 km", "62", "1:35.867", "19", "3", "Clockwise"));
        CIRCUIT_INFO.put("marina bay",   CircuitInfo.of("4.940 km", "62", "1:35.867", "19", "3", "Clockwise"));
        CIRCUIT_INFO.put("austin",       CircuitInfo.of("5.513 km", "56", "1:36.169", "20", "2", "Clockwise"));
        CIRCUIT_INFO.put("cota",         CircuitInfo.of("5.513 km", "56", "1:36.169", "20", "2", "Clockwise"));
        CIRCUIT_INFO.put("mexico city",  CircuitInfo.of("4.304 km", "71", "1:17.774", "17", "3", "Clockwise"));
        CIRCUIT_INFO.put("mexico",       CircuitInfo.of("4.304 km", "71", "1:17.774", "17", "3", "Clockwise"));
        CIRCUIT_INFO.put("interlagos",   CircuitInfo.of("4.309 km", "71", "1:10.540", "15", "2", "Anti-CW"));
        CIRCUIT_INFO.put("brazil",       CircuitInfo.of("4.309 km", "71", "1:10.540", "15", "2", "Anti-CW"));
        CIRCUIT_INFO.put("las vegas",    CircuitInfo.of("6.201 km", "50", "1:35.490", "17", "2", "Anti-CW"));
        CIRCUIT_INFO.put("lusail",       CircuitInfo.of("5.380 km", "57", "1:24.319", "16", "2", "Clockwise"));
        CIRCUIT_INFO.put("qatar",        CircuitInfo.of("5.380 km", "57", "1:24.319", "16", "2", "Clockwise"));
        CIRCUIT_INFO.put("yas marina",   CircuitInfo.of("5.281 km", "58", "1:26.103", "16", "2", "Anti-CW"));
        CIRCUIT_INFO.put("abu dhabi",    CircuitInfo.of("5.281 km", "58", "1:26.103", "16", "2", "Anti-CW"));
    }

    private static CircuitInfo get(String key) {
        if (key == null || key.isEmpty()) return null;
        return CIRCUIT_INFO.get(key.trim().toLowerCase());
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }
}
