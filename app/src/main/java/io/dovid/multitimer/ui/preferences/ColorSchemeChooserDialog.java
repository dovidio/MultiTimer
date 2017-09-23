package io.dovid.multitimer.ui.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.dovid.multitimer.R;

/**
 * Author: Umberto D'Ovidio
 * Date: 23/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class ColorSchemeChooserDialog extends DialogFragment {

    private final int[] imageResources = {R.mipmap.material_design, R.mipmap.dark, R.mipmap.bakery};
    private final String[] titles = {"Material Design", "Dark", "Bakery",};
    private final String[] descriptions = {
            "Colorful scheme inspired by Material Design",
            "Color scheme inspired by Batman",
            "Color scheme inspired by your typical bakery"
    };

    public static ColorSchemeChooserDialog getInstance() {
        return new ColorSchemeChooserDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_color_scheme, null, false);

        RecyclerView rv = v.findViewById(R.id.themes_rv);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        ColorSchemeAdapter adapter = new ColorSchemeAdapter(imageResources, titles, descriptions);
        rv.setLayoutManager(manager);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setView(v);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return b.create();
    }
}
