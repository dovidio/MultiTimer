package io.dovid.multitimer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.dovid.multitimer.R;
import io.dovid.multitimer.utilities.Converter;

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
                .setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = nameET.getText().toString().trim();
                        if (name.equalsIgnoreCase("")) {
                            nameET.setError("The name cannot be empty");
                        } else if (hoursET.getText().toString().trim().equalsIgnoreCase("")) {
                            hoursET.setError("Hours cannot be empty");
                        } else if (minutesET.getText().toString().trim().equalsIgnoreCase("")) {
                            minutesET.setError("Minutes cannot be empty");
                        } else if (secondsET.getText().toString().trim().equalsIgnoreCase("")) {
                            secondsET.setError("Seconds cannot be empty");
                        } else {
                            int hours = Integer.parseInt(hoursET.getText().toString().trim());
                            int minutes = Integer.parseInt(minutesET.getText().toString().trim());
                            int seconds = Integer.parseInt(secondsET.getText().toString().trim());

                            long milliseconds = Converter.hmsToMilliseconds(hours, minutes, seconds);

                            if (milliseconds == 0) {
                                secondsET.setError("Timer must at least be one second long");
                            } else {
                                mListener.onCreateTimer(name, milliseconds, CreateTimerDialog.this);
                                dialog.dismiss();
                            }
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

    public interface  TimerCreateDialogListener {
        void onCreateTimer(String name, long time, DialogFragment dialog);
    }
}
