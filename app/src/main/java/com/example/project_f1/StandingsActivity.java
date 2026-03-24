package com.example.project_f1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.models.JolpicaStandingsResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StandingsActivity extends BaseActivity {
    private LinearLayout standingsContainer;
    private TextView tvSelectedYear;
    private int selectedYear = 2026;
    private Call<JolpicaStandingsResponse> currentCall;
    private ThemeManager.TeamTheme currentTheme;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_standings);
        standingsContainer = findViewById(R.id.standingsContainer);
        tvSelectedYear = findViewById(R.id.tvSelectedYear);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        applyStandingsTheme(theme);
        Button btn1950s = findViewById(R.id.btn1950s);
        Button btn2000s = findViewById(R.id.btn2000s);
        Button btnCurrent = findViewById(R.id.btnCurrent);
        
        if (standingsContainer == null || tvSelectedYear == null) {
            finish();
            return;
        }
        
        btn1950s.setOnClickListener(v -> showYearPicker(1950, 2000));
        btn2000s.setOnClickListener(v -> showYearPicker(2000, 2025));
        btnCurrent.setOnClickListener(v -> loadYear(2026));
        
        swipeRefresh = findViewById(R.id.swipeRefresh);
        setupSwipeRefresh(swipeRefresh, currentTheme.accent, () -> loadYear(selectedYear));
        loadYear(2026);
    }

    private void applyStandingsTheme(ThemeManager.TeamTheme theme) {
        this.currentTheme = theme;
        // Background
        View root = ((android.view.ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{theme.bgTop, theme.bgBottom});
        root.setBackground(bg);
        // Accent views
        View stripe = findViewById(R.id.topStripe);
        if (stripe != null) stripe.setBackgroundColor(theme.accent);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        // Year label
        if (tvSelectedYear != null) tvSelectedYear.setTextColor(theme.accent);
        // Era buttons
        int[] btnIds = {R.id.btn1950s, R.id.btn2000s, R.id.btnCurrent};
        for (int id : btnIds) {
            com.google.android.material.button.MaterialButton btn = findViewById(id);
            if (btn != null) btn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(theme.buttonBg));
        }
        // Back button
        com.google.android.material.button.MaterialButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(theme.buttonBg));
    }

    private void showYearPicker(int startYear, int endYear) {
        List<String> yearList = new ArrayList<>();
        for (int i = endYear; i >= startYear; i--) {
            yearList.add(String.valueOf(i));
        }
        String[] years = yearList.toArray(new String[0]);
        
        new AlertDialog.Builder(this)
            .setTitle("Select Year")
            .setItems(years, (dialog, which) -> loadYear(endYear - which))
            .show();
    }

    private void loadYear(int year) {
        if (currentCall != null) currentCall.cancel();
        selectedYear = year;
        tvSelectedYear.setText(year + " SEASON");
        standingsContainer.removeAllViews();
        addLoadingText();
        loadHistoricalStandings(year);
    }



    private void createDriverRow(int position, String name, String points, String wins) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        row.setBackgroundResource(position == 1 ? R.drawable.bg_podium_gold
            : position == 2 ? R.drawable.bg_podium_silver
            : position == 3 ? R.drawable.bg_podium_bronze
            : R.drawable.bg_glass_card);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(params);

        // Position badge
        TextView tvPos = new TextView(this);
        tvPos.setText(String.valueOf(position));
        tvPos.setTextColor(0xFFFFFFFF);
        tvPos.setTextSize(15);
        tvPos.setGravity(android.view.Gravity.CENTER);
        tvPos.setWidth(dp(32));
        tvPos.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        tvPos.setBackgroundResource(position == 1 ? R.drawable.gradient_red : R.drawable.bg_position_circle);
        if (currentTheme != null) {
            android.graphics.drawable.GradientDrawable posCircle = new android.graphics.drawable.GradientDrawable();
            posCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            posCircle.setColor(position == 1 ? currentTheme.accent
                    : ThemeManager.blendColors(currentTheme.cardBg, currentTheme.accent, 0.45f));
            tvPos.setBackground(posCircle);
        }
        row.addView(tvPos);

        // Name
        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(14);
        tvName.setTypeface(ResourcesCompat.getFont(this, R.font.inter));
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameParams.setMargins(dp(12), 0, dp(8), 0);
        tvName.setLayoutParams(nameParams);
        row.addView(tvName);

        // Wins
        if (wins != null && !wins.equals("0") && !wins.isEmpty()) {
            TextView tvWins = new TextView(this);
            tvWins.setText(wins + "W");
            tvWins.setTextColor(0xFFFFD700);
            tvWins.setTextSize(12);
            tvWins.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams wParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            wParams.setMargins(0, 0, dp(10), 0);
            tvWins.setLayoutParams(wParams);
            row.addView(tvWins);
        }

        // Points
        TextView tvPts = new TextView(this);
        tvPts.setText(points + " pts");
        tvPts.setTextColor(currentTheme != null ? currentTheme.accent : 0xFFE10600);
        tvPts.setTextSize(14);
        tvPts.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        row.addView(tvPts);

        standingsContainer.addView(row);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void addLoadingText() {
        TextView loading = new TextView(this);
        loading.setText("Loading standings...");
        loading.setTextColor(0xFF999999);
        loading.setTextSize(14);
        loading.setPadding(40, 30, 40, 30);
        standingsContainer.addView(loading);
    }

    private void showFallbackStandings() {
        standingsContainer.removeAllViews();
        TextView error = new TextView(this);
        error.setText("No data available for " + selectedYear);
        error.setTextColor(0xFF999999);
        error.setTextSize(14);
        error.setPadding(40, 30, 40, 30);
        standingsContainer.addView(error);
    }

    private static final long STALE_MS = 60 * 60 * 1000; // 1 hour

    private void loadHistoricalStandings(int year) {
        List<UserRepository.StandingRow> cached = UserRepository.getStandings(this, year);
        boolean isStale = UserRepository.isStandingsStale(this, year, STALE_MS);

        if (!cached.isEmpty()) {
            displayFromDb(cached);
            if (!isStale) return; // fresh — skip network
        }

        currentCall = JolpicaApiClient.getApiService().getDriverStandings(year);
        currentCall.enqueue(new Callback<JolpicaStandingsResponse>() {
            @Override
            public void onResponse(Call<JolpicaStandingsResponse> call, Response<JolpicaStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        !response.body().mrData.standingsTable.standingsLists.isEmpty()) {
                    UserRepository.saveStandings(StandingsActivity.this, year, response.body());
                    displayFromDb(UserRepository.getStandings(StandingsActivity.this, year));
                } else if (cached.isEmpty()) {
                    showFallbackStandings();
                }
            }

            @Override
            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {
                if (cached.isEmpty()) showFallbackStandings();
            }
        });
    }

    private void displayFromDb(List<UserRepository.StandingRow> rows) {
        standingsContainer.removeAllViews();
        for (UserRepository.StandingRow row : rows) {
            String name = (row.givenName != null ? row.givenName.charAt(0) + ". " : "") +
                (row.familyName != null ? row.familyName : row.driverId);
            String constructor = row.constructor != null && !row.constructor.isEmpty()
                ? "  " + row.constructor : "";
            createDriverRow(row.position, name + constructor, row.points, row.wins);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null) currentCall.cancel();
    }
}
