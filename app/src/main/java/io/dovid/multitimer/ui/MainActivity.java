package io.dovid.multitimer.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.model.TimerEntity;
import io.dovid.multitimer.ui.preferences.PreferenceActivity;
import io.dovid.multitimer.utilities.AppRater;
import io.dovid.multitimer.utilities.RingtonePlayer;
import io.dovid.multitimer.utilities.VibrationPlayer;

public class MainActivity extends AppCompatActivity implements CreateTimerDialog.TimerCreateDialogListener, TimerUpdateDialog.TimerUpdateDialogListener {

    private RecyclerView recyclerView;
    private TimersAdapter timersAdapter;
    private FloatingActionButton fab;
    private int[] colors;

    private static final String TAG = "MAINACTIVITY";
    private static final int MATERIAL_THEME = 0;
    private static final int DARK_THEME = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup recycler view holding timers
        recyclerView = findViewById(R.id.recyclerView);
        // this avoid timer blinking animation when calling notifyItemChanged(position)
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        timersAdapter = new TimersAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(timersAdapter);
        }

        setupColors();
        AppRater.app_launched(this);
        initSwap();

        fab = findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timersAdapter != null && timersAdapter.getItemCount() < BuildConfig.MAX_TIMERS) {
                        CreateTimerDialog createDialog = CreateTimerDialog.getInstance();
                        createDialog.show(getFragmentManager(), "createTimer");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.main_max_number_of_timers);

                        builder.setMessage(getString(R.string.main_delete_before_adding_timer));
                        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    }
                }
            });
        }

        refreshTimers();
    }


    private void refreshTimers() {
        final Handler handler = new Handler();

        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timersAdapter.refreshTimers();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_paid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, PreferenceActivity.class);
                startActivity(i);
                break;
            case R.id.action_about:
                AboutDialog.getInstance().show(getFragmentManager(), "AboutDialog");
                break;
            case R.id.action_play_tutorial:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setupColors() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int colorTheme = preferences.getInt(getString(R.string.preference_color_scheme), 0);

        if (colorTheme == MATERIAL_THEME) {
            if (fab != null) {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
            }
            colors = ColorsThemePalette.getMaterialColors();
        } else if (colorTheme == DARK_THEME) {
            if (fab != null) {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_dark_material_dark)));
            }
            colors = ColorsThemePalette.getDarkColors();
        } else {
            if (fab != null) {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bakery_card2)));
            }

            colors = ColorsThemePalette.getBakeryColors();
        }
    }

    private void initSwap() {
        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new SwipeCallback(this) {
            @Override
            void swipeLeft(final int position) {
                timersAdapter.notifyDataSetChanged();
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are you sure you want this timer?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timersAdapter.deleteTimer(position);
                                timersAdapter.notifyItemRemoved(position);
                                timersAdapter.refreshTimers();
                                Log.d(TAG, "notifyItemRemoved at position: " + position);
                                dialogInterface.dismiss();
                            }
                        }).show();

            }

            @Override
            void swipeRight(int position) {
                if (timersAdapter != null) {
                    Log.d(TAG, "showUpdateTimerDialog");
                    timersAdapter.showUpdateTimerDialog(position);
                    timersAdapter.refreshTimers();
                    timersAdapter.notifyDataSetChanged();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // TimerCreateDialogListener implementation
    public void onCreateTimer(String name, long time, DialogFragment dialog) {

        TimerDAO.create(DatabaseHelper.getInstance(this), name, time, false, true);
        if (timersAdapter != null) {
            timersAdapter.insertTimer(new TimerEntity(20, name, time, time, false, true));
        }

        dialog.dismiss();
    }

    // TimerUpdateDialogListener implementation
    @Override
    public void onUpdate(String name, long defaultTime, int timerId, DialogFragment dialog) {
        dialog.dismiss();
        TimerDAO.updateTimer(DatabaseHelper.getInstance(this), timerId, name, defaultTime, defaultTime);
        if (timersAdapter != null) {
            timersAdapter.refreshTimers();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupColors();
        if (timersAdapter != null) {
            timersAdapter.setColors(colors);
        }
        RingtonePlayer.stopPlaying();
        VibrationPlayer.stopVibrating();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
