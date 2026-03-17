package com.example.project_f1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.project_f1.models.JolpicaStandingsResponse;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public static class User {
        public final int id;
        public final String name;
        public final String email;
        public final String level;

        User(int id, String name, String email, String level) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.level = level;
        }
    }

    // Returns user id on success, -1 if email already exists
    public static long register(Context ctx, String name, String email, String password) {
        SQLiteDatabase db = F1Database.get(ctx).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(F1Database.COL_NAME, name);
        cv.put(F1Database.COL_EMAIL, email);
        cv.put(F1Database.COL_PASSWORD, password);
        try {
            return db.insertOrThrow(F1Database.TABLE_USERS, null, cv);
        } catch (Exception e) {
            return -1; // duplicate email
        }
    }

    // Returns User on success, null if credentials wrong
    public static User login(Context ctx, String email, String password) {
        SQLiteDatabase db = F1Database.get(ctx).getReadableDatabase();
        Cursor c = db.query(F1Database.TABLE_USERS, null,
            F1Database.COL_EMAIL + "=? AND " + F1Database.COL_PASSWORD + "=?",
            new String[]{email, password}, null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = new User(
                c.getInt(c.getColumnIndexOrThrow(F1Database.COL_ID)),
                c.getString(c.getColumnIndexOrThrow(F1Database.COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(F1Database.COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(F1Database.COL_LEVEL))
            );
        }
        c.close();
        return user;
    }

    public static User getById(Context ctx, int userId) {
        SQLiteDatabase db = F1Database.get(ctx).getReadableDatabase();
        Cursor c = db.query(F1Database.TABLE_USERS, null,
            F1Database.COL_ID + "=?",
            new String[]{String.valueOf(userId)}, null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = new User(
                c.getInt(c.getColumnIndexOrThrow(F1Database.COL_ID)),
                c.getString(c.getColumnIndexOrThrow(F1Database.COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(F1Database.COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(F1Database.COL_LEVEL))
            );
        }
        c.close();
        return user;
    }

    public static void updateLevel(Context ctx, int userId, String level) {
        SQLiteDatabase db = F1Database.get(ctx).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(F1Database.COL_LEVEL, level);
        db.update(F1Database.TABLE_USERS, cv, F1Database.COL_ID + "=?",
            new String[]{String.valueOf(userId)});
    }

    // ── Standings ─────────────────────────────────────────────────────────────

    public static class StandingRow {
        public final int position;
        public final String driverId;
        public final String givenName;
        public final String familyName;
        public final String points;
        public final String wins;
        public final String constructor;

        StandingRow(int position, String driverId, String givenName,
                    String familyName, String points, String wins, String constructor) {
            this.position = position;
            this.driverId = driverId;
            this.givenName = givenName;
            this.familyName = familyName;
            this.points = points;
            this.wins = wins;
            this.constructor = constructor;
        }
    }

    /** Persist a full season's standings + driver info from the API response. */
    public static void saveStandings(Context ctx, int season, JolpicaStandingsResponse response) {
        if (response == null
                || response.mrData == null
                || response.mrData.standingsTable == null
                || response.mrData.standingsTable.standingsLists == null
                || response.mrData.standingsTable.standingsLists.isEmpty()) return;

        SQLiteDatabase db = F1Database.get(ctx).getWritableDatabase();
        long now = System.currentTimeMillis();

        db.beginTransaction();
        try {
            // Remove stale rows for this season so we get a clean upsert
            db.delete(F1Database.TABLE_STANDINGS,
                F1Database.COL_SEASON + "=?", new String[]{String.valueOf(season)});

            for (JolpicaStandingsResponse.DriverStanding ds
                    : response.mrData.standingsTable.standingsLists.get(0).driverStandings) {

                if (ds.driver == null) continue;

                // Upsert driver
                ContentValues dv = new ContentValues();
                dv.put(F1Database.COL_DRIVER_ID,     ds.driver.driverId != null ? ds.driver.driverId : ds.driver.familyName);
                dv.put(F1Database.COL_GIVEN_NAME,    ds.driver.givenName);
                dv.put(F1Database.COL_FAMILY_NAME,   ds.driver.familyName);
                dv.put(F1Database.COL_NATIONALITY,   ds.driver.nationality);
                dv.put(F1Database.COL_DATE_OF_BIRTH, ds.driver.dateOfBirth);
                db.insertWithOnConflict(F1Database.TABLE_DRIVERS, null, dv, SQLiteDatabase.CONFLICT_REPLACE);

                // Insert standing row
                ContentValues sv = new ContentValues();
                sv.put(F1Database.COL_SEASON,      season);
                sv.put(F1Database.COL_POSITION,    ds.position != null ? Integer.parseInt(ds.position) : 0);
                sv.put(F1Database.COL_DRIVER_ID,   dv.getAsString(F1Database.COL_DRIVER_ID));
                sv.put(F1Database.COL_POINTS,      ds.points != null ? ds.points : "0");
                sv.put(F1Database.COL_WINS,        ds.wins != null ? ds.wins : "0");
                sv.put(F1Database.COL_CONSTRUCTOR, ds.getConstructor());
                sv.put(F1Database.COL_UPDATED_AT,  now);
                db.insertWithOnConflict(F1Database.TABLE_STANDINGS, null, sv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /** Returns cached standings for a season, or empty list if none stored. */
    public static List<StandingRow> getStandings(Context ctx, int season) {
        SQLiteDatabase db = F1Database.get(ctx).getReadableDatabase();
        List<StandingRow> rows = new ArrayList<>();

        String sql = "SELECT s." + F1Database.COL_POSITION
            + ", s." + F1Database.COL_DRIVER_ID
            + ", d." + F1Database.COL_GIVEN_NAME
            + ", d." + F1Database.COL_FAMILY_NAME
            + ", s." + F1Database.COL_POINTS
            + ", s." + F1Database.COL_WINS
            + ", s." + F1Database.COL_CONSTRUCTOR
            + " FROM " + F1Database.TABLE_STANDINGS + " s"
            + " LEFT JOIN " + F1Database.TABLE_DRIVERS + " d"
            + " ON s." + F1Database.COL_DRIVER_ID + " = d." + F1Database.COL_DRIVER_ID
            + " WHERE s." + F1Database.COL_SEASON + " = ?"
            + " ORDER BY s." + F1Database.COL_POSITION + " ASC";

        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(season)});
        while (c.moveToNext()) {
            rows.add(new StandingRow(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getString(3),
                c.getString(4),
                c.getString(5),
                c.getString(6)
            ));
        }
        c.close();
        return rows;
    }

    /** True if we have standings stored for this season. */
    public static boolean hasStandings(Context ctx, int season) {
        SQLiteDatabase db = F1Database.get(ctx).getReadableDatabase();
        Cursor c = db.query(F1Database.TABLE_STANDINGS, new String[]{F1Database.COL_ID},
            F1Database.COL_SEASON + "=?", new String[]{String.valueOf(season)},
            null, null, null, "1");
        boolean has = c.moveToFirst();
        c.close();
        return has;
    }

    /** True if stored standings are older than maxAgeMs or don't exist. */
    public static boolean isStandingsStale(Context ctx, int season, long maxAgeMs) {
        SQLiteDatabase db = F1Database.get(ctx).getReadableDatabase();
        Cursor c = db.query(F1Database.TABLE_STANDINGS,
            new String[]{"MAX(" + F1Database.COL_UPDATED_AT + ")"},
            F1Database.COL_SEASON + "=?", new String[]{String.valueOf(season)},
            null, null, null);
        long updatedAt = 0;
        if (c.moveToFirst()) updatedAt = c.getLong(0);
        c.close();
        return updatedAt == 0 || (System.currentTimeMillis() - updatedAt) > maxAgeMs;
    }

    // Favorites
    public static void addFavorite(Context ctx, int userId, String type, String itemId, String itemName) {
        SQLiteDatabase db = F1Database.get(ctx).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(F1Database.COL_USER_ID, userId);
        cv.put(F1Database.COL_TYPE, type);
        cv.put(F1Database.COL_ITEM_ID, itemId);
        cv.put(F1Database.COL_ITEM_NAME, itemName);
        db.insertWithOnConflict(F1Database.TABLE_FAVORITES, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static void removeFavorite(Context ctx, int userId, String type, String itemId) {
        SQLiteDatabase db = F1Database.get(ctx).getWritableDatabase();
        db.delete(F1Database.TABLE_FAVORITES,
            F1Database.COL_USER_ID + "=? AND " + F1Database.COL_TYPE + "=? AND " + F1Database.COL_ITEM_ID + "=?",
            new String[]{String.valueOf(userId), type, itemId});
    }

    public static boolean isFavorite(Context ctx, int userId, String type, String itemId) {
        SQLiteDatabase db = F1Database.get(ctx).getReadableDatabase();
        Cursor c = db.query(F1Database.TABLE_FAVORITES, new String[]{F1Database.COL_ID},
            F1Database.COL_USER_ID + "=? AND " + F1Database.COL_TYPE + "=? AND " + F1Database.COL_ITEM_ID + "=?",
            new String[]{String.valueOf(userId), type, itemId}, null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }
}
