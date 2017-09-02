package io.dovid.multitimer.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.ui.TimesUpActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String timerName = intent.getExtras().getString(BuildConfig.EXTRA_TIMER_NAME);

        Intent newIntent = new Intent(context, TimesUpActivity.class);
        newIntent.putExtra(BuildConfig.EXTRA_TIMER_NAME, timerName);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }
}
