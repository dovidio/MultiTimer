package io.dovid.multitimer.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.ui.TimesUpActivity

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "ALARMRECEIVER"

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive called");

        val timerName = intent.extras.getString(BuildConfig.EXTRA_TIMER_NAME)
        val timerId = intent.extras.getInt(BuildConfig.EXTRA_TIMER_ID)

        val newIntent = Intent(context, TimesUpActivity::class.java)
        newIntent.putExtra(BuildConfig.EXTRA_TIMER_NAME, timerName)
        newIntent.putExtra(BuildConfig.EXTRA_TIMER_ID, timerId)
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(newIntent)
    }
}
