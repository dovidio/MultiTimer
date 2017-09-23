package io.dovid.multitimer.ui.preferences;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import io.dovid.multitimer.R;

/**
 * Author: Umberto D'Ovidio
 * Date: 23/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class PreferenceActivity extends AppCompatActivity implements PreferenceAdapter.preferenceItemOnClickListener {

    private static final int THEMES = 0;
    private static final int RINGTONES = 1;
    private static final int VIBRATION = 2;
    private static final String TAG = "PREFERENCEACTIVITY";



    private RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int[] imageResources = {R.drawable.ic_settings_theme, R.drawable.ic_settings_ringtone, R.drawable.ic_settings_vibration};
        final String[] names = {getString(R.string.color_scheme), getString(R.string.ringtone), getString(R.string.vibration)};

        setContentView(R.layout.preference_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        PreferenceAdapter adapter = new PreferenceAdapter(this, imageResources, names);

        rv = (RecyclerView) findViewById(R.id.preference_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
    }

    @Override
    public void preferenceItemClicked(int position) {
        switch (position) {
            case THEMES:
                ColorSchemeChooserDialog.getInstance().show(getFragmentManager(), "ColorSchemeChooserDialog");
                break;
            case RINGTONES:
                setRingtone();
                break;
            case VIBRATION:
                boolean tmp = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.preference_vibrate), false);
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean(getString(R.string.preference_vibrate), !tmp)
                        .apply();
                break;
            default:
                break;
        }
    }

    private void setRingtone() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ringtone = preferences.getString(getString(R.string.preference_ringtone), null);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.preference_ringtone_select_a_ringtone));
        if (ringtone != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtone));
        }

        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        this.startActivityForResult(intent, 5);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {

                Log.d(TAG, "onActivityResult: " + uri.toString());
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(getString(R.string.preference_ringtone), uri.toString())
                        .apply();
            }

        }
    }
}
