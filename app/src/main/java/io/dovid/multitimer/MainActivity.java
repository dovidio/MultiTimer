package io.dovid.multitimer;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;

public class MainActivity extends AppCompatActivity implements CreateTimerDialog.TimerCreateDialogListener, TimerUpdateDialog.TimerUpdateDialogListener {

    private RecyclerView mRecyclerView;
    private TimersAdapter mAdapter;
    private DatabaseHelper databaseHelper;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAdapter != null) {
                mAdapter.refreshTimers();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = databaseHelper.getInstance(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new TimersAdapter(this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateTimerDialog createDialog = CreateTimerDialog.getInstance();
                createDialog.show(getFragmentManager(), "createTimer");
            }
        });
    }


    @Override
    public void onCreateTimer(String name, long time, DialogFragment dialog) {
        TimerDAO.create(databaseHelper, name, time, false, true);
        mAdapter.refreshTimers();
        dialog.dismiss();
    }

    @Override
    public void onUpdate(String name, long defaultTime, int timerId, DialogFragment dialog) {
        dialog.dismiss();
        TimerDAO.updateTimer(databaseHelper, timerId, name, defaultTime, defaultTime);
        mAdapter.refreshTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(BuildConfig.UPDATE_TIMERS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
    }
}
