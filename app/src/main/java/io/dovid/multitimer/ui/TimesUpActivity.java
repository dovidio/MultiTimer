package io.dovid.multitimer.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;

public class TimesUpActivity extends AppCompatActivity {

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

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        r.play();

    }
}
