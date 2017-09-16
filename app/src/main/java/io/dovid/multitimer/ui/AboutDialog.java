package io.dovid.multitimer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.dovid.multitimer.R;

/**
 * Author: Umberto D'Ovidio
 * Date: 09/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class AboutDialog extends DialogFragment {

    public static AboutDialog getInstance() {
        return new AboutDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about_dialog, null);

        TextView tv = view.findViewById(R.id.textView);

        Spannable wordtoSpan = new SpannableString(getActivity().getString(R.string.about_app));

        int start = getString(R.string.about_app).indexOf("http://dovid.io");
        int end = start + "http:/dovid.io".length() + 1;

        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorPrimary)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        tv.setText(wordtoSpan);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://dovid.io")));
            }
        });

        return builder.setView(view).create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
