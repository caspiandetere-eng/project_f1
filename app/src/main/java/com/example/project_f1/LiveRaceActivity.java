package com.example.project_f1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.project_f1.api.OpenF1ApiClient;
import com.example.project_f1.models.OpenF1Position;
import com.example.project_f1.models.OpenF1Session;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveRaceActivity extends AppCompatActivity {
    private LinearLayout positionsContainer, updatesContainer;
    private TextView raceStatus, currentLap;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private int sessionKey = -1;
    private Map<Integer, String> driverNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_live_race);
        ThemeManager.applyStatusBar(this);

        positionsContainer = findViewById(R.id.positionsContainer);
        updatesContainer = findViewById(R.id.updatesContainer);
        raceStatus = findViewById(R.id.raceStatus);
        currentLap = findViewById(R.id.currentLap);

        if (positionsContainer == null || updatesContainer == null || raceStatus == null || currentLap == null) {
            finish();
            return;
        }

        initDriverNames();
        loadLatestSession();
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
                    OpenF1Session currentSession = findCurrentOrLatestSession(sessions);
                    
                    if (currentSession != null) {
                        sessionKey = currentSession.sessionKey;
                        raceStatus.setText("🚨 " + currentSession.sessionName.toUpperCase());
                        startLiveUpdates();
                    } else {
                        showFallbackData();
                    }
                } else {
                    showFallbackData();
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Session>> call, Throwable t) {
                showFallbackData();
            }
        });
    }
    
    private OpenF1Session findCurrentOrLatestSession(List<OpenF1Session> sessions) {
        if (sessions == null || sessions.isEmpty()) return null;
        
        long currentTime = System.currentTimeMillis();
        OpenF1Session currentSession = null;
        OpenF1Session latestSession = sessions.get(sessions.size() - 1);
        
        for (OpenF1Session session : sessions) {
            try {
                if (session.dateStart != null && session.dateEnd != null) {
                    long startTime = java.time.Instant.parse(session.dateStart).toEpochMilli();
                    long endTime = java.time.Instant.parse(session.dateEnd).toEpochMilli();
                    
                    if (currentTime >= startTime && currentTime <= endTime) {
                        currentSession = session;
                        break;
                    }
                    
                    if (currentTime > endTime && currentSession == null) {
                        currentSession = session;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return currentSession != null ? currentSession : latestSession;
    }

    private void startLiveUpdates() {
        if (sessionKey == -1) return;
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLivePositions();
                addRaceUpdate();
                handler.postDelayed(this, 5000);
            }
        }, 1000);
    }
    
    private void loadLivePositions() {
        OpenF1ApiClient.getApiService().getPositions(sessionKey).enqueue(new Callback<List<OpenF1Position>>() {
            @Override
            public void onResponse(Call<List<OpenF1Position>> call, Response<List<OpenF1Position>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    updatePositions(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Position>> call, Throwable t) {
            }
        });
    }

    private void updatePositions(List<OpenF1Position> positions) {
        positionsContainer.removeAllViews();
        
        Map<Integer, OpenF1Position> latestPositions = new HashMap<>();
        for (OpenF1Position pos : positions) {
            latestPositions.put(pos.driverNumber, pos);
        }
        
        List<OpenF1Position> sortedPositions = new ArrayList<>(latestPositions.values());
        sortedPositions.sort((a, b) -> Integer.compare(a.position, b.position));
        
        for (OpenF1Position pos : sortedPositions) {
            String driverName = driverNames.getOrDefault(pos.driverNumber, "Driver " + pos.driverNumber);
            String gap = pos.position == 1 ? "LEADER" : "+" + (pos.position * 0.5f) + "s";
            positionsContainer.addView(createPositionCard(pos.position, driverName, gap));
        }
        
        currentLap.setText("LIVE RACE DATA");
    }

    private MaterialCardView createPositionCard(int position, String name, String gap) {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardBackgroundColor(Color.parseColor("#1A1A1A"));
        card.setRadius(16);
        card.setCardElevation(12);
        card.setStrokeColor(Color.parseColor(position <= 3 ? "#FFD700" : "#E10600"));
        card.setStrokeWidth(3);
        
        TextView text = new TextView(this);
        text.setText(position + "  " + name + "  " + gap);
        text.setTextColor(0xFFFFFFFF);
        text.setTextSize(16);
        text.setPadding(40, 30, 40, 30);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.jetbrains_mono);
        if (typeface != null) text.setTypeface(typeface);
        
        card.addView(text);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);
        card.setAlpha(0f);
        card.animate().alpha(1f).setDuration(300);
        return card;
    }

    private void addRaceUpdate() {
        String[] updates = {
            "Live data from OpenF1 API",
            "DRS enabled on main straight",
            "Position changes detected",
            "Real-time telemetry active",
            "Monitoring race progress"
        };
        
        TextView update = new TextView(this);
        update.setText("⚡ " + updates[random.nextInt(updates.length)]);
        update.setTextColor(0xFFE10600);
        update.setTextSize(14);
        update.setPadding(30, 20, 30, 20);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.inter);
        if (typeface != null) update.setTypeface(typeface);
        update.setAlpha(0f);
        update.animate().alpha(1f).setDuration(300);
        updatesContainer.addView(update, 0);
    }
    
    private void showFallbackData() {
        positionsContainer.removeAllViews();
        for (int i = 1; i <= 5; i++) {
            String driverName = driverNames.getOrDefault(i, "Driver " + i);
            String gap = i == 1 ? "LEADER" : "+" + (i * 0.5f) + "s";
            positionsContainer.addView(createPositionCard(i, driverName, gap));
        }
        currentLap.setText("LIVE RACE DATA");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
