package io.dovid.multitimer.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Author: Umberto D'Ovidio
 * Date: 07/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */
public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
        TimerAlarmManager.setupAlarms(context);
    }

}