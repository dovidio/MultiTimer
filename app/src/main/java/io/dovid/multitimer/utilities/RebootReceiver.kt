package io.dovid.multitimer.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Author: Umberto D'Ovidio
 * Date: 07/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */
class RebootReceiver() : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        TimerAlarmManager.setupAlarms(p0!!);
    }

}