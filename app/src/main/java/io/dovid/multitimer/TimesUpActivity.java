package io.dovid.multitimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.utilities.TimerRunner;

public class TimesUpActivity extends AppCompatActivity {

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
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
