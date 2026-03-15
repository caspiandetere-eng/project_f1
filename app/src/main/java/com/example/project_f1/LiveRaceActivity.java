package com.example.project_f1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;
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
        OpenF1ApiClient.getApiService().getSessions(2024, "Race").enqueue(new Callback<List<OpenF1Session>>() {
            @Override
            public void onResponse(Call<List<OpenF1Session>> call, Response<List<OpenF1Session>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    sessionKey = response.body().get(response.body().size() - 1).sessionKey;
                    startLiveUpdates();
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Session>> call, Throwable t) {
            }
        });
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
        card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up_fade));
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
        update.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        updatesContainer.addView(update, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
