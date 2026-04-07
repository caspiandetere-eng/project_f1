package com.example.project_f1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.example.project_f1.models.Driver;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriverStatsActivity extends BaseActivity {

    private Spinner spinnerDriver;
    private LinearLayout chartsContainer;
    private MaterialCardView driverProfileCard;
    private TextView tvProfileName, tvProfileCountry, tvProfileCategory, tvProfileDesc;
    private ThemeManager.TeamTheme theme;
    private Map<String, List<YearData>> driverDataMap = new HashMap<>();
    private Map<String, DriverProfile> csvProfileMap = new HashMap<>();
    private Map<String, List<CircuitStats>> driverCircuitStats = new HashMap<>();
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    static class YearData {
        int year, points, wins, position;
    }

    static class DriverProfile {
        String name, country, category, description;
    }

    static class CircuitStats {
        String name;
        double avgSpeed;
        int wins, podiums, poles, fastestLaps, races;
        double avgPosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_stats);
        theme = ThemeManager.applyFullTheme(this);
        applyTheme();

        spinnerDriver = findViewById(R.id.spinnerDriver);
        chartsContainer = findViewById(R.id.chartsContainer);
        driverProfileCard = findViewById(R.id.driverProfileCard);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileCountry = findViewById(R.id.tvProfileCountry);
        tvProfileCategory = findViewById(R.id.tvProfileCategory);
        tvProfileDesc = findViewById(R.id.tvProfileDesc);

        // Load data in background
        loadAllDataAsync();
    }

    private void applyTheme() {
        View root = findViewById(android.R.id.content);
        if (root != null) root.setBackgroundColor(0xFF0D0D0D);
        View topStripe = findViewById(R.id.topStripe);
        if (topStripe != null) topStripe.setBackgroundColor(theme.accent);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadAllDataAsync() {
        executor.execute(() -> {
            loadCsvProfiles();
            loadDriverData();
            loadCircuitStats();
            
            mainHandler.post(this::setupDriverSpinner);
        });
    }

    private void loadCsvProfiles() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("f1_legends_and_current_drivers.csv")))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                int firstComma = line.indexOf(',');
                int secondComma = line.indexOf(',', firstComma + 1);
                int thirdComma = line.indexOf(',', secondComma + 1);
                if (firstComma < 0 || secondComma < 0 || thirdComma < 0) continue;
                DriverProfile p = new DriverProfile();
                p.category = line.substring(0, firstComma).trim();
                p.name = line.substring(firstComma + 1, secondComma).trim();
                p.country = line.substring(secondComma + 1, thirdComma).trim();
                p.description = line.substring(thirdComma + 1).trim().replaceAll("^\"|\"$", "");
                csvProfileMap.put(p.name.toLowerCase(), p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DriverProfile findProfile(String fullName) {
        return csvProfileMap.get(fullName.toLowerCase());
    }

    private static final Map<String, String> JSON_TO_DRIVER_ID = new HashMap<String, String>() {{
        put("max_verstappen", "verstappen");
        put("mick_schumacher", "mick_schumacher");
        put("kevin_magnussen", "kevin_magnussen");
        put("de_vries", "de_vries");
    }};

    private String resolveDriverId(String jsonId) {
        return JSON_TO_DRIVER_ID.containsKey(jsonId) ? JSON_TO_DRIVER_ID.get(jsonId) : jsonId;
    }

    private void loadDriverData() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("f1_data.json")))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            JSONObject root = new JSONObject(sb.toString());
            JSONArray drivers = root.getJSONArray("drivers");

            for (int i = 0; i < drivers.length(); i++) {
                JSONObject d = drivers.getJSONObject(i);
                String driverId = resolveDriverId(d.getString("driver_id"));

                if (!driverDataMap.containsKey(driverId)) {
                    driverDataMap.put(driverId, new ArrayList<>());
                }

                YearData yd = new YearData();
                yd.year = d.getInt("year");
                yd.points = d.getInt("points");
                yd.wins = d.getInt("wins");
                yd.position = d.getInt("position");
                driverDataMap.get(driverId).add(yd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDriverSpinner() {
        List<String> driverNames = new ArrayList<>();
        List<String> driverIds = new ArrayList<>();

        for (Driver d : Driver.getAllDrivers()) {
            if (driverDataMap.containsKey(d.id)) {
                driverNames.add(d.getFullName());
                driverIds.add(d.id);
            }
        }

        for (DriverProfile p : csvProfileMap.values()) {
            if ("Legend".equals(p.category)) {
                String key = "legend_" + p.name.toLowerCase().replace(" ", "_");
                if (!driverIds.contains(key)) {
                    driverNames.add(p.name + " ★");
                    driverIds.add(key);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDriver.setAdapter(adapter);

        spinnerDriver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String driverId = driverIds.get(position);
                String displayName = driverNames.get(position).replace(" ★", "");
                showProfile(displayName, driverId);
                displayCharts(driverId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (!driverIds.isEmpty()) {
            String displayName = driverNames.get(0).replace(" ★", "");
            showProfile(displayName, driverIds.get(0));
            displayCharts(driverIds.get(0));
        }
    }

    private void showProfile(String displayName, String driverId) {
        DriverProfile p = findProfile(displayName);
        if (p == null) {
            Driver d = Driver.getDriverById(driverId);
            if (d != null) p = findProfile(d.getFullName());
        }
        if (p != null) {
            tvProfileName.setText(p.name);
            tvProfileCountry.setText(p.country);
            tvProfileDesc.setText(p.description);
            boolean isLegend = "Legend".equals(p.category);
            tvProfileCategory.setText(isLegend ? "LEGEND" : "CURRENT DRIVER");
            tvProfileCategory.setTextColor(isLegend ? 0xFFFFD700 : theme.accent);
            tvProfileCategory.setBackgroundColor(isLegend ? 0x22FFD700 : 0x22E10600);
            driverProfileCard.setVisibility(View.VISIBLE);
        } else {
            driverProfileCard.setVisibility(View.GONE);
        }
    }

    private void loadCircuitStats() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("driver_circuit_stats.json")))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            JSONObject root = new JSONObject(sb.toString());
            String[] trackedDrivers = {"verstappen", "hamilton", "leclerc"};
            for (String driverId : trackedDrivers) {
                if (root.has(driverId)) {
                    JSONArray circuits = root.getJSONObject(driverId).getJSONArray("circuits");
                    List<CircuitStats> statsList = new ArrayList<>();
                    for (int i = 0; i < circuits.length(); i++) {
                        JSONObject c = circuits.getJSONObject(i);
                        CircuitStats cs = new CircuitStats();
                        cs.name = c.getString("name");
                        cs.avgSpeed = c.getDouble("avgSpeed");
                        cs.wins = c.getInt("wins");
                        cs.podiums = c.getInt("podiums");
                        cs.poles = c.getInt("poles");
                        cs.fastestLaps = c.getInt("fastestLaps");
                        cs.races = c.getInt("races");
                        cs.avgPosition = c.getDouble("avgPosition");
                        statsList.add(cs);
                    }
                    driverCircuitStats.put(driverId, statsList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayCharts(String driverId) {
        chartsContainer.removeAllViews();
        List<YearData> data = driverDataMap.get(driverId);
        List<CircuitStats> circuitData = driverCircuitStats.get(driverId);

        if (data != null && !data.isEmpty()) {
            chartsContainer.addView(createSectionLabel("POINTS PROGRESSION"));
            chartsContainer.addView(createLineChart(data));

            chartsContainer.addView(createSectionLabel("WINS BY YEAR"));
            chartsContainer.addView(createBarChart(data));

            chartsContainer.addView(createSectionLabel("CHAMPIONSHIP POSITION"));
            chartsContainer.addView(createPositionChart(data));
        }

        if (circuitData != null && !circuitData.isEmpty()) {
            chartsContainer.addView(createSectionLabel("CIRCUIT PERFORMANCE ANALYSIS"));
            chartsContainer.addView(createCircuitPerformanceCard());

            chartsContainer.addView(createSectionLabel("AVERAGE SPEED BY CIRCUIT"));
            chartsContainer.addView(createHorizontalBarChart(circuitData, true));

            chartsContainer.addView(createSectionLabel("WIN RATE BY CIRCUIT"));
            chartsContainer.addView(createHorizontalBarChart(circuitData, false));

            chartsContainer.addView(createSectionLabel("CIRCUIT DETAILS WITH PERFORMANCE RADAR"));
            for (CircuitStats cs : circuitData) {
                chartsContainer.addView(createCircuitDetailCard(cs));
            }
        }
    }

    private View createSectionLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(theme.accent);
        tv.setTextSize(14);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tv.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dpToPx(32), 0, dpToPx(12));
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void styleChart(Chart<?> chart) {
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.getDescription().setEnabled(false);
        chart.setNoDataTextColor(Color.GRAY);
        chart.setTouchEnabled(true);
        if (chart instanceof com.github.mikephil.charting.charts.BarLineChartBase) {
            com.github.mikephil.charting.charts.BarLineChartBase<?> blChart =
                (com.github.mikephil.charting.charts.BarLineChartBase<?>) chart;
            blChart.setDragEnabled(true);
            blChart.setScaleEnabled(true);
            blChart.setPinchZoom(true);
        }

        Legend legend = chart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setYOffset(10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(10f);
    }

    private LineChart createLineChart(List<YearData> data) {
        LineChart chart = new LineChart(this);
        chart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(240)));
        styleChart(chart);

        List<Entry> entries = new ArrayList<>();
        final List<String> years = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry(i, data.get(i).points));
            years.add(String.valueOf(data.get(i).year));
        }

        LineDataSet dataSet = new LineDataSet(entries, "World Championship Points");
        dataSet.setColor(theme.accent);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(theme.accent);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(0xFF0D0D0D);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(theme.accent);
        dataSet.setFillAlpha(40);

        chart.setData(new LineData(dataSet));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(years));
        chart.getAxisLeft().setTextColor(Color.GRAY);
        chart.getAxisRight().setEnabled(false);
        chart.animateX(1000);
        return chart;
    }

    private BarChart createBarChart(List<YearData> data) {
        BarChart chart = new BarChart(this);
        chart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(240)));
        styleChart(chart);

        List<BarEntry> entries = new ArrayList<>();
        final List<String> years = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, data.get(i).wins));
            years.add(String.valueOf(data.get(i).year));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Grand Prix Wins");
        dataSet.setColor(theme.accent);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setBarBorderWidth(1f);
        dataSet.setBarBorderColor(Color.WHITE);

        chart.setData(new BarData(dataSet));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(years));
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setTextColor(Color.GRAY);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1000);
        return chart;
    }

    private LineChart createPositionChart(List<YearData> data) {
        LineChart chart = new LineChart(this);
        chart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(240)));
        styleChart(chart);

        List<Entry> entries = new ArrayList<>();
        final List<String> years = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry(i, data.get(i).position));
            years.add(String.valueOf(data.get(i).year));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Season Standing (Lower is Better)");
        dataSet.setColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(theme.accent);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "P" + (int)value;
            }
        });

        chart.setData(new LineData(dataSet));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(years));
        
        YAxis y = chart.getAxisLeft();
        y.setInverted(true);
        y.setAxisMinimum(1f);
        y.setAxisMaximum(22f);
        y.setTextColor(Color.GRAY);
        
        chart.getAxisRight().setEnabled(false);
        chart.animateXY(800, 800);
        return chart;
    }

    private HorizontalBarChart createHorizontalBarChart(List<CircuitStats> data, boolean isSpeed) {
        HorizontalBarChart chart = new HorizontalBarChart(this);
        int height = Math.min(data.size() * 40 + 100, 600);
        chart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(height)));
        styleChart(chart);

        List<BarEntry> entries = new ArrayList<>();
        final List<String> circuitNames = new ArrayList<>();
        
        List<CircuitStats> sorted = new ArrayList<>(data);
        if (isSpeed) {
            Collections.sort(sorted, (a, b) -> Double.compare(a.avgSpeed, b.avgSpeed));
        } else {
            Collections.sort(sorted, (a, b) -> Double.compare(a.wins * 1.0 / a.races, b.wins * 1.0 / b.races));
        }

        for (int i = 0; i < sorted.size(); i++) {
            CircuitStats cs = sorted.get(i);
            entries.add(new BarEntry(i, (float)(isSpeed ? cs.avgSpeed : (cs.wins * 100.0 / cs.races))));
            circuitNames.add(cs.name);
        }

        BarDataSet dataSet = new BarDataSet(entries, isSpeed ? "Avg Speed (km/h)" : "Win Rate (%)");
        dataSet.setColor(isSpeed ? 0xFFFFAA00 : theme.accent);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(9f);

        chart.setData(new BarData(dataSet));
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(circuitNames));
        chart.getXAxis().setTextColor(Color.GRAY);
        chart.getAxisLeft().setTextColor(Color.GRAY);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1000);
        return chart;
    }

    private View createCircuitPerformanceCard() {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardBackgroundColor(0xFF1A1A1A);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(4));
        card.setStrokeColor(0xFF333333);
        card.setStrokeWidth(dpToPx(1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dpToPx(8), 0, dpToPx(16));
        card.setLayoutParams(lp);

        TextView tv = new TextView(this);
        tv.setText("Comprehensive circuit performance analysis showing average speeds, win rates, and tactical efficiency for this driver across different track layouts.");
        tv.setTextColor(0xFFCCCCCC);
        tv.setTextSize(13);
        tv.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        card.addView(tv);
        return card;
    }

    private View createCircuitDetailCard(CircuitStats cs) {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardBackgroundColor(0xFF1A1A1A);
        card.setRadius(dpToPx(16));
        card.setCardElevation(dpToPx(6));
        card.setStrokeColor(theme.accent);
        card.setStrokeWidth(dpToPx(2));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(0, dpToPx(12), 0, dpToPx(12));
        card.setLayoutParams(cardLp);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        TextView title = new TextView(this);
        title.setText(cs.name.toUpperCase());
        title.setTextColor(theme.accent);
        title.setTextSize(20);
        title.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        container.addView(title);

        Integer circuitDrawable = CircuitAssets.getCircuitDrawable(cs.name);
        if (circuitDrawable != null) {
            ImageView circuitMap = new ImageView(this);
            circuitMap.setImageResource(circuitDrawable);
            circuitMap.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(140));
            imgLp.setMargins(0, dpToPx(16), 0, dpToPx(16));
            circuitMap.setLayoutParams(imgLp);
            container.addView(circuitMap);
        }

        // Radar Chart for individual circuit efficiency
        RadarChart radar = new RadarChart(this);
        radar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(260)));
        styleChart(radar);
        
        List<RadarEntry> radarEntries = new ArrayList<>();
        radarEntries.add(new RadarEntry((float)(cs.wins * 100.0 / cs.races)));
        radarEntries.add(new RadarEntry((float)(cs.podiums * 100.0 / cs.races)));
        radarEntries.add(new RadarEntry((float)(cs.poles * 100.0 / cs.races)));
        radarEntries.add(new RadarEntry((float)(cs.fastestLaps * 100.0 / cs.races)));
        radarEntries.add(new RadarEntry((float)(100.0 - (cs.avgPosition - 1) * 5)));
        
        RadarDataSet rDataSet = new RadarDataSet(radarEntries, "Performance Efficiency");
        rDataSet.setColor(theme.accent);
        rDataSet.setFillColor(theme.accent);
        rDataSet.setDrawFilled(true);
        rDataSet.setFillAlpha(100);
        rDataSet.setLineWidth(2f);
        
        radar.setData(new RadarData(rDataSet));
        radar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Win %", "Podium %", "Poles %", "Fastest L", "Avg P"}));
        radar.getXAxis().setTextColor(Color.WHITE);
        radar.getYAxis().setEnabled(false);
        radar.getLegend().setEnabled(false);
        radar.animateXY(1000, 1000);
        container.addView(radar);

        LinearLayout statsGrid = new LinearLayout(this);
        statsGrid.setOrientation(LinearLayout.VERTICAL);
        statsGrid.setPadding(0, dpToPx(16), 0, 0);
        statsGrid.addView(createStatRow("Average Speed", String.format(Locale.US, "%.1f km/h", cs.avgSpeed)));
        statsGrid.addView(createStatRow("Wins", cs.wins + " / " + cs.races + " races"));
        statsGrid.addView(createStatRow("Average Position", String.format(Locale.US, "P%.1f", cs.avgPosition)));
        container.addView(statsGrid);

        card.addView(container);
        return card;
    }

    private View createStatRow(String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLp.setMargins(0, dpToPx(4), 0, dpToPx(4));
        row.setLayoutParams(rowLp);

        TextView labelTv = new TextView(this);
        labelTv.setText(label);
        labelTv.setTextColor(0xFF999999);
        labelTv.setTextSize(13);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        labelTv.setLayoutParams(labelLp);
        row.addView(labelTv);

        TextView valueTv = new TextView(this);
        valueTv.setText(value);
        valueTv.setTextColor(0xFFFFFFFF);
        valueTv.setTextSize(13);
        valueTv.setTypeface(null, Typeface.BOLD);
        row.addView(valueTv);

        return row;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
