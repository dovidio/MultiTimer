package io.dovid.multitimer.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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

    public static void setupAlarms(final Context context, final ArrayList<TimerEntity> timers) {
        long minExpiredTime = Long.MAX_VALUE;
        String nameOfTimer = null;
        for (TimerEntity timer : timers) {
            if (timer.isRunning() && timer.getExpiredTime() < minExpiredTime && timer.shouldNotify()) {
                minExpiredTime = timer.getExpiredTime();
                nameOfTimer = timer.getName();
            }
        }

        if (nameOfTimer != null) {

            Intent intentAlarm = new Intent(context, AlarmReceiver.class);
            intentAlarm.setAction(BuildConfig.TIME_IS_UP);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, new Date().getTime() + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    public static void setupAlarms(final Context context, final DatabaseHelper databaseHelper) {
        ArrayList<TimerEntity> timers = TimerDAO.getTimers(databaseHelper);

        long minExpiredTime = Long.MAX_VALUE;
        String nameOfTimer = null;
        for (TimerEntity timer : timers) {
            if (timer.isRunning() && timer.getExpiredTime() < minExpiredTime && timer.shouldNotify()) {
                minExpiredTime = timer.getExpiredTime();
                nameOfTimer = timer.getName();
            }
        }

        if (nameOfTimer != null) {

            Intent intentAlarm = new Intent(context, AlarmReceiver.class);
            intentAlarm.setAction(BuildConfig.TIME_IS_UP);
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_NAME, nameOfTimer);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, new Date().getTime() + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }
}
