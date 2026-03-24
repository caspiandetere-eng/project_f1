package com.example.project_f1;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

public class RaceCenterActivity extends BaseActivity {

    private LinearLayout racesContainer;
    private Call<JolpicaScheduleResponse> scheduleCall;
    private ThemeManager.TeamTheme currentTheme;
    private SwipeRefreshLayout swipeRefresh;
    private static final DateTimeFormatter IN_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("dd MMM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_center);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        racesContainer = findViewById(R.id.racesContainer);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        
        // Tint back button and accent views
        View stripe = findViewById(R.id.topStripe);
        if (stripe != null) stripe.setBackgroundColor(theme.accent);
        android.widget.ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setBackgroundTintList(
                    ColorStateList.valueOf(theme.buttonBg));
            btnBack.setOnClickListener(v -> finish());
        }
        
        swipeRefresh.setOnRefreshListener(this::loadSchedule);
        swipeRefresh.setColorSchemeColors(theme.accent, 0xFFFFFFFF);
        
        // Store theme for card building
        this.currentTheme = theme;
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
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JolpicaScheduleResponse> call, Throwable t) {
                showError();
                swipeRefresh.setRefreshing(false);
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
            card.setTranslationY(dp(20));
            racesContainer.addView(card);
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(i * 50L)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
        }
    }

    private MaterialCardView buildRaceCard(Race race, boolean isNext) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(cardParams);
        
        int accentColor = currentTheme != null ? currentTheme.accent : 0xFFE10600;
        int bgColor = currentTheme != null ? currentTheme.cardBg : 0xFF1A1A1A;
        
        card.setCardBackgroundColor(ThemeManager.blendColors(bgColor, isNext ? accentColor : bgColor, isNext ? 0.05f : 0f));
        card.setRadius(dp(20));
        card.setCardElevation(isNext ? dp(12) : dp(4));
        card.setStrokeColor(isNext ? accentColor : 0xFF333333);
        card.setStrokeWidth(isNext ? dp(2) : dp(1));
        
        card.setOnClickListener(v -> navigateToRaceDetails(race));

        // Horizontal layout with 3 main sections: Date/Round, Info, Circuit
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.HORIZONTAL);
        inner.setGravity(Gravity.CENTER_VERTICAL);
        inner.setPadding(dp(16), dp(18), dp(16), dp(18));
        card.addView(inner);

        // Section 1: Date & Round
        LinearLayout dateCol = new LinearLayout(this);
        dateCol.setOrientation(LinearLayout.VERTICAL);
        dateCol.setGravity(Gravity.CENTER);
        dateCol.setLayoutParams(new LinearLayout.LayoutParams(dp(65), LinearLayout.LayoutParams.WRAP_CONTENT));
        
        TextView tvMonth = new TextView(this);
        TextView tvDay = new TextView(this);
        try {
            LocalDate d = LocalDate.parse(race.date, IN_FMT);
            tvMonth.setText(d.format(DateTimeFormatter.ofPattern("MMM")).toUpperCase());
            tvDay.setText(d.format(DateTimeFormatter.ofPattern("dd")));
        } catch (Exception e) {
            tvDay.setText("??");
        }
        
        tvMonth.setTextColor(isNext ? accentColor : 0xFF999999);
        tvMonth.setTextSize(11);
        tvMonth.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        tvMonth.setLetterSpacing(0.1f);
        dateCol.addView(tvMonth);

        tvDay.setTextColor(0xFFFFFFFF);
        tvDay.setTextSize(24);
        tvDay.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        dateCol.addView(tvDay);

        TextView tvRound = new TextView(this);
        tvRound.setText("ROUND " + race.round);
        tvRound.setTextColor(isNext ? accentColor : 0xFF555555);
        tvRound.setTextSize(9);
        tvRound.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono), Typeface.BOLD);
        tvRound.setPadding(0, dp(2), 0, 0);
        dateCol.addView(tvRound);
        
        inner.addView(dateCol);

        // Subtle vertical divider
        View sep = new View(this);
        LinearLayout.LayoutParams sepP = new LinearLayout.LayoutParams(dp(1), dp(45));
        sepP.setMargins(dp(12), 0, dp(16), 0);
        sep.setBackgroundColor(0xFF333333);
        sep.setLayoutParams(sepP);
        inner.addView(sep);

        // Section 2: Info column
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        inner.addView(info);

        TextView tvName = new TextView(this);
        tvName.setText(race.raceName.toUpperCase().replace(" GRAND PRIX", ""));
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(16);
        tvName.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        tvName.setLetterSpacing(0.02f);
        info.addView(tvName);

        if (race.circuit != null && race.circuit.location != null) {
            TextView tvLocation = new TextView(this);
            tvLocation.setText(race.circuit.location.locality + ", " + race.circuit.location.country);
            tvLocation.setTextColor(0xFF888888);
            tvLocation.setTextSize(12);
            tvLocation.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.NORMAL);
            tvLocation.setPadding(0, dp(1), 0, 0);
            info.addView(tvLocation);
        }

        // Sprint indicator if applicable
        if (race.sprint != null) {
            TextView sprintTag = new TextView(this);
            sprintTag.setText("SPRINT WEEKEND");
            sprintTag.setTextColor(0xFF00FFCC);
            sprintTag.setTextSize(9);
            sprintTag.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            sprintTag.setLetterSpacing(0.1f);
            sprintTag.setPadding(0, dp(4), 0, 0);
            info.addView(sprintTag);
        }

        // Section 3: Circuit Outline
        String country = (race.circuit != null && race.circuit.location != null)
                ? race.circuit.location.country : null;
        String locality = (race.circuit != null && race.circuit.location != null)
                ? race.circuit.location.locality : null;
        Integer circuitRes = CircuitAssets.getCircuitDrawable(locality);
        if (circuitRes == null) circuitRes = CircuitAssets.getCircuitDrawable(country);
        
        if (circuitRes != null) {
            FrameLayout circuitFrame = new FrameLayout(this);
            LinearLayout.LayoutParams cfp = new LinearLayout.LayoutParams(dp(65), dp(65));
            cfp.setMargins(dp(8), 0, 0, 0);
            circuitFrame.setLayoutParams(cfp);
            
            ImageView ivCircuit = new ImageView(this);
            ivCircuit.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ivCircuit.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivCircuit.setImageResource(circuitRes);
            ivCircuit.setAlpha(isNext ? 1.0f : 0.4f);
            
            // Neon glow for the next race circuit
            if (isNext) {
                ivCircuit.setColorFilter(accentColor);
            } else {
                ivCircuit.setColorFilter(0xFFAAAAAA);
            }
            
            circuitFrame.addView(ivCircuit);
            inner.addView(circuitFrame);
        }

        return card;
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

    private void navigateToRaceDetails(Race race) {
        Intent intent = new Intent(this, RaceDetailsActivity.class);
        intent.putExtra("raceName", race.raceName != null ? race.raceName : "Race");
        intent.putExtra("raceDate", race.date != null ? race.date : "");
        String location = "";
        String circuitName = "";
        if (race.circuit != null && race.circuit.location != null) {
            location = (race.circuit.location.locality != null ? race.circuit.location.locality : "") + ", " +
                      (race.circuit.location.country != null ? race.circuit.location.country : "");
            circuitName = race.circuit.circuitName != null ? race.circuit.circuitName : "";
        }
        intent.putExtra("location", location);
        intent.putExtra("circuitName", circuitName);
        String locality = race.circuit != null && race.circuit.location != null ? race.circuit.location.locality : "";
        String country  = race.circuit != null && race.circuit.location != null ? race.circuit.location.country  : "";
        intent.putExtra("locality", locality);
        intent.putExtra("country", country);
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle());
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
