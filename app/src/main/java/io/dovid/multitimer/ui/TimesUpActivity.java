package io.dovid.multitimer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;

public class TimesUpActivity extends AppCompatActivity {

    private static final String TAG = "TIMESUPACTIVITY";
    private Ringtone r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_times_up);
        Intent intent = getIntent();
        String timerName = intent.getExtras().getString(BuildConfig.EXTRA_TIMER_NAME);

        TextView timesUpTV = (TextView) findViewById(R.id.textViewTimesUp);
        timesUpTV.setText(timerName + " " + getString(R.string.time_up));

        Button backToTimers = (Button) findViewById(R.id.buttonBackToTimer);

        backToTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimesUpActivity.this, MainActivity.class);
                startActivity(i);
                if (r != null && r.isPlaying()) {
                    r.stop();
                }
            }
        });

        playAlarmRingtone();
    }

    private void playAlarmRingtone() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ringtone = preferences.getString("notifications_new_message_ringtone", null);

        if (ringtone != null) {
            Uri uri = Uri.parse(ringtone);
            r = RingtoneManager.getRingtone(getApplicationContext(), uri);
            r.play();
        }
    }
}
