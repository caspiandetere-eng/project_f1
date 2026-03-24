package com.example.project_f1;

import android.graphics.Color;

public class TeamThemeHelper {

    public static class TeamColors {
        public final int gradientStart;
        public final int gradientEnd;
        public final int accentColor;

        public TeamColors(int gradientStart, int gradientEnd, int accentColor) {
            this.gradientStart = gradientStart;
            this.gradientEnd = gradientEnd;
            this.accentColor = accentColor;
        }
    }

    /**
     * Returns team-specific gradient and accent colors for driver cards.
     * Gradient flows left → right for visual depth.
     */
    public static TeamColors getTeamColors(String teamId) {
        if (teamId == null) return getDefaultColors();

        switch (teamId.toLowerCase()) {
            case "mercedes":
                return new TeamColors(
                        color("#0D1210"),
                        color("#1A4A44"),
                        color("#00D2BE")
                );

            case "ferrari":
                return new TeamColors(
                        color("#130808"),
                        color("#4A1010"),
                        color("#DC0000")
                );

            case "mclaren":
                return new TeamColors(
                        color("#130F08"),
                        color("#4A3000"),
                        color("#FF8700")
                );

            case "red_bull":
                return new TeamColors(
                        color("#080A13"),
                        color("#1A2A4A"),
                        color("#3671C6")
                );

            case "aston_martin":
                return new TeamColors(
                        color("#081210"),
                        color("#0F3A34"),
                        color("#00A693")
                );

            case "audi":
                return new TeamColors(
                        color("#110808"),
                        color("#3A0A0A"),
                        color("#CC1100")
                );

            case "cadillac":
                return new TeamColors(
                        color("#080C13"),
                        color("#0A2A4A"),
                        color("#0082FA")
                );

            case "alpine":
                return new TeamColors(
                        color("#080C13"),
                        color("#0A2A4A"),
                        color("#0082FA")
                );

            case "williams":
                return new TeamColors(
                        color("#080B13"),
                        color("#0A2040"),
                        color("#005AFF")
                );

            case "rb":
                return new TeamColors(
                        color("#0A0C10"),
                        color("#1A2A3A"),
                        color("#4A7FC1")
                );

            case "haas":
                return new TeamColors(
                        color("#101010"),
                        color("#2A2A2A"),
                        color("#C8CACC")
                );

            default:
                return getDefaultColors();
        }
    }

    public static TeamColors getDefaultColors() {
        return new TeamColors(
                color("#1A0000"),
                color("#4A0000"),
                color("#E10600")
        );
    }

    /**
     * Blend two colors with a ratio (0.0 = color1, 1.0 = color2)
     */
    public static int blendColors(int color1, int color2, float ratio) {
        float inv = 1f - ratio;
        return Color.argb(
                (int) (Color.alpha(color1) * inv + Color.alpha(color2) * ratio),
                (int) (Color.red(color1) * inv + Color.red(color2) * ratio),
                (int) (Color.green(color1) * inv + Color.green(color2) * ratio),
                (int) (Color.blue(color1) * inv + Color.blue(color2) * ratio)
        );
    }

    /**
     * Create a glow color from accent (lighter, semi-transparent)
     */
    public static int createGlowColor(int accentColor) {
        int r = Color.red(accentColor);
        int g = Color.green(accentColor);
        int b = Color.blue(accentColor);
        // Lighten and make semi-transparent
        r = Math.min(r + 60, 255);
        g = Math.min(g + 60, 255);
        b = Math.min(b + 60, 255);
        return Color.argb(80, r, g, b);
    }

    private static int color(String hex) {
        return Color.parseColor(hex);
    }
}
