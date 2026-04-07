package com.example.project_f1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

public class RaceNotificationReceiver extends BroadcastReceiver {

    static final String CHANNEL_ID   = "f1_race_alerts";
    static final String CHANNEL_NAME = "Race Alerts";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String raceName   = intent.getStringExtra(RaceNotificationScheduler.EXTRA_RACE_NAME);
        String country    = intent.getStringExtra(RaceNotificationScheduler.EXTRA_COUNTRY);
        String round      = intent.getStringExtra(RaceNotificationScheduler.EXTRA_ROUND);
        String offsetTag  = intent.getStringExtra(RaceNotificationScheduler.EXTRA_OFFSET_TAG);
        boolean isSprint  = intent.getBooleanExtra(RaceNotificationScheduler.EXTRA_IS_SPRINT, false);
        String raceDate   = intent.getStringExtra(RaceNotificationScheduler.EXTRA_RACE_DATE);

        if (raceName == null) return;

        NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        ensureChannel(nm);

        // ── Resolve accent color from saved favorite team ─────────────────────
        String favTeamId = FavoriteRepository.getFavoriteTeamId(ctx);
        int accentColor  = ThemeManager.getTeamTheme(favTeamId != null ? favTeamId : "").accent;

        // ── Build copy based on how far away the race is ──────────────────────
        NotificationCopy copy = buildCopy(raceName, country, round, offsetTag, isSprint, raceDate, accentColor);

        // ── Large icon: custom-drawn banner ───────────────────────────────────
        Bitmap largeIcon = drawNotificationBanner(ctx, copy, accentColor);

        // ── Tap action → RaceCenterActivity ──────────────────────────────────
        Intent tapIntent = new Intent(ctx, RaceCenterActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent tapPi = PendingIntent.getActivity(
                ctx, copy.notifId, tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // ── Build notification ────────────────────────────────────────────────
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.f1)
                .setLargeIcon(largeIcon)
                .setContentTitle(copy.title)
                .setContentText(copy.body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(copy.bigText)
                        .setSummaryText(copy.summary))
                .setColor(accentColor)
                .setColorized(true)
                .setContentIntent(tapPi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        // Action button — "View Calendar"
        Intent actionIntent = new Intent(ctx, RaceCenterActivity.class);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPi = PendingIntent.getActivity(
                ctx, copy.notifId + 500, actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(R.drawable.f1, "View Calendar", actionPi);

        nm.notify(copy.notifId, builder.build());
    }

    // ── Notification copy factory ─────────────────────────────────────────────

    private static class NotificationCopy {
        String title, body, bigText, summary;
        int notifId;
    }

    private NotificationCopy buildCopy(String raceName, String country, String round,
                                        String offsetTag, boolean isSprint,
                                        String raceDate, int accentColor) {
        NotificationCopy c = new NotificationCopy();

        // Strip "Grand Prix" for shorter display
        String shortName = raceName.replace(" Grand Prix", "").replace(" GRAND PRIX", "").trim();
        String flag      = countryFlag(country);
        String sprintTag = isSprint ? " 🟣 Sprint Weekend" : "";
        String roundStr  = round != null && !round.isEmpty() ? "Round " + round : "";

        int roundNum = 0;
        try { roundNum = Integer.parseInt(round); } catch (Exception ignored) {}

        switch (offsetTag != null ? offsetTag : "") {

            case "3days":
                c.notifId = roundNum * 10;
                c.title   = flag + " " + shortName + " GP — 3 Days to Go 🏁";
                c.body    = "Engines warm up in 72 hours. Are you ready?";
                c.bigText = "The " + raceName + " is just 3 days away." + sprintTag
                        + "\n\n" + roundStr + " of the 2026 FIA Formula One World Championship."
                        + "\n\nCheck the full weekend schedule, circuit map, and lap records in the app.";
                c.summary = country + " • 2026 Season";
                break;

            case "1day":
                c.notifId = roundNum * 10 + 1;
                c.title   = flag + " TOMORROW: " + shortName + " Grand Prix 🔥";
                c.body    = "Lights out in less than 24 hours. Set your alarm.";
                c.bigText = "Race day is tomorrow for the " + raceName + "." + sprintTag
                        + "\n\n📍 " + country
                        + "\n🏆 " + roundStr + " · 2026 Season"
                        + "\n\nDon't miss the formation lap. Open the app to check session times and circuit details.";
                c.summary = "Race day tomorrow";
                break;

            case "1hour":
                c.notifId = roundNum * 10 + 2;
                c.title   = flag + " 1 HOUR TO LIGHTS OUT 🚦";
                c.body    = shortName + " GP starts in 60 minutes. Brace yourself.";
                c.bigText = "🚦 The " + raceName + " starts in approximately 1 hour."
                        + sprintTag
                        + "\n\n📍 " + country + "  •  " + roundStr
                        + "\n\nFind your seat. The grid is forming. "
                        + "Open the app for circuit info and driver standings.";
                c.summary = "Race starting soon";
                break;

            default:
                c.notifId = roundNum * 10;
                c.title   = flag + " " + raceName;
                c.body    = "Race reminder";
                c.bigText = raceName + " is coming up.";
                c.summary = country;
        }

        return c;
    }

    // ── Custom large-icon bitmap ──────────────────────────────────────────────

    /**
     * Draws a 256×256 bitmap with:
     *  - team-accent gradient background
     *  - subtle dot grid texture
     *  - round number badge
     *  - flag emoji + short race name
     *  - offset label (3 DAYS / TOMORROW / 1 HOUR)
     */
    private Bitmap drawNotificationBanner(Context ctx, NotificationCopy copy, int accentColor) {
        int size = 256;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // Background gradient
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int darkColor = blendColor(accentColor, 0xFF000000, 0.72f);
        bgPaint.setShader(new LinearGradient(0, 0, size, size,
                blendColor(accentColor, 0xFF000000, 0.45f), darkColor,
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(0, 0, size, size), 28, 28, bgPaint);

        // Dot grid texture (subtle)
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(blendColor(accentColor, 0xFFFFFFFF, 0.55f));
        dotPaint.setAlpha(30);
        for (int row = 0; row * 14 < size + 14; row++) {
            for (int col = 0; col * 14 < size + 14; col++) {
                canvas.drawCircle(col * 14, row * 14, 1.4f, dotPaint);
            }
        }

        // Accent stripe at top
        Paint stripePaint = new Paint();
        stripePaint.setColor(accentColor);
        canvas.drawRect(0, 0, size, 8, stripePaint);

        // Offset label badge (e.g. "3 DAYS" / "TOMORROW" / "1 HOUR")
        String badgeText = offsetBadgeText(copy.notifId % 10);
        Paint badgeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgeBgPaint.setColor(accentColor);
        float badgeW = 90, badgeH = 26;
        float badgeX = size - badgeW - 12, badgeY = 18;
        canvas.drawRoundRect(new RectF(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH),
                13, 13, badgeBgPaint);

        Paint badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgeTextPaint.setColor(0xFF000000);
        badgeTextPaint.setTextSize(13f);
        badgeTextPaint.setFakeBoldText(true);
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(badgeText, badgeX + badgeW / 2f, badgeY + 18f, badgeTextPaint);

        // Flag emoji (large, centered-ish)
        Paint flagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        flagPaint.setTextSize(52f);
        flagPaint.setTextAlign(Paint.Align.LEFT);
        String flag = extractFlag(copy.title);
        canvas.drawText(flag, 16, 110, flagPaint);

        // Race name (short, bold white)
        Paint namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        namePaint.setColor(0xFFFFFFFF);
        namePaint.setTextSize(22f);
        namePaint.setFakeBoldText(true);
        namePaint.setTextAlign(Paint.Align.LEFT);
        String shortName = extractShortName(copy.title);
        canvas.drawText(shortName, 16, 148, namePaint);

        // "GRAND PRIX" sub-label
        Paint subPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subPaint.setColor(blendColor(accentColor, 0xFFFFFFFF, 0.3f));
        subPaint.setTextSize(13f);
        subPaint.setLetterSpacing(0.12f);
        subPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("GRAND PRIX  •  2026", 16, 170, subPaint);

        // Bottom accent line
        Paint linePaint = new Paint();
        linePaint.setColor(accentColor);
        linePaint.setAlpha(120);
        linePaint.setStrokeWidth(2f);
        canvas.drawLine(16, 190, size - 16, 190, linePaint);

        // Lights-out icon row
        Paint lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int[] lightColors = {0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000};
        for (int i = 0; i < 5; i++) {
            lightPaint.setColor(lightColors[i]);
            lightPaint.setAlpha(copy.notifId % 10 == 2 ? 255 : 80); // full brightness on 1-hour notif
            canvas.drawCircle(16 + i * 22, 218, 8, lightPaint);
        }

        // "LIGHTS OUT" text
        Paint lightsTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightsTextPaint.setColor(0xFF888888);
        lightsTextPaint.setTextSize(11f);
        lightsTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("LIGHTS OUT", 130, 222, lightsTextPaint);

        return bmp;
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private void ensureChannel(NotificationManager nm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Reminders before each Formula 1 race");
            ch.enableLights(true);
            ch.setLightColor(Color.RED);
            ch.enableVibration(true);
            ch.setVibrationPattern(new long[]{0, 250, 100, 250});
            ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(ch);
        }
    }

    private String offsetBadgeText(int offsetIndex) {
        switch (offsetIndex) {
            case 0: return "3 DAYS";
            case 1: return "TOMORROW";
            case 2: return "1 HOUR";
            default: return "SOON";
        }
    }

    /** Extracts the leading flag emoji from the notification title. */
    private String extractFlag(String title) {
        if (title == null || title.isEmpty()) return "🏁";
        // Flag emojis are 2 code points (regional indicator pairs), take first grapheme cluster
        try {
            java.text.BreakIterator it = java.text.BreakIterator.getCharacterInstance();
            it.setText(title);
            int end = it.next();
            return title.substring(0, end).trim();
        } catch (Exception e) {
            return "🏁";
        }
    }

    /** Extracts the race short name (after the flag, before the dash). */
    private String extractShortName(String title) {
        if (title == null) return "";
        // Remove leading flag cluster
        try {
            java.text.BreakIterator it = java.text.BreakIterator.getCharacterInstance();
            it.setText(title);
            int flagEnd = it.next();
            String rest = title.substring(flagEnd).trim();
            // Take up to the first " —" or " GP" or end
            int dash = rest.indexOf(" —");
            if (dash > 0) rest = rest.substring(0, dash).trim();
            int gp = rest.indexOf(" GP");
            if (gp > 0) rest = rest.substring(0, gp).trim();
            // Truncate to 14 chars for the bitmap
            return rest.length() > 14 ? rest.substring(0, 13) + "…" : rest;
        } catch (Exception e) {
            return title.length() > 14 ? title.substring(0, 13) + "…" : title;
        }
    }

    private static String countryFlag(String country) {
        if (country == null) return "🏁";
        switch (country.toLowerCase()) {
            case "australia":     return "🇦🇺";
            case "china":         return "🇨🇳";
            case "japan":         return "🇯🇵";
            case "bahrain":       return "🇧🇭";
            case "saudi arabia":  return "🇸🇦";
            case "usa":
            case "united states": return "🇺🇸";
            case "canada":        return "🇨🇦";
            case "monaco":        return "🇲🇨";
            case "spain":         return "🇪🇸";
            case "austria":       return "🇦🇹";
            case "great britain":
            case "uk":            return "🇬🇧";
            case "belgium":       return "🇧🇪";
            case "hungary":       return "🇭🇺";
            case "netherlands":   return "🇳🇱";
            case "italy":         return "🇮🇹";
            case "azerbaijan":    return "🇦🇿";
            case "singapore":     return "🇸🇬";
            case "mexico":        return "🇲🇽";
            case "brazil":        return "🇧🇷";
            case "qatar":         return "🇶🇦";
            case "uae":
            case "abu dhabi":     return "🇦🇪";
            default:              return "🏁";
        }
    }

    private static int blendColor(int c1, int c2, float ratio) {
        float inv = 1f - ratio;
        return Color.argb(
                (int)(Color.alpha(c1) * inv + Color.alpha(c2) * ratio),
                (int)(Color.red(c1)   * inv + Color.red(c2)   * ratio),
                (int)(Color.green(c1) * inv + Color.green(c2) * ratio),
                (int)(Color.blue(c1)  * inv + Color.blue(c2)  * ratio));
    }
}
