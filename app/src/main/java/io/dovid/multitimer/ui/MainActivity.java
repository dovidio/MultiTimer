package io.dovid.multitimer.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.ui.preferences.PreferenceActivity;
import io.dovid.multitimer.utilities.AppRater;
import io.dovid.multitimer.utilities.RingtonePlayer;
import io.dovid.multitimer.utilities.VibrationPlayer;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity implements CreateTimerDialog.TimerCreateDialogListener, TimerUpdateDialog.TimerUpdateDialogListener {

    private RecyclerView mRecyclerView;
    private TimersAdapter mAdapter;
    private FloatingActionButton fab;
    private TourGuide mTourGuideHandler;
    private int[] colors;
    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;

    private static final String TAG = "MAINACTIVITY";
    private static final int MATERIAL_THEME = 0;
    private static final int DARK_THEME = 1;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.refreshTimers();
            Log.d(TAG, "refreshing timers");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // setup recycler view holding timers
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new TimersAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
        }


        setupColors();
        AppRater.app_launched(this);
        initSwap();

        if (!sharedPreferences.contains(TutorialStep.POINT_TO_FAB.toString())) {
            loadTutorial(TutorialStep.POINT_TO_FAB, fab);
        }

        fab = findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTourGuideHandler != null) {
                        mTourGuideHandler.cleanUp();
                    }

                    if (mAdapter != null && mAdapter.getItemCount() < BuildConfig.MAX_TIMERS) {
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
                sharedPreferences.edit()
                        .remove(TutorialStep.POINT_TO_FAB.toString())
                        .remove(TutorialStep.POINT_TO_TIMER.toString())
                        .commit();
                loadTutorial(TutorialStep.POINT_TO_FAB, fab);
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

    private void loadTutorial(TutorialStep step, View target) {
        if (target != null) {
            if (step.equals(TutorialStep.POINT_TO_FAB)) {
                mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                        .setPointer(new Pointer().setGravity(Gravity.TOP))
                        .setToolTip(new ToolTip().setTitle(getString(R.string.welcome)).
                                setDescription(getString(R.string.click_on_this_button)).setGravity(Gravity.TOP))
                        .setOverlay(new Overlay().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mTourGuideHandler != null) {
                                    mTourGuideHandler.cleanUp();
                                    sharedPreferences.edit()
                                            .putBoolean(TutorialStep.POINT_TO_FAB.toString(), true)
                                            .apply();
                                }
                            }
                        }))
                        .playOn(target);
            } else if (step.equals(TutorialStep.POINT_TO_TIMER)) {
                mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.VerticalDownward)
                        .setToolTip(new ToolTip()
                                .setDescription(getString(R.string.long_press_timer))
                                .setGravity(Gravity.CENTER)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (mTourGuideHandler != null) {
                                            mTourGuideHandler.cleanUp();
                                        }
                                        sharedPreferences.edit()
                                                .putBoolean(TutorialStep.POINT_TO_TIMER.toString(), true)
                                                .apply();
                                    }
                                })).setOverlay(new Overlay().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mTourGuideHandler != null) {
                                    mTourGuideHandler.cleanUp();
                                }
                            }
                        }).setStyle(Overlay.Style.Rectangle)).playOn(target);
            }
        }
    }

    private void initSwap() {
        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                if (i == ItemTouchHelper.LEFT) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemRemoved(position);
                    }
                } else {
                    if (mAdapter != null) {
                        mAdapter.showUpdateTimerDialog(position);
                    }
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    double height = itemView.getBottom() - itemView.getTop();
                    double widht = height / 3.0;

                    if (dX > 0) {
                        Paint paint = new Paint();
                        paint.setColor(Color.parseColor("388E3C"));
                        RectF background = new RectF(itemView.getLeft(), itemView.getTop() + 20, dX - 100, itemView.getBottom() - 20);
                        c.drawRect(background, paint);
                        //                        val icong = BitmapFactory.decodeResource(resources, R.drawable.ic_action_name)
//                        if (icong == null) {
//                            Log.d(TAG, "icon is null")
//                        }
//                        val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
//                        c?.drawBitmap(icong, null, iconDest, paint)
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }



    // TimerCreateDialogListener implementation
    public void onCreateTimer(String name, long time, DialogFragment dialog) {

        TimerDAO.create(DatabaseHelper.getInstance(this), name, time, false, true);
        if (mAdapter != null) {
            mAdapter.insertTimer();
        }

        dialog.dismiss();

        if (!sharedPreferences.contains(TutorialStep.POINT_TO_TIMER.toString())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRecyclerView != null) {
                        loadTutorial(TutorialStep.POINT_TO_TIMER, mRecyclerView.getChildAt(0));
                    }
                }
            }, 1000);
        }

        // in case user does not find easy to clear tutorial
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTourGuideHandler != null) {
                    mTourGuideHandler.cleanUp();
                }
            }
        }, 1000);
    }

    // TimerUpdateDialogListener implementation
    @Override
    public void onUpdate(String name, long defaultTime, int timerId, DialogFragment dialog) {
        dialog.dismiss();
        TimerDAO.updateTimer(DatabaseHelper.getInstance(this), timerId, name, defaultTime, defaultTime);
        if (mAdapter != null) {
            mAdapter.refreshTimers();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupColors();
        if (mAdapter != null) {
            mAdapter.setColors(colors);
        }
        registerReceiver(receiver, new IntentFilter(BuildConfig.UPDATE_TIMERS));
        RingtonePlayer.stopPlaying();
        VibrationPlayer.stopVibrating();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "MainActivity IllegalArgumentException", e);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private enum TutorialStep {
        POINT_TO_FAB, POINT_TO_TIMER;
    }
}
