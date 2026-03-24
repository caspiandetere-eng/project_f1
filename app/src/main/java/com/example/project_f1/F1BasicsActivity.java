package com.example.project_f1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import java.util.ArrayList;
import java.util.List;

public class F1BasicsActivity extends BaseActivity {

    private ListView listView;
    private ThemeManager.TeamTheme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f1_basics);
        theme = ThemeManager.applyFullTheme(this);
        applyTheme();

        listView = findViewById(R.id.basicsListView);
        String category = getIntent().getStringExtra("category");
        if (category == null) category = "Introduction";

        setTitle(category);
        loadItems(category);
    }

    private void applyTheme() {
        View topStripe = findViewById(R.id.topStripe);
        if (topStripe != null) topStripe.setBackgroundColor(theme.accent);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadItems(String category) {
        List<BasicsCardItem> items = new ArrayList<>();
        switch (category) {
            case "Introduction":
                items.add(new BasicsCardItem(
                        "What is Formula 1?",
                        "Formula 1 is the highest level of car racing in the world.",
                        "Formula 1 is the pinnacle of motorsport, featuring advanced single-seater cars competing globally.",
                        "#E10600", "Q-jjZMMxbZs"));
                items.add(new BasicsCardItem(
                        "The Two Championships",
                        "F1 awards two titles each season.",
                        "The Drivers' Championship and the Constructors' Championship are run in parallel.",
                        "#FF6600", "Q-jjZMMxbZs"));
                items.add(new BasicsCardItem(
                        "Teams & Constructors",
                        "11 teams each build their own car.",
                        "In 2026, there are 11 teams: Ferrari, Mercedes, Red Bull, McLaren, Aston Martin, Alpine, Williams, Haas, Racing Bulls, Audi, and Cadillac.",
                        "#C0C0C0", "Q-jjZMMxbZs"));
                break;
            case "Race Weekend":
                items.add(new BasicsCardItem("Race Weekend", "3 days of action.", "Practice, Qualifying, and Race.", "#3399FF", "R1s2oGd5K2A"));
                items.add(new BasicsCardItem("Qualifying", "The fight for Pole.", "Q1, Q2, and Q3 decide the grid.", "#0066FF", "4x4ZkWq8S6A"));
                break;
            case "Flags & Rules":
                items.add(new BasicsCardItem("Flag Signals", "Safety communication.", "Yellow for danger, Green for clear.", "#FFFF00", "F1_flags_explained"));
                break;
            case "Strategy & Tyres":
                items.add(new BasicsCardItem("Tyre Compounds", "Soft, Medium, and Hard.", "Drivers must manage wear and durability.", "#FF0000", "6s9dQ3F9g9k"));
                break;
        }
        listView.setAdapter(new BasicsAdapter(this, items));
    }

    private static class BasicsCardItem {
        String title, subtitle, details, color, videoId;
        BasicsCardItem(String t, String s, String d, String c, String v) {
            title = t; subtitle = s; details = d; color = c; videoId = v;
        }
    }

    private class BasicsAdapter extends BaseAdapter {
        private Context context;
        private List<BasicsCardItem> items;
        BasicsAdapter(Context c, List<BasicsCardItem> i) { context = c; items = i; }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int p) { return items.get(p); }
        @Override public long getItemId(int p) { return p; }
        @Override public View getView(int p, View v, ViewGroup parent) {
            if (v == null) v = LayoutInflater.from(context).inflate(R.layout.item_basics_card, parent, false);
            BasicsCardItem item = items.get(p);
            MaterialCardView card = v.findViewById(R.id.cardBasicsItem);
            TextView tvTitle = v.findViewById(R.id.basicsCardTitle);
            TextView tvSub = v.findViewById(R.id.basicsCardPreview);
            TextView tvDetails = v.findViewById(R.id.basicsCardFullDesc);
            View accent = v.findViewById(R.id.basicsAccentBar);
            YouTubePlayerView youtubeView = v.findViewById(R.id.youtubePlayerView);

            tvTitle.setText(item.title);
            tvSub.setText(item.subtitle);
            tvDetails.setText(item.details);
            int color = Color.parseColor(item.color);
            accent.setBackgroundColor(color);
            card.setStrokeColor(ThemeManager.blendColors(0xFF222222, color, 0.3f));

            // Make expandable content visible so YouTubePlayerView is accessible
            v.findViewById(R.id.basicsExpandableContent).setVisibility(View.VISIBLE);

            getLifecycle().addObserver(youtubeView);
            youtubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youtubeView.post(() -> youTubePlayer.cueVideo(item.videoId, 0));
                }
            });

            return v;
        }
    }
}
