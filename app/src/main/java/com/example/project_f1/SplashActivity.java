package com.example.project_f1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.OpenF1Session;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private AtomicInteger tasksCompleted = new AtomicInteger(0);
    private static final int TOTAL_TASKS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_splash);
        ThemeManager.applyStatusBar(this);

        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) {
            navigateTo(LoginActivity.class);
            return;
        }

        preloadEssentialData();
    }

    private void preloadEssentialData() {
        tvStatus.setText("Loading sessions...");
        
        DataSyncManager.loadSessions(this, 2026, new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
            @Override
            public void onSuccess(List<OpenF1Session> data) {
                updateProgress("Loading standings...");
                checkCompletion();
            }

            @Override
            public void onFailure(String error) {
                updateProgress("Loading standings...");
                checkCompletion();
            }
        });

        DataSyncManager.loadStandings(this, 2026, new DataSyncManager.SyncCallback<JolpicaStandingsResponse>() {
            @Override
            public void onSuccess(JolpicaStandingsResponse data) {
                updateProgress("Ready!");
                checkCompletion();
            }

            @Override
            public void onFailure(String error) {
                updateProgress("Ready!");
                checkCompletion();
            }
        });
    }

    private void updateProgress(String status) {
        tvStatus.setText(status);
        tasksCompleted.incrementAndGet();
    }

    private void checkCompletion() {
        if (tasksCompleted.get() >= TOTAL_TASKS) {
            navigateTo(MainActivity.class);
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
        finish();
    }
}
