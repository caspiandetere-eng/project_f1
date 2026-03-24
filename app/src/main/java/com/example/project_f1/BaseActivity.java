package com.example.project_f1;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class BaseActivity extends AppCompatActivity {

    // Keep for callers that still reference this constant
    public static final String ACTION_THEME_CHANGED = "com.example.project_f1.THEME_CHANGED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        StateManager.get().observeTheme().observe(this, themeMode -> {
            ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
            onThemeChanged(theme);
        });

        StateManager.get().observeMode().observe(this, detailed -> {
            onModeChanged(detailed);
        });
    }

    /**
     * Override in subclasses to re-tint views when theme changes without recreating.
     * Default implementation re-applies the full theme to the root view tree.
     */
    protected void onThemeChanged(ThemeManager.TeamTheme theme) {
        View root = findViewById(android.R.id.content);
        if (root instanceof ViewGroup) {
            ThemeManager.tintViewTree((ViewGroup) root, theme);
        }
    }

    /**
     * Override in subclasses to react to General / Detailed mode toggle.
     */
    protected void onModeChanged(boolean isDetailed) {}

    /** Kept for backward compatibility — delegates to StateManager. */
    public static void notifyThemeChanged(android.content.Context context) {
        String current = StateManager.get().getTheme();
        StateManager.get().setTheme(context, current);
    }

    protected void setupSwipeRefresh(SwipeRefreshLayout swipeRefresh, int accentColor, Runnable onRefresh) {
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                onRefresh.run();
                swipeRefresh.postDelayed(() -> swipeRefresh.setRefreshing(false), 2000);
            });
            swipeRefresh.setColorSchemeColors(accentColor, 0xFFFFFFFF);
        }
    }
}
