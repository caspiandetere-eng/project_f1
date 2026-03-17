package com.example.project_f1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class F1Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "f1_app.db";
    private static final int DB_VERSION = 2;

    // users
    static final String TABLE_USERS    = "users";
    static final String COL_ID         = "id";
    static final String COL_NAME       = "name";
    static final String COL_EMAIL      = "email";
    static final String COL_PASSWORD   = "password";
    static final String COL_LEVEL      = "knowledge_level";

    // favorites
    static final String TABLE_FAVORITES = "favorites";
    static final String COL_USER_ID     = "user_id";
    static final String COL_TYPE        = "type";
    static final String COL_ITEM_ID     = "item_id";
    static final String COL_ITEM_NAME   = "item_name";

    // drivers
    static final String TABLE_DRIVERS      = "drivers";
    static final String COL_DRIVER_ID      = "driver_id";   // e.g. "hamilton"
    static final String COL_GIVEN_NAME     = "given_name";
    static final String COL_FAMILY_NAME    = "family_name";
    static final String COL_NATIONALITY    = "nationality";
    static final String COL_DATE_OF_BIRTH  = "date_of_birth";

    // standings
    static final String TABLE_STANDINGS  = "standings";
    static final String COL_SEASON       = "season";
    static final String COL_POSITION     = "position";
    static final String COL_POINTS       = "points";
    static final String COL_WINS         = "wins";
    static final String COL_CONSTRUCTOR  = "constructor_name";
    static final String COL_UPDATED_AT   = "updated_at";

    private static F1Database instance;

    public static F1Database get(Context context) {
        if (instance == null) instance = new F1Database(context.getApplicationContext());
        return instance;
    }

    private F1Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
            COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME     + " TEXT NOT NULL, " +
            COL_EMAIL    + " TEXT NOT NULL UNIQUE, " +
            COL_PASSWORD + " TEXT NOT NULL, " +
            COL_LEVEL    + " TEXT DEFAULT 'rookie'" +
        ")");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
            COL_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_ID   + " INTEGER NOT NULL, " +
            COL_TYPE      + " TEXT NOT NULL, " +
            COL_ITEM_ID   + " TEXT NOT NULL, " +
            COL_ITEM_NAME + " TEXT NOT NULL, " +
            "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
            "UNIQUE(" + COL_USER_ID + ", " + COL_TYPE + ", " + COL_ITEM_ID + ")" +
        ")");

        db.execSQL("CREATE TABLE " + TABLE_DRIVERS + " (" +
            COL_DRIVER_ID     + " TEXT PRIMARY KEY, " +
            COL_GIVEN_NAME    + " TEXT, " +
            COL_FAMILY_NAME   + " TEXT, " +
            COL_NATIONALITY   + " TEXT, " +
            COL_DATE_OF_BIRTH + " TEXT" +
        ")");

        db.execSQL("CREATE TABLE " + TABLE_STANDINGS + " (" +
            COL_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_SEASON      + " INTEGER NOT NULL, " +
            COL_POSITION    + " INTEGER NOT NULL, " +
            COL_DRIVER_ID   + " TEXT NOT NULL, " +
            COL_POINTS      + " TEXT NOT NULL, " +
            COL_WINS        + " TEXT, " +
            COL_CONSTRUCTOR + " TEXT, " +
            COL_UPDATED_AT  + " INTEGER NOT NULL, " +
            "UNIQUE(" + COL_SEASON + ", " + COL_POSITION + "), " +
            "FOREIGN KEY(" + COL_DRIVER_ID + ") REFERENCES " + TABLE_DRIVERS + "(" + COL_DRIVER_ID + ")" +
        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
