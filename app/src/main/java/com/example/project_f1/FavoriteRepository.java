package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;

public class FavoriteRepository {
    private static final String PREFS_NAME = "F1Prefs";
    private static final String KEY_FAVORITE_TEAM = "favorite_team";
    private static final String KEY_FAVORITE_DRIVER = "favorite_driver";
    private static final String KEY_FAVORITE_TEAM_NAME = "favorite_team_name";
    private static final String KEY_FAVORITE_DRIVER_NAME = "favorite_driver_name";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Call once on app start to migrate any stale favorites from the old separate prefs file. */
    public static void migrateLegacyPrefs(Context context) {
        SharedPreferences legacy = context.getSharedPreferences("F1Favorites", Context.MODE_PRIVATE);
        if (legacy.contains(KEY_FAVORITE_TEAM) || legacy.contains(KEY_FAVORITE_DRIVER)) {
            legacy.edit().clear().apply();
        }
    }

    /**
     * Save favorite team
     */
    public static void setFavoriteTeam(Context context, String teamId, String teamName) {
        SharedPreferences prefs = getPrefs(context);
        prefs.edit()
            .putString(KEY_FAVORITE_TEAM, teamId)
            .putString(KEY_FAVORITE_TEAM_NAME, teamName)
            .apply();
    }

    /**
     * Get favorite team ID
     */
    public static String getFavoriteTeamId(Context context) {
        return getPrefs(context).getString(KEY_FAVORITE_TEAM, null);
    }

    /**
     * Get favorite team name
     */
    public static String getFavoriteTeamName(Context context) {
        return getPrefs(context).getString(KEY_FAVORITE_TEAM_NAME, null);
    }

    /**
     * Check if user has selected a favorite team
     */
    public static boolean hasFavoriteTeam(Context context) {
        return getFavoriteTeamId(context) != null;
    }

    /**
     * Save favorite driver
     */
    public static void setFavoriteDriver(Context context, String driverId, String driverName) {
        SharedPreferences prefs = getPrefs(context);
        prefs.edit()
            .putString(KEY_FAVORITE_DRIVER, driverId)
            .putString(KEY_FAVORITE_DRIVER_NAME, driverName)
            .apply();
    }

    /**
     * Get favorite driver ID
     */
    public static String getFavoriteDriverId(Context context) {
        return getPrefs(context).getString(KEY_FAVORITE_DRIVER, null);
    }

    /**
     * Get favorite driver name
     */
    public static String getFavoriteDriverName(Context context) {
        return getPrefs(context).getString(KEY_FAVORITE_DRIVER_NAME, null);
    }

    /**
     * Check if user has selected a favorite driver
     */
    public static boolean hasFavoriteDriver(Context context) {
        return getFavoriteDriverId(context) != null;
    }

    /**
     * Check if both favorites are selected
     */
    public static boolean hasBothFavorites(Context context) {
        return hasFavoriteTeam(context) && hasFavoriteDriver(context);
    }

    /**
     * Clear all favorites
     */
    public static void clearFavorites(Context context) {
        SharedPreferences prefs = getPrefs(context);
        prefs.edit()
            .remove(KEY_FAVORITE_TEAM)
            .remove(KEY_FAVORITE_DRIVER)
            .remove(KEY_FAVORITE_TEAM_NAME)
            .remove(KEY_FAVORITE_DRIVER_NAME)
            .apply();
    }
}
