package com.example.project_f1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Red status bar + black nav bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.parseColor("#E10600"));
        getWindow().setNavigationBarColor(Color.parseColor("#0A0A0A"));
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.activity_splash);

        View content = findViewById(R.id.splashContent);

        // Fade + scale in
        content.setScaleX(0.8f);
        content.setScaleY(0.8f);
        content.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .withEndAction(() ->
                    content.postDelayed(() -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
                        finish();
                    }, 1000)
                ).start();
    }
}
