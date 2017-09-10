package io.dovid.multitimer.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.util.Log
import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO
import io.dovid.multitimer.model.TimerEntity
import java.util.*

/**
 * Author: Umberto D'Ovidio
 * Date: 02/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

object TimerAlarmManager {

    private val TAG = "TIMERALARMMANAGER"

    fun setupAlarms(context: Context, timers: ArrayList<TimerEntity>) {
        setAlarm(context, timers)
    }

    fun setupAlarms(context: Context, databaseHelper: DatabaseHelper) {
        val timers = TimerDAO.getTimers(databaseHelper)
        setAlarm(context, timers)
    }

    fun setupAlarms(context: Context) {
        var databaseHelper: DatabaseHelper? = null

        try {
            databaseHelper = DatabaseHelper.getInstance(context)
            val timers = TimerDAO.getTimers(databaseHelper)
            setAlarm(context, timers)
        } catch (e: SQLiteException) {
            throw RuntimeException(e)
        } finally {
            databaseHelper?.close()
        }
    }

    private fun setAlarm(context: Context, timers: ArrayList<TimerEntity>) {

        var minExpiredTime = java.lang.Long.MAX_VALUE
        var timerName: String? = null
        var timerId: Int? = null
        for (timer in timers) {
            if (timer.isRunning && timer.expiredTime < minExpiredTime && timer.shouldNotify()) {
                minExpiredTime = timer.expiredTime
                timerName = timer.name
                timerId = timer.id
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (timerName != null && timerId != null) {
            val intentAlarm = Intent(context, AlarmReceiver::class.java)
            intentAlarm.action = BuildConfig.TIME_IS_UP
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_NAME, timerName)
            intentAlarm.putExtra(BuildConfig.EXTRA_TIMER_ID, timerId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "settingAlarm disrupting doze mode")
                val pi = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(Date().time + minExpiredTime, pi), pi)
            } else {
                Log.d(TAG, "settingAlarm simply")
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Date().time + minExpiredTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT))
            }
        } else {
            Log.d(TAG, "cancelling alarm")
            val intentAlarm = Intent(context, AlarmReceiver::class.java)
            intentAlarm.action = BuildConfig.TIME_IS_UP
            alarmManager.cancel(PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT))
        }
    }
}
