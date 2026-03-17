package com.example.project_f1;

import android.app.ActivityOptions;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.api.OpenF1ApiClient;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.OpenF1Session;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvRaceName, tvViewStandings, tvCurrentLap;
    private LinearLayout topStandingsContainer;
    private MaterialCardView cardHistory, cardTech, cardStandings, cardLatestRace, cardLapDetails, cardF1Impact;
    private DrawerLayout drawerLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Call<List<OpenF1Session>> sessionCall;

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
        ThemeManager.applyStatusBar(this);
        initViews();
        setupDrawer(prefs);
        setClickListeners();
        loadLatestSession();
        showCoachMarksIfNeeded();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        tvRaceName = findViewById(R.id.tvRaceName);
        tvViewStandings = findViewById(R.id.tvViewStandings);
        tvCurrentLap = findViewById(R.id.tvCurrentLap);
        topStandingsContainer = findViewById(R.id.topStandingsContainer);
        cardHistory = findViewById(R.id.cardHistory);
        cardTech = findViewById(R.id.cardTech);
        cardStandings = findViewById(R.id.cardStandings);
        cardLatestRace = findViewById(R.id.cardLatestRace);
        cardLapDetails = findViewById(R.id.cardLapDetails);
        cardF1Impact = findViewById(R.id.cardF1Impact);
    }

    private void setupDrawer(SharedPreferences prefs) {
        NavigationView navView = findViewById(R.id.navView);

        // Populate header with user info
        View header = navView.getHeaderView(0);
        TextView tvName = header.findViewById(R.id.tvHeaderName);
        TextView tvEmail = header.findViewById(R.id.tvHeaderEmail);
        if (tvName != null) tvName.setText(prefs.getString("user_name", "Driver"));
        if (tvEmail != null) tvEmail.setText(prefs.getString("user_email", ""));

        // Hamburger opens drawer
        findViewById(R.id.btnMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // already home
            } else if (id == R.id.nav_live_race) {
                navigate(new Intent(this, LiveRaceActivity.class));
            } else if (id == R.id.nav_standings) {
                navigate(new Intent(this, StandingsActivity.class));
            } else if (id == R.id.nav_calendar) {
                navigate(new Intent(this, RaceCenterActivity.class));
            } else if (id == R.id.nav_tech) {
                navigate(new Intent(this, TechAnalysisActivity.class));
            } else if (id == R.id.nav_history) {
                navigate(new Intent(this, F1HistoryInteractiveActivity.class));
            } else if (id == R.id.nav_impact) {
                navigate(new Intent(this, F1ImpactActivity.class));
            } else if (id == R.id.nav_settings) {
                navigate(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_logout) {
                prefs.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        });
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
                    prefs.edit().putBoolean("coach_marks_done", true).apply());
        });
    }

    private void setClickListeners() {
        cardHistory.setOnClickListener(v -> animateThen(v, new Intent(this, F1HistoryInteractiveActivity.class)));
        cardTech.setOnClickListener(v -> animateThen(v, new Intent(this, TechAnalysisActivity.class)));
        cardStandings.setOnClickListener(v -> animateThen(v, new Intent(this, StandingsActivity.class)));
        tvViewStandings.setOnClickListener(v -> navigate(new Intent(this, StandingsActivity.class)));
        cardLatestRace.setOnClickListener(v -> animateThen(v, new Intent(this, RaceCenterActivity.class)));
        cardLapDetails.setOnClickListener(v -> animateThen(v, new Intent(this, LapDetailsActivity.class)));
        cardF1Impact.setOnClickListener(v -> animateThen(v, new Intent(this, F1ImpactActivity.class)));
        animateCards();
    }

    private void animateThen(View v, Intent intent) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            v.animate().scaleX(1f).scaleY(1f).setDuration(100);
            navigate(intent);
        });
    }

    private void navigate(Intent intent) {
        startActivity(intent,
                ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.fade_in).toBundle());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initDriverNames() {}

    private void loadLatestSession() {
        sessionCall = OpenF1ApiClient.getApiService().getSessions(2026, "Race");
        sessionCall.enqueue(new Callback<List<OpenF1Session>>() {
            @Override
            public void onResponse(Call<List<OpenF1Session>> call, Response<List<OpenF1Session>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    OpenF1Session upcomingSession = findUpcomingSession(response.body());
                    if (upcomingSession != null) {
                        String raceName = upcomingSession.circuitShortName != null ?
                                upcomingSession.circuitShortName.toUpperCase() + " GP" : "RACE";
                        tvRaceName.setText(raceName);
                        if (upcomingSession.dateStart != null)
                            tvCurrentLap.setText(formatRaceDate(upcomingSession.dateStart));
                    } else {
                        tvRaceName.setText("NO UPCOMING RACE");
                    }
                } else {
                    tvRaceName.setText("LOADING RACE...");
                }
                loadTopStandings();
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
            return java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy").format(zdt);
        } catch (Exception e) {
            return "Race Date TBA";
        }
    }

    private OpenF1Session findUpcomingSession(List<OpenF1Session> sessions) {
        long now = System.currentTimeMillis();
        for (OpenF1Session s : sessions) {
            try {
                if (s.dateStart != null && java.time.Instant.parse(s.dateStart).toEpochMilli() > now)
                    return s;
            } catch (Exception ignored) {}
        }
        return sessions.get(sessions.size() - 1);
    }

    private void startLiveUpdates() {}

    private void loadTopStandings() {
        List<UserRepository.StandingRow> cached = UserRepository.getStandings(this, 2026);
        if (!cached.isEmpty()) {
            updateTopStandingsFromDb(cached);
            if (!UserRepository.isStandingsStale(this, 2026, 60 * 60 * 1000L)) return;
        }
        JolpicaApiClient.getApiService().getDriverStandings(2026).enqueue(new Callback<JolpicaStandingsResponse>() {
            @Override
            public void onResponse(Call<JolpicaStandingsResponse> call, Response<JolpicaStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        !response.body().mrData.standingsTable.standingsLists.isEmpty()) {
                    UserRepository.saveStandings(MainActivity.this, 2026, response.body());
                    updateTopStandingsFromDb(UserRepository.getStandings(MainActivity.this, 2026));
                }
            }
            @Override
            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {}
        });
    }

    private void updateTopStandingsFromDb(List<UserRepository.StandingRow> rows) {
        if (topStandingsContainer == null) return;
        topStandingsContainer.removeAllViews();
        int max = KnowledgeLevelManager.isRookie(this) ? 2 : 3;
        for (int i = 0; i < Math.min(max, rows.size()); i++) {
            UserRepository.StandingRow r = rows.get(i);
            LinearLayout row = createStandingRow(r);
            row.setAlpha(0f);
            topStandingsContainer.addView(row);
            row.animate().alpha(1f).setDuration(300).setStartDelay(i * 100);
        }
    }

    private void animateCards() {
        MaterialCardView[] cards = {cardLatestRace, cardStandings, cardHistory, cardTech, cardLapDetails, cardF1Impact};
        for (int i = 0; i < cards.length; i++) {
            cards[i].setAlpha(0f);
            cards[i].animate().alpha(1f).setDuration(400).setStartDelay(100 + i * 50L);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (sessionCall != null) sessionCall.cancel();
    }

    private void loadTopStandingsLegacy() {}
    private void loadStandingsFromApi(String k) {}
    private void updateTopStandings(JolpicaStandingsResponse s) {}

    private LinearLayout createStandingRow(UserRepository.StandingRow r) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        int pos = r.position;
        row.setBackgroundResource(pos == 1 ? R.drawable.bg_podium_gold
                : pos == 2 ? R.drawable.bg_podium_silver
                : pos == 3 ? R.drawable.bg_podium_bronze : R.drawable.bg_glass_card);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (pos > 1) params.setMargins(0, dpToPx(8), 0, 0);
        row.setLayoutParams(params);

        TextView posText = new TextView(this);
        posText.setText(String.valueOf(pos));
        posText.setTextColor(0xFFFFFFFF);
        posText.setTextSize(16);
        posText.setGravity(android.view.Gravity.CENTER);
        posText.setWidth(dpToPx(32));
        posText.setHeight(dpToPx(32));
        posText.setBackgroundResource(pos == 1 ? R.drawable.gradient_red : R.drawable.bg_position_circle);
        posText.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        row.addView(posText);

        View bar = new View(this);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(3), LinearLayout.LayoutParams.MATCH_PARENT);
        barParams.setMargins(dpToPx(12), 0, dpToPx(12), 0);
        bar.setBackgroundColor(pos == 1 ? 0xFFFFD700 : pos == 2 ? 0xFFC0C0C0 : 0xFFCD7F32);
        bar.setLayoutParams(barParams);
        row.addView(bar);

        TextView nameText = new TextView(this);
        nameText.setText((r.givenName != null ? r.givenName.charAt(0) + ". " : "") +
                (r.familyName != null ? r.familyName : r.driverId));
        nameText.setTextColor(0xFFFFFFFF);
        nameText.setTextSize(15);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(nameText);

        TextView ptsText = new TextView(this);
        ptsText.setText(r.points);
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
