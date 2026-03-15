package com.example.project_f1;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.models.JolpicaStandingsResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StandingsActivity extends AppCompatActivity {
    private LinearLayout standingsContainer;
    private TextView tvSelectedYear;
    private int selectedYear = 2026;
    private Call<JolpicaStandingsResponse> currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_standings);
        standingsContainer = findViewById(R.id.standingsContainer);
        tvSelectedYear = findViewById(R.id.tvSelectedYear);
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
        loadYear(2026);
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



    private void createDriverRow(int position, String name, String points) {
        TextView row = new TextView(this);
        row.setText(position + "  " + name + "  " + points + " pts");
        row.setTextColor(0xFFFFFFFF);
        row.setTextSize(16);
        row.setPadding(40, 30, 40, 30);
        row.setBackgroundColor(position <= 3 ? 0xFF1A1A1A : 0xFF0D0D0D);
        row.setShadowLayer(3, 1, 1, 0x80000000);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 10);
        row.setLayoutParams(params);
        standingsContainer.addView(row);
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

    private void loadHistoricalStandings(int year) {
        currentCall = JolpicaApiClient.getApiService().getDriverStandings(year);
        currentCall.enqueue(new Callback<JolpicaStandingsResponse>() {
            @Override
            public void onResponse(Call<JolpicaStandingsResponse> call, Response<JolpicaStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null && 
                    !response.body().mrData.standingsTable.standingsLists.isEmpty()) {
                    displayHistoricalStandings(response.body());
                } else {
                    showFallbackStandings();
                }
            }

            @Override
            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {
                showFallbackStandings();
            }
        });
    }

    private void displayHistoricalStandings(JolpicaStandingsResponse standings) {
        standingsContainer.removeAllViews();
        try {
            for (JolpicaStandingsResponse.DriverStanding driver : standings.mrData.standingsTable.standingsLists.get(0).driverStandings) {
                String name = "Unknown";
                if (driver.driver != null) {
                    if (driver.driver.givenName != null && !driver.driver.givenName.isEmpty() && driver.driver.familyName != null) {
                        name = driver.driver.givenName.charAt(0) + ". " + driver.driver.familyName;
                    } else if (driver.driver.familyName != null) {
                        name = driver.driver.familyName;
                    }
                }
                createDriverRow(
                    driver.position != null ? Integer.parseInt(driver.position) : 0,
                    name,
                    driver.points != null ? driver.points : "0"
                );
            }
        } catch (Exception e) {
            showFallbackStandings();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null) currentCall.cancel();
    }
}
