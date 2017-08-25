package io.dovid.multitimer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author: Umberto D'Ovidio
 * Date: 25/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Multitimer.db";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_TIMER_TABLE = "CREATE TABLE " + TimerContract.Timer.TABLE_NAME +
            " (" + TimerContract.Timer._ID + " INTEGER PRIMARY KEY," +
            TimerContract.Timer.NAME + " TEXT NOT NULL," +
            TimerContract.Timer.DEFAULT_TIME + " NUMERIC NOT NULL," +
            TimerContract.Timer.EXPIRED_TIME + " NUMERIC NOT NULL," +
            TimerContract.Timer.IS_RUNNING + " INTEGER DEFAULT 0," +
            "PRIMARY KEY (" + TimerContract.Timer.NAME + "," + TimerContract.Timer.DEFAULT_TIME + "))";


    private static final String SQL_CREATE_TIMER_COLLECTION_TABLE = "CREATE TABLE " + TimerContract.TimerCollection.TABLE_NAME +
            " (" + TimerContract.TimerCollection._ID + " INTEGER PRIMARY KEY," +
            TimerContract.TimerCollection.NAME + " TEXT UNIQUE NOT NULL," +
            TimerContract.TimerCollection.QUANTITY + " INTEGER DEFAULT 1)";

    private static final String SQL_CREATE_TIMER_TIMER_COLLECTION_TABLE = "CREATE TABLE " + TimerContract.TimerTimerCollection.TABLE_NAME +
            " (" + TimerContract.TimerTimerCollection._ID + " INTEGER PRIMARY KEY," +
            TimerContract.TimerTimerCollection.TIMER_ID + " INTEGER NOT NULL," +
            TimerContract.TimerTimerCollection.TIMER_COLLECTION_ID + " INTEGER NOT NULL," +
            "FOREIGN KEY (" + TimerContract.TimerTimerCollection.TIMER_ID + ")" +
             " REFERENCES " + TimerContract.Timer.TABLE_NAME + " (" + TimerContract.Timer._ID + ")," +
            " REFERENCES " + TimerContract.TimerCollection.TABLE_NAME + " (" + TimerContract.TimerCollection._ID + ")))";

    private static final String SQL_DROP_TIMER_TABLE = "DROP IF EXISTS " + TimerContract.Timer.TABLE_NAME;
    private static final String SQL_DROP_TIMER_COLLECTION_TABLE = "DROP IF EXISTS " + TimerContract.TimerCollection.TABLE_NAME;
    private static final String SQL_DROP_TIMER_TIMER_COLLECTION_TABLE = "DROP IF EXISTS " + TimerContract.TimerTimerCollection.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TIMER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMER_COLLECTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMER_TIMER_COLLECTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(SQL_DROP_TIMER_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_TIMER_COLLECTION_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_TIMER_TIMER_COLLECTION_TABLE);


        sqLiteDatabase.execSQL(SQL_CREATE_TIMER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMER_COLLECTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMER_TIMER_COLLECTION_TABLE);
    }
}
