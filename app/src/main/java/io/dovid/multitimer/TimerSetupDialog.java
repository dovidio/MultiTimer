package io.dovid.multitimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.dovid.multitimer.model.CustomTimer;
import io.dovid.multitimer.utilities.InputFilterMinMax;

import static android.text.style.TtsSpan.ARG_MINUTES;

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerSetupDialog extends DialogFragment {

    public static final String ARG_TITLE = "TimerSetupDialog.Title";
    private static final String ARG_COUNTDOWN_VALUES = "TimeSetupDialog.CountDownValues";
    private static final String TAG = "TIMERSETUPDIALOG";

    private TextView mCountdown;
    private TimerSetupDialogListener mListener;
    private int mPosition;

    public TimerSetupDialog() {}

    public static TimerSetupDialog getInstance(String title, int[] countdownValues, TextView countdown, int position) {
        TimerSetupDialog dialog = new TimerSetupDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putIntArray(ARG_COUNTDOWN_VALUES, countdownValues);
        dialog.setArguments(args);
        dialog.mCountdown = countdown;
        dialog.mPosition = position;
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TimerSetupDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TimerSetupDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        int[] countdownValues = args.getIntArray(ARG_COUNTDOWN_VALUES);

        int hours = countdownValues[0];
        int minutes = countdownValues[1];
        int seconds = countdownValues[2];


        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.timer_setup_dialog, null, false);

        final EditText hoursET = v.findViewById(R.id.editTextHours);
        final EditText minutesET = v.findViewById(R.id.editTextMinutes);
        final EditText secondsET = v.findViewById(R.id.editTextSeconds);

        hoursET.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 23)
        });
        hoursET.setText(String.format("%02d", hours));

        minutesET.setFilters(new InputFilter[]{
                new InputFilterMinMax(0, 59)
        });
        minutesET.setText(String.format("%02d", minutes));

        secondsET.setFilters(new InputFilter[]{
                new InputFilterMinMax(0, 59)
        });
        secondsET.setText(String.format("%02d", seconds));

        builder.setView(v)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hours = Integer.parseInt(hoursET.getText().toString());
                        int minutes = Integer.parseInt(minutesET.getText().toString());
                        int seconds = Integer.parseInt(secondsET.getText().toString());


                        String newCountdown = String.format("%02d", hours) + ":" +
                                String.format("%02d", minutes) + ":" +
                                String.format("%02d", seconds);

                        mCountdown.setText(newCountdown);
                        long milliseconds = (hours * 60 * 60 + minutes * 60 + seconds) * 1000;

                        Log.d(TAG, "saved milliseconds: " + milliseconds);
                        mListener.onOkClicked(milliseconds, mPosition, TimerSetupDialog.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public interface  TimerSetupDialogListener {
        public void onOkClicked(long time, int position, DialogFragment dialog);
    }
}