package com.example.project_f1;

import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.HashMap;

public class LapDetailsActivity extends BaseActivity {

    // ── Circuit data ──────────────────────────────────────────────────────────

    static class CircuitInfo {
        final int turns, drs;
        final String topSpeed, lapRecord, funFact;
        CircuitInfo(int turns, int drs, String topSpeed, String lapRecord, String funFact) {
            this.turns = turns; this.drs = drs;
            this.topSpeed = topSpeed; this.lapRecord = lapRecord; this.funFact = funFact;
        }
    }

    private static final HashMap<String, CircuitInfo> INFO = new HashMap<>();
    static {
        INFO.put("melbourne",    new CircuitInfo(16, 4, "328 km/h", "1:20.235 – Pérez (2023)",      "The circuit is rebuilt slightly each year as it uses public parkland roads."));
        INFO.put("shanghai",     new CircuitInfo(16, 2, "327 km/h", "1:32.238 – Schumacher (2004)", "Turn 1–2 hairpin is one of the slowest corners on the calendar at ~65 km/h."));
        INFO.put("suzuka",       new CircuitInfo(18, 1, "320 km/h", "1:30.983 – Hamilton (2019)",   "The only figure-of-eight layout on the F1 calendar, opened in 1962."));
        INFO.put("bahrain",      new CircuitInfo(15, 3, "321 km/h", "1:31.447 – De la Rosa (2005)", "Hosted the first-ever F1 night race in 2014 using its outer layout."));
        INFO.put("jeddah",       new CircuitInfo(27, 3, "322 km/h", "1:30.734 – Hamilton (2021)",   "The fastest street circuit in F1 history with an average speed over 250 km/h."));
        INFO.put("miami",        new CircuitInfo(19, 3, "320 km/h", "1:29.708 – Verstappen (2023)", "Built around a football stadium — the Hard Rock Stadium sits inside the circuit."));
        INFO.put("montreal",     new CircuitInfo(14, 2, "330 km/h", "1:13.078 – Bottas (2019)",     "Named after Gilles Villeneuve, who died in qualifying at Zolder in 1982."));
        INFO.put("monaco",       new CircuitInfo(19, 1, "290 km/h", "1:12.909 – Hamilton (2021)",   "The tunnel section drops drivers into near-darkness at over 280 km/h."));
        INFO.put("barcelona",    new CircuitInfo(16, 2, "320 km/h", "1:16.330 – Piastri (2024)",    "Teams use it for pre-season testing — setups here rarely translate elsewhere."));
        INFO.put("red bull ring", new CircuitInfo(10, 3, "318 km/h", "1:05.619 – Sainz (2020)",     "At just 10 corners it's the shortest lap on the calendar by corner count."));
        INFO.put("silverstone",  new CircuitInfo(18, 2, "330 km/h", "1:27.097 – Verstappen (2020)", "Copse corner was flat-out at over 300 km/h before the 2010 chicane was removed."));
        INFO.put("spa",          new CircuitInfo(19, 2, "355 km/h", "1:44.701 – Piastri (2024)",    "Eau Rouge / Raidillon elevation change is 35 metres — taken flat at 300+ km/h."));
        INFO.put("hungaroring",  new CircuitInfo(14, 1, "310 km/h", "1:16.627 – Hamilton (2020)",   "Often called 'Monaco without the walls' — overtaking is extremely difficult."));
        INFO.put("zandvoort",    new CircuitInfo(14, 2, "310 km/h", "1:11.097 – Hamilton (2021)",   "The banked final corner is tilted at 18° — the steepest banked turn in F1."));
        INFO.put("monza",        new CircuitInfo(11, 2, "362 km/h", "1:21.046 – Barrichello (2004)","The fastest circuit on the calendar — cars run almost zero downforce here."));
        INFO.put("madrid",       new CircuitInfo(20, 3, "315 km/h", "TBD – New for 2026",           "Replaces the Spanish GP at Barcelona, built in the IFEMA convention district."));
        INFO.put("baku",         new CircuitInfo(20, 2, "360 km/h", "1:43.009 – Leclerc (2019)",    "The castle section is the narrowest point in F1 — just 7.6 metres wide."));
        INFO.put("singapore",    new CircuitInfo(19, 3, "320 km/h", "1:35.867 – Hamilton (2023)",   "The only F1 race that regularly finishes close to the 2-hour time limit."));
        INFO.put("cota",         new CircuitInfo(20, 2, "335 km/h", "1:36.169 – Leclerc (2019)",    "Turn 1 is the highest point on the circuit at 41 metres above the start line."));
        INFO.put("mexico",       new CircuitInfo(17, 2, "370 km/h", "1:17.774 – Bottas (2021)",     "At 2,285 m altitude the thin air reduces downforce and engine power significantly."));
        INFO.put("interlagos",   new CircuitInfo(15, 2, "325 km/h", "1:10.540 – Bottas (2018)",     "One of only two anti-clockwise circuits on the calendar alongside Bahrain."));
        INFO.put("las vegas",    new CircuitInfo(17, 2, "358 km/h", "1:33.365 – Piastri (2024)",    "Cars pass the Bellagio fountains and the Sphere at over 340 km/h on the strip."));
        INFO.put("lusail",       new CircuitInfo(16, 2, "322 km/h", "1:22.384 – Verstappen (2023)", "The floodlit circuit uses 3,600 projectors — visible from space on clear nights."));
        INFO.put("yas marina",   new CircuitInfo(16, 2, "320 km/h", "1:25.637 – Verstappen (2021)", "The circuit was redesigned in 2021 to allow more overtaking through the final sector."));
        // Historic
        INFO.put("pescara",      new CircuitInfo(9,  0, "280 km/h", "N/A – 1957 only",              "At 25.8 km it remains the longest circuit ever used in a World Championship race."));
        INFO.put("nurburgring",  new CircuitInfo(154,0, "290 km/h", "6:47.3 – Lauda (1975)",        "Niki Lauda's near-fatal 1976 crash here led to the circuit being dropped from F1."));
        INFO.put("sepang",       new CircuitInfo(15, 2, "325 km/h", "1:34.080 – Vettel (2004)",     "Designed by Hermann Tilke, it set the template for all modern Tilke-designed circuits."));
        INFO.put("buddh",        new CircuitInfo(16, 3, "320 km/h", "1:27.249 – Vettel (2013)",     "Vettel won all three Indian GPs held here, clinching his 4th title at the circuit."));
        INFO.put("kyalami",      new CircuitInfo(16, 0, "295 km/h", "1:17.578 – Mansell (1992)",    "South Africa hosted F1 from 1962–1993, producing iconic battles in the apartheid era."));
        INFO.put("adelaide",     new CircuitInfo(16, 0, "310 km/h", "1:15.381 – Schumacher (1992)", "The 1986 race saw four drivers mathematically able to win the title on the final lap."));
        INFO.put("istanbul",     new CircuitInfo(14, 2, "315 km/h", "1:24.770 – Montoya (2005)",    "Turn 8 is a quadruple-apex left-hander — widely considered the greatest corner in F1."));
    }

    private ThemeManager.TeamTheme theme;
    private LinearLayout circuitsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap_details);
        theme = ThemeManager.applyFullTheme(this);

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        circuitsList = findViewById(R.id.circuitsList);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        setupSwipeRefresh(swipeRefresh, theme.accent, this::recreate);

        loadCircuits();
    }

    private void loadCircuits() {
        circuitsList.removeAllViews();
        addSectionLabel("2026 CALENDAR");
        addCircuitCard("R1",  "ALBERT PARK",           "Australia",     "5.278 km • 58 laps", "melbourne",    true);
        addCircuitCard("R2",  "SHANGHAI INT. CIRCUIT", "China",         "5.451 km • 56 laps", "shanghai",     true);
        addCircuitCard("R3",  "SUZUKA CIRCUIT",        "Japan",         "5.807 km • 53 laps", "suzuka",       true);
        addCircuitCard("R4",  "BAHRAIN INT. CIRCUIT",  "Bahrain",       "5.412 km • 57 laps", "bahrain",      true);
        addCircuitCard("R5",  "JEDDAH CORNICHE",       "Saudi Arabia",  "6.174 km • 50 laps", "jeddah",       true);
        addCircuitCard("R6",  "MIAMI INT. AUTODROME",  "USA",           "5.412 km • 57 laps", "miami",        true);
        addCircuitCard("R7",  "GILLES-VILLENEUVE",     "Canada",        "4.361 km • 70 laps", "montreal",     true);
        addCircuitCard("R8",  "CIRCUIT DE MONACO",     "Monaco",        "3.337 km • 78 laps", "monaco",       true);
        addCircuitCard("R9",  "CIRCUIT DE BARCELONA",  "Spain",         "4.657 km • 66 laps", "barcelona",    true);
        addCircuitCard("R10", "RED BULL RING",         "Austria",       "4.318 km • 71 laps", "red bull ring",true);
        addCircuitCard("R11", "SILVERSTONE",           "UK",            "5.891 km • 52 laps", "silverstone",  true);
        addCircuitCard("R12", "SPA-FRANCORCHAMPS",     "Belgium",       "7.004 km • 44 laps", "spa",          true);
        addCircuitCard("R13", "HUNGARORING",           "Hungary",       "4.381 km • 70 laps", "hungaroring",  true);
        addCircuitCard("R14", "CIRCUIT ZANDVOORT",     "Netherlands",   "4.259 km • 72 laps", "zandvoort",    true);
        addCircuitCard("R15", "MONZA",                 "Italy",         "5.793 km • 53 laps", "monza",        true);
        addCircuitCard("R16", "CIRCUITO DE MADRID",    "Spain",         "4.657 km • 66 laps", "madrid",       true);
        addCircuitCard("R17", "BAKU CITY CIRCUIT",     "Azerbaijan",    "6.003 km • 51 laps", "baku",         true);
        addCircuitCard("R18", "MARINA BAY STREET",     "Singapore",     "4.940 km • 62 laps", "singapore",    true);
        addCircuitCard("R19", "CIRCUIT OF THE AMERICAS","USA (Austin)", "5.513 km • 56 laps", "cota",         true);
        addCircuitCard("R20", "AUTODROMO H. RODRIGUEZ","Mexico",        "4.304 km • 71 laps", "mexico",       true);
        addCircuitCard("R21", "INTERLAGOS",            "Brazil",        "4.309 km • 71 laps", "interlagos",   true);
        addCircuitCard("R22", "LAS VEGAS STRIP",       "USA",           "6.201 km • 50 laps", "las vegas",    true);
        addCircuitCard("R23", "LUSAIL INTERNATIONAL",  "Qatar",         "5.419 km • 57 laps", "lusail",       true);
        addCircuitCard("R24", "YAS MARINA",            "UAE",           "5.281 km • 58 laps", "yas marina",   true);

        addSectionLabel("GIANTS OF THE PAST");
        addCircuitCard(null, "PESCARA CIRCUIT",         "Italy",         "25.8 km • 1957 GP",   "pescara",    false);
        addCircuitCard(null, "NÜRBURGRING NORDSCHLEIFE","Germany",       "22.8 km • Until 1976", "nurburgring",false);
        addCircuitCard(null, "SEPANG INTERNATIONAL",    "Malaysia",      "5.543 km • Last 2017", "sepang",     false);
        addCircuitCard(null, "BUDDH INTERNATIONAL",     "India",         "5.125 km • Last 2013", "buddh",      false);
        addCircuitCard(null, "KYALAMI",                 "South Africa",  "4.261 km • Last 1993", "kyalami",    false);
        addCircuitCard(null, "ADELAIDE STREET CIRCUIT", "Australia",     "3.780 km • Last 1995", "adelaide",   false);
        addCircuitCard(null, "ISTANBUL PARK",           "Turkey",        "5.338 km • Last 2021", "istanbul",   false);
    }

    private void addSectionLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(theme.accent);
        tv.setTextSize(13);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tv.setLetterSpacing(0.15f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, dp(20), 0, dp(10));
        tv.setLayoutParams(p);
        circuitsList.addView(tv);
    }

    private void addCircuitCard(String round, String name, String location, String length, String circuitKey, boolean isActive) {
        CircuitInfo info = INFO.get(circuitKey);

        com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(this);
        card.setCardBackgroundColor(theme.cardBg);
        card.setRadius(dp(16));
        card.setStrokeColor(isActive ? theme.accent : 0xFF333333);
        card.setStrokeWidth(dp(1));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(cardParams);

        LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        card.addView(contentContainer);

        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setPadding(dp(16), dp(16), dp(16), dp(16));

        if (round != null) {
            TextView tvRound = new TextView(this);
            tvRound.setText(round);
            tvRound.setTextColor(isActive ? theme.accent : 0xFF666666);
            tvRound.setTextSize(12);
            tvRound.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(dp(36), LinearLayout.LayoutParams.WRAP_CONTENT);
            rp.setMargins(0, 0, dp(10), 0);
            tvRound.setLayoutParams(rp);
            headerRow.addView(tvRound);
        }

        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(isActive ? 0xFFFFFFFF : 0xFF999999);
        tvName.setTextSize(15);
        tvName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        textCol.addView(tvName);

        TextView tvLoc = new TextView(this);
        tvLoc.setText(location + "  •  " + length);
        tvLoc.setTextColor(0xFF666666);
        tvLoc.setTextSize(12);
        tvLoc.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        textCol.addView(tvLoc);

        headerRow.addView(textCol);

        TextView tvChevron = new TextView(this);
        tvChevron.setText("▼");
        tvChevron.setTextColor(0xFF444444);
        tvChevron.setTextSize(11);
        headerRow.addView(tvChevron);

        contentContainer.addView(headerRow);

        // Expandable section
        LinearLayout expandable = new LinearLayout(this);
        expandable.setOrientation(LinearLayout.VERTICAL);
        expandable.setVisibility(View.GONE);
        expandable.setPadding(dp(16), 0, dp(16), dp(16));

        // Map + specs side by side
        LinearLayout detailRow = new LinearLayout(this);
        detailRow.setOrientation(LinearLayout.HORIZONTAL);

        Integer circuitRes = CircuitAssets.getCircuitDrawable(circuitKey);
        if (circuitRes != null) {
            ImageView ivMap = new ImageView(this);
            LinearLayout.LayoutParams ivp = new LinearLayout.LayoutParams(0, dp(180), 1f);
            ivp.setMargins(0, 0, dp(12), 0);
            ivMap.setLayoutParams(ivp);
            ivMap.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivMap.setImageResource(circuitRes);
            detailRow.addView(ivMap);
        }

        LinearLayout specsCol = new LinearLayout(this);
        specsCol.setOrientation(LinearLayout.VERTICAL);
        specsCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if (info != null) {
            addStatRow(specsCol, "TURNS", info.turns + "");
            addStatRow(specsCol, "DRS", info.drs + "");
            addStatRow(specsCol, "TOP SPEED", info.topSpeed);
            
            TextView tvFact = new TextView(this);
            tvFact.setText(info.funFact);
            tvFact.setTextColor(0xFF888888);
            tvFact.setTextSize(11);
            tvFact.setPadding(0, dp(8), 0, 0);
            specsCol.addView(tvFact);
        }

        detailRow.addView(specsCol);
        expandable.addView(detailRow);
        contentContainer.addView(expandable);

        card.setOnClickListener(v -> {
            boolean expanding = expandable.getVisibility() == View.GONE;
            if (expanding) CardAnimationHelper.expand(expandable);
            else CardAnimationHelper.collapse(expandable);
            CardAnimationHelper.rotateArrow(tvChevron, expanding);
            tvChevron.setTextColor(expanding ? theme.accent : 0xFF444444);
            tvChevron.setText(expanding ? "▲" : "▼");
        });

        circuitsList.addView(card);
    }

    private void addStatRow(LinearLayout parent, String label, String value) {
        LinearLayout row = new LinearLayout(this);
        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(0xFF555555);
        tvLabel.setTextSize(9);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        row.addView(tvLabel);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextColor(0xFFCCCCCC);
        tvValue.setTextSize(11);
        tvValue.setGravity(Gravity.END);
        tvValue.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        row.addView(tvValue);
        parent.addView(row);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }
}
