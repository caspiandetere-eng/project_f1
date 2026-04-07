package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StateManager {

    private static final String TAG = "StateManager";
    private static final String PREFS = "F1Prefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_DETAILED_MODE = "detailed_mode";

    private static volatile StateManager instance;

    private final MutableLiveData<String> currentTheme = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDetailedMode = new MutableLiveData<>();
    private final MutableLiveData<Integer> expandedCardPosition = new MutableLiveData<>(NO_POSITION);

    public static final int NO_POSITION = -1;

    private StateManager() {}

    public static StateManager get() {
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) instance = new StateManager();
            }
        }
        return instance;
    }

    /** Call once on app start, before any Activity loads. */
    public static void init(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        StateManager sm = get();

        String theme = prefs.getString(KEY_THEME_MODE, ThemeManager.THEME_DEFAULT);
        sm.currentTheme.setValue(theme);

        boolean detailed = KnowledgeLevelManager.isEnthusiast(ctx)
                || KnowledgeLevelManager.isInsider(ctx)
                || prefs.getBoolean(KEY_DETAILED_MODE, false);
        sm.isDetailedMode.setValue(detailed);

        Log.d(TAG, "init: theme=" + theme + " detailed=" + detailed);
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setTheme(Context ctx, String themeMode) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_THEME_MODE, themeMode).apply();
        currentTheme.setValue(themeMode);
        Log.d(TAG, "Theme changed: " + themeMode);
    }

    public void toggleMode(Context ctx, boolean detailed) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_DETAILED_MODE, detailed).apply();
        isDetailedMode.setValue(detailed);
        Log.d(TAG, "Mode toggled: detailed=" + detailed);
    }

    public void setExpandedCard(int position) {
        expandedCardPosition.setValue(position);
        Log.d(TAG, "Card expanded: " + position);
    }

    public void resetExpandedCard() {
        expandedCardPosition.setValue(NO_POSITION);
        Log.d(TAG, "Card expanded: reset");
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    public LiveData<String> observeTheme() { return currentTheme; }
    public LiveData<Boolean> observeMode() { return isDetailedMode; }
    public LiveData<Integer> observeExpandedCard() { return expandedCardPosition; }

    // ── Getters (non-reactive) ────────────────────────────────────────────────

    public String getTheme() {
        String v = currentTheme.getValue();
        return v != null ? v : ThemeManager.THEME_DEFAULT;
    }

    public boolean isDetailed() {
        Boolean v = isDetailedMode.getValue();
        return v != null && v;
    }

    public int getExpandedCardPosition() {
        Integer v = expandedCardPosition.getValue();
        return v != null ? v : NO_POSITION;
    }
}
