package com.example.project_f1;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class F1BasicsActivity extends BaseActivity {

    private ThemeManager.TeamTheme theme;
    private BasicsCardAdapter adapter;
    private View toggleIndicator;
    private TextView toggleBasic, toggleDetailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f1_basics);
        theme = ThemeManager.applyFullTheme(this);

        View topStripe = findViewById(R.id.topStripe);
        if (topStripe != null) topStripe.setBackgroundColor(theme.accent);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Toggle
        View toggleContainer = findViewById(R.id.modeToggleContainer);
        toggleIndicator = findViewById(R.id.toggleIndicator);
        toggleBasic     = findViewById(R.id.toggleBasic);
        toggleDetailed  = findViewById(R.id.toggleDetailed);
        toggleContainer.setVisibility(View.VISIBLE);
        toggleIndicator.setBackgroundResource(R.drawable.toggle_pill);

        // Position indicator immediately (no animation on first load)
        toggleContainer.post(() -> snapIndicator(StateManager.get().isDetailed()));

        toggleBasic.setOnClickListener(v -> switchMode(false));
        toggleDetailed.setOnClickListener(v -> switchMode(true));

        String category = getIntent().getStringExtra("category");
        if (category == null) category = "Introduction";
        setTitle(category);

        RecyclerView recycler = findViewById(R.id.basicsListView);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BasicsCardAdapter(this, buildItems(category), this);
        recycler.setAdapter(adapter);
    }

    private void switchMode(boolean detailed) {
        if (StateManager.get().isDetailed() == detailed) return;
        StateManager.get().toggleMode(this, detailed);
        animateIndicator(detailed);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void snapIndicator(boolean detailed) {
        toggleIndicator.setTranslationX(detailed ? toggleBasic.getWidth() : 0);
        applyTextColors(detailed, 1f);
    }

    private void animateIndicator(boolean detailed) {
        float from = toggleIndicator.getTranslationX();
        float to   = detailed ? toggleBasic.getWidth() : 0;

        ValueAnimator slide = ValueAnimator.ofFloat(from, to);
        slide.setDuration(220);
        slide.setInterpolator(new DecelerateInterpolator());
        slide.addUpdateListener(a -> toggleIndicator.setTranslationX((float) a.getAnimatedValue()));

        // Cross-fade text colours
        int activeColor   = 0xFFFFFFFF;
        int inactiveColor = 0xFF888888;
        ValueAnimator colorAnim = ValueAnimator.ofFloat(0f, 1f);
        colorAnim.setDuration(220);
        colorAnim.addUpdateListener(a -> applyTextColors(detailed, (float) a.getAnimatedValue()));

        slide.start();
        colorAnim.start();
    }

    private void applyTextColors(boolean detailed, float fraction) {
        ArgbEvaluator ev = new ArgbEvaluator();
        int active   = 0xFFFFFFFF;
        int inactive = 0xFF888888;
        toggleBasic.setTextColor((int) ev.evaluate(
                detailed ? fraction : 1f - fraction, active, inactive));
        toggleDetailed.setTextColor((int) ev.evaluate(
                detailed ? fraction : 1f - fraction, inactive, active));
    }

    private List<com.example.project_f1.BasicsCardItem> buildItems(String category) {
        List<com.example.project_f1.BasicsCardItem> items = new ArrayList<>();
        switch (category) {
            case "Introduction":
                items.add(new com.example.project_f1.BasicsCardItem(
                        "What is Formula 1?",
                        "Formula 1 is the highest level of car racing in the world.",
                        "Formula 1 is the pinnacle of motorsport, featuring advanced single-seater cars competing globally.",
                        0, "#E10600", "https://www.youtube.com/watch?v=Q-jjZMMxbZs"));
                items.add(new com.example.project_f1.BasicsCardItem(
                        "The Two Championships",
                        "F1 awards two titles each season.",
                        "The Drivers' Championship and the Constructors' Championship are run in parallel.",
                        0, "#FF6600", "https://www.youtube.com/watch?v=Q-jjZMMxbZs"));
                items.add(new com.example.project_f1.BasicsCardItem(
                        "Teams & Constructors",
                        "11 teams each build their own car.",
                        "In 2026, there are 11 teams: Ferrari, Mercedes, Red Bull, McLaren, Aston Martin, Alpine, Williams, Haas, Racing Bulls, Audi, and Cadillac.",
                        0, "#C0C0C0", "https://www.youtube.com/watch?v=Q-jjZMMxbZs"));
                break;
            case "Race Weekend":
                items.add(new com.example.project_f1.BasicsCardItem("Race Weekend", "3 days of action.", "Practice, Qualifying, and Race.", 0, "#3399FF", "https://www.youtube.com/watch?v=R1s2oGd5K2A"));
                items.add(new com.example.project_f1.BasicsCardItem("Qualifying", "The fight for Pole.", "Q1, Q2, and Q3 decide the grid.", 0, "#0066FF", "https://www.youtube.com/watch?v=4x4ZkWq8S6A"));
                break;
            case "Flags & Rules":
                items.add(new com.example.project_f1.BasicsCardItem("Flag Signals", "Safety communication.", "Yellow for danger, Green for clear.", 0, "#FFFF00", "https://www.youtube.com/watch?v=Q-jjZMMxbZs"));
                break;
            case "Strategy & Tyres":
                items.add(new com.example.project_f1.BasicsCardItem("Tyre Compounds", "Soft, Medium, and Hard.", "Drivers must manage wear and durability.", 0, "#FF0000", "https://www.youtube.com/watch?v=6s9dQ3F9g9k"));
                break;
        }
        return items;
    }
}
