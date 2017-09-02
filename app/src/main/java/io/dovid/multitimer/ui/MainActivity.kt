package io.dovid.multitimer.ui

import android.app.DialogFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View

import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.R
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO

class MainActivity : AppCompatActivity(), CreateTimerDialog.TimerCreateDialogListener, TimerUpdateDialog.TimerUpdateDialogListener {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TimersAdapter? = null
    private var databaseHelper: DatabaseHelper? = null

    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mAdapter != null) {
                mAdapter!!.refreshTimers()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        databaseHelper = DatabaseHelper.getInstance(this)

        mRecyclerView = findViewById(R.id.recyclerView) as RecyclerView
        mAdapter = TimersAdapter(this)

        val manager = LinearLayoutManager(this)

        mRecyclerView!!.layoutManager = manager
        mRecyclerView!!.adapter = mAdapter

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val createDialog = CreateTimerDialog.getInstance()
            createDialog.show(fragmentManager, "createTimer")
        }
    }


    override fun onCreateTimer(name: String, time: Long, dialog: DialogFragment) {
        TimerDAO.create(databaseHelper, name, time, false, true)
        mAdapter!!.refreshTimers()
        dialog.dismiss()
    }

    override fun onUpdate(name: String, defaultTime: Long, timerId: Int, dialog: DialogFragment) {
        dialog.dismiss()
        TimerDAO.updateTimer(databaseHelper, timerId, name, defaultTime, defaultTime)
        mAdapter!!.refreshTimers()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(BuildConfig.UPDATE_TIMERS))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP//***Change Here***
        startActivity(intent)
        finish()
    }

    companion object {
        private val TAG = "MAINACTIVITY"
    }
}
