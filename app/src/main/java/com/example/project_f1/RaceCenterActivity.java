package com.example.project_f1;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.models.JolpicaScheduleResponse;
import com.example.project_f1.models.JolpicaScheduleResponse.Race;
import com.google.android.material.card.MaterialCardView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaceCenterActivity extends AppCompatActivity {

    private LinearLayout racesContainer;
    private Call<JolpicaScheduleResponse> scheduleCall;
    private static final DateTimeFormatter IN_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("MMM dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_race_center);
        ThemeManager.applyStatusBar(this);
        racesContainer = findViewById(R.id.racesContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        loadSchedule();
    }

    private void loadSchedule() {
        scheduleCall = JolpicaApiClient.getApiService().getRaceSchedule(2026);
        scheduleCall.enqueue(new Callback<JolpicaScheduleResponse>() {
            @Override
            public void onResponse(Call<JolpicaScheduleResponse> call, Response<JolpicaScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().mrData.raceTable.races != null) {
                    showUpcoming(response.body().mrData.raceTable.races);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<JolpicaScheduleResponse> call, Throwable t) {
                showError();
            }
        });
    }

    private void showUpcoming(List<Race> all) {
        LocalDate today = LocalDate.now();
        List<Race> upcoming = new ArrayList<>();
        for (Race r : all) {
            try {
                if (!LocalDate.parse(r.date, IN_FMT).isBefore(today)) upcoming.add(r);
            } catch (Exception ignored) {}
        }

        racesContainer.removeAllViews();

        if (upcoming.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No upcoming races this season.");
            tv.setTextColor(0xFF999999);
            tv.setTextSize(14);
            tv.setPadding(dp(8), dp(8), dp(8), dp(8));
            racesContainer.addView(tv);
            return;
        }

        for (int i = 0; i < upcoming.size(); i++) {
            MaterialCardView card = buildRaceCard(upcoming.get(i), i == 0);
            card.setAlpha(0f);
            racesContainer.addView(card);
            card.animate().alpha(1f).setDuration(300).setStartDelay(i * 60L).start();
        }
    }

    private MaterialCardView buildRaceCard(Race race, boolean isNext) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(0xFF1A1A1A);
        card.setRadius(dp(16));
        card.setCardElevation(dp(8));
        card.setStrokeColor(isNext ? 0xFFE10600 : 0x33E10600);
        card.setStrokeWidth(isNext ? dp(2) : dp(1));

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.HORIZONTAL);
        inner.setGravity(Gravity.CENTER_VERTICAL);
        inner.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.addView(inner);

        // Round badge
        TextView tvRound = new TextView(this);
        tvRound.setText("R" + race.round);
        tvRound.setTextColor(isNext ? 0xFFE10600 : 0xFF666666);
        tvRound.setTextSize(13);
        tvRound.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        tvRound.setGravity(Gravity.CENTER);
        tvRound.setMinWidth(dp(40));
        inner.addView(tvRound);

        // Divider
        View divider = new View(this);
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(dp(2), dp(40));
        divParams.setMargins(dp(12), 0, dp(12), 0);
        divider.setBackgroundColor(isNext ? 0xFFE10600 : 0xFF333333);
        divider.setLayoutParams(divParams);
        inner.addView(divider);

        // Info column
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        inner.addView(info);

        TextView tvName = new TextView(this);
        tvName.setText(race.raceName);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(15);
        tvName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), android.graphics.Typeface.BOLD);
        info.addView(tvName);

        if (race.circuit != null && race.circuit.location != null) {
            TextView tvLocation = new TextView(this);
            tvLocation.setText(race.circuit.location.locality + ", " + race.circuit.location.country);
            tvLocation.setTextColor(0xFF999999);
            tvLocation.setTextSize(13);
            tvLocation.setTypeface(ResourcesCompat.getFont(this, R.font.inter), android.graphics.Typeface.NORMAL);
            info.addView(tvLocation);
        }

        // Session pills row
        LinearLayout pills = new LinearLayout(this);
        pills.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams pillsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pillsParams.setMargins(0, dp(6), 0, 0);
        pills.setLayoutParams(pillsParams);
        info.addView(pills);

        addSessionPill(pills, "FP1", race.firstPractice);
        if (race.sprint != null) {
            addSessionPill(pills, "SPR", race.sprint);
        } else {
            addSessionPill(pills, "FP2", race.secondPractice);
            addSessionPill(pills, "FP3", race.thirdPractice);
        }
        addSessionPill(pills, "QUAL", race.qualifying);

        // Circuit outline — left of date
        String country = (race.circuit != null && race.circuit.location != null)
                ? race.circuit.location.country : null;
        String locality = (race.circuit != null && race.circuit.location != null)
                ? race.circuit.location.locality : null;
        Integer circuitRes = CircuitAssets.getCircuitDrawable(locality);
        if (circuitRes == null) circuitRes = CircuitAssets.getCircuitDrawable(country);
        if (circuitRes != null) {
            ImageView ivCircuit = new ImageView(this);
            LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(dp(56), dp(56));
            ivParams.setMargins(dp(8), 0, dp(8), 0);
            ivCircuit.setLayoutParams(ivParams);
            ivCircuit.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivCircuit.setAdjustViewBounds(true);
            ivCircuit.setImageResource(circuitRes);
            inner.addView(ivCircuit);
        }

        // Date
        TextView tvDate = new TextView(this);
        try {
            tvDate.setText(LocalDate.parse(race.date, IN_FMT).format(OUT_FMT));
        } catch (Exception e) {
            tvDate.setText(race.date);
        }
        tvDate.setTextColor(isNext ? 0xFFE10600 : 0xFF999999);
        tvDate.setTextSize(14);
        tvDate.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono), android.graphics.Typeface.NORMAL);
        tvDate.setGravity(Gravity.END);
        inner.addView(tvDate);

        return card;
    }

    private void addSessionPill(LinearLayout parent, String label, JolpicaScheduleResponse.Session session) {
        if (session == null) return;
        TextView pill = new TextView(this);
        String dateStr = "";
        try {
            dateStr = " " + LocalDate.parse(session.date, IN_FMT).format(OUT_FMT);
        } catch (Exception ignored) {}
        pill.setText(label + dateStr);
        pill.setTextColor(0xFF999999);
        pill.setTextSize(11);
        pill.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono), android.graphics.Typeface.NORMAL);
        pill.setBackgroundColor(0xFF2A2A2A);
        pill.setPadding(dp(6), dp(2), dp(6), dp(2));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, dp(6), 0);
        pill.setLayoutParams(p);
        parent.addView(pill);
    }

    private void showError() {
        racesContainer.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText("Failed to load schedule. Check your connection.");
        tv.setTextColor(0xFF999999);
        tv.setTextSize(14);
        tv.setPadding(dp(8), dp(8), dp(8), dp(8));
        racesContainer.addView(tv);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduleCall != null) scheduleCall.cancel();
    }
}
