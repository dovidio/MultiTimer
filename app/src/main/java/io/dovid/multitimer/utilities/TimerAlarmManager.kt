package io.dovid.multitimer.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

import java.util.ArrayList
import java.util.Date

import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO
import io.dovid.multitimer.model.TimerEntity

/**
 * Author: Umberto D'Ovidio
 * Date: 02/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

object TimerAlarmManager {

    fun setupAlarms(context: Context, timers: ArrayList<TimerEntity>) {
        var minExpiredTime = java.lang.Long.MAX_VALUE
        var nameOfTimer: String? = null
        for (timer in timers) {
            if (timer.isRunning && timer.expiredTime < minExpiredTime && timer.shouldNotify()) {
                minExpiredTime = timer.expiredTime
                nameOfTimer = timer.name
            }
        }

        if (nameOfTimer != null) {

            val intentAlarm = Intent(context, AlarmReceiver::class.java)
            intentAlarm.action = BuildConfig.TIME_IS_UP
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_NAME, nameOfTimer)


            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Date().time + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT))
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, Date().time + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT))
            }
        }
    }

    fun setupAlarms(context: Context, databaseHelper: DatabaseHelper) {
        val timers = TimerDAO.getTimers(databaseHelper)

        var minExpiredTime = java.lang.Long.MAX_VALUE
        var nameOfTimer: String? = null
        for (timer in timers) {
            if (timer.isRunning && timer.expiredTime < minExpiredTime && timer.shouldNotify()) {
                minExpiredTime = timer.expiredTime
                nameOfTimer = timer.name
            }
        }

        if (nameOfTimer != null) {

            val intentAlarm = Intent(context, AlarmReceiver::class.java)
            intentAlarm.action = BuildConfig.TIME_IS_UP
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_NAME, nameOfTimer)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Date().time + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT))
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, Date().time + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT))
            }
        }
    }
}
