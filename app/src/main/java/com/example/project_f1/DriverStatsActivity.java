package com.example.project_f1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.example.project_f1.models.Driver;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverStatsActivity extends BaseActivity {

    private Spinner spinnerDriver;
    private LinearLayout chartsContainer;
    private MaterialCardView driverProfileCard;
    private TextView tvProfileName, tvProfileCountry, tvProfileCategory, tvProfileDesc;
    private ThemeManager.TeamTheme theme;
    private Map<String, List<YearData>> driverDataMap = new HashMap<>();
    private Map<String, DriverProfile> csvProfileMap = new HashMap<>();

    static class YearData {
        int year, points, wins, position;
    }

    static class DriverProfile {
        String name, country, category, description;
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

        loadCsvProfiles();
        loadDriverData();
        setupDriverSpinner();
    }

    private void applyTheme() {
        View topStripe = findViewById(R.id.topStripe);
        if (topStripe != null) topStripe.setBackgroundColor(theme.accent);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadCsvProfiles() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("f1_legends_and_current_drivers.csv")));
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
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DriverProfile findProfile(String fullName) {
        return csvProfileMap.get(fullName.toLowerCase());
    }

    // Maps driver_id keys in f1_data.json → Driver.id in Driver.java
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
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("f1_data.json")));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

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

        // Current drivers with chart data
        for (Driver d : Driver.getAllDrivers()) {
            if (driverDataMap.containsKey(d.id)) {
                driverNames.add(d.getFullName());
                driverIds.add(d.id);
            }
        }

        // Legends from CSV (no chart data, shown with ★)
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

    private void displayCharts(String driverId) {
        chartsContainer.removeAllViews();
        List<YearData> data = driverDataMap.get(driverId);
        if (data == null || data.isEmpty()) return;

        chartsContainer.addView(createSectionLabel("POINTS PROGRESSION"));
        chartsContainer.addView(new LineChartView(this, data, theme));

        chartsContainer.addView(createSectionLabel("WINS BY YEAR"));
        chartsContainer.addView(new BarChartView(this, data, theme));

        chartsContainer.addView(createSectionLabel("CHAMPIONSHIP POSITION"));
        chartsContainer.addView(new PositionPlotView(this, data, theme));
    }

    private TextView createSectionLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(theme.accent);
        tv.setTextSize(16);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        tv.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dpToPx(24), 0, dpToPx(12));
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    static class LineChartView extends View {
        private List<YearData> data;
        private ThemeManager.TeamTheme theme;
        private Paint linePaint, pointPaint, gridPaint, textPaint;

        public LineChartView(Context ctx, List<YearData> data, ThemeManager.TeamTheme theme) {
            super(ctx);
            this.data = data;
            this.theme = theme;
            setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(200)));

            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setColor(theme.accent);
            linePaint.setStrokeWidth(dpToPx(3));
            linePaint.setStyle(Paint.Style.STROKE);

            pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            pointPaint.setColor(theme.accent);
            pointPaint.setStyle(Paint.Style.FILL);

            gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            gridPaint.setColor(0x33FFFFFF);
            gridPaint.setStrokeWidth(dpToPx(1));

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(0xFFCCCCCC);
            textPaint.setTextSize(dpToPx(10));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (data.isEmpty()) return;

            float w = getWidth();
            float h = getHeight();
            float padL = dpToPx(40), padR = dpToPx(20), padT = dpToPx(20), padB = dpToPx(30);
            float chartW = w - padL - padR;
            float chartH = h - padT - padB;

            int maxPoints = 0;
            for (YearData yd : data) maxPoints = Math.max(maxPoints, yd.points);
            if (maxPoints == 0) maxPoints = 100;

            for (int i = 0; i <= 4; i++) {
                float y = padT + chartH * i / 4f;
                canvas.drawLine(padL, y, padL + chartW, y, gridPaint);
                int val = maxPoints - (maxPoints * i / 4);
                canvas.drawText(String.valueOf(val), dpToPx(5), y + dpToPx(4), textPaint);
            }

            Path path = new Path();
            for (int i = 0; i < data.size(); i++) {
                float x = padL + chartW * i / (data.size() - 1);
                float y = padT + chartH * (1 - (float)data.get(i).points / maxPoints);
                if (i == 0) path.moveTo(x, y);
                else path.lineTo(x, y);
            }
            canvas.drawPath(path, linePaint);

            for (int i = 0; i < data.size(); i++) {
                float x = padL + chartW * i / (data.size() - 1);
                float y = padT + chartH * (1 - (float)data.get(i).points / maxPoints);
                canvas.drawCircle(x, y, dpToPx(5), pointPaint);
                canvas.drawText(String.valueOf(data.get(i).year), x - dpToPx(12), h - dpToPx(10), textPaint);
            }
        }

        private int dpToPx(int dp) {
            return (int) (dp * getResources().getDisplayMetrics().density);
        }
    }

    static class BarChartView extends View {
        private List<YearData> data;
        private ThemeManager.TeamTheme theme;
        private Paint barPaint, textPaint;

        public BarChartView(Context ctx, List<YearData> data, ThemeManager.TeamTheme theme) {
            super(ctx);
            this.data = data;
            this.theme = theme;
            setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(180)));

            barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            barPaint.setColor(theme.accent);
            barPaint.setStyle(Paint.Style.FILL);

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(0xFFCCCCCC);
            textPaint.setTextSize(dpToPx(10));
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (data.isEmpty()) return;

            float w = getWidth();
            float h = getHeight();
            float padL = dpToPx(20), padR = dpToPx(20), padT = dpToPx(20), padB = dpToPx(30);
            float chartW = w - padL - padR;
            float chartH = h - padT - padB;

            int maxWins = 1;
            for (YearData yd : data) maxWins = Math.max(maxWins, yd.wins);

            float barWidth = chartW / data.size() * 0.7f;
            float spacing = chartW / data.size();

            for (int i = 0; i < data.size(); i++) {
                float x = padL + spacing * i + spacing / 2 - barWidth / 2;
                float barHeight = chartH * data.get(i).wins / (float)maxWins;
                float y = padT + chartH - barHeight;

                canvas.drawRect(x, y, x + barWidth, padT + chartH, barPaint);
                canvas.drawText(String.valueOf(data.get(i).wins), x + barWidth / 2, y - dpToPx(5), textPaint);
                canvas.drawText(String.valueOf(data.get(i).year), x + barWidth / 2, h - dpToPx(10), textPaint);
            }
        }

        private int dpToPx(int dp) {
            return (int) (dp * getResources().getDisplayMetrics().density);
        }
    }

    static class PositionPlotView extends View {
        private List<YearData> data;
        private ThemeManager.TeamTheme theme;
        private Paint pointPaint, gridPaint, textPaint;

        public PositionPlotView(Context ctx, List<YearData> data, ThemeManager.TeamTheme theme) {
            super(ctx);
            this.data = data;
            this.theme = theme;
            setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(180)));

            pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            pointPaint.setColor(theme.accent);
            pointPaint.setStyle(Paint.Style.FILL);

            gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            gridPaint.setColor(0x33FFFFFF);
            gridPaint.setStrokeWidth(dpToPx(1));

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(0xFFCCCCCC);
            textPaint.setTextSize(dpToPx(10));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (data.isEmpty()) return;

            float w = getWidth();
            float h = getHeight();
            float padL = dpToPx(40), padR = dpToPx(20), padT = dpToPx(20), padB = dpToPx(30);
            float chartW = w - padL - padR;
            float chartH = h - padT - padB;

            for (int i = 0; i <= 5; i++) {
                float y = padT + chartH * i / 5f;
                canvas.drawLine(padL, y, padL + chartW, y, gridPaint);
                int pos = 1 + (22 * i / 5);
                canvas.drawText("P" + pos, dpToPx(5), y + dpToPx(4), textPaint);
            }

            for (int i = 0; i < data.size(); i++) {
                float x = padL + chartW * i / (data.size() - 1);
                float y = padT + chartH * (data.get(i).position - 1) / 21f;
                canvas.drawCircle(x, y, dpToPx(8), pointPaint);
                canvas.drawText(String.valueOf(data.get(i).year), x - dpToPx(12), h - dpToPx(10), textPaint);
            }
        }

        private int dpToPx(int dp) {
            return (int) (dp * getResources().getDisplayMetrics().density);
        }
    }
}
