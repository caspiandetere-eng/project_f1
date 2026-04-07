package com.example.project_f1;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.OpenF1Session;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends AppCompatActivity {

    private TextView tvStatus;
    private View splashProgressFill;
    private View[] lights;

    private final AtomicInteger tasksCompleted = new AtomicInteger(0);
    private static final int TOTAL_TASKS = 2;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean navigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_splash);

        // Status bar dark
        getWindow().setStatusBarColor(0xFF0D0D0D);

        tvStatus          = findViewById(R.id.tvStatus);
        splashProgressFill = findViewById(R.id.splashProgressFill);

        lights = new View[]{
            findViewById(R.id.light1),
            findViewById(R.id.light2),
            findViewById(R.id.light3),
            findViewById(R.id.light4),
            findViewById(R.id.light5)
        };

        // Check login before doing anything
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) {
            // Still run entrance animation, then go to login
            runEntranceAnimation(() -> navigateTo(LoginActivity.class));
            return;
        }

        runEntranceAnimation(this::preloadEssentialData);
    }

    // ── Entrance animation ────────────────────────────────────────────────────

    private void runEntranceAnimation(Runnable onComplete) {
        View logo  = findViewById(R.id.ivSplashLogo);
        View title = findViewById(R.id.tvSplashTitle);

        // Logo: scale from 0 with overshoot
        logo.setScaleX(0f);
        logo.setScaleY(0f);
        logo.setAlpha(0f);
        logo.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        // Title: fade + slide up
        title.setAlpha(0f);
        title.setTranslationY(20f);
        title.animate()
                .alpha(1f).translationY(0f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(onComplete)
                .start();
    }

    // ── Data preload ──────────────────────────────────────────────────────────

    private void preloadEssentialData() {
        setStatus("Loading sessions...", 0.1f);

        DataSyncManager.loadSessions(this, 2026,
                new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
            @Override public void onSuccess(List<OpenF1Session> data) {
                setStatus("Loading standings...", 0.5f);
                checkCompletion();
            }
            @Override public void onFailure(String error) {
                setStatus("Loading standings...", 0.5f);
                checkCompletion();
            }
        });

        DataSyncManager.loadStandings(this, 2026,
                new DataSyncManager.SyncCallback<JolpicaStandingsResponse>() {
            @Override public void onSuccess(JolpicaStandingsResponse data) {
                setStatus("Ready!", 1.0f);
                checkCompletion();
            }
            @Override public void onFailure(String error) {
                setStatus("Ready!", 1.0f);
                checkCompletion();
            }
        });
    }

    private void checkCompletion() {
        int done = tasksCompleted.incrementAndGet();

        // Light up one light per task completed (lights 1-2 for tasks, 3-5 for effect)
        lightUpSequence(done);

        if (done >= TOTAL_TASKS) {
            // Light up remaining lights then navigate
            handler.postDelayed(() -> lightUpSequence(3), 200);
            handler.postDelayed(() -> lightUpSequence(4), 400);
            handler.postDelayed(() -> lightUpSequence(5), 600);
            // Navigate after all lights are on
            handler.postDelayed(() -> navigateTo(MainActivity.class), 1000);
        }
    }

    // ── Lights animation ──────────────────────────────────────────────────────

    /**
     * Turns on lights 1 through [count] sequentially.
     * Each light pulses with a scale animation when it turns on.
     */
    private void lightUpSequence(int count) {
        for (int i = 0; i < lights.length && i < count; i++) {
            final View light = lights[i];
            final int index  = i;
            handler.postDelayed(() -> {
                light.setBackground(getDrawable(R.drawable.splash_light_on));
                // Pulse scale
                light.setScaleX(1.4f);
                light.setScaleY(1.4f);
                light.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(250)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .start();
            }, index * 80L);
        }
    }

    // ── Progress bar ──────────────────────────────────────────────────────────

    private void setStatus(String text, float progress) {
        runOnUiThread(() -> {
            tvStatus.setText(text);
            animateProgress(progress);
        });
    }

    private void animateProgress(float targetFraction) {
        splashProgressFill.post(() -> {
            int trackWidth = ((View) splashProgressFill.getParent()).getWidth();
            int targetWidth = (int) (trackWidth * targetFraction);

            ValueAnimator anim = ValueAnimator.ofInt(
                    splashProgressFill.getLayoutParams().width < 0
                            ? 0 : splashProgressFill.getLayoutParams().width,
                    targetWidth);
            anim.setDuration(400);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(a -> {
                splashProgressFill.getLayoutParams().width = (int) a.getAnimatedValue();
                splashProgressFill.requestLayout();
            });
            anim.start();
        });
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void navigateTo(Class<?> target) {
        if (navigated) return;
        navigated = true;
        startActivity(new Intent(this, target));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
