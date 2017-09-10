package io.dovid.multitimer.utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.dovid.multitimer.BuildConfig;
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

public class TimerRunner {

    private boolean isRunning = false;
    private static final String TAG = "TIMERRUNNER";
    private static TimerRunner sTimerRunner;


    public synchronized static TimerRunner getInstance() {
        if (sTimerRunner == null) {
            sTimerRunner = new TimerRunner();
        }
        return sTimerRunner;
    }

    public void run(final Context context) {
        final DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        if (!isRunning) {
            isRunning = true;
            final Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // TimerDAO.printTimerTableStatistic(databaseHelper);
                    ArrayList<TimerEntity> timers = TimerDAO.getTimers(databaseHelper);
                    for (TimerEntity timer : timers) {
                        if (timer.isRunning()) {
                            Log.d(TAG, "run timer expired time: " + timer.getExpiredTime());
                            TimerDAO.updateTimerExpiredTime(databaseHelper, timer.getId());
                        }
                    }
                    Intent i = new Intent(BuildConfig.UPDATE_TIMERS);
                    context.sendBroadcast(i);
                }
            };

            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }

}
