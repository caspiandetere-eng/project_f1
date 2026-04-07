package com.example.project_f1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import com.example.project_f1.models.JolpicaScheduleResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class RaceNotificationScheduler {

    // How many ms before race time each alert fires
    private static final long[] OFFSETS_MS = {
        3 * 24 * 60 * 60 * 1000L,   // 3 days before
            24 * 60 * 60 * 1000L,   // 1 day before
             1 * 60 * 60 * 1000L    // 1 hour before
    };

    // Matching type tags sent to the receiver
    public static final String[] OFFSET_TAGS = {"3days", "1day", "1hour"};

    static final String EXTRA_RACE_NAME    = "race_name";
    static final String EXTRA_COUNTRY      = "country";
    static final String EXTRA_ROUND        = "round";
    static final String EXTRA_OFFSET_TAG   = "offset_tag";
    static final String EXTRA_IS_SPRINT    = "is_sprint";
    static final String EXTRA_RACE_DATE    = "race_date";

    private static final String PREFS_NOTIF = "F1NotifPrefs";
    private static final String KEY_SCHEDULED = "scheduled_version";

    /**
     * Call this from MainActivity after the schedule is loaded.
     * Skips scheduling if already done for this season version.
     */
    public static void scheduleAll(Context ctx, JolpicaScheduleResponse response) {
        if (response == null
                || response.mrData == null
                || response.mrData.raceTable == null
                || response.mrData.raceTable.races == null) return;

        List<JolpicaScheduleResponse.Race> races = response.mrData.raceTable.races;
        if (races.isEmpty()) return;

        // Respect the user's notification preference
        boolean enabled = ctx.getSharedPreferences("F1Prefs", Context.MODE_PRIVATE)
                .getBoolean("notifications_enabled", true);
        if (!enabled) return;

        // Per-timing preferences
        SharedPreferences f1Prefs = ctx.getSharedPreferences("F1Prefs", Context.MODE_PRIVATE);
        boolean[] timingEnabled = {
            f1Prefs.getBoolean("notif_3days", true),
            f1Prefs.getBoolean("notif_1day",  true),
            f1Prefs.getBoolean("notif_1hour", true)
        };

        // Use race count as a lightweight version key — reschedule only if calendar changed
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE);
        int version = races.size();
        if (prefs.getInt(KEY_SCHEDULED, -1) == version) return;

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        long now = System.currentTimeMillis();

        for (JolpicaScheduleResponse.Race race : races) {
            long raceEpoch = parseRaceEpoch(race.date, race.time);
            if (raceEpoch <= 0 || raceEpoch < now) continue;

            String country = (race.circuit != null && race.circuit.location != null)
                    ? race.circuit.location.country : "";
            boolean isSprint = race.sprint != null;

            for (int i = 0; i < OFFSETS_MS.length; i++) {
                if (!timingEnabled[i]) continue;  // skip if this timing is turned off
                long fireAt = raceEpoch - OFFSETS_MS[i];
                if (fireAt <= now) continue;

                int requestCode = buildRequestCode(race.round, i);
                Intent intent = new Intent(ctx, RaceNotificationReceiver.class);
                intent.putExtra(EXTRA_RACE_NAME,  race.raceName != null ? race.raceName : "Grand Prix");
                intent.putExtra(EXTRA_COUNTRY,    country);
                intent.putExtra(EXTRA_ROUND,      race.round != null ? race.round : "");
                intent.putExtra(EXTRA_OFFSET_TAG, OFFSET_TAGS[i]);
                intent.putExtra(EXTRA_IS_SPRINT,  isSprint);
                intent.putExtra(EXTRA_RACE_DATE,  race.date != null ? race.date : "");

                PendingIntent pi = PendingIntent.getBroadcast(
                        ctx, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                            && !am.canScheduleExactAlarms()) {
                        am.set(AlarmManager.RTC_WAKEUP, fireAt, pi);
                    } else {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fireAt, pi);
                    }
                } catch (SecurityException e) {
                    am.set(AlarmManager.RTC_WAKEUP, fireAt, pi);
                }
            }
        }

        prefs.edit().putInt(KEY_SCHEDULED, version).apply();
    }

    /** Cancel all scheduled notifications (e.g. on logout). */
    public static void cancelAll(Context ctx, JolpicaScheduleResponse response) {
        if (response == null
                || response.mrData == null
                || response.mrData.raceTable == null
                || response.mrData.raceTable.races == null) return;

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        for (JolpicaScheduleResponse.Race race : response.mrData.raceTable.races) {
            for (int i = 0; i < OFFSETS_MS.length; i++) {
                int requestCode = buildRequestCode(race.round, i);
                Intent intent = new Intent(ctx, RaceNotificationReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(
                        ctx, requestCode, intent,
                        PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
                if (pi != null) {
                    am.cancel(pi);
                    pi.cancel();
                }
            }
        }

        ctx.getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE)
                .edit().remove(KEY_SCHEDULED).apply();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static long parseRaceEpoch(String date, String time) {
        try {
            String iso = date + "T" + (time != null ? time : "13:00:00Z");
            return Instant.parse(iso).toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }

    private static int buildRequestCode(String round, int offsetIndex) {
        int r = 0;
        try { r = Integer.parseInt(round); } catch (Exception ignored) {}
        // Unique per round + offset: round 1-24, offset 0-2 → codes 100-172
        return r * 10 + offsetIndex;
    }
}
