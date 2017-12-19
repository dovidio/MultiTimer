package io.dovid.multitimer.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.model.TimerEntity;

/**
 * Author: Umberto D'Ovidio
 * Date: 02/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerAlarmManager {

    private static final String TAG = "TIMERALARMMANAGER";

    public static void setupAlarms(Context context, ArrayList<TimerEntity> timers) {
        setAlarm(context, timers);
    }

    public static void setupAlarms(Context context, DatabaseHelper databaseHelper) {
        ArrayList<TimerEntity> timers = TimerDAO.getTimers(databaseHelper);
        setAlarm(context, timers);
    }

    public static void setupAlarms(Context context) {
        DatabaseHelper databaseHelper;

        try {
            databaseHelper = DatabaseHelper.getInstance(context);
            ArrayList<TimerEntity> timers = TimerDAO.getTimers(databaseHelper);
            setAlarm(context, timers);
        } catch (SQLiteException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setAlarm(Context context, ArrayList<TimerEntity> timers) {

        long minExpiredTime = Long.MAX_VALUE;
        String timerName = null;
        Integer timerId = null;
        for (TimerEntity timer : timers) {
            if (timer.isRunning() && timer.getExpiredTime() < minExpiredTime && timer.shouldNotify()) {
                minExpiredTime = timer.getExpiredTime();
                timerName = timer.getName();
                timerId = timer.getId();
            }
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (timerName != null && timerId != null) {
            Intent intentAlarm = new Intent(context, AlarmReceiver.class);
            intentAlarm.setAction(BuildConfig.TIME_IS_UP);
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_NAME, timerName);
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_ID, timerId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "settingAlarm disrupting doze mode");
                PendingIntent pi = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(new Date().getTime() + minExpiredTime, pi), pi);
            } else {
                Log.d(TAG, "settingAlarm simply");
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, new Date().getTime() + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            }
        } else {
            Log.d(TAG, "cancelling alarm");
            Intent intentAlarm = new Intent(context, AlarmReceiver.class);
            intentAlarm.setAction(BuildConfig.TIME_IS_UP);
            alarmManager.cancel(PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }
}
