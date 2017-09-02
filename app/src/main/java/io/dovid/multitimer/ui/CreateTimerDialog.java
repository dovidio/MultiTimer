package io.dovid.multitimer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import io.dovid.multitimer.R;

/**
 * Author: Umberto D'Ovidio
 * Date: 25/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class CreateTimerDialog extends DialogFragment {

    private static final String TAG = "TIMERCREATEDIALOG";

    private CreateTimerDialog.TimerCreateDialogListener mListener;

    public CreateTimerDialog() {}

    public static CreateTimerDialog getInstance() {
        CreateTimerDialog dialog = new CreateTimerDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateTimerDialog.TimerCreateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TimerCreateDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.timer_create_dialog, null, false);


        final EditText nameET = v.findViewById(R.id.editTextTimerName);
        final EditText hoursET = v.findViewById(R.id.editTextHours);
        final EditText minutesET = v.findViewById(R.id.editTextMinutes);
        final EditText secondsET = v.findViewById(R.id.editTextSeconds);



        builder.setView(v)
                .setTitle(R.string.create_timer)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameET.getText().toString();
                        int hours = Integer.parseInt(hoursET.getText().toString());
                        int minutes = Integer.parseInt(minutesET.getText().toString());
                        int seconds = Integer.parseInt(secondsET.getText().toString());
                        long milliseconds = (hours * 60 * 60 + minutes * 60 + seconds) * 1000;

                        mListener.onCreateTimer(name, milliseconds, CreateTimerDialog.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public interface  TimerCreateDialogListener {
        void onCreateTimer(String name, long time, DialogFragment dialog);
    }
}
