package com.example.project_f1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.example.project_f1.models.Team;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class ThemeManager {

    public static final String THEME_DEFAULT = "default";
    public static final String THEME_TEAM    = "team";

    private static final String PREFS            = "F1Prefs";
    private static final String KEY_DARK         = "dark_theme";
    private static final String KEY_THEME_MODE   = "theme_mode";

    // ── Team theme data ───────────────────────────────────────────────────────

    public static class TeamTheme {
        public final int bgTop;          // screen background gradient top
        public final int bgBottom;       // screen background gradient bottom
        public final int accent;         // primary accent (stripe, labels, links)
        public final int accentDark;     // darker shade of accent (card strokes, secondary)
        public final int cardBg;         // card background color
        public final int cardStroke;     // card stroke color
        public final int navBg;          // navigation drawer background
        public final int statusBar;      // status bar color
        public final int buttonBg;       // button background tint

        public TeamTheme(int bgTop, int bgBottom, int accent, int accentDark,
                         int cardBg, int cardStroke, int navBg, int statusBar, int buttonBg) {
            this.bgTop       = bgTop;
            this.bgBottom    = bgBottom;
            this.accent      = accent;
            this.accentDark  = accentDark;
            this.cardBg      = cardBg;
            this.cardStroke  = cardStroke;
            this.navBg       = navBg;
            this.statusBar   = statusBar;
            this.buttonBg    = buttonBg;
        }
    }

    /**
     * Returns a unique TeamTheme for each team ID.
     * Each theme is designed around the team's real identity.
     */
    public static TeamTheme getTeamTheme(String teamId) {
        if (teamId == null) return getDefaultTheme();
        switch (teamId) {

            case "mercedes":
                return new TeamTheme(
                        color("#0D1210"),
                        color("#0D0D0D"),
                        color("#00D2BE"),
                        color("#009688"),
                        color("#111614"),
                        color("#1A4A44"),
                        color("#0A0F0E"),
                        color("#0D1210"),
                        color("#00A693")
                );

            case "ferrari":
                return new TeamTheme(
                        color("#130808"),
                        color("#0D0D0D"),
                        color("#DC0000"),
                        color("#A00000"),
                        color("#160A0A"),
                        color("#4A1010"),
                        color("#0F0707"),
                        color("#130808"),
                        color("#DC0000")
                );

            case "mclaren":
                return new TeamTheme(
                        color("#130F08"),
                        color("#0D0D0D"),
                        color("#FF8700"),
                        color("#CC6A00"),
                        color("#161108"),
                        color("#4A3000"),
                        color("#0F0C06"),
                        color("#130F08"),
                        color("#CC6A00")
                );

            case "red_bull":
                return new TeamTheme(
                        color("#080A13"),
                        color("#0D0D0D"),
                        color("#3671C6"),
                        color("#1E4A9A"),
                        color("#0A0C18"),
                        color("#1A2A4A"),
                        color("#07080F"),
                        color("#080A13"),
                        color("#1E4A9A")
                );

            case "aston_martin":
                return new TeamTheme(
                        color("#081210"),
                        color("#0D0D0D"),
                        color("#00A693"),
                        color("#006F62"),
                        color("#0A1614"),
                        color("#0F3A34"),
                        color("#060E0C"),
                        color("#081210"),
                        color("#006F62")
                );

            case "audi":
                return new TeamTheme(
                        color("#110808"),
                        color("#0D0D0D"),
                        color("#CC1100"),
                        color("#880000"),
                        color("#140A0A"),
                        color("#3A0A0A"),
                        color("#0D0606"),
                        color("#110808"),
                        color("#AA0000")
                );

            case "cadillac":
                return new TeamTheme(
                        color("#080C13"),
                        color("#0D0D0D"),
                        color("#0082FA"),
                        color("#0055CC"),
                        color("#0A0E18"),
                        color("#0A2A4A"),
                        color("#06090F"),
                        color("#080C13"),
                        color("#0055CC")
                );

            case "alpine":
                return new TeamTheme(
                        color("#080C13"),
                        color("#0D0D0D"),
                        color("#0082FA"),
                        color("#CC4488"),
                        color("#0A0E18"),
                        color("#0A2A4A"),
                        color("#06090F"),
                        color("#080C13"),
                        color("#0055CC")
                );

            case "williams":
                return new TeamTheme(
                        color("#080B13"),
                        color("#0D0D0D"),
                        color("#005AFF"),
                        color("#003FCC"),
                        color("#0A0D18"),
                        color("#0A2040"),
                        color("#06080F"),
                        color("#080B13"),
                        color("#003FCC")
                );

            case "rb":
                return new TeamTheme(
                        color("#0A0C10"),
                        color("#0D0D0D"),
                        color("#4A7FC1"),
                        color("#1E3A5F"),
                        color("#0C0E14"),
                        color("#1A2A3A"),
                        color("#08090D"),
                        color("#0A0C10"),
                        color("#1E3A5F")
                );

            case "haas":
                return new TeamTheme(
                        color("#101010"),
                        color("#0D0D0D"),
                        color("#C8CACC"),
                        color("#888A8C"),
                        color("#141414"),
                        color("#2A2A2A"),
                        color("#0C0C0C"),
                        color("#101010"),
                        color("#666868")
                );

            default:
                return getDefaultTheme();
        }
    }

    public static TeamTheme getDefaultTheme() {
        return new TeamTheme(
                color("#1A0000"),
                color("#0D0D0D"),
                color("#E10600"),
                color("#B00500"),
                color("#1A1A1A"),
                color("#E10600"),
                color("#0D0D0D"),
                color("#0D0D0D"),
                color("#E10600")
        );
    }

    // ── Resolve current theme ─────────────────────────────────────────────────

    public static TeamTheme getCurrentTheme(Context ctx) {
        if (isTeamTheme(ctx)) {
            String teamId = FavoriteRepository.getFavoriteTeamId(ctx);
            return getTeamTheme(teamId);
        }
        return getDefaultTheme();
    }

    public static int getAccentColor(Context ctx) {
        return getCurrentTheme(ctx).accent;
    }

    // ── Apply full theme to an Activity ──────────────────────────────────────

    /**
     * Call this after setContentView(). Applies background gradient, status bar,
     * nav bar, and returns the theme so callers can tint specific views.
     */
    public static TeamTheme applyFullTheme(Activity activity) {
        TeamTheme theme = getCurrentTheme(activity);

        // Window background gradient (top → bottom)
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{theme.bgTop, theme.bgBottom});
        activity.getWindow().setBackgroundDrawable(bg);

        // Status bar & nav bar
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), true);
        activity.getWindow().setStatusBarColor(theme.statusBar);
        activity.getWindow().setNavigationBarColor(color("#050505"));
        WindowInsetsControllerCompat ctrl = WindowCompat.getInsetsController(
                activity.getWindow(), activity.getWindow().getDecorView());
        ctrl.setAppearanceLightStatusBars(false);
        ctrl.setAppearanceLightNavigationBars(false);

        return theme;
    }

    /**
     * Tints all MaterialCardViews in a ViewGroup with a glass gradient tinted by accent.
     * Also tints MaterialButtons and accent Views tagged with "accent".
     */
    public static void tintViewTree(ViewGroup root, TeamTheme theme) {
        float density = root.getResources().getDisplayMetrics().density;
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof MaterialCardView) {
                MaterialCardView card = (MaterialCardView) child;
                card.setCardBackgroundColor(blendColors(0xFF0D0D0D, theme.accent, 0.07f));
                card.setStrokeColor(blendColors(0xFF222222, theme.accent, 0.28f));
                tintViewTree(card, theme);
            } else if (child instanceof MaterialButton) {
                ((MaterialButton) child).setBackgroundTintList(
                        ColorStateList.valueOf(theme.buttonBg));
            } else if (child instanceof ViewGroup) {
                tintViewTree((ViewGroup) child, theme);
            }
            if ("accent".equals(child.getTag())) {
                child.setBackgroundColor(theme.accent);
            }
        }
    }

    /**
     * Apply accent color to a NavigationView (icon tint + header background).
     */
    public static void tintNavView(NavigationView navView, TeamTheme theme) {
        navView.setBackgroundColor(theme.navBg);
        navView.setItemIconTintList(ColorStateList.valueOf(theme.accent));
        View header = navView.getHeaderView(0);
        if (header != null) {
            // Gradient header background
            GradientDrawable headerBg = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{blendColors(theme.bgTop, theme.accent, 0.25f), theme.navBg});
            header.setBackground(headerBg);
            View stripe = header.findViewById(R.id.viewHeaderAccent);
            if (stripe != null) stripe.setBackgroundColor(theme.accent);
        }
    }

    // ── Persist / read ────────────────────────────────────────────────────────

    public static void setThemeMode(Context ctx, String mode) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
           .edit().putString(KEY_THEME_MODE, mode).apply();
    }

    public static String getThemeMode(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                  .getString(KEY_THEME_MODE, THEME_DEFAULT);
    }

    public static boolean isTeamTheme(Context ctx) {
        return THEME_TEAM.equals(getThemeMode(ctx));
    }

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK, true);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    // kept for backward compat — delegates to applyFullTheme
    public static void applyStatusBar(Activity activity) {
        applyFullTheme(activity);
    }

    public static void applyStatusBarWithAccent(Activity activity) {
        applyFullTheme(activity);
    }

    // ── Gradient helpers ──────────────────────────────────────────────────────

    public static GradientDrawable makeAccentGradient(Context ctx, float cornerDp) {
        TeamTheme theme = getCurrentTheme(ctx);
        int r = Color.red(theme.accent), g = Color.green(theme.accent), b = Color.blue(theme.accent);
        int blendedR = (10 + Math.min(r + 20, 255)) / 2;
        int blendedG = (10 + Math.min(g + 20, 255)) / 2;
        int blendedB = (10 + Math.min(b + 20, 255)) / 2;
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.argb(255, 10, 10, 10), Color.argb(255, blendedR, blendedG, blendedB)});
        gd.setCornerRadius(cornerDp * ctx.getResources().getDisplayMetrics().density);
        return gd;
    }

    /** Semi-transparent glass card drawable tinted with accent. */
    public static GradientDrawable makeGlassCard(int accent, float cornerDp, float density) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        blendColors(0xFF0D0D0D, accent, 0.10f),
                        blendColors(0xFF111111, accent, 0.05f)
                });
        gd.setCornerRadius(cornerDp * density);
        gd.setStroke((int) density, blendColors(0xFF222222, accent, 0.30f));
        return gd;
    }

    /** Returns a glow color: accent at ~20% alpha. */
    public static int glowColor(int accent) {
        return (accent & 0x00FFFFFF) | 0x33000000;
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    public static int blendColors(int color1, int color2, float ratio) {
        float inv = 1f - ratio;
        return Color.argb(
                (int)(Color.alpha(color1) * inv + Color.alpha(color2) * ratio),
                (int)(Color.red(color1)   * inv + Color.red(color2)   * ratio),
                (int)(Color.green(color1) * inv + Color.green(color2) * ratio),
                (int)(Color.blue(color1)  * inv + Color.blue(color2)  * ratio));
    }

    private static int color(String hex) {
        return Color.parseColor(hex);
    }
}
