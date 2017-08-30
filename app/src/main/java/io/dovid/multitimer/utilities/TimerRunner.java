package io.dovid.multitimer.utilities;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.MainActivity;
import io.dovid.multitimer.TimesUpActivity;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.model.TimerEntity;

/**
 * Author: Umberto D'Ovidio
 * Date: 26/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerRunner extends IntentService {

    private static boolean isRunning = false;
    private static final String TAG  = "TIMERRUNNER";

    public TimerRunner(String name) {
        super(name);
    }

    private void run() {
        final DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        if (!isRunning) {
            isRunning = true;
            final Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    TimerDAO.printTimerTableStatistic(databaseHelper);
                    ArrayList<TimerEntity> timers = TimerDAO.getTimers(databaseHelper);
                    boolean shouldUpdateTimers = false;
                    for (TimerEntity timer : timers) {
                        if (timer.isRunning()) {
                            Log.d(TAG, "run timer expired time: " + timer.getExpiredTime());
                            if (timer.getExpiredTime() <= 0) {
                                // set isRunning to no
                                TimerDAO.updateTimerRunning(databaseHelper, timer.getId(), false);
                                TimerDAO.updateTimerExpiredTime(databaseHelper, timer.getId(), timer.getDefaultTime());
                                if (timer.shouldNotify()) {
                                    // alert user about timer expired
                                    Intent intent = new Intent(TimerRunner.this, TimesUpActivity.class);
                                    intent.putExtra(BuildConfig.EXTRA_TIMER_NAME, timer.getName());
                                    PendingIntent pi = PendingIntent.getActivity(TimerRunner.this, 0, intent, 0);

                                }
                            } else {
                                long newExpiredTime = timer.getExpiredTime() - 1000;
                                TimerDAO.updateTimerExpiredTime(databaseHelper, timer.getId(), newExpiredTime);
                            }
                            shouldUpdateTimers = true;
                        }
                    }
                    if (shouldUpdateTimers) {
                        Intent i = new Intent(BuildConfig.UPDATE_TIMERS);
                        sendBroadcast(i);
                    }
                }
            };

            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        run();
    }
}
