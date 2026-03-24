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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.api.OpenF1ApiClient;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.OpenF1Session;
import com.example.project_f1.models.Driver;
import com.example.project_f1.models.Team;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private TextView tvRaceName, tvViewStandings, tvCurrentLap, tvWelcome;
    private LinearLayout topStandingsContainer;
    private MaterialCardView cardHistory, cardTech, cardStandings, cardLatestRace, cardLapDetails, cardF1Impact, cardDrivers, cardTeams, cardBasics;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private ThemeManager.TeamTheme currentTheme;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onThemeChanged(ThemeManager.TeamTheme theme) {
        android.util.Log.d("THEME", "Home updated: " + StateManager.get().getTheme());
        currentTheme = theme;
        applyTeamAccents(theme);
        NavigationView navView = findViewById(R.id.navView);
        if (navView != null) ThemeManager.tintNavView(navView, theme);
        swipeRefresh.setColorSchemeColors(theme.accent, 0xFFFFFFFF);
        // Rebuild standing rows so their accent colors reflect the new theme
        List<UserRepository.StandingRow> rows = UserRepository.getStandings(this, 2026);
        if (!rows.isEmpty()) updateTopStandingsFromDb(rows);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (!FavoriteRepository.hasBothFavorites(this)) {
            Intent intent = new Intent(this, FavoriteSelectionActivity.class);
            intent.putExtra("from_onboarding", true);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        currentTheme = ThemeManager.applyFullTheme(this);
        initViews();
        String userName = prefs.getString("user_name", null);
        if (tvWelcome != null && userName != null)
            tvWelcome.setText("Welcome, " + userName + "!");
        applyTeamAccents(currentTheme);
        setupDrawer(prefs, currentTheme);
        setClickListeners();
        loadLatestSession();
        showCoachMarksIfNeeded();
        DataSyncManager.prefetchDriverData(this);
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvRaceName = findViewById(R.id.tvRaceName);
        tvViewStandings = findViewById(R.id.tvViewStandings);
        tvCurrentLap = findViewById(R.id.tvCurrentLap);
        tvWelcome = findViewById(R.id.tvWelcome);
        topStandingsContainer = findViewById(R.id.topStandingsContainer);
        cardHistory = findViewById(R.id.cardHistory);
        cardTech = findViewById(R.id.cardTech);
        cardStandings = findViewById(R.id.cardStandings);
        cardLatestRace = findViewById(R.id.cardLatestRace);
        cardLapDetails = findViewById(R.id.cardLapDetails);
        cardF1Impact = findViewById(R.id.cardF1Impact);
        cardDrivers = findViewById(R.id.cardDrivers);
        cardTeams = findViewById(R.id.cardTeams);
        cardBasics = findViewById(R.id.cardBasics);
        
        swipeRefresh.setOnRefreshListener(this::refreshData);
        swipeRefresh.setColorSchemeColors(currentTheme.accent, 0xFFFFFFFF);
    }

    private void applyTeamAccents(ThemeManager.TeamTheme theme) {
        // Background gradient on the main content layout
        View mainContent = findViewById(R.id.mainContentLayout);
        if (mainContent != null) {
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
                    android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{theme.bgTop, theme.bgBottom});
            mainContent.setBackground(bg);
        }
        // Top stripe
        View topStripe = findViewById(R.id.topStripe);
        if (topStripe != null) topStripe.setBackgroundColor(theme.accent);
        // Header vertical bar
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        // Cards — glass background tinted with accent
        int[] cardIds = {R.id.cardLatestRace, R.id.cardStandings, R.id.cardHistory,
                         R.id.cardTech, R.id.cardLapDetails, R.id.cardF1Impact,
                         R.id.cardDrivers, R.id.cardTeams, R.id.cardBasics};
        for (int id : cardIds) {
            MaterialCardView card = findViewById(id);
            if (card != null) {
                card.setCardBackgroundColor(ThemeManager.blendColors(0xFF0D0D0D, theme.accent, 0.07f));
                card.setStrokeColor(ThemeManager.blendColors(0xFF222222, theme.accent, 0.28f));
                card.setStrokeWidth(dpToPx(1));
            }
        }
        // Section labels & links
        int[] accentTextIds = {R.id.labelNextRace, R.id.tvViewStandings, R.id.tvLiveStatus};
        for (int id : accentTextIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(theme.accent);
        }
        // Card accent bars (small horizontal bars inside cards)
        int[] accentBarIds = {R.id.historyAccent, R.id.techAccent, R.id.lapAccent, R.id.impactAccent, R.id.driversAccent, R.id.teamsAccent, R.id.basicsAccent};
        for (int id : accentBarIds) {
            View bar = findViewById(id);
            if (bar != null) bar.setBackgroundColor(theme.accent);
        }
        // Menu button tint
        View btnMenu = findViewById(R.id.btnMenu);
        if (btnMenu != null) {
            android.graphics.drawable.GradientDrawable menuBg = new android.graphics.drawable.GradientDrawable();
            menuBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            menuBg.setColor(ThemeManager.blendColors(theme.cardBg, theme.accent, 0.3f));
            menuBg.setStroke(2, theme.accent);
            btnMenu.setBackground(menuBg);
        }
    }

    private void setupDrawer(SharedPreferences prefs, ThemeManager.TeamTheme theme) {
        NavigationView navView = findViewById(R.id.navView);
        ThemeManager.tintNavView(navView, theme);

        View header = navView.getHeaderView(0);
        TextView tvName = header.findViewById(R.id.tvHeaderName);
        TextView tvEmail = header.findViewById(R.id.tvHeaderEmail);
        if (tvName != null) tvName.setText(prefs.getString("user_name", "Driver"));
        if (tvEmail != null) tvEmail.setText(prefs.getString("user_email", ""));

        android.widget.ImageView ivDriverPhoto = header.findViewById(R.id.ivHeaderDriverPhoto);
        if (ivDriverPhoto != null) {
            ivDriverPhoto.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
            ivDriverPhoto.setClipToOutline(true);
        }

        refreshDrawerHeader();

        // Hamburger opens drawer
        findViewById(R.id.btnMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // already home
            } else if (id == R.id.nav_standings) {
                navigate(new Intent(this, StandingsActivity.class));
            } else if (id == R.id.nav_drivers) {
                navigate(new Intent(this, DriversActivity.class));
            } else if (id == R.id.nav_teams) {
                navigate(new Intent(this, TeamsActivity.class));
            } else if (id == R.id.nav_calendar) {
                navigate(new Intent(this, RaceCenterActivity.class));
            } else if (id == R.id.nav_tech) {
                navigate(new Intent(this, TechAnalysisActivity.class));
            } else if (id == R.id.nav_history) {
                navigate(new Intent(this, F1HistoryInteractiveActivity.class));
            } else if (id == R.id.nav_basics) {
                navigate(new Intent(this, F1BasicsActivity.class));
            } else if (id == R.id.nav_impact) {
                navigate(new Intent(this, F1ImpactActivity.class));
            } else if (id == R.id.nav_driver_stats) {
                navigate(new Intent(this, DriverStatsActivity.class));
            } else if (id == R.id.nav_compare) {
                navigate(new Intent(this, DriverPickerActivity.class));
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
        if (cardDrivers != null) cardDrivers.setOnClickListener(v -> animateThen(v, new Intent(this, DriversActivity.class)));
        if (cardTeams != null) cardTeams.setOnClickListener(v -> animateThen(v, new Intent(this, TeamsActivity.class)));
        if (cardBasics != null) cardBasics.setOnClickListener(v -> animateThen(v, new Intent(this, F1BasicsActivity.class)));
        animateCards();
    }

    private void animateThen(View v, Intent intent) {
        v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(120)
                .setInterpolator(new android.view.animation.AccelerateInterpolator())
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(180)
                            .setInterpolator(new android.view.animation.DecelerateInterpolator())
                            .withEndAction(() -> navigate(intent)).start()
                ).start();
    }

    private void navigate(Intent intent) {
        startActivity(intent,
                ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_right).toBundle());
    }

    private void refreshDrawerHeader() {
        NavigationView navView = findViewById(R.id.navView);
        if (navView == null) return;
        View header = navView.getHeaderView(0);
        android.widget.ImageView ivDriverPhoto = header.findViewById(R.id.ivHeaderDriverPhoto);
        android.widget.ImageView ivTeamLogo = header.findViewById(R.id.ivHeaderTeamLogo);

        if (ivDriverPhoto != null) {
            UserProfileRepository.loadInto(this, ivDriverPhoto, currentTheme.accent);
        }
        if (ivTeamLogo != null) {
            String favTeamId = FavoriteRepository.getFavoriteTeamId(this);
            if (favTeamId != null) {
                com.example.project_f1.models.Team favTeam =
                        com.example.project_f1.models.Team.getTeamById(favTeamId);
                if (favTeam != null && favTeam.logoResId != 0)
                    ivTeamLogo.setImageResource(favTeam.logoResId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDrawerHeader();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        DataSyncManager.loadSessions(this, 2026, new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
            @Override
            public void onSuccess(List<OpenF1Session> sessions) {
                if (!sessions.isEmpty()) {
                    OpenF1Session upcomingSession = findUpcomingSession(sessions);
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
                    tvRaceName.setText("NO UPCOMING RACE");
                }
                loadTopStandings();
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                tvRaceName.setText("FAILED TO LOAD");
                loadTopStandings();
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void refreshData() {
        CacheManager.clearAllCache(this);
        tvRaceName.setText("LOADING...");
        tvCurrentLap.setText("Updating data...");
        topStandingsContainer.removeAllViews();
        loadLatestSession();
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
        DataSyncManager.loadStandings(this, 2026, new DataSyncManager.SyncCallback<JolpicaStandingsResponse>() {
            @Override
            public void onSuccess(JolpicaStandingsResponse response) {
                List<UserRepository.StandingRow> standings = UserRepository.getStandings(MainActivity.this, 2026);
                if (!standings.isEmpty()) {
                    updateTopStandingsFromDb(standings);
                }
            }

            @Override
            public void onFailure(String error) {}
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
        MaterialCardView[] cards = {cardLatestRace, cardStandings, cardHistory, cardTech, cardLapDetails, cardF1Impact, cardDrivers, cardTeams, cardBasics};
        float density = getResources().getDisplayMetrics().density;
        for (int i = 0; i < cards.length; i++) {
            cards[i].setAlpha(0f);
            cards[i].setTranslationY(24 * density);
            cards[i].animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(380)
                    .setStartDelay(80 + i * 55L)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator(1.6f))
                    .start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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

        // Row background: pos 1 uses a subtle accent tint, others use card bg
        android.graphics.drawable.GradientDrawable rowBg = new android.graphics.drawable.GradientDrawable();
        rowBg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        rowBg.setCornerRadius(dpToPx(12));
        if (pos == 1) {
            rowBg.setColor(ThemeManager.blendColors(currentTheme.cardBg, currentTheme.accent, 0.22f));
            rowBg.setStroke(dpToPx(1), ThemeManager.blendColors(currentTheme.cardBg, currentTheme.accent, 0.6f));
        } else {
            rowBg.setColor(ThemeManager.blendColors(currentTheme.cardBg, 0xFF000000, 0.3f));
            rowBg.setStroke(dpToPx(1), ThemeManager.blendColors(currentTheme.cardBg, currentTheme.accent, 0.2f));
        }
        row.setBackground(rowBg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (pos > 1) params.setMargins(0, dpToPx(8), 0, 0);
        row.setLayoutParams(params);

        // Position circle — accent color for P1, dimmed accent for others
        TextView posText = new TextView(this);
        posText.setText(String.valueOf(pos));
        posText.setTextColor(0xFFFFFFFF);
        posText.setTextSize(16);
        posText.setGravity(android.view.Gravity.CENTER);
        posText.setWidth(dpToPx(32));
        posText.setHeight(dpToPx(32));
        android.graphics.drawable.GradientDrawable circleBg = new android.graphics.drawable.GradientDrawable();
        circleBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        circleBg.setColor(pos == 1 ? currentTheme.accent
                : ThemeManager.blendColors(currentTheme.cardBg, currentTheme.accent, 0.45f));
        posText.setBackground(circleBg);
        posText.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        row.addView(posText);

        // Vertical bar — accent for P1, gold/silver/bronze tones for podium, dimmed for rest
        View bar = new View(this);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(3), LinearLayout.LayoutParams.MATCH_PARENT);
        barParams.setMargins(dpToPx(12), 0, dpToPx(12), 0);
        int barColor = pos == 1 ? currentTheme.accent
                : pos == 2 ? ThemeManager.blendColors(currentTheme.accent, 0xFFC0C0C0, 0.5f)
                : pos == 3 ? ThemeManager.blendColors(currentTheme.accent, 0xFFCD7F32, 0.5f)
                : ThemeManager.blendColors(currentTheme.cardBg, currentTheme.accent, 0.3f);
        bar.setBackgroundColor(barColor);
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
        ptsText.setTextColor(currentTheme.accent);
        ptsText.setTextSize(18);
        ptsText.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), android.graphics.Typeface.BOLD);
        row.addView(ptsText);
        return row;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
