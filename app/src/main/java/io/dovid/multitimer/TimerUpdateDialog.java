package io.dovid.multitimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.database.TimerContract;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.utilities.Converter;
import io.dovid.multitimer.utilities.InputFilterMinMax;

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerUpdateDialog extends DialogFragment {

    private static final int HOURS = 0;
    private static final int MINUTES = 1;
    private static final int SECONDS = 2;
    private static final String TAG = "TIMERSETUPDIALOG";
    private DatabaseHelper databaseHelper;

    private TimerUpdateDialogListener mListener;
    private int timerId;

    public TimerUpdateDialog() {}

    public static TimerUpdateDialog getInstance(final DatabaseHelper databaseHelper, int timerId) {
        TimerUpdateDialog dialog = new TimerUpdateDialog();
        dialog.databaseHelper = databaseHelper;
        dialog.timerId = timerId;
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TimerUpdateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TimerUpdateDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        String timerName = (String) TimerDAO.getProperty(databaseHelper, TimerContract.Timer.NAME, timerId);
        Long defaultTime = (Long) TimerDAO.getProperty(databaseHelper, TimerContract.Timer.DEFAULT_TIME, timerId);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.timer_update_dialog, null, false);

        final EditText hoursET = v.findViewById(R.id.editTextHours);
        final EditText minutesET = v.findViewById(R.id.editTextMinutes);
        final EditText secondsET = v.findViewById(R.id.editTextSeconds);
        final EditText timerNameET = v.findViewById(R.id.editTextTimerName);

        timerNameET.setText(timerName);
        String[] hoursMinutesSeconds = DurationFormatUtils.formatDuration(defaultTime, BuildConfig.ITALIANTIME).split(":");

        hoursET.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 23)
        });
        hoursET.setText(hoursMinutesSeconds[HOURS]);

        minutesET.setFilters(new InputFilter[]{
                new InputFilterMinMax(0, 59)
        });
        minutesET.setText(hoursMinutesSeconds[MINUTES]);

        secondsET.setFilters(new InputFilter[]{
                new InputFilterMinMax(0, 59)
        });
        secondsET.setText(hoursMinutesSeconds[SECONDS]);

        builder.setView(v)
                .setTitle(R.string.update_your_timer)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = timerNameET.getText().toString();

                        if (!StringUtils.isNumeric(hoursET.getText().toString())) {
                            hoursET.setText("0");
                        }
                        if (!StringUtils.isNumeric(minutesET.getText().toString())) {
                            minutesET.setText("0");
                        }
                        if (!StringUtils.isNumeric(secondsET.getText().toString())) {
                            secondsET.setText("0");
                        }

                        long hours = Long.parseLong(hoursET.getText().toString());
                        long minutes = Long.parseLong(minutesET.getText().toString());
                        long seconds = Long.parseLong(secondsET.getText().toString());
                        long defaultTime = Converter.hmsToMilliseconds(hours, minutes, seconds);

                        Log.d(TAG, "saved default time milliseconds: " + defaultTime);
                        mListener.onUpdate(name, defaultTime, timerId, TimerUpdateDialog.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public interface TimerUpdateDialogListener {
        void onUpdate(String name, long defaultTime, int timerId, DialogFragment dialog);
    }
}