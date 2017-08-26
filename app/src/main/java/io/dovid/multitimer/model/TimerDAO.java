package io.dovid.multitimer.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.database.DatabaseHelper;
import static io.dovid.multitimer.database.TimerContract.Timer.*;


/**
 * Author: Umberto D'Ovidio
 * Date: 26/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerDAO {

    private static final String TAG = "TIMERDAO";

    public static void create(final DatabaseHelper databaseHelper, final String name,
                              final long time, final boolean isRunning, final boolean shouldNotify) {

        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(NAME, name);
            cv.put(DEFAULT_TIME, time);
            cv.put(EXPIRED_TIME, time);
            cv.put(IS_RUNNING, isRunning ? 1 : 0);
            cv.put(SHOULD_NOTIFY, shouldNotify ? 1 : 0);

            writeDatabase.insertOrThrow(TABLE_NAME, null, cv);

        } catch (SQLiteException e) {
            Log.e(TAG, "create: ", e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static ArrayList<TimerEntity> getTimers(final DatabaseHelper databaseHelper) {
        ArrayList<TimerEntity> timers = new ArrayList<>();
        SQLiteDatabase writeDatabase = null;
        Cursor timersCursor = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME;

            timersCursor = writeDatabase.rawQuery(query, null);

            while (timersCursor.moveToNext()) {
                TimerEntity timer = new TimerEntity();
                timer.setId(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(_ID)));
                timer.setName(timersCursor.getString(timersCursor.getColumnIndexOrThrow(NAME)));
                timer.setDefaultTime(timersCursor.getLong(timersCursor.getColumnIndexOrThrow(DEFAULT_TIME)));
                timer.setExpiredTime(timersCursor.getLong(timersCursor.getColumnIndexOrThrow(EXPIRED_TIME)));
                timer.setRunning(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(IS_RUNNING)) != 0);
                timer.setShouldNotify(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(SHOULD_NOTIFY)) != 0);
                timers.add(timer);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "getTimer: ", e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
            if (timersCursor != null) {
                timersCursor.close();
            }
        }

        return timers;
    }

    public static void updateTimerExpiredTime(final DatabaseHelper databaseHelper, final int timerId, final long expiredTime) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(EXPIRED_TIME, expiredTime);

            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[] {String.valueOf(timerId)});

        } catch (SQLiteException e) {

        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public static void updateTimerRunning(final DatabaseHelper databaseHelper, final int timerId, final boolean isRunning) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(IS_RUNNING, isRunning);

            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[] {String.valueOf(timerId)});

        } catch (SQLiteException e) {

        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }




    public static void updateTimer(final DatabaseHelper databaseHelper, final int timerId,
                                   final String name, final long defaultTime, final long expireTime) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(NAME, name);
            cv.put(DEFAULT_TIME, defaultTime);
            cv.put(EXPIRED_TIME, expireTime);
            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[] {String.valueOf(timerId)});

        } catch (SQLiteException e) {

        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public static Object getProperty(final DatabaseHelper databaseHelper, final String property, final int timerId) {
        SQLiteDatabase writeDatabase = null;
        Cursor c = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + _ID + " = " + timerId;

            c = writeDatabase.rawQuery(query, null);

            if (BuildConfig.DEBUG && c.getCount() > 1) {
                throw new AssertionError("unique timer id constraint failed");
            }

            if (c.moveToFirst()) {
                int cIndex = c.getColumnIndexOrThrow(property);
                switch (property) {
                    case NAME:
                        return c.getString(cIndex);
                    case DEFAULT_TIME:
                    case EXPIRED_TIME:
                        return c.getLong(cIndex);
                    case IS_RUNNING:
                    case SHOULD_NOTIFY:
                        return c.getInt(cIndex);
                    default:
                        throw new RuntimeException("cannot find column named " + property);
                }
            } else {
                Log.d(TAG, "getProperty: " + timerId);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getPropert: ", e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static void printTimerTableStatistic(final DatabaseHelper databaseHelper) {

        SQLiteDatabase writeDatabase = null;
        Cursor c = null;
        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME;

            c = writeDatabase.rawQuery(query, null);
            String statistics = "ID\tNAME\tDEFAULT_TIME\tEXPIRED_TIME\tIS_RUNNING\nSHOULD_NOTIFY";

            while (c.moveToNext()) {
                statistics += "\n" + c.getInt(c.getColumnIndexOrThrow(_ID)) + "\t" +
                        c.getString(c.getColumnIndexOrThrow(NAME)) + "\t" +
                        c.getLong(c.getColumnIndexOrThrow(DEFAULT_TIME)) + "\t" +
                        c.getLong(c.getColumnIndexOrThrow(EXPIRED_TIME)) +  "\t" +
                        c.getInt(c.getColumnIndexOrThrow(IS_RUNNING)) +  "\t" +
                        c.getInt(c.getColumnIndexOrThrow(SHOULD_NOTIFY));
            }

            Log.d(TAG, "printTimerTableStatistic: " + statistics);

        } catch (SQLiteException e) {
            Log.e(TAG, "printTimerTableStatistic: ", e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
            if (c != null) {
                c.close();
            }
        }
    }

    public static void deleteTimer(final DatabaseHelper databaseHelper, final int timerId) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();
            writeDatabase.delete(TABLE_NAME, _ID + "=?", new String[] {String.valueOf(timerId)});
        } catch (SQLiteException e) {
            Log.e(TAG, "deleteTimer: ", e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public static void updateTimerShouldNotify(final DatabaseHelper databaseHelper, final int timerId, final boolean shouldNotify) {

        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SHOULD_NOTIFY, shouldNotify);
            writeDatabase.update(TABLE_NAME, cv, _ID + "=?", new String[] {String.valueOf(timerId)});

        } catch (SQLiteException e) {

        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }

    }
}
