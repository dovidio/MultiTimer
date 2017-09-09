package io.dovid.multitimer.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.database.DatabaseHelper;

import static io.dovid.multitimer.database.TimerContract.Timer.DEFAULT_TIME;
import static io.dovid.multitimer.database.TimerContract.Timer.EXPIRED_TIME;
import static io.dovid.multitimer.database.TimerContract.Timer.IS_ANIMATING;
import static io.dovid.multitimer.database.TimerContract.Timer.IS_RUNNING;
import static io.dovid.multitimer.database.TimerContract.Timer.NAME;
import static io.dovid.multitimer.database.TimerContract.Timer.PlAY_STARTED_AT;
import static io.dovid.multitimer.database.TimerContract.Timer.SHOULD_NOTIFY;
import static io.dovid.multitimer.database.TimerContract.Timer.TABLE_NAME;
import static io.dovid.multitimer.database.TimerContract.Timer._ID;


/**
 * Author: Umberto D'Ovidio
 * Date: 26/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerDAO {

    private static final String TAG = "TIMERDAO";

    public synchronized static void create(final DatabaseHelper databaseHelper, final String name,
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
            String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY _ID ASC";

            timersCursor = writeDatabase.rawQuery(query, null);

            while (timersCursor.moveToNext()) {
                TimerEntity timer = new TimerEntity();
                timer.setId(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(_ID)));
                timer.setName(timersCursor.getString(timersCursor.getColumnIndexOrThrow(NAME)));
                timer.setDefaultTime(timersCursor.getLong(timersCursor.getColumnIndexOrThrow(DEFAULT_TIME)));
                timer.setExpiredTime(timersCursor.getLong(timersCursor.getColumnIndexOrThrow(EXPIRED_TIME)));
                timer.setRunning(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(IS_RUNNING)) != 0);
                timer.setShouldNotify(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(SHOULD_NOTIFY)) != 0);
                timer.setAnimating(timersCursor.getInt(timersCursor.getColumnIndexOrThrow(IS_ANIMATING)) != 0);
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

    public synchronized static void updateTimerExpiredTime(final DatabaseHelper databaseHelper, final int timerId, final long newExpiredTime) {
        SQLiteDatabase writeDatabase = null;
        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(EXPIRED_TIME, newExpiredTime);

            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[]{String.valueOf(timerId)});

        } catch (SQLiteException e) {
            Log.e(TAG, "updateTimerExpiredTime: ", e);
            throw new RuntimeException(e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static void updateTimerExpiredTime(final DatabaseHelper databaseHelper, final int timerId) {
        SQLiteDatabase writeDatabase = null;
        Cursor cursor = null;
        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            String query = "SELECT " + PlAY_STARTED_AT;
            query += ", " + DEFAULT_TIME;
            query += " FROM " + TABLE_NAME;
            query += " WHERE _ID= " + timerId;

            cursor = writeDatabase.rawQuery(query, null);

            if (cursor.getCount() != 1) {
                throw new RuntimeException("Database does not contain timer with id = " + timerId);
            }

            cursor.moveToFirst();
            long playStartedAt = cursor.getLong(cursor.getColumnIndexOrThrow(PlAY_STARTED_AT));
            long defaultTime = cursor.getLong(cursor.getColumnIndexOrThrow(DEFAULT_TIME));

            long updatedExpiredTime = defaultTime - ((new java.util.Date()).getTime() - playStartedAt);


            ContentValues cv = new ContentValues();
            cv.put(EXPIRED_TIME, updatedExpiredTime);

            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[]{String.valueOf(timerId)});

        } catch (SQLiteException e) {
            Log.e(TAG, "updateTimerExpiredTime: ", e);
            throw new RuntimeException(e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized static void updateTimerRunning(final DatabaseHelper databaseHelper, final int timerId, final boolean isRunning) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(IS_RUNNING, isRunning);

            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[]{String.valueOf(timerId)});
        } catch (SQLiteException e) {
            Log.e(TAG, "updateTimerRunning: ", e);
            throw new RuntimeException(e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static void updateTimerPlayTimestamp(final DatabaseHelper databaseHelper, final int timerId, final long timestamp) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(PlAY_STARTED_AT, timestamp);

            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[]{String.valueOf(timerId)});
        } catch (SQLiteException e) {
            Log.e(TAG, "updateTimerPlayTimestamp: ", e);
            throw new RuntimeException(e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static void updateTimer(final DatabaseHelper databaseHelper, final int timerId,
                                                final String name, final long defaultTime, final long expireTime) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(NAME, name);
            cv.put(DEFAULT_TIME, defaultTime);
            cv.put(EXPIRED_TIME, expireTime);
            writeDatabase.update(TABLE_NAME, cv, "_ID=?", new String[]{String.valueOf(timerId)});

        } catch (SQLiteException e) {

        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static Object getProperty(final DatabaseHelper databaseHelper, final String property, final int timerId) {
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

    public synchronized static void printTimerTableStatistic(final DatabaseHelper databaseHelper) {

        SQLiteDatabase writeDatabase = null;
        Cursor c = null;
        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME;

            c = writeDatabase.rawQuery(query, null);
            String statistics = "ID\tNAME\tDEFAULT_TIME\tPlAY_STARTED_AT\tEXPIRED_TIME\tIS_RUNNING\nSHOULD_NOTIFY";

            while (c.moveToNext()) {
                statistics += "\n" + c.getInt(c.getColumnIndexOrThrow(_ID)) + "\t" +
                        c.getString(c.getColumnIndexOrThrow(NAME)) + "\t" +
                        c.getLong(c.getColumnIndexOrThrow(DEFAULT_TIME)) + "\t" +
                        c.getLong(c.getColumnIndexOrThrow(PlAY_STARTED_AT)) + "\t" +
                        c.getLong(c.getColumnIndexOrThrow(EXPIRED_TIME)) + "\t" +
                        c.getInt(c.getColumnIndexOrThrow(IS_RUNNING)) + "\t" +
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

    public synchronized static void deleteTimer(final DatabaseHelper databaseHelper, final int timerId) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();
            writeDatabase.delete(TABLE_NAME, _ID + "=?", new String[]{String.valueOf(timerId)});
        } catch (SQLiteException e) {
            Log.e(TAG, "deleteTimer: ", e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static void updateTimerShouldNotify(final DatabaseHelper databaseHelper, final int timerId, final boolean shouldNotify) {

        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SHOULD_NOTIFY, shouldNotify);
            writeDatabase.update(TABLE_NAME, cv, _ID + "=?", new String[]{String.valueOf(timerId)});

        } catch (SQLiteException e) {

        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }

    }

    public synchronized static void putPlayTimeStampNull(final DatabaseHelper databaseHelper, final int timerId) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.putNull(PlAY_STARTED_AT);

            writeDatabase.update(TABLE_NAME, cv, "_ID=" + timerId, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "putPlayTimeStampNull: ", e);
            throw new RuntimeException(e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

    public synchronized static void updateIsAnimating(final DatabaseHelper databaseHelper, final int timerId, boolean isAnimating) {
        SQLiteDatabase writeDatabase = null;

        try {
            writeDatabase = databaseHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(IS_ANIMATING, isAnimating);
            writeDatabase.update(TABLE_NAME, cv, "_ID=" + timerId, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "updateIsAnimating: ", e);
            throw new RuntimeException(e);
        } finally {
            if (writeDatabase != null && writeDatabase.isOpen()) {
                writeDatabase.close();
            }
        }
    }

}
