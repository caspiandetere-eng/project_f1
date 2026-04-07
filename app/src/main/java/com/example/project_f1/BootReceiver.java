package com.example.project_f1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.project_f1.models.JolpicaScheduleResponse;
import com.google.gson.Gson;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                && !"android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) return;

        // Only reschedule if the user is logged in
        SharedPreferences prefs = ctx.getSharedPreferences("F1Prefs", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) return;

        // Load cached schedule and reschedule alarms
        String cached = CacheManager.getCache(ctx, "schedule_2026");
        if (cached == null) return;

        try {
            JolpicaScheduleResponse response = new Gson().fromJson(cached, JolpicaScheduleResponse.class);
            // Reset the version key so scheduleAll runs again after reboot
            ctx.getSharedPreferences("F1NotifPrefs", Context.MODE_PRIVATE)
                    .edit().remove("scheduled_version").apply();
            RaceNotificationScheduler.scheduleAll(ctx, response);
        } catch (Exception ignored) {}
    }
}
