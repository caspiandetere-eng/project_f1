package com.example.project_f1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.api.OpenF1ApiClient;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.OpenF1Position;
import com.example.project_f1.models.OpenF1Session;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvRaceName, tvViewStandings, tvCurrentLap;
    private LinearLayout livePositionsContainer, topStandingsContainer;
    private MaterialCardView cardHistory, cardTech, cardStandings, cardLatestRace, cardLapDetails, cardF1Impact;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int sessionKey = -1;
    private Map<Integer, String> driverNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        initViews();
        setClickListeners();
        initDriverNames();
        loadLatestSession();
        showCoachMarksIfNeeded();
    }

    private void initViews() {
        tvRaceName = findViewById(R.id.tvRaceName);
        tvViewStandings = findViewById(R.id.tvViewStandings);
        tvCurrentLap = findViewById(R.id.tvCurrentLap);
        livePositionsContainer = findViewById(R.id.livePositionsContainer);
        topStandingsContainer = findViewById(R.id.topStandingsContainer);
        cardHistory = findViewById(R.id.cardHistory);
        cardTech = findViewById(R.id.cardTech);
        cardStandings = findViewById(R.id.cardStandings);
        cardLatestRace = findViewById(R.id.cardLatestRace);
        cardLapDetails = findViewById(R.id.cardLapDetails);
        cardF1Impact = findViewById(R.id.cardF1Impact);
    }

    private void showCoachMarksIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        if (prefs.getBoolean("coach_marks_done", false)) return;

        KnowledgeLevelManager.KnowledgeLevel level = KnowledgeLevelManager.getKnowledgeLevel(this);
        if (level != KnowledgeLevelManager.KnowledgeLevel.ROOKIE &&
            level != KnowledgeLevelManager.KnowledgeLevel.CASUAL) return;

        cardLatestRace.post(() -> {
            CoachMarkOverlay.CoachMark[] marks = {
                new CoachMarkOverlay.CoachMark(cardLatestRace, "Upcoming Race", "See the next race on the calendar and tap to explore session details."),
                new CoachMarkOverlay.CoachMark(cardStandings, "Driver Standings", "Check who's leading the championship across different eras."),
                new CoachMarkOverlay.CoachMark(cardHistory, "F1 History", "Explore iconic moments and legendary drivers from F1's past."),
            };
            new CoachMarkOverlay(this).show(this, marks, () ->
                prefs.edit().putBoolean("coach_marks_done", true).apply()
            );
        });
    }

    private void setClickListeners() {
        findViewById(R.id.btnMenu).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        cardHistory.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                startActivity(new Intent(this, F1HistoryInteractiveActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
            });
        });
        cardTech.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                startActivity(new Intent(this, TechAnalysisActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
            });
        });
        cardStandings.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                startActivity(new Intent(this, StandingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
            });
        });
        tvViewStandings.setOnClickListener(v -> {
            startActivity(new Intent(this, StandingsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
        });
        cardLatestRace.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                startActivity(new Intent(this, LiveRaceActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
            });
        });
        cardLapDetails.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                startActivity(new Intent(this, LapDetailsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
            });
        });
        cardF1Impact.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                startActivity(new Intent(this, F1ImpactActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_in);
            });
        });
        animateCards();
    }


    private void initDriverNames() {
        driverNames.put(1, "M. Verstappen");
        driverNames.put(11, "S. Perez");
        driverNames.put(44, "L. Hamilton");
        driverNames.put(63, "G. Russell");
        driverNames.put(16, "C. Leclerc");
        driverNames.put(55, "C. Sainz");
        driverNames.put(4, "L. Norris");
        driverNames.put(81, "O. Piastri");
        driverNames.put(14, "F. Alonso");
        driverNames.put(18, "L. Stroll");
    }
    
    private void loadLatestSession() {
        OpenF1ApiClient.getApiService().getSessions(2026, "Race").enqueue(new Callback<List<OpenF1Session>>() {
            @Override
            public void onResponse(Call<List<OpenF1Session>> call, Response<List<OpenF1Session>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<OpenF1Session> sessions = response.body();
                    OpenF1Session upcomingSession = findUpcomingSession(sessions);
                    
                    if (upcomingSession != null) {
                        sessionKey = upcomingSession.sessionKey;
                        String raceName = upcomingSession.circuitShortName != null ? 
                            upcomingSession.circuitShortName.toUpperCase() + " GP" : "RACE";
                        tvRaceName.setText(raceName);
                        
                        if (upcomingSession.dateStart != null) {
                            String dateStr = formatRaceDate(upcomingSession.dateStart);
                            tvCurrentLap.setText(dateStr);
                        }
                        
                        loadTopStandings();
                    } else {
                        tvRaceName.setText("NO UPCOMING RACE");
                        loadTopStandings();
                    }
                } else {
                    tvRaceName.setText("LOADING RACE...");
                    loadTopStandings();
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Session>> call, Throwable t) {
                tvRaceName.setText("LOADING RACE...");
                loadTopStandings();
            }
        });
    }
    
    private String formatRaceDate(String dateStart) {
        try {
            java.time.Instant instant = java.time.Instant.parse(dateStart);
            java.time.ZonedDateTime zdt = instant.atZone(java.time.ZoneId.systemDefault());
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy");
            return formatter.format(zdt);
        } catch (Exception e) {
            return "Race Date TBA";
        }
    }
    
    private OpenF1Session findUpcomingSession(List<OpenF1Session> sessions) {
        if (sessions == null || sessions.isEmpty()) return null;
        
        long currentTime = System.currentTimeMillis();
        
        for (OpenF1Session session : sessions) {
            try {
                if (session.dateStart != null) {
                    long startTime = java.time.Instant.parse(session.dateStart).toEpochMilli();
                    if (startTime > currentTime) {
                        return session;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return sessions.get(sessions.size() - 1);
    }
    
    private void startLiveUpdates() {
        if (sessionKey == -1) return;
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLivePositions();
                handler.postDelayed(this, 5000);
            }
        }, 1000);
    }
    
    private void loadLivePositions() {
        OpenF1ApiClient.getApiService().getPositions(sessionKey).enqueue(new Callback<List<OpenF1Position>>() {
            @Override
            public void onResponse(Call<List<OpenF1Position>> call, Response<List<OpenF1Position>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    updateLivePositions(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Position>> call, Throwable t) {
            }
        });
    }
    
    private void updateLivePositions(List<OpenF1Position> positions) {
        livePositionsContainer.removeAllViews();
        
        Map<Integer, OpenF1Position> latestPositions = new HashMap<>();
        for (OpenF1Position pos : positions) {
            latestPositions.put(pos.driverNumber, pos);
        }
        
        List<OpenF1Position> sortedPositions = new ArrayList<>(latestPositions.values());
        sortedPositions.sort((a, b) -> Integer.compare(a.position, b.position));
        
        for (int i = 0; i < Math.min(5, sortedPositions.size()); i++) {
            OpenF1Position pos = sortedPositions.get(i);
            String driverName = driverNames.getOrDefault(pos.driverNumber, "Driver " + pos.driverNumber);
            String gap = pos.position == 1 ? "LEADER" : "+" + (pos.position * 0.5f) + "s";
            livePositionsContainer.addView(createLivePositionRow(pos.position, driverName, gap));
        }
        
        tvCurrentLap.setText("LIVE POSITIONS");
    }
    
    private TextView createLivePositionRow(int position, String name, String gap) {
        TextView row = new TextView(this);
        row.setText(position + "  " + name + "  " + gap);
        row.setTextColor(0xFFFFFFFF);
        row.setTextSize(14);
        row.setPadding(0, 10, 0, 10);
        android.graphics.Typeface typeface = ResourcesCompat.getFont(this, R.font.jetbrains_mono);
        if (typeface != null) row.setTypeface(typeface);
        return row;
    }
    
    private void animateCards() {
        cardLatestRace.setAlpha(0f);
        cardStandings.setAlpha(0f);
        cardHistory.setAlpha(0f);
        cardTech.setAlpha(0f);
        cardLapDetails.setAlpha(0f);
        cardF1Impact.setAlpha(0f);
        
        cardLatestRace.animate().alpha(1f).setDuration(400).setStartDelay(100);
        cardStandings.animate().alpha(1f).setDuration(400).setStartDelay(150);
        cardHistory.animate().alpha(1f).setDuration(400).setStartDelay(200);
        cardTech.animate().alpha(1f).setDuration(400).setStartDelay(250);
        cardLapDetails.animate().alpha(1f).setDuration(400).setStartDelay(300);
        cardF1Impact.animate().alpha(1f).setDuration(400).setStartDelay(350);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    
    private void loadTopStandings() {
        String cacheKey = "standings_2026";
        String cachedData = CacheManager.getCache(this, cacheKey);
        
        if (cachedData != null) {
            try {
                JolpicaStandingsResponse standings = new Gson().fromJson(cachedData, JolpicaStandingsResponse.class);
                updateTopStandings(standings);
            } catch (Exception e) {
                loadStandingsFromApi(cacheKey);
            }
        } else {
            loadStandingsFromApi(cacheKey);
        }
    }
    
    private void loadStandingsFromApi(String cacheKey) {
        JolpicaApiClient.getApiService().getDriverStandings(2026).enqueue(new Callback<JolpicaStandingsResponse>() {
            @Override
            public void onResponse(Call<JolpicaStandingsResponse> call, Response<JolpicaStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null && 
                    !response.body().mrData.standingsTable.standingsLists.isEmpty()) {
                    String json = new Gson().toJson(response.body());
                    CacheManager.saveCache(MainActivity.this, cacheKey, json);
                    updateTopStandings(response.body());
                }
            }

            @Override
            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {
            }
        });
    }
    
    private void updateTopStandings(JolpicaStandingsResponse standings) {
        if (topStandingsContainer == null) return;
        topStandingsContainer.removeAllViews();
        
        List<JolpicaStandingsResponse.DriverStanding> drivers = 
            standings.mrData.standingsTable.standingsLists.get(0).driverStandings;
        
        int maxDrivers = KnowledgeLevelManager.isRookie(this) ? 2 : 3;
        for (int i = 0; i < Math.min(maxDrivers, drivers.size()); i++) {
            JolpicaStandingsResponse.DriverStanding driver = drivers.get(i);
            LinearLayout row = createStandingRow(i + 1, driver);
            row.setAlpha(0f);
            topStandingsContainer.addView(row);
            row.animate().alpha(1f).setDuration(300).setStartDelay(i * 100);
        }
    }
    
    private LinearLayout createStandingRow(int position, JolpicaStandingsResponse.DriverStanding driver) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        
        int bgResource = R.drawable.bg_glass_card;
        if (position == 1) bgResource = R.drawable.bg_podium_gold;
        else if (position == 2) bgResource = R.drawable.bg_podium_silver;
        else if (position == 3) bgResource = R.drawable.bg_podium_bronze;
        
        row.setBackgroundResource(bgResource);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        if (position > 1) params.setMargins(0, dpToPx(8), 0, 0);
        row.setLayoutParams(params);
        
        TextView posText = new TextView(this);
        posText.setText(String.valueOf(position));
        boolean isDarkTheme = getSharedPreferences("F1Prefs", MODE_PRIVATE).getBoolean("dark_theme", true);
        posText.setTextColor(isDarkTheme ? 0xFFFFFFFF : 0xFF1A1A1A);
        posText.setTextSize(16);
        posText.setGravity(android.view.Gravity.CENTER);
        posText.setWidth(dpToPx(32));
        posText.setHeight(dpToPx(32));
        posText.setBackgroundResource(position == 1 ? R.drawable.gradient_red : R.drawable.bg_position_circle);
        posText.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        row.addView(posText);
        
        View bar = new View(this);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(3), LinearLayout.LayoutParams.MATCH_PARENT);
        barParams.setMargins(dpToPx(12), 0, dpToPx(12), 0);
        bar.setLayoutParams(barParams);
        int barColor = 0xFFFFD700;
        if (position == 2) barColor = 0xFFC0C0C0;
        else if (position == 3) barColor = 0xFFCD7F32;
        bar.setBackgroundColor(barColor);
        row.addView(bar);
        
        TextView nameText = new TextView(this);
        String name = driver.driver.givenName.charAt(0) + ". " + driver.driver.familyName;
        nameText.setText(name);
        nameText.setTextColor(0xFFFFFFFF);
        nameText.setTextSize(15);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameText.setLayoutParams(nameParams);
        row.addView(nameText);
        
        TextView ptsText = new TextView(this);
        ptsText.setText(driver.points);
        ptsText.setTextColor(0xFFE10600);
        ptsText.setTextSize(18);
        ptsText.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        row.addView(ptsText);
        
        return row;
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}