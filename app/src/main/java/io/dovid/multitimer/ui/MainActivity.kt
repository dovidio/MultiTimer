package io.dovid.multitimer.ui

import android.app.DialogFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.R
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO

class MainActivity : AppCompatActivity(), CreateTimerDialog.TimerCreateDialogListener, TimerUpdateDialog.TimerUpdateDialogListener {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TimersAdapter? = null
    private var databaseHelper: DatabaseHelper? = null
    private var fab: FloatingActionButton? = null
    lateinit var colors: IntArray

    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAdapter?.refreshTimers()
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

        mRecyclerView?.layoutManager = manager
        mRecyclerView?.adapter = mAdapter

        fab = findViewById(R.id.fab) as FloatingActionButton
        fab?.setOnClickListener {
            val createDialog = CreateTimerDialog.getInstance()
            createDialog.show(fragmentManager, "createTimer")
        }
        setupColors()
    }

    override fun onCreateTimer(name: String, time: Long, dialog: DialogFragment) {
        TimerDAO.create(databaseHelper, name, time, false, true)
        mAdapter?.refreshTimers()
        dialog.dismiss()
    }

    override fun onUpdate(name: String, defaultTime: Long, timerId: Int, dialog: DialogFragment) {
        dialog.dismiss()
        TimerDAO.updateTimer(databaseHelper, timerId, name, defaultTime, defaultTime)
        mAdapter?.refreshTimers()
    }

    override fun onResume() {
        super.onResume()
        setupColors()
        mAdapter?.setColors(colors)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
            }
            R.id.action_about -> {
                Log.d(TAG, "onOptionsItemSelected: selected about")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (BuildConfig.PAID) {
            menuInflater.inflate(R.menu.menu_main_paid, menu)
        } else {

        }
        return true
    }

    fun setupColors() {
        if (BuildConfig.PAID) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val colorTheme = preferences.getString("color_theme_list", "0")

            if (colorTheme == MATERIAL_THEME) {
                fab?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
                colors = intArrayOf(R.color.material_card1,
                        R.color.material_card2,
                        R.color.material_card3,
                        R.color.material_card4,
                        R.color.material_card5,
                        R.color.material_card6,
                        R.color.material_card7,
                        R.color.material_card8,
                        R.color.material_card9,
                        R.color.material_card10)
            } else if (colorTheme == DARK_THEME) {
                fab?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_dark_material_dark))
                colors = intArrayOf(R.color.dark_card)
            } else {
                fab?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cream))
                colors = intArrayOf(
                        R.color.vintage_card1,
                        R.color.vintage_card2,
                        R.color.vintage_card3)
            }
        } else {
            colors = intArrayOf(R.color.material_card1,
                    R.color.material_card2,
                    R.color.material_card3,
                    R.color.material_card4,
                    R.color.material_card5,
                    R.color.material_card6,
                    R.color.material_card7,
                    R.color.material_card8,
                    R.color.material_card9,
                    R.color.material_card10)
        }
    }

    companion object {
        private val TAG = "MAINACTIVITY"
        private val MATERIAL_THEME = "0"
        private val DARK_THEME = "1"
        private val VINTAGE_THEME = "2"
    }
}
