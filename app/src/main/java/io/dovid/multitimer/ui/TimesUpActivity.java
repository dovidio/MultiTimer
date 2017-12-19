package io.dovid.multitimer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.utilities.RingtonePlayer;
import io.dovid.multitimer.utilities.TimerAlarmManager;
import io.dovid.multitimer.utilities.VibrationPlayer;


public class TimesUpActivity extends AppCompatActivity {
    private static int MATERIAL_THEME = 0;
    private static int DARK_THEME = 1;
    private static int BAKERY_THEME = 2;

    private static final String TAG = "TIMESUPACTIVITY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // wake lock
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_times_up);

        // get timer info
        Intent intent = getIntent();
        String timerName = intent.getExtras().getString(BuildConfig.EXTRA_TIMER_NAME);
        int timerId = intent.getExtras().getInt(BuildConfig.EXTRA_TIMER_ID);

        // stop this timer and setup other alarms
        updateExpiredTimer(timerId);
        TimerAlarmManager.setupAlarms(this);

        TextView timesUpTV = (TextView) findViewById(R.id.textViewTimesUp);
        timesUpTV.setText(timerName + " " + getString(R.string.time_up));

        Button backToTimers = (Button) findViewById(R.id.times_up_back_to_timers_button);

        backToTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimesUpActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);  //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        TextView timeTV = (TextView) findViewById(R.id.times_up_zero_time_tv);

        timeTV.setAnimation(anim);


        playAlarmRingtone();
        setupColors();
    }

    private void playAlarmRingtone() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ringtone = preferences.getString(getString(R.string.preference_ringtone), null);
        boolean vibrate = preferences.getBoolean(getString(R.string.preference_vibrate), false);

        if (vibrate) {
            VibrationPlayer.vibrate(this);
        }

        if (ringtone != null) {
            RingtonePlayer.playRingtone(this, ringtone);
        } else {
            RingtonePlayer.playDefaultAlarm(this);
        }
    }

    private void setupColors() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int colorTheme = sharedPreferences.getInt(getString(R.string.preference_color_scheme), 0);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.times_up_rl);

        if (colorTheme == MATERIAL_THEME) {
            rl.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else if (colorTheme == DARK_THEME) {
            rl.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_card));
        } else {
            rl.setBackgroundColor(ContextCompat.getColor(this, R.color.bakery_card2));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RingtonePlayer.stopPlaying();
        VibrationPlayer.stopVibrating();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
        finish();
    }

    private void updateExpiredTimer(int timerId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        long defaultTime = (Long) TimerDAO.getProperty(databaseHelper, "DEFAULT_TIME", timerId);
        TimerDAO.updateTimerRunning(databaseHelper, timerId, false);
        TimerDAO.updateTimerExpiredTime(databaseHelper, timerId, defaultTime);
    }
}
