package com.example.project_f1;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.api.OpenF1ApiClient;
import com.example.project_f1.models.JolpicaScheduleResponse;
import com.example.project_f1.models.JolpicaStandingsResponse;
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
    private MaterialCardView cardHistory, cardTech, cardStandings, cardLatestRace, cardLapDetails, cardF1Impact, cardDrivers, cardTeams, cardBasics, cardQuiz;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private ThemeManager.TeamTheme currentTheme;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ActivityResultLauncher<String> notifPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {});

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
        requestNotificationPermissionIfNeeded();
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
        cardQuiz = findViewById(R.id.cardQuiz);
        
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
                         R.id.cardDrivers, R.id.cardTeams, R.id.cardBasics, R.id.cardQuiz};
        for (int id : cardIds) {
            MaterialCardView card = findViewById(id);
            if (card != null) {
                if (id == R.id.cardQuiz) {
                    // Quiz card keeps its gold accent, only tint the background
                    card.setCardBackgroundColor(ThemeManager.blendColors(0xFF0D0D1A, theme.accent, 0.05f));
                } else {
                    card.setCardBackgroundColor(ThemeManager.blendColors(0xFF0D0D0D, theme.accent, 0.07f));
                    card.setStrokeColor(ThemeManager.blendColors(0xFF222222, theme.accent, 0.28f));
                    card.setStrokeWidth(dpToPx(1));
                }
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
                RaceNotificationScheduler.cancelAll(this, lastScheduleResponse);
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

        cardLatestRace.post(() -> {
            CoachMarkOverlay.CoachMark[] marks = {
                new CoachMarkOverlay.CoachMark(
                    findViewById(R.id.btnMenu),
                    "Navigation Menu",
                    "Open the side menu for your profile, XP level, and links to every section."
                ),
                new CoachMarkOverlay.CoachMark(
                    cardLatestRace,
                    "Upcoming Race",
                    "Next race on the 2026 calendar — name, date, and location. Tap to explore session details."
                ),
                new CoachMarkOverlay.CoachMark(
                    cardStandings,
                    "Driver Standings",
                    "Live championship standings. Tap 'View full standings' to browse any season."
                ),
                new CoachMarkOverlay.CoachMark(
                    cardHistory,
                    "F1 History",
                    "Explore golden eras and legendary drivers like Senna, Schumacher, and Hamilton."
                ),
                new CoachMarkOverlay.CoachMark(
                    cardTech,
                    "Tech Analysis",
                    "Deep-dive into F1 engineering — aero, power units, tyres, and more."
                ),
                new CoachMarkOverlay.CoachMark(
                    cardDrivers,
                    "Drivers & Teams",
                    "Browse every driver on the 2026 grid. Tap a driver to see stats and performance charts."
                ),
                new CoachMarkOverlay.CoachMark(
                    cardQuiz,
                    "F1 Knowledge Quiz",
                    "Test your F1 knowledge and earn XP to unlock more detailed content across the app."
                ),
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
        if (cardQuiz != null) cardQuiz.setOnClickListener(v -> animateThen(v, new Intent(this, QuizActivity.class)));
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

        // Level badge
        TextView tvLevel = header.findViewById(R.id.tvHeaderLevel);
        TextView tvXp    = header.findViewById(R.id.tvHeaderXp);
        TextView tvNextTier = header.findViewById(R.id.tvHeaderNextTier);
        int level = KnowledgeLevelManager.getLevel(this);
        int xp    = KnowledgeLevelManager.getXp(this);
        String tier = KnowledgeLevelManager.tierForLevel(level);

        if (tvLevel != null) {
            tvLevel.setText(tier.toUpperCase() + " \u00b7 L" + level);
            android.graphics.drawable.GradientDrawable badgeBg = new android.graphics.drawable.GradientDrawable();
            badgeBg.setColor(currentTheme.accent);
            badgeBg.setCornerRadius(dpToPx(4));
            tvLevel.setBackground(badgeBg);
            tvLevel.setOnClickListener(v -> showLevelUpDialog(level, xp, tier));
        }
        if (tvXp != null) tvXp.setText(xp + " / 100");
        if (tvNextTier != null) {
            String next = nextTierName(level);
            tvNextTier.setText(next.isEmpty() ? "MAX LEVEL" : "Next: " + next.toUpperCase());
        }
        View xpFill = header.findViewById(R.id.viewXpFill);
        if (xpFill != null) {
            xpFill.post(() -> {
                android.view.ViewGroup track = (android.view.ViewGroup) xpFill.getParent();
                if (track == null) return;
                int trackWidth = track.getWidth();
                int fillWidth  = (int) (trackWidth * (xp / 100f));
                android.widget.FrameLayout.LayoutParams lp =
                        new android.widget.FrameLayout.LayoutParams(
                                fillWidth, android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
                xpFill.setLayoutParams(lp);
                android.graphics.drawable.GradientDrawable fillBg =
                        new android.graphics.drawable.GradientDrawable();
                fillBg.setColor(currentTheme.accent);
                fillBg.setCornerRadius(dpToPx(4));
                xpFill.setBackground(fillBg);
            });
        }
        TextView tvXpFraction = header.findViewById(R.id.tvHeaderXpFraction);
        if (tvXpFraction != null) {
            tvXpFraction.setText(xp + " / 100 XP to next level");
            tvXpFraction.setTextColor(
                    ThemeManager.blendColors(0xFF444444, currentTheme.accent, 0.4f));
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

    // Kept so cancelAll can reference it on logout
    private JolpicaScheduleResponse lastScheduleResponse = null;

    private void loadLatestSession() {
        // Use Jolpica schedule for accurate official race names and dates
        DataSyncManager.loadSchedule(this, 2026, new DataSyncManager.SyncCallback<JolpicaScheduleResponse>() {
            @Override
            public void onSuccess(JolpicaScheduleResponse response) {
                lastScheduleResponse = response;
                // Schedule race notifications whenever fresh schedule arrives
                RaceNotificationScheduler.scheduleAll(MainActivity.this, response);

                JolpicaScheduleResponse.Race next = findNextRace(response);
                if (next != null) {
                    tvRaceName.setText(next.raceName != null ? next.raceName.toUpperCase() : "UPCOMING RACE");
                    String location = (next.circuit != null && next.circuit.location != null)
                            ? next.circuit.location.locality + ", " + next.circuit.location.country
                            : "";
                    String dateStr = formatRaceDate(next.date, next.time);
                    tvCurrentLap.setText(location.isEmpty() ? dateStr : dateStr + "  •  " + location);
                } else {
                    tvRaceName.setText("SEASON COMPLETE");
                    tvCurrentLap.setText("No upcoming races");
                }
                loadTopStandings();
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                tvRaceName.setText("SCHEDULE UNAVAILABLE");
                tvCurrentLap.setText("Pull to refresh");
                loadTopStandings();
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void refreshData() {
        CacheManager.clearAllCache(this);
        tvRaceName.setText("LOADING...");
        tvCurrentLap.setText("Updating data...");
        topStandingsContainer.removeAllViews();
        loadLatestSession();
    }

    /** Returns the next race after today, or null if the season is over. */
    private JolpicaScheduleResponse.Race findNextRace(JolpicaScheduleResponse response) {
        if (response == null || response.mrData == null
                || response.mrData.raceTable == null
                || response.mrData.raceTable.races == null) return null;
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(new java.util.Date());
        for (JolpicaScheduleResponse.Race race : response.mrData.raceTable.races) {
            if (race.date != null && race.date.compareTo(today) >= 0) return race;
        }
        return null;
    }

    /** Formats "yyyy-MM-dd" + optional "HH:mm:ssZ" into a readable date string. */
    private String formatRaceDate(String date, String time) {
        try {
            String iso = date + (time != null ? "T" + time : "T00:00:00Z");
            java.time.Instant instant = java.time.Instant.parse(iso);
            java.time.ZonedDateTime zdt = instant.atZone(java.time.ZoneId.systemDefault());
            return java.time.format.DateTimeFormatter.ofPattern("EEE, MMM dd yyyy").format(zdt);
        } catch (Exception e) {
            return date != null ? date : "Date TBA";
        }
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
        MaterialCardView[] cards = {cardLatestRace, cardStandings, cardHistory, cardTech, cardLapDetails, cardF1Impact, cardDrivers, cardTeams, cardBasics, cardQuiz};
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
        String flag = nationalityToFlag(r.nationality);
        String nameLabel = (r.givenName != null ? r.givenName.charAt(0) + ". " : "")
                + (r.familyName != null ? r.familyName : r.driverId);
        nameText.setText(flag.isEmpty() ? nameLabel : flag + "  " + nameLabel);
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

    private static String nationalityToFlag(String nationality) {
        if (nationality == null) return "";
        switch (nationality) {
            case "British":       return "🇬🇧";
            case "Dutch":         return "🇳🇱";
            case "Monegasque":    return "🇲🇨";
            case "Spanish":       return "🇪🇸";
            case "Australian":    return "🇦🇺";
            case "Finnish":       return "🇫🇮";
            case "German":        return "🇩🇪";
            case "French":        return "🇫🇷";
            case "Brazilian":     return "🇧🇷";
            case "Mexican":       return "🇲🇽";
            case "Canadian":      return "🇨🇦";
            case "Thai":          return "🇹🇭";
            case "Italian":       return "🇮🇹";
            case "Argentine":     return "🇦🇷";
            case "New Zealander": return "🇳🇿";
            case "Swedish":       return "🇸🇪";
            case "Danish":        return "🇩🇰";
            case "American":      return "🇺🇸";
            case "Japanese":      return "🇯🇵";
            case "Chinese":       return "🇨🇳";
            case "Austrian":      return "🇦🇹";
            case "Polish":        return "🇵🇱";
            default:              return "";
        }
    }

    private static String nextTierName(int level) {
        if (level < 6)  return "Casual";
        if (level < 11) return "Enthusiast";
        if (level < 16) return "Insider";
        return ""; // already max tier
    }

    private void showLevelUpDialog(int level, int xp, String tier) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogDark);

        // Build message
        String nextTier = nextTierName(level);
        int levelsToNextTier = nextTier.isEmpty() ? 0 : (level <= 5 ? 6 : level <= 10 ? 11 : level <= 15 ? 16 : 21) - level;
        int xpToNextLevel = 100 - xp;

        StringBuilder msg = new StringBuilder();
        msg.append("Current Tier: ").append(tier.toUpperCase()).append(" · Level ").append(level).append("\n");
        msg.append("XP this level: ").append(xp).append(" / 100\n\n");
        msg.append("── How to earn XP ──────────────\n");
        msg.append("Score ≥ 70% on a quiz  →  full XP\n");
        msg.append("Score 40–69%  →  half XP, no level change\n");
        msg.append("Score < 40%   →  no XP, demote 1 level\n\n");
        msg.append("── XP per correct answer ───────\n");
        msg.append("Rookie (L1–5)      10 XP\n");
        msg.append("Casual (L6–10)     15 XP\n");
        msg.append("Enthusiast (L11–15) 20 XP\n");
        msg.append("Insider (L16–20)   25 XP\n\n");
        if (!nextTier.isEmpty()) {
            msg.append("── Next milestone ──────────────\n");
            msg.append(xpToNextLevel).append(" XP to reach Level ").append(level + 1).append("\n");
            msg.append(levelsToNextTier).append(" level(s) to unlock ").append(nextTier.toUpperCase()).append(" tier\n\n");
            msg.append("Unlocking ").append(nextTier.toUpperCase()).append(" reveals:\n");
            if (nextTier.equalsIgnoreCase("casual")) {
                msg.append("• More standings rows on home\n• Richer driver detail cards");
            } else if (nextTier.equalsIgnoreCase("enthusiast")) {
                msg.append("• Career stats & records in driver cards\n• Constructor stats in team cards");
            } else if (nextTier.equalsIgnoreCase("insider")) {
                msg.append("• Full telemetry analysis\n• Circuit-by-circuit speed data");
            }
        } else {
            msg.append("🏆 You've reached the maximum level!\nYou're a true F1 Insider.");
        }

        builder.setTitle(tier.toUpperCase() + " · Level " + level)
               .setMessage(msg.toString())
               .setPositiveButton("Take Quiz", (d, w) -> navigate(new Intent(this, QuizActivity.class)))
               .setNegativeButton("Close", null)
               .show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
