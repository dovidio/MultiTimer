package io.dovid.multitimer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang.time.DurationFormatUtils;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.database.TimerContract;
import io.dovid.multitimer.model.TimerDAO;
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
        final EditText nameET = v.findViewById(R.id.editTextTimerName);

        nameET.setText(timerName);
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
                .setPositiveButton(R.string.update, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = nameET.getText().toString().trim();
                        if (name.equalsIgnoreCase("")) {
                            nameET.setError(getActivity().getString(R.string.name_cannot_be_empty));
                        } else if (hoursET.getText().toString().trim().equalsIgnoreCase("")) {
                            hoursET.setError(getActivity().getString(R.string.hours_cannot_be_empty));
                        } else if (minutesET.getText().toString().trim().equalsIgnoreCase("")) {
                            minutesET.setError(getActivity().getString(R.string.minutes_cannot_be_empty));
                        } else if (secondsET.getText().toString().trim().equalsIgnoreCase("")) {
                            secondsET.setError(getActivity().getString(R.string.name_cannot_be_empty));
                        } else {
                            int hours = Integer.parseInt(hoursET.getText().toString().trim());
                            int minutes = Integer.parseInt(minutesET.getText().toString().trim());
                            int seconds = Integer.parseInt(secondsET.getText().toString().trim());

                            long milliseconds = (hours * 60 * 60 + minutes * 60 + seconds) * 1000;

                            mListener.onUpdate(name, milliseconds, timerId, TimerUpdateDialog.this);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        return dialog;
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