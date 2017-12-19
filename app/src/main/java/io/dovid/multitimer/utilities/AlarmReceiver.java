package io.dovid.multitimer.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.ui.TimesUpActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "ALARMRECEIVER";

    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive called");

        String timerName = intent.getExtras().getString(BuildConfig.EXTRA_TIMER_NAME);
        int timerId = intent.getExtras().getInt(BuildConfig.EXTRA_TIMER_ID);

        Intent newIntent = new Intent(context, TimesUpActivity.class);
        newIntent.putExtra(BuildConfig.EXTRA_TIMER_NAME, timerName);
        newIntent.putExtra(BuildConfig.EXTRA_TIMER_ID, timerId);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }
}
