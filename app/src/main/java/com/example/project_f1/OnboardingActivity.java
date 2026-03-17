package com.example.project_f1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout dotsContainer;
    private Button btnNext;
    private OnboardingAdapter.OnboardingPage[] pages;

    private static final OnboardingAdapter.OnboardingPage[] ROOKIE_PAGES = {
        new OnboardingAdapter.OnboardingPage("🏎️", "What is Formula 1?",
            "Formula 1 is the pinnacle of motorsport — the fastest, most technologically advanced racing series on the planet. Teams spend hundreds of millions building cars that can reach 350+ km/h."),
        new OnboardingAdapter.OnboardingPage("🌍", "A Global Championship",
            "Every year, 20 drivers from 10 teams race across 20+ countries. From Monaco's tight streets to Monza's high-speed straights — each circuit is a unique challenge."),
        new OnboardingAdapter.OnboardingPage("🏁", "How a Race Works",
            "Races cover around 300km. The first driver to cross the finish line wins. Points are awarded to the top 10 — 25 for 1st place, down to 1 point for 10th."),
        new OnboardingAdapter.OnboardingPage("🔧", "The Pit Stop",
            "During a race, cars enter the pit lane to change tires. A world-class pit stop takes under 2 seconds. Pit strategy can make or break a race result."),
        new OnboardingAdapter.OnboardingPage("🏆", "Two Championships",
            "There are two titles up for grabs — the Drivers' Championship (best individual) and the Constructors' Championship (best team). Both are decided over the full season."),
        new OnboardingAdapter.OnboardingPage("🚦", "Race Flags",
            "Flags signal race conditions. 🟢 Green = go, 🟡 Yellow = caution, 🔴 Red = race stopped, 🏁 Chequered = race over. Blue flags warn a driver a faster car is approaching."),
    };

    private static final OnboardingAdapter.OnboardingPage[] CASUAL_PAGES = {
        new OnboardingAdapter.OnboardingPage("📋", "Points System",
            "Points are awarded 25-18-15-12-10-8-6-4-2-1 for positions 1–10. An extra point goes to the driver who sets the fastest lap — but only if they finish in the top 10."),
        new OnboardingAdapter.OnboardingPage("🛞", "Tire Compounds",
            "Soft (red) = fastest but wears quickly. Medium (yellow) = balanced. Hard (white) = slowest but lasts longest. Teams must use at least 2 different compounds per race."),
        new OnboardingAdapter.OnboardingPage("💨", "DRS — Drag Reduction System",
            "DRS opens a flap on the rear wing to cut drag and boost top speed by ~15 km/h. It can only be used in designated zones when within 1 second of the car ahead."),
        new OnboardingAdapter.OnboardingPage("🚦", "Safety Car & VSC",
            "A Safety Car is deployed when there's danger on track — all cars slow and queue behind it. A Virtual Safety Car (VSC) slows cars electronically without a physical car."),
        new OnboardingAdapter.OnboardingPage("⚖️", "Parc Fermé",
            "After qualifying, cars enter Parc Fermé — teams cannot make major setup changes. This ensures the car that qualifies is essentially the same car that races on Sunday."),
        new OnboardingAdapter.OnboardingPage("🔵", "Blue Flags & Lapping",
            "When a leading driver is about to lap a slower car, the slower driver gets a blue flag and must let them past within 3 corners or risk a penalty."),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_onboarding);
        ThemeManager.applyStatusBar(this);

        viewPager = findViewById(R.id.viewPager);
        dotsContainer = findViewById(R.id.dotsContainer);
        btnNext = findViewById(R.id.btnNext);
        TextView tvSkip = findViewById(R.id.tvSkip);

        KnowledgeLevelManager.KnowledgeLevel level = KnowledgeLevelManager.getKnowledgeLevel(this);
        pages = level == KnowledgeLevelManager.KnowledgeLevel.ROOKIE ? ROOKIE_PAGES : CASUAL_PAGES;

        viewPager.setAdapter(new OnboardingAdapter(pages));
        setupDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);
                btnNext.setText(position == pages.length - 1 ? "GET STARTED" : "NEXT");
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < pages.length - 1) {
                viewPager.setCurrentItem(current + 1, true);
            } else {
                finishOnboarding();
            }
        });

        tvSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupDots(int activeIndex) {
        dotsContainer.removeAllViews();
        int dp8 = (int) (8 * getResources().getDisplayMetrics().density);
        int dp6 = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < pages.length; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(i == activeIndex ? R.drawable.dot_active : R.drawable.dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                i == activeIndex ? dp8 : dp6,
                i == activeIndex ? dp8 : dp6
            );
            params.setMargins(dp6, 0, dp6, 0);
            dot.setLayoutParams(params);
            dotsContainer.addView(dot);
        }
    }

    private void finishOnboarding() {
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_done", true).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
